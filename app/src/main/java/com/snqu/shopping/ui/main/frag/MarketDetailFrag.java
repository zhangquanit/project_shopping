package com.snqu.shopping.ui.main.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.data.home.entity.PlateCategoryEntity;
import com.snqu.shopping.data.home.entity.PlateOptions;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.main.adapter.CategoryListAdapter;
import com.snqu.shopping.ui.main.frag.channel.adapter.ChannelPagerAdapter;
import com.snqu.shopping.ui.main.view.FilterView;
import com.snqu.shopping.ui.main.view.FlitingCoverBar;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.statistics.DataCache;
import com.snqu.shopping.util.statistics.SndoData;

import java.util.ArrayList;
import java.util.List;

import common.widget.viewpager.ViewPager;

public class MarketDetailFrag extends SimpleFrag {
    private CategoryListAdapter mFloorListAdapter;
    private RecyclerView mFloorListView;
    private View bannerContainer;
    private HomeViewModel mHomeViewModel;

    private FilterView filterView;
    private ViewPager viewPager;
    private SmartRefreshLayout refreshLayout;
    private GoodsQueryParam queryParam = new GoodsQueryParam();


    private boolean showPostage;
    private static final String PARAM_ID = "PARAM_ID";
    private static final String PARAM_POSTAGE = "PARAM_PLATE12";
    private String plate;


    public static void start(Context ctx, PlateCategoryEntity plateInfo) {
        start(ctx, plateInfo, false);
    }

    public static void start(Context ctx, PlateCategoryEntity plateInfo, boolean showPostage) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_ID, plateInfo.plate_id);
        bundle.putInt(PARAM_POSTAGE, showPostage ? 1 : 0);
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam(plateInfo.name, MarketDetailFrag.class, bundle).showBg());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.market_detail_frag;
    }


    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, false, getTitleBar());

        plate = getArguments().getString(PARAM_ID);
        showPostage = getArguments().getInt(PARAM_POSTAGE) == 1;
        queryParam.plate = plate;

        initView();
        initData();
    }

    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);


        mHomeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_PLATE_LIST: //分类列表
                        if (netReqResult.successful) {
                            ResponseDataArray<CategoryEntity> dataArray = (ResponseDataArray<CategoryEntity>) netReqResult.data;
                            List<CategoryEntity> categoryEntities = dataArray.data;
                            if (categoryEntities.isEmpty()) {
                                bannerContainer.setVisibility(View.GONE);
                                AppBarLayout appBarLayout = findViewById(R.id.appbar);
                                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                                layoutParams.setBehavior(null);
                                appBarLayout.setLayoutParams(layoutParams);

                            } else {
                                bannerContainer.setVisibility(View.VISIBLE);
                                setChannelList(categoryEntities);
                            }
                        } else {

                        }
                        break;
                    case HomeViewModel.TAG_GOO0D_LIST: //商品列表
                        mFloorListAdapter.setEnableLoadMore(true);
                        refreshLayout.finishRefresh(netReqResult.successful);
                        if (netReqResult.successful) {
                            ResponseDataArray<GoodsEntity> goodsData = (ResponseDataArray<GoodsEntity>) netReqResult.data;
                            if (queryParam.page == 1) {
                                mFloorListAdapter.setNewData(goodsData.getDataList());
                            } else if (!goodsData.getDataList().isEmpty()) {
                                mFloorListAdapter.addData(goodsData.getDataList());
                            }

                            if (goodsData.hasMore()) {
                                queryParam.page++;
                                mFloorListAdapter.loadMoreComplete(); //刷新成功
                            } else {
                                mFloorListAdapter.loadMoreEnd(queryParam.page == 1);//无下一页
                            }
                        } else {
                            if (queryParam.page > 1) {
                                mFloorListAdapter.loadMoreFail();
                            } else {
                                ToastUtil.show(netReqResult.message);
                            }
                        }
                        break;
                    case HomeViewModel.TAG_PLATE_OPTIONS:
                        if (netReqResult.successful) {
                            ResponseDataObject<PlateOptions> dataObject = (ResponseDataObject<PlateOptions>) netReqResult.data;
                            if (dataObject.isSuccessful()) {
                                coverBar.setItemSources(dataObject.data);
                            }
                        }
                        break;
                }
            }
        });

        loadData();

    }

    private void loadData() {
        mHomeViewModel.getPlateList(plate);
        mHomeViewModel.getPlateOptions(plate);
        loadGoods();
    }

    private void loadGoods() {
        mFloorListAdapter.setEnableLoadMore(false);
        mHomeViewModel.getGoodList(queryParam);
    }

    FlitingCoverBar coverBar;

    private void initView() {
        getTitleBar().setTitleTextColor(R.color.white);
        getTitleBar().setLeftBtnDrawable(R.drawable.back_white);

        bannerContainer = findViewById(R.id.viewpager_container);

        coverBar = CommonUtil.getCoverBar(getActivity());
        coverBar.setCoverBarListener(new FlitingCoverBar.CoverBarListener() {
            @Override
            public void sure(String item_source, int postage, String minPrice, String maxPrice) {
                    queryParam.item_source = item_source;
                    queryParam.postage = postage;
                    queryParam.start_price = minPrice;
                    queryParam.end_price = maxPrice;
                    queryParam.page = 1;
                    loadGoods();
                }
        });

        refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                queryParam.page = 1;
                loadGoods();
            }
        });

        filterView = findViewById(R.id.filterview);
        filterView.setOnItemClickListener(new FilterView.OnItemClickListener() {
            @Override
            public void filtrate() {
                coverBar.show();
            }

            @Override
            public void onFilter(GoodsQueryParam.Sort sort) {
                queryParam.sort = sort;
                queryParam.page = 1;
                loadGoods();
            }
        });

        findViewById(R.id.iv_coupon_switch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                queryParam.has_coupon = v.isSelected() ? 1 : 0;
                queryParam.page = 1;
                loadGoods();
                SndoData.event(SndoData.XLT_EVENT_FILTER_COUPON);
            }
        });

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        ImageView scroll_to_top = findViewById(R.id.scroll_to_top);
        scroll_to_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) layoutParams.getBehavior();
                if (behavior.getTopAndBottomOffset() != 0) {
                    behavior.setTopAndBottomOffset(0);
                    scroll_to_top.setVisibility(View.GONE);
                }
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int pos) {
                if (pos > -400) {
                    scroll_to_top.setVisibility(View.GONE);
                } else {
                    scroll_to_top.setVisibility(View.VISIBLE);
                }
            }
        });

        viewPager = findViewById(R.id.viewPager);

        mFloorListView = findViewById(R.id.listview);

//        GridSpaceItemDecoration dividerItemDecoration = new GridSpaceItemDecoration(DeviceUtil.dip2px(mContext, 10));
//        mFloorListView.addItemDecoration(dividerItemDecoration);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        mFloorListView.setLayoutManager(gridLayoutManager);

        mFloorListAdapter = new CategoryListAdapter();
        mFloorListView.setAdapter(mFloorListAdapter);
        mFloorListAdapter.showPostage(showPostage);
        mFloorListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goodsEntity = mFloorListAdapter.getData().get(position);
                GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), plate, null, 0, goodsEntity);
                DataCache.reportGoodsByPlate(goodsEntity, position);
            }
        });


        mFloorListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadGoods();
            }
        }, mFloorListView);
    }


    private void setChannelList(List<CategoryEntity> categoryEntities) {
        int count = categoryEntities.size();
        findViewById(R.id.holderView).setVisibility(View.VISIBLE);
        List<List<CategoryEntity>> dataList = new ArrayList<>();
        if (count >= 8) {
            dataList.add(categoryEntities.subList(0, 8));
            dataList.add(categoryEntities.subList(8, categoryEntities.size()));
        } else {
            dataList.add(categoryEntities);
        }

        if (count <= 4) {
            ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
            layoutParams.height = ChannelPagerAdapter.getLineHeight();
            viewPager.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
            layoutParams.height = ChannelPagerAdapter.getTwoLineHeight();
            viewPager.setLayoutParams(layoutParams);
        }

        ChannelPagerAdapter channelPagerAdapter = new ChannelPagerAdapter(mContext, dataList);
        viewPager.setAdapter(channelPagerAdapter);
    }
}