package com.seapip.thomas.pear.module;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class SimpleTicksModule implements Module {
    private int mStyle;
    private Rect mBounds;
    private boolean mAmbient;
    private boolean mBurnInProtection;

    /* Paint */
    private Paint mSecondsTickPaint;
    private Paint mMinuteTickPaint;
    private Paint mHourTickPaint;
    private Paint mHollowHourTickPaint;
    private Paint mTickTextPaint;

    public SimpleTicksModule(int style) {
        mStyle = style;
        mSecondsTickPaint = new Paint();
        mSecondsTickPaint.setAntiAlias(true);
        mSecondsTickPaint.setColor(Color.parseColor("#707070"));
        mMinuteTickPaint = new Paint();
        mMinuteTickPaint.setAntiAlias(true);
        mMinuteTickPaint.setColor(Color.WHITE);
        mHourTickPaint = new Paint();
        mHourTickPaint.setAntiAlias(true);
        mHourTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mHourTickPaint.setColor(Color.parseColor("#b2b2b2"));
        mHollowHourTickPaint = new Paint();
        mHollowHourTickPaint.setAntiAlias(true);
        mHollowHourTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mHollowHourTickPaint.setColor(Color.BLACK);
        mTickTextPaint = new Paint();
        mTickTextPaint.setAntiAlias(true);
        mTickTextPaint.setColor(Color.WHITE);
        mTickTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
        mSecondsTickPaint.setStrokeWidth(bounds.width() * 0.005f);
        mMinuteTickPaint.setStrokeWidth(bounds.width() * 0.007f);
        int stroke = (int) (bounds.width() * 0.026f);
        if (stroke % 2 != 0) {
            stroke--;
        }
        mHourTickPaint.setStrokeWidth(stroke);
        mHollowHourTickPaint.setStrokeWidth(stroke - 6);
        mTickTextPaint.setTextSize(bounds.width() * 0.05f);
    }

    @Override
    public void draw(Canvas canvas) {
        float outerRadius = mBounds.width() / 2;
        float innerRadius = outerRadius - mBounds.width() * 0.038f;
        float hourOuterRadius = mBounds.width() / 2 - mBounds.width() * 0.055f - mHourTickPaint.getStrokeWidth();
        float hourInnerRadius = hourOuterRadius - mBounds.width() * 0.12f + mHourTickPaint.getStrokeWidth();
        float textRadius = mBounds.width() / 2 + mBounds.width() * 0.05f;
        for (int i = 0; i < 240; i++) {
            float rot = (float) (i * Math.PI * 2 / 240);
            float x = (float) Math.sin(rot);
            float y = (float) -Math.cos(rot);
            if ((mStyle > 0 && i % 4 == 0)
                    || mStyle > 2 && i % 2 == 0 && i % 20 != 0
                    || mStyle > 3 && i % 20 != 0) {
                canvas.drawLine(mBounds.centerX() + x * innerRadius,
                        mBounds.centerY() + y * innerRadius,
                        mBounds.centerX() + x * outerRadius,
                        mBounds.centerY() + y * outerRadius,
                        mSecondsTickPaint);
            }
            if (mStyle > 1 && i % 20 == 0) {
                canvas.drawLine(mBounds.centerX() + x * innerRadius,
                        mBounds.centerY() + y * innerRadius,
                        mBounds.centerX() + x * outerRadius,
                        mBounds.centerY() + y * outerRadius,
                        mMinuteTickPaint);
            }
            if (mStyle > 2 && i % 20 == 0) {
                canvas.drawLine(mBounds.centerX() + x * hourInnerRadius,
                        mBounds.centerY() + y * hourInnerRadius,
                        mBounds.centerX() + x * hourOuterRadius,
                        mBounds.centerY() + y * hourOuterRadius,
                        mHourTickPaint);
                if(mAmbient && mBurnInProtection && mHollowHourTickPaint.getStrokeWidth() > 0) {
                    canvas.drawLine(mBounds.centerX() + x * hourInnerRadius,
                            mBounds.centerY() + y * hourInnerRadius,
                            mBounds.centerX() + x * hourOuterRadius,
                            mBounds.centerY() + y * hourOuterRadius,
                            mHollowHourTickPaint);
                }
            }
            if (mStyle > 3 && i % 20 == 0) {
                canvas.drawText(String.valueOf(i / 4),
                        mBounds.centerX() + x * textRadius,
                        mBounds.centerY() + y * textRadius - (mTickTextPaint.descent() + mTickTextPaint.ascent()) / 2,
                        mTickTextPaint);
            }
        }
    }

    public void setStyle(int style) {
        mStyle = style;
    }

    @Override
    public void setColor(int color) {
    }

    @Override
    public void setAmbient(boolean ambient) {
        mAmbient = ambient;
    }

    @Override
    public void setBurnInProtection(boolean burnInProtection) {
        mBurnInProtection = burnInProtection;
    }

    @Override
    public void setLowBitAmbient(boolean lowBitAmbient) {

    }
}
