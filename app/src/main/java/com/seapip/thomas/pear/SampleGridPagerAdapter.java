package com.seapip.thomas.pear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.util.DisplayMetrics;

import java.util.ArrayList;

public class SampleGridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context mContext;
    private ArrayList<SimpleRow> mPages;

    public SampleGridPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;
        initPages();
    }

    private void initPages() {
        mPages = new ArrayList<>();

        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int spacing = ModularWatchFaceService.MODULE_SPACING - 2;
        int inset = (width - (int) Math.sqrt(width * width / 2)) / 2 + 20;
        Rect bounds = new Rect(inset, inset, width - inset, height - inset);

        ArrayList<SettingModuleOverlay> colorModules = new ArrayList<>();
        SettingModuleOverlay colorModuleOverlay = new SettingModuleOverlay(bounds, "Color",
                Paint.Align.LEFT, null, 12);
        colorModules.add(colorModuleOverlay);
        colorModuleOverlay.setActive(true);

        ArrayList<SettingModuleOverlay> complicationModules = new ArrayList<>();
        SettingModuleOverlay complicationTopLeftModuleOverlay = new SettingModuleOverlay(
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
        SettingModuleOverlay complicationCenterModuleOverlay = new SettingModuleOverlay(
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
        SettingModuleOverlay complicationBottomLeftModuleOverlay = new SettingModuleOverlay(
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
        SettingModuleOverlay complicationBottomCenterModuleOverlay = new SettingModuleOverlay(
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
        SettingModuleOverlay complicationBottomRightModuleOverlay = new SettingModuleOverlay(
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

        SimpleRow row = new SimpleRow();
        row.addPages(new SimplePage(colorModules));
        row.addPages(new SimplePage(complicationModules));
        mPages.add(row);
    }

    @Override
    public Fragment getFragment(int row, int col) {
        SettingFragment settingFragment = new SettingFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("row", row);
        bundle.putInt("col", col);
        settingFragment.setArguments(bundle);
        return settingFragment;
    }

    public ArrayList<SettingModuleOverlay> getSettingModuleOverlays(int row, int col) {
        SimplePage page = ((SimpleRow) mPages.get(row)).getPages(col);
        return page.getSettingModuleOverlays();
    }

    @Override
    public Drawable getBackgroundForPage(int row, int col) {
        SimplePage page = ((SimpleRow) mPages.get(row)).getPages(col);
        return new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
            }

            @Override
            public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSPARENT;
            }
        };
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