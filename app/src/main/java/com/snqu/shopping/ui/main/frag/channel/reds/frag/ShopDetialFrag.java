package com.snqu.shopping.ui.main.frag.channel.reds.frag;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
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
import com.android.util.os.DeviceUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.data.home.entity.ShopItemEntity;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.main.adapter.CategoryListAdapter;
import com.snqu.shopping.ui.main.frag.channel.reds.RedViewModel;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.view.FilterView;
import com.snqu.shopping.ui.main.view.FlitingCoverBar;
import com.snqu.shopping.ui.main.view.ShopLevelView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.RecycleViewScrollToTop;

/**
 * @author 张全
 */
public class ShopDetialFrag extends SimpleFrag {
    private CategoryListAdapter adapter;
    ImageView item_img;
    TextView item_name;
    TextView item_name2;
    TextView item_tv1;
    TextView item_tv2;
    TextView item_tv3;
    TextView item_shop_tag, item_shop_fans;
    private static final String PARAM_ID = "PARAM_ID";
    private static final String PARAM_ITEM_SOURCE = "PARAM_ITEM_SOURCE";
    private String seller_shop_id;
    private String item_source;

    private GoodsQueryParam queryParam = new GoodsQueryParam();
    private HomeViewModel homeViewModel;
    private RedViewModel redViewModel;
    private SmartRefreshLayout refreshLayout;
    private LoadingStatusView loadingStatusView;

    public static void start(Context ctx, String seller_shop_id, String item_source) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_ID, seller_shop_id);
        bundle.putString(PARAM_ITEM_SOURCE, item_source);
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("", ShopDetialFrag.class, bundle).hideTitleBar(true));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.red_shop_detail_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        seller_shop_id = getArguments().getString(PARAM_ID);
        item_source = getArguments().getString(PARAM_ITEM_SOURCE);
        queryParam.seller_shop_id = seller_shop_id;
        queryParam.item_source = item_source;

        StatusBar.setStatusBar(mContext, true);
        initView();
        initData();
    }

    private void initData() {
        redViewModel = ViewModelProviders.of(this).get(RedViewModel.class);
        redViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case RedViewModel.TAG_SHOP_DETAIL: //店铺详情
                        if (netReqResult.successful) {
                            ResponseDataObject<ShopItemEntity> dataObject = (ResponseDataObject<ShopItemEntity>) netReqResult.data;
                            if (null != dataObject.data) {
                                setShopDetail(dataObject.data);
                            }

                        } else {

                        }
                        break;
                }
            }
        });

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_GOO0D_LIST:
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
        redViewModel.getShopDetail(seller_shop_id, item_source);
        loadGoods();
    }

    private void loadGoods() {
        queryParam.isShop = true;
        homeViewModel.getGoodList(queryParam);
    }

    private void initView() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

        FlitingCoverBar coverBar = CommonUtil.getCoverBar(getActivity());
        coverBar.setCoverBarListener(new FlitingCoverBar.CoverBarListener() {
            @Override
            public void sure(String item_source, int postage, String minPrice, String maxPrice) {
                queryParam.item_source = item_source;
                queryParam.postage = postage;
                queryParam.start_price = minPrice;
                queryParam.end_price = maxPrice;
                queryParam.page = 1;
                loadGoods();
            }
        });

        FilterView filterView = findViewById(R.id.filterview);
        filterView.hideFilterItem();
        filterView.setOnItemClickListener(new FilterView.OnItemClickListener() {
            @Override
            public void filtrate() {
                coverBar.show();
            }

            @Override
            public void onFilter(GoodsQueryParam.Sort sort) {
                queryParam.sort = sort;
                queryParam.page = 1;
                loadGoods();
            }
        });

        int dvalue = DeviceUtil.dip2px(mContext, 75);
        View top_contentbar = findViewById(R.id.top_contentbar);
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int pos) {
                top_contentbar.setVisibility(pos > -dvalue ? View.VISIBLE : View.INVISIBLE);
                item_name2.setVisibility(pos > -dvalue ? View.INVISIBLE : View.VISIBLE);
            }
        });


        item_img = findViewById(R.id.item_img);
        item_name = findViewById(R.id.item_name);
        item_name2 = findViewById(R.id.item_name2);
        item_shop_tag = findViewById(R.id.item_shop_tag);
        item_shop_fans = findViewById(R.id.item_shop_fans);

        item_tv1 = findViewById(R.id.item_tv1);
        item_tv2 = findViewById(R.id.item_tv2);
        item_tv3 = findViewById(R.id.item_tv3);

        RecyclerView listView = findViewById(R.id.listview);

        RecycleViewScrollToTop.addScroolToTop(listView, findViewById(R.id.scroll_to_top));

//        GridSpaceItemDecoration dividerItemDecoration = new GridSpaceItemDecoration(DeviceUtil.dip2px(mContext, 10));
//        listView.addItemDecoration(dividerItemDecoration);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        listView.setLayoutManager(gridLayoutManager);

        adapter = new CategoryListAdapter();
        adapter.showRobBtn();
        listView.setAdapter(adapter);
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
                loadGoods();
            }
        }, listView);

        loadingStatusView = new LoadingStatusView(mContext);
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        adapter.setEmptyView(loadingStatusView);
    }

    private void setShopDetail(ShopItemEntity shopDetail) {
        GlideUtil.loadPic(item_img, shopDetail.seller_shop_icon, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        item_name.setText(shopDetail.seller_shop_name);
        item_name2.setText(shopDetail.seller_shop_name);
        item_shop_tag.setVisibility(View.VISIBLE);
        item_shop_tag.setText(ItemSourceClient.getItemSourceName(item_source));

        if (TextUtils.equals(shopDetail.seller_type, Constant.BusinessType.PDD)) { //拼多多

            // 大于0的时候才显示已拼和关注
            if(!"0".equals(shopDetail.getSellCount())&&!"0".equals(shopDetail.getFans())) {
                findViewById(R.id.item_shop_pdd).setVisibility(View.VISIBLE);
                TextView tv_sellCount = findViewById(R.id.item_shop_pdd_sellcount);
                SpannableStringBuilder stringBuilder = new SpanUtils()
                        .append("已拼").setForegroundColor(Color.parseColor("#25282D"))
                        .append(shopDetail.getSellCount()).setForegroundColor(Color.parseColor("#F73737"))
                        .append("件").setForegroundColor(Color.parseColor("#25282D"))
                        .create();
                tv_sellCount.setText(stringBuilder);

                TextView tv_fans = findViewById(R.id.item_shop_pdd_fans);
                stringBuilder = new SpanUtils()
                        .append(shopDetail.getFans()).setForegroundColor(Color.parseColor("#F73737"))
                        .append("人关注").setForegroundColor(Color.parseColor("#25282D"))
                        .create();
                tv_fans.setText(stringBuilder);
            }


        } else { //淘宝、天猫、京东
            findViewById(R.id.item_shop).setVisibility(View.VISIBLE);
            if(!shopDetail.getFans().equals("0")) {
                item_shop_fans.setVisibility(View.VISIBLE);
                item_shop_fans.setText(String.format("粉丝·%s", shopDetail.getFans()));
            }else{
                item_shop_fans.setVisibility(View.GONE);
            }
            ShopLevelView shopLevelView = findViewById(R.id.shop_level);
            shopLevelView.setData(shopDetail);
        }

        if (null != shopDetail.getScoreDesc()) {
            findViewById(R.id.item_soces).setVisibility(View.VISIBLE);

            item_tv1.setText(shopDetail.getScoreDesc());
            item_tv2.setText(shopDetail.getScoreServ());
            item_tv3.setText(shopDetail.getScorePost());
        } else {
            findViewById(R.id.item_soces).setVisibility(View.INVISIBLE);
        }

    }


    private SpannableStringBuilder getDetail(String text, String value, int color) {
        return new SpanUtils()
                .append(text).setForegroundColor(Color.parseColor("#848487")).setFontSize(12, true)
                .append(" " + value).setForegroundColor(color).setFontSize(12, true)
                .create();
    }
}
