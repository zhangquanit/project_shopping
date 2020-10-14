package com.snqu.xlt.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.util.pay.PayWay;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;

/**
 * 微信支付回调页面
 *
 * @author 张全
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_pay_result);
        api = WXAPIFactory.createWXAPI(this, LContext.getString(R.string.wx_appid));
        api.handleIntent(getIntent(), this);
        LogUtil.d(TAG, "WXPayEntryActivity............onCreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        LogUtil.d(TAG, "WXPayEntryActivity............onNewIntent");
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (null == resp) {
            finish();
            return;
        }
        printInfo(resp);
        LogUtil.d(TAG, "WXPayEntryActivity,onResp...onPayFinish, errCode = "
                + resp.errCode + ",type=" + resp.getType());

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0:// 成功
                    EventBus.getDefault().post(new PushEvent(Constant.Event.ORDER_BUY_SUCCESS, PayWay.WX));
                    finish();
                    break;
                case -1:// 错误
                    /*
                     * 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
                     */
                    EventBus.getDefault().post(new PushEvent(Constant.Event.ORDER_BUY_FAIL, PayWay.WX));
                    ToastUtil.show("支付失败");
                    finish();
                    break;
                case -2:// 用户取消
                    /*
                     * 无需处理。发生场景：用户不支付了，点击取消，返回APP。
                     */
                    EventBus.getDefault().post(new PushEvent(Constant.Event.ORDER_BUY_CANCEL, PayWay.WX));
                    ToastUtil.show("取消支付");
                    finish();
                    break;
                default:
                    finish();
                    break;
            }
        }
    }

    public static void printInfo(BaseResp baseResp) {
        if (!LContext.isDebug || null == baseResp) {
            return;
        }
        LogUtil.d(TAG, "---------微信支付--------start");
        Field[] fields = baseResp.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(baseResp);
                LogUtil.d(TAG, field.getName() + "=" + value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LogUtil.d(TAG, "---------微信支付--------end");
    }

}