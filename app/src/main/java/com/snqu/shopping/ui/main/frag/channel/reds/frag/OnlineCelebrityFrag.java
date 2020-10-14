package com.snqu.shopping.ui.main.frag.channel.reds.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;

import com.android.util.ext.ToastUtil;
import com.anroid.base.SimpleFrag;
import com.snqu.shopping.R;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.ui.main.frag.channel.reds.RedViewModel;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import common.widget.viewpager.ViewPager;
import common.widget.viewpager.indicator.TabPageIndicator;
import common.widget.viewpager.indicator.TitleIndicator;

/**
 * 网红爆款
 */
public class OnlineCelebrityFrag extends SimpleFrag {
    TabPageIndicator tabPageIndicator;
    ViewPager viewPager;

    private List<View> lines = new ArrayList<>();
    private static List<CategoryEntity> categoryList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.reds_online_celebrity_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initView() {
        tabPageIndicator = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
//        viewPager.setPagingEnabled(false);
        viewPager.setSmoothScroll(false);
        viewPager.setOffscreenPageLimit(3);
    }

    private void initData() {
        RedViewModel redViewModel =
                ViewModelProviders.of(this).get(RedViewModel.class);
        redViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_CATEGORY:
                        if (netReqResult.successful) {
                            List<CategoryEntity> categoryEntities = (List<CategoryEntity>) netReqResult.data;
                            initCategoryList(categoryEntities);
                        } else {
                            ToastUtil.show(netReqResult.message);
                        }
                        break;
                }
            }
        });
        redViewModel.getCategorys();
    }

    private void initCategoryList(List<CategoryEntity> dataList) {

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.name = "全部";
        dataList.add(0, categoryEntity);
        categoryList = dataList;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        tabPageIndicator.setTitleInidcator(new TitleIndicator() {
            @Override
            public View addTab(int index, CharSequence title) {
                View view = inflater.inflate(R.layout.home_tab_title_layout, null);
                TextView textView = view.findViewById(R.id.tv_title);
                textView.setText(title);
                lines.add(view);
                if (index == 0) {
                    view.setBackgroundResource(R.drawable.home_category_title_bg);
                    view.setSelected(true);
                }
                return view;
            }
        });
        viewPager.setAdapter(new TabPagerAdapter(getChildFragmentManager()));
        tabPageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float v, int i1) {

            }

            @Override
            public void onPageSelected(int pos) {
                for (View view : lines) {
                    view.setSelected(false);
                    view.setBackground(null);
                }
                lines.get(pos).setBackgroundResource(R.drawable.home_category_title_bg);
                lines.get(pos).setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

        tabPageIndicator.setViewPager(viewPager);
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new OnlineRecommendFrag();
            } else {
                OnlineItemFrag page = new OnlineItemFrag();
                page.setArguments(OnlineItemFrag.getParam(categoryList.get(position)));
                return page;
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return categoryList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categoryList.get(position).name;
        }
    }

}
