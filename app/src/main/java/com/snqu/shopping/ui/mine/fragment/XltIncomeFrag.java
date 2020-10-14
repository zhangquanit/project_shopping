package com.snqu.shopping.ui.mine.fragment;

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

import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.user.entity.XltIncomeEntity;
import com.snqu.shopping.ui.login.vm.UserViewModel;
import com.snqu.shopping.ui.main.view.TabIndicatorLayout;
import com.snqu.shopping.ui.mine.adapter.XltIncomeAdapter;
import com.snqu.shopping.util.RecycleViewScrollToTop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 乐淘收入榜
 *
 * @author 张全
 */
public class XltIncomeFrag extends SimpleFrag {
    private SmartRefreshLayout smartRefreshLayout;
    private XltIncomeAdapter adapter;
    private LoadingStatusView loadingStatusView;
    private String type;
    private List<String> typeList = new ArrayList<>();
    private UserViewModel userViewModel;
    private Map<String, List<XltIncomeEntity>> dataMap = new HashMap<>();

    public static void start(Context ctx) {
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("乐桃收入榜", XltIncomeFrag.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.xlt_income_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, true, getTitleBar());
        initView();
        initData();
    }

    private void initView() {
        getTitleBar().setBackgroundColor(Color.WHITE);
        smartRefreshLayout = findViewById(R.id.refresh_layout);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData();
            }
        });

        List<String> tabs = new ArrayList<>();
        tabs.add("今日收益");
        tabs.add("本月收益");
        tabs.add("上月收益");


        TabIndicatorLayout tabIndicatorLayout = findViewById(R.id.indicator_layout);
        tabIndicatorLayout.setData(tabs, 0);
        tabIndicatorLayout.setOnItemClickListener(new TabIndicatorLayout.OnItemClickListener() {
            @Override
            public void onClick(int pos, View v) {
                type = typeList.get(pos);
                if (dataMap.containsKey(type)) {
                    adapter.setNewData(dataMap.get(type));
                } else {
                    //显示loading
                    LoadingStatusView.Status status = LoadingStatusView.Status.LOADING;
                    loadingStatusView.setStatus(status);
                    adapter.setNewData(new ArrayList<>());
                    loadData();
                }
            }
        });

        RecyclerView mFloorListView = findViewById(R.id.listview);
        mFloorListView.setLayoutManager(new LinearLayoutManager(mContext));

        adapter = new XltIncomeAdapter(new ArrayList<>());
        mFloorListView.setAdapter(adapter);

        RecycleViewScrollToTop.addScroolToTop(mFloorListView, findViewById(R.id.scroll_to_top));


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
                if (TextUtils.equals(netReqResult.tag, ApiHost.XLT_INCOME)) {
                    String reqType = (String) netReqResult.extra;
                    smartRefreshLayout.finishRefresh(netReqResult.successful);
                    if (!TextUtils.equals(type, reqType)) {
                        return;
                    }
                    if (netReqResult.successful) {
                        List<XltIncomeEntity> dataList = (List<XltIncomeEntity>) netReqResult.data;
                        if (!dataList.isEmpty()) {
                            dataList.get(0).type = XltIncomeEntity.TYPE_NO1;
                        }
                        dataMap.put(type, dataList);


                        adapter.setNewData(dataList);

                        if (dataList.isEmpty()) {
                            LoadingStatusView.Status status = LoadingStatusView.Status.EMPTY;
                            loadingStatusView.setStatus(status);
                            loadingStatusView.setText("暂无数据");
                        }

                    } else {
                        LoadingStatusView.Status status = LoadingStatusView.Status.FAIL;
                        loadingStatusView.setStatus(status);
                    }
                }
            }
        });

        typeList.add("today");
        typeList.add("cmonth");
        typeList.add("pmonth");
        type = typeList.get(0);
        loadData();
    }

    private void loadData() {
        userViewModel.getXltIncome(type);
    }
}
