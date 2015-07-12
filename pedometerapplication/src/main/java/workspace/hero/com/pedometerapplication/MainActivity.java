package workspace.hero.com.pedometerapplication;

import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;
import com.samsung.android.sdk.pass.SpassInvalidStateException;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsMenu;


@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private Spass mSpass;
    private boolean isFeatureEnabled;
    private SpassFingerprint mSpassFingerprint;
    private boolean onReadyIdentify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpass = new Spass();

        try {
            mSpass.initialize(this);
        } catch (SsdkUnsupportedException e) {
            Log.e(TAG, "Exception: " + e);
        } catch (UnsupportedOperationException e){
            Log.d(TAG, "Fingerprint Service is not supported in the device");
        }
        isFeatureEnabled = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

        if(isFeatureEnabled){
            mSpassFingerprint = new SpassFingerprint(this);
            Log.d(TAG, "Fingerprint Service is supported in the device.");
            Log.d(TAG, "SDK version : " + mSpass.getVersionName());
        } else {
            Log.d(TAG, "Fingerprint Service is not supported in the device.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSpassFingerprint == null) {
            return;
        }
        try {
            if (!mSpassFingerprint.hasRegisteredFinger()) {
                mSpassFingerprint.registerFinger(this, registerListener);
            } else {
                if (onReadyIdentify == false) {
                    try {
                        onReadyIdentify = true;
                        mSpassFingerprint.startIdentifyWithDialog(this, listener, true);
                        Log.d(TAG, "Please identify finger to verify you");
                    } catch (SpassInvalidStateException ise) {
                        onReadyIdentify = false;
                        if (ise.getType() == SpassInvalidStateException.STATUS_OPERATION_DENIED) {
                            Log.e(TAG, "Exception: " + ise.getMessage());
                        }
                    } catch (IllegalStateException e) {
                        onReadyIdentify = false;
                        Log.e(TAG, "Exception: " + e);
                    }
                }
            }
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, "Fingerprint Service is not supported in the device");
        }
    }

    private SpassFingerprint.RegisterListener registerListener = new SpassFingerprint.RegisterListener() {
        @Override
        public void onFinished() {
            Log.d(TAG, "register finish!");
        }
    };

    private SpassFingerprint.IdentifyListener listener = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            Log.d(TAG, "identify finished : reason=" + getEventStatusName(eventStatus));
            onReadyIdentify = false;
            int FingerprintIndex = 0;
            try {
                FingerprintIndex = mSpassFingerprint.getIdentifiedFingerprintIndex();
            } catch (IllegalStateException ise) {
                Log.e(TAG, ise.getMessage());
            }
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                Log.d(TAG, "onFinished() : Identify authentification Success with FingerprintIndex : " + FingerprintIndex);
            } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                Log.d(TAG, "onFinished() : Password authentification Success");
            } else {
                Log.d(TAG, "onFinished() : Authentification Fail for identify");
                MainActivity.this.finish();
            }
        }

        @Override
        public void onReady() {
            Log.d(TAG, "identify state is ready");
        }

        @Override
        public void onStarted() {
            Log.d(TAG, "User touched fingerprint sensor!");
        }
    };

    private static String getEventStatusName(int eventStatus) {
        switch (eventStatus) {
            case SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS:
                return "STATUS_AUTHENTIFICATION_SUCCESS";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS:
                return "STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS";
            case SpassFingerprint.STATUS_TIMEOUT_FAILED:
                return "STATUS_TIMEOUT";
            case SpassFingerprint.STATUS_SENSOR_FAILED:
                return "STATUS_SENSOR_ERROR";
            case SpassFingerprint.STATUS_USER_CANCELLED:
                return "STATUS_USER_CANCELLED";
            case SpassFingerprint.STATUS_QUALITY_FAILED:
                return "STATUS_QUALITY_FAILED";
            case SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE:
                return "STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_FAILED:
            default:
                return "STATUS_AUTHENTIFICATION_FAILED";
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        onReadyIdentify = false;
    }
}
