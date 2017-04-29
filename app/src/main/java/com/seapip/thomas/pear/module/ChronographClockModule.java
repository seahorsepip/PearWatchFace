package com.seapip.thomas.pear.module;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Calendar;

public class ChronographClockModule implements Module {
    private Rect mBounds;
    private Calendar mCalendar;
    private boolean mAmbient;
    private boolean mBurnInProtection;
    private long mValue;
    private long mLapValue;
    private int mScale;

    /* Paint */
    private Paint mTickPaint;
    private Paint mValueTickPaint;
    private Paint mHandPaint;
    private Paint mHollowHandPaint;
    private Paint mConnectHandCenterPaint;
    private Paint mHandCenterPaint;
    private Paint mValueCenterPaint;
    private Paint mLapValueCenterPaint;
    private Paint mMinuteValueCenterPaint;
    private Paint mLapMinuteValueCenterPaint;
    private Paint mValueHandPaint;
    private Paint mLapValueHandPaint;
    private Paint mCenterPaint;
    private Paint mSecondsHandPaint;
    private Paint mSecondsCenterPaint;
    private Paint mTextPaint;

    public ChronographClockModule(Calendar calendar, int scale) {
        mCalendar = calendar;
        mValue = 0;
        mScale = scale;

        /* Paint */
        mTickPaint = new Paint();
        mTickPaint.setAntiAlias(true);
        mTickPaint.setColor(Color.parseColor("#707070"));
        mValueTickPaint = new Paint();
        mValueTickPaint.setAntiAlias(true);
        mValueTickPaint.setColor(Color.WHITE);
        mHandPaint = new Paint();
        mHandPaint.setAntiAlias(true);
        mHandPaint.setColor(Color.WHITE);
        mHandPaint.setStrokeCap(Paint.Cap.ROUND);
        mHollowHandPaint = new Paint();
        mHollowHandPaint.setAntiAlias(true);
        mHollowHandPaint.setColor(Color.BLACK);
        mHollowHandPaint.setStrokeCap(Paint.Cap.ROUND);
        mConnectHandCenterPaint = new Paint();
        mConnectHandCenterPaint.setAntiAlias(true);
        mConnectHandCenterPaint.setColor(Color.WHITE);
        mConnectHandCenterPaint.setStrokeCap(Paint.Cap.ROUND);
        mHandCenterPaint = new Paint();
        mHandCenterPaint.setAntiAlias(true);
        mHandCenterPaint.setColor(Color.WHITE);
        mHandCenterPaint.setStrokeCap(Paint.Cap.ROUND);
        mValueCenterPaint = new Paint();
        mValueCenterPaint.setAntiAlias(true);
        mValueCenterPaint.setStrokeCap(Paint.Cap.ROUND);
        mLapValueCenterPaint = new Paint();
        mLapValueCenterPaint.setAntiAlias(true);
        mLapValueCenterPaint.setStrokeCap(Paint.Cap.ROUND);
        mMinuteValueCenterPaint = new Paint();
        mMinuteValueCenterPaint.setAntiAlias(true);
        mMinuteValueCenterPaint.setStrokeCap(Paint.Cap.ROUND);
        mLapMinuteValueCenterPaint = new Paint();
        mLapMinuteValueCenterPaint.setAntiAlias(true);
        mLapMinuteValueCenterPaint.setStrokeCap(Paint.Cap.ROUND);
        mValueHandPaint = new Paint();
        mValueHandPaint.setAntiAlias(true);
        mLapValueHandPaint = new Paint();
        mLapValueHandPaint.setAntiAlias(true);
        mCenterPaint = new Paint();
        mCenterPaint.setAntiAlias(true);
        mCenterPaint.setColor(Color.BLACK);
        mCenterPaint.setStrokeCap(Paint.Cap.ROUND);
        mSecondsHandPaint = new Paint();
        mSecondsHandPaint.setAntiAlias(true);
        mSecondsHandPaint.setColor(Color.WHITE);
        mSecondsCenterPaint = new Paint();
        mSecondsCenterPaint.setAntiAlias(true);
        mSecondsCenterPaint.setColor(Color.WHITE);
        mSecondsCenterPaint.setStrokeCap(Paint.Cap.ROUND);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
        mTickPaint.setStrokeWidth(bounds.width() * 0.005f);
        mValueTickPaint.setStrokeWidth(bounds.width() * 0.007f);
        int stroke = (int) (bounds.width() * 0.042f);
        if (stroke % 2 != 0) {
            stroke--;
        }
        mHandPaint.setStrokeWidth(stroke);
        mHollowHandPaint.setStrokeWidth(stroke - 6);
        stroke = (int) (bounds.width() * 0.016f);
        if (stroke % 2 != 0) {
            stroke--;
        }
        mConnectHandCenterPaint.setStrokeWidth(stroke);
        stroke = (int) (bounds.width() * 0.048f);
        if (stroke % 2 != 0) {
            stroke--;
        }
        mHandCenterPaint.setStrokeWidth(stroke);
        stroke = (int) (bounds.width() * 0.036f);
        if (stroke % 2 != 0) {
            stroke--;
        }
        mValueCenterPaint.setStrokeWidth(stroke);
        mLapValueCenterPaint.setStrokeWidth(stroke);
        mValueHandPaint.setStrokeWidth(bounds.width() * 0.007f);
        mLapValueHandPaint.setStrokeWidth(bounds.width() * 0.007f);
        stroke = (int) (bounds.width() * 0.012f);
        if (stroke % 2 != 0) {
            stroke--;
        }
        mCenterPaint.setStrokeWidth(stroke);
        mSecondsHandPaint.setStrokeWidth(bounds.width() * 0.007f);
        stroke = (int) (bounds.width() * 0.022f);
        if (stroke % 2 != 0) {
            stroke--;
        }
        mMinuteValueCenterPaint.setStrokeWidth(stroke);
        mLapMinuteValueCenterPaint.setStrokeWidth(stroke);
        mSecondsCenterPaint.setStrokeWidth(stroke);
        mTextPaint.setTextSize(bounds.width() * 0.04f);
    }

    @Override
    public void draw(Canvas canvas) {
        float hours = mCalendar.get(Calendar.HOUR);
        float minutes = mCalendar.get(Calendar.MINUTE);
        float milliSeconds = mAmbient ?
                0 : mCalendar.get(Calendar.SECOND) * 1000 + mCalendar.get(Calendar.MILLISECOND);

        /* Value minutes dial */
        float dialY = mBounds.height() * 0.19f;
        float outerRadius = mBounds.height() * 0.14f;
        float textRadius = outerRadius - mBounds.width() * 0.065f;
        for (int i = 0; i < 60; i++) {
            float rot = (float) (i * Math.PI * 2 / 60);
            float x = (float) Math.sin(rot);
            float y = (float) -Math.cos(rot);
            boolean valueTick = (mScale == 3 && i % 10 == 0)
                    || ((mScale == 6 || mScale == 30) && i % 4 == 0)
                    || (mScale == 60 && i % 10 == 0);
            float innerRadius = outerRadius - mBounds.height() * (i % 2 == 0 ? 0.024f : 0.016f);
            canvas.drawLine(mBounds.centerX() + x * innerRadius,
                    mBounds.centerY() - dialY + y * innerRadius,
                    mBounds.centerX() + x * outerRadius,
                    mBounds.centerY() - dialY + y * outerRadius,
                    valueTick ? mValueTickPaint : mTickPaint);
            int scale = mScale == 3 ? 180 : mScale;
            if ((mScale == 3 && i % 10 == 0)
                    || (mScale == 6 && i % 20 == 0)
                    || (mScale == 30 && i % 12 == 0)
                    || (mScale == 60 && i % 10 == 0)) {
                canvas.drawText(String.valueOf(i == 0 ? scale / 2 : scale * i / 60 / 2),
                        mBounds.centerX() + x * textRadius,
                        mBounds.centerY() - dialY + y * textRadius
                                - (mTextPaint.descent() + mTextPaint.ascent()) / 2,
                        mTextPaint);
            }
        }

        /* Value minutes dial */
        float rot = (float) (mValue * Math.PI * 2 / (mScale * 30000));
        float outerX = (float) Math.sin(rot) * outerRadius;
        float outerY = (float) -Math.cos(rot) * outerRadius;
        canvas.drawLine(mBounds.centerX(),
                mBounds.centerY() - dialY,
                mBounds.centerX() + outerX,
                mBounds.centerY() - dialY + outerY,
                mValueHandPaint);
        if(mLapValue > -1) {
            rot = (float) (mLapValue * Math.PI * 2 / (mScale * 30000));
            outerX = (float) Math.sin(rot) * outerRadius;
            outerY = (float) -Math.cos(rot) * outerRadius;
            canvas.drawLine(mBounds.centerX(),
                    mBounds.centerY() - dialY,
                    mBounds.centerX() + outerX,
                    mBounds.centerY() - dialY + outerY,
                    mLapValueHandPaint);
        }

        /* Value minutes dial center */
        canvas.drawLine(mBounds.centerX(), mBounds.centerY() - dialY,
                mBounds.centerX() + 0.0001f, mBounds.centerY() - dialY,
                mLapValue < 0 ? mMinuteValueCenterPaint : mLapMinuteValueCenterPaint);

        /* Seconds dial */
        for (int i = 0; i < 60; i++) {
            rot = (float) (i * Math.PI * 2 / 60);
            float x = (float) Math.sin(rot);
            float y = (float) -Math.cos(rot);
            float innerRadius = outerRadius - mBounds.height() * (i % 5 == 0 ? 0.024f : 0.016f);
            canvas.drawLine(mBounds.centerX() + x * innerRadius,
                    mBounds.centerY() + dialY + y * innerRadius,
                    mBounds.centerX() + x * outerRadius,
                    mBounds.centerY() + dialY + y * outerRadius,
                    i % 5 == 0 ? mValueTickPaint : mTickPaint);
            if (i % 15 == 0) {
                canvas.drawText(String.valueOf(i == 0 ? 60 : i),
                        mBounds.centerX() + x * textRadius,
                        mBounds.centerY() + dialY + y * textRadius
                                - (mTextPaint.descent() + mTextPaint.ascent()) / 2,
                        mTextPaint);
            }
        }

        /* Seconds hand */
        if (!mAmbient) {
            rot = (float) (milliSeconds * Math.PI * 2 / 60000);
            outerX = (float) Math.sin(rot) * outerRadius;
            outerY = (float) -Math.cos(rot) * outerRadius;
            canvas.drawLine(mBounds.centerX(),
                    mBounds.centerY() + dialY,
                    mBounds.centerX() + outerX,
                    mBounds.centerY() + dialY + outerY,
                    mSecondsHandPaint);

            /* Seconds center */
            canvas.drawLine(mBounds.centerX(), mBounds.centerY() + dialY,
                    mBounds.centerX() + 0.0001f, mBounds.centerY() + dialY,
                    mSecondsCenterPaint);
        }


        /* Hour hand */
        outerRadius = mBounds.width() * 0.28f - mHandPaint.getStrokeWidth();
        float innerRadius = mBounds.width() * 0.04f + mHandPaint.getStrokeWidth();
        rot = (float) ((hours * 5 + minutes / 12) * Math.PI * 2 / 60);
        float innerX = (float) Math.sin(rot) * innerRadius;
        float innerY = (float) -Math.cos(rot) * innerRadius;
        outerX = (float) Math.sin(rot) * outerRadius;
        outerY = (float) -Math.cos(rot) * outerRadius;
        canvas.drawLine(mBounds.centerX(), mBounds.centerY(),
                mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                mConnectHandCenterPaint);
        canvas.drawLine(mBounds.centerX() + innerX, mBounds.centerY() + innerY,
                mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                mHandPaint);
        if (mHollowHandPaint.getStrokeWidth() > 0) {
            canvas.drawLine(mBounds.centerX() + innerX, mBounds.centerY() + innerY,
                    mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                    mHollowHandPaint);
        }

        /* Minute hand */
        outerRadius = mBounds.width() * 0.46f - mHandPaint.getStrokeWidth();
        rot = (float) ((minutes + milliSeconds / 60000) * Math.PI * 2 / 60);
        innerX = (float) Math.sin(rot) * innerRadius;
        innerY = (float) -Math.cos(rot) * innerRadius;
        outerX = (float) Math.sin(rot) * outerRadius;
        outerY = (float) -Math.cos(rot) * outerRadius;
        canvas.drawLine(mBounds.centerX(), mBounds.centerY(),
                mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                mConnectHandCenterPaint);
        canvas.drawLine(mBounds.centerX() + innerX, mBounds.centerY() + innerY,
                mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                mHandPaint);
        if (mHollowHandPaint.getStrokeWidth() > 0) {
            canvas.drawLine(mBounds.centerX() + innerX, mBounds.centerY() + innerY,
                    mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                    mHollowHandPaint);
        }

        /* Hands center */
        canvas.drawLine(mBounds.centerX(), mBounds.centerY(),
                mBounds.centerX() + 0.0001f, mBounds.centerY(),
                mHandCenterPaint);

        /* Value hand */
        outerRadius = mBounds.width() / 2;
        innerRadius = mBounds.width() * -0.08f;
        rot = (float) (mValue * Math.PI * 2 / mScale / 1000);
        innerX = (float) Math.sin(rot) * innerRadius;
        innerY = (float) -Math.cos(rot) * innerRadius;
        outerX = (float) Math.sin(rot) * outerRadius;
        outerY = (float) -Math.cos(rot) * outerRadius;
        canvas.drawLine(mBounds.centerX() + innerX, mBounds.centerY() + innerY,
                mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                mValueHandPaint);
        if (mLapValue > -1) {
            rot = (float) (mLapValue * Math.PI * 2 / mScale / 1000);
            innerX = (float) Math.sin(rot) * innerRadius;
            innerY = (float) -Math.cos(rot) * innerRadius;
            outerX = (float) Math.sin(rot) * outerRadius;
            outerY = (float) -Math.cos(rot) * outerRadius;
            canvas.drawLine(mBounds.centerX() + innerX, mBounds.centerY() + innerY,
                    mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                    mLapValueHandPaint);
        }

        /* Value center */
        canvas.drawLine(mBounds.centerX(), mBounds.centerY(),
                mBounds.centerX() + 0.0001f, mBounds.centerY(),
                mLapValue < 0 ? mValueCenterPaint : mLapValueCenterPaint);

        /* Center */
        canvas.drawLine(mBounds.centerX(), mBounds.centerY(),
                mBounds.centerX() + 0.0001f, mBounds.centerY(),
                mCenterPaint);
    }

    public void setValue(long value) {
        mValue = value;
    }

    public void setLapValue(long lapValue) {
        mLapValue = lapValue;
    }

    public void setScale(int scale) {
        mScale = scale;
    }

    @Override
    public void setColor(int color) {
        mValueCenterPaint.setColor(color);
        mValueHandPaint.setColor(color);
        mMinuteValueCenterPaint.setColor(color);
    }

    public void setAccentColor(int accentColor) {
        mLapValueCenterPaint.setColor(accentColor);
        mLapValueHandPaint.setColor(accentColor);
        mLapMinuteValueCenterPaint.setColor(accentColor);
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
