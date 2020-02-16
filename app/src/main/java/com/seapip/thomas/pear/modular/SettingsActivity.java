package com.seapip.thomas.pear.modular;

import android.content.ComponentName;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;

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

        adapter = new SettingsAdapter(getFragmentManager()) {
            @Override
            public ArrayList<SettingsRow> initPages() {
                ArrayList<SettingsRow> pages = new ArrayList<>();

                DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                int spacing = WatchFaceService.MODULE_SPACING - 2;
                int inset = (WatchFaceService.ROUND ?
                        (width - (int) Math.sqrt(width * width / 2)) / 2 :
                        WatchFaceService.MODULE_SPACING) + 20;
                Rect bounds = new Rect(inset, inset, width - inset, height - inset);

                ArrayList<SettingsOverlay> colorModules = new ArrayList<>();
                final SettingsOverlay colorOverlay = new SettingsOverlay(bounds, bounds, "",
                        Paint.Align.LEFT);
                setColorOverlay(colorOverlay,
                        "settings_modular_color_name",
                        "settings_modular_color_value",
                        "Cyan",
                        Color.parseColor("#00BCD4"));
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
                SettingsOverlay complicationCenterOverlay = new SettingsOverlay(
                        new Rect(bounds.left,
                                bounds.top + (bounds.height() - spacing * 2) / 3 + spacing,
                                bounds.right,
                                bounds.bottom - (bounds.height() - spacing * 2) / 3 - spacing),
                        bounds,
                        "OFF",
                        Paint.Align.CENTER);
                setComplicationOverlay(complicationCenterOverlay,
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
                        WatchFaceService.COMPLICATION_IDS[3],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[3]);
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
                        WatchFaceService.COMPLICATION_IDS[4],
                        WatchFaceService.COMPLICATION_SUPPORTED_TYPES[4]);
                mComplicationModules.add(complicationTopLefOverlay);
                mComplicationModules.add(complicationCenterOverlay);
                mComplicationModules.add(complicationBottomLeftOverlay);
                mComplicationModules.add(complicationBottomCenterOverlay);
                mComplicationModules.add(complicationBottomRightOverlay);
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