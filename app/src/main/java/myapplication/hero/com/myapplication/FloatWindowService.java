package myapplication.hero.com.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by he.b.wang on 15/4/7.
 */
public class FloatWindowService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        View view = LayoutInflater.from(this).inflate(R.layout.float_window_layout, null);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = 300;
        lp.height = 100;
        lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mWindowManager.addView(view, lp);
        return super.onStartCommand(intent, flags, startId);
    }
}
