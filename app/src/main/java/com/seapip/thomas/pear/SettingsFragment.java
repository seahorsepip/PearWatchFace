package com.seapip.thomas.pear;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderChooserIntent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends Fragment implements View.OnTouchListener {
    /* Paint */
    Paint overlayPaint;
    private View view;
    private int row;
    private int col;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        row = getArguments().getInt("row");
        col = getArguments().getInt("col");

        overlayPaint = new Paint();
        overlayPaint.setColor(Color.argb(192, 0, 0, 0));
        view = new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);
                for (SettingOverlay moduleOverlay : getSettingModuleOverlays()) {
                    moduleOverlay.draw(canvas);
                }
            }
        };
        view.setOnTouchListener(this);
        return view;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            SettingOverlay active = null;
            for (SettingOverlay moduleOverlay : getSettingModuleOverlays()) {
                if (moduleOverlay.contains((int) event.getX(), (int) event.getY())) {
                    active = moduleOverlay;
                }
            }
            if (active != null) {
                if (active.getActive()) {
                    Intent intent = active.getIntent();
                    if (intent != null) {
                        startActivityForResult(intent, active.getRequestCode());
                    }
                }
                for (SettingOverlay moduleOverlay : getSettingModuleOverlays()) {
                    moduleOverlay.setActive(false);
                }
                active.setActive(true);
            }
            view.invalidate();
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            for(int id : ModularWatchFaceService.COMPLICATION_IDS) {
                if(id == requestCode) {
                    String title = "OFF";
                    if (data != null) {
                        ComplicationProviderInfo providerInfo =
                                data.getParcelableExtra(ProviderChooserIntent.EXTRA_PROVIDER_INFO);
                        if (providerInfo != null) {
                            title = providerInfo.providerName;
                        }
                    }
                    getSettingModuleOverlays().get(requestCode).setTitle(title);
                    return;
                }
            }
        }
    }

    private ArrayList<SettingOverlay> getSettingModuleOverlays() {
        return ((SettingsActivity) getActivity()).getSettingModuleOverlays(row, col);
    }
}
