package com.snqu.shopping.ui.main.frag;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.LazyFragment;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.animation.ArgbEvaluatorCompat;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.data.home.entity.HomeAdEntity;
import com.snqu.shopping.data.home.entity.HomeLayoutEntity;
import com.snqu.shopping.data.home.entity.ItemSourceEntity;
import com.snqu.shopping.data.home.entity.RecommendDayEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.login.LoginFragment;
import com.snqu.shopping.ui.main.MainActivity;
import com.snqu.shopping.ui.main.adapter.HomeTypeEightAdapter;
import com.snqu.shopping.ui.main.adapter.HomeTypeHalfAdapter;
import com.snqu.shopping.ui.main.adapter.HomeTypeSevenAdapter;
import com.snqu.shopping.ui.main.adapter.HomeTypeSixAdapter;
import com.snqu.shopping.ui.main.frag.channel.adapter.PlatePagerAdapter;
import com.snqu.shopping.ui.main.view.HomeViewType;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.ui.mall.goods.adapter.FlowLayoutManager;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.statistics.DataCache;
import com.snqu.shopping.util.statistics.SndoData;
import com.snqu.shopping.util.statistics.StatisticInfo;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import common.widget.viewpager.BannerImageLoader;
import common.widget.viewpager.BannerViewPager;
import common.widget.viewpager.ViewPager;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * 首页-推荐
 *
 * @author 张全
 */
public class RecommendFrag extends LazyFragment {
    private SmartRefreshLayout refreshLayout;

    public View mBanner;
    private BannerViewPager mBannerViewPager;
    private BannerViewPager mAdViewPager;
    private ViewPager recommedViewPager;
    private int homePos = 0;
    private HomeViewModel mHomeViewModel;
    private long lastRefreshTime;
    private ViewFlipper viewFlipper;
    private TabLayout typeTabLayout;
    private RecyclerView mHomeRecyclerView;
    private SeekBar indicatorView;
    private List<ItemSourceEntity> itemSourceList;
    public MutableLiveData<NetReqResult> mRefreshLiveData = new MutableLiveData<>();//刷新全部
    private List<RecommendGoodFrag> recommendGoodFrags = new ArrayList<>();
    private MutableLiveData<NetReqResult> mNetLiveData;
    private int selPos;
    private List<AdvertistEntity> bannerEntitys; // banner
    private List<AdvertistEntity> vajraDistEntitys; //金刚区
    private List<AdvertistEntity> centerEntities;
    private List<AdvertistEntity> alertEntitys;
    private List<AdvertistEntity> freeEntitys;
    private List<RecommendDayEntity> recommendDayEntities;
    private View mTbEmpowerLayout;
    private int mIndicW = 0;
    private ImageView mRed_envelope;
    private AdvertistEntity mTaskAdvertistEntity;
    private LinearLayout mContainerLayout;
    private RecommPagerAdapter recommPagerAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.recommend_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        addAction(Constant.Event.LOGIN_SUCCESS);
        addAction(Constant.Event.LOGIN_OUT);
        addAction(Constant.Event.HOME_TAP_TOP);
        addAction(Constant.Event.HOME_LAYOUT_INDEX);
        mNetLiveData = new MutableLiveData<>();
        initView();
        initData();
        setData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        if (TextUtils.equals(event.getAction(), Constant.Event.LOGIN_SUCCESS) || TextUtils.equals(event.getAction(), Constant.Event.LOGIN_OUT)) {
            //登录或退出登录 刷新
            loadAll();
        } else if (TextUtils.equals(event.getAction(), Constant.Event.HOME_TAP_TOP)) {
            View scroll_to_top = findViewById(R.id.scroll_to_top);
            if (scroll_to_top != null) {
                scroll_to_top.performClick();
            }
        } else if (TextUtils.equals(event.getAction(), Constant.Event.PAUSE_RECOMMEND)) {
            homePos = (int) event.getData();
            if (mBanner != null && mBanner.getVisibility() == View.GONE) {
                resetBannerTopImage();
            }
        }
    }

    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        registLiveData();

        mHomeViewModel.doTaskList();

        if (null == centerEntities) {
            mHomeViewModel.getHomeAd(mNetLiveData);
        }
        if (null == recommendDayEntities) {
            mHomeViewModel.getDayRecommend(mNetLiveData);
        }
        if (null == HomeFrag.layoutIndex) {
            loadAll();
        }
    }

    private void registLiveData() {

        mHomeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (netReqResult.successful) {
                    if (netReqResult.tag.equals(ApiHost.AD_TASK_PAGE)) {
                        List<AdvertistEntity> datas = (List<AdvertistEntity>) netReqResult.data;
                        if (datas != null && datas.size() > 0) {
                            mTaskAdvertistEntity = datas.get(0);
                            if (!TextUtils.isEmpty(mTaskAdvertistEntity.image)) {
                                GlideUtil.loadPic(mRed_envelope, mTaskAdvertistEntity.image);
                            }
                        }
                    }
                }
            }
        });

        //刷新全部数据
        mRefreshLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult homeRefreshResult) {
                if (homeRefreshResult.successful) {
                    lastRefreshTime = System.currentTimeMillis();
                }
                refreshLayout.finishRefresh(homeRefreshResult.successful);
                if (homeRefreshResult.successful) {
                    HomeViewModel.HomeData homeData = (HomeViewModel.HomeData) homeRefreshResult.data;
                    if (null != homeData.homeLayoutIndex) {
                        HomeFrag.layoutIndex = homeData.homeLayoutIndex;
                    }
                    if (null != homeData.homeAdEntity) {
                        centerEntities = homeData.homeAdEntity.cetnerAdEntity;
                        alertEntitys = homeData.homeAdEntity.alertAdEntity;
//                        freeEntitys = homeData.homeAdEntity.freeAdEntity;
                        HomeFrag homeFrag = (HomeFrag) getParentFragment();
//                        homeFrag.setTopAd(homeData.homeAdEntity.tipAdEntity);
                    }
                    recommendDayEntities = homeData.dayRecommendList;
                    setData();
                } else {
                    ToastUtil.show(homeRefreshResult.message);
                }
            }
        });

        mNetLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(HomeViewModel.TAG_HOME_AD, netReqResult.tag)) {
                    if (netReqResult.successful) {
                        ResponseDataObject<HomeAdEntity> responseDataObject = (ResponseDataObject<HomeAdEntity>) netReqResult.data;
                        if (null != responseDataObject.data) {
                            Constant.searchAdEntity = responseDataObject.data.searchAdEntity;
                            centerEntities = responseDataObject.data.cetnerAdEntity;
                            alertEntitys = responseDataObject.data.alertAdEntity;
                            freeEntitys = responseDataObject.data.freeAdEntity;
                            showAlertDialog(alertEntitys, freeEntitys);
                        }
                    }
                } else if (TextUtils.equals(HomeViewModel.TAG_DAY_RECOMMEND, netReqResult.tag)) {
                    if (netReqResult.successful) {
                        recommendDayEntities = (List<RecommendDayEntity>) netReqResult.data;
                        setDayRecommend(recommendDayEntities);
                    }
                }
            }
        });

    }


    private void loadAll() {
        mHomeViewModel.refreshAll(mRefreshLiveData);
        if (null != recommendGoodFrags && !recommendGoodFrags.isEmpty()) {
            for (RecommendGoodFrag frag : recommendGoodFrags) {
                frag.refresh();
            }
        }

        //安全域名
        if (HomeClient.getSafeDomain().isEmpty()) {
            mHomeViewModel.getSafeDomain();
        }
    }


    private void initView() {
        mContainerLayout = findViewById(R.id.container_layout);
        mTbEmpowerLayout = findViewById(R.id.tb_empower_layout);
        mRed_envelope = findViewById(R.id.icon_red_envelope);
        mRed_envelope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTaskAdvertistEntity != null) {
                    homeStart(getActivity(), mTaskAdvertistEntity);
                }
            }
        });

        mTbEmpowerLayout.setOnClickListener(v -> {
            if (UserClient.isLogin()) {
                CommonUtil.showTBAuthDialog(getActivity());
            } else {
                LoginFragment.start(getActivity());
            }
        });


        refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadAll();
            }
        });
        CommonUtil.setRefreshHeaderWhiteText(refreshLayout);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        ImageView scroll_to_top = findViewById(R.id.scroll_to_top);
        scroll_to_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) layoutParams.getBehavior();
                if (behavior.getTopAndBottomOffset() != 0) {
                    behavior.setTopAndBottomOffset(0);
                    try {
                        recommendGoodFrags.get(selPos).scrollToTop();
                        scroll_to_top.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int pos) {
                if (pos > -540) {
                    scroll_to_top.setVisibility(View.INVISIBLE);
                } else {
                    scroll_to_top.setVisibility(View.VISIBLE);
                }
            }
        });

        viewFlipper = findViewById(R.id.flipper);

        mHomeRecyclerView = findViewById(R.id.home_recyclerView);
        mHomeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        indicatorView = findViewById(R.id.sbar_indicator);
        indicatorView.setEnabled(false);
        typeTabLayout = findViewById(R.id.type_tabs);
        OverScrollDecoratorHelper.setUpOverScroll(mHomeRecyclerView, OverScrollDecoratorHelper.ORIENTATION_HORIZONTAL);

        initBanner();

        recommedViewPager = findViewById(R.id.recommend_vp);

        itemSourceList = ItemSourceClient.getHomeItemSource();
        ItemSourceEntity itemSourceEntity = new ItemSourceEntity();
        itemSourceEntity.name = "为你推荐";
        itemSourceEntity.code = "";
        itemSourceList.add(0, itemSourceEntity);

        for (ItemSourceEntity it : itemSourceList) {
            TabLayout.Tab tab = typeTabLayout.newTab().setText(it.name);
            // 去掉点击背景
            LinearLayout tabView = tab.view;
            if (tabView != null) {
                tabView.setBackgroundColor(getColor(R.color.transparent));
            }
            typeTabLayout.addTab(tab);
        }

        typeTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == null || tab.getText() == null) {
                    return;
                }
                updateTabTextView(tab, true);
                if (tab.getPosition() < recommedViewPager.getChildCount()) {
                    recommedViewPager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab == null || tab.getText() == null) {
                    return;
                }
                updateTabTextView(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        TabLayout.Tab firstTab = typeTabLayout.getTabAt(0);
        if (firstTab != null) {
            firstTab.select();
            updateTabTextView(firstTab, true);
        }


        recommedViewPager = findViewById(R.id.recommend_vp);
        try {
            PagerAdapter adapter = recommedViewPager.getAdapter();
            if (null != adapter) {
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        recommPagerAdapter = new RecommPagerAdapter(getChildFragmentManager());
        recommedViewPager.setAdapter(recommPagerAdapter);
        recommedViewPager.setOffscreenPageLimit(itemSourceList.size() + 2);
        recommedViewPager.addOnPageChangeListener(new androidx.viewpager.widget.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                selectRecommendItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void selectRecommendItem(int pos) {
        selPos = pos;
        if (typeTabLayout != null) {
            TabLayout.Tab tab = typeTabLayout.getTabAt(pos);
            if (tab != null) {
                tab.select();
            }
        }
    }


    private void setData() {

        if (null != HomeFrag.layoutIndex) { //板块布局

            //顶部广告位，设置banner和金刚区
            if (HomeFrag.layoutIndex.page != null && HomeFrag.layoutIndex.page.top != null) {
                for (HomeLayoutEntity.Data data : HomeFrag.layoutIndex.page.top) {
                    if (TextUtils.equals(HomeViewType.TYPE_BANNER, data.type)) {
                        bannerEntitys = data.data;
                    } else if (TextUtils.equals(HomeViewType.TYPE_VAJRA, data.type)) {
                        vajraDistEntitys = data.data;
                    }
                }
            }

            setBanner(bannerEntitys);
            initChannel(vajraDistEntitys);

            //底部布局

            if (HomeFrag.layoutIndex.page != null && HomeFrag.layoutIndex.page.middle != null) {
                mContainerLayout.removeAllViews();
                // 1.所有的item都能设置背景和颜色，背景优先，颜色其次
                // 2.同类型之间小间距，不同类型之间大间距
                for (int i = 0; i < HomeFrag.layoutIndex.page.middle.size(); i++) {
                    int nextIndex = i + 1;
                    HomeLayoutEntity.Data currentData = HomeFrag.layoutIndex.page.middle.get(i); //当前item
                    if (nextIndex >= HomeFrag.layoutIndex.page.middle.size()) {
                        currentData.lineHeight = ConvertUtils.dp2px(10); //大间距
                    } else {
                        HomeLayoutEntity.Data nextData = HomeFrag.layoutIndex.page.middle.get(nextIndex); //当前item
                        if (TextUtils.equals(currentData.type, nextData.type)) {
                            currentData.lineHeight = ConvertUtils.dp2px(5); //小间距
                        } else {
                            currentData.lineHeight = ConvertUtils.dp2px(10); //大间距
                        }
                    }
                }
                for (HomeLayoutEntity.Data data : HomeFrag.layoutIndex.page.middle) {
                    // 广告长图
                    if (TextUtils.equals(HomeViewType.TYPE_THREE, data.type)) {
                        if (data.data != null && data.data.size() > 0) {
                            setAd(data);
                        }
                    }
                    // 样式4-1/2图-95高
                    if (TextUtils.equals(HomeViewType.TYPE_FOURE, data.type)) {
                        if (data.data != null && data.data.size() > 0) {
                            setHalfPicView(data, ConvertUtils.dp2px(95));
                        }
                    }
                    // 样式5-1/2图-85高
                    if (TextUtils.equals(HomeViewType.TYPE_FIVE, data.type)) {
                        if (data.data != null && data.data.size() > 0) {
                            setHalfPicView(data, ConvertUtils.dp2px(85));
                        }
                    }
                    // 大额券
                    if (TextUtils.equals(HomeViewType.TYPE_SIX, data.type)) {
                        if (data.data != null && data.data.size() > 0) {
                            setSixView(data);
                        }
                    }
                    ////样式7-2个商品
                    if (TextUtils.equals(HomeViewType.TYPE_SEVEN, data.type)) {
                        if (data.data != null && data.data.size() > 0) {
                            setSevenView(data);
                        }
                    }
                    // //样式8-1/4图
                    if (TextUtils.equals(HomeViewType.TYPE_EIGHT, data.type)) {
                        if (data.data != null && data.data.size() > 0) {
                            setEightView(data);
                        }
                    }
                }
            }
        }

        //每日推荐
        setDayRecommend(recommendDayEntities);

        if (null != alertEntitys || null != freeEntitys) {
            showAlertDialog(alertEntitys, freeEntitys);
        }
    }

    /**
     * //样式7-2个商品
     */
    private void setSevenView(HomeLayoutEntity.Data data) {
        // 设置背景图片，颜色以及间距
        FrameLayout frameLayout = getTypeLayout(data);
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false));
        HomeTypeSevenAdapter adapter = new HomeTypeSevenAdapter(data.data);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                homeStart(mContext, data.data.get(position));
            }
        });
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if (parent.getChildLayoutPosition(view) % 2 == 0) {
                    outRect.set(ConvertUtils.dp2px(10), 0, ConvertUtils.dp2px(2.5F), 0);
                } else {
                    outRect.set(ConvertUtils.dp2px(2.5F), 0, ConvertUtils.dp2px(10), 0);
                }
            }
        });
        frameLayout.addView(recyclerView);
        mContainerLayout.addView(frameLayout);
    }

    /**
     * 样式4-1/2图-95高
     * 样式5-1/2图-85高
     *
     * @param height
     */
    private void setHalfPicView(HomeLayoutEntity.Data data, int height) {
        // 设置背景图片，颜色以及间距
        FrameLayout frameLayout = getTypeLayout(data);
        // 设置样式图
        List<AdvertistEntity> advertistEntities = data.data;
        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        HomeTypeHalfAdapter adapter = new HomeTypeHalfAdapter(advertistEntities, height);
        List<AdvertistEntity> finalAdvertistEntities = advertistEntities;
        adapter.setOnItemClickListener((adapter1, view, position) -> homeStart(mContext, finalAdvertistEntities.get(position)));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int pos = parent.getChildLayoutPosition(view);
                if (pos % 2 == 0) {
                    outRect.set(ConvertUtils.dp2px(10), 0, ConvertUtils.dp2px(2.5F), ConvertUtils.dp2px(5F));
                } else {
                    outRect.set(ConvertUtils.dp2px(2.5F), 0, ConvertUtils.dp2px(10), ConvertUtils.dp2px(5F));
                }
            }
        });
        frameLayout.addView(recyclerView);
        mContainerLayout.addView(frameLayout);
    }

    private void showAlertDialog(List<AdvertistEntity> advertistEntities, List<AdvertistEntity> freeEntitys) {
        if (null != advertistEntities) {
            MainActivity activity = (MainActivity) getActivity();
            activity.setAd(advertistEntities, freeEntitys);
        }
    }

    private void setBanner(List<AdvertistEntity> bannerEntitys) {

        View bg_image = getActivity().findViewById(R.id.bg_image);
        View bg_top = getActivity().findViewById(R.id.bg_top);
        if (mBannerViewPager != null && mBannerViewPager.isStarted()) {
            mBannerViewPager.pauseAutoScroll();
        }
        resetBannerTopImage();

        if (null == bannerEntitys || bannerEntitys.isEmpty()) {
            mBanner.setVisibility(View.GONE);
            if (bg_image != null) {
                bg_image.setVisibility(View.GONE);
            }
            if (bg_top != null) {
                bg_top.getLayoutParams().height = ConvertUtils.dp2px(171);
            }
        } else {
            mBanner.setVisibility(View.VISIBLE);
            if (bg_image != null) {
                bg_image.setVisibility(View.VISIBLE);
            }
            if (bg_top != null) {
                bg_top.getLayoutParams().height = ConvertUtils.dp2px(191);
            }
            if (bannerEntitys.size() > 12) {
                bannerEntitys = bannerEntitys.subList(0, 12);
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

    private void setBannerTopImage(AdvertistEntity advertistEntity) {
        if (getActivity() != null) {
            ImageView bg_top = getActivity().findViewById(R.id.bg_top);
            if (bg_top != null) {
                bg_top.setImageDrawable(null);
                // 有图片先设置图片
                if (bg_top != null && !TextUtils.isEmpty(advertistEntity.bgImage)) {
                    GlideUtil.loadDetailPic(bg_top, advertistEntity.bgImage, R.drawable.icon_home_top_bg, R.drawable.icon_home_top_bg);
                }
            }
        }
    }

    private void resetBannerTopImage() {
        ImageView bg_top = getActivity().findViewById(R.id.bg_top);
        if (bg_top != null) {
            bg_top.setImageDrawable(null);
            bg_top.setBackgroundResource(R.drawable.self_balance_bg);
        }
    }

    /**
     * 样式8-1/4图
     *
     * @param data
     */
    private void setEightView(HomeLayoutEntity.Data data) {
        // 设置背景图片，颜色以及间距
        FrameLayout frameLayout = getTypeLayout(data);
        //只支持显示4条数据
//        if (data.data.size() > 4) {
//            data.data = data.data.subList(0, 2);
//        }
        RecyclerView recyclerView = new RecyclerView(mContext);
        // 设置间距
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int pos = parent.getChildLayoutPosition(view);
                int bottom = ConvertUtils.dp2px(5);
                if (pos >= (data.data.size() - 4)) {
                    bottom = 0;
                }
                if (pos % 4 == 0) {
                    outRect.set(ConvertUtils.dp2px(10), 0, 0, bottom);
                } else if ((pos + 1) % 4 == 0) {
                    outRect.set(ConvertUtils.dp2px(5), 0, ConvertUtils.dp2px(10), bottom);
                } else {
                    outRect.set(ConvertUtils.dp2px(5), 0, 0, bottom);
                }
//                } else if (pos == 3) {
//                    outRect.set(ConvertUtils.dp2px(5), 0, ConvertUtils.dp2px(10), 0);
//                } else {
//                    outRect.set(ConvertUtils.dp2px(5), 0, 0, 0);
//                }
            }
        });
        recyclerView.setLayoutManager(new FlowLayoutManager(mContext, false));
        HomeTypeEightAdapter adapter = new HomeTypeEightAdapter(data.data);
        adapter.setOnItemClickListener((adapter1, view, position) -> homeStart(mContext, data.data.get(position)));
        recyclerView.setAdapter(adapter);
        frameLayout.addView(recyclerView);
        mContainerLayout.addView(frameLayout);
    }

    /**
     * 大额券
     */
    private void setSixView(HomeLayoutEntity.Data data) {
        // 设置背景图片，颜色以及间距
        FrameLayout frameLayout = getTypeLayout(data);
        // 跳转大额券
        View mSixView = View.inflate(mContext, R.layout.home_type_six, null);
        TextView titleView = mSixView.findViewById(R.id.six_title);
        TextView subTitleView = mSixView.findViewById(R.id.six_sub_title);
        RecyclerView recyclerView = mSixView.findViewById(R.id.six_list);
        titleView.setVisibility(View.INVISIBLE);
        subTitleView.setVisibility(View.INVISIBLE);
        AdvertistEntity advertistEntity = data.data.get(0);
        mSixView.setOnClickListener(v -> homeStart(mContext, advertistEntity));
        if (advertistEntity.attribute != null) {
            if (!TextUtils.isEmpty(advertistEntity.attribute.title)) {
                titleView.setVisibility(View.VISIBLE);
                titleView.setText(advertistEntity.attribute.title);
                if (!TextUtils.isEmpty(advertistEntity.attribute.title_font_color) && advertistEntity.attribute.title_font_color.length() >= 7) {
                    titleView.setTextColor(Color.parseColor(advertistEntity.attribute.title_font_color));
                }
            }
            if (!TextUtils.isEmpty(advertistEntity.attribute.label)) {
                subTitleView.setVisibility(View.VISIBLE);
                subTitleView.setText(advertistEntity.attribute.label);
                if (!TextUtils.isEmpty(advertistEntity.attribute.label_font_color) && advertistEntity.attribute.label_font_color.length() >= 7) {
                    subTitleView.setTextColor(Color.parseColor(advertistEntity.attribute.label_font_color));
                    GradientDrawable gradientDrawable = (GradientDrawable) subTitleView.getBackground();
                    if (gradientDrawable != null) {
                        gradientDrawable.setStroke(ConvertUtils.dp2px(0.5F), Color.parseColor(advertistEntity.attribute.label_font_color));
                    }
                }

            }
        }
        if (advertistEntity.goodsList != null && advertistEntity.goodsList.size() > 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            // 最多10个
            if (advertistEntity.goodsList.size() > 10) {
                advertistEntity.goodsList = advertistEntity.goodsList.subList(0, 10);
            }
            // 最后再添加一个空view，用于润滑滑动效果
            advertistEntity.goodsList.add(advertistEntity.goodsList.get(0));

//            if (advertistEntity.goodsList.size() >= 10) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                //用来标记是否正在向最后一个滑动
                boolean isSlidingToLast = false;

                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    // 当不滚动时
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        //获取最后一个完全显示的ItemPosition
                        int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                        int totalItemCount = manager.getItemCount();
                        // 判断是否滚动到底部，并且是向右滚动
                        if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                            homeStart(mContext, advertistEntity);
                        }
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
                    if (dx > 0) {
                        //大于0表示正在向右滚动
                        isSlidingToLast = true;
                    } else {
                        //小于等于0表示停止或向左滚动
                        isSlidingToLast = false;
                    }
                }
            });
//            }
            HomeTypeSixAdapter homeTypeSixAdapter = new HomeTypeSixAdapter(advertistEntity.goodsList);
            homeTypeSixAdapter.setOnItemClickListener((adapter, view, position) -> {
                if (data.data.get(0).goodsList != null && data.data.get(0).goodsList.size() > 0) {
                    GoodsEntity goodsEntity = data.data.get(0).goodsList.get(position);
                    GoodsDetailActivity.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), goodsEntity.getItem_id());
                }
            });
            recyclerView.setAdapter(homeTypeSixAdapter);
        }
        frameLayout.addView(mSixView);
        mContainerLayout.addView(frameLayout);
    }


    /**
     * banner
     */
    private void initBanner() {
        mBanner = findViewById(R.id.banner_container);
        mBannerViewPager = findViewById(R.id.home_banner);
        mBannerViewPager.setPageMargin(DeviceUtil.dip2px(mContext, 7));
        mBannerViewPager.setOnPageChangeListener(new androidx.viewpager.widget.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
                // 当前页面不是推荐页，不要进行任何操作
                if (homePos != 0) {
                    mBannerViewPager.pauseAutoScroll();
                    return;
                }
                if (findViewById(R.id.scroll_to_top).getVisibility() == View.VISIBLE) {
                    return;
                }
                if (mBanner.getVisibility() == View.VISIBLE) {
                    // 获取当前界面颜色和下一页的颜色
                    List<AdvertistEntity> datas = mBannerViewPager.getDataList();
                    if (datas != null && datas.size() > 0) {
                        ArgbEvaluatorCompat evaluator = ArgbEvaluatorCompat.getInstance();
                        AdvertistEntity currentAd = datas.get(position);
                        AdvertistEntity nextAd = null;

                        if (position == datas.size() - 1) {
                            nextAd = datas.get(0);
                        } else {
                            nextAd = datas.get(position + 1);
                        }
                        int startValuate = 0; // 初始默认颜色（透明白）
                        int endValuate = 0;
                        if (!TextUtils.isEmpty(currentAd.bgColor) && currentAd.bgColor.length() >= 7) {
                            startValuate = Color.parseColor(currentAd.bgColor);
                        } else {
                            startValuate = Color.parseColor("#FF6E02");
                        }
                        if (!TextUtils.isEmpty(nextAd.bgColor) && nextAd.bgColor.length() >= 7) {
                            endValuate = Color.parseColor(nextAd.bgColor);
                        } else {
                            endValuate = Color.parseColor("#FF6E02");
                        }
                        int evaluate = 0;
                        if (startValuate == endValuate && startValuate == (Color.parseColor("#FF6E02"))) {
                            ImageView bg_top = getActivity().findViewById(R.id.bg_top);
                            bg_top.setBackgroundColor(endValuate);
                        } else {
                            evaluate = evaluator.evaluate(positionOffset, startValuate, endValuate);
                            ImageView bg_top = getActivity().findViewById(R.id.bg_top);
                            bg_top.setBackgroundColor(evaluate);
                        }
                    }
                } else {
                    resetBannerTopImage();
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (getActivity() == null || getActivity().isFinishing()) {
                    return;
                }
                // 当前页面不是推荐页，不要进行任何背景操作
                if (homePos != 0) {
                    mBannerViewPager.pauseAutoScroll();
                    return;
                }
                if (findViewById(R.id.scroll_to_top).getVisibility() == View.VISIBLE) {
                    return;
                }
                if (mBanner.getVisibility() == View.VISIBLE) {
                    if (mBannerViewPager.getDataList() != null && mBannerViewPager.getDataList().size() > 0) {
                        AdvertistEntity advertistEntity = (AdvertistEntity) mBannerViewPager.getDataList().get(position);
                        setBannerTopImage(advertistEntity);
                    }
                } else {
                    resetBannerTopImage();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mBannerViewPager.setImageLoader(new BannerImageLoader<ImageView, AdvertistEntity>() {
            @Override
            public void displayView(Context ctx, AdvertistEntity bannerEntity, ImageView imageView, int pos, int count) {
                bannerEntity.image = GlideUtil.checkUrl(bannerEntity.image);
                GlideUtil.loadBitmap(ctx, bannerEntity.image, new BitmapImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            Bitmap bitmap = resource;
                            int vw = LContext.screenWidth - DeviceUtil.dip2px(LContext.getContext(), 20);
                            int vh = (int) (bitmap.getHeight() * vw * 1.0f / (bitmap.getWidth() * 1.0f));

                            if (vh > ConvertUtils.dp2px(125F)) {
                                vh = ConvertUtils.dp2px(125);
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
                AdvertistEntity advertistEntity = (AdvertistEntity) mBannerViewPager.getDataList().get(position);
                mHomeViewModel.adClick(advertistEntity._id);
                homeStart(mContext, advertistEntity);
                SndoData.event(SndoData.XLT_EVENT_HOME_BANNER,
                        "banner_name", advertistEntity.name,
                        "banner_id", advertistEntity._id == null ? "null" : advertistEntity._id,
                        "url", advertistEntity.link_url == null ? "null" : advertistEntity.link_url,
                        "banner_rank", position + 1,
                        SndoData.XLT_ACTIVITY_TIME, "null"
                );
            }
        });

        mBannerViewPager.setInterval(3 * 1000);
        mBannerViewPager.startAutoScroll(2 * 1000);
    }

    private void initChannel(List<AdvertistEntity> advertistEntities) {

        if (null == advertistEntities || advertistEntities.isEmpty()) {
            indicatorView.setVisibility(View.GONE);
            mHomeRecyclerView.setVisibility(View.GONE);
            return;
        }

        mHomeRecyclerView.setVisibility(View.VISIBLE);

//        advertistEntities = advertistEntities.subList(0,13);
//        advertistEntities.addAll(advertistEntities);
        // 如果小于15个，则显示2行，大于等于15个，显示3行
        int spanCount = advertistEntities.size() < 15 ? 2 : 3;

        List<AdvertistEntity> topDataList = new ArrayList<>();
        List<AdvertistEntity> centerDataList = new ArrayList<>();
        List<AdvertistEntity> bottomDataList = new ArrayList<>();
        if (spanCount == 3) {
            for (int i = 0; i < advertistEntities.size(); i++) {
                AdvertistEntity advertistEntity = advertistEntities.get(i);
                if (i % 3 == 0) {
                    topDataList.add(advertistEntity);
                } else if (i % 3 == 1) {
                    centerDataList.add(advertistEntity);
                } else {
                    bottomDataList.add(advertistEntity);
                }
            }
            if (advertistEntities.size() <= 15) {
                indicatorView.setVisibility(View.GONE);
            } else {
                indicatorView.setVisibility(View.VISIBLE);
            }
        } else if (spanCount == 2) {
            for (int i = 0; i < advertistEntities.size(); i++) {
                if (i == 0) {
                    topDataList.add(advertistEntities.get(i));
                } else {
                    if (i % 2 != 0) {
                        centerDataList.add(advertistEntities.get(i));
                    } else {
                        topDataList.add(advertistEntities.get(i));
                    }
                }
            }
            if (advertistEntities.size() <= 10) {
                indicatorView.setVisibility(View.GONE);
            } else {
                indicatorView.setVisibility(View.VISIBLE);
            }
        }

        PlatePagerAdapter channelPagerAdapter = new PlatePagerAdapter(topDataList, centerDataList, bottomDataList);
        channelPagerAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                String title = "";
                switch (view.getId()) {
                    case R.id.topView:
                        title = topDataList.get(position).name;
                        jumpPlatePage(mContext, topDataList.get(position));
                        break;
                    case R.id.centerView:
                        title = centerDataList.get(position).name;
                        jumpPlatePage(mContext, centerDataList.get(position));
                        break;
                    case R.id.bottomView:
                        title = bottomDataList.get(position).name;
                        jumpPlatePage(mContext, bottomDataList.get(position));
                        break;
                }
                //深度数据汇报
                SndoData.event(SndoData.XLT_EVENT_JGQ,
                        "jgq_rank", position + 1,
                        SndoData.XLT_ACTIVITY_TIME, "null",
                        SndoData.XLT_ITEM_TITLE, title == null ? "null" : title
                );
            }
        });
        mHomeRecyclerView.setAdapter(channelPagerAdapter);
        mHomeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //显示区域的高度。
                int extent = mHomeRecyclerView.computeHorizontalScrollExtent();
                //整体的高度，注意是整体，包括在显示区域之外的。
                int range = mHomeRecyclerView.computeHorizontalScrollRange();
                //已经向下滚动的距离，为0时表示已处于顶部。
                int offset = mHomeRecyclerView.computeHorizontalScrollOffset();
                //此处获取seekbar的getThumb，就是可以滑动的小的滚动游标
                GradientDrawable gradientDrawable = (GradientDrawable) indicatorView.getThumb();
                //根据列表的个数，动态设置游标的大小，设置游标的时候，progress进度的颜色设置为和seekbar的颜色设置的一样的，所以就不显示进度了。
                mIndicW = extent / (spanCount == 2 ? 10 : 15);
                gradientDrawable.setSize(mIndicW, ConvertUtils.dp2px(2));
                //设置可滚动区域
                indicatorView.setMax((range - extent));
                if (dx == 0) {
                    indicatorView.setProgress(0);
                } else if (dx > 0) {
                    indicatorView.setProgress(offset);
                } else if (dx < 0) {
                    indicatorView.setProgress(offset);
                }
            }
        });
    }


    public void jumpPlatePage(Context ctx, AdvertistEntity advertistEntity) {
        //汇报
        StatisticInfo statisticInfo = new StatisticInfo();
        statisticInfo.viewPage(advertistEntity._id, null);


        DataCache.plate_first_id = advertistEntity._id;
        DataCache.plate_first_name = advertistEntity.name;

        mHomeViewModel.layoutIndexClick(advertistEntity._id);
        CommonUtil.startWebFrag(getActivity(), advertistEntity);
    }

    /**
     * 中心长图广告
     */
    private void setAd(HomeLayoutEntity.Data data) {
        // 设置背景图片，颜色以及间距
        View view = View.inflate(mContext, R.layout.home_type_ad, null);
        View adView = view.findViewById(R.id.ad_tip_view);
        adView.setBackgroundColor(Color.TRANSPARENT);
        if (!TextUtils.isEmpty(data.bgColor)) {
            if (data.bgColor.length() >= 7) {
                adView.setBackgroundColor(Color.parseColor(data.bgColor));
            }
        }
        mAdViewPager = view.findViewById(R.id.ad_banner);
        mAdViewPager.setImageLoader(new BannerImageLoader<ImageView, AdvertistEntity>() {
            @Override
            public void displayView(Context ctx, AdvertistEntity bannerEntity, ImageView imageView, int pos, int count) {
                bannerEntity.image = GlideUtil.checkUrl(bannerEntity.image);
                int vh = 0;
                if (bannerEntity.height > 0) {
                    vh = bannerEntity.height;
                } else {
                    vh = ConvertUtils.dp2px(90);
                }
                ViewGroup.LayoutParams layoutParams = mAdViewPager.getLayoutParams();
                if (bannerEntity.width > 0) {
                    float sc = ((float) ScreenUtils.getScreenWidth() * 1.0F) / bannerEntity.width;
                    layoutParams.height = (int) (vh * (sc));
                } else {
                    layoutParams.height = vh;
                }

                mAdViewPager.setLayoutParams(layoutParams);
                GlideUtil.loadPic(imageView, bannerEntity.image);
            }

            @Override
            public ImageView createView(Context ctx) {
                ImageView imageView = (ImageView) getLayoutInflater().inflate(R.layout.home_banner_item_two, null);
                return imageView;
            }
        });
        mAdViewPager.setmOnItemClickListener((parent, view1, position, id) -> {
            AdvertistEntity advertistEntity = data.data.get(position);
            if (!TextUtils.isEmpty(advertistEntity._id)) {
                mHomeViewModel.adClick(advertistEntity._id);
            }
            homeStart(mContext, advertistEntity);
        });
        mAdViewPager.setInterval(3 * 1000);
        mAdViewPager.startAutoScroll(2 * 1000);

        if (data.data.size() > 6) {
            data.data = data.data.subList(0, 6);
        }
        mAdViewPager.setDataList(data.data);
        AdvertistEntity advertistEntity = data.data.get(0);
        view.setOnClickListener(v -> {
            mHomeViewModel.adClick(advertistEntity._id);
            homeStart(mContext, advertistEntity);
            SndoData.reportAd(advertistEntity);
        });
//        frameLayout.addView(viewPager);
//        GlideUtil.loadPic(mThreeView, advertistEntity.image);
//        frameLayout.addView(view);

        mContainerLayout.addView(view);
    }

    @NotNull
    private FrameLayout getTypeLayout(HomeLayoutEntity.Data data) {
        FrameLayout frameLayout = new FrameLayout(mContext);
        if (!TextUtils.isEmpty(data.bgImage)) {
            Glide.with(mContext).load(data.bgImage).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    frameLayout.setBackground(resource);
                }
            });
        } else if (!TextUtils.isEmpty(data.bgColor)) {
            if (data.bgColor.length() >= 7) {
                frameLayout.setBackgroundColor(Color.parseColor(data.bgColor));
            }
        }
        FrameLayout.LayoutParams rootLayoutParams = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        frameLayout.setLayoutParams(rootLayoutParams);

        int lineHeight = data.lineHeight;
        if (TextUtils.equals(data.type, HomeViewType.TYPE_FOURE) || TextUtils.equals(data.type, HomeViewType.TYPE_FIVE)) {
            lineHeight = lineHeight - ConvertUtils.dp2px(5);
        }
        frameLayout.setPadding(0, 0, 0, lineHeight);
        return frameLayout;
    }


    /**
     * 每日推荐
     */
    private void setDayRecommend(List<RecommendDayEntity> recommendLists) {
        viewFlipper.removeAllViews();
        if (recommendLists == null || recommendLists.isEmpty()) {
            viewFlipper.setVisibility(View.GONE);
            return;
        }
        viewFlipper.setVisibility(View.VISIBLE);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        for (int i = 0; i < recommendLists.size(); i++) {
            RecommendDayEntity recommendDayEntity = recommendLists.get(i);
            TextView view = (TextView) layoutInflater.inflate(R.layout.recommend_day_item, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //商品详情
                    GoodsDetailActivity.Companion.start(mContext, recommendDayEntity._id, recommendDayEntity.item_source, recommendDayEntity.item_id);
                    SndoData.reportDayRecommend(recommendDayEntity, "首页");
                }
            });
            view.setText(recommendDayEntity.item_title);
            viewFlipper.addView(view);
        }
        viewFlipper.startFlipping();
    }

    //----------------------------------
    private class RecommPagerAdapter extends FragmentPagerAdapter {

        public RecommPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            RecommendGoodFrag frag = new RecommendGoodFrag();
            frag.setArguments(RecommendGoodFrag.getParam(itemSourceList.get(position).code));
            recommendGoodFrags.add(frag);
            return frag;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return getPagerCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

    private int getPagerCount() {
        if (itemSourceList == null) {
            return 0;
        }
        int dataSize = itemSourceList.size();
        int count = dataSize;
        return count;
    }

    @Override
    public void onFirstInit() {

    }

    @Override
    public void onLazyResume() {
        // 判断是否授权，没有则显示淘宝授权
        if (mTbEmpowerLayout != null) {
            TextView tv_emp = mTbEmpowerLayout.findViewById(R.id.tv_emp);
            TextView tv_btn = mTbEmpowerLayout.findViewById(R.id.tv_btn);
            ImageView icon_emp = mTbEmpowerLayout.findViewById(R.id.icon_emp);
            if (UserClient.isLogin() && UserClient.getUser().has_bind_tb == 1) {
                mTbEmpowerLayout.setVisibility(View.GONE);
                mRed_envelope.setVisibility(View.VISIBLE);
            } else {
                mTbEmpowerLayout.setVisibility(View.VISIBLE);
                mRed_envelope.setVisibility(View.GONE);
                if (UserClient.isLogin()) {
                    if (UserClient.getUser().has_bind_tb == 0) {
                        tv_emp.setText("一键授权，立享优惠权益");
                        tv_btn.setText("淘宝授权");
                        icon_emp.setImageResource(R.drawable.icon_empower);
                        mTbEmpowerLayout.setBackgroundResource(R.drawable.bg_home_empower);
                        tv_emp.setTextColor(Color.parseColor("#FF333333"));
                    } else {
                        mTbEmpowerLayout.setVisibility(View.GONE);
                    }
                } else {
                    tv_emp.setText("登录领取京东、淘宝大额优惠券");
                    tv_btn.setText("登录/注册");
                    icon_emp.setImageResource(R.drawable.icon_login);
                    mTbEmpowerLayout.setBackgroundResource(R.drawable.bg_home_login);
                    tv_emp.setTextColor(Color.parseColor("#FFFFFFFF"));
                }
            }
        }


        //TODO 如果授权界面不可见，且登录
        TextView bottom_tip_msg = findViewById(R.id.bottom_tip_msg);
        TextView bottom_tip_jump = findViewById(R.id.bottom_tip_jump);
        bottom_tip_msg.setText("asasasasasasas122112121212121221121212121212121212assasasasaassaasasas");
        bottom_tip_msg.requestFocus();


        //距离上次刷新超过30分钟后 自动刷新
        long now = System.currentTimeMillis();
        if (lastRefreshTime > 0 && (now - lastRefreshTime) >= AlarmManager.INTERVAL_HALF_HOUR) {
            loadAll();
        }
        resumeScroll();
        if (mBannerViewPager.getDataList() != null && mBannerViewPager.getDataList().size() > 0) {
            if (mBanner.getVisibility() == View.VISIBLE) {
                // 恢复banner背景
                AdvertistEntity advertistEntity = (AdvertistEntity) mBannerViewPager.getDataList().get(mBannerViewPager.getCurrentItem());
                ImageView bg_top = getActivity().findViewById(R.id.bg_top);
                if (bg_top != null) {
                    bg_top.setImageDrawable(null);
                    // 有图片先设置图片
                    if (!TextUtils.isEmpty(advertistEntity.bgImage)) {
                        GlideUtil.loadDetailPic(bg_top, advertistEntity.bgImage, R.drawable.icon_home_top_bg, R.drawable.icon_home_top_bg);
                    } else if (!TextUtils.isEmpty(advertistEntity.bgColor)) {
                        bg_top.setBackgroundColor(Color.parseColor(advertistEntity.bgColor));
                    }
                }
            } else {
                resetBannerTopImage();
            }
        }


    }

    @Override
    public void onLazyPause() {
        pauseScroll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (null != recommPagerAdapter) {
                recommPagerAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resumeScroll() {
        if (null != mBannerViewPager) {
            mBannerViewPager.resumeAutoScroll();
        }
        if (null != mAdViewPager) {
            mAdViewPager.resumeAutoScroll();
        }
    }

    private void pauseScroll() {
        if (null != mBannerViewPager) {
            mBannerViewPager.pauseAutoScroll();
        }
        if (null != mAdViewPager) {
            mAdViewPager.resumeAutoScroll();
        }
    }

    private void updateTabTextView(TabLayout.Tab tab, boolean flag) {
        String tabText = tab.getText().toString().trim();
        SpannableString spannableString = new SpannableString(tabText);
        StyleSpan styleSpan = null;
        if (flag) {
            styleSpan = new StyleSpan(Typeface.BOLD);
        } else {
            styleSpan = new StyleSpan(Typeface.NORMAL);
        }
        spannableString.setSpan(styleSpan, 0, tabText.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tab.setText(spannableString);
    }

    private void homeStart(Activity ctx, AdvertistEntity data) {
        mHomeViewModel.layoutIndexClick(data._id);
        SndoData.event(SndoData.XLT_EVENT_PLATE,
                SndoData.XLT_ACTIVITY_TIME, "null",
                SndoData.XLT_ITEM_SOURCE, data.item_source == null ? "null" : data.item_source,
                SndoData.XLT_ITEM_TITLE, data.name == null ? "null" : data.name
        );
        CommonUtil.startWebFrag(ctx, data);
    }
}
