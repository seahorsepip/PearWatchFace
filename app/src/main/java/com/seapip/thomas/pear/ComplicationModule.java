package com.seapip.thomas.pear;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationText;

public class ComplicationModule implements Module {
    private Rect mBounds;
    private Context mContext;
    private ComplicationData mComplicationData;
    private long mCurrentTimeMillis;

    /* Fonts */
    Typeface mFontBold;

    /* Paint */
    private Paint backgroundPaint;
    private Paint mShortTextTitlePaint;
    private Paint mShortTextTextPaint;

    public ComplicationModule(Context context) {
        mContext = context;

        /* Fonts */
        mFontBold = Typeface.create("sans-serif", Typeface.BOLD);

        /* Paint */
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.argb(20, 0, 255, 0));
        mShortTextTitlePaint = new Paint();
        mShortTextTitlePaint.setAntiAlias(true);
        mShortTextTitlePaint.setTextAlign(Paint.Align.CENTER);
        mShortTextTitlePaint.setColor(Color.WHITE);
        mShortTextTitlePaint.setTypeface(mFontBold);
        mShortTextTextPaint = new Paint();
        mShortTextTextPaint.setAntiAlias(true);
        mShortTextTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
        mShortTextTitlePaint.setTextSize(bounds.height() * 0.2f);
        mShortTextTextPaint.setTextSize(bounds.height() * 0.5f);
    }

    @Override
    public void draw(Canvas canvas) {
        //canvas.drawRect(mBounds, backgroundPaint);
        if ((mComplicationData != null) && (mComplicationData.isActive(mCurrentTimeMillis))) {
            switch (mComplicationData.getType()) {
                case ComplicationData.TYPE_RANGED_VALUE:
                    drawRange(canvas);
                    break;
                case ComplicationData.TYPE_SHORT_TEXT:
                    drawShortText(canvas);
                    break;
            }
        }
    }

    private void drawRange(Canvas canvas) {
    }

    private void drawShortText(Canvas canvas) {
        ComplicationText title = mComplicationData.getShortTitle();
        ComplicationText text = mComplicationData.getShortText();

        float padding = mBounds.height() * 0.2f;
        float textY = mBounds.centerY() - (mShortTextTextPaint.descent() + mShortTextTextPaint.ascent()) / 2;
        if (title != null) {
            canvas.drawText(title.getText(mContext, mCurrentTimeMillis).toString().toUpperCase(),
                    mBounds.centerX(),
                    mBounds.top + padding - mShortTextTitlePaint.descent() - mShortTextTitlePaint.ascent(),
                    mShortTextTitlePaint);
            textY = mBounds.bottom - padding;
        }
        canvas.drawText(text.getText(mContext, mCurrentTimeMillis).toString().toUpperCase(),
                mBounds.centerX(),
                textY,
                mShortTextTextPaint);
    }

    @Override
    public void setColor(int color) {
        mShortTextTextPaint.setColor(color);
    }

    public void setComplicationData(ComplicationData complicationData) {
        mComplicationData = complicationData;
    }

    public void setCurrentTimeMillis(long currentTimeMillis) {
        mCurrentTimeMillis = currentTimeMillis;
    }
}
