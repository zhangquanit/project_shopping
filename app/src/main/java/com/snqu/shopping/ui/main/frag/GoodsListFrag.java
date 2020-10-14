package com.snqu.shopping.ui.main.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.data.home.entity.ItemSourceEntity;
import com.snqu.shopping.data.home.entity.PlateOptions;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.main.adapter.CategoryListAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.view.FilterView;
import com.snqu.shopping.ui.main.view.FlitingCoverBar;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.RecycleViewScrollToTop;
import com.snqu.shopping.util.statistics.DataCache;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品列表
 *
 * @author 张全
 */
public class GoodsListFrag extends SimpleFrag {
    private SmartRefreshLayout smartRefreshLayout;
    private CategoryListAdapter adapter;
    private HomeViewModel mHomeViewModel;
    private static final String PARAM_CATEGORY = "PARAM";
    private static final String PARAM_PLATE = "PARAM_PLATE";
    GoodsQueryParam queryParam = new GoodsQueryParam();
    private LoadingStatusView loadingStatusView;
    private FlitingCoverBar coverBar;
    private CategoryEntity channelEntity;
    private List<ItemSourceEntity> itemSources = new ArrayList<>();

    /**
     * 分类列表
     *
     * @param ctx
     * @param id
     * @param name
     */
    public static void start(Context ctx, String id, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_CATEGORY, id);
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam(name, GoodsListFrag.class, bundle).showBg());
    }

    /**
     * 子板块商品列表
     *
     * @param ctx
     */
    public static void startForPlate(Context ctx, CategoryEntity channelEntity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_PLATE, channelEntity);
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam(channelEntity.getName(), GoodsListFrag.class, bundle).showBg());
    }


    @Override
    protected int getLayoutId() {
        return R.layout.goods_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, false, getTitleBar());

        Bundle arguments = getArguments();

        if (arguments.containsKey(PARAM_PLATE)) {
            channelEntity = (CategoryEntity) arguments.getSerializable(PARAM_PLATE);
            queryParam.plate = channelEntity._id;
        } else if (arguments.containsKey(PARAM_CATEGORY)) {
            queryParam.category = arguments.getString(PARAM_CATEGORY);
        }

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
                    case HomeViewModel.TAG_PLATE_OPTIONS:
                        if (netReqResult.successful) {
                            ResponseDataObject<PlateOptions> dataObject = (ResponseDataObject<PlateOptions>) netReqResult.data;
                            if (dataObject.isSuccessful()) {
                                coverBar.setItemSources(dataObject.data);
                            }
                        }
                        break;
                    case HomeViewModel.TAG_GOO0D_LIST:
                        smartRefreshLayout.finishRefresh(netReqResult.successful);
                        if (netReqResult.successful) {
                            ResponseDataArray<GoodsEntity> goodsData = (ResponseDataArray<GoodsEntity>) netReqResult.data;
                            if (queryParam.page == 1) {
                                adapter.setNewData(goodsData.getDataList());
                            } else if (!goodsData.getDataList().isEmpty()) {
                                adapter.addData(goodsData.getDataList());
                            }

                            if (goodsData.hasMore()) {
                                queryParam.page++;
                                adapter.loadMoreComplete(); //刷新成功
                            } else {
                                adapter.loadMoreEnd(queryParam.page == 1);//无下一页
                            }

                            if (queryParam.page == 1 && goodsData.getDataList().isEmpty()) { //第一页 无数据
                                LoadingStatusView.Status status = LoadingStatusView.Status.EMPTY;
                                loadingStatusView.setStatus(status);
                                loadingStatusView.setText("暂无数据");
                            }

                            if (null != channelEntity) { //板块商品
                                DataCache.reportGoodsCountByPlate(adapter.getData().size());
                            } else { //分类商品
                                DataCache.reportGoodsCountByCategory(adapter.getData().size());
                            }

                        } else {
                            if (queryParam.page > 1) { //加载下一页数据失败
                                adapter.loadMoreFail();
                            } else if (adapter.getData().isEmpty()) { //第一页  无数据
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

        if (null != channelEntity) {
            mHomeViewModel.getPlateOptions(channelEntity._id);
        }

        loadData();
    }

    private void loadData() {
        mHomeViewModel.getGoodList(queryParam);
    }

    private void initView() {

        getTitleBar().setTitleTextColor(R.color.white);
        getTitleBar().setLeftBtnDrawable(R.drawable.back_white);

        coverBar = CommonUtil.getCoverBar(getActivity());
        coverBar.setCoverBarListener(new FlitingCoverBar.CoverBarListener() {
            @Override
            public void sure(String itemSource, int postage, String minPrice, String maxPrice) {
                if (getUserVisibleHint()) { //当前页面
                    itemSources.clear();
                    queryParam.item_source = itemSource;
                    queryParam.postage = postage;
                    queryParam.start_price = minPrice;
                    queryParam.end_price = maxPrice;
                    queryParam.page = 1;
                    loadData();
                }
            }
        });
        FilterView filterView = findViewById(R.id.filterview);
        filterView.setOnItemClickListener(new FilterView.OnItemClickListener() {
            @Override
            public void filtrate() {
//                coverBar.setSelectedItems(itemSources);
                coverBar.show();
            }

            @Override
            public void onFilter(GoodsQueryParam.Sort sort) {
                queryParam.sort = sort;
                queryParam.page = 1;
                loadData();
            }
        });
        smartRefreshLayout = findViewById(R.id.refreshlayout);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                queryParam.page = 1;
                loadData();
            }
        });

        RecyclerView mFloorListView = findViewById(R.id.listview);
//        GridSpaceItemDecoration dividerItemDecoration = new GridSpaceItemDecoration(DeviceUtil.dip2px(mContext, 10));
//        mFloorListView.addItemDecoration(dividerItemDecoration);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        mFloorListView.setLayoutManager(gridLayoutManager);


        adapter = new CategoryListAdapter();
        mFloorListView.setAdapter(adapter);

        RecycleViewScrollToTop.addScroolToTop(mFloorListView, findViewById(R.id.scroll_to_top));

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goodsEntity = GoodsListFrag.this.adapter.getData().get(position);
                if (null != channelEntity) {
                    GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), channelEntity.pid, channelEntity._id, 0, goodsEntity);
                    DataCache.reportGoodsByPlate(goodsEntity, position);
                } else {
                    GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), goodsEntity);
                    DataCache.reportGoodsByCategory(goodsEntity, position);
                }
            }
        });


        adapter.setLoadMoreView(new CommonLoadingMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, mFloorListView);

        loadingStatusView = new LoadingStatusView(mContext);
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        adapter.setEmptyView(loadingStatusView);
    }
}
