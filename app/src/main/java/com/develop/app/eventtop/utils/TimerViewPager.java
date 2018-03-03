package com.develop.app.eventtop.utils;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.TimerTask;

public class TimerViewPager extends TimerTask {
    private Activity activity;
    private ViewPager viewPager;
    private int screenLimit;
    private static int position = 0;

    public TimerViewPager(Activity activity, ViewPager viewPager, int screenLimit) {
        this.activity = activity;
        this.viewPager = viewPager;
        this.screenLimit = screenLimit;
    }

    @Override
    public void run() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("ttdd", String.valueOf(viewPager.getCurrentItem()) + " - " + String.valueOf(screenLimit));
                if (viewPager.getCurrentItem() == position || viewPager.getCurrentItem() < screenLimit) {
                    position++;
                    viewPager.setCurrentItem(position, true);
                } else if (viewPager.getCurrentItem() == screenLimit) {
                    position = 0;
                    viewPager.setCurrentItem(position, true);
                }
            }
        });
    }
}
