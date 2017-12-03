package com.seapip.thomas.pear.chronograph;

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

import com.seapip.thomas.pear.R;
import com.seapip.thomas.pear.Timer;
import com.seapip.thomas.pear.module.ButtonModule;
import com.seapip.thomas.pear.module.ChronographClockModule;
import com.seapip.thomas.pear.module.ChronographTicksModule;
import com.seapip.thomas.pear.module.ComplicationModule;
import com.seapip.thomas.pear.module.Module;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class WatchFaceService extends CanvasWatchFaceService {

    public static final int[][] COMPLICATION_SUPPORTED_TYPES = {
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON},
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON},
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON},
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON},
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON},
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON},
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON},
            {ComplicationData.TYPE_RANGED_VALUE, ComplicationData.TYPE_SHORT_TEXT, ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON}
    };
    public static final long INTERACTIVE_UPDATE_RATE_MS = 20;
    private static final int TOP_LEFT_COMPLICATION = 0;
    private static final int TOP_RIGHT_COMPLICATION = 1;
    private static final int RIGHT_COMPLICATION = 2;
    private static final int BOTTOM_LEFT_COMPLICATION = 3;
    private static final int BOTTOM_RIGHT_COMPLICATION = 4;
    private static final int LEFT_COMPLICATION = 5;
    public static final int[] COMPLICATION_IDS = {
            TOP_LEFT_COMPLICATION,
            TOP_RIGHT_COMPLICATION,
            RIGHT_COMPLICATION,
            BOTTOM_LEFT_COMPLICATION,
            BOTTOM_RIGHT_COMPLICATION,
            LEFT_COMPLICATION
    };
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
        private ComplicationModule mTopLeftComplicationModule;
        private ComplicationModule mTopRightComplicationModule;
        private ComplicationModule mRightComplicationModule;
        private ComplicationModule mLeftComplicationModule;
        private ComplicationModule mBottomLeftComplicationModule;
        private ComplicationModule mBottomRightComplicationModule;
        private ChronographTicksModule mChronographTicksModule;
        private ButtonModule mStartButtonModule;
        private ButtonModule mContinueButtonModule;
        private ButtonModule mPauseButtonModule;
        private ButtonModule mStopButtonModule;
        private ButtonModule mLapButtonModule;
        private ChronographClockModule mChronographClockModule;

        /* Timer */
        private Timer mTimer;

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

            int scale = mPrefs.getInt("settings_chronograph_scale", 60);
            mTimer = new Timer(getApplicationContext());

            mTopLeftComplicationModule = new ComplicationModule(context);
            mTopRightComplicationModule = new ComplicationModule(context);
            mRightComplicationModule = new ComplicationModule(context);
            mLeftComplicationModule = new ComplicationModule(context);
            mBottomLeftComplicationModule = new ComplicationModule(context);
            mBottomRightComplicationModule = new ComplicationModule(context);
            mChronographTicksModule = new ChronographTicksModule(12);
            mStartButtonModule = new ButtonModule(context.getDrawable(R.drawable.ic_timer_black_24dp));
            mContinueButtonModule = new ButtonModule(
                    context.getDrawable(R.drawable.ic_continue_black_24dp),
                    context.getDrawable(R.drawable.ic_continue_burninprotection_black_24px));
            mPauseButtonModule = new ButtonModule(context.getDrawable(R.drawable.ic_pause_black_24dp));
            mStopButtonModule = new ButtonModule(context.getDrawable(R.drawable.ic_stop_black_24dp));
            mLapButtonModule = new ButtonModule(context.getDrawable(R.drawable.ic_lap_black_24dp));
            mChronographClockModule = new ChronographClockModule(mCalendar, scale);

            mModules = new ArrayList<>();
            mModules.add(mTopLeftComplicationModule);
            mModules.add(mTopRightComplicationModule);
            mModules.add(mRightComplicationModule);
            mModules.add(mBottomLeftComplicationModule);
            mModules.add(mBottomRightComplicationModule);
            mModules.add(mLeftComplicationModule);
            mModules.add(mChronographTicksModule);
            mModules.add(mStartButtonModule);
            mModules.add(mContinueButtonModule);
            mModules.add(mPauseButtonModule);
            mModules.add(mStopButtonModule);
            mModules.add(mLapButtonModule);
            mModules.add(mChronographClockModule);

            int color = mPrefs.getInt("settings_chronograph_color_value", Color.parseColor("#00BCD4"));
            int accentColor = mPrefs.getInt("settings_chronograph_accent_color_value",
                    Color.parseColor("#CDDC39"));
            for (Module module : mModules) {
                module.setColor(Color.parseColor("#747474"));
            }
            mRightComplicationModule.setColor(color);
            mLeftComplicationModule.setColor(color);
            mStartButtonModule.setColor(color);
            mContinueButtonModule.setColor(color);
            mPauseButtonModule.setColor(accentColor);
            mStopButtonModule.setColor(Color.WHITE);
            mLapButtonModule.setColor(Color.WHITE);
            mChronographClockModule.setColor(accentColor);
            mChronographClockModule.setAccentColor(color);
            mChronographClockModule.setLapValue(-1);
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
            if (mAmbient && mTimer.isRunning() && !mTimer.isPaused()) {
                mTimer.pause(mCalendar);
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
            int inset = 10;
            if (SETTINGS_MODE == 3) {
                inset += 20;
            }

            Rect bounds = new Rect(inset, inset, mWidth - inset, mHeight - inset);
            Rect screenBounds = new Rect(inset - 5, inset - 5,
                    mWidth - inset + 5, mHeight - inset + 5);

            int offset = (int) (bounds.height() * 0.18f);
            int size = (int) (bounds.width() * 0.20f);
            mTopLeftComplicationModule.setBounds(new Rect(screenBounds.left,
                    screenBounds.top,
                    screenBounds.left + size,
                    screenBounds.top + size));
            mTopRightComplicationModule.setBounds(new Rect(screenBounds.right - size,
                    screenBounds.top,
                    screenBounds.right,
                    screenBounds.top + size));
            mRightComplicationModule.setBounds(new Rect(bounds.right - offset - size - size / 4,
                    bounds.centerY() - size / 2,
                    bounds.right - offset + size / 4,
                    bounds.centerY() + size / 2));
            mBottomLeftComplicationModule.setBounds(new Rect(screenBounds.left,
                    screenBounds.bottom - size,
                    screenBounds.left + size,
                    screenBounds.bottom));
            mBottomRightComplicationModule.setBounds(new Rect(screenBounds.right - size,
                    screenBounds.bottom - size,
                    screenBounds.right,
                    screenBounds.bottom));
            mChronographTicksModule.setBounds(bounds);
            Rect leftDialBounds = new Rect(bounds.left + offset,
                    bounds.centerY() - size / 2,
                    bounds.left + offset + size,
                    bounds.centerY() + size / 2);
            mLeftComplicationModule.setBounds(leftDialBounds);
            mStartButtonModule.setBounds(leftDialBounds);
            mContinueButtonModule.setBounds(leftDialBounds);
            mPauseButtonModule.setBounds(leftDialBounds);
            Rect rightDialBounds = new Rect(bounds.right - offset - size,
                    bounds.centerY() - size / 2,
                    bounds.right - offset,
                    bounds.centerY() + size / 2);
            mLapButtonModule.setBounds(rightDialBounds);
            mStopButtonModule.setBounds(rightDialBounds);
            mChronographClockModule.setBounds(bounds);
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
                    if(!mTimer.isRunning()) {
                        for (Module module : mModules) {
                            if (module instanceof ComplicationModule &&
                                    ((ComplicationModule) module).contains(x, y)) {
                                PendingIntent intent = ((ComplicationModule) module).getTapAction();
                                if (intent != null) {
                                    try {
                                        intent.send();
                                    } catch (PendingIntent.CanceledException e) {
                                    }
                                }
                            }
                        }
                    }
                    if (mStartButtonModule.contains(x, y)) {
                        if (mTimer.isRunning()) {
                            if (mTimer.isPaused()) {
                                mTimer.start(mCalendar);
                            } else {
                                mTimer.pause(mCalendar);
                            }
                        } else {
                            mTimer.reset(mCalendar);
                            int scale = mPrefs.getInt("settings_chronograph_scale", 60);
                            mChronographTicksModule.setScale(scale);
                        }
                    } else if (mTimer.isRunning() && mStopButtonModule.contains(x, y)) {
                        if (mTimer.isPaused()) {
                            mTimer.stop();
                            mChronographTicksModule.setScale(12);
                            mChronographClockModule.setValue(0);
                            mChronographClockModule.setLapValue(-1);
                        } else {
                            mTimer.lap(mCalendar);
                        }
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
                    mChronographTicksModule.setScale(12);
                    mWatchFaceStyleBuilder.setHideStatusBar(false);
                    setWatchFaceStyle(mWatchFaceStyleBuilder.build());
                    SETTINGS_MODE = 0;
                    break;
                case 3:
                    if (mTimer.isRunning()) {
                        mTimer.stop();
                    }
                    setBounds();
                    int scale = mPrefs.getInt("settings_chronograph_scale", 60);
                    int color = mPrefs.getInt("settings_chronograph_color_value",
                            Color.parseColor("#00BCD4"));
                    int accentColor = mPrefs.getInt("settings_chronograph_accent_color_value",
                            Color.parseColor("#CDDC39"));
                    mRightComplicationModule.setColor(color);
                    mStartButtonModule.setColor(color);
                    mContinueButtonModule.setColor(color);
                    mPauseButtonModule.setColor(accentColor);
                    mChronographTicksModule.setScale(scale);
                    mChronographClockModule.setScale(scale);
                    mChronographClockModule.setValue(0);
                    mChronographClockModule.setLapValue(-1);
                    mChronographClockModule.setColor(accentColor);
                    mChronographClockModule.setAccentColor(color);
                    mWatchFaceStyleBuilder.setHideStatusBar(true);
                    setWatchFaceStyle(mWatchFaceStyleBuilder.build());
                    SETTINGS_MODE = 2;
                    break;
            }

            if (SETTINGS_MODE > 1) {
                mCalendar.set(Calendar.HOUR, 10);
                mCalendar.set(Calendar.MINUTE, 10);
                mCalendar.set(Calendar.SECOND, 30);
                mCalendar.set(Calendar.MILLISECOND, 0);
            }

            for (Module module : mModules) {
                if (module instanceof ComplicationModule) {
                    ((ComplicationModule) module).setCurrentTimeMillis(now);
                }
            }

            canvas.drawColor(Color.BLACK);
            mChronographTicksModule.draw(canvas);
            if (mTimer.isRunning()) {
                if (mTimer.isPaused()) {
                    mContinueButtonModule.draw(canvas);
                    mStopButtonModule.draw(canvas);
                } else {
                    mTimer.update(mCalendar);
                    mChronographClockModule.setValue(mTimer.getTime());
                    mChronographClockModule.setLapValue(mTimer.getLapTime());
                    mPauseButtonModule.draw(canvas);
                    mLapButtonModule.draw(canvas);
                }
            } else {
                if (mAmbient || SETTINGS_MODE > 1){
                    mLeftComplicationModule.draw(canvas);
                }else {
                    mStartButtonModule.draw(canvas);
                }
                mRightComplicationModule.draw(canvas);
            }
            if (!mIsRound) {
                mTopLeftComplicationModule.draw(canvas);
                mTopRightComplicationModule.draw(canvas);
                mBottomLeftComplicationModule.draw(canvas);
                mBottomRightComplicationModule.draw(canvas);
            }
            mChronographClockModule.draw(canvas);
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
                if (mTimer.isRunning() && !mTimer.isPaused()) {
                    mTimer.pause(mCalendar);
                }
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
