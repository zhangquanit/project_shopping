package com.snqu.shopping.ui.main.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.LazyFragment;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.goods.vm.GoodsViewModel;
import com.snqu.shopping.ui.main.MainActivity;
import com.snqu.shopping.ui.main.adapter.CategoryListAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.view.FilterView;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.statistics.DataCache;
import com.snqu.shopping.util.statistics.SndoData;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * 首页-分类列表
 *
 * @author 张全
 */
public class GuessLikeFrag extends LazyFragment {

    private CategoryListAdapter adapter;
    private RecyclerView mFloorListView;
    private GoodsViewModel goodsViewModel;
    private static final String PARAM = "CategoryEntity";
    private FilterView filterView;
    private SmartRefreshLayout refreshLayout;
    private GoodsQueryParam queryParam = new GoodsQueryParam();
    public MutableLiveData<NetReqResult> gooodLiveData = new MutableLiveData<>();
    private LoadingStatusView loadingStatusView;
    private String mLikeMode;
    private int totalDy;

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
        totalDy = 0;
        queryParam.page = 1;
        queryParam.item_source = "C";
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

        //猜你喜欢状态模式
        mLikeMode = SPUtils.getInstance().getString(Constant.PREF.LIKE_MODE, "0");
        goodsViewModel = ViewModelProviders.of(this).get(GoodsViewModel.class);
        gooodLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
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

                            queryParam.page++;
                            adapter.loadMoreComplete(); //刷新成功
                            // 只有1状态的时候，没有下一页，才显示加载完成
                            if (TextUtils.equals(mLikeMode, "1")) {
                                if (!goodsData.hasMore()) {
                                    adapter.loadMoreEnd();
                                }
                            }

                            if (goodsData.getDataList().isEmpty()) {
                                if (TextUtils.equals(mLikeMode, "0")) {
                                    SPUtils.getInstance().put(Constant.PREF.LIKE_MODE, "1");
                                    mLikeMode = "1";
                                } else {
                                    if (queryParam.page == 1) { //第一页 无数据
                                        LoadingStatusView.Status status = LoadingStatusView.Status.EMPTY;
                                        loadingStatusView.setStatus(status);
                                        loadingStatusView.setText("换个分类看看吧~");
                                    }
                                }
                            }


                            //进行深度数据上报
                            if (goodsData.getDataList().size() > 0) {
                                List<GoodsEntity> goodsEntityList = adapter.getData();
                                int index = goodsEntityList.indexOf(goodsData.getDataList().get(0));
                                for (int i = index; i < goodsEntityList.size(); i++) {
                                    GoodsEntity goodsEntity = goodsEntityList.get(i);
                                    SndoData.event(SndoData.XLT_EVENT_RECOMMEN_POSITION_SHOW,
                                            "is_power", true,
                                            "power_position", "猜你喜欢",
                                            "power_model", "null",
                                            "activity_time", "null",
                                            SndoData.XLT_GOOD_ID, goodsEntity.get_id(),
                                            "xlt_item_firstcate_title", "null",
                                            "xlt_item_thirdcate_title", "null",
                                            "xlt_item_secondcate_title", "null",
                                            "good_name", goodsEntity.getItem_title(),
                                            SndoData.XLT_ITEM_PLACE, String.valueOf(i + 1),
                                            SndoData.XLT_ITEM_SOURCE, goodsEntity.getItem_source()
                                    );
                                }
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
        queryParam.page = 1;
        loadGoods();
    }

    private void loadGoods() {
        if (TextUtils.equals(mLikeMode, "1")) {
            goodsViewModel.getLikeGoodsList(queryParam, "1", "7", gooodLiveData);
        } else {
            goodsViewModel.getLikeGoodsList(queryParam, "1", "9", gooodLiveData);
        }
    }

    private void initView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        AppBarLayout appBarLayout = findViewById(R.id.appbar);

//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int pos) {
//                if (pos > -400) {
//                    scroll_to_top.setVisibility(View.GONE);
//                } else {
//                    scroll_to_top.setVisibility(View.VISIBLE);
//                }
//            }
//        });


        refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData();
            }
        });

        CommonUtil.setRefreshHeaderWhiteText(refreshLayout);

        filterView = findViewById(R.id.filterview);

        mFloorListView = findViewById(R.id.listview);
        ImageView scroll_to_top = findViewById(R.id.scroll_to_top);
        scroll_to_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFloorListView.scrollToPosition(0);
                scroll_to_top.setVisibility(View.GONE);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        mFloorListView.setLayoutManager(gridLayoutManager);
        mFloorListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 判断是否滚动超过一屏
                    if (firstVisibleItemPosition == 0) {
                        scroll_to_top.setVisibility(View.GONE);
                    } else {
                        //显示回到顶部按钮
                        scroll_to_top.setVisibility(View.VISIBLE);
                    }
                    //获取RecyclerView滑动时候的状态
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
//        GridSpaceItemDecoration dividerItemDecoration = new GridSpaceItemDecoration(DeviceUtil.dip2px(mContext, 10));
//        dividerItemDecoration.setSpanCount(2);
//        mFloorListView.addItemDecoration(dividerItemDecoration);


        adapter = new CategoryListAdapter();
        mFloorListView.setAdapter(adapter);


        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goodsEntity = GuessLikeFrag.this.adapter.getData().get(position);
                GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), goodsEntity);
                DataCache.firstCategory = DataCache.homeFirstCategory;
                DataCache.thirdCategory = null;
                DataCache.reportGoodsByCategory(goodsEntity, position);
                SndoData.reportGuessLikeItem(goodsEntity, position);
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
//        RecycleViewScrollToTop.addScroolToTop(mFloorListView,scroll_to_top, (StaggeredGridLayoutManager) mFloorListView.getLayoutManager());
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
    }

}
