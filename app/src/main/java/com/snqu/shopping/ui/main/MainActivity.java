package com.snqu.shopping.ui.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.util.LContext;
import com.android.util.ext.SPUtil;
import com.android.util.ext.ToastUtil;
import com.anroid.base.BaseActivity;
import com.anroid.base.BaseFragment;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.receiver.BaseReceiver;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.AlertDialogView;
import com.snqu.shopping.common.ui.BottomBar;
import com.snqu.shopping.common.ui.BottomBarTab;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.PushEntity;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.goods.helper.RecodeHelper;
import com.snqu.shopping.ui.login.LoginFragment;
import com.snqu.shopping.ui.main.frag.ActivityDetailFrag;
import com.snqu.shopping.ui.main.frag.HomeFrag;
import com.snqu.shopping.ui.main.frag.PrivilegeFrag;
import com.snqu.shopping.ui.main.frag.WebViewFrag;
import com.snqu.shopping.ui.main.frag.classification.frag.ClassificationFrag;
import com.snqu.shopping.ui.main.frag.collection.CollectionFrag;
import com.snqu.shopping.ui.main.frag.community.CommunityFrag;
import com.snqu.shopping.ui.main.scan.ADDialogView;
import com.snqu.shopping.ui.main.view.FlitingCoverBar;
import com.snqu.shopping.ui.main.view.UpdateDialogView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.ui.mine.dialog.UpDateDialogFragment;
import com.snqu.shopping.ui.mine.fragment.BindAlipayFragment;
import com.snqu.shopping.ui.mine.fragment.InvitateFrag;
import com.snqu.shopping.ui.mine.fragment.InvitePersonFragment;
import com.snqu.shopping.ui.mine.fragment.MyTeamFragment;
import com.snqu.shopping.ui.mine.fragment.PersonFragment;
import com.snqu.shopping.ui.mine.fragment.SelfBalanceFragment;
import com.snqu.shopping.ui.mine.fragment.WithdrawalFragment;
import com.snqu.shopping.ui.order.OrderActivity;
import com.snqu.shopping.ui.vip.frag.VipFrag;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.PushUtil;
import com.snqu.shopping.util.log.LogClient;
import com.snqu.shopping.util.statistics.ActivationUtil;
import com.snqu.shopping.util.statistics.SndoData;
import com.snqu.shopping.util.statistics.task.NewTaskType;
import com.snqu.shopping.util.statistics.task.TaskInfo;
import com.snqu.shopping.util.statistics.task.TaskReport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import common.widget.dialog.EffectDialogBuilder;
import component.update.AppDownloadClient;
import component.update.AppVersion;
import component.update.VersionUpdateListener;

/**
 * 主页面
 *
 * @author zhangquan
 */
public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private static final String TARGET_PAGE = "TARGET_PAGE";
    private static final String TASK_INFO = "TASK_INFO";
    private BottomBar mNavigationView;
    private List<BaseFragment> mFragmentList = new ArrayList<>();
    private long mExitTime = 0;
    private static final int TARGET_LOGIN = -1;
    private VersionUpdateListener mUpdateListener;
    private boolean isDestroyed;
    FlitingCoverBar coverBar;
    public static TaskInfo taskInfo;
    private List<String> tabNames = new ArrayList<>();


    public static void start(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * 打开我的
     *
     * @param
     */
    public static void startForPage(Context ctx, int pageIndex) {
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.putExtra(TARGET_PAGE, pageIndex);
        ctx.startActivity(intent);
    }

    public static void startForPage(Context ctx, int pageIndex, TaskInfo taskInfo) {
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.putExtra(TARGET_PAGE, pageIndex);
        intent.putExtra(TASK_INFO, taskInfo);
        ctx.startActivity(intent);
    }

    /**
     * 登录页面
     *
     * @param ctx
     */
    public static void startForLogin(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.putExtra(TARGET_PAGE, TARGET_LOGIN);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseClipboard = false;
        LogClient.log(TAG, "onCreate");
        RecodeHelper.INSTANCE.setMainActivityIsExist(true);
        mFragmentList.clear();
        addAction(Constant.Event.CLASSFICATION_ITEM);
        addAction(Constant.Event.LOGIN_SUCCESS);

        if (null == findFragment(HomeFrag.class)) {
            BaseFragment homeFrag = new HomeFrag();
            mFragmentList.add(homeFrag);

            PrivilegeFrag privilegeFrag = new PrivilegeFrag();
            WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
            webViewParam.url = Constant.WebPage.PRIVILEGE;
            Bundle bundle = WebViewFrag.getParamBundle(webViewParam);
            privilegeFrag.setArguments(bundle);
            mFragmentList.add(privilegeFrag);

            BaseFragment mallFrag = new VipFrag();
            mFragmentList.add(mallFrag);

            BaseFragment communityFrag = new CommunityFrag();
            mFragmentList.add(communityFrag);

            BaseFragment personFragment = new PersonFragment();
            mFragmentList.add(personFragment);

            loadMultipleRootFragment(R.id.layout_container_content, 0,
                    homeFrag, privilegeFrag, mallFrag, communityFrag, personFragment
            );
        } else {
            mFragmentList.add(findFragment(HomeFrag.class));
            mFragmentList.add(findFragment(PrivilegeFrag.class));
            mFragmentList.add(findFragment(VipFrag.class));
            mFragmentList.add(findFragment(CommunityFrag.class));
            mFragmentList.add(findFragment(PersonFragment.class));
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.main_activity;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        responseCodeReceiver.register(this);
        initPushEvent(getIntent());
        initView();
        initData();
        initOthers();
    }

    private void initView() {
        coverBar = findViewById(R.id.coverbar);
        initBottomBar();
    }

    public void initBottomBar() {
        tabNames.add("首页");
        tabNames.add("特权");
        tabNames.add("会员");
        tabNames.add("发圈");
        tabNames.add("我的");

        mNavigationView = findViewById(R.id.bottom_bar);
        mNavigationView
                .addItem(new BottomBarTab(this, R.drawable.tab_home, tabNames.get(0)))
                .addItem(new BottomBarTab(this, R.drawable.tab_right, tabNames.get(1)))
//                .addItem(new BottomBarTab(this, R.drawable.tab_shop, tabNames.get(2)))
                .addItem(new BottomBarTab(this, R.drawable.tab_vip, tabNames.get(2)))
                .addItem(new BottomBarTab(this, R.drawable.tab_community, tabNames.get(3)))
                .addItem(new BottomBarTab(this, R.drawable.tab_user, tabNames.get(4)));
        mNavigationView.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position, int prePosition) {
                if (position == 1 || position == 2 || position == 3) { // 特权/发圈
                    if (!UserClient.isLogin()) {
                        LoginFragment.Companion.start(MainActivity.this);
                        //还原状态
                        mNavigationView.setCurrentItem(prePosition);
                    } else {
                        if (TextUtils.isEmpty(UserClient.getUser().inviter)) {
                            InvitePersonFragment.start(MainActivity.this);
                            //还原状态
                            mNavigationView.setCurrentItem(prePosition);
                        } else {
                            showHideFragment(mFragmentList.get(position), mFragmentList.get(prePosition));
                        }
                    }
                } else {
                    showHideFragment(mFragmentList.get(position), mFragmentList.get(prePosition));
                }

                SndoData.event(SndoData.XLT_EVENT_HOME_TAB, SndoData.XLT_ITEM_TITLE, tabNames.get(position));
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
                if (position == 0) {
                    EventBus.getDefault().post(new PushEvent(Constant.Event.HOME_TAP_TOP));
                } else if (position == 4) {
                    EventBus.getDefault().post(new PushEvent(Constant.Event.PERSON_TAP_TOP));
                }
            }
        });
    }

    public FlitingCoverBar getCoverBar() {
        return coverBar;
    }

    private void initData() {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        //安全域名
        homeViewModel.getSafeDomain();

        //版本检测
        post(new Runnable() {
            @Override
            public void run() {
                checkVersion();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onEvent(PushEvent event) {
        if (TextUtils.equals(event.getAction(), Constant.Event.CLASSFICATION_ITEM)) {
            //分类-查看更多
//            mNavigationView.setCurrentItem(1);
            SimpleFragAct.SimpleFragParam simpleFragParam = new SimpleFragAct.SimpleFragParam("商品分类", ClassificationFrag.class);
            Bundle bundle = new Bundle();
            CategoryEntity selEntity = (CategoryEntity) event.getData();
            bundle.putSerializable(Constant.Event.CLASSFICATION_ITEM, selEntity);
            simpleFragParam.paramBundle = bundle;
            SimpleFragAct.start(this, simpleFragParam);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int pagePos = intent.getIntExtra(TARGET_PAGE, 0);
        if (pagePos == TARGET_LOGIN) { //重新登录
            return;
        }
        initPushEvent(intent);
        mNavigationView.setCurrentItem(pagePos);

        //任务
        if (intent.hasExtra(TASK_INFO)) {
            TaskInfo taskInfo = (TaskInfo) intent.getSerializableExtra(TASK_INFO);
            MainActivity.taskInfo = taskInfo;
            if (pagePos == 4) {
                MainActivity.taskInfo = null;
                TaskReport.newTaskReport(this, NewTaskType.ME);
            }
        }
    }

    private void initOthers() {

//        SimulatorUtil.check(this);
        ActivationUtil.activate();
    }

    //----------------------------------------------------------
    private MutableLiveData<NetReqResult> liveData = new MutableLiveData<>();
    private boolean parseFinished; //完成剪贴板解析
    private boolean versionFinished; //完成版本升级检测
    private AppVersion appVersion;
    private List<AdvertistEntity> advertistEntities;
    private List<AdvertistEntity> fredAdList;
    private boolean isDialogShowed;

    /**
     * 版本检测
     *
     * @param appVersion
     */
    private void showVersion(AppVersion appVersion) {
        this.appVersion = appVersion;
        versionFinished = true;

        if (null != appVersion && appVersion.forceUpdate == 1) { //强制升级
            showVersionDialog(appVersion);
            return;
        }

        String searchText = getSearchText();
        LogClient.log(TAG, "showVersion, searchText=" + searchText);
        if (!TextUtils.isEmpty(searchText)) {
            HomeViewModel homeViewModel =
                    ViewModelProviders.of(this).get("ClipData", HomeViewModel.class);
            liveData.observe(this, new Observer<NetReqResult>() {
                @Override
                public void onChanged(@Nullable NetReqResult netReqResult) {
                    String lastText = (String) netReqResult.extra;
                    //记录本次剪贴板内容
                    ClipboardManager cm = (ClipboardManager) LContext.getContext().getSystemService(CLIPBOARD_SERVICE);
                    if (cm != null) {
                        ClipData clipData = cm.getPrimaryClip();
                        if (clipData != null) {
                            CommonUtil.setClipboardText(clipData.toString());
                        }
                    }

                    switch (netReqResult.tag) {
                        case HomeViewModel.TAG_GOODS_DECODE_URL: //URL解析
                        case HomeViewModel.TAG_GOODS_DECODE_CODE: //淘口令
                            showPareseResult(lastText, netReqResult);
                            break;
                        default:
                            break;
                    }
                }
            });

            homeViewModel.decodeGoodByCode(searchText, 1, liveData, "0");
        } else {
            parseFinished = true;
            if (null != appVersion) { //普通升级
                showVersionDialog(appVersion);
            } else if (null != advertistEntities) { //广告
                showAdDialog();
            }
        }
    }

    /**
     * 剪贴板解析
     */
    private void showPareseResult(String search, NetReqResult netReqResult) {
        parseFinished = true;
        if (null != netReqResult.data) {
            ResponseDataObject<GoodsEntity> goodsDecodeEntity = (ResponseDataObject<GoodsEntity>) netReqResult.data;
            if (goodsDecodeEntity.code == 502) {
                isDialogShowed = true;
                showScanUrlDialog(goodsDecodeEntity.message);
                return;
            }
            GoodsEntity data = goodsDecodeEntity.data;
            if (null == data) {
                if (null != appVersion) {
                    showVersionDialog(appVersion);
                } else if (null != advertistEntities) {
                    showAdDialog();
                }
                return;
            }

            if (!TextUtils.isEmpty(data.getGoods_id())) {
                isDialogShowed = true;
                showGoodDialog(data);
                return;
            }

            if (TextUtils.equals(data.getNeed_search(), "1")) { //需要搜索
                isDialogShowed = true;
                showSearchDialog(search, data);
            }
        } else {
            if (null != appVersion) {
                showVersionDialog(appVersion);
            } else if (null != advertistEntities) {
                showAdDialog();
            }
        }

    }

    /**
     * 版本弹框
     *
     * @param appVersion
     */
    private void showVersionDialog(AppVersion appVersion) {
        isDialogShowed = true;
        if (isDestroyed || isFinishing()) return;
        UpDateDialogFragment upDateDialogFragment = new UpDateDialogFragment();
        Bundle bundle = new Bundle();
        upDateDialogFragment.setArguments(bundle);
        bundle.putSerializable("appversion", appVersion);
        upDateDialogFragment.setCancelable(false);
        if (isDestroyed || isFinishing()) return;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(upDateDialogFragment, "UpDateDialogFragment");
        ft.commitAllowingStateLoss();
    }

    /**
     * 首页弹框广告
     *
     * @param advertistEntities
     */
    public void setAd(List<AdvertistEntity> advertistEntities, List<AdvertistEntity> freeEntitys) {
        this.advertistEntities = advertistEntities;
        this.fredAdList = freeEntitys;
        showAdDialog();
    }

    private void showAdDialog() {
        //完成版本检测或剪贴板解析 且未弹框
        if (!versionFinished || !parseFinished || isDialogShowed) {
            return;
        }

        //0元购弹框
        if (showFreeAdDialog(fredAdList)) {
            return;
        }

        //广告弹框
        if (null == advertistEntities || advertistEntities.isEmpty()) {
            return;
        }

        AdvertistEntity advertistEntity = advertistEntities.get(0);
        if (TextUtils.isEmpty(advertistEntity.image)) {
            return;
        }
        long lastShowTime = SPUtil.getLong("home_ad");
        long diffTime = System.currentTimeMillis() - lastShowTime;
        if (diffTime > advertistEntity.per * 1000) { //超过间隔时间 显示
            GlideUtil.loadBitmap(mContext, advertistEntity.image, new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                    if (isFinishing()) return;
                    SPUtil.setLong("home_ad", System.currentTimeMillis());
                    ADDialogView tipDialogView = new ADDialogView(mContext);
                    tipDialogView.setAd(advertistEntities, bitmap);
                    new EffectDialogBuilder(mContext)
                            .setContentView(tipDialogView)
                            .setCancelable(false)
                            .setCancelableOnTouchOutside(false)
                            .show();
                }
            });
        }
    }

    /**
     * 版本检测
     */
    private void checkVersion() {
        UpdateDialogView.fileSize = -1;
        mUpdateListener = new VersionUpdateListener() {
            @Override
            public void onNoVersionReturned() {
                showVersion(null);
            }

            @Override
            public void fail() {
                showVersion(null);
            }

            @Override
            public void onNewVersionReturned(AppVersion appVersion) {
                if (isFinishing()) return;
                showVersion(appVersion);
            }
        };
        AppDownloadClient.doCheckVersion(mUpdateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        parseClipboard = true;
        taskInfo = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        mUpdateListener = null;
        AppDownloadClient.stopCheckVersion();
        try {
            unregisterReceiver(responseCodeReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogClient.appEnd();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                showToastShort("再按一次退出");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    BaseReceiver responseCodeReceiver = new BaseReceiver() {
        final String LOGIN = LContext.pkgName + ".action.login";
        final String APCHANGE = LContext.pkgName + ".action.apchange";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), LOGIN)) {
                ToastUtil.show(R.string.tip_relogin);
                startForLogin(MainActivity.this);
            } else if (TextUtils.equals(intent.getAction(), APCHANGE)) {
                AlertDialogView alertDialogView = new AlertDialogView(MainActivity.this)
                        .setTitle("温馨提示")
                        .setContent("本应用为非官方正版应用，请从正规市场或官网下载.")
                        .setSingleBtn("好的", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Process.killProcess(Process.myPid());
                            }
                        });
                if (isFinishing()) return;
                new EffectDialogBuilder(MainActivity.this)
                        .setContentView(alertDialogView)
                        .setCancelable(false)
                        .setCancelableOnTouchOutside(false)
                        .show();
            }
        }

        @Override
        protected IntentFilter getIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(LOGIN);
            intentFilter.addAction(APCHANGE);//app攥改弹框
            return intentFilter;
        }
    };

    /**
     * 对推送来的消息进行分发处理
     */
    private void initPushEvent(Intent intent) {


        if (null == intent || !TextUtils.equals("push", intent.getAction())) {
            return;
        }

        PushEntity pushEntity = (PushEntity) intent.getSerializableExtra("push_data");
        if (pushEntity == null) {
            return;
        }

        //点击汇报
        if (!TextUtils.isEmpty(pushEntity.id)) {
            HomeViewModel homeViewModel =
                    ViewModelProviders.of(this).get(HomeViewModel.class);
            homeViewModel.umengClickReport(pushEntity.id);
        }


        //深度数据汇报
        String url = "null";
        if (pushEntity.param != null && !TextUtils.isEmpty(pushEntity.param.url)) {
            url = pushEntity.param.url;
        }
        SndoData.event(
                "PushClick",
                "message_title", pushEntity.title == null ? "null" : pushEntity.title,
                "message_content", pushEntity.content == null ? "null" : pushEntity.content,
                "skip_name", "null",
                "skip_url", url,
                "target_people", "null",
                "push_time", "null",
                "valid_time", "null",
                "push_close", true,
                "push_id", pushEntity._id
        );

        if (PushUtil.TYPE_OPENWEBVIEW.equals(pushEntity.page)) {
            if (!TextUtils.isEmpty(pushEntity.param.url)) {
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.url = pushEntity.param.url;
                WebViewFrag.start(mContext, webViewParam);
            }
        } else if (PushUtil.TYPE_OPENGOODSDETAIL.equals(pushEntity.page)) {
            if (!TextUtils.isEmpty(pushEntity.param.item_source) && !TextUtils.isEmpty(pushEntity.param.id)) {
                GoodsDetailActivity.start(this, pushEntity.param.id, pushEntity.param.item_source, "");
            }
        } else if (PushUtil.TYPE_OPENCATEGORY.equals(pushEntity.page)) {
            startForPage(mContext, 1);
        } else if (PushUtil.TYPE_OPENUSER.equals(pushEntity.page)) {
            startForPage(mContext, 4);
        } else if (PushUtil.TYPE_OPENVIPTAB.equals(pushEntity.page)) {
            if (UserClient.isLogin()) {
                startForPage(mContext, 2);
            } else {
                jumpToLoginPage("请先登录，然后打开会员页面", pushEntity);
            }
        } else if (PushUtil.TYPE_OPENCOMMUNITY.equals(pushEntity.page)) {
            if (UserClient.isLogin()) {
                startForPage(mContext, 3);
            } else {
                jumpToLoginPage("请先登录，然后打开发圈页面", pushEntity);
            }
        } else if (PushUtil.TYPE_OPENSELFORDER.equals(pushEntity.page)) {
            if (UserClient.isLogin()) {
                OrderActivity.start(this, 0, true);
            } else {
                jumpToLoginPage("请先登录，然后打开我的订单页面", pushEntity);
            }
        } else if (PushUtil.TYPE_OPENGROUPORDER.equals(pushEntity.page)) {
            if (UserClient.isLogin()) {
                OrderActivity.start(this, 0, false);
            } else {
                jumpToLoginPage("请先登录，然后打开粉丝订单页面", pushEntity);
            }

        } else if (PushUtil.TYPE_OPENMYCOLLECTION.equals(pushEntity.page)) {
            if (UserClient.isLogin()) {
                CollectionFrag.start(this);
            } else {
                jumpToLoginPage("请先登录，然后打开我的收藏页面", pushEntity);
            }

        } else if (PushUtil.TYPE_OPENMYTEAM.equals(pushEntity.page)) {
            if (UserClient.isLogin()) {
                MyTeamFragment.start(this);
            } else {
                jumpToLoginPage("请先登录，然后打开我的粉丝页面", pushEntity);
            }

        } else if (PushUtil.TYPE_OPENINCOMEREPORT.equals(pushEntity.page)) {
            if (UserClient.isLogin()) {
                CommonUtil.jumpToEarningPage(this);
            } else {
                jumpToLoginPage("请先登录，然后打开我的收益页面", pushEntity);
            }

        } else if (PushUtil.TYPE_OPENINVITATE.equals(pushEntity.page)) {
            if (UserClient.isLogin()) {
                InvitateFrag.start(this);
            } else {
                jumpToLoginPage("请先登录，然后打开我的邀请页面", pushEntity);
            }

        } else if (PushUtil.TYPE_OPENSELFBALANCE.equals(pushEntity.page)) {
            if (UserClient.isLogin()) {
                SelfBalanceFragment.start(this);
            } else {
                jumpToLoginPage("请先登录，然后打开我的余额页面", pushEntity);
            }

        } else if (PushUtil.TYPE_OPENBINDALIPAY.equals(pushEntity.page)) {
            if (UserClient.isLogin()) {
                BindAlipayFragment.start(this);
            } else {
                jumpToLoginPage("请先登录，然后绑定支付宝", pushEntity);
            }

        } else if (PushUtil.TYPE_OPENWITHDRAW.equals(pushEntity.page)) {
            if (UserClient.isLogin()) {
                WithdrawalFragment.start(this);
            } else {
                jumpToLoginPage("请先登录，然后打开提现页面", pushEntity);
            }
        } else if (PushUtil.TYPE_OPENACTIVITYDETAIL.equals(pushEntity.page)) {
            if (UserClient.isLogin()) {
                ActivityDetailFrag.start(this, "", pushEntity.param.code);
            } else {
                jumpToLoginPage("请先登录，然后打开提现页面", pushEntity);
            }
        }
    }

    private void jumpToLoginPage(String toast, PushEntity pushEntity) {
        ToastUtil.show(toast);
        LoginFragment.start(this, pushEntity);
    }


}
