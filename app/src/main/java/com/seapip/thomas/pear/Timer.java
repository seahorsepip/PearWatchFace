package com.seapip.thomas.pear;

import android.content.Context;
import android.os.PowerManager;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.POWER_SERVICE;

public class Timer {
    private PowerManager.WakeLock mWakeLock;
    private Calendar mCalendar;
    private long mTime;
    private long mLapTime;
    private Calendar mLapCalendar;
    private boolean mIsPaused;
    private Calendar mPausedCalendar;
    private boolean mIsRunning;

    public Timer(Context context) {
        mWakeLock = ((PowerManager) context.getSystemService(POWER_SERVICE))
                .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "PearStopwatchModuleWakeLock");
    }

    public void reset(Calendar calendar) {
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        mCalendar = (Calendar) calendar.clone();
        mLapCalendar = null;
        mTime = 0;
        mLapTime = -1;
        mIsRunning = true;
        mIsPaused = false;
    }

    public void start(Calendar calendar) {
        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
        }
        Date start = mPausedCalendar.getTime();
        Date now = calendar.getTime();
        int diff = (int) (now.getTime() - start.getTime());
        mCalendar.add(Calendar.MILLISECOND, diff);
        if(mLapCalendar != null) {
            mLapCalendar.add(Calendar.MILLISECOND, diff);
        }
        mIsPaused = false;
    }

    public void pause(Calendar calendar) {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mPausedCalendar = (Calendar) calendar.clone();
        mIsPaused = true;
    }

    public void update(Calendar calendar) {
        Date now = calendar.getTime();
        Date start = mCalendar.getTime();
        mTime = now.getTime() - start.getTime();
        if(mLapCalendar != null) {
            Date lap = mLapCalendar.getTime();
            mLapTime = now.getTime() - lap.getTime();
        }
    }

    public void stop() {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        mIsRunning = false;
    }

    public void lap(Calendar calendar) {
        mLapCalendar = (Calendar) calendar.clone();
        mLapTime = 0;
    }

    public long getTime() {
        return mTime;
    }

    public long getLapTime() {
        return mLapTime;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public boolean isPaused() {
        return mIsPaused;
    }
}
