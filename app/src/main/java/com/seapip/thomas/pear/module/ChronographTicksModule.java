package com.seapip.thomas.pear.module;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class ChronographTicksModule implements Module {
    private Rect mBounds;
    private boolean mAmbient;
    private boolean mBurnInProtection;
    private int mScale;

    /* Paint */
    private Paint mTickPaint;
    private Paint mValueTickPaint;
    private Paint mTextPaint;

    public ChronographTicksModule(int scale) {
        mScale = scale;
        mTickPaint = new Paint();
        mTickPaint.setAntiAlias(true);
        mTickPaint.setColor(Color.parseColor("#707070"));
        mValueTickPaint = new Paint();
        mValueTickPaint.setAntiAlias(true);
        mValueTickPaint.setColor(Color.WHITE);
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
        mTextPaint.setTextSize(bounds.width() * 0.08f);
    }

    @Override
    public void draw(Canvas canvas) {
        float outerRadius = mBounds.width() / 2;
        float textRadius = outerRadius - mBounds.width() * 0.10f;
        for (int i = 0; i < 240; i++) {
            float rot = (float) (i * Math.PI * 2 / 240);
            float x = (float) Math.sin(rot);
            float y = (float) -Math.cos(rot);
            float innerRadius = outerRadius - mBounds.width() * (i % 4 == 0 ? 0.042f : 0.022f);
            canvas.drawLine(mBounds.centerX() + x * innerRadius,
                    mBounds.centerY() + y * innerRadius,
                    mBounds.centerX() + x * outerRadius,
                    mBounds.centerY() + y * outerRadius,
                    ((i % (48 / (float) mScale) == 0 && mScale < 12)
                            || (i % 16 == 0 && mScale == 30)
                            || (i % 20 == 0 && (mScale > 30 || mScale == 12))) ?
                            mValueTickPaint : mTickPaint);
            if ((i % (240 / mScale) == 0 && mScale < 30)
                    || (i % 16 == 0 && mScale == 30)
                    || (i % 20 == 0 && mScale > 30)) {
                canvas.drawText(String.valueOf(i == 0 ? mScale : mScale * i / 240),
                        mBounds.centerX() + x * textRadius,
                        mBounds.centerY() + y * textRadius
                                - (mTextPaint.descent() + mTextPaint.ascent()) / 2,
                        mTextPaint);
            }
        }
    }

    public void setScale(int scale) {
        mScale = scale;
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
