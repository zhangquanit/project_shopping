package com.snqu.shopping.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.util.pay.OrderPay;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileFilter;

/**
 * @author zhangquan
 */
public class TestFrag extends SimpleFrag {

    public static void start(Context ctx) {
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("支付测试", TestFrag.class, null));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.test_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, true, getTitleBar());

        addAction(Constant.Event.ORDER_BUY_SUCCESS);
        addAction(Constant.Event.ORDER_BUY_CANCEL);
        addAction(Constant.Event.ORDER_BUY_FAIL);

        TextView textView = findViewById(R.id.et);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = textView.getText().toString();
                if (TextUtils.isEmpty(data)) {
                    ToastUtil.show("不能为空");
                    return;
                }
                new OrderPay().alipay(mContext, data);
            }
        });

        FileFilter[] fileFilters = {null};

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent pushEvent) {
        if (TextUtils.equals(pushEvent.getAction(), Constant.Event.ORDER_BUY_SUCCESS)) {
            ToastUtil.show("支付成功");
        } else if (TextUtils.equals(pushEvent.getAction(), Constant.Event.ORDER_BUY_CANCEL)) {
            ToastUtil.show("取消支付");
        } else if (TextUtils.equals(pushEvent.getAction(), Constant.Event.ORDER_BUY_FAIL)) {
            ToastUtil.show("取消失败");
        }
    }
}
