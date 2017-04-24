package com.seapip.thomas.pear.modular;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderInfoRetriever;
import android.support.wearable.view.GridViewPager;
import android.util.DisplayMetrics;

import com.seapip.thomas.pear.ColorActivity;
import com.seapip.thomas.pear.R;
import com.seapip.thomas.pear.settings.SettingsAdapter;
import com.seapip.thomas.pear.settings.SettingsFragment;
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
                int spacing = WatchFaceService.MODULE_SPACING - 2;
                int inset = (WatchFaceService.ROUND ? (width - (int) Math.sqrt(width * width / 2)) / 2 : WatchFaceService.MODULE_SPACING) + 20;
                Rect bounds = new Rect(inset, inset, width - inset, height - inset);


                Intent colorIntent = new Intent(getApplicationContext(), ColorActivity.class);
                colorIntent.putExtra("color", preferences.getInt("settings_color_value",
                        Color.parseColor("#18FFFF")));
                colorIntent.putExtra("color_names_id", R.array.color_names);
                colorIntent.putExtra("color_values_id", R.array.color_values);
                ArrayList<SettingsOverlay> colorModules = new ArrayList<>();
                SettingsOverlay colorModuleOverlay = new SettingsOverlay(bounds,
                        bounds,
                        preferences.getString("settings_modular_color_name", "Cyan"),
                        Paint.Align.LEFT, colorIntent, SettingsFragment.COLOR_REQUEST);
                colorModules.add(colorModuleOverlay);
                colorModuleOverlay.setActive(true);

                mComplicationModules = new ArrayList<>();
                SettingsOverlay complicationTopLeftModuleOverlay = new SettingsOverlay(
                        new Rect(bounds.left,
                                bounds.top,
                                bounds.left + (bounds.width() - spacing * 2) / 3,
                                bounds.top + (bounds.height() - spacing * 2) / 3),
                        bounds,
                        "OFF", Paint.Align.LEFT,
                        ComplicationHelperActivity.createProviderChooserHelperIntent(
                                getApplicationContext(),
                                new ComponentName(getApplicationContext().getApplicationContext(),
                                        WatchFaceService.class),
                                WatchFaceService.COMPLICATION_IDS[0],
                                WatchFaceService.COMPLICATION_SUPPORTED_TYPES[0]), 0
                );
                SettingsOverlay complicationCenterModuleOverlay = new SettingsOverlay(
                        new Rect(bounds.left,
                                bounds.top + (bounds.height() - spacing * 2) / 3 + spacing,
                                bounds.right,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3 - spacing),
                        bounds,
                        "OFF",
                        Paint.Align.CENTER,
                        ComplicationHelperActivity.createProviderChooserHelperIntent(
                                getApplicationContext(),
                                new ComponentName(getApplicationContext().getApplicationContext(),
                                        WatchFaceService.class),
                                WatchFaceService.COMPLICATION_IDS[1],
                                WatchFaceService.COMPLICATION_SUPPORTED_TYPES[1]), 1
                );
                SettingsOverlay complicationBottomLeftModuleOverlay = new SettingsOverlay(
                        new Rect(bounds.left,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3,
                                bounds.left + (bounds.width() - spacing * 2) / 3,
                                bounds.bottom),
                        bounds,
                        "OFF",
                        Paint.Align.LEFT,
                        ComplicationHelperActivity.createProviderChooserHelperIntent(
                                getApplicationContext(),
                                new ComponentName(getApplicationContext().getApplicationContext(),
                                        WatchFaceService.class),
                                WatchFaceService.COMPLICATION_IDS[2],
                                WatchFaceService.COMPLICATION_SUPPORTED_TYPES[2]), 2
                );
                SettingsOverlay complicationBottomCenterModuleOverlay = new SettingsOverlay(
                        new Rect(bounds.left + (bounds.width() - spacing * 2) / 3 + spacing,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3,
                                bounds.right - (bounds.width() - spacing * 2) / 3 - spacing,
                                bounds.bottom),
                        bounds,
                        "OFF",
                        Paint.Align.CENTER,
                        ComplicationHelperActivity.createProviderChooserHelperIntent(
                                getApplicationContext(),
                                new ComponentName(getApplicationContext().getApplicationContext(),
                                        WatchFaceService.class),
                                WatchFaceService.COMPLICATION_IDS[3],
                                WatchFaceService.COMPLICATION_SUPPORTED_TYPES[3]), 3
                );
                SettingsOverlay complicationBottomRightModuleOverlay = new SettingsOverlay(
                        new Rect(bounds.right - (bounds.width() - spacing * 2) / 3,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3,
                                bounds.right,
                                bounds.bottom),
                        bounds,
                        "OFF",
                        Paint.Align.RIGHT,
                        ComplicationHelperActivity.createProviderChooserHelperIntent(
                                getApplicationContext(),
                                new ComponentName(getApplicationContext().getApplicationContext(),
                                        WatchFaceService.class),
                                WatchFaceService.COMPLICATION_IDS[4],
                                WatchFaceService.COMPLICATION_SUPPORTED_TYPES[4]), 4
                );
                mComplicationModules.add(complicationTopLeftModuleOverlay);
                mComplicationModules.add(complicationCenterModuleOverlay);
                mComplicationModules.add(complicationBottomLeftModuleOverlay);
                mComplicationModules.add(complicationBottomCenterModuleOverlay);
                mComplicationModules.add(complicationBottomRightModuleOverlay);
                complicationTopLeftModuleOverlay.setActive(true);

                SettingsRow row = new SettingsRow();
                row.addPages(new SettingsPage(colorModules));
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