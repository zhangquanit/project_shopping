package com.snqu.shopping.ui.main.frag.community;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.snqu.shopping.data.home.entity.artical.ArticalCategoryEntity;
import com.snqu.shopping.data.home.entity.artical.ArticalEntity;
import com.snqu.shopping.ui.main.adapter.ArticalListAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.RecycleViewScrollToTop;

/**
 * 商学院-文章列表
 */
public class ArticalListFrag extends SimpleFrag {

    private static final String PARAM_ID = "PARAM_ID";

    private String id;
    private HomeViewModel mHomeViewModel;
    private ArticalListAdapter articalListAdapter;
    private LoadingStatusView loadingStatusView;
    private SmartRefreshLayout smartRefreshLayout;
    private int page = 1;

    public static void start(Context ctx, ArticalCategoryEntity entity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_ID, entity._id);
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam(entity.name, ArticalListFrag.class, bundle));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.artical_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, true, getTitleBar());
        id = getArguments().getString(PARAM_ID);

        initView();
        initData();

    }

    private void initView() {

        getTitleBar().setBackgroundColor(Color.WHITE);

        smartRefreshLayout = findViewById(R.id.refreshlayout);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                loadData();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        articalListAdapter = new ArticalListAdapter(mContext);
        recyclerView.setAdapter(articalListAdapter);


        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        articalListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticalEntity articalEntity = articalListAdapter.getData().get(position);
                CommonUtil.jumpToArticalDetial(getActivity(), articalEntity);
            }
        });
        articalListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ArticalEntity articalEntity = articalListAdapter.getItem(position);
                if (view.getId() == R.id.item_copy) {
                    CommonUtil.shareArtical(getActivity(), articalEntity);
                }
            }
        });
        articalListAdapter.setLoadMoreView(new CommonLoadingMoreView());
        articalListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
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
        articalListAdapter.setEmptyView(loadingStatusView);
    }

    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_ARTICAL_LIST: //
                        smartRefreshLayout.finishRefresh(netReqResult.successful);
                        if (netReqResult.successful) {
                            ResponseDataArray<ArticalEntity> goodsData = (ResponseDataArray<ArticalEntity>) netReqResult.data;
                            if (page == 1) {
                                articalListAdapter.setNewData(goodsData.getDataList());
                            } else if (!goodsData.getDataList().isEmpty()) {
                                articalListAdapter.addData(goodsData.getDataList());
                            }

                            if (goodsData.hasMore()) {
                                page++;
                                articalListAdapter.loadMoreComplete(); //刷新成功
                            } else {
                                articalListAdapter.loadMoreEnd(page == 1);//无下一页
                            }

                            if (page == 1 && goodsData.getDataList().isEmpty()) { //第一页 无数据
                                LoadingStatusView.Status status = LoadingStatusView.Status.EMPTY;
                                loadingStatusView.setStatus(status);
                                loadingStatusView.setText("暂无数据");
                            }

                        } else {
                            if (page > 1) { //加载下一页数据失败
                                articalListAdapter.loadMoreFail();
                            } else if (articalListAdapter.getData().isEmpty()) { //第一页  无数据
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

        loadData();

    }

    private void loadData() {
        mHomeViewModel.getArticalList(id, page);
    }
}
