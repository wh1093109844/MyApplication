package workspace.hero.com.pedometerapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
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

    private int buttomMargin = 100;
    private int leftMargin = 50;
    private int topMargin = 50;
    private int rightMargin = 50;

    private float mOffset = 0;

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
        RectF rectF = getOffset(dateLine.get(dateLine.size() - 1));
        if ((width - leftMargin - rightMargin) > rectF.left) {
            mOffset = 0;
        }
        drawDottedDineY(canvas);
        drawCoordinate(canvas);
    }

    /**
     * ����������
     * @param canvas
     */
    private void drawDottedDineY(Canvas canvas) {
        float itemHeight = (float)(height - topMargin - buttomMargin) / 10;
        mPaint.setColor(Color.BLACK);
        mPaint.setPathEffect(null);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(leftMargin, topMargin, leftMargin, (float) (height - buttomMargin), mPaint);
        Path path = new Path();
        for (int i = 0; i <= 10; i++) {

            mPaint.setStrokeWidth(2);
            mPaint.setStyle(Paint.Style.STROKE);
            if (i != 0) {
                DashPathEffect effects = new DashPathEffect(new float[]{4, 4},1);
                mPaint.setPathEffect(effects);
                mPaint.setColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                mPaint.setPathEffect(null);
                mPaint.setColor(Color.BLACK);
            }


            float bottomHeight = (float)(height - buttomMargin) - itemHeight * i;
            Log.d(TAG, "height:" + bottomHeight);
            path.reset();
            path.moveTo(leftMargin, bottomHeight);
            path.lineTo((float) (width - rightMargin), bottomHeight);
            canvas.drawPath(path, mPaint);

            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setPathEffect(null);
            mPaint.setTextSize(20);
            mPaint.setColor(Color.BLACK);

            int num = i * 2;
            Rect rect = getTextBounds(String.valueOf(num), mPaint);
            Log.d(TAG, num + " : " + rect.width());
            canvas.drawText(String.valueOf(num), leftMargin - rect.width() - 20, bottomHeight + rect.height() / 2, mPaint);
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
        final int count = dateLine.size();
        Path path = new Path();
        RectF temp = new RectF();
        temp.left = 0;
        temp.top = 0;

        RectF temp1 = null;
        for (int i = 0; i < count; i++) {
            long key = dateLine.get(i);
            RectF currRect = getOffset(key);

            if (currRect.left < mOffset) {
                temp = currRect;
            } else if (temp != null) {
                temp = getBound(temp, currRect);
                path.moveTo(leftMargin - mOffset + temp.left, (float) (height - buttomMargin) - temp.top);
                temp = null;
            }
            if (currRect.left < width - rightMargin - leftMargin + mOffset) {
                temp1 = currRect;
                if (temp == null) {
                    path.lineTo(leftMargin + currRect.left - mOffset, (float) (height - buttomMargin) - currRect.top);
                    drawDottedDineX(canvas, currRect);
                    drawText(canvas, currRect, key);
                }
            } else {
                temp1 = getBound(temp1, currRect);
                path.lineTo(leftMargin + temp1.right - mOffset, (float) (height - buttomMargin) - temp1.bottom);
                break;
            }
        }
        mPaint.setColor(getResources().getColor(R.color.line_color));
        mPaint.setPathEffect(null);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, mPaint);
    }

    private RectF getOffset(Long key) {
        SmotionPedometer.Info info = infoMap.get(key);
        RectF rectF = new RectF();
        rectF.top = (float)info.getSpeed() * (height - topMargin - buttomMargin) / 20;
        rectF.left = (int)((key - startTime) * 50 ) / 1000;
        rectF.left = rectF.left < 0 ? 0 : rectF.left;
        Log.d(TAG, "left:" + rectF.left + "      top:" + rectF.top + "    ti" + "         startTime:" + getDate(key));
        return rectF;
    }

    private String getDate(Long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(calendar.getTime());
    }

    private void drawDottedDineX(Canvas canvas, RectF currRect) {

        DashPathEffect effects = new DashPathEffect(new float[]{4, 4},1);
        mPaint.setPathEffect(effects);
        mPaint.setColor(getResources().getColor(android.R.color.darker_gray));
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);

        Path path = new Path();

        float x = leftMargin + currRect.left - mOffset;
        float y1 = (float)(height - buttomMargin - 2);
        float y2 = y1 - currRect.top;
        path.moveTo(x, y1);
        path.lineTo(x, y2);
        canvas.drawPath(path, mPaint);
        Log.d(TAG, "draw X,   lineTo:" + x + ", " + y2 + ", " + x + ", " + y1);
    }

    private void drawText(Canvas canvas, RectF currRect, long key) {
        mPaint.setPathEffect(null);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(15);

        Path path = new Path();
        String str = getDate(key);
        Rect rect = getTextBounds(str, mPaint);
        float x = leftMargin + currRect.left - rect.height() / 2 - mOffset;
        float y = (float)(height - buttomMargin + 2);

        path.moveTo(x, y);
        path.lineTo(x, height);
        canvas.drawTextOnPath(str, path, 10, 0, mPaint);
    }

    private RectF getBound(RectF prevRect, RectF currRect) {
        RectF rectF = new RectF();
        float k = (currRect.top - prevRect.top) / (currRect.left - prevRect.left);
        float b = currRect.top - k * currRect.left;
        if (prevRect.left < mOffset) {
            rectF.left = mOffset;
            rectF.top = k * rectF.left + b;
        } else {
            rectF.left = prevRect.left;
            rectF.top = prevRect.top;
        }
        if (currRect.left >= width - leftMargin - rightMargin + mOffset) {
            rectF.right = width - leftMargin - rightMargin + mOffset;
            rectF.bottom = k * rectF.right + b;
        } else {
            rectF.right = currRect.left;
            rectF.bottom = currRect.top;
        }

        return rectF;
    }

    private float tempX = 0;
    private float temp = 0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tempX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                temp = event.getX() - tempX;
                tempX = event.getX();
                if ((temp <= 0 && isLast()) || (temp > 0 && mOffset > 0)) {
                    mOffset -= temp;
                    if (temp > 0 && mOffset < 0) {
                        mOffset = 0;
                    }
                    RectF rectF = getOffset(dateLine.get(dateLine.size() - 1));
                    if ((width - leftMargin - rightMargin + mOffset) > rectF.left) {
                        mOffset = rectF.left - width + leftMargin + rightMargin;
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private boolean isLast() {
        RectF rectF = getOffset(dateLine.get(dateLine.size() - 1));
        if ((width - leftMargin - rightMargin + mOffset) <= rectF.left) {
            return true;
        }
        return false;
    }

}
