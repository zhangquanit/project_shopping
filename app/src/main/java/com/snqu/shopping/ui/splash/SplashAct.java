package com.snqu.shopping.ui.splash;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.util.ext.SPUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.blankj.utilcode.util.IntentUtils;
import com.sndo.android.sdk.view.SndoADListener;
import com.sndo.android.sdk.view.SndoMoAdSplashView;
import com.snqu.shopping.App;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.AlertDialogView;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.ui.main.MainActivity;
import com.snqu.shopping.ui.main.frag.WebViewFrag;
import com.snqu.shopping.ui.splash.dialog.PrivacyDialog;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.DispatchUtil;
import com.snqu.shopping.util.log.LogClient;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.net.URLDecoder;

import common.widget.dialog.EffectDialogBuilder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 欢迎页面
 *
 * @author 张全
 */
public class SplashAct extends FragmentActivity {
    private static final String SHOW_PRIVACY_DIALOG = "SHOW_PRIVACY_DIALOG";
    private Handler mHandler = new Handler();
    private static long DELAY = 0;//延迟进入主页面时间
    private int REQUEST_PERMISSION_SETTING = 1;
    private SndoMoAdSplashView adView;
    private boolean showAd;
    private boolean hasAd;
    private static final String AD = "2wtk4balwylc";
    private View iv_bottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        //APP冷启动不需要延迟打开主页面  热启动需要延迟
        DELAY = App.coldStart ? 500 : 1000;
        App.coldStart = false;
        LogClient.appStart();
        applyPermission();
        CommonUtil.setIcon(this, HomeClient.getIcon());
        loadAd();
        iv_bottom = findViewById(R.id.iv_bottom);
    }


    /**
     * 获取权限
     */
    @SuppressLint({"AutoDispose", "CheckResult"})
    public void applyPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        if (
                        !rxPermissions.isGranted(permission.READ_EXTERNAL_STORAGE) ||
                        !rxPermissions.isGranted(permission.WRITE_EXTERNAL_STORAGE) ||
                        !rxPermissions.isGranted(permission.ACCESS_COARSE_LOCATION) ||
                        !rxPermissions.isGranted(permission.ACCESS_FINE_LOCATION)
        ) {

            rxPermissions.request(
                    permission.READ_EXTERNAL_STORAGE,
                    permission.WRITE_EXTERNAL_STORAGE,
                    permission.ACCESS_COARSE_LOCATION,
                    permission.ACCESS_FINE_LOCATION
            )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean result) throws Exception {
                            if (result) {
                                showPrivacyDialog(false);
                                Log.e("showPrivacyDialog", "2");
                            } else {
                                grantPermissionDialog(rxPermissions);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
        } else {
            showPrivacyDialog(true);
            Log.e("showPrivacyDialog", "1");
        }


    }

    @SuppressLint("CheckResult")
    private void reCheck(RxPermissions rxPermissions) {
        rxPermissions.shouldShowRequestPermissionRationale(this,
                permission.READ_EXTERNAL_STORAGE,
                permission.WRITE_EXTERNAL_STORAGE,
                permission.ACCESS_COARSE_LOCATION,
                permission.ACCESS_FINE_LOCATION)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                        if (result) {
                            applyPermission();
                        } else { //返回 false 就表示勾选了不再询问。转到设置界面现在我们唯一能做的就是跳转到我们 App 的设置界面，让用户手动开启权限了。
                            ToastUtil.show("请同意所有权限");
                            try {
                                Intent intent = IntentUtils.getLaunchAppDetailsSettingsIntent(getPackageName());
                                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            } catch (Exception e) {
                                finish();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 展示权限弹框
     */
    private void grantPermissionDialog(RxPermissions rxPermissions) {
        if (isFinishing()) return;
        AlertDialogView dialogView = new AlertDialogView(SplashAct.this)
                .setContent("为保证您正常、安全地使用，请允许开通相应权限。")
                .setSingleBtn("去允许", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reCheck(rxPermissions);
                    }
                });
        new EffectDialogBuilder(SplashAct.this)
                .setContentView(dialogView)
                .setCancelableOnTouchOutside(false)
                .setCancelable(false)
                .show();
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            delayOpenPage();
        }
    };

    Runnable mDelayRunnable = new Runnable() {
        @Override
        public void run() {
            if (!showAd) {
                openPage();
            }
        }
    };

    /**
     * 展示隐私政策弹框
     *
     * @param delay 延长时长
     */
    private void showPrivacyDialog(boolean delay) {
        if (SPUtil.getBoolean(SHOW_PRIVACY_DIALOG, false)) {
            startPage(delay);
        } else {
            if (isFinishing()) return;
            PrivacyDialog privacyDialog = new PrivacyDialog();
            privacyDialog.setCallBack(() -> {
                startPage(false);
                SPUtil.setBoolean(SHOW_PRIVACY_DIALOG, true);
                privacyDialog.dismiss();
            });

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(privacyDialog, "privacyDialog");
            ft.commitAllowingStateLoss();
        }
    }


    private void startPage(boolean delay) {
//        JMLinkAPI.getInstance().registerWithAnnotation();
//        JMLinkAPI.getInstance().deferredRouter();
        Uri uri = getIntent().getData();
        Log.e("JMLinkAPI", "data = " + uri);
        if (uri != null) {
            String param = uri.getQueryParameter("param");
            if (TextUtils.isEmpty(param)) {
                startOrigin(delay);
                return;
            }
            String pageInfo = null;
            try {
                pageInfo = URLDecoder.decode(param.trim(), "UTF-8");
                DispatchUtil.goToPage(this, pageInfo);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("DispatchActivity", "HttpUrlActivity，打开页面失败 data=" + uri);
                startOrigin(delay);
//                JMLinkAPI.getInstance().checkYYB(new YYBCallback() {
//                    @Override
//                    public void onFailed() {
//                        startOrigin(delay);
//                    }
//
//                    @Override
//                    public void onSuccess() {
//                        //待處理
//                        startOrigin(delay);
//                    }
//                });
            }
        } else {
            startOrigin(delay);
//            JMLinkAPI.getInstance().checkYYB(new YYBCallback() {
//                @Override
//                public void onFailed() {
//                    startOrigin(delay);
//                }
//
//                @Override
//                public void onSuccess() {
//                    //待處理
//                    startOrigin(delay);
//                }
//            });
        }
    }

    private void startOrigin(boolean delay) {
        if (delay) {
            if (null != mHandler) mHandler.postDelayed(mRunnable, DELAY);
        } else {
            openPage();
        }
    }

    private void delayOpenPage() {
        if (hasAd) {//有广告
            adView.show();
            mHandler.postDelayed(mDelayRunnable, 2 * 1000); //2秒后还没有显示广告 则进入主页面
        } else {
            openPage();
        }
    }

    private void openPage() {
//        SplashTwoAct.start(this);

        if (UserClient.hasShowGuide()) {
            MainActivity.start(this);
        } else { //跳转到引导界面
            GuideActivity.start(this);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
        mHandler.removeCallbacks(mDelayRunnable);
        mRunnable = null;
        mHandler = null;
        adView.removeAdListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        applyPermission();
    }


    private void loadAd() {
        adView = findViewById(R.id.ad);
        adView.setAdListener(new SndoADListener() {
            @Override
            public void onShowEnd() {
                LogUtil.d("SplashAd", "onShowEnd");
//                openPage();
            }

            @Override
            public void onADLoaded(boolean has) {
                hasAd = has;
                LogUtil.d("SplashAd", "onADLoaded hasAd=" + hasAd);
            }

            @Override
            public void onADPresent() {
                showAd = true;
                iv_bottom.setVisibility(View.VISIBLE);
                LogUtil.d("SplashAd", "onADPresent");
            }

            @Override
            public void onADClicked(String url) {
                LogUtil.d("SplashAd", "onADClicked url=" + url);
                if (!TextUtils.isEmpty(url)) {
                    WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                    webViewParam.url = url;
                    webViewParam.toMain = true;
                    WebViewFrag.start(SplashAct.this, webViewParam);
                    adView.removeAdListener();
                    finish();
                }
            }

            @Override
            public void onADTick(long millisUntilFinished) {
                int ticker = (int) (millisUntilFinished / 1000);
                LogUtil.d("SplashAd", "onADTick " + ticker);
                if (ticker == 1) {
                    openPage();
                }
            }
        });
        adView.handleForward(true);
        adView.setCountTime(4);
        adView.setAdId(AD);
    }
}
