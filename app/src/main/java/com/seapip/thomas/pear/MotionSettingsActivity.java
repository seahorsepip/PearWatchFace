package com.seapip.thomas.pear;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.view.GridViewPager;
import android.util.DisplayMetrics;

import java.util.ArrayList;

public class MotionSettingsActivity extends SettingsActivity {
    private SettingsAdapter adapter;

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
                int inset = 10;
                Rect bounds = new Rect(inset, inset, width - inset, height - inset);

                ArrayList<SettingsOverlay> modules = new ArrayList<>();
                final SettingsOverlay backgroundOverlay = new SettingsOverlay(bounds,
                        preferences.getString("settings_color_name", "Cyan"),
                        Paint.Align.CENTER);
                Runnable sceneRunnable = new Runnable() {
                    @Override
                    public void run() {
                        int scene = preferences.getInt("settings_scene", 0);
                        scene++;
                        scene = scene > 1 ? 0 : scene;
                        preferences.edit().putInt("settings_scene", scene).apply();
                        switch (scene) {
                            case 0:
                                backgroundOverlay.setTitle("Jellyfish");
                                break;
                            case 1:
                                backgroundOverlay.setTitle("Flowers");
                                break;
                        }
                        MotionWatchFaceService.SETTINGS_MODE = 3;
                    }
                };
                backgroundOverlay.setRunnable(sceneRunnable);
                backgroundOverlay.setRound(true);
                modules.add(backgroundOverlay);
                backgroundOverlay.setActive(true);

                SettingsRow row = new SettingsRow();
                row.addPages(new SettingsPage(modules));
                pages.add(row);

                return pages;
            }
        };
        ((GridViewPager) findViewById(R.id.pager)).setAdapter(adapter);
        MotionWatchFaceService.SETTINGS_MODE = 3;
    }

    @Override
    public void setSettingsMode(boolean mode) {
        MotionWatchFaceService.SETTINGS_MODE = mode ? 3 : 1;
    }

    @Override
    public SettingsAdapter getAdapter() {
        return adapter;
    }
}