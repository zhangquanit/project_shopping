package com.snqu.shopping.ui.goods;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.trade.biz.applink.adapter.AlibcFailModeType;
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback;
import com.alibaba.baichuan.trade.common.utils.AlibcLogger;
import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.anroid.base.ui.StatusBar;
import com.anroid.base.ui.TitleBarView;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.data.DataConfig;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.GoodsClient;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;

import org.greenrobot.eventbus.EventBus;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import common.widget.dialog.loading.LoadingDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AliAuthActivity extends AppCompatActivity {
    private static final String PARAM = "URL";
    private boolean isSelfFinish;
    private WebView webView;
    private String url;

    public static void start(Context ctx, String authUrl) {
        Intent intent = new Intent(ctx, AliAuthActivity.class);
        intent.putExtra(PARAM, authUrl);
        ctx.startActivity(intent);
    }

    WebViewClient webViewClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        init(savedInstanceState);
    }

    public int getLayoutId() {
        return R.layout.webview_activity;
    }

    public void init(Bundle savedInstanceState) {
        // 网络无连接
        if (!NetworkUtil.isNetworkAvailable(this)) {
            ToastUtil.show(R.string.net_noconnection);
            finish();
            return;
        }


        TitleBarView titleBar = findViewById(R.id.titlebar);
        titleBar.setTitleText("淘宝授权");
        titleBar.setOnLeftBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelfFinish = true;
                finish();
            }
        });

        StatusBar.setStatusBar(this, true);

        url = getIntent().getStringExtra(PARAM);
        webView = findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
        //启用支持JavaScript
        settings.setJavaScriptEnabled(true);
        //启用支持DOM Storage
        settings.setDomStorageEnabled(true);

        webViewClient = new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.d("AlibcWebViewActivity", "shouldOverrideUrlLoading  url=" + url);
                if ((url.startsWith("https://api-t.xinletao.vip/taobao/auth")
                        || url.startsWith("https://api.xinletao.vip/taobao/auth")
                        || url.startsWith("https://api.xin1.cn/taobao/auth")
                        || url.startsWith("https://api-t.xin1.cn/taobao/auth")
                        || url.startsWith("https://api-t2.xin1.cn/taobao/auth")
                )) {
                    auth(url, null);
                    return true;
                }
                return false;
            }
        };
        webView.setWebViewClient(webViewClient);

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                LogUtil.d("AlibcWebViewActivity", "onDownloadStart url=" + url);
                // 方式1：跳转浏览器下载
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                startActivity(intent);
                //方式2：系统的下载服务
            }
        });

        if (!AlibcLogin.getInstance().isLogin()) {
            AlibcLogger.d("AlibcWebViewActivity", "开始登陆");
            AlibcLogin.getInstance().showLogin(new AlibcLoginCallback() {
                public void onSuccess(int var1x, String var2, String var3) {
                    AlibcLogger.d("AlibcWebViewActivity", "登录成功,重新加载页面");
                    openByUrl(url, webView);
                }

                public void onFailure(int var1x, String var2) {
                    if (!TextUtils.equals(var2, "用户取消登录")) {
                        ToastUtil.show(var2);
                    }
                    isSelfFinish = true;
                    finish();
                }
            });
        } else {
            AlibcLogger.d("AlibcWebViewActivity", "已登录  直接加载");
            openByUrl(url, webView);
        }
    }

    private void openByUrl(String url, WebView webView) {
        AlibcShowParams showParams = new AlibcShowParams();
        showParams.setOpenType(OpenType.Auto);
        showParams.setClientType("taobao");
        showParams.setBackUrl("xlts://");
        showParams.setNativeOpenFailedMode(AlibcFailModeType.AlibcNativeFailModeJumpH5);

        AlibcTaokeParams taokeParams = new AlibcTaokeParams("", "", "");
        taokeParams.setPid(LContext.getString(R.string.ali_pid));
        Map<String, String> trackParams = new HashMap<>();

        AlibcTrade.openByUrl(AliAuthActivity.this, "", url, webView,
                webViewClient, new WebChromeClient(),
                showParams, taokeParams, trackParams, new AlibcTradeCallback() {
                    @Override
                    public void onTradeSuccess(AlibcTradeResult tradeResult) {
                        LogUtil.d("AlibcWebViewActivity", "onTradeSuccess tradeResult=" + tradeResult);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        LogUtil.d("AlibcWebViewActivity", "onFailure code=" + code + ", msg=" + msg);
                        if (code == -1) {
                            ToastUtil.show(msg);
                        }
                    }
                });
    }

    @SuppressLint({"AutoDispose", "CheckResult"})
    private void auth(String callback, String authCode) {
        LoadingDialog loadingDialog = LoadingDialog.showDialog(this, "请稍候");
        String path = callback;
        if (!TextUtils.isEmpty(authCode)) {
            Uri uri = Uri.parse(URLDecoder.decode(url));
            String appkey = uri.getQueryParameter("appkey");
            String state = uri.getQueryParameter("state");
            path = DataConfig.API_HOST + "taobao/auth?app_source=1&appkey=" + appkey + "&state=" + state + "&code=" + authCode;
        }

        GoodsClient.INSTANCE.authTaoBao(path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseDataObject<Object>>() {
                    @Override
                    public void accept(ResponseDataObject<Object> dataObject) throws Exception {
                        loadingDialog.dismiss();
                        if (dataObject.isSuccessful()) {
                            isSelfFinish = true;
                            ToastUtil.show("授权成功");
                            UserEntity user = UserClient.getUser();
                            user.has_bind_tb = 1;
                            UserClient.updateUser(user);
                            EventBus.getDefault().post(new PushEvent(Constant.Event.AUTH_SUCCESS));
                            finish();
                        } else {
                            ToastUtil.show(dataObject.message);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        loadingDialog.dismiss();
                        ToastUtil.show("授权失败");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        isSelfFinish = true;
        super.onBackPressed();
    }

    @Override
    public void finish() {
//        super.finish();
        LogUtil.d("AlibcWebViewActivity", "finish  isSelfFinish=" + isSelfFinish);
        if (isSelfFinish) {
            super.finish();
        } else {
            webView.loadUrl(url);
        }

    }
}