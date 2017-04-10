package com.seapip.thomas.pear;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class ColorModule implements Module {
    private Rect mBounds;

    /* Paint */
    private Paint backgroundPaint;

    public ColorModule() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.argb(128, 0, 255, 0));
    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(mBounds, backgroundPaint);
    }

    @Override
    public void setColor(int color) {
    }
}
