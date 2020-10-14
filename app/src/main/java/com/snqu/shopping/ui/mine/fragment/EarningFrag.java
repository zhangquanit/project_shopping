package com.snqu.shopping.ui.mine.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.snqu.shopping.R;
import com.snqu.shopping.util.statistics.SndoData;

import common.widget.viewpager.ViewPager;

/**
 * 我的收益
 *
 * @author 张全
 */
public class EarningFrag extends SimpleFrag {
    private ViewPager viewPager;
    View divider1, divider2;
    TextView tv_title_label1, tv_title_label2;

    public static void start(Context ctx) {
        SimpleFragAct.SimpleFragParam fragParam = new SimpleFragAct.SimpleFragParam("", EarningFrag.class);
        fragParam.hideTitleBar(true);
        SimpleFragAct.start(ctx, fragParam);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.earning_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, true);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        divider1 = findViewById(R.id.divider1);
        divider2 = findViewById(R.id.divider2);
        tv_title_label1 = findViewById(R.id.tv_title_label1);
        tv_title_label2 = findViewById(R.id.tv_title_label2);
        findViewById(R.id.leftbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0);
            }
        });
        findViewById(R.id.rightbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        viewPager = findViewById(R.id.viewpager);
        viewPager.setSmoothScroll(false);
        viewPager.setOffscreenPageLimit(3);
        EarningTabPagerAdapter earningTabPagerAdapter = new EarningTabPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(earningTabPagerAdapter);

        viewPager.addOnPageChangeListener(new androidx.viewpager.widget.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int pos) {
                switchTab(pos);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        switchTab(0);
    }

    private void switchTab(int pos) {
        if (pos == 0) {
            divider1.setVisibility(View.VISIBLE);
            divider2.setVisibility(View.INVISIBLE);
            tv_title_label1.setSelected(true);
            tv_title_label2.setSelected(false);
            SndoData.event(SndoData.XLT_EVENT_GROUP_EARNINGS);

        } else {
            divider1.setVisibility(View.INVISIBLE);
            divider2.setVisibility(View.VISIBLE);
            tv_title_label1.setSelected(false);
            tv_title_label2.setSelected(true);
            SndoData.event(SndoData.XLT_EVENT_SELF_EARNINGS);
        }
    }

    private class EarningTabPagerAdapter extends FragmentStatePagerAdapter {

        public EarningTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            EarningItemFrag page = new EarningItemFrag();
            page.setArguments(EarningItemFrag.getParam(position));
            return page;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }
}
