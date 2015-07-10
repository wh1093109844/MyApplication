package workspace.hero.com.pedometerapplication;

import android.os.Looper;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.motion.Smotion;
import com.samsung.android.sdk.motion.SmotionPedometer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;


/**
 * A placeholder fragment containing a simple view.
 */
@EFragment(R.layout.fragment_main)
public class MainActivityFragment extends Fragment {

    @ViewById(R.id.txt)
    TextView mTextView;

    @ViewById(R.id.charView)
    LineChatView_ charView;

    private Smotion mSmotion;
    private SmotionPedometer mSmotionPedometer;
    private SmotionPedometer.ChangeListener changeListener;



    public MainActivityFragment() {
    }

    @AfterViews
    public void init() {
        changeListener = new SmotionPedometer.ChangeListener() {
            @Override
            public void onChanged(SmotionPedometer.Info info) {
                long currTime = info.getTimeStamp();
                double speed = info.getSpeed();
                double count = info.getCount(SmotionPedometer.Info.COUNT_WALK_FLAT);
                double distance = info.getDistance();
                String str = getResources().getString(R.string.text);
                str = String.format(str, currTime, speed, count, distance);
                mTextView.setText(str);
                charView.updateInfo(info);
            }
        };

        mSmotion = new Smotion();
        try {
            mSmotion.initialize(getActivity());
            mSmotionPedometer = new SmotionPedometer(Looper.getMainLooper(), mSmotion);
            mSmotionPedometer.start(changeListener);
        } catch (SsdkUnsupportedException e) {
            e.printStackTrace();
        }
    }

}
