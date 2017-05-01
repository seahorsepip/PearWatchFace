package com.seapip.thomas.pear_watchface.phone;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class MainActivity extends AppIntro2 {

    private Fragment mVersionFragment;
    private AlertDialog.Builder mVersionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        mVersionFragment = AppIntroFragment.newInstance("Version",
                "Check Android Wear version on the watch.",
                R.drawable.version, Color.parseColor("#00BCD4"));
        addSlide(mVersionFragment);
        addSlide(AppIntroFragment.newInstance("Play Store", "Open the Play Store on the watch.",
                R.drawable.playstore, Color.parseColor("#12C26D")));
        addSlide(AppIntroFragment.newInstance("Search", "Search for: \"Pear Watch Face\".",
                R.drawable.search, Color.parseColor("#12C26D")));
        addSlide(AppIntroFragment.newInstance("Install", "Install the watch face.",
                R.drawable.install, Color.parseColor("#12C26D")));

        showSkipButton(false);
        setProgressButtonEnabled(true);
        showDoneButton(false);

        final AlertDialog.Builder oldVersionDialog = new AlertDialog.Builder(this)
                .setTitle("Android Wear 1.5")
                .setMessage("This version is only Android Wear 2.0 compatible.")
                .setPositiveButton("Show older version", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.seapip.thomas.pear")));
                            finish();
                        } catch (android.content.ActivityNotFoundException e) {
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
        mVersionDialog = new AlertDialog.Builder(this)
                .setTitle("Version")
                .setMessage("Android wear version on the watch?")
                .setPositiveButton("Android Wear 2.0", null)
                .setNegativeButton("Android Wear 1.5", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        oldVersionDialog.show();
                    }
                })
                .setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                })
                .setCancelable(false);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        if (oldFragment == mVersionFragment) {
            mVersionDialog.show();
        }
    }
}
