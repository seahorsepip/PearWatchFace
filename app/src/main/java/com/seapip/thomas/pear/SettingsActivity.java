package com.seapip.thomas.pear;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.view.View;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);
        GridViewPager mGridPager = (GridViewPager) findViewById(R.id.pager);
        DotsPageIndicator dots = (DotsPageIndicator) findViewById(R.id.indicator);
        dots.setPager(mGridPager);
        mGridPager.setAdapter(new SampleGridPagerAdapter(this, getFragmentManager()));
    }

    @Override
    public void setContentView(View view) {
        getWindow().setExitTransition(null);
        getWindow().setEnterTransition(null);
        super.setContentView(view);
    }
}