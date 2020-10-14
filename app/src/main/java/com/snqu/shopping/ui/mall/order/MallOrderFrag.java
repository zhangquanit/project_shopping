package com.snqu.shopping.ui.mall.order;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.google.android.material.tabs.TabLayout;
import com.snqu.shopping.R;
import com.snqu.shopping.ui.mall.order.helper.MallOrderType;

import java.util.ArrayList;
import java.util.List;

import common.widget.viewpager.ViewPager;

/**
 * 订单
 */
public class MallOrderFrag extends SimpleFrag {
    public static void start(Context ctx) {
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("我的订单", MallOrderFrag.class));
    }

    private static List<MallOrderType> orderTypes;

    static {
        orderTypes = new ArrayList<>();
        orderTypes.add(MallOrderType.ALL);
        orderTypes.add(MallOrderType.PAY);
        orderTypes.add(MallOrderType.FH);
        orderTypes.add(MallOrderType.SH);
        orderTypes.add(MallOrderType.COMPLETE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mall_order_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, true, getTitleBar());
        initView();
        initData();
    }

    private void initView() {
        getTitleBar().setBackgroundColor(Color.WHITE);
        TabLayout tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        viewPager.setSmoothScroll(false);
        viewPager.setOffscreenPageLimit(orderTypes.size());


        tabLayout.removeAllTabs();
        for (MallOrderType orderType : orderTypes) {
            TabLayout.Tab tab = tabLayout.newTab().setText(orderType.name);
            // 去掉点击背景
            LinearLayout tabView = tab.view;
            if (tabView != null) {
                tabView.setBackgroundColor(getColor(R.color.transparent));
            }
            tabLayout.addTab(tab);
        }

        tabLayout.getTabAt(0).select();

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                viewPager.setCurrentItem(pos);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.setAdapter(new MallOrderFrag.TabPagerAdapter(getChildFragmentManager()));
    }

    private void initData() {

    }

    private class TabPagerAdapter extends FragmentPagerAdapter {


        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MallOrderItemFrag categoryFrag = new MallOrderItemFrag();
            categoryFrag.setArguments(MallOrderItemFrag.getParam(orderTypes.get(position)));
            categoryFrag.setData(orderTypes.get(position));
            return categoryFrag;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return orderTypes.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return orderTypes.get(position).name;
        }
    }
}
