package com.seapip.thomas.pear.module;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.ComplicationText;

import com.seapip.thomas.pear.DrawableTools;
import com.seapip.thomas.pear.modular.WatchFaceService;

public class ComplicationModule implements Module {
    private Rect mBounds;
    private Context mContext;
    private ComplicationData mComplicationData;
    private long mCurrentTimeMillis;
    private boolean mAmbient;
    private boolean mBurnInProtection;
    private boolean mLowBitAmbient;
    private boolean mRangeValue = false;

    /* Paint */
    private Paint mRangeCirclePaint;
    private Paint mRangeArcPaint;
    private Paint mRangeValuePaint;
    private Paint mShortTextTitlePaint;
    private Paint mShortTextTextPaint;
    private Paint mShortTextHorizontalTitlePaint;
    private Paint mShortTextHorizontalTextPaint;
    private Paint mLongTextTitlePaint;
    private Paint mLongTextTextPaint;
    private Paint mSmallImageOverlayPaint;

    public ComplicationModule(Context context) {
        mContext = context;

        /* Paint */
        mRangeCirclePaint = new Paint();
        mRangeCirclePaint.setAntiAlias(true);
        mRangeCirclePaint.setStyle(Paint.Style.STROKE);
        mRangeArcPaint = new Paint();
        mRangeArcPaint.setAntiAlias(true);
        mRangeArcPaint.setStyle(Paint.Style.STROKE);
        mRangeArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mRangeValuePaint = new Paint();
        mRangeValuePaint.setAntiAlias(true);
        mRangeValuePaint.setTextAlign(Paint.Align.CENTER);
        mShortTextTitlePaint = new Paint();
        mShortTextTitlePaint.setAntiAlias(true);
        mShortTextTitlePaint.setTextAlign(Paint.Align.CENTER);
        mShortTextTitlePaint.setColor(Color.WHITE);
        mShortTextTextPaint = new Paint();
        mShortTextTextPaint.setAntiAlias(true);
        mShortTextTextPaint.setTextAlign(Paint.Align.CENTER);
        mShortTextHorizontalTitlePaint = new Paint();
        mShortTextHorizontalTitlePaint.setAntiAlias(true);
        mShortTextHorizontalTitlePaint.setColor(Color.WHITE);
        mShortTextHorizontalTitlePaint.setTextAlign(Paint.Align.LEFT);
        mShortTextHorizontalTextPaint = new Paint();
        mShortTextHorizontalTextPaint.setAntiAlias(true);
        mShortTextHorizontalTextPaint.setTextAlign(Paint.Align.RIGHT);
        mLongTextTitlePaint = new Paint();
        mLongTextTitlePaint.setAntiAlias(true);
        mLongTextTextPaint = new Paint();
        mLongTextTextPaint.setAntiAlias(true);
        mLongTextTextPaint.setColor(Color.WHITE);
        mSmallImageOverlayPaint = new Paint();
        mSmallImageOverlayPaint.setAntiAlias(true);
    }

    @Override
    public void setBounds(Rect bounds) {
        mBounds = bounds;
        mRangeCirclePaint.setStrokeWidth(Math.round(bounds.height() * 0.05f));
        mRangeArcPaint.setStrokeWidth(Math.round(bounds.height() * 0.05f));
        mRangeValuePaint.setTextSize(bounds.height() * 0.25f);
        mShortTextTitlePaint.setTextSize(bounds.height() * 0.22f);
        mLongTextTitlePaint.setTextSize(bounds.height() * 0.35f);
        mLongTextTextPaint.setTextSize(bounds.height() * 0.30f);
    }

    @Override
    public void draw(Canvas canvas) {
        if ((mComplicationData != null) && (mComplicationData.isActive(mCurrentTimeMillis))) {
            switch (mComplicationData.getType()) {
                case ComplicationData.TYPE_RANGED_VALUE:
                    drawRangedValue(canvas);
                    break;
                case ComplicationData.TYPE_SHORT_TEXT:
                    if(mBounds.width() > mBounds.height()) {
                        drawShortTextHorizontal(canvas);
                    } else {
                        drawShortText(canvas);
                    }
                    break;
                case ComplicationData.TYPE_LONG_TEXT:
                    drawLongText(canvas);
                    break;
                case ComplicationData.TYPE_SMALL_IMAGE:
                    drawSmallImage(canvas);
                    break;
                case ComplicationData.TYPE_ICON:
                    drawIcon(canvas);
                    break;
            }
        }
    }

    private void drawRangedValue(Canvas canvas) {
        float val = mComplicationData.getValue();
        float min = mComplicationData.getMinValue();
        float max = mComplicationData.getMaxValue();
        Icon icon = mAmbient && mBurnInProtection &&
                mComplicationData.getBurnInProtectionIcon() != null ?
                mComplicationData.getBurnInProtectionIcon() : mComplicationData.getIcon();

        canvas.drawCircle(mBounds.centerX(), mBounds.centerY(),
                mBounds.height() * 0.32f - mRangeCirclePaint.getStrokeWidth() / 2,
                mRangeCirclePaint);

        float padding = 0.18f * mBounds.height() + mRangeCirclePaint.getStrokeWidth() / 2;
        canvas.drawArc(mBounds.left + padding, mBounds.top + padding,
                mBounds.right - padding, mBounds.bottom - padding,
                -90, (float) (360 * (val - min) / (max - min)), false, mRangeArcPaint);

        if (mRangeValue || icon == null) {
            String valString = String.valueOf((int) val);
            if (val > 99999) {
                valString = String.valueOf(val / 1000000).substring(0, 3) + "M";
            } else if (val > 999) {
                valString = String.valueOf(val / 1000).substring(0, 3);
                valString += "K";
            }
            if (valString.length() > 2 && valString.charAt(2) == '.') {
                valString = valString.substring(0, 2) + valString.substring(3, valString.length());
            }
            canvas.drawText(valString, mBounds.centerX(),
                    mBounds.centerY() - (mRangeValuePaint.descent() + mRangeValuePaint.ascent()) / 2,
                    mRangeValuePaint);
        } else {
            Drawable drawable = icon.loadDrawable(mContext);
            if (drawable != null) {
                drawable.setTint(mRangeArcPaint.getColor());
                int size = (int) (mBounds.height() * 0.3);
                drawable.setBounds(mBounds.centerX() - size / 2, mBounds.centerY() - size / 2,
                        mBounds.centerX() + size / 2, mBounds.centerY() + size / 2);
                drawable.draw(canvas);
            }
        }
    }

    private void drawShortTextHorizontal(Canvas canvas) {
        ComplicationText title = mComplicationData.getShortTitle();
        ComplicationText text = mComplicationData.getShortText();
        String textString = text.getText(mContext, mCurrentTimeMillis).toString().toUpperCase();
        float padding = 0.18f * mBounds.height();

        if (title != null) {
            String titleString = title.getText(mContext, mCurrentTimeMillis).toString().toUpperCase();
            if(titleString.length() > 3) {
                drawShortText(canvas);
                return;
            }
            float titleWidth = mLongTextTitlePaint.measureText(titleString, 0, titleString.length());
            float textWidth = mLongTextTitlePaint.measureText(textString, 0, textString.length());
            float center = textWidth / (titleWidth + textWidth) * (mBounds.width() * 0.9f - padding * 2);
            float scale = center / textWidth;
            mShortTextHorizontalTitlePaint.setTextSize(mLongTextTitlePaint.getTextSize() * scale);
            mShortTextHorizontalTextPaint.setTextSize(mLongTextTitlePaint.getTextSize() * scale);
            float y = mBounds.centerY() - (mShortTextHorizontalTextPaint.descent() + mShortTextHorizontalTextPaint.ascent()) / 2;
            canvas.drawText(titleString,
                    mBounds.left + padding,
                    y,
                    mShortTextHorizontalTitlePaint);
            canvas.drawText(textString,
                    mBounds.right - padding,
                    y,
                    mShortTextHorizontalTextPaint);
        } else {
            drawShortText(canvas);
        }

    }

    private void drawShortText(Canvas canvas) {
        ComplicationText title = mComplicationData.getShortTitle();
        ComplicationText text = mComplicationData.getShortText();
        Icon icon = mAmbient && mBurnInProtection &&
                mComplicationData.getBurnInProtectionIcon() != null ?
                mComplicationData.getBurnInProtectionIcon() : mComplicationData.getIcon();

        float textY = mBounds.bottom - mBounds.height() * 0.2f;
        Paint textPaint = mShortTextTextPaint;

        if (icon != null) {
            Drawable drawable = icon.loadDrawable(mContext);
            if (drawable != null) {
                drawable.setTint(mShortTextTextPaint.getColor());
                int size = (int) (mBounds.height() * 0.4);
                drawable.setBounds(mBounds.centerX() - size / 2,
                        mBounds.top + (int) (mBounds.height() * 0.15f),
                        mBounds.centerX() + size / 2,
                        mBounds.top + (int) (mBounds.height() * 0.15f) + size);
                drawable.draw(canvas);
            }
            textPaint = mShortTextTitlePaint;
        } else if (title != null) {
            String titleString = title.getText(mContext, mCurrentTimeMillis).toString().toUpperCase();
            canvas.drawText(titleString,
                    mBounds.centerX(),
                    mBounds.top + mBounds.height() * 0.2f - mShortTextTitlePaint.descent() - mShortTextTitlePaint.ascent(),
                    mShortTextTitlePaint);
        } else {
            textY = mBounds.centerY() - (mShortTextTextPaint.descent() + mShortTextTextPaint.ascent()) / 2;
        }

        String textString = text.getText(mContext, mCurrentTimeMillis).toString().toUpperCase();
        mShortTextTextPaint.setTextSize(mBounds.height() *
                (0.60f - 0.06f * (textString.length() == 1 ? 2 : textString.length())));
        canvas.drawText(textString,
                mBounds.centerX(),
                textY,
                textPaint);
    }

    private void drawLongText(Canvas canvas) {
        ComplicationText title = mComplicationData.getLongTitle();
        ComplicationText text = mComplicationData.getLongText();

        float textY = mBounds.centerY() - (mShortTextTextPaint.descent() + mShortTextTextPaint.ascent()) / 2;

        if (title != null) {
            String titleString = title.getText(mContext, mCurrentTimeMillis).toString();
            canvas.drawText(titleString,
                    mBounds.left + 0.20f * mBounds.height(),
                    mBounds.top + mBounds.height() * 0.20f - mLongTextTitlePaint.descent() - mLongTextTitlePaint.ascent(),
                    mLongTextTitlePaint);
            textY = mBounds.bottom - mBounds.height() * 0.20f;
        }

        String textString = text.getText(mContext, mCurrentTimeMillis).toString();
        canvas.drawText(textString,
                mBounds.left + 0.20f * mBounds.height(),
                textY,
                mLongTextTextPaint);
    }

    private void drawSmallImage(Canvas canvas) {
        Icon icon = mComplicationData.getSmallImage();
        if (icon != null && !(mAmbient && mBurnInProtection)) {
            Drawable drawable = icon.loadDrawable(mContext);
            if (drawable != null) {
                int size = (int) (mBounds.height() * 0.64);
                if(mComplicationData.getImageStyle() == ComplicationData.IMAGE_STYLE_PHOTO) {
                    drawable = DrawableTools.convertToGrayscale(drawable);
                    drawable = DrawableTools.convertToCircle(drawable);
                }
                drawable.setBounds(mBounds.centerX() - size / 2, mBounds.centerY() - size / 2,
                        mBounds.centerX() + size / 2, mBounds.centerY() + size / 2);
                drawable.draw(canvas);
                if(mComplicationData.getImageStyle() == ComplicationData.IMAGE_STYLE_PHOTO) {
                    canvas.drawCircle(mBounds.centerX(), mBounds.centerY(),
                            mBounds.height() * 0.32f, mSmallImageOverlayPaint);
                }
            }
        }
    }

    private void drawIcon(Canvas canvas) {
        Icon icon = mAmbient && mBurnInProtection &&
                mComplicationData.getBurnInProtectionIcon() != null ?
                mComplicationData.getBurnInProtectionIcon() : mComplicationData.getIcon();
        if (icon != null ) {
            Drawable drawable = icon.loadDrawable(mContext);
            if (drawable != null) {
                int size = (int) (mBounds.height() * 0.4);
                drawable.setTint(mShortTextTextPaint.getColor());
                drawable.setBounds(mBounds.centerX() - size / 2, mBounds.centerY() - size / 2,
                        mBounds.centerX() + size / 2, mBounds.centerY() + size / 2);
                drawable.draw(canvas);
            }
        }
    }

    @Override
    public void setColor(int color) {
        mRangeCirclePaint.setColor(Color.argb(64,
                Color.red(color),
                Color.green(color),
                Color.blue(color)));
        mRangeArcPaint.setColor(color);
        mRangeValuePaint.setColor(color);
        mShortTextTextPaint.setColor(color);
        mShortTextHorizontalTextPaint.setColor(color);
        mLongTextTitlePaint.setColor(color);
        mSmallImageOverlayPaint.setColor(Color.argb(128,
                Color.red(color),
                Color.green(color),
                Color.blue(color)));
    }

    public void setComplicationData(ComplicationData complicationData) {
        mComplicationData = complicationData;
    }

    public void setCurrentTimeMillis(long currentTimeMillis) {
        mCurrentTimeMillis = currentTimeMillis;
    }

    public boolean contains(int x, int y) {
        return mBounds.contains(x, y);
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
        mLowBitAmbient = lowBitAmbient;
    }

    public PendingIntent getTapAction() {
        PendingIntent intent = null;
        if(mComplicationData != null) {
            intent = mComplicationData.getTapAction();
            if (mComplicationData.getType() == ComplicationData.TYPE_NO_PERMISSION) {
                ComponentName componentName = new ComponentName(
                        mContext, WatchFaceService.class);
                Intent permissionRequestIntent =
                        ComplicationHelperActivity.createPermissionRequestHelperIntent(
                                mContext, componentName);
                mContext.startActivity(permissionRequestIntent);
            } else if (mComplicationData.getType() == ComplicationData.TYPE_RANGED_VALUE &&
                    intent == null) {
                mRangeValue = !mRangeValue;
            }
        }
        return intent;
    }
}
