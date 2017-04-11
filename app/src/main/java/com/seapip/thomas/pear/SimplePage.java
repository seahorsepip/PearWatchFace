package com.seapip.thomas.pear;

import java.util.ArrayList;

public class SimplePage {
    private ArrayList<SettingModuleOverlay> settingModuleOverlays;

    public SimplePage(ArrayList<SettingModuleOverlay> settingModuleOverlays) {
        this.settingModuleOverlays = settingModuleOverlays;
    }

    public ArrayList<SettingModuleOverlay> getSettingModuleOverlays() {
        return settingModuleOverlays;
    }
}