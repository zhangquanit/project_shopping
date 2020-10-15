package com.snqu.shopping.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.util.LContext;
import com.android.util.ext.SPUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.kepler.jd.Listener.OpenAppAction;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.snqu.shopping.App;
import com.snqu.shopping.BuildConfig;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.base.BaseResponseObserver;
import com.snqu.shopping.data.base.HttpResponseException;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.GoodsClient;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.PromotionLinkBodyEntity;
import com.snqu.shopping.data.goods.entity.PromotionLinkEntity;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.data.home.entity.artical.ArticalEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.ui.goods.AliAuthActivity;
import com.snqu.shopping.ui.goods.util.JumpUtil;
import com.snqu.shopping.ui.login.LoginFragment;
import com.snqu.shopping.ui.main.frag.WebViewFrag;
import com.snqu.shopping.ui.main.view.FlitingCoverBar;
import com.snqu.shopping.ui.main.view.ShareWxTipDialogView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.ui.video.PlayerVideoActivity;
import com.snqu.shopping.util.statistics.ui.TaskProgressView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.util.List;

import common.widget.dialog.loading.LoadingDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author 张全
 */
public class CommonUtil {
    public static String PLATE;
    public static String PLATE_CHILD;
    private static final String CLIPBOARD_TEXT = "CLIPBOARD_TEXT";
    private HomeViewModel homeViewModel;

    /**
     * 检测是否安装支付宝
     *
     * @param context
     * @return
     */
    public static boolean checkAliPayInstalled(Context context) {

        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    public static SpannableStringBuilder getShop(String name, int fontSize) {
        int d4 = DeviceUtil.dip2px(LContext.getContext(), 4);
        return new SpanUtils()
                .appendImage(R.mipmap.ic_launcher)
                .setHorizontalAlign(Layout.Alignment.ALIGN_CENTER)
                .appendSpace(d4)
                .append(name).setForegroundColor(Color.parseColor("#848487")).setFontSize(fontSize, true)
                .create();

    }

    public static SpannableStringBuilder getPrice(GoodsEntity item) {
        String price = item.getNow_price();
        String oldPrice = item.getOld_price();
        if (TextUtils.isEmpty(price)) {
            price = "0.0";
        }
        if (TextUtils.isEmpty(oldPrice)) {
            oldPrice = "0.0";
        }
        int d7 = DeviceUtil.dip2px(LContext.getContext(), 7);
        SpanUtils spanUtils = new SpanUtils();

        if (!TextUtils.isEmpty(item.getCouponPrice())) {
            //有优惠券显示券后
            spanUtils.append("券后").setForegroundColor(Color.parseColor("#25282D")).setFontSize(12, true);
        }

        if (!TextUtils.equals(price, oldPrice)) {
            //有返利金显示原件
            spanUtils.append(price).setForegroundColor(Color.parseColor("#F73737")).setFontSize(16, true).setBold().appendSpace(d7)
                    .append(oldPrice).setForegroundColor(Color.parseColor("#848487")).setFontSize(11, true).setStrikethrough();
        } else {
            spanUtils.append(price).setForegroundColor(Color.parseColor("#F73737")).setFontSize(16, true).setBold();
        }
        return spanUtils.create();
    }

    public static FlitingCoverBar getCoverBar(Activity activity) {
        RelativeLayout relativeLayout = activity.findViewById(R.id.act_root);
        FlitingCoverBar coverBar = new FlitingCoverBar(activity);
        coverBar.setVisibility(View.GONE);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeLayout.addView(coverBar, layoutParams);
        return coverBar;

    }

    public static TaskProgressView getTaskProgressView(Activity activity) {
        RelativeLayout relativeLayout = activity.findViewById(R.id.act_root);
        TaskProgressView taskProgressView = new TaskProgressView(activity);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.bottomMargin = DeviceUtil.dip2px(activity, 80);
        layoutParams.rightMargin = DeviceUtil.dip2px(activity, 5);
        relativeLayout.addView(taskProgressView, layoutParams);
        taskProgressView.setVisibility(View.GONE);
        return taskProgressView;
    }


    public static boolean isInnerUrl(String url) {
        List<String> safeDomain = HomeClient.getSafeDomain();
        for (String item : safeDomain) {
            if (url.contains(item)) {
                return true;
            }
        }
        return false;

//        List<String> hosts = new ArrayList<>();
//        //自有
//        hosts.add("xinletao");
//        hosts.add("xin1");
//        hosts.add("zhoubianshuo");
//        hosts.add("addashi");
//        hosts.add("snhe");
//        hosts.add("97playing");
//        hosts.add("addashi");
//        hosts.add("snhe");
//        for (String item : hosts) {
//            if (url.contains(item)) {
//                return true;
//            }
//        }
//        return false;
    }

    public static void startWebFrag(Activity ctx, AdvertistEntity data) {

        UserEntity user = UserClient.getUser();
        //登录
        if (data.needLogin == 1) {
            if (null == user) {
                LoginFragment.Companion.start(ctx);
                return;
            }
        }

        //淘宝授权
        if (data.needAuth == 1) {
            if (null == user) {
                LoginFragment.Companion.start(ctx);
                return;
            }
            if (user.has_bind_tb == 0) { //未授权
                showTBAuthDialog(ctx);
                return;
            }
        }

        //是否需要转链
        if (TextUtils.equals(data.direct, "2")) {
            //转链  转链完成后打开webview
            LoadingDialog loadingDialog = LoadingDialog.showDialog(ctx, "请稍候");
            PromotionLinkBodyEntity promotionLinkBodyEntity = new PromotionLinkBodyEntity(data.link_type, data.tid, data.link_url, data.item_source, "1");
            GoodsClient.doPromotionLink(promotionLinkBodyEntity)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseResponseObserver<ResponseDataObject<PromotionLinkEntity>>() {
                        @Override
                        public void onSuccess(ResponseDataObject<PromotionLinkEntity> value) {
                            if (null == value.data || TextUtils.isEmpty(value.data.getClick_url())) {
                                LogUtil.d("请求失败，请重试");
                            }
                            JumpUtil.isShowShare = true;
                            if (data.open_third_app == 1) {
                                if (!TextUtils.isEmpty(value.data.getClick_url())) {
                                    if (TextUtils.equals(data.item_source, Constant.BusinessType.TB) || TextUtils.equals(data.item_source, Constant.BusinessType.TM)) {
                                        JumpUtil.jumpToToAli(ctx, value.data.getClick_url(), false);
                                    } else if (TextUtils.equals(data.item_source, Constant.BusinessType.JD)) {
                                        JumpUtil.jumpToJdCouponsPage(ctx, value.data.getClick_url(), new OpenAppAction() {
                                            @Override
                                            public void onStatus(int i) {
                                            }
                                        });
                                    } else {
                                        JumpUtil.jumpToH5(ctx, value.data.getApp_url(), value.data.getClick_url(), false);
                                    }
                                } else {
                                    JumpUtil.jumpToH5(ctx, value.data.getApp_url(), value.data.getClick_url(), false);
                                }
                            } else {
                                String click_url = value.data.getClick_url();
                                jumpToThrid(ctx, data.platform, click_url, true);
                            }
                        }

                        @Override
                        public void onError(HttpResponseException e) {
                            //做拼多多的授权处理
                            if (e.resultCode == 500002) {
                                ResponseDataObject<PromotionLinkEntity> pd = (ResponseDataObject<PromotionLinkEntity>) e.data;
                                if (pd != null && pd.data != null) {
                                    JumpUtil.authPdd(ctx, pd.data.getAuth_url());
                                }
                            } else {
                                ToastUtil.show(e.getMsg());
                                LogUtil.d("请求失败，请重试");
                            }
                        }

                        @Override
                        public void onEnd() {
                            loadingDialog.dismiss();
                        }
                    });
            return;
        }

        //解析打开页面
        String path = data.direct_protocal;
        if (TextUtils.isEmpty(path)) {
            return;
        }
        String param = Uri.parse(path).getQueryParameter("param");
        if (TextUtils.isEmpty(param)) {
            return;
        }


        try {
            if (path.startsWith(WebViewFrag.URL_OPENTAOBAO)) { //打开淘宝
                JumpUtil.jumpToToAli(ctx, param, false);
            } else if (path.startsWith(WebViewFrag.URL_OPENPDD)) { //打开拼多多
                JumpUtil.jumpToPdd(ctx, param, false);
            } else if (path.startsWith(WebViewFrag.URL_OPENJD)) { //打开京东
                JumpUtil.jumpToJdCouponsPage(ctx, param, new OpenAppAction() {
                    LoadingDialog loadingDialog;

                    @Override
                    public void onStatus(int i) {
                        ctx.runOnUiThread(() -> {
                            if (i == OpenAppAction.OpenAppAction_start) {
                                loadingDialog = LoadingDialog.showCancelableDialog(ctx, "页面跳转中...");
                            } else {
                                if (null != loadingDialog) loadingDialog.dismiss();
                            }
                        });
                    }
                });
            } else {
                DispatchUtil.goToPage(ctx, param);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("打开页面失败");
        }
    }

    @SuppressLint("CheckResult")
    public static void showTBAuthDialog(Context ctx) {
        LoadingDialog loadingDialog = LoadingDialog.showDialog(ctx, "请稍候");
        HomeClient.getAuthUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new BaseResponseObserver<ResponseDataObject<String>>() {
                    @Override
                    public void onSuccess(ResponseDataObject<String> value) {
                        if (!TextUtils.isEmpty(value.data)) {
                            AliAuthActivity.start(ctx, value.data);
                        } else {
//                                    ToastUtil.show("请求失败，请重试");
                            LogUtil.d("请求失败，请重试");
                        }
                    }

                    @Override
                    public void onError(HttpResponseException e) {
                        e.printStackTrace();
//                                ToastUtil.show("请求失败，请重试");
                        LogUtil.d("请求失败，请重试");
                    }

                    @Override
                    public void onEnd() {
                        loadingDialog.dismiss();
                    }
                });
    }


    public static void jumpToThrid(Activity ctx, String platform, String clickUrl, boolean isDetailJump) {
        WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
        webViewParam.url = clickUrl;
        webViewParam.isDetailJump = false;
        webViewParam.isPromotionLinkJump = isDetailJump;
        webViewParam.isShowShare = JumpUtil.isShowShare;
        WebViewFrag.start(ctx, webViewParam);
    }

    //-------------------------------------动态切换icon

    public static void setIcon(Context ctx, int icon) {
        if (App.devEnv) { //prod_test
            return;
        }
        if (icon == 1) { //双11
            disableComponent(normalComponent(ctx));
            enableComponent(componentName11(ctx));
            disableComponent(componentName12(ctx));
        } else if (icon == 2) { //双12
            disableComponent(normalComponent(ctx));
            disableComponent(componentName11(ctx));
            enableComponent(componentName12(ctx));
        } else {  //默认
            enableComponent(normalComponent(ctx));
            disableComponent(componentName11(ctx));
            disableComponent(componentName12(ctx));
        }
    }

    private static ComponentName normalComponent(Context pkg) {
        return new ComponentName(pkg, pkg.getPackageName() + ".DefaultLauncher");
    }

    private static ComponentName componentName11(Context pkg) {
        return new ComponentName(pkg, pkg.getPackageName() + ".Launcher11");
    }

    private static ComponentName componentName12(Context pkg) {
        return new ComponentName(pkg, pkg.getPackageName() + ".Launcher12");
    }

    private static void enableComponent(ComponentName componentName) {
        PackageManager pm = LContext.getContext().getPackageManager();
        if (pm.getComponentEnabledSetting(componentName) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }

    }

    private static void disableComponent(ComponentName componentName) {
        PackageManager pm = LContext.getContext().getPackageManager();
        if (pm.getComponentEnabledSetting(componentName) != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }

    public static String getVipText(int level) {
        switch (level) {
            case 2:
                return "会员";
            case 3:
                return "超级会员";
            case 4:
                return "运营总监";
            default:
                return "";
        }
    }

    public static String getVipTagText(int level) {
        switch (level) {
            case 2:
                return "VIP";
            case 3:
                return "SVIP";
            case 4:
                return "MD";
            default:
                return "";
        }
    }

    /**
     * 保存文件到相册
     *
     * @param ctx
     * @param file
     */
    public static void saveFileToGallery(Context ctx, File file) {
        if (null == file) return;
        try {
            MediaStore.Images.Media.insertImage(ctx.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
            LogUtil.d("gallery", "保存相册成功");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("gallery", "保存相册失败 e=" + e.getMessage());
        }

        // 通知图库更新11
        try {
            ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("gallery", "通知图库更新失败 111 e=" + e.getMessage());
        }

        // 通知图库更新22
        try {
            String[] paths = new String[]{file.getAbsolutePath()};
            MediaScannerConnection.scanFile(ctx, paths, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(uri);
                    ctx.sendBroadcast(mediaScanIntent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("gallery", "通知图库更新失败 222 e=" + e.getMessage());
        }
    }

    public static void notifyFileToGallery(Context ctx, File file) {
        if (file == null) {
            return;
        }
        // 通知图库更新11
        try {
            ctx.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("gallery", "通知图库更新失败 111 e=" + e.getMessage());
        }

        // 通知图库更新22
        try {
            String[] paths = new String[]{file.getParentFile().getAbsolutePath()};
            MediaScannerConnection.scanFile(ctx, paths, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(uri);
                    ctx.sendBroadcast(mediaScanIntent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("gallery", "通知图库更新失败 222 e=" + e.getMessage());
        }
    }


    public static void clearClipboard() {
        ClipboardManager manager = (ClipboardManager) LContext.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager != null) {
            try {
                manager.setPrimaryClip(ClipData.newPlainText(null, null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setClipboardTextWithTag(String text) {
        SPUtil.setString(CLIPBOARD_TEXT, text);
        if (!TextUtils.isEmpty(text)) {
            addClipboradTag();
        }
    }

    public static void setClipboardText(String text) {
        SPUtil.setString(CLIPBOARD_TEXT, text);
    }

    public static String getClipboardText() {
        return SPUtil.getString(CLIPBOARD_TEXT, null);
    }

    public static void addToClipboard(String text) {
        try {
            ClipboardManager cm = (ClipboardManager) LContext.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addClipboradTag() {
        try {
            ClipboardManager cm = (ClipboardManager) LContext.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = cm.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                clipData.addItem(new ClipData.Item("⭐"));
            }
            cm.setPrimaryClip(clipData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCooperationChannel() {
        String cooperChannels = BuildConfig.cooperChannels;
        if (TextUtils.isEmpty(cooperChannels)) {
            return null;
        }
        String[] channels = cooperChannels.split(",");
        for (String channel : channels) {
            if (TextUtils.equals(LContext.channel, channel)) {
                return channel;
            }
        }
        return null;
    }

    public static void jumpToTaskPage(Context ctx) {
        if (UserClient.isLogin()) {
            WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
            webViewParam.url = Constant.WebPage.TASK_PAGE;
            WebViewFrag.start(ctx, webViewParam);
        } else {
            LoginFragment.start(ctx);
        }
    }

    public static void jumpToEarningPage(Context ctx) {
        if (UserClient.isLogin()) { //先登录
            WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
            webViewParam.url = Constant.WebPage.EARNING_PAGE;
            WebViewFrag.start(ctx, webViewParam);
        } else {
            LoginFragment.Companion.start(ctx);
        }
    }

    /**
     * 跳转到文章详情
     *
     * @param ctx
     * @param articalEntity
     */
    public static void jumpToArticalDetial(Activity ctx, ArticalEntity articalEntity) {
        if (TextUtils.equals(articalEntity.type, "3")) { //播放视频
            PlayerVideoActivity.Companion.start(ctx, articalEntity.jump_url);
        } else if (articalEntity.share_wechat_open == 1) {
            shareArtical(ctx, articalEntity);
        } else {
            WebViewFrag.WebViewParam.ShareInfo shareInfo = new WebViewFrag.WebViewParam.ShareInfo();
            shareInfo.title = articalEntity.title;
            shareInfo.content = articalEntity.description;
            shareInfo.url = articalEntity.jump_url;
            shareInfo.pic_url = articalEntity.cover_image;
            WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
            webViewParam.url = articalEntity.jump_url;
            webViewParam.shareInfo = shareInfo;
            WebViewFrag.start(ctx, webViewParam);
        }
    }

    public static void shareArtical(Activity act, ArticalEntity articalEntity) {
        ShareWxTipDialogView.show(act, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtil.share(act, articalEntity.jump_url, articalEntity.title, articalEntity.description, articalEntity.cover_image, SHARE_MEDIA.WEIXIN);
            }
        });
    }

    public static void setRefreshHeaderWhiteText(SmartRefreshLayout refreshLayout) {
        RefreshHeader refreshHeader = refreshLayout.getRefreshHeader();
        if (refreshHeader instanceof ClassicsHeader) {
            ClassicsHeader header = (ClassicsHeader) refreshHeader;
            header.setAccentColor(Color.WHITE);
        }
    }
}
