package com.snqu.shopping.util;

import com.android.util.ext.ToastUtil;
import com.snqu.shopping.App;
import com.snqu.shopping.ui.login.hepler.WXLoginHelper;
import com.snqu.shopping.util.statistics.AnalysisUtil;
import com.snqu.shopping.util.statistics.UmengAnalysisUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import common.widget.dialog.loading.LoadingDialog;

/**
 * desc:
 * time: 2019/1/7
 *
 * @author 银进
 */
public class ThirdLoginUtil {


    public static void loginWX(LoadingDialog loadingDialog, int codePage) {
        UmengAnalysisUtil.onEvent(UmengAnalysisUtil.WEIXIN_LOGIN, "deviceId", AnalysisUtil.getUniqueId(), "ua", AnalysisUtil.getUA(), "operation", "开始微信登录");
        WXLoginHelper.setCodePage(codePage);
        if (!App.mApp.iwxapi.isWXAppInstalled()) {
            ToastUtil.show("请先安装微信客户端");
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_xlt";
        try {
            boolean successful = App.mApp.iwxapi.sendReq(req);
            UmengAnalysisUtil.onEvent(UmengAnalysisUtil.WEIXIN_LOGIN, "deviceId", AnalysisUtil.getUniqueId(), "ua", AnalysisUtil.getUA(), "operation", "打开微信successful=" + successful);
            if (successful) {
                if (loadingDialog != null && !loadingDialog.isShowing()) {
                    loadingDialog.show();
                }
            } else {
                ToastUtil.show("打开微信失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("打开微信失败");
        }
    }
}
