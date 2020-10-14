package com.snqu.shopping.ui.mall.frag;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.ui.mall.address.AddressManagerFrag;
import com.snqu.shopping.ui.mall.order.MallOrderFrag;
import com.snqu.shopping.util.GlideUtil;

/**
 * 我的
 */
public class MallUserFrag extends SimpleFrag {
    public static void start(Context ctx) {
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("我的", MallUserFrag.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mall_user_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, true, getTitleBar());
        initView();
    }

    private void initView() {
        getTitleBar().setBackgroundColor(Color.WHITE);

        UserEntity user = UserClient.getUser();
        //
        GlideUtil.loadPic(findViewById(R.id.iv_user_head), user.avatar, R.drawable.icon_default_head, R.drawable.icon_default_head);
        //
        TextView tv_nickname = findViewById(R.id.tv_nickname);
        tv_nickname.setText(user.username);

        findViewById(R.id.rl_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MallOrderFrag.start(mContext);
            }
        });
        findViewById(R.id.rl_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressManagerFrag.start(mContext);
            }
        });
        findViewById(R.id.rl_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MallContactFrag.start(mContext);
            }
        });
    }
}
