package com.snqu.shopping.ui.mall.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.mall.adapter.MallCategoryListAdapter;
import com.snqu.shopping.ui.mall.goods.ShopGoodsDetailActivity;
import com.snqu.shopping.ui.mall.viewmodel.MallViewModel;
import com.snqu.shopping.util.RecycleViewScrollToTop;

import java.util.ArrayList;

/**
 * 搜索结果
 */
public class MallSearchResultFrag extends SimpleFrag {
    private SmartRefreshLayout refreshLayout;
    private MallCategoryListAdapter adapter;
    private LoadingStatusView loadingStatusView;
    private MallViewModel mallViewModel;
    private int page = 1;
    private String keyword;

    @Override
    protected int getLayoutId() {
        return R.layout.mall_search_result_frag;
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
                page = 1;
                loadData();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycleView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        adapter = new MallCategoryListAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ShopGoodsEntity mallGoods = (ShopGoodsEntity) adapter.getData().get(position);
                ShopGoodsDetailActivity.start(mContext, mallGoods._id);
            }
        });

        adapter.setLoadMoreView(new CommonLoadingMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
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
        adapter.setEmptyView(loadingStatusView);

    }

    private void initData() {
        mallViewModel = ViewModelProviders.of(this).get(MallViewModel.class);
        mallViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, MallViewModel.TAG_CATEGORY_GOODS)) {
                    refreshLayout.finishRefresh(netReqResult.successful);
                    if (netReqResult.successful) {
                        ResponseDataArray<ShopGoodsEntity> goodsData = (ResponseDataArray<ShopGoodsEntity>) netReqResult.data;
                        if (page == 1) {
                            adapter.setNewData(goodsData.getDataList());
                        } else if (!goodsData.getDataList().isEmpty()) {
                            adapter.addData(goodsData.getDataList());
                        }

                        if (goodsData.hasMore()) {
                            page++;
                            adapter.loadMoreComplete(); //刷新成功
                        } else {
                            adapter.loadMoreEnd(page == 1);//无下一页
                        }

                        if (page == 1 && goodsData.getDataList().isEmpty()) { //第一页 无数据
                            LoadingStatusView.Status status = LoadingStatusView.Status.EMPTY;
                            loadingStatusView.setStatus(status);
                            loadingStatusView.setText("暂无该商品，请尝试其他关键词");
                        }

                    } else {
                        if (page > 1) { //加载下一页数据失败
                            adapter.loadMoreFail();
                        } else if (adapter.getData().isEmpty()) { //第一页  无数据
                            LoadingStatusView.Status status = LoadingStatusView.Status.FAIL;
                            loadingStatusView.setStatus(status);
                        } else { //下拉刷新失败
                            ToastUtil.show(netReqResult.message);
                        }
                    }
                }
            }
        });
    }

    private void resetUI() {
        adapter.setNewData(new ArrayList<>());

    }

    private void loadData() {
        mallViewModel.getCategeoryGoods(null, keyword, page);
    }

    public void startSearch(String keyword) {
        this.keyword = keyword;
        this.page = 1;
        resetUI();
        loadData();
    }
}
