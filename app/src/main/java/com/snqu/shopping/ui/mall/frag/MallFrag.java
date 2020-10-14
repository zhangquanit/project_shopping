package com.snqu.shopping.ui.mall.frag;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.ui.StatusBar;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.tabs.TabLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.mall.entity.MallBannerEntity;
import com.snqu.shopping.data.mall.entity.MallCategoryEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.ui.login.LoginFragment;
import com.snqu.shopping.ui.main.frag.WebViewFrag;
import com.snqu.shopping.ui.mall.goods.ShopGoodsDetailActivity;
import com.snqu.shopping.ui.mall.viewmodel.MallViewModel;
import com.snqu.shopping.util.GlideUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.widget.viewpager.BannerImageLoader;
import common.widget.viewpager.BannerViewPager;
import common.widget.viewpager.ViewPager;

/**
 * 自供
 *
 * @author zhangquan
 */
public class MallFrag extends SimpleFrag {
    @BindView(R.id.home_banner)
    BannerViewPager mBannerViewPager;
    @BindView(R.id.banner_container)
    RelativeLayout mBanner;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.refreshlayout)
    SmartRefreshLayout smartRefreshLayout;

    MallViewModel mallViewModel;
    private static List<MallCategoryEntity> plateList;
    public static final String RECOMMEND_ID = "-1";


    float maxHeight = 125f;

    @Override
    protected int getLayoutId() {
        return R.layout.mall_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this, mView);
        initView();
        initData();
    }

    private void initView() {
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mallViewModel.getBanner();
                try {
                    if (null != plateList && !plateList.isEmpty()) {
                        int selectedTabPosition = tabLayout.getSelectedTabPosition();
                        String id = plateList.get(selectedTabPosition)._id;
                        EventBus.getDefault().post(new PushEvent(Constant.Event.MALL_RECOM_REFRESH, id));
                    } else {
                        mallViewModel.getCategory();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ImageView iv_user = findViewById(R.id.iv_user);
        iv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == UserClient.getUser()) {
                    LoginFragment.Companion.start(mContext);
                } else {
                    MallUserFrag.start(mContext);
                }
            }
        });

        initBanner();

        viewPager.setPagingEnabled(false);
        viewPager.setSmoothScroll(false);
        viewPager.setOffscreenPageLimit(2);

    }

    private void initData() {
        mallViewModel = ViewModelProviders.of(this).get(MallViewModel.class);
        mallViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(NetReqResult netReqResult) {
                smartRefreshLayout.finishRefresh(netReqResult.successful);
                if (TextUtils.equals(netReqResult.tag, MallViewModel.TAG_BANNER)) {
                    if (netReqResult.successful) {
                        MallBannerEntity bannerEntity = (MallBannerEntity) netReqResult.data;
                        List<MallBannerEntity.Banner> banner_list = bannerEntity.banner_list;
                        setBanner(banner_list);
                    }
                } else if (TextUtils.equals(netReqResult.tag, MallViewModel.TAG_CATEGORY)) {
                    if (netReqResult.successful) {
                        List<MallCategoryEntity> categoryEntity = (List<MallCategoryEntity>) netReqResult.data;
                        initCategoryList(categoryEntity);
                    }
                }
            }
        });

        mallViewModel.getBanner();
        mallViewModel.getCategory();
    }

    /**
     * banner
     */
    private void initBanner() {
        mBannerViewPager.setPageMargin(DeviceUtil.dip2px(mContext, 7));
        mBannerViewPager.setOnPageChangeListener(new androidx.viewpager.widget.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
//                // 当前页面不是推荐页，不要进行任何操作
//                if (homePos != 0) {
//                    mBannerViewPager.pauseAutoScroll();
//                    return;
//                }
            }

            @Override
            public void onPageSelected(int position) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
                // 当前页面不是推荐页，不要进行任何背景操作
//                if (homePos != 0) {
//                    mBannerViewPager.pauseAutoScroll();
//                    return;
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBannerViewPager.setImageLoader(new BannerImageLoader<ImageView, MallBannerEntity.Banner>() {
            @Override
            public void displayView(Context ctx, MallBannerEntity.Banner bannerEntity, ImageView imageView, int pos, int count) {
                bannerEntity.images_url = GlideUtil.checkUrl(bannerEntity.images_url);
                GlideUtil.loadBitmap(ctx, bannerEntity.images_url, new BitmapImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            Bitmap bitmap = resource;
                            int vw = LContext.screenWidth - DeviceUtil.dip2px(LContext.getContext(), 20);
                            int vh = (int) (bitmap.getHeight() * vw * 1.0f / (bitmap.getWidth() * 1.0f));

                            if (vh > ConvertUtils.dp2px(maxHeight)) {
                                vh = ConvertUtils.dp2px(maxHeight);
                            }

                            ViewGroup.LayoutParams layoutParams = mBannerViewPager.getLayoutParams();
                            if (layoutParams.height != vh) {
                                layoutParams.height = vh;
                                mBannerViewPager.setLayoutParams(layoutParams);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        super.onResourceReady(resource, transition);
                    }
                });
            }

            @Override
            public ImageView createView(Context ctx) {
                RoundedImageView imageView = (RoundedImageView) getLayoutInflater().inflate(R.layout.home_banner_item, null);
                return imageView;
            }
        });
        mBannerViewPager.setmOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MallBannerEntity.Banner banner = (MallBannerEntity.Banner) mBannerViewPager.getDataList().get(position);
                mallViewModel.bannerReport(banner._id);

                if (TextUtils.equals(banner.place, "2") && !TextUtils.isEmpty(banner.goods_details)) { //打开商品详情
                    ShopGoodsDetailActivity.start(mContext, banner.goods_details);
                } else if (TextUtils.equals(banner.place, "1") && !TextUtils.isEmpty(banner.activity_id)) { //打开活动
                    WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                    webViewParam.url = GlideUtil.checkUrl(banner.url);
                    WebViewFrag.start(mContext, webViewParam);
                }
            }
        });

        mBannerViewPager.setInterval(3 * 1000);
        mBannerViewPager.startAutoScroll(2 * 1000);
    }

    private void setBanner(List<MallBannerEntity.Banner> bannerEntitys) {
        if (mBannerViewPager != null && mBannerViewPager.isStarted()) {
            mBannerViewPager.pauseAutoScroll();
        }

        if (null == bannerEntitys || bannerEntitys.isEmpty()) {
            mBanner.setVisibility(View.GONE);
        } else {
            mBanner.setVisibility(View.VISIBLE);
            if (bannerEntitys.size() > 6) {
                bannerEntitys = bannerEntitys.subList(0, 6);
            }
            mBannerViewPager.setDataList(bannerEntitys);
            if (mBannerViewPager != null) {
                if (mBannerViewPager.isStarted()) {
                    mBannerViewPager.resumeAutoScroll();
                } else {
                    mBannerViewPager.startAutoScroll();
                }
            }
        }
    }

    private void initCategoryList(List<MallCategoryEntity> dataList) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        MallCategoryEntity categoryEntity = new MallCategoryEntity();
        categoryEntity._id = RECOMMEND_ID;
        categoryEntity.name = "推荐";
        dataList.add(0, categoryEntity);

        plateList = dataList;

        tabLayout.removeAllTabs();
        for (MallCategoryEntity entity : dataList) {
            TabLayout.Tab tab = tabLayout.newTab().setText(entity.name);
            // 去掉点击背景
            LinearLayout tabView = tab.view;
            if (tabView != null) {
                tabView.setBackgroundColor(getColor(R.color.transparent));
            }
            if (entity.name.equals("推荐")) {
                SpanUtils spanUtils = new SpanUtils();
                spanUtils.append(tab.getText())
                        .setFontSize(17, true)
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
                        .setFontSize(17, true)
                        .setBold();
                tab.setText(spanUtils.create());

                int pos = tab.getPosition();

                EventBus.getDefault().post(new PushEvent(Constant.Event.PAUSE_RECOMMEND, pos));
                viewPager.setCurrentItem(pos);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                SpanUtils spanUtils = new SpanUtils();
                spanUtils.append(tab.getText())
                        .setFontSize(15, true);
                tab.setText(spanUtils.create());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        try {
            PagerAdapter adapter = viewPager.getAdapter();
            if (null != adapter) {
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewPager.setAdapter(new MallFrag.TabPagerAdapter(getChildFragmentManager()));
    }

    private class TabPagerAdapter extends FragmentPagerAdapter {


        public TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new MallRecommendFrag();
            }

            MallCategoryFrag categoryFrag = new MallCategoryFrag();
            categoryFrag.setArguments(MallCategoryFrag.getParam(plateList.get(position)));
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

    @OnClick({R.id.rl_search})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_search:
                MallSearchFrag.start(mContext);
                break;
        }
    }

    private void resumeScroll() {
        if (null != mBannerViewPager) {
            mBannerViewPager.resumeAutoScroll();
        }

    }

    private void pauseScroll() {
        if (null != mBannerViewPager) {
            mBannerViewPager.pauseAutoScroll();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        resumeScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseScroll();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            PagerAdapter adapter = viewPager.getAdapter();
            if (null != adapter) {
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBar.setStatusBar(mContext, true);
        }
    }
}
