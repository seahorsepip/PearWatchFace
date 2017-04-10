package com.seapip.thomas.pear;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.Calendar;

public class ClockModule implements Module {
    private Rect mBounds;
    private Calendar mCalendar;
    private boolean mTimeFormat24;

    /* Fonts */
    private Typeface mFontLight;

    /* Paint */
    private Paint mTextPaint;
    private Paint backgroundPaint;

    public ClockModule(Calendar calendar, boolean timeFormat24) {
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
        backgroundPaint.setColor(Color.argb(128, 0, 255, 0));

    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
        mTextPaint.setTextSize(bounds.height() * 0.85f);
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

        canvas.drawRect(mBounds, backgroundPaint);
        canvas.drawText(hourString + ":" + minuteString, mBounds.right,
                mBounds.centerY() - (mTextPaint.descent() + mTextPaint.ascent()) / 2, mTextPaint);
    }

    @Override
    public void setColor(int color) {
        mTextPaint.setColor(color);
    }
}
