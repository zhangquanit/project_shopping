package com.snqu.shopping.util.pay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.android.util.ext.ToastUtil;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.util.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * 支付
 *
 * @author 张全
 */
public class OrderPay {
    private static final int SDK_PAY_FLAG = 1;
    private AliPayCallBack aliPayCallBack;

    public void wxPay(Context ctx, OrderPayResponse orderPayResponse) {
        WXHelper wxHelper = new WXHelper(ctx);
        wxHelper.pay(orderPayResponse);
    }

    public void alipay(Activity ctx, String orderInfo) {
        alipay(ctx, orderInfo, null);
    }

    public void alipay(Activity ctx, String orderInfo, AliPayCallBack aliPayCallBack) {
        if (!CommonUtil.checkAliPayInstalled(ctx)) { //未安装支付宝
            ToastUtil.show(R.string.alipay_not_support);
            return;
        }
        this.aliPayCallBack = aliPayCallBack;
        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(ctx);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) { //支付成功
                        if (null != aliPayCallBack) {
                            aliPayCallBack.success();
                        } else {
                            EventBus.getDefault().post(new PushEvent(Constant.Event.ORDER_BUY_SUCCESS, PayWay.ZHIBAO));
                        }
                    } else if (TextUtils.equals(resultStatus, "6001")) { //取消支付
                        if (null != aliPayCallBack) {
                            aliPayCallBack.cancel();
                        } else {
                            EventBus.getDefault().post(new PushEvent(Constant.Event.ORDER_BUY_CANCEL, PayWay.ZHIBAO));
                        }
                    } else {  //支付失败
                        if (null != aliPayCallBack) {
                            aliPayCallBack.fail();
                        } else {
                            EventBus.getDefault().post(new PushEvent(Constant.Event.ORDER_BUY_FAIL, PayWay.ZHIBAO));
                        }
                    }
                    break;
                }
            }

        }
    };
}
