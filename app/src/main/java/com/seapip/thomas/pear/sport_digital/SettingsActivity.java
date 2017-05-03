package com.seapip.thomas.pear.sport_digital;

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
import com.seapip.thomas.pear.settings.SettingsFinish;
import com.seapip.thomas.pear.settings.SettingsOverlay;
import com.seapip.thomas.pear.settings.SettingsPage;
import com.seapip.thomas.pear.settings.SettingsRow;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import static com.seapip.thomas.pear.sport_digital.WatchFaceService.MODULE_SPACING;

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
                int spacing = MODULE_SPACING - 2;
                int inset = (WatchFaceService.ROUND ?
                        (width - (int) Math.sqrt(width * width / 2)) / 2 :
                        MODULE_SPACING) + 20;
                Rect bounds = new Rect(inset, inset, width - inset, height - inset);

                ArrayList<SettingsOverlay> styleModules = new ArrayList<>();
                SettingsOverlay styleOverlay = new SettingsOverlay(
                        new Rect(
                                bounds.right - (bounds.width() - MODULE_SPACING * 2) / 3 * 2 - MODULE_SPACING,
                                bounds.top,
                                bounds.right,
                                bounds.bottom),
                        bounds, "Style",
                        Paint.Align.RIGHT);
                styleOverlay.setActive(true);
                styleOverlay.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        int style = preferences.getInt("settings_sport_digital_style", 0);
                        style++;
                        style = style > 2 ? 0 : style;
                        preferences.edit().putInt("settings_sport_digital_style", style).apply();
                        setSettingsMode(true);
                    }
                });
                styleModules.add(styleOverlay);

                int colorStyle = preferences.getInt("settings_sport_digital_color_style", 0);
                String colorName = preferences.getString("settings_sport_digital_color_name", "Cyan");
                ArrayList<SettingsOverlay> colorStyleModules = new ArrayList<>();
                final SettingsOverlay colorStyleOverlay = new SettingsOverlay(bounds,
                        bounds,
                        colorStyle == 0 ? colorName : "White/" + colorName,
                        Paint.Align.LEFT);
                colorStyleOverlay.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        int colorStyle = preferences.getInt("settings_sport_digital_color_style", 0);
                        String colorName = preferences.getString("settings_sport_digital_color_name", "Cyan");
                        colorStyle = colorStyle == 0 ? 1 : 0;
                        preferences.edit().putInt("settings_sport_digital_color_style", colorStyle).apply();
                        colorStyleOverlay.setTitle(colorStyle == 0 ? colorName : "White/" + colorName);
                        setSettingsMode(true);
                    }
                });
                colorStyleOverlay.setActive(true);
                colorStyleModules.add(colorStyleOverlay);

                ArrayList<SettingsOverlay> colorModules = new ArrayList<>();
                SettingsOverlay colorOverlay = new SettingsOverlay(bounds, bounds, "",
                        Paint.Align.LEFT);
                colorOverlay.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        int colorStyle = preferences.getInt("settings_sport_digital_color_style", 0);
                        String colorName = preferences.getString("settings_sport_digital_color_name", "Lime");
                        colorStyleOverlay.setTitle(colorStyle == 0 ? colorName : "White/" + colorName);
                    }
                });
                setColorOverlay(colorOverlay,
                        "settings_sport_digital_color_name",
                        "settings_sport_digital_color_value",
                        "Lime",
                        Color.parseColor("#CDDC39"));
                colorOverlay.setActive(true);
                colorModules.add(colorOverlay);

                mComplicationModules = new ArrayList<>();
                SettingsOverlay complicationTopLefOverlay = new SettingsOverlay(
                        new Rect(bounds.left,
                                bounds.top,
                                bounds.left + (bounds.width() - spacing * 2) / 3,
                                bounds.top + (bounds.height() - spacing * 2) / 3),
                        bounds,
                        "OFF", Paint.Align.LEFT);
                setComplicationOverlay(complicationTopLefOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[0],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[0]);
                SettingsOverlay complicationCenterLeftOverlay = new SettingsOverlay(
                        new Rect(bounds.left,
                                bounds.top + (bounds.height() - spacing * 2) / 3 + spacing,
                                bounds.left + (bounds.width() - spacing * 2) / 3,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3 - spacing),
                        bounds,
                        "OFF",
                        Paint.Align.LEFT);
                setComplicationOverlay(complicationCenterLeftOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[1],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[1]);
                SettingsOverlay complicationBottomLeftOverlay = new SettingsOverlay(
                        new Rect(bounds.left,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3,
                                bounds.left + (bounds.width() - spacing * 2) / 3,
                                bounds.bottom),
                        bounds,
                        "OFF",
                        Paint.Align.LEFT);
                setComplicationOverlay(complicationBottomLeftOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[2],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[2]);
                mComplicationModules.add(complicationTopLefOverlay);
                mComplicationModules.add(complicationCenterLeftOverlay);
                mComplicationModules.add(complicationBottomLeftOverlay);
                complicationTopLefOverlay.setActive(true);

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
                row.addPages(new SettingsPage(styleModules));
                row.addPages(new SettingsPage(colorStyleModules));
                row.addPages(new SettingsPage(colorModules));
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