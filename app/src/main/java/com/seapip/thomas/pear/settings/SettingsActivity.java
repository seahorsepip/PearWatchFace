package com.seapip.thomas.pear.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;

import com.seapip.thomas.pear.R;

import java.util.ArrayList;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);
        ((DotsPageIndicator) findViewById(R.id.indicator))
                .setPager((GridViewPager) findViewById(R.id.pager));
    }

    public void setSettingsMode(boolean mode) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSettingsMode(true);
    }
    @Override
    protected void onStop() {
        super.onStop();
        setSettingsMode(false);
    }

    public SettingsAdapter getAdapter() {
        return null;
    }

    public ArrayList<SettingsOverlay> getSettingModuleOverlays(int row, int col) {
        return getAdapter().getSettingModuleOverlays(row, col);
    }
}