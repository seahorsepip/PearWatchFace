package com.seapip.thomas.pear;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import org.jraf.android.androidwearcolorpicker.app.ColorPickActivity;

public class ColorActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ColorPreferenceFragment colorPreferenceFragment = new ColorPreferenceFragment();
        Bundle bundle = getIntent().getExtras();
        colorPreferenceFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(android.R.id.content, colorPreferenceFragment).commit();
    }

    public static class ColorPreferenceFragment extends PreferenceFragment {

        int oldColor;
        int colorNamesId;
        int colorValuesId;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle bundle = getArguments();

            oldColor = bundle.getInt("color");
            colorNamesId = bundle.getInt("color_names_id");
            colorValuesId = bundle.getInt("color_values_id");

            PreferenceScreen preferenceScreen = getPreferenceManager().createPreferenceScreen(getContext());
            setPreferenceScreen(preferenceScreen);
        }

        @Override
        public void onStart() {
            super.onStart();

            getPreferenceScreen().removeAll();

            Preference customPreference = new Preference(getContext());
            customPreference.setTitle("Custom");
            customPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivityForResult(new ColorPickActivity.IntentBuilder().oldColor(oldColor).build(getContext()), 0);
                    return false;
                }
            });
            setStyleIcon(customPreference, getContext().getDrawable(R.drawable.ic_colorize_black_24dp), Color.WHITE);
            getPreferenceScreen().addPreference(customPreference);

            String[] colorNames = getResources().getStringArray(colorNamesId);
            TypedArray colorValues = getResources().obtainTypedArray(colorValuesId);
            for (int x = 0; x < colorNames.length; x++) {
                Preference preference = new Preference(getContext());
                final String name = colorNames[x];
                final int color = colorValues.getColor(x, 0);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent();
                        intent.putExtra("color_name", name);
                        intent.putExtra("color_value", color);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                        return false;
                    }
                });
                preference.setTitle(name);
                setStyleIcon(preference, getContext().getDrawable(R.drawable.ic_circle_black_24dp).mutate(), color);

                getPreferenceScreen().addPreference(preference);

            }
            colorValues.recycle();
        }

        private void setStyleIcon(Preference preference, Drawable icon, int color) {
            LayerDrawable layerDrawable = (LayerDrawable) getContext().getDrawable(R.drawable.config_icon);
            icon.setTint(color);
            if (layerDrawable.setDrawableByLayerId(R.id.nested_icon, icon)) {
                preference.setIcon(layerDrawable);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                intent.putExtra("color_name", "Custom");
                intent.putExtra("color_value", ColorPickActivity.getPickedColor(data));
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        }
    }
}
