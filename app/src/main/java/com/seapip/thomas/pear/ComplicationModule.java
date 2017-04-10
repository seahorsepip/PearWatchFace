package com.seapip.thomas.pear;

import android.graphics.Canvas;
import android.graphics.Rect;

public class ComplicationModule implements Module {
    private Rect mBounds;

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void setColor(int color) {

    }
}
