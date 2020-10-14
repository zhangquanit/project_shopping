package com.snqu.shopping.ui.mine.fragment;

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
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.user.entity.IncomeQueryParam;
import com.snqu.shopping.data.user.entity.TeamIncomeEntity;
import com.snqu.shopping.ui.login.vm.UserViewModel;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.mine.adapter.TeamIncomeAdapter;
import com.snqu.shopping.util.RecycleViewScrollToTop;

/**
 * @author 张全
 */
public class TeamIncomePageFrag extends SimpleFrag {
    private int page;
    private static final String PARAM = "PARAM";

    private SmartRefreshLayout smartRefreshLayout;
    private TeamIncomeAdapter adapter;
    private LoadingStatusView loadingStatusView;
    private IncomeQueryParam queryParam = new IncomeQueryParam();
    private UserViewModel userViewModel;

    public static Bundle getParam(int page) {
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM, page);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.team_income_page;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        page = getArguments().getInt(PARAM);
        initView();
        initData();
    }

    private void initView() {
        smartRefreshLayout = findViewById(R.id.refresh_layout);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                queryParam.page = 1;
                loadData();
            }
        });

        RecyclerView mFloorListView = findViewById(R.id.listview);
        mFloorListView.setLayoutManager(new LinearLayoutManager(mContext));

        adapter = new TeamIncomeAdapter(page);
        mFloorListView.setAdapter(adapter);

        RecycleViewScrollToTop.addScroolToTop(mFloorListView, findViewById(R.id.scroll_to_top));

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

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

    private void initData() {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getDataResult().observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, ApiHost.TEAM_INCOME_TOTAL)
                        || TextUtils.equals(netReqResult.tag, ApiHost.TEAM_INCOME_MONTH)
                        || TextUtils.equals(netReqResult.tag, ApiHost.TEAM_INCOME_WEEK)
                ) {
                    smartRefreshLayout.finishRefresh(netReqResult.successful);
                    if (netReqResult.successful) {
                        ResponseDataArray<TeamIncomeEntity> goodsData = (ResponseDataArray<TeamIncomeEntity>) netReqResult.data;
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

                }
            }
        });
        loadData();
    }

    private void loadData() {
        if (page == 0) {
            userViewModel.getTeamIncomeTotal(queryParam);
        } else if (page == 1) {
            userViewModel.getTeamIncomeMonth(queryParam);
        } else if (page == 2) {
            userViewModel.getTeamIncomeWeek(queryParam);
        }
    }

    public void refresh(int page, IncomeQueryParam.Sort sort) {
        if (page == 2) { //七日拉新
            queryParam.sort = sort;
            queryParam.page = 1;
            loadData();
        }
    }

    public void refresh(String relation) {
        queryParam.page = 1;
        queryParam.relation = relation;
        loadData();
    }
}
