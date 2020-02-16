package com.seapip.thomas.pear.settings;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderChooserIntent;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;

import com.seapip.thomas.pear.ColorActivity;
import com.seapip.thomas.pear.R;

import java.util.ArrayList;

public class SettingsActivity extends Activity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext().getApplicationContext());
        editor = preferences.edit();
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

    public void setColorOverlay(final SettingsOverlay settingsOverlay,
                                final String nameKey, final String valueKey,
                                String defaultName, int defaultValue) {
        final Intent intent = new Intent(getApplicationContext(), ColorActivity.class);
        intent.putExtra("color", preferences.getInt(valueKey, defaultValue));
        intent.putExtra("color_names_id", R.array.color_names);
        intent.putExtra("color_values_id", R.array.color_values);
        settingsOverlay.setIntent(intent);
        settingsOverlay.setTitle(preferences.getString(nameKey, defaultName));
        final Runnable runnable = settingsOverlay.getRunnable();
        settingsOverlay.setRunnable(new Runnable() {
            @Override
            public void run() {
                Intent data = settingsOverlay.getData();
                String name = data.getStringExtra("color_name");
                editor.putString(nameKey, name);
                int value = data.getIntExtra("color_value", 0);
                editor.putInt(valueKey, value);
                editor.apply();
                intent.putExtra("color", value);
                settingsOverlay.setTitle(name);
                if(runnable != null) {
                    runnable.run();
                }
                setSettingsMode(true);
            }
        });

    }

    public void setComplicationOverlay(final SettingsOverlay settingsOverlay,
                                       Class watchFaceService,
                                       int id,
                                       int[] supportedTypes) {
        settingsOverlay.setIntent(ComplicationHelperActivity.createProviderChooserHelperIntent(
                getApplicationContext(),
                new ComponentName(getApplicationContext().getApplicationContext(),
                        watchFaceService),
                id,
                supportedTypes));
        final Runnable runnable = settingsOverlay.getRunnable();
        settingsOverlay.setRunnable(new Runnable() {
            @Override
            public void run() {
                String title = "OFF";
                Intent data = settingsOverlay.getData();
                if (data != null) {
                    ComplicationProviderInfo providerInfo =
                            data.getParcelableExtra(ProviderChooserIntent.EXTRA_PROVIDER_INFO);
                    if (providerInfo != null) {
                        title = providerInfo.providerName;
                    }
                }
                settingsOverlay.setTitle(title);
                if(runnable != null) {
                    runnable.run();
                }
            }
        });
    }
}