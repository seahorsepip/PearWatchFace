/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seapip.thomas.pear;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class ModularWatchFaceService extends CanvasWatchFaceService {

    public static final int[][] COMPLICATION_SUPPORTED_TYPES = {
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON},
            {ComplicationData.TYPE_LONG_TEXT},
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON},
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON},
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON}
    };
    public static final int MODULE_SPACING = 10;
    private static final int TOP_LEFT_COMPLICATION = 0;
    private static final int CENTER_COMPLICATION = 1;
    private static final int BOTTOM_LEFT_COMPLICATION = 2;
    private static final int BOTTOM_CENTER_COMPLICATION = 3;
    private static final int BOTTOM_RIGHT_COMPLICATION = 4;
    public static final int[] COMPLICATION_IDS = {
            TOP_LEFT_COMPLICATION,
            CENTER_COMPLICATION,
            BOTTOM_LEFT_COMPLICATION,
            BOTTOM_CENTER_COMPLICATION,
            BOTTOM_RIGHT_COMPLICATION
    };
    private static final long INTERACTIVE_UPDATE_RATE_MS = 32;
    private static final int MSG_UPDATE_TIME = 0;

    public static int SETTINGS_MODE = 0;

    private SharedPreferences mPrefs;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<ModularWatchFaceService.Engine> mWeakReference;

        public EngineHandler(ModularWatchFaceService.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            ModularWatchFaceService.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        private final Handler mUpdateTimeHandler = new EngineHandler(this);

        private Calendar mCalendar;
        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        private boolean mRegisteredTimeZoneReceiver = false;

        /* Display */
        private int mWidth;
        private int mHeight;
        private boolean mIsRound;

        /* Complications */
        private RectF[] mComplicationTapBoxes;

        /* Ambient */
        private boolean mAmbient;
        private boolean mLowBitAmbient;
        private boolean mBurnInProtection;

        /*Modules */
        private ArrayList<Module> mModules;
        private ClockModule mClockModule;
        private ComplicationModule mTopLeftComplicationModule;
        private ComplicationModule mCenterComplicationModule;
        private ComplicationModule mBottomLeftComplicationModule;
        private ComplicationModule mBottomCenterComplicationModule;
        private ComplicationModule mBottomRightComplicationModule;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(ModularWatchFaceService.this)
                    .setStatusBarGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                    .setAcceptsTapEvents(true)
                    .build());

            mCalendar = Calendar.getInstance();
            mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            initializeFonts();
            initializeComplications();
            initializeModules();
        }

        private void initializeFonts() {
            /*
            mFontLight = Typeface.create("sans-serif-light", Typeface.NORMAL);
            mFontBold = Typeface.create("sans-serif", Typeface.BOLD);
            mFont = Typeface.create("sans-serif", Typeface.NORMAL);
            */
        }

        private void initializeComplications() {
            mComplicationTapBoxes = new RectF[COMPLICATION_IDS.length];
            setActiveComplications(COMPLICATION_IDS);
        }

        private void initializeModules() {
            mClockModule = new ClockModule(mCalendar, true);
            mTopLeftComplicationModule = new ComplicationModule(getApplicationContext());
            mCenterComplicationModule = new ComplicationModule(getApplicationContext());
            mBottomLeftComplicationModule = new ComplicationModule(getApplicationContext());
            mBottomCenterComplicationModule = new ComplicationModule(getApplicationContext());
            mBottomRightComplicationModule = new ComplicationModule(getApplicationContext());

            mModules = new ArrayList<>();
            mModules.add(mClockModule);
            mModules.add(mTopLeftComplicationModule);
            mModules.add(mCenterComplicationModule);
            mModules.add(mBottomLeftComplicationModule);
            mModules.add(mBottomCenterComplicationModule);
            mModules.add(mBottomRightComplicationModule);

            for (Module module : mModules) {
                module.setColor(Color.CYAN);
            }
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
        }

        @Override
        public void onComplicationDataUpdate(int complicationId,
                                             ComplicationData complicationData) {
            ((ComplicationModule) mModules.get(complicationId + 1)).setComplicationData(complicationData);
            invalidate();
        }


        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            mAmbient = inAmbientMode;
            setBounds();
            /*
            for (Module module : mModules) {
                module.setColor(mAmbient ? Color.CYAN : Color.YELLOW);
            }
            */
            updateTimer();
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            mIsRound = insets.isRound();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mWidth = width;
            mHeight = height;
            setBounds();
        }

        private void setBounds() {
            int inset = (mWidth - (int) Math.sqrt(mWidth * mWidth / 2)) / 2;
            if (SETTINGS_MODE == 2) {
                inset += 20;
            }

            Rect bounds = new Rect(inset, inset, mWidth - inset, mHeight - inset);

            mClockModule.setBounds(new Rect(
                    bounds.right - (bounds.width() - MODULE_SPACING * 2) / 3 * 2 - MODULE_SPACING,
                    bounds.top,
                    bounds.right,
                    bounds.top + bounds.height() / 3 - MODULE_SPACING / 2)
            );
            mTopLeftComplicationModule.setBounds(new Rect(
                    bounds.left,
                    bounds.top,
                    bounds.left + (bounds.width() - MODULE_SPACING * 2) / 3,
                    bounds.top + (bounds.height() - MODULE_SPACING * 2) / 3)
            );
            mCenterComplicationModule.setBounds(new Rect(
                    bounds.left,
                    bounds.top + (bounds.height() - MODULE_SPACING * 2) / 3 + MODULE_SPACING,
                    bounds.right,
                    bounds.bottom - (bounds.height() - MODULE_SPACING * 2) / 3 - MODULE_SPACING)
            );
            mBottomLeftComplicationModule.setBounds(new Rect(
                    bounds.left,
                    bounds.bottom - (bounds.height() - MODULE_SPACING * 2) / 3,
                    bounds.left + (bounds.width() - MODULE_SPACING * 2) / 3,
                    bounds.bottom)
            );
            mBottomCenterComplicationModule.setBounds(new Rect(
                    bounds.left + (bounds.width() - MODULE_SPACING * 2) / 3 + MODULE_SPACING,
                    bounds.bottom - (bounds.height() - MODULE_SPACING * 2) / 3,
                    bounds.right - (bounds.width() - MODULE_SPACING * 2) / 3 - MODULE_SPACING,
                    bounds.bottom
            ));
            mBottomRightComplicationModule.setBounds(new Rect(
                    bounds.right - (bounds.width() - MODULE_SPACING * 2) / 3,
                    bounds.bottom - (bounds.height() - MODULE_SPACING * 2) / 3,
                    bounds.right,
                    bounds.bottom)
            );

        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    for (int i = 0; i < mComplicationTapBoxes.length; i++) {
                        if (mComplicationTapBoxes[i] != null && mComplicationTapBoxes[i].contains(x, y)) {
                            onComplicationTapped(i);
                        }
                    }
                    break;
            }
            invalidate();
        }

        private void onComplicationTapped(int id) {
            ComplicationData complicationData = null;

            if (complicationData != null) {

                if (complicationData.getTapAction() != null) {
                    try {
                        complicationData.getTapAction().send();
                    } catch (PendingIntent.CanceledException e) {
                    }

                } else if (complicationData.getType() == ComplicationData.TYPE_NO_PERMISSION) {
                    ComponentName componentName = new ComponentName(
                            getApplicationContext(),
                            ModularWatchFaceService.class);

                    Intent permissionRequestIntent =
                            ComplicationHelperActivity.createPermissionRequestHelperIntent(
                                    getApplicationContext(), componentName);

                    startActivity(permissionRequestIntent);
                }
            }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            if (SETTINGS_MODE > 0) {
                setBounds();
                SETTINGS_MODE = SETTINGS_MODE == 1 ? 0 : 2;
            }

            canvas.drawColor(Color.BLACK);
            /*
            Drawable bgTemp = getResources().getDrawable(R.drawable.bg_temp);
            bgTemp.setBounds(0, 0, bounds.right, bounds.bottom);
            bgTemp.draw(canvas);
            */

            for (int x = 0; x < COMPLICATION_IDS.length; x++) {
                ((ComplicationModule) mModules.get(COMPLICATION_IDS[x] + 1)).setCurrentTimeMillis(now);
            }

            for (Module module : mModules) {
                module.draw(canvas);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();
                /* Update time zone in case it changed while we weren't visible. */
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            ModularWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            ModularWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        /**
         * Starts/stops the {@link #mUpdateTimeHandler} timer based on the state of the watch face.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer
         * should only run in active mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !mAmbient;
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }
}
