package com.seapip.thomas.pear.settings;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;

public class SettingsOverlay {
    private Rect mBounds;
    private Rect mScreenBounds;
    private String mTitle;
    private Paint.Align mAlign;
    private Intent mIntent;
    private Runnable mRunnable;
    private Intent mData;
    private int mRequestCode;
    private boolean mActive;
    private boolean mRound;
    private boolean mInsetTitle;

    /* Colors */
    private int mColor;
    private int mActiveColor;

    /* Fonts */
    private Typeface mFontBold;

    /* Paint */
    private Paint mBoxPaint;
    private Paint mOverlayRemovePaint;
    private TextPaint mTitleTextPaint;
    private Paint mTitlePaint;

    /* Path */
    private Path mBoxPath;
    private Path mTitlePath;

    public SettingsOverlay(Rect bounds, Rect screenbounds, String title, Paint.Align align) {
        mBounds = bounds;
        mScreenBounds = screenbounds;
        mAlign = align;
        mRequestCode = this.hashCode();

        /* Colors */
        mColor = Color.argb(102, 255, 255, 255);
        mActiveColor = Color.parseColor("#69F0AE");

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
        mTitleTextPaint = new TextPaint();
        mTitleTextPaint.setColor(Color.BLACK);
        mTitleTextPaint.setTypeface(mFontBold);
        mTitleTextPaint.setTextSize(18);
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
            RectF titleRect = new RectF();
            mTitlePath.computeBounds(titleRect, false);
            float textX = titleRect.centerX();
            switch (mAlign) {
                case LEFT:
                    textX = titleRect.left + 8;
                    break;
                case RIGHT:
                    textX = titleRect.right - 8;
                    break;
            }
            canvas.drawText(mTitle.toUpperCase(),
                    textX,
                    titleRect.centerY() - (mTitleTextPaint.descent() + mTitleTextPaint.ascent()) / 2,
                    mTitleTextPaint);
        }
    }

    public boolean contains(int x, int y) {
        return mBounds.contains(x, y);
    }

    public void setTitle(String title) {
        mTitle = title;
        int width = (int) mTitleTextPaint.measureText(title.toUpperCase());
        if (width > mScreenBounds.width()) {
            width = mScreenBounds.width() - 14;
            mTitle = TextUtils.ellipsize(title, mTitleTextPaint, width - 38, TextUtils.TruncateAt.END).toString();
        }
        Rect titleRect = new Rect(mBounds.centerX() - width / 2 - 8,
                mBounds.top - 30,
                mBounds.centerX() + width / 2 + 8,
                mBounds.top - 6);
        switch (mAlign) {
            case LEFT:
                titleRect.left = mBounds.left - 1;
                titleRect.right = mBounds.left + width - 1 + 16;
                break;
            case RIGHT:
                titleRect.left = mBounds.right - width + 1 - 16;
                titleRect.right = mBounds.right + 1;
                break;
        }
        if (mRound && mInsetTitle) {
            titleRect.top = mBounds.bottom - 50;
            titleRect.bottom = mBounds.bottom - 26;
        } else if (mInsetTitle) {
            titleRect.top = mBounds.bottom - 30;
            titleRect.bottom = mBounds.bottom - 6;
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

    public void setRound(boolean round) {
        mRound = round;
        int radius = mBounds.width();
        if(mBounds.height() < radius) {
            radius = mBounds.height();
        }
        mBoxPath = roundedRect(mBounds, radius / 2);
    }

    public void setInsetTitle(boolean insetTitle) {
        mInsetTitle = insetTitle;
        setTitle(mTitle);
    }

    public Intent getIntent() {
        return mIntent;
    }

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    public Intent getData() {
        return mData;
    }

    public void setData(Intent data) {
        mData = data;
    }

    public Runnable getRunnable() {
        return mRunnable;
    }

    public void setRunnable(Runnable runnable) {
        mRunnable = runnable;
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
