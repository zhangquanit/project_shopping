package com.snqu.shopping.ui.main.frag;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.LazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.goods.vm.GoodsViewModel;
import com.snqu.shopping.ui.main.frag.channel.adapter.ChannelListAdapter1;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.util.RecycleViewScrollToTop;
import com.snqu.shopping.util.statistics.DataCache;

public class FreelItemFrag extends LazyFragment {
    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private static final String PARAM_TYPE = "PARAM_TYPE";
    private static final String PARAM_SOURCE = "PARAM_SOURCE";
    private BaseQuickAdapter adapter;
    private GoodsQueryParam queryParam = new GoodsQueryParam();
    private MutableLiveData<NetReqResult> gooodLiveData = new MutableLiveData<>();
    private GoodsViewModel goodsViewModel;
    private LoadingStatusView loadingStatusView;
    private String item_source;

    public static Bundle getParam(String id, String item_source) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_TYPE, id);
        bundle.putString(PARAM_SOURCE, item_source);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.channel_item_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        queryParam.id = getArguments().getString(PARAM_TYPE);
        item_source = getArguments().getString(PARAM_SOURCE);
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
        recyclerView = findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));


        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        int d10 = DeviceUtil.dip2px(mContext, 10);

//        switch (type) {
//            case 1:
        recyclerView.setBackgroundColor(Color.WHITE);
        adapter = new ChannelListAdapter1();
//                break;
//            case 2:
//                recyclerView.setBackgroundColor(Color.WHITE);
//                adapter = new ChannelListAdapter2();
//                break;
//            case 3: //大额券
//                queryParam.has_coupon = 1;
//                recyclerView.setBackgroundColor(Color.TRANSPARENT);
//                recyclerView.setPadding(d10, 0, d10, 0);
//                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
//                dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.rv_list_divider_15));
//                recyclerView.addItemDecoration(dividerItemDecoration);
//
//                adapter = new ChannelListAdapter3();
//        }
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goodsEntity = (GoodsEntity) adapter.getData().get(position);
                GoodsDetailActivity.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), goodsEntity.getItem_id());
                DataCache.reportGoodsByPlate(goodsEntity, position);
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

//        mHomeViewModel =
//                ViewModelProviders.of(this).get(HomeViewModel.class);
        goodsViewModel = ViewModelProviders.of(this).get(GoodsViewModel.class);
        goodsViewModel.getDataResult().observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case ApiHost.GET_GOODS_LIST: //商品列表
                        refreshLayout.finishRefresh(netReqResult.successful);
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
                                adapter.loadMoreEnd();//无下一页
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

    public void loadData() {
        if (TextUtils.equals(item_source, "C")) { //淘宝
            goodsViewModel.getGoodsList(queryParam.page, "1", "4", "recommend_nine_p_nine", item_source, "", "", queryParam.id);
        } else { //拼多多  京东
            goodsViewModel.getGoodsList(queryParam.page, "1", "4", "recommend_nine_p_nine", item_source, "", "", "");
        }
    }


    @Override
    public void onFirstInit() {
        queryParam.page = 1;
        loadData();
    }

    @Override
    public void onLazyResume() {

    }

    @Override
    public void onLazyPause() {

    }
}
