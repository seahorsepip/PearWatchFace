package com.seapip.thomas.pear;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Xfermode;

public class SettingModuleOverlay {
    private Rect mBounds;
    private String mTitle;
    private Paint.Align mAlign;
    private Intent mIntent;
    private int mRequestCode;
    private boolean mActive;

    /* Colors */
    private int mColor;
    private int mActiveColor;

    /* Fonts */
    private Typeface mFontBold;

    /* Paint */
    private Paint mBoxPaint;
    private Paint mOverlayRemovePaint;
    private Paint mTitleTextPaint;
    private Paint mTitlePaint;

    /* Path */
    private Path mBoxPath;
    private Path mTitlePath;

    public SettingModuleOverlay(Rect bounds, String title, Paint.Align align, Intent intent, int requestCode) {
        mBounds = bounds;
        mAlign = align;
        mIntent = intent;
        mRequestCode = requestCode;

        /* Colors */
        mColor = Color.argb(102, 255, 255, 255);
        mActiveColor = Color.parseColor("#00c853");

        /* Fonts */
        mFontBold = Typeface.create("sans-serif", Typeface.BOLD);

        /* Paint */
        mBoxPaint = new Paint();
        mBoxPaint.setColor(mColor);
        mBoxPaint.setStrokeWidth(2);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setAntiAlias(true);
        mOverlayRemovePaint = new Paint();
        mOverlayRemovePaint.setAntiAlias(true);
        mOverlayRemovePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mTitleTextPaint = new Paint();
        mTitleTextPaint.setColor(Color.BLACK);
        mTitleTextPaint.setTypeface(mFontBold);
        mTitleTextPaint.setTextSize(20);
        mTitleTextPaint.setAntiAlias(true);
        mTitleTextPaint.setTextAlign(align);
        mTitlePaint = new Paint();
        mTitlePaint.setColor(mActiveColor);
        mTitlePaint.setStyle(Paint.Style.FILL);
        mTitlePaint.setAntiAlias(true);

        /* Path */
        mBoxPath = roundedRect(bounds, 16);
        setTitle(title);
    }

    public void draw(Canvas canvas) {
        canvas.drawPath(mBoxPath, mOverlayRemovePaint);
        canvas.drawPath(mBoxPath, mBoxPaint);
        if (mActive && mTitle != null) {
            canvas.drawPath(mTitlePath, mTitlePaint);
            canvas.drawText(mTitle.toUpperCase(),
                    mAlign == Paint.Align.LEFT ? mBounds.left + 7 :
                            mAlign == Paint.Align.CENTER ? mBounds.centerX() - 1:
                                    mBounds.right - 7,
                    mBounds.top - 18 - (mTitleTextPaint.descent() + mTitleTextPaint.ascent()) / 2,
                    mTitleTextPaint);
        }
    }

    public boolean contains(int x, int y) {
        return mBounds.contains(x, y);
    }

    public void setTitle(String title) {
        mTitle = title;
        Rect titleBounds = new Rect();
        mTitleTextPaint.getTextBounds(title.toUpperCase(), 0, title.length(), titleBounds);
        Rect titleRect = new Rect(mBounds.left - 1, mBounds.top - 30,
                mBounds.left + titleBounds.width() + 16, mBounds.top - 6);
        int width = titleRect.width();
        if (mAlign == Paint.Align.CENTER) {
            titleRect.left += mBounds.width() / 2 - width / 2;
            titleRect.right += mBounds.width() / 2 - width / 2;
        } else if (mAlign == Paint.Align.RIGHT) {
            titleRect.left += mBounds.width() - width + 1;
            titleRect.right += mBounds.width() - width + 1;
        }
        mTitlePath = roundedRect(titleRect, 4);
    }

    public boolean getActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
        mBoxPaint.setColor(active ? mActiveColor : mColor);
    }

    public Intent getIntent() {
        return mIntent;
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    private Path roundedRect(Rect bounds, int radius) {
        Path path = new Path();
        path.moveTo(bounds.left + radius, bounds.top);
        path.lineTo(bounds.right - radius, bounds.top);
        path.arcTo(bounds.right - 2 * radius, bounds.top,
                bounds.right, bounds.top + 2 * radius,
                -90, 90, false);
        path.lineTo(bounds.right, bounds.bottom - radius);
        path.arcTo(bounds.right - 2 * radius, bounds.bottom - 2 * radius,
                bounds.right, bounds.bottom,
                0, 90, false);
        path.lineTo(bounds.left + radius, bounds.bottom);
        path.arcTo(bounds.left, bounds.bottom - 2 * radius,
                bounds.left + 2 * radius, bounds.bottom,
                90, 90, false);
        path.lineTo(bounds.left, bounds.top + radius);
        path.arcTo(bounds.left, bounds.top,
                bounds.left + 2 * radius, bounds.top + 2 * radius,
                180, 90, false);
        return path;
    }
}
