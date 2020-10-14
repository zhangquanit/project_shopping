package com.snqu.xlt.wxapi;
import android.content.ClipData;
import android.content.ClipboardManager;

import com.snqu.shopping.BuildConfig;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.util.statistics.AnalysisUtil;
import com.snqu.shopping.util.statistics.UmengAnalysisUtil;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * 微信分享回调页面
 */
public class WXEntryActivity extends WXCallbackActivity {

    @Override
    public void onResp(BaseResp resp) {
        if (resp instanceof SendAuth.Resp) { // 授权
            SendAuth.Resp authResp = (SendAuth.Resp) resp;
            int errorCode = authResp.errCode;
            UmengAnalysisUtil.onEvent(UmengAnalysisUtil.WEIXIN_LOGIN, "deviceId", AnalysisUtil.getUniqueId(), "ua", AnalysisUtil.getUA(), "operation", "微信登录回调errorCode=" + errorCode);
            if (errorCode == BaseResp.ErrCode.ERR_OK) { // 用户同意
                String code = authResp.code;
                addToClip(code);
                EventBus.getDefault().post(new PushEvent(Constant.Event.WX_CODE, code));
            } else if (errorCode == BaseResp.ErrCode.ERR_AUTH_DENIED) { // 用户拒绝授权
                EventBus.getDefault().post(new PushEvent(Constant.Event.WX_CODE, "0"));
            } else if (errorCode == BaseResp.ErrCode.ERR_USER_CANCEL) { // 取消授权
                EventBus.getDefault().post(new PushEvent(Constant.Event.WX_CODE, "1"));
            } else {
                EventBus.getDefault().post(new PushEvent(Constant.Event.WX_CODE, "1"));
            }
            finish();
        } else { // 分享
            super.onResp(resp);
        }

    }

    private void addToClip(String code) {
        if (BuildConfig.DEBUG) {
            ClipboardManager myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData myClip = ClipData.newPlainText("levelText", code);
            myClipboard.setPrimaryClip(myClip);
        }
    }
}