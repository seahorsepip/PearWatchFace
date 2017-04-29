package com.seapip.thomas.pear.module;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class UtilityTicksModule implements Module {
    private int mStyle;
    private Rect mBounds;
    private boolean mAmbient;
    private boolean mBurnInProtection;

    /* Paint */
    private Paint mSecondsTickPaint;
    private Paint mMinuteTickPaint;
    private Paint mHourTextPaint;
    private Paint mSecondsTextPaint;

    public UtilityTicksModule(int style) {
        mStyle = style;
        mSecondsTickPaint = new Paint();
        mSecondsTickPaint.setAntiAlias(true);
        mSecondsTickPaint.setColor(Color.parseColor("#707070"));
        mMinuteTickPaint = new Paint();
        mMinuteTickPaint.setAntiAlias(true);
        mMinuteTickPaint.setColor(Color.WHITE);
        mHourTextPaint = new Paint();
        mHourTextPaint.setAntiAlias(true);
        mHourTextPaint.setColor(Color.WHITE);
        mHourTextPaint.setTextAlign(Paint.Align.CENTER);
        mSecondsTextPaint = new Paint();
        mSecondsTextPaint.setAntiAlias(true);
        mSecondsTextPaint.setColor(Color.parseColor("#eeeeee"));
        mSecondsTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
        mSecondsTickPaint.setStrokeWidth(bounds.width() * 0.005f);
        int stroke = (int) (bounds.width() * 0.02f);
        if (stroke % 2 != 0) {
            stroke--;
        }
        mMinuteTickPaint.setStrokeWidth(stroke);
        mHourTextPaint.setTextSize(bounds.width() * 0.12f);
        mSecondsTextPaint.setTextSize(bounds.width() * 0.04f);
    }

    @Override
    public void draw(Canvas canvas) {
        float outerRadius = mBounds.width() / 2;
        float innerRadius = outerRadius - mBounds.width() * 0.038f;
        float secondsTextRadius = outerRadius - mBounds.width() * 0.019f;
        float hourTextRadius = outerRadius - mBounds.width() * 0.13f;
        for (int i = 0; i < 60; i++) {
            float rot = (float) (i * Math.PI * 2 / 60);
            float x = (float) Math.sin(rot);
            float y = (float) -Math.cos(rot);
            if (i % 5 != 0) {
                canvas.drawLine(mBounds.centerX() + x * innerRadius,
                        mBounds.centerY() + y * innerRadius,
                        mBounds.centerX() + x * outerRadius,
                        mBounds.centerY() + y * outerRadius,
                        mSecondsTickPaint);
            } else if (mStyle < 3) {
                canvas.drawLine(mBounds.centerX() + x * innerRadius,
                        mBounds.centerY() + y * innerRadius,
                        mBounds.centerX() + x * outerRadius,
                        mBounds.centerY() + y * outerRadius,
                        mMinuteTickPaint);
            } else {
                String seconds = String.valueOf(i == 0 ? 12 : i);
                seconds = seconds.length() < 2 ? "0" + seconds : seconds;
                canvas.drawText(seconds,
                        mBounds.centerX() + x * secondsTextRadius,
                        mBounds.centerY() + y * secondsTextRadius - (mSecondsTextPaint.descent() + mSecondsTextPaint.ascent()) / 2,
                        mSecondsTextPaint);
            }
            if ((i % 15 == 0 && mStyle > 0) || (i % 5 == 0 && mStyle > 1)) {
                canvas.drawText(String.valueOf(i == 0 ? 12 : i / 5),
                        mBounds.centerX() + x * hourTextRadius,
                        mBounds.centerY() + y * hourTextRadius - (mHourTextPaint.descent() + mHourTextPaint.ascent()) / 2,
                        mHourTextPaint);
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
