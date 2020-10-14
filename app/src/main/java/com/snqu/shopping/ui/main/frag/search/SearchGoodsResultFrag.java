package com.snqu.shopping.ui.main.frag.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.SimpleFrag;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.main.adapter.GoodListAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.view.FilterView;
import com.snqu.shopping.ui.main.view.FlitingCoverBar;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.RecycleViewScrollToTop;
import com.snqu.shopping.util.statistics.SndoData;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果——商品
 *
 * @author 张全
 */
public class SearchGoodsResultFrag extends SimpleFrag {
    SmartRefreshLayout refreshLayout;
    View empty_banner, spaceView;
    GoodListAdapter adapter;
    private HomeViewModel mHomeViewModel;

    private View iv_coupon_switch;
    private FlitingCoverBar coverBar;
    private FilterView filterView;

    private GoodsQueryParam queryParam = new GoodsQueryParam();
    private boolean isSearchListEmpty; //搜索列表为空

    private LoadingStatusView loadingStatusView;
    private View header;
    private int row = 4;//后台bug，所以定为5条就不请求下一条


    @Override
    protected int getLayoutId() {
        return R.layout.search_result_goods_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initView() {
        coverBar = CommonUtil.getCoverBar(getActivity());
        coverBar.setCoverBarListener((item_source, postage, minPrice, maxPrice) -> {
            queryParam.postage = postage;
            queryParam.start_price = minPrice;
            queryParam.end_price = maxPrice;
            queryParam.page = 1;
            mHomeViewModel.searchGoods(queryParam);
        });
        filterView = findViewById(R.id.filterview);
        filterView.setOnItemClickListener(new FilterView.OnItemClickListener() {
            @Override
            public void filtrate() {
                coverBar.searchShow();
            }

            @Override
            public void onFilter(GoodsQueryParam.Sort sort) {
                queryParam.sort = sort;
                queryParam.page = 1;
                loadData();
            }
        });
        refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                queryParam.page = 1;
                loadData();
            }
        });

        loadingStatusView = findViewById(R.id.loadingStatusView);
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLoadingStatus(LoadingStatusView.Status.LOADING);
                loadData();
            }
        });

        header = LayoutInflater.from(mContext).inflate(R.layout.search_result_goods_header, null);

        iv_coupon_switch = header.findViewById(R.id.iv_coupon_switch);
        iv_coupon_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = !iv_coupon_switch.isSelected();
                iv_coupon_switch.setSelected(isSelected);
                queryParam.has_coupon = isSelected ? 1 : 0;
                queryParam.page = 1;
                mHomeViewModel.searchGoods(queryParam);
                SndoData.event(SndoData.XLT_EVENT_FILTER_COUPON);
            }
        });
        empty_banner = header.findViewById(R.id.empty_banner);
        spaceView = header.findViewById(R.id.space);

        RecyclerView recyclerView = findViewById(R.id.goods_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        adapter = new GoodListAdapter();
        adapter.addHeaderView(header);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goodsEntity = SearchGoodsResultFrag.this.adapter.getData().get(position);
                GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), goodsEntity);

                try {
                    // 深度数据汇报
                    SndoData.event(
                            SndoData.XLT_EVENT_SEARCHRESULTCLICK,
                            "search_way", "null",
                            SndoData.XLT_ITEM_SEARCH_KEYWORD, "null",
                            SndoData.XLT_ITEM_SOURCE, goodsEntity.getItem_source() == null ? "null" : goodsEntity.getItem_source(),
                            SndoData.XLT_GOOD_ID, goodsEntity.getGoods_id(),
                            "key_word_type", "null",
                            "position_number", position,
                            "xlt_item_firstcate_title", "null",
                            "xlt_item_thirdcate_title", "null",
                            "xlt_item_secondcate_title", "null",
                            "good_name", goodsEntity.getItem_title()
                    );
                } catch (Exception e) {

                }
            }
        });

        adapter.setLoadMoreView(new CommonLoadingMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, recyclerView);


//        loadingStatusView = new LoadingStatusView(mContext);
//        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadData();
//            }
//        });
//        adapter.setEmptyView(loadingStatusView);

    }


    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                refreshLayout.finishRefresh(netReqResult.successful);

                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_SEARCH_GOODS: //搜索列表

                        if (netReqResult.successful) {
                            loadingStatusView.setVisibility(View.GONE);

                            ResponseDataArray<GoodsEntity> searchData = (ResponseDataArray<GoodsEntity>) netReqResult.data;
                            List<GoodsEntity> dataList = searchData.getDataList();

                            if (queryParam.page == 1) {
                                refreshUI(dataList); //空数据
                            }

                            if (queryParam.page == 1 && dataList.isEmpty()) {
                                //无搜索列表
                                isSearchListEmpty = true;
                                loadData();
                                return;
                            }


                            if (queryParam.page == 1) {
                                adapter.setNewData(dataList);
                            } else if (!dataList.isEmpty()) {
                                adapter.addData(dataList);
                            }
                            if (row > dataList.size()) { //无下一页
                                adapter.loadMoreEnd(true);
                            } else {
                                queryParam.page++;
                                adapter.loadMoreComplete();
                            }
                        } else {
                            if (queryParam.page > 1) { //加载下一页数据失败
                                adapter.loadMoreFail();
                            } else { //下拉刷新失败
                                ToastUtil.show(netReqResult.message);
                                updateLoadingStatus(LoadingStatusView.Status.FAIL);
                            }
                        }
                        break;
                    case HomeViewModel.TAG_RECOMMEND_GOODS: //推荐列表
                        if (netReqResult.successful) {
                            loadingStatusView.setVisibility(View.GONE);

                            ResponseDataArray<GoodsEntity> recommendData = (ResponseDataArray<GoodsEntity>) netReqResult.data;
                            List<GoodsEntity> dataList = recommendData.getDataList();

                            if (queryParam.page == 1) {
                                adapter.setNewData(dataList);
                            } else if (!dataList.isEmpty()) {
                                adapter.addData(dataList);
                            }
                            if (row > dataList.size()) { //无下一页
                                adapter.loadMoreEnd(true);
                            } else {
                                queryParam.page++;
                                adapter.loadMoreComplete();
                            }
                        } else {
                            if (queryParam.page > 1) { //加载下一页数据失败
                                adapter.loadMoreFail();
                            } else { //下拉刷新失败
                                ToastUtil.show(netReqResult.message);
                                updateLoadingStatus(LoadingStatusView.Status.FAIL);
                            }
                        }
                        break;
                }
            }
        });

    }

    private void refreshUI(List<GoodsEntity> dataList) {
        if (null == dataList || dataList.isEmpty()) {
            spaceView.setVisibility(View.GONE);
            empty_banner.setVisibility(View.VISIBLE);
        } else {
            spaceView.setVisibility(View.VISIBLE);
            empty_banner.setVisibility(View.GONE);
        }
    }

    private void resetUI(String item_source) {
        spaceView.setVisibility(View.GONE);
        empty_banner.setVisibility(View.GONE);
        iv_coupon_switch.setSelected(false);//优惠券开关
        coverBar.resetUI(); //过滤
        filterView.resetUI();

        if (TextUtils.equals(item_source, Constant.BusinessType.S)) {
            filterView.setSuningSearchStyle();
        } else if (TextUtils.equals(item_source, Constant.BusinessType.PDD)) {
            filterView.setPddSearchStyle();
        } else {
            filterView.setTbSearchStyle();
        }

        if (header != null) {
            View layout = header.findViewById(R.id.rl_coupon_layout);
            if (layout != null) {
                //苏宁唯品会不显示优惠券
                if (TextUtils.equals(item_source, Constant.BusinessType.V) || TextUtils.equals(item_source, Constant.BusinessType.S)) {
                    layout.setVisibility(View.GONE);
                } else {
                    layout.setVisibility(View.VISIBLE);
                }
            }
        }

        adapter.setNewData(new ArrayList<>());
        loadingStatusView.setVisibility(View.GONE);
    }


    public void startSearch(String keyword, String good_id, String item_source) {
        queryParam = new GoodsQueryParam();
        queryParam.search = keyword;
        queryParam.goods_id = good_id;
        queryParam.item_source = item_source;
        isSearchListEmpty = false;
        resetUI(item_source);
        updateLoadingStatus(LoadingStatusView.Status.LOADING);
        mHomeViewModel.searchGoods(queryParam);
    }

    private void loadData() {
        if (isSearchListEmpty) { //推荐列表
            updateLoadingStatus(LoadingStatusView.Status.LOADING);
            mHomeViewModel.getRecommendGoods(queryParam);
        } else { //搜索列表
            mHomeViewModel.searchGoods(queryParam);
        }
    }

    private void updateLoadingStatus(LoadingStatusView.Status status) {
        if (adapter.getData().isEmpty()) {
            loadingStatusView.setStatus(status);
        }
    }
}
