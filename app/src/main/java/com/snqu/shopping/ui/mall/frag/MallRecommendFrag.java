package com.snqu.shopping.ui.mall.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.LazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.common.ui.SpacesItemDecoration;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.mall.entity.MallRecommendEntity;
import com.snqu.shopping.ui.main.frag.WebViewFrag;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.mall.adapter.MallRecommendAdapter;
import com.snqu.shopping.ui.mall.goods.ShopGoodsDetailActivity;
import com.snqu.shopping.ui.mall.viewmodel.MallViewModel;
import com.snqu.shopping.util.GlideUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 自供-推荐
 */
public class MallRecommendFrag extends LazyFragment {
    private MallRecommendAdapter adapter;
    private MallViewModel mallViewModel;
    private LoadingStatusView loadingStatusView;
    private int page = 1;
    public MutableLiveData<NetReqResult> liveData = new MutableLiveData<>();

    @Override
    protected int getLayoutId() {
        return R.layout.mall_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        page = 1;
        liveData = new MutableLiveData<>();
        addAction(Constant.Event.MALL_RECOM_REFRESH);
        initView();
        initData();
    }

    private void initView() {
        RecyclerView recycleView = findViewById(R.id.recycleView);
        recycleView.setLayoutManager(new LinearLayoutManager(mContext));
        //间隔
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(DeviceUtil.dip2px(mContext, 14), LinearLayoutManager.VERTICAL);
        recycleView.addItemDecoration(spacesItemDecoration);

        adapter = new MallRecommendAdapter(new ArrayList<>());
        recycleView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MallRecommendEntity recommendEntity = (MallRecommendEntity) adapter.getData().get(position);
                if (recommendEntity.place == 1) { //活动
                    WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                    webViewParam.url = GlideUtil.checkUrl(recommendEntity.url);
                    WebViewFrag.start(mContext, webViewParam);
                } else { //商品详情
                    ShopGoodsDetailActivity.start(mContext, recommendEntity.goods_details);
                }
            }
        });
        adapter.setLoadMoreView(new CommonLoadingMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, recycleView);

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
                if (TextUtils.equals(netReqResult.tag, MallViewModel.TAG_RECOMMEND)) {
                    if (netReqResult.successful) {
                        ResponseDataArray<MallRecommendEntity> goodsData = (ResponseDataArray<MallRecommendEntity>) netReqResult.data;
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
                            ToastUtil.show(netReqResult.message);
                        }
                    }
                }
            }
        });

        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent pushEvent) {
        if (TextUtils.equals(pushEvent.getAction(), Constant.Event.MALL_RECOM_REFRESH)) {
            if (TextUtils.equals((CharSequence) pushEvent.getData(), MallFrag.RECOMMEND_ID)) {
                refresh();
            }
        }
    }

    private void loadData() {
        mallViewModel.getRecommend(page, liveData);
    }

    public void refresh() {
        page = 1;
        loadData();
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
