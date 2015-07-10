package com.hero.auth;

import com.hero.auth.listener.IAuthLoginListener;
import com.hero.auth.qq.QQLoginUtilty;

import android.app.Activity;

public class AuthLogin {
	
	public static final int QQ 			= 3;
	public static final int WEBCHART 	= 6;
	public static final int SINA 		= 4;
	
	public static void init(Activity activity, IAuthLoginListener listener){
		QQLoginUtilty.init(activity, listener);
	}
	
	public static void login(Activity activity, int loginType){
		QQLoginUtilty.getInstance().login(activity);
	}

}
