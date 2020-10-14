package com.snqu.shopping.ui.mall.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.os.DeviceUtil;
import com.anroid.base.LazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.mall.entity.MallCategoryEntity;
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.mall.adapter.MallCategoryListAdapter;
import com.snqu.shopping.ui.mall.goods.ShopGoodsDetailActivity;
import com.snqu.shopping.ui.mall.viewmodel.MallViewModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 自供-分类
 */
public class MallCategoryFrag extends LazyFragment {
    private static final String PARAM = "MallEntity";

    private MallViewModel mallViewModel;
    private MallCategoryEntity categoryEntity;
    private MallCategoryListAdapter adapter;
    private LoadingStatusView loadingStatusView;

    public MutableLiveData<NetReqResult> liveData = new MutableLiveData<>();

    private int page = 1;
    private String id;

    public static Bundle getParam(MallCategoryEntity categoryEntity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM, categoryEntity);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mall_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        addAction(Constant.Event.MALL_RECOM_REFRESH);
        page = 1;
        liveData = new MutableLiveData<>();
        categoryEntity = (MallCategoryEntity) getArguments().getSerializable(PARAM);
        id = categoryEntity._id;
        initView();
        initData();
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycleView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new MallCategoryListAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ShopGoodsEntity goods = (ShopGoodsEntity) adapter.getData().get(position);
                ShopGoodsDetailActivity.start(mContext, goods._id);
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
        loadingStatusView.setContentViewTop(DeviceUtil.dip2px(mContext, 15));
        loadingStatusView.setLoadingViewTop(DeviceUtil.dip2px(mContext, 30));
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        adapter.setEmptyView(loadingStatusView);
    }

    private void initData() {
        mallViewModel = ViewModelProviders.of(this).get(MallViewModel.class);
        liveData.removeObservers(getLifecycleOwner());
        liveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, MallViewModel.TAG_CATEGORY_GOODS)) {
                    if (netReqResult.successful) {
                        ResponseDataArray<ShopGoodsEntity> goodsData = (ResponseDataArray<ShopGoodsEntity>) netReqResult.data;
                        if (page == 1) {
                            adapter.setNewData(goodsData.getDataList());
                        } else if (!goodsData.getDataList().isEmpty()) {
                            adapter.addData(goodsData.getDataList());
                        }

                        if (goodsData.hasMore()) {
                            page++;
                            adapter.loadMoreComplete(); //刷新成功
                        } else {
                            adapter.loadMoreEnd(page == 1);//无下一页
                        }

                        if (page == 1 && goodsData.getDataList().isEmpty()) { //第一页 无数据
                            LoadingStatusView.Status status = LoadingStatusView.Status.EMPTY;
                            loadingStatusView.setStatus(status);
                            loadingStatusView.setText("暂无数据");
                        }

                    } else {
                        if (page > 1) { //加载下一页数据失败
                            adapter.loadMoreFail();
                        } else if (adapter.getData().isEmpty()) { //第一页  无数据
                            LoadingStatusView.Status status = LoadingStatusView.Status.FAIL;
                            loadingStatusView.setStatus(status);
                        } else { //下拉刷新失败
//                            ToastUtil.show(netReqResult.message);
                        }
                    }
                }
            }
        });

        loadData();
    }

    public void setData(MallCategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
        this.id = categoryEntity._id;
    }

    public void refresh() {
        page = 1;
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent pushEvent) {
        if (TextUtils.equals(pushEvent.getAction(), Constant.Event.MALL_RECOM_REFRESH)) {
            if (TextUtils.equals((CharSequence) pushEvent.getData(), id)) {
                refresh();
            }
        }
    }

    private void loadData() {
        mallViewModel.getCategeoryGoods(id, null, page, liveData);
    }

    @Override
    public void onFirstInit() {
//        page = 1;
//        loadData();
    }

    @Override
    public void onLazyResume() {

    }

    @Override
    public void onLazyPause() {

    }

}
