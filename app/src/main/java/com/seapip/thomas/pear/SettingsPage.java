package com.seapip.thomas.pear;

import java.util.ArrayList;

public class SettingsPage {
    private ArrayList<SettingOverlay> settingOverlays;

    public SettingsPage(ArrayList<SettingOverlay> settingOverlays) {
        this.settingOverlays = settingOverlays;
    }

    public ArrayList<SettingOverlay> getSettingOverlays() {
        return settingOverlays;
    }
}