package com.snqu.shopping.ui.main.frag.channel.reds.frag;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.SimpleFrag;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.entity.ShopItemEntity;
import com.snqu.shopping.ui.main.frag.channel.reds.RedViewModel;
import com.snqu.shopping.ui.main.frag.channel.reds.adapter.RedShopAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.util.RecycleViewScrollToTop;

/**
 * 网红店
 */
public class RedsShopFrag extends SimpleFrag {
    private RedShopAdapter redShopAdapter;
    private SmartRefreshLayout refreshLayout;

    private GoodsQueryParam queryParam = new GoodsQueryParam();
    private RedViewModel redViewModel;

    private LoadingStatusView loadingStatusView;

    @Override
    protected int getLayoutId() {
        return R.layout.reds_shop_frag;
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
                loadData();
            }
        });
        RecyclerView recyclerView = findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.rv_list_divider_15));
        recyclerView.addItemDecoration(dividerItemDecoration);

        //
        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        redShopAdapter = new RedShopAdapter();
        redShopAdapter.reportPlate(true);
        recyclerView.setAdapter(redShopAdapter);

        redShopAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                String seller_shop_id = redShopAdapter.getData().get(position).seller_shop_id;
                String item_source = redShopAdapter.getData().get(position).seller_type;
                switch (view.getId()) {
                    case R.id.item_detail: //进入店铺
                    case R.id.item_top:
                        ShopDetialFrag.start(mContext, seller_shop_id, item_source);
                        break;
                }
            }
        });

        redShopAdapter.setLoadMoreView(new CommonLoadingMoreView());
        redShopAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
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
        redShopAdapter.setEmptyView(loadingStatusView);
    }

    private void initData() {
        redViewModel =
                ViewModelProviders.of(this).get(RedViewModel.class);
        redViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case RedViewModel.TAG_SHOP:
                        redShopAdapter.setEnableLoadMore(true);
                        refreshLayout.finishRefresh(netReqResult.successful);
                        if (netReqResult.successful) {
                            ResponseDataArray<ShopItemEntity> goodsData = (ResponseDataArray<ShopItemEntity>) netReqResult.data;
                            if (queryParam.page == 1) {
                                redShopAdapter.setNewData(goodsData.getDataList());
                            } else if (!goodsData.getDataList().isEmpty()) {
                                redShopAdapter.addData(goodsData.getDataList());
                            }

                            if (goodsData.hasMore()) {
                                queryParam.page++;
                                redShopAdapter.loadMoreComplete(); //刷新成功
                            } else {
                                redShopAdapter.loadMoreEnd(queryParam.page == 1);//无下一页
                            }

                            if (queryParam.page == 1 && goodsData.getDataList().isEmpty()) { //第一页 无数据
                                LoadingStatusView.Status status = LoadingStatusView.Status.EMPTY;
                                loadingStatusView.setStatus(status);
                                loadingStatusView.setText("暂无数据");
                            }

                        } else {
                            if (queryParam.page > 1) { //加载下一页数据失败
                                redShopAdapter.loadMoreFail();
                            } else if (redShopAdapter.getData().isEmpty()) { //第一页  无数据
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
    }

    private void loadData() {
        redShopAdapter.setEnableLoadMore(false);
        redViewModel.getRedShops(queryParam.page);
    }

    @Override
    public void restorePage() {
        initData = true;
        loadData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && !initData) {
            initData = true;
            loadData();
        }
    }

    private boolean initData;

}
