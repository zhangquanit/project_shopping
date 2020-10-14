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
import com.snqu.shopping.data.red.entity.BigVEntity;
import com.snqu.shopping.ui.main.frag.channel.reds.RedViewModel;
import com.snqu.shopping.ui.main.frag.channel.reds.adapter.BigVAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.util.RecycleViewScrollToTop;

/**
 * 大V推荐
 */
public class BigVFrag extends SimpleFrag {
    private BigVAdapter adapter;
    private SmartRefreshLayout refreshLayout;

    private RedViewModel redViewModel;
    private GoodsQueryParam queryParam = new GoodsQueryParam();

    private LoadingStatusView loadingStatusView;

    @Override
    protected int getLayoutId() {
        return R.layout.reds_big_v_frag;
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
        adapter = new BigVAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.rv_list_divider_15));
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(adapter);

        //
        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_more:
                    case R.id.item_top:
                        BigVEntity bigVEntity = (BigVEntity) adapter.getData().get(position);
                        BigVDetailFrag.start(mContext, bigVEntity.getBigvInfo());
                        break;
                }
            }
        });

        adapter.setLoadMoreView(new CommonLoadingMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        });


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

        redViewModel =
                ViewModelProviders.of(this).get(RedViewModel.class);
        redViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case RedViewModel.TAG_BIGV:
                        refreshLayout.finishRefresh(netReqResult.successful);
                        if (netReqResult.successful) {
                            ResponseDataArray<BigVEntity> goodsData = (ResponseDataArray<BigVEntity>) netReqResult.data;
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
                                loadingStatusView.setText("换个分类看看吧~");
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
    }

    private void loadData() {
        redViewModel.getBigVList(queryParam);
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
