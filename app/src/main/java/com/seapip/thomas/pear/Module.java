package com.seapip.thomas.pear;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public interface Module {
    void setBounds(Rect bounds);
    void draw(Canvas canvas);
    void setColor(int color);
    void setAmbient(boolean ambient);
    void setBurnInProtection(boolean burnInProtection);
    void setLowBitAmbient(boolean lowBitAmbient);
}
