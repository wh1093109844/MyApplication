package workspace.hero.com.pedometerapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.samsung.android.sdk.motion.SmotionPedometer;

import org.androidannotations.annotations.EView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by he.b.wang on 15/6/19.
 */
@EView
public class LineChatView extends View {

    private static final String TAG = "LineChatView";

    private int height, width;
    private long startTime = -1;
    private Paint mPaint;

    private List<Long> dateLine = new ArrayList<>();
    private Map<Long, SmotionPedometer.Info> infoMap = new HashMap<Long, SmotionPedometer.Info>();

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
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }
        height = getHeight();
        width = getWidth();
        drawDottedDine(canvas);
        drawCoordinate(canvas);
    }

    /**
     * »­ÐéÏß×ø±ê
     * @param canvas
     */
    private void drawDottedDine(Canvas canvas) {
        float itemHeight = (float)(height - 100) / 10;
        mPaint.setColor(getResources().getColor(android.R.color.darker_gray));
        canvas.drawLine(50f, 50f, 50f, (float) (height - 50), mPaint);

        for (int i = 0; i <= 10; i++) {
            if (i != 0) {
                DashPathEffect effects = new DashPathEffect(new float[]{4, 4},1);
                mPaint.setPathEffect(effects);
                mPaint.setColor(getResources().getColor(android.R.color.darker_gray));
            }
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
            mPaint.setTextSize(20);
            mPaint.setColor(Color.BLACK);

            int num = i * 2;
            Rect rect = getTextBounds(String.valueOf(num), mPaint);
            Log.d(TAG, num + " : " + rect.width());
            canvas.drawText(String.valueOf(num), 50 - rect.width() - 20, bottomHeight + rect.height() / 2, mPaint);
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

    public void updateInfo(SmotionPedometer.Info info) {
        long key = System.currentTimeMillis();
        dateLine.add(key);
        infoMap.put(key, info);
        invalidate();
    }

    private void drawCoordinate(Canvas canvas) {
        mPaint.setColor(getResources().getColor(R.color.line_color));
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        final int count = dateLine.size();

        for (int i = 0; i < count; i++) {
            Path path = new Path();
            RectF startRect;
            if (i != 0) {
                startRect = getOffset(dateLine.get(i - 1));
            } else {
                startRect = new RectF();
            }
            path.moveTo(50 + startRect.left, (float) (height - 50) - startRect.top);

            RectF currRect = getOffset(dateLine.get(i));
            path.lineTo(50 + currRect.left, (float)(height - 50) - currRect.top);

            canvas.drawPath(path, mPaint);
        }
    }

    private RectF getOffset(Long key) {
        SmotionPedometer.Info info = infoMap.get(key);
        RectF rectF = new RectF();
        rectF.top = (float)info.getSpeed() * (height - 100) / 20;
        rectF.left = ((key - startTime) * 50 ) / 1000;
        Log.d(TAG, "left:" + rectF.left + "      top:" + rectF.top + "    ti" + "         startTime:" + getDate(key));
        return rectF;
    }

    private String getDate(Long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(calendar.getTime());
    }
}
