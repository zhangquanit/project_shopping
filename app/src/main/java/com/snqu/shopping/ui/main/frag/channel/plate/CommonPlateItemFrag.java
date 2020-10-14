package com.snqu.shopping.ui.main.frag.channel.plate;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.LazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.main.adapter.GoodListAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.RecycleViewScrollToTop;
import com.snqu.shopping.util.statistics.DataCache;

/**
 * @author 张全
 */
public class CommonPlateItemFrag extends LazyFragment {
    private SmartRefreshLayout refreshLayout;
    private GoodListAdapter adapter;
    private String plate;
    private static final String PARAM_PLATE = "PARAM_PLATE";
    private HomeViewModel mHomeViewModel;
    private GoodsQueryParam queryParam = new GoodsQueryParam();
    private MutableLiveData<NetReqResult> mLiveData = new MutableLiveData<>();
    private LoadingStatusView loadingStatusView;
    private CategoryEntity categoryEntity;

    public static Bundle getParam(CategoryEntity categoryEntity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_PLATE, categoryEntity);
        return bundle;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.plate_item_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        categoryEntity = (CategoryEntity) getArguments().getSerializable(PARAM_PLATE);
        plate = categoryEntity._id;
        queryParam.plate = plate;

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
        RecyclerView recyclerView = findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new GoodListAdapter();
        recyclerView.setAdapter(adapter);

        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goodsEntity = (GoodsEntity) adapter.getData().get(position);
                GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), categoryEntity.pid, categoryEntity._id, 0,goodsEntity);
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
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        mLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_GOO0D_LIST:
                        refreshLayout.finishRefresh(netReqResult.successful);
                        if (netReqResult.successful) {
                            ResponseDataArray<GoodsEntity> goodsData = (ResponseDataArray<GoodsEntity>) netReqResult.data;
                            if (queryParam.page == 1) {
                                adapter.setNewData(goodsData.getDataList());
                                if (goodsData.getDataList().isEmpty()) {
                                    loadingStatusView.setStatus(LoadingStatusView.Status.EMPTY);
                                    loadingStatusView.setText("换个分类看看吧~");
                                }
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


    private void loadData() {
        mHomeViewModel.getGoodList(queryParam, mLiveData);
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
