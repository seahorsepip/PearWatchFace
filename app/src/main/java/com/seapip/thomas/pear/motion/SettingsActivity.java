package com.seapip.thomas.pear.motion;

import android.content.ComponentName;
import android.content.SharedPreferences;
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
                int inset = (WatchFaceService.ROUND ? (width - (int) Math.sqrt(width * width / 2)) / 2 : WatchFaceService.MODULE_SPACING) + 20;
                int screenInset = 20;
                Rect screenBounds = new Rect(screenInset, screenInset, width - screenInset, height - screenInset);
                Rect bounds = new Rect(inset, inset, width - inset, height - inset);

                ArrayList<SettingsOverlay> backgroundModules = new ArrayList<>();
                int scene = preferences.getInt("settings_motion_scene", 0);
                String sceneTitle;
                switch (scene) {
                    default:
                    case 0:
                        sceneTitle = "Jellyfish";
                        break;
                    case 1:
                        sceneTitle = "Flowers";
                        break;
                    case 2:
                        sceneTitle = "Cities";
                        break;
                }
                final SettingsOverlay backgroundOverlay = new SettingsOverlay(screenBounds,
                        screenBounds,
                        sceneTitle,
                        Paint.Align.CENTER);
                Runnable sceneRunnable = new Runnable() {
                    @Override
                    public void run() {
                        int scene = preferences.getInt("settings_motion_scene", 0);
                        scene++;
                        scene = scene > 2 ? 0 : scene;
                        preferences.edit().putInt("settings_motion_scene", scene).apply();
                        switch (scene) {
                            case 0:
                                backgroundOverlay.setTitle("Jellyfish");
                                break;
                            case 1:
                                backgroundOverlay.setTitle("Flowers");
                                break;
                            case 2:
                                backgroundOverlay.setTitle("Cities");
                                break;
                        }
                        setSettingsMode(true);
                    }
                };
                backgroundOverlay.setRunnable(sceneRunnable);
                backgroundOverlay.setRound(WatchFaceService.ROUND);
                backgroundOverlay.setInsetTitle(true);
                backgroundOverlay.setActive(true);
                backgroundModules.add(backgroundOverlay);

                ArrayList<SettingsOverlay> dateModules = new ArrayList<>();
                int date = preferences.getInt("settings_motion_date", 0);
                String dateTitle;
                switch (date) {
                    default:
                    case 0:
                        dateTitle = "Off";
                        break;
                    case 1:
                        dateTitle = "Day of week";
                        break;
                    case 2:
                        dateTitle = "Day of month";
                        break;
                    case 3:
                        dateTitle = "Day";
                        break;
                }
                final SettingsOverlay dateOverlay = new SettingsOverlay(new Rect(
                        bounds.left + WatchFaceService.MODULE_SPACING * 2,
                        bounds.top + bounds.height() / 3 - WatchFaceService.MODULE_SPACING / 2 * 3,
                        bounds.right,
                        bounds.bottom - (bounds.height() - WatchFaceService.MODULE_SPACING * 2) / 3 - 3 * WatchFaceService.MODULE_SPACING),
                        bounds,
                        dateTitle,
                        Paint.Align.RIGHT);
                dateOverlay.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        int date = preferences.getInt("settings_motion_date", 0);
                        date++;
                        date = date > 3 ? 0 : date;
                        preferences.edit().putInt("settings_motion_date", date).apply();
                        switch (date) {
                            default:
                            case 0:
                                dateOverlay.setTitle("Off");
                                break;
                            case 1:
                                dateOverlay.setTitle("Day of week");
                                break;
                            case 2:
                                dateOverlay.setTitle("Day of month");
                                break;
                            case 3:
                                dateOverlay.setTitle("Day");
                                break;
                        }
                        setSettingsMode(true);
                    }
                });
                dateOverlay.setActive(true);
                dateModules.add(dateOverlay);

                int spacing = WatchFaceService.MODULE_SPACING - 2;
                mComplicationModules = new ArrayList<>();
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
                        WatchFaceService.COMPLICATION_IDS[0],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[0]);
                SettingsOverlay complicationBottomCenterOverlay = new SettingsOverlay(
                        new Rect(bounds.left + (bounds.width() - spacing * 2) / 3 + spacing,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3,
                                bounds.right - (bounds.width() - spacing * 2) / 3 - spacing,
                                bounds.bottom),
                        bounds,
                        "OFF",
                        Paint.Align.CENTER);
                setComplicationOverlay(complicationBottomCenterOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[1],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[1]);
                SettingsOverlay complicationBottomRightOverlay = new SettingsOverlay(
                        new Rect(bounds.right - (bounds.width() - spacing * 2) / 3,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3,
                                bounds.right,
                                bounds.bottom),
                        bounds,
                        "OFF",
                        Paint.Align.RIGHT);
                setComplicationOverlay(complicationBottomRightOverlay,
                        WatchFaceService.class,
                        WatchFaceService.COMPLICATION_IDS[2],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[2]);
                mComplicationModules.add(complicationBottomLeftOverlay);
                mComplicationModules.add(complicationBottomCenterOverlay);
                mComplicationModules.add(complicationBottomRightOverlay);
                complicationBottomLeftOverlay.setActive(true);

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
                row.addPages(new SettingsPage(backgroundModules));
                row.addPages(new SettingsPage(dateModules));
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