package com.snqu.shopping.ui.mine.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.BottomInDialog;
import com.snqu.shopping.common.ui.SpacesItemDecoration;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.SharePosterEntity;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.ui.mine.adapter.SharePosterListAdapter;
import com.snqu.shopping.ui.order.util.ImgUtils;
import com.snqu.shopping.util.FileDownloader;
import com.snqu.shopping.util.ShareManagerUtil;
import com.snqu.shopping.util.statistics.SndoData;
import com.snqu.shopping.util.statistics.task.DayTaskReport;
import com.snqu.shopping.util.statistics.task.TaskInfo;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * 邀请分享
 *
 * @author 张全
 */
public class InvitateFrag extends SimpleFrag {

    @BindView(R.id.tv_code)
    TextView tv_code;
    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;
    private SharePosterListAdapter posterListAdapter;
    private String code;
    private final String url = "https://www.xinletao.vip/starDown.html";

    @BindView(R.id.item_img)
    ImageView item_img;
    @BindView(R.id.iv_qrcode)
    ImageView iv_qrcode;
    @BindView(R.id.item_code)
    TextView item_code;
    @BindView(R.id.item_content)
    ViewGroup item_content;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;

    HomeViewModel mHomeViewModel;
    private TaskInfo taskInfo;
    private static final String PARAM_TASKINFO = "PARAM_TASKINFO";

    public static void start(Context ctx) {
        SimpleFragAct.SimpleFragParam fragParam = new SimpleFragAct.SimpleFragParam("", InvitateFrag.class);
        fragParam.hideTitleBar(true);
        SimpleFragAct.start(ctx, fragParam);
    }

    public static void start(Context ctx, TaskInfo taskInfo) {
        if (null == taskInfo) {
            start(ctx);
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_TASKINFO, taskInfo);
        SimpleFragAct.SimpleFragParam fragParam = new SimpleFragAct.SimpleFragParam("", InvitateFrag.class, bundle);
        fragParam.hideTitleBar(true);
        SimpleFragAct.start(ctx, fragParam);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.invitate_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        UserClient.verifyInviter(getActivity());
        ButterKnife.bind(this, mView);
        StatusBar.setStatusBar(getActivity(), false);


        Bundle arguments = getArguments();
        if (null != arguments && arguments.containsKey(PARAM_TASKINFO)) {
            taskInfo = (TaskInfo) arguments.getSerializable(PARAM_TASKINFO);
        }

        code = UserClient.inviteCode();

        initView();
        initData();
    }

    private void initView() {

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mHomeViewModel.getInvitePosterImgs(code);
            }
        });
        tv_code.setText(code);

        posterListAdapter = new SharePosterListAdapter(code);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recycler_view.setLayoutManager(linearLayoutManager);

        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(DeviceUtil.dip2px(getContext(), 10), LinearLayoutManager.HORIZONTAL);
        recycler_view.addItemDecoration(spacesItemDecoration);
        recycler_view.setAdapter(posterListAdapter);

        posterListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                posterListAdapter.setChecked(position);
                posterListAdapter.notifyDataSetChanged();
            }
        });
        //邀请码
        item_code.setText("邀请口令：" + code);
    }

    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(HomeViewModel.TAG_INIVTE_POSTER_LIST, netReqResult.tag)) {
                    refreshLayout.finishRefresh(netReqResult.successful);
                    if (netReqResult.successful) {
                        List<SharePosterEntity> dataList = (List<SharePosterEntity>) netReqResult.data;
                        posterListAdapter.setNewData(dataList);
                    } else {
                    }
                }
            }
        });
        mHomeViewModel.getInvitePosterImgs(code);
    }

    @OnClick({R.id.btn_copy, R.id.btn_share, R.id.btn_share_url, R.id.btn_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                close();
                break;
            case R.id.btn_copy: //复制邀请码
                try {
                    ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText(null, code);
                    clipboardManager.setPrimaryClip(clipData);
                    ToastUtil.show("复制成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.show("复制失败");
                }
                break;
            case R.id.btn_share: //分享
                if (null == posterListAdapter.getData() || posterListAdapter.getData().isEmpty()) {
                    ToastUtil.show("海报加载中，请稍候");
                    return;
                }
                createBitmap();
                break;
            case R.id.btn_share_url: //邀请链接
                SndoData.event(SndoData.XLT_EVENT_APP_LINK_COPY);

                StringBuilder sb = new StringBuilder();
                sb.append("这里免费下载【星乐桃】APP").append("\n");
                sb.append("领大额内部优惠券").append("\n");
                sb.append("同时享最高90%自购分享返佣").append("\n").append("\n");
                sb.append("免费下载链接" + url).append("\n");
                sb.append("官方登录邀请码：" + code);

                try {
                    ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText(null, sb.toString());
                    clipboardManager.setPrimaryClip(clipData);
                    ToastUtil.show("复制成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.show("复制失败");
                }
                break;

        }
    }

    private void createBitmap() {
        showLoading();

        SharePosterEntity sharePosterEntity = posterListAdapter.getData().get(posterListAdapter.getCheckedPos());
        FileDownloader fileDownloader = new FileDownloader(mContext, false);

        StringBuffer url = new StringBuffer(sharePosterEntity.gen_image);
        url.append("&").append("pre=0")
                .append("&").append("set_user_nick=1")
                .append("&").append("user_id="+ UserClient.getUser()._id);
        LogUtil.e("url="+url);
        fileDownloader.downloadFile(url.toString(), new FileDownloader.DownloadCallback() {
            @Override
            public void success(File file, String url) {
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null == bitmap) {
                    createFail();
                    return;
                }
//                item_img.setImageBitmap(bitmap);
//                bitmap= ImageUtils.view2Bitmap(item_content);
//                if (null == bitmap) {
//                    createFail();
//                    return;
//                }

                dissmissLoading();
                showShareDialog(bitmap);
            }

            @Override
            public void fail(String url) {
                createFail();
            }
        });


//        Bitmap bp = null;
//        try {
//            View childView = recycler_view.getLayoutManager().findViewByPosition(posterListAdapter.getCheckedPos());
//            if (null == childView) return null;
//            childView = childView.findViewById(R.id.item_content);
//            childView.setDrawingCacheEnabled(true);
//            childView.buildDrawingCache();
//
//            bp = Bitmap.createBitmap(childView.getDrawingCache(), 0, 0, childView.getMeasuredWidth(),
//                    childView.getMeasuredHeight());
//
//            childView.setDrawingCacheEnabled(false);
//            childView.destroyDrawingCache();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return bp;
    }

    private void showShareDialog(Bitmap bitmap) {
        BottomInDialog bottomInDialog = new BottomInDialog(getContext());
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.inviate_share_dialog_item, null);
        dialogView.findViewById(R.id.ll_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAndReport(bitmap, SHARE_MEDIA.WEIXIN);
            }
        });
        dialogView.findViewById(R.id.ll_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAndReport(bitmap, SHARE_MEDIA.WEIXIN_CIRCLE);
            }
        });
        dialogView.findViewById(R.id.ll_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAndReport(bitmap, SHARE_MEDIA.QQ);
            }
        });
        dialogView.findViewById(R.id.ll_wb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAndReport(bitmap, SHARE_MEDIA.SINA);
            }
        });
        dialogView.findViewById(R.id.ll_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SndoData.event(SndoData.XLT_EVENT_APP_POSTER_SAVE_ALBUM);
                SharePosterEntity sharePosterEntity = posterListAdapter.getData().get(posterListAdapter.getCheckedPos());
                ImgUtils.INSTANCE.saveImageToGalleryCheckExist(getContext(), bitmap, sharePosterEntity._id);
                ToastUtil.show("图片已保存到本地相册");
            }
        });
        dialogView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomInDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomInDialog.dismiss();
            }
        });
        bottomInDialog.setCanceledOnTouchOutside(true);
        bottomInDialog.setContentView(dialogView);
        bottomInDialog.show();
    }

    LoadingDialog loadingDialog;

    private void showLoading() {
        loadingDialog = LoadingDialog.showDialog(getContext(), "图片正在生成，请稍等");
    }

    private void createFail() {
        ToastUtil.show("图片生成失败");
        dissmissLoading();
    }

    private void dissmissLoading() {
        if (null != loadingDialog) loadingDialog.dismiss();
    }

    private void shareAndReport(Bitmap bitmap, SHARE_MEDIA media) {
        SndoData.event(SndoData.XLT_EVENT_APP_POSTER_SHARE);
        shareResult = ShareManagerUtil.shareSingleImage(mContext, bitmap, media);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shareResult && null != taskInfo && TextUtils.equals(taskInfo.type, "2")) {
            DayTaskReport.shareReport(mContext, taskInfo);
        }
        shareResult = false;
    }

    private boolean shareResult;
}
