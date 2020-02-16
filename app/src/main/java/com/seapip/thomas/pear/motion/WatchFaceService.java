package com.seapip.thomas.pear.motion;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.seapip.thomas.pear.module.ComplicationModule;
import com.seapip.thomas.pear.module.MotionModule;
import com.seapip.thomas.pear.module.MotionDateModule;
import com.seapip.thomas.pear.module.DigitalClockModule;
import com.seapip.thomas.pear.module.Module;

import java.lang.ref.WeakReference;
import android.text.format.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class WatchFaceService extends CanvasWatchFaceService {

    public static final int[][] COMPLICATION_SUPPORTED_TYPES = {
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON},
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON},
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON}
    };
    public static final int MODULE_SPACING = 10;

    private static final int BOTTOM_LEFT_COMPLICATION = 0;
    private static final int BOTTOM_CENTER_COMPLICATION = 1;
    private static final int BOTTOM_RIGHT_COMPLICATION = 2;
    public static final int[] COMPLICATION_IDS = {
            BOTTOM_LEFT_COMPLICATION,
            BOTTOM_CENTER_COMPLICATION,
            BOTTOM_RIGHT_COMPLICATION
    };
    public static final long INTERACTIVE_UPDATE_RATE_MS = 20;
    private static final int MSG_UPDATE_TIME = 0;

    public static int SETTINGS_MODE = 0;

    public static boolean ROUND = false;

    private SharedPreferences mPrefs;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<WatchFaceService.Engine> mWeakReference;

        public EngineHandler(WatchFaceService.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            WatchFaceService.Engine engine = mWeakReference.get();
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

        private WatchFaceStyle.Builder mWatchFaceStyleBuilder;

        /* Display */
        private int mWidth;
        private int mHeight;
        private boolean mIsRound;
        private boolean mAmbient;

        /*Modules */
        private ArrayList<Module> mModules;
        private MotionModule mMotionModule;
        private ComplicationModule mBottomLeftComplicationModule;
        private ComplicationModule mBottomCenterComplicationModule;
        private ComplicationModule mBottomRightComplicationModule;
        private DigitalClockModule mDigitalClockModule;
        private MotionDateModule mMotionDateModule;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            mWatchFaceStyleBuilder = new WatchFaceStyle.Builder(WatchFaceService.this)
                    .setStatusBarGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                    .setViewProtectionMode(WatchFaceStyle.PROTECT_STATUS_BAR)
                    .setAcceptsTapEvents(true);
            setWatchFaceStyle(mWatchFaceStyleBuilder.build());

            Context context = getApplicationContext();
            mCalendar = Calendar.getInstance();
            mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

            setActiveComplications(COMPLICATION_IDS);

            int date = mPrefs.getInt("settings_motion_date", 0);
            int scene = mPrefs.getInt("settings_motion_scene", 0);

            mMotionModule = new MotionModule(context, scene);
            mBottomLeftComplicationModule = new ComplicationModule(context);
            mBottomCenterComplicationModule = new ComplicationModule(context);
            mBottomRightComplicationModule = new ComplicationModule(context);
            mDigitalClockModule = new DigitalClockModule(mCalendar, DateFormat.is24HourFormat(WatchFaceService.this));
            mMotionDateModule = new MotionDateModule(mCalendar, date);

            mModules = new ArrayList<>();
            mModules.add(mBottomLeftComplicationModule);
            mModules.add(mBottomCenterComplicationModule);
            mModules.add(mBottomRightComplicationModule);
            mModules.add(mMotionModule);
            mModules.add(mDigitalClockModule);
            mModules.add(mMotionDateModule);

            for (Module module : mModules) {
                module.setColor(Color.WHITE);
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
            for (Module module : mModules) {
                module.setBurnInProtection(properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false));
                module.setLowBitAmbient(properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false));
            }
        }

        @Override
        public void onComplicationDataUpdate(int complicationId,
                                             ComplicationData complicationData) {
            ((ComplicationModule) mModules.get(complicationId)).setComplicationData(complicationData);
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
            for (Module module : mModules) {
                module.setAmbient(inAmbientMode);
            }
            updateTimer();
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            mIsRound = insets.isRound();
            ROUND = mIsRound;
            setBounds();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mWidth = width;
            mHeight = height;
            setBounds();
        }

        private void setBounds() {
            int inset = mIsRound ? (mWidth - (int) Math.sqrt(mWidth * mWidth / 2)) / 2 : MODULE_SPACING;
            if (SETTINGS_MODE == 3) {
                inset += 20;
            }

            Rect bounds = new Rect(inset, inset, mWidth - inset, mHeight - inset);

            mMotionModule.setBounds(new Rect(0, 0, mWidth, mHeight));
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
            mDigitalClockModule.setBounds(new Rect(
                    bounds.right - (bounds.width() - MODULE_SPACING * 2) / 3 * 2 - MODULE_SPACING,
                    bounds.top,
                    bounds.right,
                    bounds.top + bounds.height() / 3 - MODULE_SPACING / 2)
            );
            mMotionDateModule.setBounds(new Rect(
                    bounds.left + MODULE_SPACING * 2,
                    bounds.top + bounds.height() / 3 - MODULE_SPACING / 2 * 3,
                    bounds.right,
                    bounds.bottom - (bounds.height() - MODULE_SPACING * 2) / 3 - 3 * MODULE_SPACING)
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
                    boolean tapped = false;
                    for (Module module : mModules) {
                        if (module instanceof ComplicationModule &&
                                ((ComplicationModule) module).contains(x, y)) {
                            PendingIntent intent = ((ComplicationModule) module).getTapAction();
                            tapped = true;
                            if (intent != null) {
                                try {
                                    intent.send();
                                } catch (PendingIntent.CanceledException e) {
                                }
                            }
                        }
                    }
                    if(!tapped) {
                        mMotionModule.tap(x, y);
                    }
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            switch (SETTINGS_MODE) {
                case 1:
                    setBounds();
                    mWatchFaceStyleBuilder.setHideStatusBar(false);
                    setWatchFaceStyle(mWatchFaceStyleBuilder.build());
                    SETTINGS_MODE = 0;
                    break;
                case 3:
                    setBounds();
                    int date = mPrefs.getInt("settings_motion_date", 0);
                    int scene = mPrefs.getInt("settings_motion_scene", 0);
                    mMotionDateModule.setDate(date);
                    mMotionModule.setScene(scene);
                    mWatchFaceStyleBuilder.setHideStatusBar(true);
                    setWatchFaceStyle(mWatchFaceStyleBuilder.build());
                    SETTINGS_MODE = 2;
                    break;
            }

            canvas.drawColor(Color.BLACK);
            for (Module module : mModules) {
                if (module instanceof ComplicationModule) {
                    ((ComplicationModule) module).setCurrentTimeMillis(now);
                }
            }

            mMotionModule.draw(canvas);
            mBottomLeftComplicationModule.draw(canvas);
            mBottomCenterComplicationModule.draw(canvas);
            mBottomRightComplicationModule.draw(canvas);
            mDigitalClockModule.draw(canvas);
            mMotionDateModule.draw(canvas);
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
            mMotionModule.setAmbient(mAmbient);

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            WatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            WatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
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
