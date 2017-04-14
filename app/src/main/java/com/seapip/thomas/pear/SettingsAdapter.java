package com.seapip.thomas.pear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderInfoRetriever;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class SettingsAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;
    private ArrayList<SettingsRow> mPages;
    private ArrayList<SettingOverlay> complicationModules;

    public SettingsAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
        initPages();
        ProviderInfoRetriever providerInfoRetriever = new ProviderInfoRetriever(mContext, new Executor() {
            @Override
            public void execute(@NonNull Runnable r) {
                new Thread(r).start();
            }
        });
        providerInfoRetriever.init();
        providerInfoRetriever.retrieveProviderInfo(
                new ProviderInfoRetriever.OnProviderInfoReceivedCallback() {
                    @Override
                    public void onProviderInfoReceived(int i, @Nullable ComplicationProviderInfo complicationProviderInfo) {
                        String title = "OFF";
                        if(complicationProviderInfo != null) {
                            title = complicationProviderInfo.providerName;
                        }
                        complicationModules.get(i).setTitle(title);
                    }

                },
                new ComponentName(mContext, ModularWatchFaceService.class),
                ModularWatchFaceService.COMPLICATION_IDS
        );
    }

    private void initPages() {
        mPages = new ArrayList<>();

        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int spacing = ModularWatchFaceService.MODULE_SPACING - 2;
        int inset = (width - (int) Math.sqrt(width * width / 2)) / 2 + 20;
        Rect bounds = new Rect(inset, inset, width - inset, height - inset);

        ArrayList<SettingOverlay> colorModules = new ArrayList<>();
        SettingOverlay colorModuleOverlay = new SettingOverlay(bounds, "Cyan",
                Paint.Align.LEFT, null, 12);
        colorModules.add(colorModuleOverlay);
        colorModuleOverlay.setActive(true);

        complicationModules = new ArrayList<>();
        SettingOverlay complicationTopLeftModuleOverlay = new SettingOverlay(
                new Rect(bounds.left,
                        bounds.top,
                        bounds.left + (bounds.width() - spacing * 2) / 3,
                        bounds.top + (bounds.height() - spacing * 2) / 3),
                "OFF", Paint.Align.LEFT,
                ComplicationHelperActivity.createProviderChooserHelperIntent(
                        mContext,
                        new ComponentName(mContext.getApplicationContext(),
                                ModularWatchFaceService.class),
                        ModularWatchFaceService.COMPLICATION_IDS[0],
                        ModularWatchFaceService.COMPLICATION_SUPPORTED_TYPES[0]), 0
        );
        SettingOverlay complicationCenterModuleOverlay = new SettingOverlay(
                new Rect(bounds.left,
                        bounds.top + (bounds.height() - spacing * 2) / 3 + spacing,
                        bounds.right,
                        bounds.bottom - (bounds.height() - spacing * 2) / 3 - spacing), "OFF",
                Paint.Align.CENTER,
                ComplicationHelperActivity.createProviderChooserHelperIntent(
                        mContext,
                        new ComponentName(mContext.getApplicationContext(),
                                ModularWatchFaceService.class),
                        ModularWatchFaceService.COMPLICATION_IDS[1],
                        ModularWatchFaceService.COMPLICATION_SUPPORTED_TYPES[1]), 1
        );
        SettingOverlay complicationBottomLeftModuleOverlay = new SettingOverlay(
                new Rect(bounds.left,
                        bounds.bottom - (bounds.height() - spacing * 2) / 3,
                        bounds.left + (bounds.width() - spacing * 2) / 3,
                        bounds.bottom), "OFF",
                Paint.Align.LEFT,
                ComplicationHelperActivity.createProviderChooserHelperIntent(
                        mContext,
                        new ComponentName(mContext.getApplicationContext(),
                                ModularWatchFaceService.class),
                        ModularWatchFaceService.COMPLICATION_IDS[2],
                        ModularWatchFaceService.COMPLICATION_SUPPORTED_TYPES[2]), 2
        );
        SettingOverlay complicationBottomCenterModuleOverlay = new SettingOverlay(
                new Rect(bounds.left + (bounds.width() - spacing * 2) / 3 + spacing,
                        bounds.bottom - (bounds.height() - spacing * 2) / 3,
                        bounds.right - (bounds.width() - spacing * 2) / 3 - spacing,
                        bounds.bottom), "OFF",
                Paint.Align.CENTER,
                ComplicationHelperActivity.createProviderChooserHelperIntent(
                        mContext,
                        new ComponentName(mContext.getApplicationContext(),
                                ModularWatchFaceService.class),
                        ModularWatchFaceService.COMPLICATION_IDS[3],
                        ModularWatchFaceService.COMPLICATION_SUPPORTED_TYPES[3]), 3
        );
        SettingOverlay complicationBottomRightModuleOverlay = new SettingOverlay(
                new Rect(bounds.right - (bounds.width() - spacing * 2) / 3,
                        bounds.bottom - (bounds.height() - spacing * 2) / 3,
                        bounds.right,
                        bounds.bottom), "OFF",
                Paint.Align.RIGHT,
                ComplicationHelperActivity.createProviderChooserHelperIntent(
                        mContext,
                        new ComponentName(mContext.getApplicationContext(),
                                ModularWatchFaceService.class),
                        ModularWatchFaceService.COMPLICATION_IDS[4],
                        ModularWatchFaceService.COMPLICATION_SUPPORTED_TYPES[4]), 4
        );
        complicationModules.add(complicationTopLeftModuleOverlay);
        complicationModules.add(complicationCenterModuleOverlay);
        complicationModules.add(complicationBottomLeftModuleOverlay);
        complicationModules.add(complicationBottomCenterModuleOverlay);
        complicationModules.add(complicationBottomRightModuleOverlay);
        complicationTopLeftModuleOverlay.setActive(true);

        SettingsRow row = new SettingsRow();
        row.addPages(new SettingsPage(colorModules));
        row.addPages(new SettingsPage(complicationModules));
        mPages.add(row);
    }

    @Override
    public Fragment getFragment(int row, int col) {
        SettingsFragment settingsFragment = new SettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("row", row);
        bundle.putInt("col", col);
        settingsFragment.setArguments(bundle);
        return settingsFragment;
    }

    public ArrayList<SettingOverlay> getSettingModuleOverlays(int row, int col) {
        SettingsPage page = (mPages.get(row)).getPages(col);
        return page.getSettingOverlays();
    }

    @Override
    public int getRowCount() {
        return mPages.size();
    }

    @Override
    public int getColumnCount(int row) {
        return mPages.get(row).size();
    }
}