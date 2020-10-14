package com.snqu.shopping.common.ui;

import android.view.View;

import common.widget.viewpager.ViewPager;

public class BannerTransFormer implements ViewPager.PageTransformer {
    public static float MIN_SCALE = 0.92f;

    @Override
    public void transformPage(View page, float position) {
        if (position < -1 || position > 1) {
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
        } else if (position <= 1) { // [-1,1]
            if (position < 0) {
                float scaleX = 1 + 0.08f * position;
                page.setScaleX(scaleX);
                page.setScaleY(scaleX);
            } else {
                float scaleX = 1 - 0.08f * position;
                page.setScaleX(scaleX);
                page.setScaleY(scaleX);
            }
        }
    }
}