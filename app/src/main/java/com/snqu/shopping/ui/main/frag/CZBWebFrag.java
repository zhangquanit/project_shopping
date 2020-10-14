package com.snqu.shopping.ui.main.frag;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.anroid.base.ui.TitleBarView;
import com.google.gson.Gson;
import com.sndo.czbwebview.WebPageNavigationJsObject;
import com.sndo.czbwebview.X5WebView;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.ui.login.LoginFragment;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;


/**
 * @author 张全
 */
public class CZBWebFrag extends SimpleFrag {
    public static final String PARAM = "PARAM";
    private X5WebView x5webView;
    private ProgressBar progressBar;
    private WebViewFrag.WebViewParam x5webViewParam;
    private TitleBarView titleBarView;
    private static final String TAG = "WebViewFrag";
    private static final String URL_GETTOKEN = "xkd://user/getToken";//获取token
    private static final String URL_USERINFO = "xkd://user/info";//获取用户信息
    private static final String URL_LOGIN = "xkd://user/login"; //登录

    private static SimpleFragAct.SimpleFragParam getStartParam(WebViewFrag.WebViewParam param) {
        return new SimpleFragAct.SimpleFragParam(param.title,
                CZBWebFrag.class, WebViewFrag.getParamBundle(param));
    }

    public static void start(Context ctx, WebViewFrag.WebViewParam param) {
        try {
            String url = param.url;
            String needLogin = Uri.parse(url).getQueryParameter("needLogin");
            if (TextUtils.equals(needLogin, "1") && UserClient.getUser() == null) { //首先登录
                LoginFragment.start(ctx);
            } else {
                SimpleFragAct.SimpleFragParam startParam = getStartParam(param);
                startParam.mutliPage = true;
                SimpleFragAct.start(ctx, startParam);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start(Context ctx, SimpleFragAct.SimpleFragParam param) {
        SimpleFragAct.start(ctx, param);
    }

    @Override
    public int getLayoutId() {
        return R.layout.czb_webview;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // 网络无连接
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            close();
            return;
        }

        if (null != getArguments())
            x5webViewParam = (WebViewFrag.WebViewParam) getArguments().getSerializable(PARAM);

        if (null == x5webViewParam) {
            close();
            return;
        }

        //请求无链接
        if (TextUtils.isEmpty(x5webViewParam.url)) {
            showToastShort("请求无链接");
            close();
            return;
        }
        LogUtil.d(TAG, x5webViewParam.url);

        StatusBar.setStatusBar(mContext, true, getTitleBar());
        addAction(Constant.Event.LOGIN_SUCCESS);


        init();
    }

    protected void init() {
        x5webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.pb);

        titleBarView = getTitleBar();
        if (null != titleBarView) {
            titleBarView.setRightBtnDrawable(R.drawable.close_b);
            titleBarView.setOnRightBtnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    close();
                }
            });
            View.OnClickListener backListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goBack();
                }
            };
            titleBarView.setOnLeftTxtClickListener(backListener);
            titleBarView.setOnLeftBtnClickListener(backListener);
        }


        //加载的url按规定的使用即可
        x5webView.loadUrl(x5webViewParam.url);
        final WebPageNavigationJsObject webPageNavigationJsObject = new WebPageNavigationJsObject(getActivity());
        x5webView.addJavascriptInterface(webPageNavigationJsObject, "czb");//第二个参数czb不可更改，
        x5webView.setWebChromeClient(new WebChromeClient() {
            @Override//进度条
            public void onProgressChanged(WebView webView, int progress) {
                super.onProgressChanged(webView, progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);//加载完网页进度条消失
                } else {
                    progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressBar.setProgress(progress);//设置进度值
                }
            }

            @Override
            public void onReceivedTitle(WebView webView, String title) {
                super.onReceivedTitle(webView, title);
                LogUtil.d(TAG, "onReceivedTitle,title=" + title);
                if (x5webViewParam.shouldResetTitle && !TextUtils.isEmpty(title) && !title.startsWith("http")) {
                    if (null != titleBarView) {
                        titleBarView.setTitleText(title);
                    }
                }
            }
        });

        x5webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                LogUtil.d(TAG, "shouldOverrideUrlLoading url=" + url);
                if (url.startsWith("weixin://") || url.contains("alipays://platformapi")) {//如果微信或者支付宝，跳转到相应的app界面,
                    x5webView.goBack();
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    } catch (Exception e) {
                        ToastUtil.show("未安装相应的客户端");
                    }
                    return true;
                }

                if (url.startsWith(URL_GETTOKEN)) {
                    loginCallback();
                    return true;
                } else if (url.startsWith(URL_USERINFO)) {
                    userInfoCallback();
                    return true;
                } else if (url.startsWith(URL_LOGIN)) {
                    login();
                    return true;
                }

                /**
                 *
                 * 设置 Header 头方法
                 * window.czb.extraHeaders(String key, String value)
                 */
                if (webPageNavigationJsObject != null && webPageNavigationJsObject.getKey() != null) {
                    Map extraHeaders = new HashMap();
                    extraHeaders.put(webPageNavigationJsObject.getKey(), webPageNavigationJsObject.getValue());
                    webView.loadUrl(url, extraHeaders);
                } else {
                    webView.loadUrl(url);
                }
                return true;

            }
        });

    }

    @Override
    public void close() {
        finish();
    }

    @Override
    public boolean onBackPressedSupport() {
        return goBack();
    }

    //返回上一级
    public boolean goBack() {
        if (x5webView.canGoBack()) {
            x5webView.goBack();
        } else {
            finish();
        }
        return true;
    }

    //---------------登录
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        if (containsAction(event.getAction())) {
            switch (event.getAction()) {
                case Constant.Event.LOGIN_SUCCESS:
                    //登录成功
                    loginCallback();
                    break;
            }

        }
    }

    /**
     * 登录
     */
    public void login() {
        LoginFragment.Companion.start(mContext);
    }

    /**
     * 登录回调
     */
    private void loginCallback() {
        String token = UserClient.getToken();
        x5webView.loadUrl("javascript:userLoginStatus('" + token + "')");
    }

    /**
     * 获取用户信息回调
     */
    private void userInfoCallback() {
        UserEntity user = UserClient.getUser();
        String userInfo = "";
        if (null != user) {
            userInfo = new Gson().toJson(user);
        }
        x5webView.loadUrl("javascript:userInfoCallback('" + userInfo + "')");
    }
}
