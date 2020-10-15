package com.snqu.shopping.ui.main.frag;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.android.util.os.DeviceUtil;
import com.android.util.os.NetworkUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.anroid.base.ui.TitleBarView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kepler.jd.Listener.OpenAppAction;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.AlertDialogView;
import com.snqu.shopping.common.ui.BottomInDialog;
import com.snqu.shopping.data.base.RestClient;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.data.home.entity.CommunityEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.ui.goods.AliAuthActivity;
import com.snqu.shopping.ui.goods.util.JumpUtil;
import com.snqu.shopping.ui.login.LoginFragment;
import com.snqu.shopping.ui.main.MainActivity;
import com.snqu.shopping.ui.main.frag.community.CommunityDownloadDialogView;
import com.snqu.shopping.ui.mine.fragment.BindAlipayFragment;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.DispatchUtil;
import com.snqu.shopping.util.FileDownloader;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NotificationPageHelper;
import com.snqu.shopping.util.ShareUtil;
import com.snqu.shopping.util.pay.OrderPay;
import com.snqu.shopping.util.statistics.AnalysisUtil;
import com.snqu.shopping.util.statistics.task.DayTaskReport;
import com.snqu.shopping.util.statistics.task.TaskInfo;
import com.snqu.shopping.util.statistics.ui.TaskProgressView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import common.widget.LoadingBar;
import common.widget.dialog.EffectDialogBuilder;
import common.widget.dialog.loading.LoadingDialog;

/**
 * WebView页面
 *
 * @author zhangquan
 */
public class WebViewFrag extends SimpleFrag {
    public static final String PARAM = "PARAM";
    private WebView webView;
    private ProgressBar progressBar;
    private LoadingBar loadingBar;
    private WebViewParam webViewParam;
    private TitleBarView titleBarView;
    private FrameLayout videoView;
    private RelativeLayout webContainer;
    private View myView;
    private static final String TAG = "WebViewFrag";
    private LoadingDialog loadingDialog;
    private boolean hideTitleBar;
    private boolean isInit = false;
    private TaskProgressView taskProgressView;
    private boolean clearClipBoard; //转链页面--退出后清空剪贴板

    public static class WebViewParam implements Serializable {
        //标题
        public String title;
        //是否需要重置title
        public boolean shouldResetTitle = true;
        //链接URL  url目前支持needLogin(先登录)、hideTitlebar(隐藏标题栏)、lightModel(透明状态栏、黑色文字)
        public String url;
        public String original_Url;
        public String titleBarColor;

        public ShareInfo shareInfo;
        public TaskInfo taskInfo;
        public boolean isDetailJump;
        public boolean isPromotionLinkJump;
        public boolean isShowShare; // 是否显示右上角分享按钮
        public boolean isAuthJump;
        public boolean toMain;
        public boolean checkNetwork = true; //入口检查网络
        public boolean sensorOriention; //自动旋转屏幕方向

        /*
         * 是否支持分享
         */
        public static class ShareInfo implements Serializable {
            public String title;
            public String content;
            public String pic_url;
            public String url;

            public String getShareInfo() {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("title", title);
                    jsonObject.put("content", content);
                    jsonObject.put("url", url);
                    jsonObject.put("pic_url", pic_url);
                    return jsonObject.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }

    }

    public static Bundle getParamBundle(WebViewParam param) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM, param);
        return bundle;
    }

    public static SimpleFragAct.SimpleFragParam getStartParam(WebViewParam param) {
        return new SimpleFragAct.SimpleFragParam(param.title,
                WebViewFrag.class, WebViewFrag.getParamBundle(param));
    }

    public static void start(Context ctx, WebViewParam param) {
        try {
            String url = param.url;
            if (url.contains("ac202003jiayou")) {
                param.shouldResetTitle = false;
            }
            String needLogin = Uri.parse(url).getQueryParameter("needLogin");
            if (TextUtils.equals(needLogin, "1") && UserClient.getUser() == null) { //首先登录
                LoginFragment.start(ctx);
            } else {
                SimpleFragAct.SimpleFragParam startParam = getStartParam(param);
                startParam.mutliPage = true;
                SimpleFragAct.start(ctx, startParam, param.sensorOriention);
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
        return R.layout.common_webview;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        if (null != getArguments()) {
            webViewParam = (WebViewParam) getArguments().getSerializable(PARAM);
        }

        if (null == webViewParam) {
            close();
            return;
        }

        // 网络无连接
        if (webViewParam.checkNetwork && !NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            close();
            return;
        }

        //请求无链接
        if (TextUtils.isEmpty(webViewParam.url)) {
            showToastShort("请求无链接");
            close();
            return;
        }

        LogUtil.d(TAG, webViewParam.url);

        addAction(Constant.Event.LOGIN_SUCCESS);
        addAction(Constant.Event.AUTH_SUCCESS);
        addAction(Constant.Event.ORDER_BUY_SUCCESS);
        addAction(Constant.Event.ORDER_BUY_FAIL);
        addAction(Constant.Event.ORDER_BUY_CANCEL);
//        addAction(Constant.Event.TASK_REPORT);
        init();
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            Method onResume = webView.getClass().getMethod("onResume");
            if (null != onResume) onResume.invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isInit) {
            viewWillAppear();
        }
        isInit = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            Method onPause = webView.getClass().getMethod("onPause");
            if (null != onPause) onPause.invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        constructUrl();
        webView.loadUrl(webViewParam.url);
    }

    private boolean handleBackEvent() {
        if (null != webView && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }

    protected void init() {
        webView = findViewById(R.id.webview);
        handleLongClick(); //长按保存图片
        videoView = findViewById(R.id.video_fullscreen); //webview视频全屏播放
        webContainer = findViewById(R.id.web_parent);
        progressBar = findViewById(R.id.pb);
        loadingBar = findViewById(R.id.loadingBar);
        loadingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loadingBar.canLoading()) {
                    return;
                }
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    ToastUtil.show(R.string.net_noconnection);
                    return;
                }
                webView.reload();
            }
        });

        titleBarView = getTitleBar();

        if (null != titleBarView) {
            View.OnClickListener backListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (handleBackEvent()) {
                        return;
                    }
                    close();
                }
            };
            titleBarView.setOnLeftTxtClickListener(backListener);
            titleBarView.setOnLeftBtnClickListener(backListener);


            //刷新按钮
            titleBarView.setRightBtnDrawable(R.drawable.btn_refresh);
            titleBarView.mRightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetworkUtil.isNetworkAvailable(mContext)) {
                        ToastUtil.show(R.string.net_noconnection);
                        return;
                    }
                    webView.reload();
                }
            });
            //分享
            if (null != webViewParam.shareInfo) {
                titleBarView.setRightBtnDrawable(R.drawable.webview_share);
                titleBarView.mRightButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            share(webViewParam.shareInfo.getShareInfo());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            if (webViewParam.isShowShare && webViewParam.isPromotionLinkJump) {
                titleBarView.findViewById(R.id.title_icon_share).setVisibility(View.VISIBLE);
                titleBarView.findViewById(R.id.title_icon_share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            WebViewFrag.WebViewParam.ShareInfo shareInfo = new WebViewFrag.WebViewParam.ShareInfo();
                            shareInfo.title = webViewParam.title == null ? webView.getTitle() : webViewParam.title;
                            shareInfo.url = webViewParam.original_Url;
                            shareInfo.content = "点击活动链接,赶快来参与活动吧~ \n" + webViewParam.original_Url;
                            share(shareInfo.getShareInfo());
//                            shareInfo.pic_url = articalEntity.cover_image;
//                            WebViewParam.ShareInfo shareInfo = WebViewParam.ShareInfo();
//                            ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
//                            CharSequence text = "点击活动链接,赶快来参与活动吧~ \n"+webView.getUrl();
//                            cm.setPrimaryClip(ClipData.newPlainText(null, text));
//                            ToastUtils.showShort("活动链接复制成功");
                        } catch (Exception e) {
                            e.printStackTrace();
                            ToastUtil.show("分享失败");
                        }
                    }
                });
            } else {
                titleBarView.findViewById(R.id.title_icon_share).setVisibility(View.GONE);
            }

        }


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setGeolocationEnabled(true); //获取位置信息
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);//自动播放视频
        webSettings.setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        webSettings.setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        webSettings.setAllowFileAccessFromFileURLs(false);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        //开启JavaScript支持

        //设置缓存
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webView.clearCache(true);

//        webSettings.setAllowFileAccessFromFileURLs(true);
//        webSettings.setAllowUniversalAccessFromFileURLs(true);


        webViewParam.original_Url = webViewParam.url;
        //拼接参数
        constructUrl();

        //标题栏背景
        if (null != getTitleBar()) {
            if (webViewParam.titleBarColor != null) {
                getTitleBar().setBackgroundColor(Color.parseColor(webViewParam.titleBarColor));
                getTitleBar().setTitleTextColor(R.color.white);
                getTitleBar().setLeftBtnWhiteColor();
            } else {
                getTitleBar().setBackgroundColor(Color.WHITE);
            }
        }
        //隐藏标题栏
        setStatusBar();

        Uri uri = Uri.parse(webViewParam.url);
        try {
            String parseClipboardEnable = uri.getQueryParameter("parseClipboard");
            if (TextUtils.equals(parseClipboardEnable, "0")) {
                SimpleFragAct fragAct = (SimpleFragAct) getActivity();
                fragAct.parseClipboard = false;
                clearClipBoard = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        //状态栏文字颜色
//        String lightModel = uri.getQueryParameter("lightModel");
//        if (TextUtils.equals(lightModel, "0")) {
//            StatusBar.setStatusBar(mContext, false, titleBarView);
//        } else {
//            StatusBar.setStatusBar(mContext, true, titleBarView);
//        }


        //浏览商品
        if (null != webViewParam.taskInfo) {
            taskProgressView = CommonUtil.getTaskProgressView(mContext);
            TaskInfo taskInfo = webViewParam.taskInfo;
            webViewParam.taskInfo = null;
            taskProgressView.setTaskInfo(taskInfo);
        }

        LogUtil.d(TAG, "加载 url=" + webViewParam.url);

        webView.loadUrl(webViewParam.url);
        webView.setWebChromeClient(new DWebChromeClient());
        webView.setWebViewClient(new DWebViewClient());
        webView.setDownloadListener(new DWebViewDownLoadListener());

    }

    private void constructUrl() {
        int statusBarHeight = 0;
        try {
            statusBarHeight = DeviceUtil.getStatusBarHeight(getActivity());
            statusBarHeight = DeviceUtil.px2dip(getContext(), statusBarHeight);
        } catch (Exception e) {
            statusBarHeight = 25;
        }
        LogUtil.d(TAG, "statusBarHeight=" + statusBarHeight);
        //拼接参数
        if (CommonUtil.isInnerUrl(webViewParam.original_Url)) {
            Uri uri = Uri.parse(webViewParam.original_Url);
            Uri.Builder builder = uri.buildUpon();
            Set<String> parameterNames = uri.getQueryParameterNames();
            if (!parameterNames.contains("appVersion")) {
                builder.appendQueryParameter("appVersion", LContext.versionName);
            }
            if (!parameterNames.contains("isApp")) {
                builder.appendQueryParameter("isApp", "1");
            }
            if (!parameterNames.contains("appId")) {
                builder.appendQueryParameter("appId", RestClient.APPID);
            }
            if (!parameterNames.contains("x-app-source")) {
                builder.appendQueryParameter("x-app-source", RestClient.APP_SOURCE);
            }
            if (!parameterNames.contains("statusBarH")) {
                builder.appendQueryParameter("statusBarH", statusBarHeight + "");
            }
            if (!parameterNames.contains("x-m")) {
                builder.appendQueryParameter("x-m", AnalysisUtil.getUniqueId());
            }
            if (!parameterNames.contains("x-sid")) {
                builder.appendQueryParameter("x-sid", UserClient.getToken());
            }
            webViewParam.url = builder.build().toString();
        }
    }

    private void setStatusBar() {
        Uri uri = Uri.parse(webViewParam.original_Url);
        String hideTitlebar = uri.getQueryParameter("hideTitlebar");
        if (TextUtils.equals(hideTitlebar, "1")) {
            hideTitleBar = true;
            titleBarView.setVisibility(View.GONE);
            String lightModel = uri.getQueryParameter("lightModel");
            if (TextUtils.equals(lightModel, "1")) {
                StatusBar.setStatusBar(mContext, true, titleBarView);
            } else {
                StatusBar.setStatusBar(mContext, false, titleBarView);
            }
        } else {
            StatusBar.setStatusBar(mContext, true, titleBarView);
        }
    }

    /**
     * 辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
     */
    private final class DWebChromeClient extends WebChromeClient {
        private FileChooserParams fileChooserParams;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            LogUtil.d(TAG, "onShowCustomView  view=" + view);
            try {
                ViewGroup parent = (ViewGroup) webView.getParent();
                parent.removeView(webView);

                videoView.addView(view);
                videoView.setVisibility(View.VISIBLE);
                myView = view;

                getTitleBar().setVisibility(View.GONE);

                setFullScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            LogUtil.d(TAG, "onHideCustomView  ");
            try {
                if (myView != null) {
                    videoView.removeAllViews();
                    webContainer.addView(webView);
                    videoView.setVisibility(View.GONE);

                    myView = null;
                    getTitleBar().setVisibility(View.VISIBLE);
                    quitFullScreen();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            LogUtil.d(TAG, "onJsAlert,url=" + url + ",message=" + message);
            AlertDialogView dialogView = new AlertDialogView(webView.getContext())
                    .setTitle("提示")
                    .setContent(message)//
                    .setSingleBtn("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            result.confirm();
                        }
                    });

            new EffectDialogBuilder(webView.getContext())
                    .setCancelable(false)
                    .setCancelableOnTouchOutside(false)
                    .setContentView(dialogView).show();

//            return super.onJsAlert(view, url, message, result);
            return true;
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            LogUtil.d(TAG, "onJsConfirm,url=" + url + ",message=" + message);
            AlertDialogView dialogView = new AlertDialogView(webView.getContext())
                    .setContent(message)//
                    .setRightBtn("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            result.confirm();
                        }
                    })
                    .setLeftBtn("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            result.cancel();
                        }
                    });
            new EffectDialogBuilder(webView.getContext())
                    .setCancelable(false)
                    .setCancelableOnTouchOutside(false)
                    .setContentView(dialogView).show();
//            return super.onJsConfirm(view, url, message, result);
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            LogUtil.d(TAG, "onJsPrompt,url=" + url + ",message=" + message);
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }


        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, true);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            LogUtil.d(TAG, "onReceivedTitle,url=" + view.getUrl());
            LogUtil.d(TAG, "onReceivedTitle,title=" + title);
            if (webViewParam.shouldResetTitle && !TextUtils.isEmpty(title) && !title.startsWith("http")) {
                if (null != titleBarView) {
                    titleBarView.setTitleText(title);
                }
                if (!"登录".equals(title)) {
                    webViewParam.title = title;
                }
            }


        }

        /**
         * 上传文件
         *
         * @param webView
         * @param filePathCallback
         * @param fileChooserParams
         * @return
         */
        @Override
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {


            this.fileChooserParams = fileChooserParams;
            fileCallback = filePathCallback;
            if (TextUtils.equals(webView.getTitle(), "人脸验证")) {
                int mCameraPermission = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.CAMERA);
                if (mCameraPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);  // 表示跳转至相机的录视频界面
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);    // MediaStore.EXTRA_VIDEO_QUALITY 表示录制视频的质量，从 0-1，越大表示质量越好，同时视频也越大
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);   // 设置视频录制的最长时间
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, FILE_CHOOSER_RESULT_CODE);  // 跳转
                    } else {
                        ToastUtil.show("打开录屏失败，请联系客服手动解决签署协议。");
                    }
                }
            } else {
                openFileChooser(null != fileChooserParams ? fileChooserParams.getAcceptTypes() : null);
            }
            return true;
        }
    }

    /**
     * 设置全屏
     */
    private void setFullScreen() {
        // 设置全屏的相关属性，获取当前的屏幕状态，然后设置全屏
        try {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出全屏
     */
    private void quitFullScreen() {
        // 声明当前屏幕状态的参数并获取
        try {
            final WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().getWindow().setAttributes(attrs);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ValueCallback<Uri[]> fileCallback;
    private static final int FILE_CHOOSER_RESULT_CODE = 11;


    /**
     * webview选择上传图片或视频
     *
     * @param types
     */
    private void openFileChooser(String[] types) {
        //https://ida.webank.com/s/web/h5/#/h5pre
        LogUtil.d(TAG, "onShowFileChooser 请求type=" + Arrays.toString(types));
        String type = "";
        if (null != types && types.length > 0) {
            type = types[0];
        }
        if (TextUtils.isEmpty(type)) {
            type = "image/*;video/*";
        }

        if (type.contains("jpg") || type.contains("png")) {
            type = "image/*";
        } else if (type.contains("mp4")) {
            type = "video/*";
        }
        LogUtil.d(TAG, "onShowFileChooser 执行type=" + type);

        try {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            i.setType(type);
            startActivityForResult(Intent.createChooser(i, "选择图片/视频"), FILE_CHOOSER_RESULT_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("无法选择文件");
            fileCallback.onReceiveValue(null);
            fileCallback = null;
        }
    }

    /**
     * 长按保存图片
     */
    private void handleLongClick() {
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final WebView.HitTestResult hitTestResult = webView.getHitTestResult();
                if (null == hitTestResult) return false;
                int type = hitTestResult.getType();
                LogUtil.d(TAG, "onLongClick type=" + type);
                // 如果是图片类型或者是带有图片链接的类型
                if (type == WebView.HitTestResult.IMAGE_TYPE ||
                        type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    AlertDialogView dialogView = new AlertDialogView(webView.getContext())
                            .setContent("保存到相册")//
                            .setRightBtn("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String pic = hitTestResult.getExtra();//获取图片
                                    LogUtil.d(TAG, "onLongClick  图片url=" + pic);
                                    if (TextUtils.isEmpty(pic)) {
                                        return;
                                    }
                                    new FileDownloader(mContext)
                                            .downloadFile(pic, new FileDownloader.DownloadCallback() {
                                                @Override
                                                public void success(File file, String url) {
                                                    ToastUtil.show("保存成功");
                                                }

                                                @Override
                                                public void fail(String url) {
                                                    ToastUtil.show("保存失败");
                                                }
                                            });
                                }
                            })
                            .setLeftBtn("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                    new EffectDialogBuilder(webView.getContext())
                            .setCancelable(false)
                            .setCancelableOnTouchOutside(false)
                            .setContentView(dialogView).show();

                    return true;
                }
                return false; //保持长按复制文字
            }
        };
        webView.setOnLongClickListener(onLongClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == fileCallback) return;
            try {
                Uri[] results = null;
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String dataString = data.getDataString();
                    ClipData clipData = data.getClipData();
                    LogUtil.d(TAG, "onShowFileChooser,dataString=" + dataString + ",clipData=" + clipData);
                    if (!TextUtils.isEmpty(dataString)) {
                        results = new Uri[]{Uri.parse(dataString)};
                    } else if (clipData != null && clipData.getItemCount() > 0) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                }


                LogUtil.d(TAG, "onShowFileChooser,results=" + results);
                fileCallback.onReceiveValue(results);
                fileCallback = null;
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.show("选择文件失败");
                fileCallback.onReceiveValue(null);
                fileCallback = null;
            }
        }
    }


    /**
     * 主要帮助WebView处理各种通知、请求事件的
     */
    private final int LOAD_START = 1;
    private final int LOAD_ERROR = 2;
    private final int LOAD_FINISHED = 3;
    private int loadStatus = LOAD_FINISHED;

    private class DWebViewClient extends WebViewClient {

        @Override
        public void onLoadResource(WebView view, String url) {
            progressBar.setProgress(view.getProgress());
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            loadStatus = LOAD_START;
            progressBar.setProgress(1);
            progressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setProgress(100);
            progressBar.setVisibility(View.GONE);
            if (loadStatus != LOAD_ERROR) {
                loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.SUCCESS);
            }
            loadStatus = LOAD_FINISHED;
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            loadStatus = LOAD_ERROR;
            //显示上层的错误页面
            if (!NetworkUtil.isNetworkAvailable(mContext)) {
                loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.NOCONNECTION, R.drawable.icon_fail);
            } else {
                loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.RELOAD, R.drawable.icon_fail);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            // super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            LogUtil.e("web="+webViewParam.isPromotionLinkJump+","+webViewParam.isDetailJump);
//            url = "xkd://app/share?param=%7b%22title%22%3a%22%e5%88%86%e4%ba%ab%e6%a0%87%e9%a2%98%22%2c%22content%22%3a%22%e5%88%86%e4%ba%ab%e5%86%85%e5%ae%b9%22%2c%22url%22%3a%22https%3a%2f%2fwww.baidu.com%22%2c%22pic_url%22%3a%22%e5%88%86%e4%ba%ab%e7%9a%84%e5%9b%be%e7%89%87%e9%93%be%e6%8e%a5%22%7d";
            return handleUrl(url);
        }
    }

    /**
     * 下载
     *
     * @author zhangquan
     */
    private class DWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent,
                                    String contentDisposition, String mimetype, long contentLength) {
            try {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkStartMainAct() {
        if (null != webViewParam && webViewParam.toMain) {
            MainActivity.start(mContext);
        }
    }

    @Override
    public void close() {
        checkStartMainAct();
        finish();
    }

    @Override
    public boolean onBackPressedSupport() {
        boolean goBack = handleBackEvent();
        if (!goBack) {
            checkStartMainAct();
        }
        return goBack;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseWebView();
        DispatchUtil.taskInfo = null;
        if (null != taskProgressView) {
            taskProgressView.stop();
        }

        if (clearClipBoard) {
            CommonUtil.addToClipboard(null);
        }
    }

    private void releaseWebView() {
        try {
            if (null != webView) {
                webView.setVisibility(View.GONE);
                webView.removeAllViews();
                webView.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //------------------------------url handle
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent pushEvent) {
        if (TextUtils.equals(pushEvent.getAction(), Constant.Event.LOGIN_SUCCESS)) {
            loginCallback();
        } else if (TextUtils.equals(pushEvent.getAction(), Constant.Event.AUTH_SUCCESS)) {
            authCallback();
        } else if (TextUtils.equals(pushEvent.getAction(), Constant.Event.TASK_REPORT)) {
            webView.reload();
        } else if (TextUtils.equals(pushEvent.getAction(), Constant.Event.ORDER_BUY_SUCCESS)) {
            alipayResult(1);
        } else if (TextUtils.equals(pushEvent.getAction(), Constant.Event.ORDER_BUY_FAIL)) {
            alipayResult(2);
        } else if (TextUtils.equals(pushEvent.getAction(), Constant.Event.ORDER_BUY_CANCEL)) {
            alipayResult(3);
        }
    }

    private static final String URL_SHARE = "xkd://app/share?param="; //分享
    private static final String URL_GETTOKEN = "xkd://user/getToken";//获取token
    private static final String URL_USERINFO = "xkd://user/info";//获取用户信息
    private static final String URL_LOGIN = "xkd://user/login"; //登录
    private static final String URL_OPENPAGE = "xkd://app/appPage?param=";//打开原生页面
    private static final String URL_CLOSEPAGE = "xkd://app/closepage";//关闭页面
    private static final String URL_SIGN = "xkd://app/sign?param=";//app签名
    private static final String URL_REQUES = "xkd://app/request?param=";//h5调用app网络接口
    private static final String URL_BIND_ALIPAY = "xkd://app/bind/alipay";//绑定支付宝
    private static final String URL_OPENURL = "xkd://app/openURL";//通过浏览器打开
    public static final String URL_OPENTAOBAO = "xkd://app/openTaobao";//通过协议打开淘宝
    private static final String URL_AUTH_TB = "xkd://app/bind/taobaoAuth";//淘宝授权
    public static final String URL_OPENJD = "xkd://app/openJd"; // 通过协议打开京东
    public static final String URL_OPENPDD = "xkd://app/openPdd"; //通过协议打开拼多多
    private static final String URL_AUTH_PDD = "xkd://app/bind/pddAuth";//拼多多授权
    public static final String URL_OPENVIP = "xkd://app/openVip"; //打开唯品会
    private static final String URL_OPEN_JIAYOU = "czb365.com"; //加油
    private static final String URL_CLIPBOARD = "xkd://app/clipboard"; //复制到剪贴板
    private static final String URL_CLIPBOARD_READ = "xkd://app/readclipboard"; //读取剪贴板
    private static final String URL_NOTIFICATION_ENABLED = "xkd://app/notification/status";//获取通知状态  1开启 0关闭
    private static final String URL_NOTIFICATION_OPEN = "xkd://app/notification/setting";//打开通知设置界面
    private static final String URL_SAVE_PIC = "xkd://app/storage/imgs"; //保存图片
    private static final String URL_STATUSBAR = "xkd://app/statusbar"; //修改状态栏
    private static final String URL_H5_ADJUMP = "xkd://app/nativeAdJump";//h5广告跳转
    private static final String URL_SAVE_PHOTOS = "xkd://savedPhotosAlbum";//保存到相册
    private static final String URL_ALIPAY = "xkd://app/alipay";//支付宝支付


    private static final String URL_WEIXIN = "weixin://"; //打开微信
    private static final String URL_OPEN_ALIPAY = "alipays://"; //打开支付宝
    private static final String URL_VPAGE = "vipshop://";//唯品会
    private static final String URL_TPOPEN = "tbopen://";//淘宝t
    private static final String URL_TAOBAO = "taobao://";//淘宝
    private static final String URL_PDD = "pinduoduo://";//拼多多
    private static final String URL_BDISK = "bdnetdisk://";//百度网盘
    private static final String URL_TIANMAO = "tmall://";//天猫
    private static final String URL_MEITUAN = "imeituan://";//美团
    private static final String URL_QQ = "wtloginmqq://";//QQ
    private static final String URL_SUNING = "suning://";//苏宁
    private static final String URL_DIDA = "didapinche://";//嘀嗒出行


    private boolean handleUrl(String path) {

//        path = "https://sumfs.suning.com/sumis-web/staticRes/web/pgWelfare/index.html?commodityCode=10288345525&supplierCode=010activityType";
//        webViewParam.isDetailJump = false;
        LogUtil.d(TAG, "shouldOverrideUrlLoading ,url=" + path);
        LogUtil.d(TAG, "shouldOverrideUrlLoading ,detail=" + webViewParam.isDetailJump + "," + webViewParam.isPromotionLinkJump);

        if (!webViewParam.isDetailJump) {
            //第三方活动商品
            if (DispatchUtil.parseGoodUrl(mContext, path)) {
                return true;
            }
        }


        if (webViewParam.isPromotionLinkJump) { //商品详情下单领取优惠券
            //第三方活动商品
            //拼多多
            if (path.startsWith(URL_PDD)) {
                return true;
            }

            //唯品会
            if (path.startsWith(URL_VPAGE)) {
                return true;
            }

            //打开淘宝
            if (path.startsWith(URL_TPOPEN)) {
                return true;
            }

            if (path.startsWith(URL_TAOBAO)) {
                return true;
            }
        } else {
            //拼多多
            if (path.startsWith(URL_PDD)) {
                openUrl(path, "打开失败，检查是否安装拼多多APP ");
                if (webViewParam.isAuthJump) {
                    close();
                }
                return true;
            }
            //唯品会
            if (path.startsWith(URL_VPAGE)) {
                openUrl(path, "打开失败，检查是否安装唯品会APP ");
                return true;
            }

            //打开淘宝
            if (path.startsWith(URL_TPOPEN)) {
                openUrl(path, getString(R.string.tb_install_alert));
                return true;
            }

            if (path.startsWith(URL_TAOBAO)) {
                openUrl(path, getString(R.string.tb_install_alert));
                return true;
            }


            if (path.startsWith(URL_SUNING)) {
                openUrl(path, "");
                return true;
            }
        }

        if (path.startsWith(URL_DIDA)) {
            openUrl(path, "请下载嘀嗒出行app");
            return true;
        }


        //拨打电话
        if (path.startsWith("tel")) {
            try {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(path)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        //加油
        if (path.contains(URL_OPEN_JIAYOU)) {
            //"https://test-open.czb365.com/redirection/todo/?platformType=92658769&platformCode=13568959655"
            WebViewParam webViewParam = new WebViewParam();
            webViewParam.url = path;
            CZBWebFrag.start(mContext, webViewParam);
            close();
            return true;
        }


        //打开支付宝
        if (path.startsWith(URL_OPEN_ALIPAY)) {
            openUrl(path, "打开失败，检查是否安装支付宝App");
            return true;
        }


        //跳转到微信
        if (path.startsWith(URL_WEIXIN)) {
            openUrl(path, "打开失败，检查是否安装微信App");
            return true;
        }

        //百度网盘
        if (path.startsWith(URL_BDISK)) {
            openUrl(path, "打开失败，检查是否安装百度网盘APP");
            return true;
        }

        if (path.startsWith(URL_MEITUAN)) {
            openUrl(path, "打开失败，检查是否安装美团APP (非美团外卖APP) ");
            return true;
        }
        if (path.startsWith(URL_QQ)) {
            openUrl(path, "打开失败");
            return true;
        }
        if (path.startsWith(URL_SUNING)) {
            openUrl(path, "");
            return true;
        }

        //星乐桃自定义协议
        if (!path.startsWith("xkd://")) {
            return false;
        }
        try {
            String param = Uri.parse(path).getQueryParameter("param");
            if (path.startsWith(URL_SHARE)) {
                share(param);
            } else if (path.startsWith(URL_GETTOKEN)) {
                loginCallback();
            } else if (path.startsWith(URL_USERINFO)) {
                userInfoCallback();
            } else if (path.startsWith(URL_LOGIN)) {
                login();
            } else if (path.startsWith(URL_OPENTAOBAO)) {
                JumpUtil.jumpToToAli(mContext, param, false);
            } else if (path.startsWith(URL_AUTH_TB)) {
                AliAuthActivity.start(mContext, param);
            } else if (path.startsWith(URL_OPENJD)) {
                JumpUtil.jumpToJdCouponsPage(mContext, param, new OpenAppAction() {
                    @Override
                    public void onStatus(int i) {
                        mContext.runOnUiThread(() -> {
                            if (i == OpenAppAction.OpenAppAction_start) {
                                showLoadingDialog("页面跳转中...");
                            } else {
                                closeLoadDialog();
                            }
                        });
                    }
                });
            } else if (path.startsWith(URL_OPENPDD)) { //打开拼多多
                JumpUtil.jumpToPdd(mContext, param, false);
            } else if (path.startsWith(URL_AUTH_PDD)) { //拼多多授权
                JumpUtil.authPdd(mContext, param);
            } else if (path.startsWith(URL_OPENURL)) { //打开浏览器
                openUrl(param, "打开失败");
            } else if (path.startsWith(URL_OPENPAGE)) { //打开APP页面
                DispatchUtil.goToPage(mContext, param);
                if (!TextUtils.isEmpty(param)) {
                    JSONObject jsonObject = new JSONObject(param);
                    int closepage = jsonObject.optInt("closepage");
                    if (closepage == 1) {
                        close();
                    }
                }
            } else if (path.startsWith(URL_SIGN)) { //签名
                long timestamp = System.currentTimeMillis() / 1000;
                JSONObject jsonObject = new JSONObject(param);
                String url = jsonObject.optString("url");
                JSONObject paramsJson = jsonObject.optJSONObject("params");
                Map<String, String> map = new HashMap<>();
                if (null != paramsJson && paramsJson.length() > 0) {
                    String value = paramsJson.toString();
                    map = new Gson().fromJson(value, new TypeToken<Map<String, String>>() {
                    }.getType());
                }
                String sign = RestClient.signParam(url, map, timestamp);
                JSONObject dataJson = new JSONObject();
                dataJson.put("x-app-source", RestClient.APP_SOURCE);
                dataJson.put("x-sign", sign);
                dataJson.put("x-timestamp", timestamp);
                dataJson.put("x-appid", RestClient.APPID);
                dataJson.put("x-m", AnalysisUtil.getUniqueId());
                dataJson.put("Authorization", UserClient.getToken());

                dataJson.put("client-v", LContext.versionName);
                dataJson.put("check-enable", "1");
                dataJson.put("dev-type", "1");
                dataJson.put("client-type", "2");
//               dataJson.put("Content-Security-Policy","upgrade-insecure-requests");

                String headers = dataJson.toString();
                LogUtil.d("webview", "签名headers=" + headers);
//                 headers = URLEncoder.encode(headers);
                String value = String.format("'%s','%s','%s'", url, null != paramsJson ? paramsJson.toString() : "'{}'", headers);
                LogUtil.d("webview", "URL_SIGN 签名回调=" + value);
                webView.loadUrl("javascript:signResult(" + value + ")");

            } else if (path.startsWith(URL_BIND_ALIPAY)) { //绑定支付宝
                UserEntity user = UserClient.getUser();
                if (null == user) {
                    LoginFragment.Companion.start(mContext);
                } else if (TextUtils.equals(user.bind_alipay, "1")) {  //已绑定支付宝
                    bindedAlipay();
                } else { //支付宝绑定页面
                    BindAlipayFragment.Companion.start(mContext);
                }
            } else if (path.startsWith(URL_CLOSEPAGE)) { //关闭页面
                close();
            } else if (path.startsWith(URL_CLIPBOARD)) { //复制到剪贴板
                try {
                    ClipboardManager cm = (ClipboardManager) LContext.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(param);
                    ToastUtil.show("复制成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.show("复制失败，请长按文字复制");
                }
            } else if (path.startsWith(URL_CLIPBOARD_READ)) { //读取剪贴板
                readClipboard();
            } else if (path.startsWith(URL_NOTIFICATION_ENABLED)) { //app是否开启通知权限
                notificationCallback();
            } else if (path.startsWith(URL_NOTIFICATION_OPEN)) { //跳转到app通知权限页面
                NotificationPageHelper.openNotificationSetting(mContext);
            } else if (path.startsWith(URL_SAVE_PIC)) { //保存图片到相册
                if (!TextUtils.isEmpty(param)) {
                    LoadingDialog loadingDialog = LoadingDialog.showCancelableDialog(mContext, "图片保存中");
                    new FileDownloader(mContext).downloadFile(param, new FileDownloader.DownloadCallback() {
                        @Override
                        public void success(File file, String url) {
                            loadingDialog.dismiss();
                            ToastUtil.show("保存成功");
                        }

                        @Override
                        public void fail(String url) {
                            loadingDialog.dismiss();
                            ToastUtil.show("保存失败");
                        }
                    });
                } else {
                    ToastUtil.show("图片地址为空");
                }
            } else if (path.startsWith(URL_STATUSBAR)) { //修改状态栏
                boolean lightModel = TextUtils.equals(param, "1");
                StatusBar.setStatusBar(mContext, lightModel, titleBarView);
//                titleBarView.setBackgroundColor(false ? Color.BLACK : Color.WHITE);
            } else if (path.startsWith(URL_H5_ADJUMP)) { //H5广告跳转
                AdvertistEntity advertistEntity = new Gson().fromJson(param, AdvertistEntity.class);
                CommonUtil.startWebFrag(getActivity(), advertistEntity);
            } else if (path.startsWith(URL_SAVE_PHOTOS)) {
                savePics(param);
            } else if (path.startsWith(URL_ALIPAY)) {
                new OrderPay().alipay(mContext, param);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void savePics(String param) {
        JSONObject jsonObject = null;
        try {
            CommunityEntity communityEntity = new CommunityEntity();
            communityEntity.videos = new ArrayList<>();
            communityEntity.images = new ArrayList<>();

            jsonObject = new JSONObject(param);

            JSONArray jsonElements = jsonObject.optJSONArray("videos");
            if (jsonElements != null && jsonElements.length() > 0) {
                for (int i = 0; i < jsonElements.length(); i++) {
                    String path = jsonElements.get(i).toString();
                    communityEntity.videos.add(GlideUtil.checkUrl(path));
                }
            }
            jsonElements = jsonObject.optJSONArray("images");
            if (jsonElements != null && jsonElements.length() > 0) {
                for (int i = 0; i < jsonElements.length(); i++) {
                    String path = jsonElements.get(i).toString();
                    communityEntity.images.add(GlideUtil.checkUrl(path));
                }
            }

            if (communityEntity.videos.size() > 0 || communityEntity.images.size() > 0) {
                CommunityDownloadDialogView dialogView = new CommunityDownloadDialogView(mContext, communityEntity, "素材下载");
                new EffectDialogBuilder(mContext)
                        .setContentView(dialogView)
                        .setCancelable(true)
                        .show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openUrl(String param, String msg) {

        if (param.startsWith(URL_MEITUAN)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(param));
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                if (!TextUtils.isEmpty(msg)) {
                    ToastUtil.show(msg);
                }
            }
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(param));
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                if (!TextUtils.isEmpty(msg)) {
                    ToastUtil.show(msg);
                }
            }
        }
    }

    /**
     * 登录
     */
    public void login() {
//        String token = UserClient.getToken();
//        if (!TextUtils.isEmpty(token)) {
//            loginCallback();
//        } else {
        LoginFragment.Companion.start(mContext);
//        }
    }

    /**
     * 登录回调
     */
    private void loginCallback() {
        String token = UserClient.getToken();
        webView.loadUrl("javascript:userLoginStatus('" + token + "')");
    }

    /**
     * 支付宝绑定回调
     */
    private void bindedAlipay() {
//        ToastUtil.show("绑定支付宝回调");
        webView.loadUrl("javascript:bindedAlipay()");
    }

    /**
     * 支付宝 支付回调
     *
     * @param result
     */
    private void alipayResult(int result) {
        webView.loadUrl("javascript:alipayResult('" + result + "')");
    }

    /**
     * 读取剪贴板
     */
    private void readClipboard() {
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        CharSequence text = "";
        ClipData clipData = cm.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            text = clipData.getItemAt(0).getText();
        }
        if (!TextUtils.isEmpty(text)) {
            String content = text.toString();
            String[] test = content.split("\n");
            StringBuffer sb = new StringBuffer();
            for (String line : test) {
                sb.append(line).append("\\n");
            }
            webView.loadUrl("javascript:readClipboard('" + sb.toString() + "')");
            CommonUtil.addToClipboard(null);
        }
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
        webView.loadUrl("javascript:userInfoCallback('" + userInfo + "')");
    }

    private void authCallback() {
        webView.loadUrl("javascript:taoBaoAuthComplete()");
    }

    private void viewWillAppear() {
        webView.loadUrl("javascript:viewWillAppearEvent()");
    }

    private void notificationCallback() {
        int status = NotificationPageHelper.areNotificationsEnabled(mContext) ? 1 : 0;
        webView.loadUrl("javascript:notificationEnabled('" + status + "')");
    }

    public void showLoadingDialog(String content) {
        loadingDialog = LoadingDialog.showBackCancelableDialog(mContext, content);
    }


    public void closeLoadDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 分享
     */
    public void share(String json) throws Exception {
        if (TextUtils.isEmpty(json)) return;
        //{"title":"分享标题","content":"分享内容","url":"分享链接","pic_url":"分享图片链接"}

        JSONObject jsonObject = new JSONObject(json);
        String title = jsonObject.optString("title");
        String content = jsonObject.optString("content");
        String url = jsonObject.optString("url");
        String pic_url = jsonObject.optString("pic_url");
//        String mini_progm_path = jsonObject.optString("mini_progm_path");
//        String mini_progm_id = jsonObject.optString("mini_progm_id");
        JSONObject taskInfoObj = jsonObject.optJSONObject("taskInfo");
        TaskInfo taskInfo = null;
        if (null != taskInfoObj) {
            taskInfo = new TaskInfo();
            taskInfo.id = taskInfoObj.optString("id");
            taskInfo.countDown = taskInfoObj.optLong("countDown");
            taskInfo.reward = taskInfoObj.optString("reward");
            taskInfo.type = taskInfoObj.optString("type");
        }

        BottomInDialog bottomInDialog = new BottomInDialog(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.webview_share_dialog, null);
        TaskInfo finalTaskInfo = taskInfo;

        View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SHARE_MEDIA media = SHARE_MEDIA.WEIXIN;
                switch (v.getId()) {
                    case R.id.ll_wx:
                        media = SHARE_MEDIA.WEIXIN;
                        break;
                    case R.id.ll_wx_circle:
                        media = SHARE_MEDIA.WEIXIN_CIRCLE;
                        break;
                    case R.id.ll_qq:
                        media = SHARE_MEDIA.QQ;
                        break;
                }
                ShareUtil.share(getActivity(), url, title, content, pic_url, media, new UMShareListener() {

                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        if (null != finalTaskInfo) {
                            DayTaskReport.shareReport(mContext, finalTaskInfo);
                        }
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {

                    }
                });
                bottomInDialog.dismiss();
            }
        };
        view.findViewById(R.id.ll_wx).setOnClickListener(clickListener);
        view.findViewById(R.id.ll_wx_circle).setOnClickListener(clickListener);
        view.findViewById(R.id.ll_qq).setOnClickListener(clickListener);
        view.findViewById(R.id.tv_close).setOnClickListener(v -> bottomInDialog.dismiss());
        bottomInDialog.setContentView(view);
        bottomInDialog.show();
    }
}
