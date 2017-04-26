package com.seapip.thomas.pear.module;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Calendar;

public class ClockHandsModule implements Module {
    private Rect mBounds;
    private Calendar mCalendar;
    private boolean mAmbient;
    private boolean mBurnInProtection;

    /* Paint */
    private Paint mHandPaint;
    private Paint mHollowHandPaint;
    private Paint mConnectHandCenterPaint;
    private Paint mHandCenterPaint;
    private Paint mSecondsCenterPaint;
    private Paint mSecondsHandPaint;
    private Paint mCenterPaint;

    public ClockHandsModule(Calendar calendar) {
        mCalendar = calendar;

        /* Paint */
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
        mSecondsCenterPaint = new Paint();
        mSecondsCenterPaint.setAntiAlias(true);
        mSecondsCenterPaint.setStrokeCap(Paint.Cap.ROUND);
        mSecondsHandPaint = new Paint();
        mSecondsHandPaint.setAntiAlias(true);
        mSecondsHandPaint.setStrokeCap(Paint.Cap.ROUND);
        mCenterPaint = new Paint();
        mCenterPaint.setAntiAlias(true);
        mCenterPaint.setColor(Color.BLACK);
        mCenterPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
        int stroke = (int) (mBounds.width() * 0.042f);
        if(stroke % 2 != 0) {
            stroke--;
        }
        mHandPaint.setStrokeWidth(stroke);
        mHollowHandPaint.setStrokeWidth(stroke - 6);
        stroke = (int) (mBounds.width() * 0.016f);
        if(stroke % 2 != 0) {
            stroke--;
        }
        mConnectHandCenterPaint.setStrokeWidth(stroke);
        stroke = (int) (mBounds.width() * 0.048f);
        if(stroke % 2 != 0) {
            stroke--;
        }
        mHandCenterPaint.setStrokeWidth(stroke);
        stroke = (int) (mBounds.width() * 0.036f);
        if(stroke % 2 != 0) {
            stroke--;
        }
        mSecondsCenterPaint.setStrokeWidth(stroke);
        mSecondsHandPaint.setStrokeWidth(mBounds.width() * 0.007f);
        stroke = (int) (mBounds.width() * 0.012f);
        if(stroke % 2 != 0) {
            stroke--;
        }
        mCenterPaint.setStrokeWidth(stroke);
    }

    @Override
    public void draw(Canvas canvas) {
        float hours = mCalendar.get(Calendar.HOUR);
        float minutes = mCalendar.get(Calendar.MINUTE);
        float milliSeconds = mAmbient ?
                0 : mCalendar.get(Calendar.SECOND) * 1000 + mCalendar.get(Calendar.MILLISECOND);

        /* Hour hand */
        float outerRadius = mBounds.width() * 0.28f - mHandPaint.getStrokeWidth();
        float innerRadius = mBounds.width() * 0.04f + mHandPaint.getStrokeWidth();
        float rot = (float) ((hours * 5 + minutes / 12) * Math.PI * 2 / 60);
        float innerX = (float) Math.sin(rot) * innerRadius;
        float innerY = (float) -Math.cos(rot) * innerRadius;
        float outerX = (float) Math.sin(rot) * outerRadius;
        float outerY = (float) -Math.cos(rot) * outerRadius;
        canvas.drawLine(mBounds.centerX(), mBounds.centerY(),
                mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                mConnectHandCenterPaint);
        canvas.drawLine(mBounds.centerX() + innerX, mBounds.centerY() + innerY,
                mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                mHandPaint);
        if(mAmbient && mBurnInProtection && mHollowHandPaint.getStrokeWidth() > 0) {
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
        if(mAmbient && mBurnInProtection && mHollowHandPaint.getStrokeWidth() > 0) {
            canvas.drawLine(mBounds.centerX() + innerX, mBounds.centerY() + innerY,
                    mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                    mHollowHandPaint);
        }

        /* Hands center */
        canvas.drawLine(mBounds.centerX(), mBounds.centerY(),
                mBounds.centerX() + 0.0001f, mBounds.centerY(),
                mHandCenterPaint);

        /* Seconds hand */
        if(!mAmbient) {
            outerRadius = mBounds.width() / 2 - mSecondsHandPaint.getStrokeWidth();
            innerRadius = mBounds.width() * -0.06f - mSecondsHandPaint.getStrokeWidth();
            rot = (float) (milliSeconds * Math.PI * 2 / 60000);
            innerX = (float) Math.sin(rot) * innerRadius;
            innerY = (float) -Math.cos(rot) * innerRadius;
            outerX = (float) Math.sin(rot) * outerRadius;
            outerY = (float) -Math.cos(rot) * outerRadius;
            canvas.drawLine(mBounds.centerX() + innerX, mBounds.centerY() + innerY,
                    mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                    mSecondsHandPaint);

            /* Seconds center */
            canvas.drawLine(mBounds.centerX(), mBounds.centerY(),
                    mBounds.centerX() + 0.0001f, mBounds.centerY(),
                    mSecondsCenterPaint);
        }

        /* Center */
        canvas.drawLine(mBounds.centerX(), mBounds.centerY(),
                mBounds.centerX() + 0.0001f, mBounds.centerY(),
                mCenterPaint);
        /*
        for (int i = 0; i < 60; i++) {
            float rot = (float) (i * Math.PI * 2 / 60);
            float innerX = (float) Math.sin(rot);
            float innerY = (float) -Math.cos(rot);
            float outerX = (float) Math.sin(rot) * outerRadius;
            float outerY = (float) -Math.cos(rot) * outerRadius;
            if (i % 5 == 0) {
                innerX *= innerHourRadius;
                innerY *= innerHourRadius;
            } else {
                innerX *= innerMinuteRadius;
                innerY *= innerMinuteRadius;
            }
            canvas.drawLine(mBounds.centerX() + innerX, mBounds.centerY() + innerY,
                    mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                    mTickPaint);
            if (mAmbient && mBurnInProtection && mHollowTickPaint.getStrokeWidth() > 0) {
                canvas.drawLine(mBounds.centerX() + innerX, mBounds.centerY() + innerY,
                        mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                        mHollowTickPaint);
            }
        }
        */
    }

    @Override
    public void setColor(int color) {
        mSecondsCenterPaint.setColor(color);
        mSecondsHandPaint.setColor(color);
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
