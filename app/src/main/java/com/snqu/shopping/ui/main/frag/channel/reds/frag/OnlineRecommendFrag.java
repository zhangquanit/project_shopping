package com.snqu.shopping.ui.main.frag.channel.reds.frag;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.LazyFragment;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.data.home.entity.RecommendDayEntity;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.main.frag.channel.adapter.RedListAdapter;
import com.snqu.shopping.ui.main.frag.channel.reds.RedViewModel;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.view.FlitingCoverBar;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.statistics.SndoData;

import java.util.List;

import common.widget.viewpager.BannerImageLoader;
import common.widget.viewpager.BannerViewPager;

/**
 * @author 张全
 */
public class OnlineRecommendFrag extends LazyFragment {
    public View mBanner;
    private BannerViewPager mBannerViewPager;
    private SmartRefreshLayout refreshLayout;
    private RedListAdapter adapter;

    private RedViewModel redViewModel;
    private HomeViewModel homeViewModel;
    private ViewFlipper viewFlipper;
    private GoodsQueryParam queryParam = new GoodsQueryParam();
    public MutableLiveData<NetReqResult> goodLiveData = new MutableLiveData<>();
    public MutableLiveData<NetReqResult> mRefreshData = new MutableLiveData<>();
    private RecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.reds_online_celebrity_recommend_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initView() {

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        ImageView scroll_to_top = findViewById(R.id.scroll_to_top);
        scroll_to_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) layoutParams.getBehavior();
                if (behavior.getTopAndBottomOffset() != 0) {
                    behavior.setTopAndBottomOffset(0);
                    recyclerView.scrollToPosition(0);
                    scroll_to_top.setVisibility(View.GONE);
                }
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int pos) {
                if (pos > -340) {
                    scroll_to_top.setVisibility(View.GONE);
                } else {
                    scroll_to_top.setVisibility(View.VISIBLE);
                }
            }
        });

        FlitingCoverBar coverBar = CommonUtil.getCoverBar(getActivity());
        coverBar.setCoverBarListener(new FlitingCoverBar.CoverBarListener() {
            @Override
            public void sure(String item_source, int postage, String minPrice, String maxPrice) {
                queryParam.item_source = item_source;
                queryParam.postage = postage;
                queryParam.start_price = minPrice;
                queryParam.end_price = maxPrice;
                queryParam.page = 1;
                getGoods();
            }
        });
        findViewById(R.id.tv_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coverBar.show();
            }
        });

        refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadAll();
            }
        });

        viewFlipper = findViewById(R.id.flipper);

        recyclerView = findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new RedListAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goodsEntity = (GoodsEntity) adapter.getData().get(position);
                GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), CommonUtil.PLATE, CommonUtil.PLATE_CHILD, 1, goodsEntity);
            }
        });

        adapter.setLoadMoreView(new CommonLoadingMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getGoods();
            }
        });

        initBanner();
    }

    private void initData() {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        redViewModel =
                ViewModelProviders.of(this).get(RedViewModel.class);
        mRefreshData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                adapter.setEnableLoadMore(true);
                refreshLayout.finishRefresh(netReqResult.successful);
                if (netReqResult.successful) {
                    RedViewModel.RedHomeData homeData = (RedViewModel.RedHomeData) netReqResult.data;

                    //banner广告
                    List<AdvertistEntity> bannerEntitys = homeData.advertistEntities;
                    if (null == bannerEntitys || bannerEntitys.isEmpty()) {
                        mBanner.setVisibility(View.GONE);
                    } else {
                        mBanner.setVisibility(View.VISIBLE);
                        if (bannerEntitys.size() > 6) {
                            bannerEntitys = bannerEntitys.subList(0, 6);
                        }
                        mBannerViewPager.setDataList(bannerEntitys);
                    }

                    //每日推荐
                    setDayRecommend(homeData.recommendDayEntities);

                    //推荐商品
                    ResponseDataArray<GoodsEntity> goodsData = homeData.goodList;
                    adapter.setNewData(goodsData.data);
                    if (goodsData.hasMore()) { //还有下一页数据 刷新完成
                        queryParam.page++;
                        adapter.loadMoreComplete();
                    } else { //到底啦
                        adapter.loadMoreEnd();
                    }
                } else {
                    ToastUtil.show(netReqResult.message);
                }
            }
        });


        goodLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (netReqResult.successful) {
                    ResponseDataArray<GoodsEntity> goodsData = (ResponseDataArray<GoodsEntity>) netReqResult.data;

                    if (queryParam.page == 1) {
                        adapter.setNewData(goodsData.getDataList());
                    } else if (!goodsData.getDataList().isEmpty()) {
                        adapter.addData(goodsData.getDataList());
                    }

                    if (goodsData.hasMore()) {
                        queryParam.page++;
                        adapter.loadMoreComplete();
                    } else {
                        adapter.loadMoreEnd();
                    }
                } else {
                    if (queryParam.page > 1) {
                        adapter.loadMoreFail();
                    }
                }
            }
        });
    }

    private void loadAll() {
        adapter.setEnableLoadMore(false);
        queryParam.page = 1;
        redViewModel.refreshAll(queryParam, mRefreshData);
    }

    private void getGoods() {
        redViewModel.getGoods(queryParam, goodLiveData);
    }

    private void initBanner() {
        mBanner = findViewById(R.id.banner_container);
        mBannerViewPager = findViewById(R.id.viewpager);
        mBannerViewPager.setPageMargin(DeviceUtil.dip2px(mContext, 7));
        mBannerViewPager.setImageLoader(new BannerImageLoader<ImageView, AdvertistEntity>() {
            @Override
            public void displayView(Context ctx, AdvertistEntity bannerEntity, ImageView imageView, int pos, int count) {

                GlideUtil.loadBitmap(getContext(), bannerEntity.image, new BitmapImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            Bitmap bitmap = resource;
                            int vw = LContext.screenWidth - DeviceUtil.dip2px(LContext.getContext(), 20);
                            int vh = (int) (bitmap.getHeight() * vw * 1.0f / (bitmap.getWidth() * 1.0f));

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
                AdvertistEntity data = (AdvertistEntity) mBannerViewPager.getDataList().get(position);
                homeViewModel.adClick(data._id);
                CommonUtil.startWebFrag(mContext, data);
            }
        });

        mBannerViewPager.setInterval(3 * 1000);
        mBannerViewPager.startAutoScroll(2 * 1000);
    }

    /**
     * 每日推荐
     */
    private void setDayRecommend(List<RecommendDayEntity> recommendLists) {
        viewFlipper.removeAllViews();
        if (recommendLists.isEmpty()) {
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
                    SndoData.reportDayRecommend(recommendDayEntity, "红人街");
                }
            });
            view.setText(recommendDayEntity.item_title);
            viewFlipper.addView(view);
        }
        viewFlipper.startFlipping();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mBannerViewPager.stopAutoScroll();
    }

    @Override
    public void onFirstInit() {
        loadAll();
    }

    @Override
    public void onLazyResume() {
        mBannerViewPager.resumeAutoScroll();
    }

    @Override
    public void onLazyPause() {
        mBannerViewPager.pauseAutoScroll();
    }
}
