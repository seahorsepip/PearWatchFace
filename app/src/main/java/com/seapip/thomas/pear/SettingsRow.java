package com.seapip.thomas.pear;

import java.util.ArrayList;

public class SettingsRow {

    ArrayList<SettingsPage> mPagesRow = new ArrayList<SettingsPage>();

    public void addPages(SettingsPage page) {
        mPagesRow.add(page);
    }

    public SettingsPage getPages(int index) {
        return mPagesRow.get(index);
    }

    public int size(){
        return mPagesRow.size();
    }
}