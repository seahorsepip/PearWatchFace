package com.seapip.thomas.pear.module;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.Calendar;
import java.util.Locale;

public class MotionDateModule implements Module {
    private Rect mBounds;
    private Calendar mCalendar;
    private int mDate;
    private boolean mAmbient;

    /* Fonts */
    private Typeface mFontLight;

    /* Paint */
    private Paint mTextPaint;

    public MotionDateModule(Calendar calendar, int date) {
        mCalendar = calendar;
        mDate = date;

        /* Fonts */
        mFontLight = Typeface.create("sans-serif-light", Typeface.NORMAL);

        /* Paint */
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.RIGHT);
        mTextPaint.setTypeface(mFontLight);

    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
        mTextPaint.setTextSize(bounds.height() * 0.80f);
    }

    @Override
    public void draw(Canvas canvas) {
        String dayOfWeek = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        String dayOfMonth = String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH));
        String month = mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        String date = "";
        switch (mDate) {
            case 1:
                date = dayOfWeek.toUpperCase() + " " + dayOfMonth;
                break;
            case 2:
                date = month.toUpperCase()+ " " + dayOfMonth;
                break;
            case 3:
                date = dayOfMonth;
                break;
        }

        canvas.drawText(date,
                mBounds.right - mBounds.height() * 0.05f,
                mBounds.centerY() - (mTextPaint.descent() + mTextPaint.ascent()) / 2,
                mTextPaint);
    }

    @Override
    public void setColor(int color) {
        mTextPaint.setColor(color);
        mTextPaint.setAlpha(192);
    }

    public void setDate(int date) {
        mDate = date;
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
