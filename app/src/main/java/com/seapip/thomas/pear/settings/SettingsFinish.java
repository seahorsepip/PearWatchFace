package com.seapip.thomas.pear.settings;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.seapip.thomas.pear.R;

public class SettingsFinish extends SettingsOverlay {
    Drawable mDrawable;
    Paint mPaint;
    Rect mBounds;

    public SettingsFinish(Context context, Rect bounds) {
        super(bounds, bounds, "", Paint.Align.CENTER);
        mDrawable = context.getResources().getDrawable(R.drawable.ic_check_black_150dp);
        int size = (int) (bounds.width() * 0.375f);
        mBounds = new Rect(bounds.centerX() - size / 2,
                bounds.centerY() - size / 2,
                bounds.centerX() + size / 2,
                bounds.centerY() + size / 2);
        mDrawable.setBounds(mBounds);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#69F0AE"));
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mBounds.centerX(), mBounds.centerY(), mBounds.width() / 2, mPaint);
        mDrawable.draw(canvas);
    }

    @Override
    public boolean contains(int x, int y) {
        if(mBounds.contains(x, y)) {
            getRunnable().run();
        }
        return false;
    }
}
