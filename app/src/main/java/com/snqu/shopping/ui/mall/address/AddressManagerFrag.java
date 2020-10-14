package com.snqu.shopping.ui.mall.address;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.common.ui.SpacesItemDecoration;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.mall.entity.address.AddressEntity;
import com.snqu.shopping.ui.mall.adapter.AddressManagerAdapter;
import com.snqu.shopping.ui.mall.viewmodel.AddressViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * 收货地址列表
 *
 * @author 张全
 */
public class AddressManagerFrag extends SimpleFrag {
    private static final String PARAM = "PARAM";
    private static final String PARAM_SELECT = "PARAM_SELECT";
    private SmartRefreshLayout mSmartRefreshLayout;
    private RecyclerView mAddressListView;
    private AddressManagerAdapter mAddressManagerAdapter;
    private AddressViewModel mAddressViewModel;
    private AddressEntity mCurAddress;
    private View btn_add;
    private LoadingStatusView loadingStatusView;
    private final int MAX_SIZE = 10;
    private boolean selectAddress;

    public static void start(Context ctx) {
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("收货地址", AddressManagerFrag.class));
    }

    /**
     * 下单选择地址
     *
     * @param ctx
     * @param addressEntity 用户当前选择的地址
     */
    public static void startForOrder(Context ctx, AddressEntity addressEntity) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(PARAM_SELECT, true);
        if (null != addressEntity) {
            bundle.putSerializable(PARAM, addressEntity);
        }
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("收货地址", AddressManagerFrag.class, bundle));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.address_manager_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, true, getTitleBar());
        addAction(Constant.Event.ADDRESS_UPDATE);

        Bundle arguments = getArguments();
        if (null != arguments && arguments.containsKey(PARAM)) {
            mCurAddress = (AddressEntity) arguments.getSerializable(PARAM);
        }
        if (null != arguments && arguments.containsKey(PARAM_SELECT)) {
            selectAddress = arguments.getBoolean(PARAM_SELECT, false);
        }

        initView();
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        if (TextUtils.equals(event.getAction(), Constant.Event.ADDRESS_UPDATE)) { //更新地址
            mAddressViewModel.getAddressList();
        }
    }

    private void initView() {
        getTitleBar().setBackgroundColor(Color.WHITE);
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddressManagerAdapter.getData().size() >= MAX_SIZE) {
                    ToastUtil.show("超出最大地址数量，请删除无效地址");
                    return;
                }
                AddressAddFrag.start(mContext);
            }
        });


        mSmartRefreshLayout = findViewById(R.id.refreshlayout);
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mAddressViewModel.getAddressList();
            }
        });

        mAddressListView = findViewById(R.id.address_list);
        mAddressListView.addItemDecoration(new SpacesItemDecoration(10, LinearLayoutManager.VERTICAL));
        mAddressListView.setLayoutManager(new LinearLayoutManager(getContext()));


        loadingStatusView = findViewById(R.id.emptyView);
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddressViewModel.getAddressList();
            }
        });

        mAddressManagerAdapter = new AddressManagerAdapter();
        mAddressListView.setAdapter(mAddressManagerAdapter);

        mAddressManagerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (selectAddress) { //选择地址
                    EventBus.getDefault().post(new PushEvent(Constant.Event.ADDRESS_MANAGER_ITEM, mAddressManagerAdapter.getItem(position)));
                    finish();
                } else {  //编辑地址
                    AddressAddFrag.start(mContext, mAddressManagerAdapter.getData().get(position));
                }
//                if (null != mCurAddress) { //选择地址
//                    if (!TextUtils.equals(mCurAddress._id, mAddressManagerAdapter.getItem(position)._id)) { //切换地址
//                        EventBus.getDefault().post(new PushEvent(Constant.Event.ADDRESS_MANAGER_ITEM, mAddressManagerAdapter.getItem(position)));
//                        finish();
//                    }
//                } else {  //编辑地址
//                    AddressAddFrag.start(mContext, mAddressManagerAdapter.getData().get(position));
//                }
            }
        });
        mAddressManagerAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.item_edit) { //编辑
                    AddressAddFrag.start(mContext, (AddressEntity) adapter.getData().get(position));
                }
            }
        });
    }

    private void initData() {
        mAddressViewModel = ViewModelProviders.of(this).get(AddressViewModel.class);
        mAddressViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case AddressViewModel.TAG_LIST:
                        mSmartRefreshLayout.finishRefresh(netReqResult.successful);
                        if (!netReqResult.successful) { //刷新失败
                            if (mAddressManagerAdapter.getData().isEmpty()) {
                                loadingStatusView.setStatus(LoadingStatusView.Status.FAIL);
                            }
                            return;
                        }

                        List<AddressEntity> addressEntities = (List<AddressEntity>) netReqResult.data;
                        if (null == addressEntities) {
                            addressEntities = new ArrayList<>();
                        }

                        mAddressViewModel.updateUserAddress(addressEntities);

                        int selPos = -1;
                        if (null != mCurAddress) {
                            for (int i = 0; i < addressEntities.size(); i++) {
                                if (TextUtils.equals(addressEntities.get(i)._id, mCurAddress._id)) {
                                    selPos = i;
                                    break;
                                }
                            }
                        }

                        btn_add.setBackgroundResource(addressEntities.size() < MAX_SIZE ? R.drawable.btn_address_add_normal : R.drawable.btn_address_add_disabled);
                        mAddressManagerAdapter.refreshData(addressEntities, selPos);

                        //空数据
                        boolean empty = mAddressManagerAdapter.getData().isEmpty();
                        loadingStatusView.setVisibility(empty ? View.VISIBLE : View.GONE);
                        if (empty) {
                            LoadingStatusView.Status status = LoadingStatusView.Status.EMPTY;
                            loadingStatusView.setStatus(status);
                            loadingStatusView.setText("您还没有收货地址");
                        }

                        break;
                    case AddressViewModel.TAG_DEL:  //删除
                        if (netReqResult.successful) {
                            mAddressViewModel.getAddressList();
                        }
                        ToastUtil.show(netReqResult.successful ? "删除成功" : "删除失败");
                        break;
                }
            }
        });
        mAddressViewModel.getAddressList();
    }
}
