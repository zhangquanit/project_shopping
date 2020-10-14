package com.snqu.shopping.ui.main.frag.channel.reds.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.android.material.appbar.AppBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.red.entity.BigVInfo;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.main.adapter.CategoryListAdapter;
import com.snqu.shopping.ui.main.frag.channel.reds.RedViewModel;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.view.FilterView;
import com.snqu.shopping.ui.main.view.FlitingCoverBar;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.RecycleViewScrollToTop;

/**
 * @author 张全
 */
public class BigVDetailFrag extends SimpleFrag {
    private CategoryListAdapter adapter;
    private SmartRefreshLayout refreshLayout;
    private BigVInfo bigVInfo;
    private static final String PARAM = "PARAM";
    private GoodsQueryParam queryParam = new GoodsQueryParam();
    private RedViewModel redViewModel;
    private LoadingStatusView loadingStatusView;

    public static void start(Context ctx, BigVInfo bigVInfo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM, bigVInfo);
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("", BigVDetailFrag.class, bundle).hideTitleBar(true));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.reds_big_v_detail_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, true);
        bigVInfo = (BigVInfo) getArguments().getSerializable(PARAM);
        queryParam.id = bigVInfo._id;
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

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });


        FlitingCoverBar coverBar = CommonUtil.getCoverBar(getActivity());
        coverBar.setCoverBarListener(new FlitingCoverBar.CoverBarListener() {
            @Override
            public void sure(String item_source, int postage, String minPrice, String maxPrice) {
                queryParam.item_source = item_source;
                queryParam.postage = postage;
                queryParam.start_price = minPrice;
                queryParam.end_price = maxPrice;
                queryParam.page = 1;
                loadData();
            }
        });

        ImageView iv_head = findViewById(R.id.iv_head);
        GlideUtil.loadPic(iv_head, bigVInfo.avatar, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);

        ImageView iv_source = findViewById(R.id.iv_head_source);
        int sourceDrawable = bigVInfo.getDetailSourceDrawable();
        if (sourceDrawable != -1) {
            iv_source.setImageResource(sourceDrawable);
        }

        TextView tv_name = findViewById(R.id.tv_name);
        tv_name.setText(bigVInfo.name);

        TextView tv_intro = findViewById(R.id.tv_intro);
        tv_intro.setText(bigVInfo.getSourceText());

        FilterView filterView = findViewById(R.id.filterview);
        filterView.setOnItemClickListener(new FilterView.OnItemClickListener() {

            @Override
            public void filtrate() {
                coverBar.show();
            }

            @Override
            public void onFilter(GoodsQueryParam.Sort sort) {
                queryParam.sort = sort;
                queryParam.page = 1;
                loadData();
            }
        });


        TextView toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(bigVInfo.name);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int v) {
                if (Math.abs(v) <= 475) {
                    toolbar_title.setVisibility(View.INVISIBLE);
                    tv_intro.setVisibility(View.VISIBLE);
                    tv_name.setVisibility(View.VISIBLE);
                } else {
                    toolbar_title.setVisibility(View.VISIBLE);
                    tv_intro.setVisibility(View.INVISIBLE);
                    tv_name.setVisibility(View.INVISIBLE);
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.listview);

//        GridSpaceItemDecoration dividerItemDecoration = new GridSpaceItemDecoration(DeviceUtil.dip2px(mContext, 10));
//        recyclerView.addItemDecoration(dividerItemDecoration);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new CategoryListAdapter();
        recyclerView.setAdapter(adapter);

        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goodsEntity = (GoodsEntity) adapter.getData().get(position);
                GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), CommonUtil.PLATE, CommonUtil.PLATE_CHILD, 1, goodsEntity);
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
        redViewModel =
                ViewModelProviders.of(this).get(RedViewModel.class);
        redViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case RedViewModel.TAG_BIGV_GOODS:
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
                        break;
                }
            }
        });

        loadData();

    }

    private void loadData() {
        redViewModel.getBigVGoods(queryParam);
    }
}
