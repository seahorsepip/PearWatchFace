package com.seapip.thomas.pear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.util.ArrayList;

public class SettingsAdapter extends FragmentGridPagerAdapter {
    private ArrayList<SettingsRow> pages;

    public SettingsAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        pages = initPages();
    }

    public ArrayList<SettingsRow> initPages() {
        return new ArrayList<>();
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

    public ArrayList<SettingsOverlay> getSettingModuleOverlays(int row, int col) {
        SettingsPage page = (pages.get(row)).getPages(col);
        return page.getSettingOverlays();
    }

    @Override
    public int getRowCount() {
        return pages.size();
    }

    @Override
    public int getColumnCount(int row) {
        return pages.get(row).size();
    }
}