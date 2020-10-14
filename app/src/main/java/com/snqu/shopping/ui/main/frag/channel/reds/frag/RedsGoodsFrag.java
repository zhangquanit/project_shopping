package com.snqu.shopping.ui.main.frag.channel.reds.frag;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.SimpleFrag;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.makeramen.roundedimageview.RoundedImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.data.red.entity.RedGoodeEntity;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.main.frag.channel.reds.RedViewModel;
import com.snqu.shopping.ui.main.frag.channel.reds.adapter.RedGoodsAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.RecycleViewScrollToTop;
import com.snqu.shopping.util.statistics.DataCache;

import java.util.List;

import common.widget.viewpager.BannerImageLoader;
import common.widget.viewpager.BannerViewPager;

/**
 * 好物说
 */
public class RedsGoodsFrag extends SimpleFrag {
    private RedGoodsAdapter redGoodsAdapter;
    private SmartRefreshLayout refreshLayout;
    private BannerViewPager mBannerViewPager;
    private View mBanner;
    private GoodsQueryParam queryParam = new GoodsQueryParam();
    private RedViewModel mRedViewModel;
    private HomeViewModel homeViewModel;

    private LoadingStatusView loadingStatusView;

    @Override
    protected int getLayoutId() {
        return R.layout.reds_goods_frag;
    }


    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                queryParam.page = 1;
                loadAll();
            }
        });

        View headView = LayoutInflater.from(mContext).inflate(R.layout.reds_goods_header, null);
        initBanner(headView);

        RecyclerView recyclerView = findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.rv_list_divider_15));
        recyclerView.addItemDecoration(dividerItemDecoration);

        redGoodsAdapter = new RedGoodsAdapter();
        redGoodsAdapter.addHeaderView(headView);
        recyclerView.setAdapter(redGoodsAdapter);
        //
        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        redGoodsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                RedGoodeEntity redGoodeEntity = redGoodsAdapter.getData().get(position);
                GoodsDetailActivity.Companion.start(mContext, redGoodeEntity.good_info.get_id(), redGoodeEntity.good_info.getItem_source(), CommonUtil.PLATE, CommonUtil.PLATE_CHILD, 1, redGoodeEntity.item_id);
                DataCache.reportGoodsByPlate(redGoodeEntity.good_info, position);
            }
        });

        redGoodsAdapter.setLoadMoreView(new CommonLoadingMoreView());
        redGoodsAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, recyclerView);

        loadingStatusView = new LoadingStatusView(mContext);
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        redGoodsAdapter.setEmptyView(loadingStatusView);

    }

    private void initBanner(View headView) {

        mBanner = headView.findViewById(R.id.banner_container);
        mBannerViewPager = headView.findViewById(R.id.viewpager);
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

    private void initData() {
        mRedViewModel =
                ViewModelProviders.of(this).get(RedViewModel.class);
        mRedViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case RedViewModel.TAG_HAOWU:
                        redGoodsAdapter.setEnableLoadMore(true);
                        refreshLayout.finishRefresh(netReqResult.successful);
                        if (netReqResult.successful) {
                            ResponseDataArray<RedGoodeEntity> goodsData = (ResponseDataArray<RedGoodeEntity>) netReqResult.data;
                            if (queryParam.page == 1) {
                                redGoodsAdapter.setNewData(goodsData.getDataList());
                            } else if (!goodsData.getDataList().isEmpty()) {
                                redGoodsAdapter.addData(goodsData.getDataList());
                            }

                            if (goodsData.hasMore()) {
                                queryParam.page++;
                                redGoodsAdapter.loadMoreComplete(); //刷新成功
                            } else {
                                redGoodsAdapter.loadMoreEnd(queryParam.page == 1);//无下一页
                            }

                            if (queryParam.page == 1 && goodsData.getDataList().isEmpty()) { //第一页 无数据
                                LoadingStatusView.Status status = LoadingStatusView.Status.EMPTY;
                                loadingStatusView.setStatus(status);
                                loadingStatusView.setText("暂无数据~");
                            }
                        } else {
                            if (queryParam.page > 1) { //加载下一页数据失败
                                redGoodsAdapter.loadMoreFail();
                            } else if (redGoodsAdapter.getData().isEmpty()) { //第一页  无数据
                                LoadingStatusView.Status status = LoadingStatusView.Status.FAIL;
                                loadingStatusView.setStatus(status);
                            } else { //下拉刷新失败
                                ToastUtil.show(netReqResult.message);
                            }
                        }
                        break;
                }
            }
        });

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_AD:
                        if (netReqResult.successful) {
                            //banner广告
                            ResponseDataArray<AdvertistEntity> responseDataArray = (ResponseDataArray<AdvertistEntity>) netReqResult.data;
                            List<AdvertistEntity> bannerEntitys = responseDataArray.getDataList();
                            if (null == bannerEntitys || bannerEntitys.isEmpty()) {
                                mBanner.setVisibility(View.GONE);
                            } else {
                                mBanner.setVisibility(View.VISIBLE);
                                if (bannerEntitys.size() > 6) {
                                    bannerEntitys = bannerEntitys.subList(0, 6);
                                }
                                mBannerViewPager.setDataList(bannerEntitys);
                            }
                        }
                        break;
                }
            }
        });

//        loadAll();
    }

    private void loadAll() {
        homeViewModel.getAdList("9");
        loadData();
    }

    private void loadData() {
        redGoodsAdapter.setEnableLoadMore(false);
        mRedViewModel.getHaoWuList(queryParam.page);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mBannerViewPager.stopAutoScroll();
    }

    @Override
    public void restorePage() {
        initData = true;
        loadAll();
        if (null != mBannerViewPager) {
            mBannerViewPager.resumeAutoScroll();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && !initData) {
            initData = true;
            loadAll();
        }

        if (null != mBannerViewPager) {
            if (!hidden) {
                mBannerViewPager.resumeAutoScroll();
            } else {
                mBannerViewPager.pauseAutoScroll();
            }
        }
    }

    private boolean initData;
}
