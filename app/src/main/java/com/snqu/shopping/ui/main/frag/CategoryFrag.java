package com.snqu.shopping.ui.main.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.anroid.base.LazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.data.home.entity.ItemSourceEntity;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.main.MainActivity;
import com.snqu.shopping.ui.main.adapter.CategoryListAdapter;
import com.snqu.shopping.ui.main.frag.channel.adapter.ChannelPagerAdapter;
import com.snqu.shopping.ui.main.frag.channel.adapter.ClassficationPagerAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.view.FilterView;
import com.snqu.shopping.ui.main.view.FlitingCoverBar;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.statistics.DataCache;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import common.widget.viewpager.ViewPager;

/**
 * 首页-分类列表
 *
 * @author 张全
 */
public class CategoryFrag extends LazyFragment {
    private CategoryListAdapter adapter;
    private RecyclerView mFloorListView;
    private HomeViewModel mHomeViewModel;
    private static final String PARAM = "CategoryEntity";
    private CategoryEntity categoryEntity;
    private FilterView filterView;
    private ViewPager viewPager;
    private SmartRefreshLayout refreshLayout;
    private GoodsQueryParam queryParam = new GoodsQueryParam();
    public MutableLiveData<NetReqResult> categoryLiveData = new MutableLiveData<>();//刷新全部
    public MutableLiveData<NetReqResult> gooodLiveData = new MutableLiveData<>();
    //    private List<ItemSource> itemSources = new ArrayList<>();
    private List<ItemSourceEntity> itemSources = new ArrayList<>();
    private LoadingStatusView loadingStatusView;

    public static Bundle getParam(CategoryEntity categoryEntity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM, categoryEntity);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.home_cateory_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        addAction(Constant.Event.LOGIN_SUCCESS);
        addAction(Constant.Event.LOGIN_OUT);
        categoryEntity = (CategoryEntity) getArguments().getSerializable(PARAM);
        queryParam.category = categoryEntity._id;
        initView();
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        if (TextUtils.equals(event.getAction(), Constant.Event.LOGIN_SUCCESS) || TextUtils.equals(event.getAction(), Constant.Event.LOGIN_OUT)) {
            //登录或退出登录 刷新
            loadData();
        }
    }

    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        categoryLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_CATEGORY: //分类列表
                        if (netReqResult.successful) {
                            List<CategoryEntity> categoryEntities = (List<CategoryEntity>) netReqResult.data;

                            List<CategoryEntity> dataList = new ArrayList<>();
                            for (CategoryEntity item : categoryEntities) {
                                if (item.level == 3) {
                                    dataList.add(item);
                                }
                            }
                            if (dataList.isEmpty()) {
                                findViewById(R.id.viewpager_container).setVisibility(View.GONE);
                                findViewById(R.id.holderView).setVisibility(View.GONE);

                                AppBarLayout appBarLayout = findViewById(R.id.appbar);
                                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                                layoutParams.setBehavior(null);
                                appBarLayout.setLayoutParams(layoutParams);

                            } else {
                                findViewById(R.id.viewpager_container).setVisibility(View.VISIBLE);
                                findViewById(R.id.holderView).setVisibility(View.VISIBLE);
                                setChannelList(dataList);
                            }
                        } else {

                        }
                        break;
                }
            }
        });

        gooodLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_GOO0D_LIST: //商品列表
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

    public void setData(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
        queryParam.category = categoryEntity._id;
    }

    private void loadData() {
        queryParam.page = 1;
        mHomeViewModel.getCategoryById(categoryEntity._id, categoryEntity.level, categoryLiveData);
        loadGoods();
    }

    private void loadGoods() {
        mHomeViewModel.getGoodList(queryParam, gooodLiveData);
    }

    private void initView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        FlitingCoverBar coverBar = mainActivity.getCoverBar();
        coverBar.setCoverBarListener(coverBarListener);


        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        ImageView scroll_to_top = findViewById(R.id.scroll_to_top);
        scroll_to_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) layoutParams.getBehavior();
                if (behavior.getTopAndBottomOffset() != 0) {
                    behavior.setTopAndBottomOffset(0);
                    scroll_to_top.setVisibility(View.GONE);
                }
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int pos) {
                if (pos > -400) {
                    scroll_to_top.setVisibility(View.GONE);
                } else {
                    scroll_to_top.setVisibility(View.VISIBLE);
                }
            }
        });


        refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData();
            }
        });
        CommonUtil.setRefreshHeaderWhiteText(refreshLayout);
        filterView = findViewById(R.id.filterview);
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

        viewPager = findViewById(R.id.viewPager);

        mFloorListView = findViewById(R.id.listview);

//        GridSpaceItemDecoration dividerItemDecoration = new GridSpaceItemDecoration(DeviceUtil.dip2px(mContext, 10));
//        dividerItemDecoration.setSpanCount(2);
//        mFloorListView.addItemDecoration(dividerItemDecoration);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        mFloorListView.setLayoutManager(gridLayoutManager);


        adapter = new CategoryListAdapter();
        mFloorListView.setAdapter(adapter);


        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goodsEntity = CategoryFrag.this.adapter.getData().get(position);
                GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), goodsEntity);

                DataCache.firstCategory = DataCache.homeFirstCategory;
                DataCache.thirdCategory = null;
                DataCache.reportGoodsByCategory(goodsEntity, position);
            }
        });


        adapter.setLoadMoreView(new CommonLoadingMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadGoods();
            }
        }, mFloorListView);

        loadingStatusView = new LoadingStatusView(mContext);
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGoods();
            }
        });
        adapter.setEmptyView(loadingStatusView);
    }

    private void setChannelList(List<CategoryEntity> categoryEntities) {
        List<List<CategoryEntity>> dataList = new ArrayList<>();
        List<CategoryEntity> categoryList = new ArrayList<>(categoryEntities);
        if (categoryList.size() > 9) {
            categoryList = categoryList.subList(0, 9);
            CategoryEntity entity = categoryEntity;
            entity.name = LContext.getString(R.string.watch_more);
            categoryList.add(entity);
        }
        dataList.add(categoryList);

        ClassficationPagerAdapter channelPagerAdapter = new ClassficationPagerAdapter(mContext, dataList);
        viewPager.setAdapter(channelPagerAdapter);

        if (categoryEntities.size() <= 4) {
            ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
            layoutParams.height = ChannelPagerAdapter.getLineHeight();
            viewPager.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
            layoutParams.height = ChannelPagerAdapter.getTwoLineHeight();
            viewPager.setLayoutParams(layoutParams);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getCoverBar().removeCoverBarListener(coverBarListener);
    }


    FlitingCoverBar.CoverBarListener coverBarListener = new FlitingCoverBar.CoverBarListener() {
        @Override
        public void sure(String item_source, int postage, String minPrice, String maxPrice) {
//            itemSources.clear();
//            if (item_source.contains(",")) { //多个平台
//                String[] items = item_source.split(",");
//                for (String item : items) {
//                    ItemSourceEntity source = ItemSourceClient.getItemSourceEntity(ItemSourceClient.ItemSourceType.SEARCH, item_source);
//                    itemSources.add(source);
//                }
//            } else { //1个平台
//                ItemSourceEntity source = ItemSourceClient.getItemSourceEntity(ItemSourceClient.ItemSourceType.SEARCH, item_source);
//                itemSources.add(source);
//            }
            queryParam.item_source = item_source;
            queryParam.postage = postage;
            queryParam.start_price = minPrice;
            queryParam.end_price = maxPrice;
            queryParam.page = 1;
            loadGoods();
        }
    };
}
