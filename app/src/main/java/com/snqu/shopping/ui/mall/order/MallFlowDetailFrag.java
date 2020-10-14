package com.snqu.shopping.ui.mall.order;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.snqu.shopping.R;
import com.snqu.shopping.data.mall.entity.flow.FlowEntity;
import com.snqu.shopping.ui.mall.adapter.MallFlowListAdapter;
import com.snqu.shopping.util.CommonUtil;

/**
 * 物流详情
 */
public class MallFlowDetailFrag extends SimpleFrag {
    private static final String PARAM = "PARAM";
    private FlowEntity flowEntity;

    public static void start(Context ctx, FlowEntity flowEntity) {
        if (null == flowEntity || TextUtils.isEmpty(flowEntity.no)) return;
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARAM, flowEntity);

        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("", MallFlowDetailFrag.class, bundle).hideTitleBar(true));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mall_flow_detail_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, false);

        FlowEntity flowEntity = getArguments().getParcelable(PARAM);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        TextView tv_name = findViewById(R.id.tv_name);
        tv_name.setText(flowEntity.name);
        TextView tv_no = findViewById(R.id.tv_no);
        tv_no.setText("快递单号：" + flowEntity.no);
        TextView tv_copy = findViewById(R.id.tv_copy);
        tv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.addToClipboard(flowEntity.no);
                ToastUtil.show("复制成功");
            }
        });

        RecyclerView recyclerView = findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        MallFlowListAdapter adapter = new MallFlowListAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setNewData(flowEntity.detail);

    }
}
