package com.snqu.shopping.ui.main.frag.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.SimpleFrag;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.entity.ItemSourceEntity;
import com.snqu.shopping.data.home.entity.SearchShopEntity;
import com.snqu.shopping.data.home.entity.ShopItemEntity;
import com.snqu.shopping.ui.main.frag.channel.reds.adapter.RedShopAdapter;
import com.snqu.shopping.ui.main.frag.channel.reds.frag.ShopDetialFrag;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.RecycleViewScrollToTop;

import java.util.ArrayList;
import java.util.List;

import common.widget.listview.LinearSpaceItemDecoration;

/**
 * 搜索结果——店铺
 *
 * @author 张全
 */
public class SearchShopResultFrag extends SimpleFrag {
    HomeViewModel mHomeViewModel;
    SmartRefreshLayout refreshLayout;
    View tv_emtpy;
    View spaceView;
    ViewGroup type_banner;
    RedShopAdapter adapter;
    RecyclerView recyclerView;

    private GoodsQueryParam queryParam = new GoodsQueryParam();
    private ResponseDataArray<ShopItemEntity> recommendShopData;
    private boolean isSearchListEmpty; //搜索列表为空
    private String itemSource = Constant.BusinessType.TB;

    @Override
    protected int getLayoutId() {
        return R.layout.search_result_shop_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
        initData();
    }

    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                refreshLayout.finishRefresh(netReqResult.successful);
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_SEARCH_SHOP:
                        if (netReqResult.successful) {
                            SearchShopEntity shopData = (SearchShopEntity) netReqResult.data;
                            recommendShopData = shopData.recommendEntities;
                            refreshUI(shopData.shopEntities.getDataList());
                            adapter.setNewData(shopData.getDataList());

                            if (shopData.shopEntities.getDataList().isEmpty()) {//无搜索列表
                                isSearchListEmpty = true;
                            }
                            if (queryParam.row > shopData.getDataList().size()) { //无下一页
                                adapter.loadMoreEnd(true);
                            } else {
                                queryParam.page++;
                                adapter.loadMoreComplete();
                            }
                        } else {
                            ToastUtil.show(netReqResult.message);
                        }
                        break;
                    case HomeViewModel.TAG_SEARCH_SHOP_LIST: //店铺搜索列表
                        if (netReqResult.successful) {
                            ResponseDataArray<ShopItemEntity> searchData = (ResponseDataArray<ShopItemEntity>) netReqResult.data;
                            List<ShopItemEntity> dataList = searchData.getDataList();

                            if (queryParam.page == 1) {
                                if (dataList.isEmpty()) {
                                    //无搜索列表
                                    isSearchListEmpty = true;
                                    refreshUI(dataList); //空数据
                                    adapter.setNewData(recommendShopData.getDataList());//展示推荐列表
                                    queryParam.page++;
                                    return;
                                }
                            }

                            if (queryParam.page == 1) {
                                adapter.setNewData(dataList);
                            } else if (!dataList.isEmpty()) {
                                adapter.addData(dataList);
                            }
                            if (queryParam.row > dataList.size()) { //无下一页
                                adapter.loadMoreEnd(true);
                            } else {
                                queryParam.page++;
                                adapter.loadMoreComplete();
                            }
                        } else {
                            if (queryParam.page > 1) { //加载下一页数据失败
                                adapter.loadMoreFail();
                            } else { //下拉刷新失败
                                ToastUtil.show(netReqResult.message);
                            }
                        }

                        break;
                    case HomeViewModel.TAG_SEARCH_SHOP_RECOMMEND_LIST: //推荐店铺列表
                        if (netReqResult.successful) {
                            ResponseDataArray<ShopItemEntity> recommendData = (ResponseDataArray<ShopItemEntity>) netReqResult.data;
                            List<ShopItemEntity> dataList = recommendData.getDataList();

                            if (queryParam.page == 1) {
                                adapter.setNewData(dataList);
                            } else if (!dataList.isEmpty()) {
                                adapter.addData(dataList);
                            }
                            if (queryParam.row > dataList.size()) { //无下一页
                                adapter.loadMoreEnd(queryParam.page == 1);
                            } else {
                                queryParam.page++;
                                adapter.loadMoreComplete();
                            }
                        } else {
                            if (queryParam.page > 1) { //加载下一页数据失败
                                adapter.loadMoreFail();
                            } else { //下拉刷新失败
                                ToastUtil.show(netReqResult.message);
                            }
                        }
                        break;
                }
            }
        });
    }


    private void initView() {
        List<ItemSourceEntity> dataList = new ArrayList();
        dataList.add(new ItemSourceEntity("淘宝","C"));
        dataList.add(new ItemSourceEntity("京东","D"));
        dataList.add(new ItemSourceEntity("拼多多","P"));
        dataList.add(new ItemSourceEntity("唯品会","V"));

        type_banner = findViewById(R.id.type_banner);
        int childCount = type_banner.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final int pos = i;
            type_banner.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.isSelected()) {
                        return;
                    }
                    TextView textView = (TextView) v;
                    textView.getPaint().setFakeBoldText(true);
                    v.setSelected(true);
                    for (int j = 0; j < childCount; j++) {
                        TextView child = (TextView) type_banner.getChildAt(j);
                        if (child != v) {
                            child.setSelected(false);
                            child.getPaint().setFakeBoldText(false);
                        } else {
                            //条件查询
                            ItemSourceEntity itemSource = dataList.get(pos);
                            queryParam.item_source = itemSource.code;
                            queryParam.page = 1;
                            loadData();
                        }
                    }
                }
            });
        }

        //默认选中第一个
        selectFirst();

        refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                queryParam.page = 1;
                loadData();
            }
        });

        View header = LayoutInflater.from(mContext).inflate(R.layout.search_result_shop_header, null);
        tv_emtpy = header.findViewById(R.id.tv_emtpy);
        spaceView = header.findViewById(R.id.spaceView);

        recyclerView = findViewById(R.id.shop_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        adapter = new RedShopAdapter();
        adapter.hideUserBanner();
        adapter.addHeaderView(header);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_detail:
                    case R.id.item_top:
                        ShopItemEntity shopEntity = (ShopItemEntity) adapter.getData().get(position);
                        ShopDetialFrag.start(mContext, shopEntity.seller_shop_id,shopEntity.seller_type);
                        break;
                }
            }
        });

        LinearSpaceItemDecoration itemDecoration = new LinearSpaceItemDecoration(DeviceUtil.dip2px(mContext, 10), LinearLayoutManager.VERTICAL);
        itemDecoration.setHeaderCount(1);
        recyclerView.addItemDecoration(itemDecoration);

        adapter.setLoadMoreView(new CommonLoadingMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, recyclerView);
    }

    private void refreshUI(List<ShopItemEntity> dataList) {
        if (null == dataList || dataList.isEmpty()) {
            tv_emtpy.setVisibility(View.VISIBLE);
            spaceView.setVisibility(View.GONE);
        } else {
            tv_emtpy.setVisibility(View.GONE);
            spaceView.setVisibility(View.VISIBLE);
        }
    }

    private void resetUI() {

        tv_emtpy.setVisibility(View.GONE);
        spaceView.setVisibility(View.GONE);
        int childCount = type_banner.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (type_banner.getChildAt(i).isSelected()) {
                View v = type_banner.getChildAt(i);
                TextView textView = (TextView) v;
                textView.getPaint().setFakeBoldText(false);
                v.setSelected(false);
                break;
            }
        }
        //默认选中第一个
        selectFirst();

        adapter.setNewData(new ArrayList<>());
    }

    private void selectFirst() {
        type_banner.getChildAt(0).setSelected(true);
        View v = type_banner.getChildAt(0);
        TextView textView = (TextView) v;
        textView.getPaint().setFakeBoldText(true);
        v.setSelected(true);
    }

    public void startSearch(String keyword,String item_source) {
        queryParam.search = keyword;
        queryParam.page = 1;
        itemSource = item_source;
        isSearchListEmpty = false;
        queryParam.item_source = itemSource;
        resetUI();

        if (null == recommendShopData) { //推荐商铺
            mHomeViewModel.searchShop(queryParam);
        } else {
            mHomeViewModel.getSearchShopList(queryParam); //搜索列表
        }
    }

    private void loadData() {
        queryParam.item_source = itemSource;
        if (queryParam.page == 1 || null == recommendShopData) { //推荐商铺
            mHomeViewModel.searchShop(queryParam);
        } else {
            if (isSearchListEmpty) { //推荐列表
                mHomeViewModel.getRecommendShops(queryParam);
            } else { //搜索列表
                mHomeViewModel.getSearchShopList(queryParam);
            }
        }
    }
}
