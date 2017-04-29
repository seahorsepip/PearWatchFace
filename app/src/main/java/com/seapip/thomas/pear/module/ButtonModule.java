package com.seapip.thomas.pear.module;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class ButtonModule implements Module {
    private Rect mBounds;
    private boolean mAmbient;
    private boolean mBurnInProtection;
    private Drawable mDrawable;
    private Drawable mDrawableBurnInProtection;

    /* Paint */
    private Paint mCirclePaint;

    public ButtonModule(Drawable drawable) {
        mDrawable = drawable;

        /* Paint */
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
    }

    public ButtonModule(Drawable drawable, Drawable drawableBurnInProtection) {
        this(drawable);
        mDrawableBurnInProtection = drawableBurnInProtection;
    }

    public boolean contains(int x, int y) {
        return mBounds.contains(x, y);
    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
        int size = (int) (bounds.width() * 0.6f);
        mDrawable.setBounds(bounds.centerX() - size / 2,
                bounds.centerY() - size / 2,
                bounds.centerX() + size / 2,
                bounds.centerY() + size / 2);
        if(mDrawableBurnInProtection != null) {
            mDrawableBurnInProtection.setBounds(bounds.centerX() - size / 2,
                    bounds.centerY() - size / 2,
                    bounds.centerX() + size / 2,
                    bounds.centerY() + size / 2);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!(mAmbient && mBurnInProtection)) {
            canvas.drawCircle(mBounds.centerX(), mBounds.centerY(),
                    mBounds.width() / 2, mCirclePaint);
            mDrawable.draw(canvas);
        } else if (mDrawableBurnInProtection != null) {
            mDrawableBurnInProtection.draw(canvas);
        } else {
            mDrawable.draw(canvas);
        }
    }

    @Override
    public void setColor(int color) {
        mDrawable.setTint(color);
        if(mDrawableBurnInProtection != null) {
            mDrawableBurnInProtection.setTint(color);
        }
        mCirclePaint.setColor(color);
        mCirclePaint.setAlpha(52);
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
