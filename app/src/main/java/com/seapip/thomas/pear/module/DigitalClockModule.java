package com.seapip.thomas.pear.module;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.Calendar;

public class DigitalClockModule implements Module {
    private Rect mBounds;
    private Calendar mCalendar;
    private boolean mTimeFormat24;
    private boolean mAmbient;

    /* Fonts */
    private Typeface mFontLight;

    /* Paint */
    private Paint mTextPaint;
    private Paint backgroundPaint;

    public DigitalClockModule(Calendar calendar, boolean timeFormat24) {
        mCalendar = calendar;
        mTimeFormat24 = timeFormat24;

        /* Fonts */
        mFontLight = Typeface.create("sans-serif-light", Typeface.NORMAL);

        /* Paint */
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        mTextPaint.setTypeface(mFontLight);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.argb(20, 0, 255, 0));

    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
        mTextPaint.setTextSize(bounds.height() * 0.80f);
    }

    @Override
    public void draw(Canvas canvas) {
        String hourString;
        if (mTimeFormat24) {
            hourString = String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY));
        } else {
            int hour = mCalendar.get(Calendar.HOUR);
            if (hour == 0) {
                hour = 12;
            }
            hourString = String.valueOf(hour);
        }
        String minuteString = String.valueOf(mCalendar.get(Calendar.MINUTE));
        if (minuteString.length() == 1) {
            minuteString = "0" + minuteString;
        }

        float x = mBounds.right - mBounds.height() * 0.05f;
        float y = mBounds.centerY() - (mTextPaint.descent() + mTextPaint.ascent()) / 2;

        mTextPaint.setAlpha(255);
        canvas.drawText(hourString + " " + minuteString, x, y, mTextPaint);
        int alpha = mAmbient ? 164 :
                (int) (Math.abs(mCalendar.get(Calendar.MILLISECOND) / 1000f - 0.5f) * 2 * 128 + 128);
        mTextPaint.setAlpha(alpha);
        canvas.drawText(":", x - mTextPaint.measureText(minuteString), y, mTextPaint);
    }

    @Override
    public void setColor(int color) {
        mTextPaint.setColor(color);
    }

    @Override
    public void setAmbient(boolean ambient) {
        mAmbient = ambient;
    }

    @Override
    public void setBurnInProtection(boolean burnInProtection) {
    }

    @Override
    public void setLowBitAmbient(boolean lowBitAmbient) {
    }
}
