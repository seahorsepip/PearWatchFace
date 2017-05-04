package com.seapip.thomas.pear.module;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.Calendar;

public class SportDigitalClockModule implements Module {
    private Rect mBounds;
    private Context mContext;
    private Calendar mCalendar;
    private boolean mTimeFormat24;
    private int mStyle;
    private boolean mAmbient;
    private boolean mBurnInProtection;

    /* Fonts */
    private Typeface mFontBold;

    /* Paint */
    private Paint mHourTextPaint;
    private Paint mMinuteTextPaint;

    public SportDigitalClockModule(Context context, Calendar calendar,
                                   boolean timeFormat24, int style) {
        mContext = context;
        mCalendar = calendar;
        mTimeFormat24 = timeFormat24;
        mStyle = style;

        /* Fonts */
        mFontBold = Typeface.createFromAsset(mContext.getAssets(), "fonts/Sport.ttf");

        /* Paint */
        mHourTextPaint = new Paint();
        mHourTextPaint.setAntiAlias(true);
        mHourTextPaint.setTextAlign(Paint.Align.RIGHT);
        mHourTextPaint.setTypeface(mFontBold);
        mHourTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mMinuteTextPaint = new Paint();
        mMinuteTextPaint.setAntiAlias(true);
        mMinuteTextPaint.setTextAlign(Paint.Align.RIGHT);
        mMinuteTextPaint.setTypeface(mFontBold);
        mMinuteTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        setStyle(mStyle);
    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
        mHourTextPaint.setTextSize(bounds.height() * 0.54f);
        mHourTextPaint.setStrokeWidth(bounds.height() * 0.015f);
        mMinuteTextPaint.setTextSize(bounds.height() * 0.54f);
        mMinuteTextPaint.setStrokeWidth(bounds.height() * 0.015f);
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

        canvas.drawText(hourString,
                mBounds.right - mBounds.width() * 0.1f,
                mBounds.top + mBounds.width() * 0.07f - (mHourTextPaint.descent() + mHourTextPaint.ascent()),
                mHourTextPaint);
        canvas.drawText(minuteString,
                mBounds.right - mBounds.width() * 0.1f,
                mBounds.bottom - mBounds.width() * 0.1f,
                mMinuteTextPaint);
    }

    public void setStyle(int style) {
        mStyle = style;
        changeStyle(mStyle);
    }

    private void changeStyle(int style) {
        switch (style) {
            case 0: //Both filled
                mHourTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                mMinuteTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                break;
            case 1: //Top filled and bottom stroke
                mHourTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                mMinuteTextPaint.setStyle(Paint.Style.STROKE);
                break;
            case 2: //Top and bottom stroke
                mHourTextPaint.setStyle(Paint.Style.STROKE);
                mMinuteTextPaint.setStyle(Paint.Style.STROKE);
                break;
        }
    }

    @Override
    public void setColor(int color) {
        mHourTextPaint.setColor(color);
        mMinuteTextPaint.setColor(color);
    }

    @Override
    public void setAmbient(boolean ambient) {
        mAmbient = ambient;
        if (mBurnInProtection) {
            if (mAmbient) {
                changeStyle(2);
            } else {
                changeStyle(mStyle);
            }
        }
    }

    @Override
    public void setBurnInProtection(boolean burnInProtection) {
        mBurnInProtection = burnInProtection;
    }

    @Override
    public void setLowBitAmbient(boolean lowBitAmbient) {
    }
}
