package com.snqu.shopping.ui.bringgood.frag;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.SPUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.anroid.base.BaseActivity;
import com.anroid.base.ui.StatusBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.BottomInDialog;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.bringgoods.BringGoodsItemBean;
import com.snqu.shopping.data.goods.entity.PromotionLinkEntity;
import com.snqu.shopping.ui.bringgood.adapter.BringVideoListAdapter;
import com.snqu.shopping.ui.bringgood.helper.OnViewPagerListener;
import com.snqu.shopping.ui.bringgood.helper.ViewPagerLayoutManager;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.goods.player.MyJzVideo;
import com.snqu.shopping.ui.goods.vm.GoodsViewModel;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.FileDownloader;
import com.snqu.shopping.util.ShareManagerUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.util.List;

import cn.jzvd.Jzvd;

/**
 * @author 张全
 */
public class BringVideoAct extends BaseActivity {
    BringVideoListAdapter adapter;
    private static List<BringGoodsItemBean> videoList;
    private RecyclerView recyclerView;
    private SmartRefreshLayout refreshLayout;
    private static int pos;
    private HomeViewModel homeViewModel;
    private GoodsViewModel goodsViewModel;
    private static int nextPage = -1;
    private static String cid;
    private int offsetCount = 3;
    private static boolean loadEnd;
    private BringGoodsItemBean itemBean;
    private PromotionLinkEntity promotionLinkEntity;
    private int lastPos;
    private String TAG = "Bring_video";
    private final String SP_GUIDE = "bring_v_guide";
    private View guideBar;

    public static void start(Context ctx, List<BringGoodsItemBean> videoList, String cid, int pos, int page, boolean loadEnd) {
        BringVideoAct.videoList = videoList;
        BringVideoAct.pos = pos;
        BringVideoAct.cid = cid;
        BringVideoAct.nextPage = page;
        BringVideoAct.loadEnd = loadEnd;
        Intent intent = new Intent(ctx, BringVideoAct.class);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Window window = getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
////        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
////        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(Color.TRANSPARENT);

        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {

        return R.layout.bring_video_act;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, false, R.color.transparent, null);
        parseClipboard = false;

        lastPos = pos;
        initView();
        initData();

    }

    private void initView() {


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        refreshLayout = findViewById(R.id.refreshLayout);
//        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//
//            }
//        });

        recyclerView = findViewById(R.id.recyclerView);
        ViewPagerLayoutManager linearLayoutManager = new ViewPagerLayoutManager(mContext, LinearLayoutManager.VERTICAL);
        linearLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {

            @Override
            public void onInitComplete() {

            }

            @Override
            public void onPageSelected(int position, boolean isBottom) {
                LogUtil.d(TAG, "onPageSelected pos=" + position + ",isBottom=" + isBottom + ",dataSize=" + adapter.getData().size());

                if (adapter.getData().size() - position < offsetCount && !loadEnd) {
                    loadData();
                }

                if (isBottom && lastPos == position) {
                    ToastUtil.show("到底啦");
                    return;
                }
                if (lastPos == position) { // 重复滑动
                    return;
                }
                //预加载
//                preLoad(position);
                lastPos = position;
                View child = linearLayoutManager.findViewByPosition(position);
                if (child != null) {
                    //视频开始播放
                    MyJzVideo jzVideo = child.findViewById(R.id.item_video_player);
                    jzVideo.startVideo();
                }


            }

            @Override
            public void onPageRelease(boolean isNext, int position) {

            }
        });

        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new BringVideoListAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setNewData(videoList);
        recyclerView.scrollToPosition(pos);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                itemBean = (BringGoodsItemBean) adapter.getData().get(position);
                switch (view.getId()) {
                    case R.id.item_goodbar: //商品详情
                        GoodsDetailActivity.start(mContext, itemBean.goods_info.get_id(), itemBean.goods_info.getItem_source(), itemBean.goods_info.getItem_id());
                        break;
                    case R.id.item_share: //分享
                        showLoadingDialog("请稍候...");
                        goodsViewModel.doPromotionLink(GoodsDetailActivity.LINK_TYPE,itemBean.goods_info.get_id(), itemBean.goods_info.getItem_source(),"","1");
                        break;
                    case R.id.item_copy: //复制
                        BottomInDialog bottomInDialog = new BottomInDialog(mContext);
                        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.bring_copy_dialog, null);
                        TextView tv_desc = dialogView.findViewById(R.id.desc);
                        tv_desc.setText(itemBean.item_desc);
                        dialogView.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomInDialog.dismiss();
                            }
                        });
                        dialogView.findViewById(R.id.tv_copy).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomInDialog.dismiss();
                                copyText(itemBean.item_desc, "复制成功");
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
                        break;
                }
            }
        });


        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Jzvd jzvd = view.findViewById(R.id.item_video_player);
                if (jzvd != null && Jzvd.CURRENT_JZVD != null &&
                        jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.getCurrentUrl())) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos();
                    }
                }
            }
        });


        //引导
        guideBar = findViewById(R.id.guidebar);
        boolean showedGuide = SPUtil.getBoolean(SP_GUIDE, false);
        if (!showedGuide) {
            guideBar.setVisibility(View.VISIBLE);
            View guide1 = findViewById(R.id.guide1);
            View guide2 = findViewById(R.id.guide2);
            guide1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guide1.setVisibility(View.GONE);
                    guide2.setVisibility(View.VISIBLE);
                }
            });
            guide2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guideBar.setVisibility(View.GONE);
                    SPUtil.setBoolean(SP_GUIDE,true);
                    View child = linearLayoutManager.findViewByPosition(pos);
                    if (child != null) {
                        //视频开始播放
                        MyJzVideo jzVideo = child.findViewById(R.id.item_video_player);
                        jzVideo.startVideo();
                    }
                }
            });
        } else {
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    View child = linearLayoutManager.findViewByPosition(pos);
                    if (child != null) {
                        //视频开始播放
                        MyJzVideo jzVideo = child.findViewById(R.id.item_video_player);
                        jzVideo.startVideo();
                    }
                }
            });
        }
    }

    private void initData() {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.mNetReqResultLiveData.observe(this, new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, ApiHost.DYDH_LIST)) {
                    isLoading = false;
                    if (netReqResult.successful) {
                        ResponseDataArray<BringGoodsItemBean> dataArray = (ResponseDataArray<BringGoodsItemBean>) netReqResult.data;
                        List<BringGoodsItemBean> dataList = dataArray.getDataList();
                        if (!dataList.isEmpty()) {
                            LogUtil.d(TAG, "加载数据完成 data.size=" + dataList.size() + ",page=" + nextPage);
                            adapter.addData(dataList);
                            nextPage++;
                        } else {
                            loadEnd = true;
                        }

                    }
                }
            }
        });

        goodsViewModel = ViewModelProviders.of(this).get(GoodsViewModel.class);
        goodsViewModel.getDataResult().observe(this, new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, ApiHost.PROMOTION_LINK)) {
                    closeLoadDialog();
                    if (netReqResult.successful) {
                        promotionLinkEntity = (PromotionLinkEntity) netReqResult.data;
                        showShareDialog();
                    } else {
                        ToastUtil.show("请求失败,请重试");
                    }

                }
            }
        });


        //点击列表最后一条数据
        if (!loadEnd && pos == videoList.size() - 1) {
            loadData();
        }
    }

    private boolean isLoading;

    private void loadData() {
        if (isLoading) {
            return;
        }
        LogUtil.d(TAG, "开始加载第" + nextPage + "页的数据");
        isLoading = true;
        homeViewModel.getDydhList(cid, nextPage);
    }


    private void copyText(String text, String toast) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        try {
            ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(null, text);
            clipboardManager.setPrimaryClip(clipData);
            ToastUtil.show(toast);
            CommonUtil.setClipboardText(clipData.toString());
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("复制失败");
        }

    }

    private void showShareDialog() {
        BottomInDialog bottomInDialog = new BottomInDialog(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.bring_share_dialog_item, null);
        dialogView.findViewById(R.id.ll_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAndShare(SHARE_MEDIA.WEIXIN);
            }
        });
        dialogView.findViewById(R.id.ll_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAndShare(SHARE_MEDIA.QQ);
            }
        });
        dialogView.findViewById(R.id.ll_wb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadAndShare(SHARE_MEDIA.SINA);
            }
        });
        dialogView.findViewById(R.id.ll_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVideo();
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

    private void downloadAndShare(SHARE_MEDIA share_media) {
        showLoadingDialog("请稍候...");
        new FileDownloader(mContext, false)
                .downloadFile(itemBean.dy_video_url, new FileDownloader.DownloadCallback() {
                    @Override
                    public void success(File file, String url) {
                        closeLoadDialog();
                        boolean shareResult = ShareManagerUtil.shareSingleFile(mContext, file, share_media);

                        if (!shareResult) {
                            ToastUtil.show("分享失败");
                        }

                        if (shareResult && null != promotionLinkEntity) {
                            copyText(promotionLinkEntity.getShare_code(), "商品口令已复制");
                        }
                    }

                    @Override
                    public void fail(String url) {
                        closeLoadDialog();
                        ToastUtil.show("下载失败,请重试");
                    }
                });

    }

    private void saveVideo() {
        showLoadingDialog("请稍候...");
        new FileDownloader(mContext, false)
                .downloadFile(itemBean.dy_video_url, new FileDownloader.DownloadCallback() {
                    @Override
                    public void success(File file, String url) {
                        closeLoadDialog();
                        CommonUtil.notifyFileToGallery(mContext, file);
                        if (null != promotionLinkEntity) {
                            copyText(promotionLinkEntity.getShare_code(), "保存成功,商品口令已复制");
                        }
                    }

                    @Override
                    public void fail(String url) {
                        closeLoadDialog();
                        ToastUtil.show("下载失败,请重试");
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (guideBar.getVisibility() != View.VISIBLE && null != Jzvd.CURRENT_JZVD && null != Jzvd.CURRENT_JZVD.mediaInterface) {
            Jzvd.CURRENT_JZVD.mediaInterface.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (guideBar.getVisibility() != View.VISIBLE && null != Jzvd.CURRENT_JZVD && null != Jzvd.CURRENT_JZVD.mediaInterface) {
            Jzvd.CURRENT_JZVD.mediaInterface.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Jzvd.releaseAllVideos();
    }

}
