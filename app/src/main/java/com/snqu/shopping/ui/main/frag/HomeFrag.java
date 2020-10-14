package com.snqu.shopping.ui.main.frag;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;

import com.anroid.base.SimpleFrag;
import com.anroid.base.ui.StatusBar;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.google.android.material.tabs.TabLayout;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.data.home.entity.HomeLayoutEntity;
import com.snqu.shopping.data.home.entity.IconEntity;
import com.snqu.shopping.ui.main.frag.search.SearchFrag;
import com.snqu.shopping.ui.main.scan.ScanActivity;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.statistics.DataCache;
import com.snqu.shopping.util.statistics.SndoData;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.widget.viewpager.ViewPager;

/**
 * @author 张全
 */
public class HomeFrag extends SimpleFrag {

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.loadingview)
    LoadingStatusView loadingStatusView;
    @BindView(R.id.bg_top)
    ImageView bg_top;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    private AnimationDrawable taskDrawable;
    private static List<CategoryEntity> plateList;
    private HomeViewModel mHomeViewModel;
    public static HomeLayoutEntity layoutIndex;

    @Override
    protected int getLayoutId() {
        return R.layout.home_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this, mView);
        StatusBar.setStatusBar(mContext, false);
        initView();
        initData();
    }

    private void initView() {
        viewPager.setPagingEnabled(false);
        viewPager.setSmoothScroll(false);
        viewPager.setOffscreenPageLimit(2);


        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingStatusView.setStatus(LoadingStatusView.Status.LOADING);
                getData();
            }
        });
        //任务入口
        ImageView iv_task = findViewById(R.id.iv_task);
        taskDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.icon_home_task);
        iv_task.setImageDrawable(taskDrawable);
        taskDrawable.start();
    }

    @OnClick({R.id.iv_scan, R.id.search_bar, R.id.iv_task})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_scan:
                ScanActivity.Companion.start(mContext);
                SndoData.event(SndoData.XLT_EVENT_HOME_SCAN);
                break;
            case R.id.search_bar:
                SearchFrag.start(mContext);
                break;
            case R.id.iv_task: //会员任务
                CommonUtil.jumpToTaskPage(mContext);
                break;
        }
    }

    private void initData() {
        // 这里做猜你喜欢模式的状态切换
        String mode = SPUtils.getInstance().getString(Constant.PREF.LIKE_MODE, "0");
        if (TextUtils.equals(mode, "0")) {
            SPUtils.getInstance().put(Constant.PREF.LIKE_MODE, "1");
        } else {
            SPUtils.getInstance().put(Constant.PREF.LIKE_MODE, "0");
        }

        // 加载数据
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(HomeViewModel.TAG_LAYOUTINDEX, netReqResult.tag)) {
                    if (netReqResult.successful) {
                        loadingStatusView.setVisibility(View.GONE);
                        layoutIndex = (HomeLayoutEntity) netReqResult.data;
                        initCategoryList(layoutIndex.category);
                    } else {
                        loadingStatusView.setStatus(LoadingStatusView.Status.FAIL);
                    }
                } else if (TextUtils.equals(HomeViewModel.TAG_ICON_CONFIG, netReqResult.tag)) {
                    if (!netReqResult.successful || null == netReqResult.data) {
                        return;
                    }
                    ResponseDataObject<IconEntity> dataObject = (ResponseDataObject<IconEntity>) netReqResult.data;
                    if (null == dataObject.data) {
                        return;
                    }
                    HomeClient.setIcon(dataObject.data.logo);
                    CommonUtil.setIcon(getActivity(), HomeClient.getIcon());
                }
            }
        });

//        mHomeViewModel.getIconConfig();
        getData();

    }


    private void getData() {
        mHomeViewModel.init();
    }

    private void initCategoryList(List<CategoryEntity> dataList) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.name = "推荐";
        dataList.add(0, categoryEntity);

        categoryEntity = new CategoryEntity();
        categoryEntity.name = "猜你喜欢";
        dataList.add(1, categoryEntity);

        plateList = dataList;

        tabLayout.removeAllTabs();
        for (CategoryEntity entity : dataList) {
            TabLayout.Tab tab = tabLayout.newTab().setText(entity.name);
            // 去掉点击背景
            LinearLayout tabView = tab.view;
            if (tabView != null) {
                tabView.setBackgroundColor(getColor(R.color.transparent));
            }
            if (entity.name.equals("推荐")) {
                SpanUtils spanUtils = new SpanUtils();
                spanUtils.append(tab.getText())
                        .setFontSize(15, true)
                        .setBold();
                tab.setText(spanUtils.create());
                tab.select();
            }
            tabLayout.addTab(tab);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SpanUtils spanUtils = new SpanUtils();
                spanUtils.append(tab.getText())
                        .setFontSize(15, true)
                        .setBold();
                tab.setText(spanUtils.create());

                int pos = tab.getPosition();
                if (pos != 0) {
                    // 如果不是在推荐页面，则背景恢复为默认的
                    bg_top.setImageDrawable(null);
                    bg_top.setBackgroundResource(R.drawable.self_balance_bg);
                }
                EventBus.getDefault().post(new PushEvent(Constant.Event.PAUSE_RECOMMEND, pos));
                viewPager.setCurrentItem(pos);

                //统计
                CategoryEntity categoryEntity = plateList.get(tab.getPosition());
                DataCache.homeFirstCategory = categoryEntity;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                SpanUtils spanUtils = new SpanUtils();
                String text = tab.getText().toString();
                spanUtils.append(text);
                tab.setText(spanUtils.create());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.setAdapter(new TabPagerAdapter(getChildFragmentManager()));
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {

        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new RecommendFrag();
            }
            if (position == 1) {
                GuessLikeFrag guessLikeFrag = new GuessLikeFrag();
                return guessLikeFrag;
            }
            CategoryFrag categoryFrag = new CategoryFrag();
            categoryFrag.setArguments(CategoryFrag.getParam(plateList.get(position)));
            categoryFrag.setData(plateList.get(position));
            return categoryFrag;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return plateList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return plateList.get(position).name;
        }
    }

    @Override
    public void restorePage() {
        StatusBar.setStatusBar(mContext, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        taskDrawable.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        taskDrawable.start();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBar.setStatusBar(mContext, false);
            taskDrawable.start();
        } else {
            taskDrawable.stop();
        }

    }
}
