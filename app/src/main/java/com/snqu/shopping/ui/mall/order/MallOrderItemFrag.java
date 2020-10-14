package com.snqu.shopping.ui.mall.order;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
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
import com.snqu.shopping.common.ui.SpacesItemDecoration;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.mall.entity.MallOrderEntity;
import com.snqu.shopping.data.mall.entity.PayResultDataEntity;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.mall.adapter.MallOrderListAdapter;
import com.snqu.shopping.ui.mall.order.helper.MallCancelDialogView;
import com.snqu.shopping.ui.mall.order.helper.MallOrderType;
import com.snqu.shopping.ui.mall.viewmodel.MallViewModel;
import com.snqu.shopping.util.RecycleViewScrollToTop;
import com.snqu.shopping.util.pay.AliPayCallBack;
import com.snqu.shopping.util.pay.OrderPay;

import common.widget.dialog.EffectDialogBuilder;
import common.widget.dialog.loading.LoadingDialog;

/**
 * 订单列表
 */
public class MallOrderItemFrag extends LazyFragment {
    private static final String PARAM = "ORDER_TYPE";

    private MallViewModel mallViewModel;
    private MallOrderListAdapter adapter;
    private LoadingStatusView loadingStatusView;
    private SmartRefreshLayout smartRefreshLayout;
    private LoadingDialog loadingDialog;
    private MallOrderType orderType;
    public MutableLiveData<NetReqResult> liveData = new MutableLiveData<>();
    private int page = 1;

    public static Bundle getParam(MallOrderType orderType) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM, orderType);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mall_order_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        page = 1;
        liveData = new MutableLiveData<>();
        orderType = (MallOrderType) getArguments().getSerializable(PARAM);

        initView();
        initData();
    }

    private void initView() {
        smartRefreshLayout = findViewById(R.id.refreshlayout);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                loadData();
            }
        });
        RecyclerView recyclerView = findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new SpacesItemDecoration(10, LinearLayoutManager.VERTICAL));

        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        adapter = new MallOrderListAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MallOrderEntity order = (MallOrderEntity) adapter.getData().get(position);
                MallOrderDetailFrag.start(mContext, order._id);
            }
        });

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                MallOrderEntity orderEntity = (MallOrderEntity) adapter.getData().get(position);
                switch (view.getId()) {
                    case R.id.item_btn_cancel: //取消支付
                        MallCancelDialogView dialogView = new MallCancelDialogView(mContext)
                                .setLeftClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mallViewModel.orderCancel(orderEntity._id, liveData);
                                    }
                                });

                        new EffectDialogBuilder(mContext)
                                .setCancelable(false)
                                .setCancelableOnTouchOutside(false)
                                .setContentView(dialogView).show();
                        break;
                    case R.id.item_btn_pay: //去支付
                        payOrder(orderEntity._id);
                        break;
                }
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
        mallViewModel = ViewModelProviders.of(this).get(MallViewModel.class);
        liveData.removeObservers(getLifecycleOwner());
        liveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, MallViewModel.TAG_ORDER_LIST)) {
                    smartRefreshLayout.finishRefresh(netReqResult.successful);
                    if (netReqResult.successful) {
                        ResponseDataArray<MallOrderEntity> goodsData = (ResponseDataArray<MallOrderEntity>) netReqResult.data;
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
                } else if (TextUtils.equals(netReqResult.tag, MallViewModel.TAG_ORDER_CANCEL)) { //取消订单
                    if (netReqResult.successful) {
                        page = 1;
                        loadData();
                        ToastUtil.show("订单已取消");
                    } else {
                        ToastUtil.show(netReqResult.message);
                    }
                } else if (TextUtils.equals(netReqResult.tag, ApiHost.MALL_ORDER_RE_PAY)) { //订单支付
                    cancelLoading();
                    if (netReqResult.successful) {
                        PayResultDataEntity dataEntity = (PayResultDataEntity) netReqResult.data;
                        alipay(dataEntity.sign);
                    } else {
                        ToastUtil.show(netReqResult.message);
                    }
                }
            }
        });
    }

    public void setData(MallOrderType orderType) {
        this.orderType = orderType;
    }

    private void loadData() {
        mallViewModel.getOrderList(orderType.status, page, liveData);
    }

    private void payOrder(String orderId) {
        showLoading();
        mallViewModel.goRePay(orderId, null, liveData);
    }

    private void alipay(String payInfo) {
        new OrderPay().alipay(mContext, payInfo, new AliPayCallBack() {
            @Override
            public void success() {
                ToastUtil.show("支付成功");
                page = 1;
                loadData();
            }

            @Override
            public void fail() {
                ToastUtil.show("支付失败");
            }

            @Override
            public void cancel() {

            }
        });
    }

    private void showLoading() {
        loadingDialog = LoadingDialog.showBackCancelableDialog(mContext, "请稍候");
    }

    private void cancelLoading() {
        if (null != loadingDialog) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onFirstInit() {

    }

    @Override
    public void onLazyResume() {
        page = 1;
        loadData();
    }

    @Override
    public void onLazyPause() {

    }


}
