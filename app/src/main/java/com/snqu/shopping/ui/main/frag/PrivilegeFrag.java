package com.snqu.shopping.ui.main.frag;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.anroid.base.SimpleFrag;
import com.anroid.base.ui.StatusBar;
import com.anroid.base.ui.TitleBarView;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 特权页面
 *
 * @author zhangquan
 */
public class PrivilegeFrag extends SimpleFrag {
    private WebViewFrag webViewFrag;
    private boolean firstInited;

    @Override
    protected int getLayoutId() {
        return R.layout.privilege_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        addAction(Constant.Event.LOGIN_SUCCESS);
    }

    private void initView() {
        webViewFrag = findChildFragment(WebViewFrag.class);
        if (firstInited || null != webViewFrag) return;

        TitleBarView titleBarView = findViewById(R.id.titlebar);
        titleBarView.setTitleText("尊享特权");
        titleBarView.mLeftButton.setVisibility(View.GONE);

        WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
        webViewParam.url = Constant.WebPage.PRIVILEGE;
        webViewParam.checkNetwork = false;
        Bundle bundle = WebViewFrag.getParamBundle(webViewParam);
        webViewFrag = new PrivilegeContentFrag();
        webViewFrag.setArguments(bundle);
        webViewFrag.setToolbar(titleBarView);
        loadRootFragment(R.id.container_layout, webViewFrag);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent pushEvent) {
        if (TextUtils.equals(pushEvent.getAction(), Constant.Event.LOGIN_SUCCESS)) { //登录成功
            if (null != webViewFrag) {
                webViewFrag.reload();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initView();
            firstInited = true;
            StatusBar.setStatusBar(mContext, true);
        }
    }

    public static class PrivilegeContentFrag extends WebViewFrag {

        @Override
        public void close() {

        }
    }
}
