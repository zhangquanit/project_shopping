package com.snqu.shopping.ui.goods.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.alibaba.baichuan.android.trade.AlibcTrade
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback
import com.alibaba.baichuan.android.trade.model.AlibcShowParams
import com.alibaba.baichuan.android.trade.model.OpenType
import com.alibaba.baichuan.trade.biz.applink.adapter.AlibcFailModeType
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams
import com.alibaba.baichuan.trade.common.utils.AlibcLogger
import com.android.util.LContext
import com.android.util.ext.ToastUtil
import com.kepler.jd.Listener.OpenAppAction
import com.kepler.jd.login.KeplerApiManager
import com.kepler.jd.sdk.bean.KeplerAttachParameter
import com.kepler.jd.sdk.bean.KeplerGlobalParameter
import com.snqu.shopping.R
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.util.AppInstallUtil
import com.snqu.shopping.util.CommonUtil
import com.snqu.shopping.util.log.LogClient
import java.util.*


/**
 * desc:
 * time: 2019/9/2
 * @author 银进
 */
object JumpUtil {


    const val TAG = "JumpUtil"

    @JvmField
    var isShowShare = false

    @JvmStatic
    fun jumpToH5(context: Context, appUrl: String?, urlClick: String, isDetailJump: Boolean) {
        CommonUtil.addToClipboard(null)

        if (TextUtils.isEmpty(appUrl)) {
            jumpToWeb(urlClick, context, isDetailJump)
            return
        }

        LogClient.log(TAG, "打开其他平台,是否原生打开=" + (AppInstallUtil.isAppInstalled(context, "com.other")) + ",appUrl=" + appUrl + ",url=" + urlClick + ",是否详情跳转=" + isDetailJump)

        if (appUrl != null) {
            if (appUrl.startsWith("http")) {
                if (urlClick.isNotEmpty()) {
                    jumpToWeb(urlClick, context, true)
                }
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appUrl))
                // 能够通过系统intent跳转，并且appurl不为空，才进行跳转，否则就走H5跳转流程
                if (intent.resolveActivity(context.packageManager) != null && !TextUtils.isEmpty(appUrl)) {
                    context.startActivity(intent)
                } else {
                    if (urlClick.isNotEmpty()) {
                        jumpToWeb(urlClick, context, true)
                    }
                }
            }
        }
    }

    /**
     * 跳转到京东
     */
    @JvmStatic
    fun jumpToJdCouponsPage(context: Context, urlClick: String, mOpenAppAction: OpenAppAction) {
        CommonUtil.addToClipboard(null)
        LogClient.log(TAG, "打开京东,是否原生打开=" + (AppInstallUtil.isAppInstalled(context, "com.jingdong.app.mall")) + ",url=" + urlClick)
        try {
            if (AppInstallUtil.isAppInstalled(context, "com.jingdong.app.mall")) {
                //需要申请
                KeplerGlobalParameter.getSingleton().jDappBackTagID = "kpl_jdc82bf597dd77946e645ea8e2b904618d"
                KeplerApiManager.getWebViewService().openJDUrlPage(urlClick, KeplerAttachParameter(), context, mOpenAppAction, 15)
            } else {
                KeplerApiManager.getWebViewService().openJDUrlWebViewPage(urlClick, KeplerAttachParameter())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 跳转pdd
     */
    @JvmStatic
    fun jumpToPdd(context: Context, urlClick: String, isDetailJump: Boolean) {
        CommonUtil.addToClipboard(null)
        LogClient.log(TAG, "打开拼多多,是否原生打开=" + (AppInstallUtil.isAppInstalled(context, "com.xunmeng.pinduoduo")) + ",url=" + urlClick + ",是否详情跳转=" + isDetailJump)
        try {
            if (AppInstallUtil.isAppInstalled(context, "com.xunmeng.pinduoduo")) {
                val buffer = StringBuffer("pddopen://?h5Url=")
                buffer.append(Uri.encode(urlClick))
                buffer.append("&backUrl=xlts://")
                buffer.append("&packageId=com.snqu.xlt")
                buffer.append("&appKey=36d222e228f343dfb16d6297312e970a")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(buffer.toString()))
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    if (urlClick.isNotEmpty()) {
                        jumpToWeb(urlClick, context, isDetailJump)
                    }
                }
            } else {
                if (urlClick.isNotEmpty()) {
                    jumpToWeb(urlClick, context, isDetailJump)
                }
            }
        } catch (e: Exception) {

        }
    }

    /**
     * 跳转唯品会
     */
    @JvmStatic
    fun jumpToVPage(context: Context, appUrl: String?, urlClick: String, isDetailJump: Boolean) {
        CommonUtil.addToClipboard(null)

        if (TextUtils.isEmpty(appUrl)) {
            jumpToWeb(urlClick, context, isDetailJump)
            return
        }

        LogClient.log(TAG, "打开唯品会,是否原生打开=" + (AppInstallUtil.isAppInstalled(context, "com.achievo.vipshop")) + ",appUrl=" + appUrl + ",url=" + urlClick + ",是否详情跳转=" + isDetailJump)

        try {
            if (AppInstallUtil.isAppInstalled(context, "com.achievo.vipshop")) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appUrl))
                // 能够通过系统intent跳转，并且appurl不为空，才进行跳转，否则就走H5跳转流程
                if (intent.resolveActivity(context.packageManager) != null && !TextUtils.isEmpty(appUrl)) {
                    context.startActivity(intent)
                } else {
                    if (urlClick.isNotEmpty()) {
                        jumpToWeb(urlClick, context, isDetailJump)
                    }
                }
            } else {
                if (urlClick.isNotEmpty()) {
                    jumpToWeb(urlClick, context, isDetailJump)
                }
            }
        } catch (e: Exception) {

        }
    }

    fun jumpToSuning(activity: Activity, url: String, isDetailJump: Boolean) {

        LogClient.log(TAG, "打开苏宁,是否原生打开=" + (AppInstallUtil.isAppInstalled(activity, "com.suning.mobile.ebuy")) + ",url=" + url + ",是否详情跳转=" + isDetailJump)
        if (TextUtils.isEmpty(url)) {
            return
        }
        val intent = Intent("android.intent.action.VIEW", Uri.parse("suning://m.suning.com/index?adTypeCode=1165&adId=56238165301063684098&wap_source=wap-app&wap_medium=pgsy&utm_source=union&utm_medium=27&adtype=5&utm_campaign=658648d6-6a43-4d90-91bf-82e7d31c0269&union_place=un"))
        intent
                .setFlags(0x10000000)
        activity.startActivity(intent)
//        if (AppInstallUtil.isAppInstalled(activity, "com.suning.mobile.ebuy") || TextUtils.isEmpty(url)) {
//            jumpToWeb(url, activity, isDetailJump)
////            val decode: String = URLDecoder.decode(StringUtils.a(this.p.getWapExtendUrl()))
////            if (TextUtils.isEmpty(decode)) {
////                ToastUtils.a(this.mContext, "苏宁详情不存在")
////            } else {
////                PageManager.c(this.mContext, decode, "商品详情")
////            }
//        } else {
//            try {
//                val intent = Intent("android.intent.action.VIEW", Uri.parse(url))
//                if (intent.resolveActivity(activity.packageManager) != null) {
//                    activity.startActivity(intent)
//                } else {
////                    if (urlClick.isNotEmpty()) {
////                        jumpToWeb(urlClick, context, isDetailJump)
////                    }
//                }
////                intent.flags = CommonNetImpl.FLAG_AUTH
////                startActivity(intent)
//            } catch (e2: java.lang.Exception) {
//                e2.printStackTrace()
//            }
//        }
    }

    /**
     * 跳转淘宝
     *
     * @param activity
     * @param url
     */
    @JvmStatic
    fun jumpToToAli(activity: Activity, url: String, isDetailJump: Boolean) {
        CommonUtil.addToClipboard(null)
        LogClient.log(TAG, "打开淘宝,是否原生打开=" + (AppInstallUtil.isAppInstalled(activity, "com.taobao.taobao")) + ",url=" + url + ",是否详情跳转=" + isDetailJump)
        if (AppInstallUtil.isAppInstalled(activity, "com.taobao.taobao")) {
            val showParams = AlibcShowParams()
            showParams.openType = OpenType.Native
            showParams.clientType = "taobao"
            showParams.backUrl = "xlts://"
            showParams.nativeOpenFailedMode = AlibcFailModeType.AlibcNativeFailModeJumpH5
            val taokeParams = AlibcTaokeParams("", "", "")
            taokeParams.setPid(LContext.getString(R.string.ali_pid))
            val trackParams: Map<String, String> = HashMap()
            // 通过百川内部的webview打开页面
            AlibcTrade.openByUrl(activity, "", url, null,
                    WebViewClient(), WebChromeClient(),
                    showParams, taokeParams, trackParams, object : AlibcTradeCallback {
                override fun onTradeSuccess(tradeResult: AlibcTradeResult) {
                    AlibcLogger.i("MainActivity", "request success")
                }

                override fun onFailure(code: Int, msg: String) {
                    AlibcLogger.e("MainActivity", "code=$code, msg=$msg")
                    if (code == -1) {
                    }
                }
            })
        } else {
            ToastUtil.show(R.string.tb_install_alert)
            jumpToWeb(url, activity, isDetailJump)
        }
    }

    private fun jumpToWeb(urlClick: String, context: Context, isDetailJump: Boolean) {
        val webViewParam = WebViewFrag.WebViewParam()
        webViewParam.url = urlClick
        webViewParam.isDetailJump = isDetailJump
        webViewParam.isShowShare = isShowShare
        webViewParam.isPromotionLinkJump = isShowShare
        WebViewFrag.start(context, webViewParam)
        isShowShare = false
    }

    @JvmStatic
    fun openWechat(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.data = Uri.parse("weixin://")
        context.startActivity(intent)
    }

    @JvmStatic
    fun authPdd(context: Context, authUrl: String?) {
        val webViewParam = WebViewFrag.WebViewParam()
        webViewParam.url = authUrl
        webViewParam.isAuthJump = true
        WebViewFrag.start(context, webViewParam)
    }
}