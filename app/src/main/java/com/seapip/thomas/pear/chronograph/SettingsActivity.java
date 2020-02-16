package com.seapip.thomas.pear.chronograph;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderInfoRetriever;
import android.support.wearable.view.GridViewPager;
import android.util.DisplayMetrics;

import com.seapip.thomas.pear.R;
import com.seapip.thomas.pear.settings.SettingsAdapter;
import com.seapip.thomas.pear.settings.SettingsFinish;
import com.seapip.thomas.pear.settings.SettingsOverlay;
import com.seapip.thomas.pear.settings.SettingsPage;
import com.seapip.thomas.pear.settings.SettingsRow;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class SettingsActivity extends com.seapip.thomas.pear.settings.SettingsActivity {
    private SettingsAdapter adapter;
    private ArrayList<SettingsOverlay> mComplicationModules;
    private ProviderInfoRetriever mProviderInfoRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext().getApplicationContext());
        adapter = new SettingsAdapter(getFragmentManager()) {
            @Override
            public ArrayList<SettingsRow> initPages() {
                ArrayList<SettingsRow> pages = new ArrayList<>();

                DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                int inset = 20;
                Rect bounds = new Rect(inset, inset, width - inset, height - inset);
                Rect insetBounds = new Rect(30, 30, width - 30, height - 30);
                Rect screenBounds = new Rect(25, 25, width - 25, height - 25);

                ArrayList<SettingsOverlay> timescaleModules = new ArrayList<>();
                SettingsOverlay timescaleOverlay = new SettingsOverlay(bounds, bounds,
                        "Timescale", Paint.Align.CENTER);
                timescaleOverlay.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        int scale = preferences.getInt("settings_chronograph_scale", 60);
                        switch (scale) {
                            case 3:
                            case 30:
                                scale *= 2;
                                break;
                            case 6:
                                scale *= 5;
                                break;
                            case 60:
                                scale /= 20;
                                break;
                        }
                        preferences.edit().putInt("settings_chronograph_scale", scale).apply();
                        setSettingsMode(true);
                    }
                });
                timescaleOverlay.setRound(true);
                timescaleOverlay.setInsetTitle(true);
                timescaleOverlay.setActive(true);
                timescaleModules.add(timescaleOverlay);

                int offset = (int) (insetBounds.height() * 0.18f);
                int size = (int) (insetBounds.width() * 0.20f);
                ArrayList<SettingsOverlay> colorModules = new ArrayList<>();
                SettingsOverlay colorOverlay = new SettingsOverlay(new Rect(insetBounds.left + offset,
                        insetBounds.centerY() - size / 2,
                        insetBounds.right - offset + size / 3,
                        insetBounds.centerY() + size / 2), bounds,
                        "Color", Paint.Align.CENTER);
                setColorOverlay(colorOverlay,
                        "settings_chronograph_color_name",
                        "settings_chronograph_color_value",
                        "Cyan",
                        Color.parseColor("#00BCD4"));
                colorOverlay.setRound(WatchFaceService.ROUND);
                colorOverlay.setActive(true);
                colorModules.add(colorOverlay);

                ArrayList<SettingsOverlay> accentColorModules = new ArrayList<>();
                SettingsOverlay accentColorOverlay = new SettingsOverlay(
                        new Rect(bounds.centerX() - (int) (bounds.width() * 0.08f),
                                bounds.top,
                                bounds.centerX() + (int) (bounds.width() * 0.08f),
                                bounds.top + (int) (bounds.height() * 0.60f)),
                        bounds, "Color", Paint.Align.CENTER);
                setColorOverlay(accentColorOverlay,
                        "settings_chronograph_accent_color_name",
                        "settings_chronograph_accent_color_value",
                        "Lime",
                        Color.parseColor("#CDDC39"));
                accentColorOverlay.setRound(true);
                accentColorOverlay.setBottomTitle(true);
                accentColorOverlay.setActive(true);
                accentColorModules.add(accentColorOverlay);

                mComplicationModules = new ArrayList<>();
                SettingsOverlay topLeftComplicationOverlay = new SettingsOverlay(
                        new Rect(screenBounds.left,
                                screenBounds.top,
                                screenBounds.left + size,
                                screenBounds.top + size),
                        screenBounds,
                        "Off",
                        Paint.Align.LEFT);
                setComplicationOverlay(topLeftComplicationOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[0],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[0]);
                topLeftComplicationOverlay.setBottomTitle(true);
                SettingsOverlay topRightComplicationOverlay = new SettingsOverlay(
                        new Rect(screenBounds.right - size,
                                screenBounds.top,
                                screenBounds.right,
                                screenBounds.top + size),
                        screenBounds,
                        "Off",
                        Paint.Align.RIGHT);
                setComplicationOverlay(topRightComplicationOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[1],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[1]);
                topRightComplicationOverlay.setBottomTitle(true);
                // left
                SettingsOverlay leftComplicationOverlay = new SettingsOverlay(
                        new Rect(insetBounds.left + offset,
                                insetBounds.centerY() - size / 2,
                                insetBounds.left + offset + size,
                                insetBounds.centerY() + size / 2),
                        bounds,
                        "Off",
                        Paint.Align.LEFT);
                setComplicationOverlay(leftComplicationOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[5],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[5]);
                // right
                SettingsOverlay rightComplicationOverlay = new SettingsOverlay(
                        new Rect(insetBounds.right - offset - size - size / 3,
                                insetBounds.centerY() - size / 2,
                                insetBounds.right - offset + size / 3,
                                insetBounds.centerY() + size / 2),
                        bounds,
                        "Off",
                        Paint.Align.RIGHT);
                setComplicationOverlay(rightComplicationOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[2],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[2]);
                if(WatchFaceService.ROUND) {
                    //rightComplicationOverlay.setActive(true);
                    leftComplicationOverlay.setActive(true);
                } else {
                    topLeftComplicationOverlay.setActive(true);
                }
                SettingsOverlay bottomLeftComplicationOverlay = new SettingsOverlay(
                        new Rect(screenBounds.left,
                                screenBounds.bottom - size,
                                screenBounds.left + size,
                                screenBounds.bottom),
                        screenBounds,
                        "Off",
                        Paint.Align.LEFT);
                setComplicationOverlay(bottomLeftComplicationOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[3],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[3]);
                SettingsOverlay bottomRightComplicationOverlay = new SettingsOverlay(
                        new Rect(screenBounds.right - size,
                                screenBounds.bottom - size,
                                screenBounds.right,
                                screenBounds.bottom),
                        screenBounds,
                        "Off",
                        Paint.Align.RIGHT);
                setComplicationOverlay(bottomRightComplicationOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[4],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[4]);
                if(WatchFaceService.ROUND) {
                    topLeftComplicationOverlay.setDisabled(true);
                    topRightComplicationOverlay.setDisabled(true);
                    bottomLeftComplicationOverlay.setDisabled(true);
                    bottomRightComplicationOverlay.setDisabled(true);
                }
                mComplicationModules.add(topLeftComplicationOverlay);
                mComplicationModules.add(topRightComplicationOverlay);
                mComplicationModules.add(rightComplicationOverlay);
                mComplicationModules.add(bottomLeftComplicationOverlay);
                mComplicationModules.add(bottomRightComplicationOverlay);
                mComplicationModules.add(leftComplicationOverlay);

                ArrayList<SettingsOverlay> finishModules = new ArrayList<>();
                SettingsFinish finishOverlay = new SettingsFinish(getApplicationContext(),
                        new Rect(0, 0, width, height));
                finishOverlay.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });

                finishModules.add(finishOverlay);

                SettingsRow row = new SettingsRow();
                row.addPages(new SettingsPage(timescaleModules));
                row.addPages(new SettingsPage(colorModules));
                row.addPages(new SettingsPage(accentColorModules));
                row.addPages(new SettingsPage(mComplicationModules));
                row.addPages(new SettingsPage(finishModules));
                pages.add(row);

                return pages;
            }
        };
        ((GridViewPager) findViewById(R.id.pager)).setAdapter(adapter);

        mProviderInfoRetriever = new ProviderInfoRetriever(getApplicationContext(), new Executor() {
            @Override
            public void execute(@NonNull Runnable r) {
                new Thread(r).start();
            }
        });
        mProviderInfoRetriever.init();
        mProviderInfoRetriever.retrieveProviderInfo(
                new ProviderInfoRetriever.OnProviderInfoReceivedCallback() {
                    @Override
                    public void onProviderInfoReceived(int i, @Nullable ComplicationProviderInfo complicationProviderInfo) {
                        String title = "OFF";
                        if (complicationProviderInfo != null) {
                            title = complicationProviderInfo.providerName;
                        }
                        mComplicationModules.get(i).setTitle(title);
                    }

                },
                new ComponentName(getApplicationContext(), WatchFaceService.class),
                WatchFaceService.COMPLICATION_IDS
        );
        setSettingsMode(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProviderInfoRetriever.release();
    }

    @Override
    public void setSettingsMode(boolean mode) {
        WatchFaceService.SETTINGS_MODE = mode ? 3 : 1;
    }

    @Override
    public SettingsAdapter getAdapter() {
        return adapter;
    }
}