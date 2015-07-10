package workspace.hero.com.pedometerapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.androidannotations.annotations.EView;

/**
 * Created by he.b.wang on 15/6/19.
 */
@EView
public class LineChatView extends View {

    private static final String TAG = "LineChatView";

    private int height, width;
    private long startTime = -1;
    private Paint mPaint;

    public LineChatView(Context context) {
        super(context);
        initParam();
    }

    public LineChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParam();
    }

    private void initParam() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setColor(getResources().getColor(android.R.color.darker_gray));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }
        height = getHeight();
        width = getWidth();
        coordinates(canvas);
    }

    private void coordinates(Canvas canvas) {
        float itemHeight = (float)(height - 100) / 10;
        canvas.drawLine(50f, 50f, 50f, (float) (height - 50), mPaint);
        canvas.drawLine(50f, (float) (height - 50), (float) (width - 50), (float) (height - 50), mPaint);

        for (int i = 1; i <= 10; i++) {
            DashPathEffect effects = new DashPathEffect(new float[]{4, 4},1);
            mPaint.setPathEffect(effects);
            mPaint.setStrokeWidth(2);
            mPaint.setStyle(Paint.Style.STROKE);
            Path path = new Path();
            float bottomHeight = (float)(height - 50) - itemHeight * i;
            Log.d(TAG, "height:" + bottomHeight);
            path.moveTo(50f, bottomHeight);
            path.lineTo((float) (width - 50), bottomHeight);
            canvas.drawPath(path, mPaint);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setPathEffect(null);
            Rect rect = getTextBounds(String.valueOf(i), mPaint);
        }
        mPaint.setPathEffect(null);
    }

    private Rect getTextBounds(String text, Paint paint) {
        Rect rect = new Rect();
        if (paint != null && text != null) {
            paint.getTextBounds(text, 0, text.length(), rect);
        }
        return rect;
    }
}
