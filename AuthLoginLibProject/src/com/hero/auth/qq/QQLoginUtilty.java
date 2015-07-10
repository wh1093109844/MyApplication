package com.hero.auth.qq;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.hero.auth.AuthLogin;
import com.hero.auth.AuthUser;
import com.hero.auth.listener.IAuthLoginListener;
import com.hero.auth.listener.IAuthLogoutListener;
import com.tencent.connect.UserInfo;
import com.tencent.open.utils.Util;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class QQLoginUtilty {
	
	private static final String TAG = "QQLoginUtilty";
	
	private static final String APP_KEY = "222222";
	
	private static QQLoginUtilty loginUtility;
	private Tencent mTencent;
	private IAuthLoginListener logintListener;
	private IUiListener qqLogintListener;
	private UserInfo mInfo;
	
	private QQLoginUtilty(){}
	
	private QQLoginUtilty(Context context){
		if (mTencent == null) {
	        mTencent = Tencent.createInstance(APP_KEY, context);
	    }
	}
	
	public static void init(Activity activity, IAuthLoginListener listener){
		getInstance().initSDK(activity, listener);
	}
	
	private void initSDK(final Activity activity, IAuthLoginListener listener){
		mTencent = Tencent.createInstance(APP_KEY, activity);
		this.logintListener = listener;
		qqLogintListener = new IUiListener() {
			
			@Override
			public void onError(UiError arg0) {
				if(logintListener != null){
					logintListener.onError(arg0.errorDetail);
				}
			}
			
			@Override
			public void onComplete(Object arg0) {
				JSONObject jsonObj = (JSONObject)arg0;
				initOpenidAndToken(jsonObj);
				updateUserInfo(jsonObj, activity);
			}
			
			@Override
			public void onCancel() {
				if(logintListener != null){
					logintListener.onCancel();
				}
			}
		};
	}
	
	public static QQLoginUtilty getInstance(){
		if(loginUtility == null){
			loginUtility = new QQLoginUtilty();
		}
		return loginUtility;
	}
	
	public void login(Activity activity){
		if(!mTencent.isSessionValid()){
			mTencent.login(activity, "all", qqLogintListener);
		}
	}
	
	public void logout(Context context, IAuthLogoutListener logoutListener){
		mTencent.logout(context);
		if(logoutListener != null){
			logoutListener.onLogout();
		}
	}
	
	public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
        }
    }
	
	private void updateUserInfo(final JSONObject jsonObj, final Activity activity) {
		if (mTencent != null && mTencent.isSessionValid()) {
			IUiListener listener = new IUiListener() {

				@Override
				public void onError(UiError e) {

				}

				@Override
				public void onComplete(final Object response) {
					Message msg = new Message();
					
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								AuthUser authUser = new AuthUser();
								authUser.openid = jsonObj.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
								authUser.accessToken = jsonObj.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
								JSONObject obj = (JSONObject) response;
								
								if(obj.has("nickname")){
									authUser.nickName = obj.getString("nickname");
								}
								if(obj.has("figureurl_qq_2")){
									authUser.figureurl = obj.getString("figureurl_qq_2");
								}
								authUser.loginType = AuthLogin.QQ;
								if(logintListener != null){
									logintListener.onComplete(authUser);
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}

				@Override
				public void onCancel() {

				}
			};
			mInfo = new UserInfo(activity, mTencent.getQQToken());
			mInfo.getUserInfo(listener);

		} else {
//			mUserInfo.setText("");
//			mUserInfo.setVisibility(android.view.View.GONE);
//			mUserLogo.setVisibility(android.view.View.GONE);
		}
	}

	public static Bitmap getbitmap(String imageUri) {
		Log.v(TAG, "getbitmap:" + imageUri);
		// 显示网络上的图片
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();

			Log.v(TAG, "image download finished." + imageUri);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
		} catch (IOException e) {
			e.printStackTrace();
			Log.v(TAG, "getbitmap bmp fail---");
            bitmap = null;
		}
		return bitmap;
	}
}
