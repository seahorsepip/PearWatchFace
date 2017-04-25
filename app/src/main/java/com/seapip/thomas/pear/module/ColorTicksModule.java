package com.seapip.thomas.pear.module;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class ColorTicksModule implements Module {
    private Rect mBounds;
    private boolean mAmbient;
    private boolean mBurnInProtection;

    /* Paint */
    private Paint mTickPaint;
    private Paint mHollowTickPaint;

    public ColorTicksModule() {
        mTickPaint = new Paint();
        mTickPaint.setAntiAlias(true);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mHollowTickPaint = new Paint();
        mHollowTickPaint.setAntiAlias(true);
        mHollowTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mHollowTickPaint.setColor(Color.BLACK);
    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
        int stroke = (int) (bounds.width() * 0.02f);
        if(stroke % 2 != 0) {
            stroke--;
        }
        mTickPaint.setStrokeWidth(stroke);
        mHollowTickPaint.setStrokeWidth(stroke - 6);
    }

    @Override
    public void draw(Canvas canvas) {
        float outerRadius = mBounds.width() / 2 - mTickPaint.getStrokeWidth();
        float innerHourRadius = outerRadius - mBounds.width() * 0.14f + mTickPaint.getStrokeWidth();
        float innerMinuteRadius = outerRadius - mBounds.width() * 0.0001f;
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
            if(mAmbient && mBurnInProtection && mHollowTickPaint.getStrokeWidth() > 0) {
                canvas.drawLine(mBounds.centerX() + innerX, mBounds.centerY() + innerY,
                        mBounds.centerX() + outerX, mBounds.centerY() + outerY,
                        mHollowTickPaint);
            }
        }
    }

    @Override
    public void setColor(int color) {
        mTickPaint.setColor(color);
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
