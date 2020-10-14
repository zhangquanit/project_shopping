package com.anroid.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.anroid.base.ui.TitleBarView;
import com.snqu.shopping.R;
import com.snqu.shopping.ui.main.frag.WebViewFrag;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.DispatchUtil;
import com.snqu.shopping.util.statistics.task.TaskInfo;
import com.snqu.shopping.util.statistics.ui.TaskProgressView;
import com.umeng.socialize.UMShareAPI;

import java.io.Serializable;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * @author 张全
 */
public class SimpleFragAct extends BaseActivity {
    private static final String FRAG_PARAM = "FRAG_PARAM";//
    protected static final String PARAM = "key_param";
    protected static final String BUNDLE = "key_bundle";
    public static String lastClass = "";
    private SimpleFragParam fragParam;

    /**
     * 传递数据
     */
    public static class SimpleFragParam implements Serializable {
        private static final long serialVersionUID = 8769472745632311186L;
        public String title;// 标题
        public String targetCls;// 目标Class
        public Bundle paramBundle;// 参数bundle
        public int outEnterAnim, outExitAnim;// 关闭页面时的动画
        private boolean hideTitleBar;// 可覆盖标题栏
        private boolean hideStatusBar;// 隐藏状态栏 全屏显示
        private boolean translucentBg;// 透明背景
        private boolean showBg;//显示背景
        public boolean mutliPage;//多开页面

        public SimpleFragParam(int titleId, Class<? extends Fragment> cls) {
            this.title = LContext.getString(titleId);
            this.targetCls = cls.getName();
        }

        public SimpleFragParam(Class<? extends Fragment> cls) {
            this.targetCls = cls.getName();
            hideTitleBar = true;
        }

        public SimpleFragParam(String title, Class<? extends Fragment> cls) {
            this.title = title;
            this.targetCls = cls.getName();
        }

        public SimpleFragParam(int titleId, Class<? extends Fragment> cls,
                               Bundle bundle) {
            this(LContext.getString(titleId), cls, bundle);
        }

        public SimpleFragParam(String title, Class<? extends Fragment> cls,
                               Bundle bundle) {
            this.title = title;
            this.targetCls = cls.getName();
            this.paramBundle = bundle;
        }

        public SimpleFragParam(Class<? extends Fragment> cls,
                               Bundle bundle) {
            this.targetCls = cls.getName();
            this.paramBundle = bundle;
        }

        public SimpleFragParam hideTitleBar(boolean hideTtileBar) {
            this.hideTitleBar = hideTtileBar;
            return this;
        }

        public SimpleFragParam showBg() {
            this.showBg = true;
            return this;
        }

        /**
         * 隐藏状态栏
         *
         * @param hide
         * @return
         */
        public SimpleFragParam hideStatusBar(boolean hide) {
            this.hideStatusBar = hide;
            return this;
        }

        /**
         * 设置透明背景
         *
         * @return
         */
        public SimpleFragParam setTranslucentBg(boolean transulcent) {
            this.translucentBg = transulcent;
            return this;
        }

        public SimpleFragParam setExitAnim(int outEnterAnim, int outExitAnim) {
            this.outEnterAnim = outEnterAnim;
            this.outExitAnim = outExitAnim;
            return this;
        }

        @Override
        public String toString() {
            return "SimpleFragParam{" +
                    "title='" + title + '\'' +
                    ", targetCls='" + targetCls + '\'' +
                    ", paramBundle=" + paramBundle +
                    ", outEnterAnim=" + outEnterAnim +
                    ", outExitAnim=" + outExitAnim +
                    ", hideTitleBar=" + hideTitleBar +
                    ", hideStatusBar=" + hideStatusBar +
                    ", translucentBg=" + translucentBg +
                    ", showBg=" + showBg +
                    '}';
        }
    }

    public static void start(Context ctx, SimpleFragParam param) {
        if (!param.mutliPage && TextUtils.equals(lastClass, param.targetCls)) {
            return;
        }
        lastClass = param.targetCls;
        Intent intent = getStartIntent(ctx, param);
        ctx.startActivity(intent);
    }

    public static void start(Activity ctx, SimpleFragParam param,
                             int inEnterAnim, int inExitAnim) {
        if (!param.mutliPage && TextUtils.equals(lastClass, param.targetCls)) {
            return;
        }
        lastClass = param.targetCls;
        Intent intent = getStartIntent(ctx, param);
        ctx.startActivity(intent);
        ctx.overridePendingTransition(inEnterAnim, inExitAnim);
    }

    public static Intent getStartIntent(Context ctx, SimpleFragParam param) {
        Intent intent = new Intent(ctx, SimpleFragAct.class);
        // 给SimpleFragAct传递的数据
        intent.putExtra(PARAM, param);
        // 给fragment传递的数据
        if (null != param.paramBundle) {
            intent.putExtra(BUNDLE, param.paramBundle);
        }
        param.paramBundle = null;
        return intent;
    }

    @Override
    public int getLayoutId() {
        fragParam = (SimpleFragParam) getIntent()
                .getSerializableExtra(PARAM);
        if (null == fragParam) {
            return INVALIDE_LAYOUT;
        }
        // 设置全屏
        if (fragParam.hideStatusBar) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        // 透明背景
        if (fragParam.translucentBg) {
            setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        }

        //设置页面标题 方便深度数据SDK采集
        if (!TextUtils.isEmpty(fragParam.title)) {
            setTitle(fragParam.title);
        }
        return R.layout.common_act;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        SimpleFragParam fragParam = null;
        if (null != savedInstanceState) {
            fragParam = (SimpleFragParam) savedInstanceState.getSerializable(FRAG_PARAM);
        } else {
            fragParam = (SimpleFragParam) getIntent()
                    .getSerializableExtra(PARAM);
        }
        if (null == fragParam) {
            finish();
            return;
        }

        TitleBarView mToolbar = findViewById(R.id.titlebar);

        // 覆盖标题栏
        if (fragParam.hideTitleBar || fragParam.hideStatusBar) {
            mToolbar.setVisibility(View.GONE);
        } else if (!TextUtils.isEmpty(fragParam.title)) {
            // 设置标题
            mToolbar.setTitleText(fragParam.title);
        }

        //背景
        if (fragParam.showBg) {
            findViewById(R.id.common_bg).setVisibility(View.VISIBLE);
        }

        //加载frag
        try {
            Class<? extends ISupportFragment> frag = (Class<? extends ISupportFragment>) getClassLoader().loadClass(fragParam.targetCls);
            ISupportFragment fragment = findFragment(frag);
            if (fragment == null) {
                Bundle args = getIntent().getParcelableExtra(BUNDLE);
                if (null != args) {
                    args = new Bundle(args);
                }
                ISupportFragment fragmentInstance = (ISupportFragment) Fragment.instantiate(this, frag.getName(), args);
                if (fragmentInstance instanceof SimpleFrag) {
                    SimpleFrag simpleFrag = (SimpleFrag) fragmentInstance;
                    simpleFrag.setToolbar(mToolbar);
                }
                loadRootFragment(R.id.container, fragmentInstance);
            } else {
                if (fragment instanceof SimpleFrag) {
                    SimpleFrag simpleFrag = (SimpleFrag) fragment;
                    simpleFrag.setToolbar(mToolbar);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            ToastUtil.show("打开页面失败");
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(FRAG_PARAM, fragParam);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void finish() {
        lastClass = "";
        super.finish();
        if (null != fragParam && fragParam.outEnterAnim > 0 && fragParam.outExitAnim > 0) {
            overridePendingTransition(fragParam.outEnterAnim, fragParam.outExitAnim);
        }
        if (null != taskProgressView) {
            taskProgressView.stop();
        }
    }

    @Override
    protected void onDestroy() {
        lastClass = "";
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //浏览商品
        if (null != DispatchUtil.taskInfo && TextUtils.equals(DispatchUtil.taskInfo.type, "1") && !TextUtils.equals(fragParam.targetCls, WebViewFrag.class.getName())) {
            taskProgressView = CommonUtil.getTaskProgressView(this);
            TaskInfo taskInfo = DispatchUtil.taskInfo;
            DispatchUtil.taskInfo = null;
            taskProgressView.setTaskInfo(taskInfo);
        }

//        TaskInfo taskInfo = new TaskInfo();
//        taskInfo.id = "EverydayTaskTwo";
//        taskInfo.countDown = 20;
//        taskInfo.type = "1";
//        taskInfo.reward = "20";
//        TaskProgressView taskProgressView = CommonUtil.getTaskProgressView(this);
//        taskProgressView.setTaskInfo(taskInfo);


    }

    private TaskProgressView taskProgressView;
}
