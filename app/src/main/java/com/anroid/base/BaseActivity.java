package com.anroid.base;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.leak.InputMethodManagerLeakUtil;
import com.android.util.log.LogUtil;
import com.android.util.os.KeyboardUtils;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.ui.main.scan.ADDialogView;
import com.snqu.shopping.ui.main.scan.ClipboardDialogView;
import com.snqu.shopping.ui.main.scan.ClipboardSearchDialogView;
import com.snqu.shopping.ui.main.scan.ParseTextDialogView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.log.LogClient;
import com.snqu.shopping.util.statistics.SndoData;
import com.snqu.shopping.util.statistics.task.NewTaskType;
import com.snqu.shopping.util.statistics.task.TaskReport;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.magicwindow.Session;
import common.widget.dialog.EffectDialogBuilder;
import common.widget.dialog.loading.LoadingDialog;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * Activity基类
 *
 * @author zhangquan
 */
public abstract class BaseActivity extends SupportActivity {

    private static final String TAG = "BaseActivity";
    private String mPageName;
    private LoadingDialog mLoadingDialog;
    protected List<String> mActionList = new ArrayList<String>();
    private boolean mEventBusRegisted;
    protected Activity mContext;
    protected int INVALIDE_LAYOUT = -1;
    private boolean isForground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageName = this.getClass().getSimpleName();
        mContext = this;
        LogUtil.d(TAG, "onCreate " + mPageName + ",pid=" + Process.myPid());
        int layoutId = getLayoutId();
        if (layoutId == INVALIDE_LAYOUT) {
            finish();
            return;
        }
        try {
            setContentView(layoutId);
            init(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
            return;
        }
        registObserver();
        addClipboardListener();
//        SndoDataAPI.sharedInstance(getApplicationContext()).addHeatMapActivity(getClass());
//        PushAgent.getInstance(this).onAppStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart " + mPageName);
        isForground = true;
    }

    protected void onResume() {
        Session.onResume(this);
        super.onResume();
        LogUtil.d(TAG, "onResume " + mPageName);

        //解析剪贴板
        LogUtil.e(TAG, "parseClipboard=" + parseClipboard);
        LogClient.log(TAG, "onResume,parseClipboard=" + parseClipboard);
        if (parseClipboard) {
            post(new Runnable() {
                @Override
                public void run() {
                    parseClipboardText();
                }
            });

            //新人0元购
            if (UserClient.getUser() != null && !UserClient.hasShowNewUserGuide()) {
                homeViewModel.getAdList("10003");
            }
        }
    }

    protected void onPause() {
        Session.onPause(this);
        super.onPause();
        LogUtil.d(TAG, "onPause " + mPageName);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop " + mPageName);
        isForground = false;
    }

    @Override
    protected void onDestroy() {
        InputMethodManagerLeakUtil.fixInputMethodManagerLeak(this);
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy " + mPageName);
        closeLoadDialog();
//        ToastUtil.cancel();
//        fixInputMethodManagerLeak(this);
        if (!mActionList.isEmpty()) {
            EventBus.getDefault().unregister(this);
        }
        KeyboardUtils.hideSoftInput(this);
        removeClipboardListener();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.d(TAG, "onNewIntent " + mPageName);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        LogUtil.d(TAG, "onRestoreInstanceState " + mPageName);
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    protected String getPageName() {
        return getClass().getSimpleName();
    }

    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj_get = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                }
                obj_get = f.get(imm);
                if (obj_get != null && obj_get instanceof View) {
                    View v_get = (View) obj_get;
                    if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
                        f.set(imm, null); // 置空，破坏掉path to gc节点
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void showToastShort(String s) {
        if (isFinishing()) {
            return;
        }
        ToastUtil.show(s);
    }

    public void showToastShort(int resId) {
        if (isFinishing()) {
            return;
        }
        ToastUtil.show(resId);
    }

    public void showLoadingDialog(String content) {
        mLoadingDialog = LoadingDialog.showBackCancelableDialog(this, content);
    }


    public void closeLoadDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    protected void startAct(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(), cls);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startAct(intent);
    }

    protected void startAct(String action, Bundle bundle) {
        Intent intent = new Intent(action);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startAct(intent);
    }

    public void addAction(String action) {
        if (!mActionList.contains(action))
            mActionList.add(action);
        if (!mEventBusRegisted) {
            EventBus.getDefault().register(this);
        }
        mEventBusRegisted = true;
    }

    public boolean containsAction(String action) {
        return mActionList.contains(action);
    }

    protected void startAct(Intent intent) {
        startActivity(intent);
    }

    public abstract int getLayoutId();

    public abstract void init(Bundle savedInstanceState);


    //-------------------剪贴板------------
    private HomeViewModel homeViewModel;
    private MutableLiveData<NetReqResult> liveData = new MutableLiveData<>();
    public boolean parseClipboard = true;

    ClipboardManager.OnPrimaryClipChangedListener clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            if (!parseClipboard) {
                return;
            }
            CharSequence text = null;
            ClipboardManager cm = (ClipboardManager) getBaseContext().getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = cm.getPrimaryClip();

            if (clipData != null && clipData.getItemCount() > 0) {
                text = clipData.getItemAt(0).getText();
            }
            if (TextUtils.isEmpty(text)) {
                return;
            }
            LogUtil.e(TAG, "剪贴板监听 clipData=" + clipData + ",itemCount=" + clipData.getItemCount());
//            String clipboardText = CommonUtil.getClipboardText();
//            if (TextUtils.equals(clipboardText, text) && clipData.getItemCount() > 1) {
//                for (int i = 1; i < clipData.getItemCount(); i++) {
//                    ClipData.Item item = clipData.getItemAt(i);
//                    if (TextUtils.equals(item.getText(), "⭐")) {
//                        LogUtil.e(TAG, "剪贴板监听 后台 监听到标记的记录,直接返回");
//                        return;
//                    }
//                }
//            }
            if (isForground) {
                LogUtil.e(TAG, "剪贴板监听 前台 ");
//                CommonUtil.setClipboardText(text.toString());
                CommonUtil.setClipboardText(clipData.toString());
            } else {
                LogUtil.e(TAG, "剪贴板监听 后台 ");
            }
        }
    };


    private void addClipboardListener() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.addPrimaryClipChangedListener(clipChangedListener);
    }

    private void removeClipboardListener() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        cm.removePrimaryClipChangedListener(clipChangedListener);
    }

    protected String getSearchText() {
        ClipboardManager cm = (ClipboardManager) getBaseContext().getSystemService(CLIPBOARD_SERVICE);
        CharSequence text = null;
        ClipData clipData = cm.getPrimaryClip();
        LogUtil.e(TAG, "curClipData=" + clipData);
        LogClient.log(TAG, "getSearchText,curClipData=" + clipData);
        if (clipData != null && clipData.getItemCount() > 0) {
            text = clipData.getItemAt(0).getText();
        }
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        String curClipData = clipData.toString();


        //自己app内复制的淘口令
        String lastText = CommonUtil.getClipboardText();
        LogUtil.e(TAG, "lastText=" + lastText);
        LogClient.log(TAG, "getSearchText,lastText=" + lastText);
//        if (TextUtils.equals(lastText, text)) {
//            return null;
//        }
        if (TextUtils.equals(curClipData, lastText)) {
            LogClient.log(TAG, "剪贴板内容无变化");
            return null;
        }
        LogClient.log(TAG, "剪贴板内容有变化，开始调用接口");
        return text.toString();
    }

    private void parseClipboardText() {
        String value = getSearchText();
        if (TextUtils.isEmpty(value)) {
            return;
        }
        homeViewModel.decodeGoodByCode(value, 1, liveData, "0");
    }


    private void registObserver() {
        homeViewModel =
                ViewModelProviders.of(this).get("ClipData" + this, HomeViewModel.class);
        homeViewModel.mNetReqResultLiveData.observe(this, new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, HomeViewModel.TAG_AD)) {
                    if (netReqResult.successful && netReqResult.data != null) {
                        ResponseDataArray<AdvertistEntity> dataArray = (ResponseDataArray<AdvertistEntity>) netReqResult.data;
                        List<AdvertistEntity> adList = dataArray.getDataList();
                        showFreeAdDialog(adList);
                    }
                }
            }
        });

        liveData.observe(this, new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                String lastText = (String) netReqResult.extra;
                ClipboardManager cm = (ClipboardManager) LContext.getContext().getSystemService(CLIPBOARD_SERVICE);
                if (null != cm) {
                    ClipData clipData = cm.getPrimaryClip();
                    if (null != clipData) {
                        CommonUtil.setClipboardText(clipData.toString());
                    }
                }

                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_GOODS_DECODE_URL: //URL解析
                    case HomeViewModel.TAG_GOODS_DECODE_CODE: //淘口令
                        if (null != netReqResult.data) {
                            ResponseDataObject<GoodsEntity> goodsDecodeEntity = (ResponseDataObject<GoodsEntity>) netReqResult.data;
                            if (goodsDecodeEntity.code == 502) {
                                showScanUrlDialog(goodsDecodeEntity.message);
                                return;
                            }
                            GoodsEntity data = goodsDecodeEntity.data;
                            if (null == data) { //data返回为空
                                return;
                            }
                            if (!TextUtils.isEmpty(data.getGoods_id())) { //商品基础数据
                                showGoodDialog(data);
                                return;
                            }
                            if (TextUtils.equals(data.getNeed_search(), "1")) { //需要搜索
                                showSearchDialog(lastText, data);
                            }
                        }
                        break;

                }
            }
        });
    }

    protected void showScanUrlDialog(String text) {
        if (isFinishing()) return;
        ParseTextDialogView textDialogView = new ParseTextDialogView(this, text);
        new EffectDialogBuilder(this)
                .setContentView(textDialogView)
                .setCancelable(false)
                .setCancelableOnTouchOutside(false)
                .show();
    }

    protected void showSearchDialog(String text, GoodsEntity goodsEntity) {
        if (isFinishing()) return;
        ClipboardSearchDialogView textDialogView = new ClipboardSearchDialogView(this, text, goodsEntity.getItem_source());
        new EffectDialogBuilder(this)
                .setContentView(textDialogView)
                .setCancelable(false)
                .setCancelableOnTouchOutside(false)
                .show();

        SndoData.event(SndoData.XLT_EVENT_POPUP,
                "xlt_item_search_keyword", text,
                SndoData.XLT_GOOD_ID, goodsEntity.getGoods_id(),
                "xlt_item_firstcate_title", "null",
                "xlt_item_thirdcate_title", "null",
                "xlt_item_secondcate_title", "null",
                "good_name", goodsEntity.getItem_title()==null?"null":goodsEntity.getItem_title(),
                "paste_content", "null",
                SndoData.XLT_ITEM_SOURCE, goodsEntity.getItem_source()
        );

        //新手任务汇报
        TaskReport.newTaskReport(mContext, NewTaskType.CLIPBOARD);
    }

    protected void showGoodDialog(GoodsEntity goodsEntity) {
        CommonUtil.addToClipboard(null);
        if (isFinishing()) return;
        ClipboardDialogView clipboardDialogView = new ClipboardDialogView(this, goodsEntity);
        new EffectDialogBuilder(this)
                .setContentView(clipboardDialogView)
                .setCancelable(true)
                .setCancelableOnTouchOutside(false)
                .show();

        //新手任务汇报
        TaskReport.newTaskReport(mContext, NewTaskType.CLIPBOARD);


    }


    protected boolean showFreeAdDialog(List<AdvertistEntity> fredAdList) {
        if (null != UserClient.getUser() && (null == fredAdList || fredAdList.isEmpty())) {
            UserClient.setShowNewUserGuide();
            return false;
        }
        if (null != fredAdList && !fredAdList.isEmpty() && !UserClient.hasShowNewUserGuide()) {
            String url = fredAdList.get(0).image;
            if (TextUtils.isEmpty(url)) {
                return false;
            }
            GlideUtil.loadBitmap(mContext, url, new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                    if (null == bitmap || isFinishing()) return;
                    UserClient.setShowNewUserGuide();
                    ADDialogView tipDialogView = new ADDialogView(mContext);
                    tipDialogView.setAd(fredAdList, bitmap);
                    new EffectDialogBuilder(mContext)
                            .setContentView(tipDialogView)
                            .setCancelable(false)
                            .setCancelableOnTouchOutside(false)
                            .show();
                }
            });
            return true;
        }
        return false;
    }
}
