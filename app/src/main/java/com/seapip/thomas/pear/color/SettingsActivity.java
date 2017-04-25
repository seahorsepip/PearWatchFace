package com.seapip.thomas.pear.color;

import android.content.ComponentName;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderInfoRetriever;
import android.support.wearable.view.GridViewPager;
import android.util.DisplayMetrics;

import com.seapip.thomas.pear.R;
import com.seapip.thomas.pear.settings.SettingsAdapter;
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
        final SharedPreferences.Editor editor = preferences.edit();
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

                ArrayList<SettingsOverlay> colorModules = new ArrayList<>();
                SettingsOverlay colorOverlay = new SettingsOverlay(bounds, bounds,
                        "Color", Paint.Align.CENTER);
                setColorOverlay(colorOverlay,
                        "settings_color_color_name",
                        "settings_color_color_value",
                        "Cyan",
                        Color.parseColor("#00BCD4"));
                colorOverlay.setRound(true);
                colorOverlay.setInsetTitle(true);
                colorOverlay.setActive(true);
                colorModules.add(colorOverlay);

                ArrayList<SettingsOverlay> accentColorModules = new ArrayList<>();
                SettingsOverlay accentColorOverlay = new SettingsOverlay(
                        new Rect(bounds.centerX() - (int) (bounds.width() * 0.08f),
                                bounds.bottom - (int) (bounds.height() * 0.60f),
                                bounds.centerX() + (int) (bounds.width() * 0.08f),
                                bounds.bottom),
                        bounds, "Color", Paint.Align.CENTER);
                setColorOverlay(accentColorOverlay,
                        "settings_color_accent_color_name",
                        "settings_color_accent_color_value",
                        "Lime",
                        Color.parseColor("#CDDC39"));
                accentColorOverlay.setRound(true);
                accentColorOverlay.setActive(true);
                accentColorModules.add(accentColorOverlay);

                mComplicationModules = new ArrayList<>();
                int offset = (int) (insetBounds.height() * 0.18f);
                int size = (int) (insetBounds.width() * 0.20f);
                SettingsOverlay topComplicationOverlay = new SettingsOverlay(
                        new Rect(insetBounds.centerX() - size / 2,
                                insetBounds.top + offset,
                                insetBounds.centerX() + size / 2,
                                insetBounds.top + offset + size),
                        bounds,
                        "Off",
                        Paint.Align.CENTER);
                setComplicationOverlay(topComplicationOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[0],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[0]);
                topComplicationOverlay.setActive(true);
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
                        WatchFaceService.COMPLICATION_IDS[1],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[1]);
                SettingsOverlay rightComplicationOverlay = new SettingsOverlay(
                        new Rect(insetBounds.right - offset - size,
                                insetBounds.centerY() - size / 2,
                                insetBounds.right - offset,
                                insetBounds.centerY() + size / 2),
                        bounds,
                        "Off",
                        Paint.Align.RIGHT);
                setComplicationOverlay(rightComplicationOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[2],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[2]);
                SettingsOverlay bottomComplicationOverlay = new SettingsOverlay(
                        new Rect(insetBounds.centerX() - size / 2,
                                insetBounds.bottom - offset - size,
                                insetBounds.centerX() + size / 2,
                                insetBounds.bottom - offset),
                        bounds,
                        "Off",
                        Paint.Align.CENTER);
                setComplicationOverlay(bottomComplicationOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[3],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[3]);
                mComplicationModules.add(topComplicationOverlay);
                mComplicationModules.add(leftComplicationOverlay);
                mComplicationModules.add(rightComplicationOverlay);
                mComplicationModules.add(bottomComplicationOverlay);

                SettingsRow row = new SettingsRow();
                row.addPages(new SettingsPage(colorModules));
                row.addPages(new SettingsPage(accentColorModules));
                row.addPages(new SettingsPage(mComplicationModules));
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