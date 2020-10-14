package com.snqu.shopping.ui.goods.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.home.entity.CommunityRewardEntity;
import com.snqu.shopping.ui.goods.adapter.RewardListAdapter;
import com.snqu.shopping.ui.main.frag.WebViewFrag;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.RecycleViewScrollToTop;

import java.util.List;

/**
 * 商品推荐-奖励记录
 *
 * @author 张全
 */
public class GoodRecommRewardListFrag extends SimpleFrag {
    private SmartRefreshLayout refreshLayout;
    private LoadingStatusView loadingStatusView;
    private HomeViewModel homeViewModel;
    private RewardListAdapter adapter;
    private int page = 1;

    public static void start(Context ctx) {
        SimpleFragAct.SimpleFragParam fragParam = new SimpleFragAct.SimpleFragParam("奖励记录", GoodRecommRewardListFrag.class);
        SimpleFragAct.start(ctx, fragParam);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.good_recomm_reward_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, true, getTitleBar());

        initView();
        initData();

    }

    private void initView() {
        getTitleBar().setBackgroundColor(Color.WHITE);
        getTitleBar().setRightText("奖励规则")
                .setOnRightTxtClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                        webViewParam.url = Constant.WebPage.REWARD_RULE;
                        WebViewFrag.start(mContext, webViewParam);
                    }
                });
        getTitleBar().mRightTxtView.setTextColor(Color.parseColor("#333333"));
        getTitleBar().mRightTxtView.setTextSize(14);

        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                loadData();
            }
        });
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        adapter = new RewardListAdapter();
        recyclerView.setAdapter(adapter);

        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

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
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(HomeViewModel.TAG_COMMUNITY_REWARD_LIST, netReqResult.tag)) {
                    refreshLayout.finishRefresh(netReqResult.successful);
                    if (netReqResult.successful) {
                        List<CommunityRewardEntity> dataList = (List<CommunityRewardEntity>) netReqResult.data;

                        if (page == 1) {
                            adapter.setNewData(dataList);
                        } else if (!dataList.isEmpty()) {
                            adapter.addData(dataList);
                        }

                        if (!dataList.isEmpty()) {
                            page++;
                            adapter.loadMoreComplete(); //刷新成功
                        } else {
                            adapter.loadMoreEnd(page == 1);//无下一页
                        }

                        if (page == 1 && dataList.isEmpty()) {
                            LoadingStatusView.Status status = LoadingStatusView.Status.EMPTY;
                            loadingStatusView.setStatus(status);
                            loadingStatusView.setText("暂无数据");
                        }

                    } else {
                        if (page > 1) { //加载下一页数据失败
                            adapter.loadMoreFail();
                        } else if (adapter.getData().isEmpty()) { // 无数据
                            LoadingStatusView.Status status = LoadingStatusView.Status.FAIL;
                            loadingStatusView.setStatus(status);
                        } else { //下拉刷新失败
                            ToastUtil.show(netReqResult.message);
                        }
                    }
                }
            }
        });

        loadData();
    }

    private void loadData() {
        homeViewModel.getCommunityRewardList(page);
    }
}
