package com.usda.fmsc.android.listeners;

import androidx.viewpager.widget.ViewPager;

public class ComplexOnPageChangeListener implements ViewPager.OnPageChangeListener {
    private boolean isPageChanged = false;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        isPageChanged = true;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                if (isPageChanged) {
                    onPageChanged();
                    isPageChanged = false;
                    break;
                }
            case ViewPager.SCROLL_STATE_DRAGGING:
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                break;
        }
    }

    public void onPageChanged() {

    }
}
