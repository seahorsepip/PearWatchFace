package com.seapip.thomas.pear;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.view.View;

import java.util.ArrayList;

public class SettingsActivity extends Activity {
    private SampleGridPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);
        GridViewPager mGridPager = (GridViewPager) findViewById(R.id.pager);
        DotsPageIndicator dots = (DotsPageIndicator) findViewById(R.id.indicator);
        dots.setPager(mGridPager);
        adapter = new SampleGridPagerAdapter(this, getFragmentManager());
        mGridPager.setAdapter(adapter);
        ModularWatchFaceService.SETTINGS_MODE_TRIGGER = true;
        ModularWatchFaceService.SETTINGS_MODE = true;
    }

    @Override
    public void setContentView(View view) {
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);
        super.setContentView(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ModularWatchFaceService.SETTINGS_MODE_TRIGGER = true;
        ModularWatchFaceService.SETTINGS_MODE = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ModularWatchFaceService.SETTINGS_MODE_TRIGGER = true;
        ModularWatchFaceService.SETTINGS_MODE = false;
    }

    public ArrayList<SettingModuleOverlay> getSettingModuleOverlays(int row, int col) {
        return adapter.getSettingModuleOverlays(row, col);
    }
}