package com.snqu.shopping.ui.main.frag.community;

import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.LazyFragment;
import com.blankj.utilcode.util.ImageUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.google.android.material.tabs.TabLayout;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.App;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.BottomInDialog;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.BaseResponseObserver;
import com.snqu.shopping.data.base.HttpResponseException;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.goods.entity.PromotionLinkEntity;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.data.home.entity.CommunityEntity;
import com.snqu.shopping.data.home.entity.PlateEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.ui.goods.AliAuthActivity;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.goods.fragment.ShareFragment;
import com.snqu.shopping.ui.goods.util.JumpUtil;
import com.snqu.shopping.ui.goods.vm.GoodsViewModel;
import com.snqu.shopping.ui.main.adapter.CommunityListAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.ui.order.util.ImgUtils;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.FileDownloader;
import com.snqu.shopping.util.IntentUtils;
import com.snqu.shopping.util.RecycleViewScrollToTop;
import com.snqu.shopping.util.ShareManagerUtil;
import com.snqu.shopping.util.ShareUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import common.widget.dialog.EffectDialogBuilder;
import common.widget.dialog.loading.LoadingDialog;
import common.widget.viewpager.indicator.TabIndicator;
import common.widget.viewpager.indicator.TitleIndicator;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * @author 张全
 */
public class CommunityListFrag extends LazyFragment {

    private static final String PARAM = "PLATE";
    private SmartRefreshLayout refreshLayout;
    protected GoodsQueryParam queryParam = new GoodsQueryParam();
    public MutableLiveData<NetReqResult> liveData = new MutableLiveData<>();//刷新全部
    private LoadingStatusView loadingStatusView;
    private CommunityShareView good_share_view;
    private PlateEntity plateEntity;
    private BaseQuickAdapter adapter;
    protected HomeViewModel mHomeViewModel;
    private GoodsViewModel mGoodViewModel;
    private TabLayout tabIndicator_two;
    private String itemSource = "";
    private boolean clickCopyBtn = false;
    private int cPos = -1;
    ExecutorService service = Executors.newCachedThreadPool();


    private List<View> lines = new ArrayList<>();
    private List<TextView> textViews = new ArrayList<>();


    public static Bundle getParam(PlateEntity plateEntity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM, plateEntity);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.community_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        addAction(Constant.Event.AUTH_SUCCESS);
        plateEntity = (PlateEntity) getArguments().getSerializable(PARAM);
        setPlateId();
        initView();
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        if (TextUtils.equals(event.getAction(), Constant.Event.AUTH_SUCCESS)) {
            if (!TextUtils.isEmpty(authId)) {
                authId = null;
            }
        }
    }

    protected void initTab() {
        lines = new ArrayList<>();
        textViews = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        TabIndicator tabIndicator = findViewById(R.id.child_tabs);
        tabIndicator_two = findViewById(R.id.child_tabs_child);
        int d20 = DeviceUtil.dip2px(mContext, 40);
        tabIndicator.setInterval(d20);
        tabIndicator.setTitleInidcator(new TitleIndicator() {
            @Override
            public View addTab(int index, CharSequence title) {
                View view = inflater.inflate(R.layout.community_tab_title_layout, null);
                TextView textView = view.findViewById(R.id.tv_title);
                textView.setText(title);
                View line = view.findViewById(R.id.line);
                lines.add(line);
                textViews.add(textView);
                if (index == 0) {
                    textView.setSelected(true);
                    line.setVisibility(View.VISIBLE);
                }
                return view;
            }
        });
        tabIndicator.setOnTabSelectedListener(new TabIndicator.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int pos) {
                for (View view : lines) {
                    view.setVisibility(View.INVISIBLE);
                }

                for (TextView view : textViews) {
                    view.setSelected(false);
                }
                cPos = pos;
                lines.get(pos).setVisibility(View.VISIBLE);
                textViews.get(pos).setSelected(true);
                tabIndicator_two.setVisibility(View.GONE);
                if (plateEntity.categories_list != null && plateEntity.categories_list.get(pos).categories_list != null &&
                        plateEntity.categories_list.get(pos).categories_list.size() > 0) {
                    setPlateChildTabs(pos);
                } else {
                    //刷新数据
//                    if (plateEntity.categories_list.get(pos).categories_list != null && plateEntity.categories_list.get(pos).categories_list.size() > 0) {
//                        queryParam.plate = plateEntity.categories_list.get(pos).categories_list.get(0)._id;
//                    }else{
                    queryParam.plate = plateEntity.categories_list.get(pos)._id;
//                    }
                    loadData();
                }
            }
        });
        tabIndicator_two.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (cPos != -1) {
                    int pos = tab.getPosition();
                    //刷新数据
                    queryParam.plate = plateEntity.categories_list.get(cPos).categories_list.get(pos)._id;
                    loadData();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (null != plateEntity.categories_list && !plateEntity.categories_list.isEmpty()) {
            tabIndicator.setVisibility(View.VISIBLE);
            findViewById(R.id.tab_divider).setVisibility(View.VISIBLE);
            List<String> subTabs = new ArrayList<>();
            for (PlateEntity item : plateEntity.categories_list) {
                subTabs.add(item.getSubTitle());
            }
            setPlateChildTabs(0);
            tabIndicator.setData(subTabs);
            if (tabIndicator_two.getTabCount() > 0) {
                Objects.requireNonNull(tabIndicator_two.getTabAt(0)).select();
            }
        } else {
            tabIndicator.setVisibility(View.GONE);
            findViewById(R.id.tab_divider).setVisibility(View.GONE);
        }
    }

    public BaseQuickAdapter getAdapter() {
        return new CommunityListAdapter(this);
    }

    public LoadMoreView getLoadMoreView() {
        return new CommonLoadingMoreView();
    }

    private void initView() {

        initTab();

        good_share_view = findViewById(R.id.good_share_view);
        refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData();
            }
        });
        RecyclerView recyclerView = findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));


        adapter = getAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                CommunityEntity communityEntity = (CommunityEntity) adapter.getData().get(position);
                switch (view.getId()) {
                    case R.id.item_content:
                        pasteContent(communityEntity);
                        break;
                }
                return true;
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                CommunityEntity communityEntity = (CommunityEntity) adapter.getData().get(position);
                itemSource = communityEntity.item_source;
                GoodsEntity goodsEntity = communityEntity.getGoods();
                switch (view.getId()) {
                    case R.id.item_download: //下载
//                        if (getCouponPrice(goodsEntity)) { //优惠券过期
//                            ToastUtil.show("宝贝已抢完");
//                        } else {
                        clickCopyBtn = false;
                        downloadPics(communityEntity);
//                        }
                        break;
                    case R.id.goods_detail:
//                        if (getCouponPrice(goodsEntity)) { //优惠券过期
//                            ToastUtil.show("宝贝已抢完");
//                        } else {  //商品详情
                        clickCopyBtn = false;
                        GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), goodsEntity);
//                        }
                        break;
                    case R.id.item_copy:
//                        if (getCouponPrice(goodsEntity)) { //优惠券过期惠券过期
//                            ToastUtil.show("宝贝已抢完");
//                        } else {
                        showLoading("复制口令生成中");
                        if (TextUtils.isEmpty(communityEntity.goods_id) || TextUtils.isEmpty(communityEntity.item_source)) {
                            ToastUtil.show("复制失败");
                        } else {
                            itemSource = communityEntity.item_source;
                            clickCopyBtn = true;
                            mGoodViewModel.doPromotionLink(GoodsDetailActivity.LINK_TYPE, communityEntity.goods_id, communityEntity.item_source, "", "1");

                        }
//                        }
                        break;
                }
            }
        });

        adapter.setLoadMoreView(getLoadMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, recyclerView);

        loadingStatusView = new LoadingStatusView(mContext);
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        adapter.setEmptyView(loadingStatusView);


    }

    private void setPlateChildTabs(int pos) {
        tabIndicator_two.setVisibility(View.GONE);
        if (plateEntity.categories_list.get(pos).categories_list != null &&
                plateEntity.categories_list.get(pos).categories_list.size() > 0) {
            tabIndicator_two.removeAllTabs();
            tabIndicator_two.setVisibility(View.VISIBLE);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            for (PlateEntity item_two : plateEntity.categories_list.get(pos).categories_list) {
                TabLayout.Tab tab = tabIndicator_two.newTab();
                LinearLayout tabView = tab.view;
                if (tabView != null) {
                    tabView.setBackground(null);
                    tabView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparent));
                }
                TextView view = (TextView) inflater.inflate(R.layout.community_label_item, null);
                view.setText(item_two.getSubTitle());
                tab.setCustomView(view);
                tabIndicator_two.addTab(tab.setText(item_two.getSubTitle()));
                cPos = pos;
            }
        }
    }

    public boolean getCouponPrice(GoodsEntity goodsEntity) {
        if (goodsEntity != null) {
            if (TextUtils.equals(goodsEntity.getItem_source(), "V")) {
                return false;
            }
            if (TextUtils.isEmpty(goodsEntity.getCouponPrice())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转发
     *
     * @param communityEntity
     */
    public void onCountBarClick(CommunityEntity communityEntity) {
        clickCopyBtn = false;
        GoodsEntity goodsEntity = communityEntity.getGoods();
//        if (getCouponPrice(goodsEntity)) { //优惠券过期
//            ToastUtil.show("宝贝已抢完");
//        } else {
        mHomeViewModel.clickCommunity(communityEntity._id);
        //复制文案
        try {
            CommonUtil.addToClipboard(communityEntity.content);
            ToastUtil.show("分享文案已复制到剪贴板");
        } catch (Exception e) {
            e.printStackTrace();
        }
        share(communityEntity);
//        }
    }

    private void pasteContent(CommunityEntity communityEntity) {
        try {
            ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(null, communityEntity.content);
            clipboardManager.setPrimaryClip(clipData);
            ToastUtil.show("文案已复制");
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("复制失败");
        }
    }

    private void initData() {
        mGoodViewModel =
                ViewModelProviders.of(this).get(GoodsViewModel.class);
        MutableLiveData<NetReqResult> dataResult = mGoodViewModel.getDataResult();
        dataResult.removeObservers(getLifecycleOwner());
        dataResult.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case ApiHost.PROMOTION_LINK:
                        dissmissDialog();

                        if (clickCopyBtn) {
                            if (netReqResult.successful && netReqResult.data != null) {
                                PromotionLinkEntity mPromotionLinkEntity = (PromotionLinkEntity) netReqResult.data;
                                if (mPromotionLinkEntity != null && !TextUtils.isEmpty(mPromotionLinkEntity.getShare_code())) {
                                    try {
                                        CommonUtil.addToClipboard(mPromotionLinkEntity.getShare_code());
                                        ToastUtil.show("复制成功");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        ToastUtil.show(netReqResult.message);
                                    }
                                } else {
                                    //未授权
                                    if (itemSource.equals("C") || itemSource.equals("B")) {
                                        if (mPromotionLinkEntity.getAuth_url() != null) {
                                            AliAuthActivity.start(getActivity(),
                                                    mPromotionLinkEntity.getAuth_url());
                                        }
                                    } else if (itemSource.equals(Constant.BusinessType.PDD)) {
                                        if (mPromotionLinkEntity != null) {
                                            if (mPromotionLinkEntity.getAuth_url() != null) {
                                                JumpUtil.authPdd(getActivity(),
                                                        mPromotionLinkEntity.getAuth_url());
                                            }
                                        }
                                    } else {
                                        if (itemSource.equals(Constant.BusinessType.PDD) && mPromotionLinkEntity.getAuth_url() != null) {
                                            JumpUtil.authPdd(getActivity(),mPromotionLinkEntity.getAuth_url());
                                        } else {
                                            ToastUtil.show(netReqResult.message);
                                        }
                                    }
                                }
                            } else {
                                ToastUtil.show(netReqResult.message);
                            }
                        } else {
                            // 下载发圈
                            if (netReqResult.successful) {
                                PromotionLinkEntity data = (PromotionLinkEntity) netReqResult.data;
                                String url = data.getClick_url();
                                if (TextUtils.isEmpty(url)) {
                                    url = data.getClick_url() != null ? data.getClick_url() : data.getItem_url();
                                }
                                shareGoodsBitmap(url);
                            } else {
                                dissmissDialog();
                                if (downloading) {
                                    showDownloadingDialog(communityEntity, null, null);
                                }
                            }
                        }

                        clickCopyBtn = false;

                        break;
                }
            }
        });


        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        liveData.removeObservers(getLifecycleOwner());
        liveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                refreshLayout.finishRefresh(netReqResult.successful);
                if (netReqResult.successful) {
                    ResponseDataArray<CommunityEntity> goodsData = (ResponseDataArray<CommunityEntity>) netReqResult.data;
                    if (queryParam.page == 1) {
                        adapter.setNewData(goodsData.getDataList());
                    } else if (!goodsData.getDataList().isEmpty()) {
                        adapter.addData(goodsData.getDataList());
                    }

                    if (goodsData.hasMore()) {
                        queryParam.page++;
                        adapter.loadMoreComplete(); //刷新成功
                    } else {
                        adapter.loadMoreEnd();//无下一页
                    }

                    if (queryParam.page == 1 && goodsData.getDataList().isEmpty()) { //第一页 无数据
                        LoadingStatusView.Status status = LoadingStatusView.Status.EMPTY;
                        loadingStatusView.setStatus(status);
                        loadingStatusView.setText("换个分类看看吧~");
                    }
                } else {
                    if (queryParam.page > 1) { //加载下一页数据失败
                        adapter.loadMoreFail();
                    } else if (adapter.getData().isEmpty()) { //第一页  无数据
                        LoadingStatusView.Status status = LoadingStatusView.Status.FAIL;
                        loadingStatusView.setStatus(status);
                    } else { //下拉刷新失败
                        ToastUtil.show(netReqResult.message);
                    }
                }
            }
        });
    }

    public void setData(PlateEntity plateEntity) {
        this.plateEntity = plateEntity;
        setPlateId();
    }

    private void setPlateId() {
        if (null == plateEntity) return;
        queryParam.plate = plateEntity._id;
        if (null != plateEntity.categories_list && !plateEntity.categories_list.isEmpty()) {
            queryParam.plate = plateEntity.categories_list.get(0)._id;
            if (plateEntity.categories_list.get(0).categories_list != null && plateEntity.categories_list.get(0).categories_list.size() > 0) {
                queryParam.plate = plateEntity.categories_list.get(0).categories_list.get(0)._id;
            }
        }
    }


    public void loadData() {
        queryParam.page = 1;
        loadMore();
    }

    public void loadMore() {
        mHomeViewModel.getCommunityList(queryParam, liveData);
    }

    @Override
    public void onFirstInit() {
        queryParam.page = 1;
        loadData();
    }

    @Override
    public void onLazyResume() {

    }

    @Override
    public void onLazyPause() {

    }

    private void showLoading() {
        loadingDialog = LoadingDialog.showDialog(mContext, "分享图片加载中...");
    }

    private void showLoading(String text) {
        loadingDialog = LoadingDialog.showDialog(mContext, text);
    }

    private void dissmissDialog() {
        if (null != loadingDialog)
            loadingDialog.dismiss();
    }

    private void showFail() {
        ToastUtil.show("下载失败，请重试");
        dissmissDialog();
    }


    //---------------------------------分享
    private void share(CommunityEntity communityEntity) {
        downloading = false;
        if (communityEntity.hasVideos()) { //有视频 ，系统分享界面 只分享一个视频
            showLoading();
            String videoUrl = communityEntity.videos.get(0);
            new FileDownloader(mContext)
                    .downloadFile(videoUrl, new FileDownloader.DownloadCallback() {
                        @Override
                        public void success(File file, String url) {
                            dissmissDialog();
                            IntentUtils.shareVideo(mContext, file, "分享视频");
                        }

                        @Override
                        public void fail(String url) {
                            showFail();
                        }
                    });
            return;
        }

        hasAddGoodBitmap = false;
        this.communityEntity = communityEntity;
        showLoading();
        if (null != communityEntity.getGoods()) { //有商品，先生成商品图片，再下载图片分享
            createGoodBitmap(communityEntity);
        } else { //只有图片
            shareMutiImages(communityEntity, null);
        }
    }


    //-----------------------下载文件
    CommunityDownloadDialogView dialogView;
    private boolean downloading;

    private void downloadPics(CommunityEntity communityEntity) {
        downloading = true;
        if (null != communityEntity.getGoods()) { //有商品 先下载商品
            showLoading();
            createGoodBitmap(communityEntity);
        } else {
            showDownloadingDialog(communityEntity, null, null);
        }

    }

    private void showDownloadingDialog(CommunityEntity communityEntity, Bitmap goodBitmap, String title) {
        dissmissDialog();
        if (!communityEntity.hasImgs() && !communityEntity.hasVideos() && null == goodBitmap) {
            showFail();
            return;
        }

        if (null != goodBitmap) {
            ImgUtils.saveImageToGalleryCheckExist(getActivity(), goodBitmap, communityEntity.getGoods().getItem_image());
        }
        if (null != goodBitmap && !communityEntity.hasImgs() && !communityEntity.hasVideos()) { //只有商品  下载商品完成
            ToastUtil.show("图片下载完成，请到相册查看");
            return;
        }
        if (null == getActivity() || getActivity().isFinishing()) return;
        if (null != dialogView) {
            dialogView.dismiss();
        }
        dialogView = new CommunityDownloadDialogView(mContext, communityEntity, title);
        new EffectDialogBuilder(mContext)
                .setContentView(dialogView)
                .setCancelable(true)
                .show();
    }

    //----------------分享
    private int progress = 0;
    private int total = 0;

    /**
     * 1、沒有图片+商品
     * 2、只有图片
     * 3、只有商品
     * 4、图片+商品
     *
     * @param communityEntity
     * @param share_media
     */

    /**
     * 分享多张图片
     */
    private void shareMutiImages(CommunityEntity communityEntity, Bitmap
            goodBitmap) {
        List<String> imagesList = communityEntity.getImages();
        if (null == goodBitmap && !communityEntity.hasImgs()) {
            showFail();
            return;
        }

        //只有商品
        if (null != goodBitmap && !communityEntity.hasImgs()) {
            showShareDialog(null, goodBitmap);
            return;
        }

        if (imagesList.size() >= 9 && null != goodBitmap) { //有商品图片 并且图片>9张
            imagesList = imagesList.subList(0, 8);
        }

        progress = 0;
        total = imagesList.size();
        List<File> files = new ArrayList<>();
        FileDownloader fileDownloader = new FileDownloader(mContext);
        fileDownloader.downloadFile(imagesList, new FileDownloader.DownloadCallback() {
            @Override
            public void success(File file, String url) {

                service.execute(() -> {
                    if (file != null && file.exists()) {
                        String name = Constant.water_name;
                        if (!TextUtils.isEmpty(name)) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath()).copy(Bitmap.Config.ARGB_8888, true);
                            Bitmap newBitmap = ShareFragment.setWaterMark(App.mApp.getResources(), bitmap, name);
                            String path = file.getPath();
                            if (newBitmap != null) {
                                file.delete();
                                File newFile = new File(path);
                                ImageUtils.save(newBitmap, newFile, Bitmap.CompressFormat.PNG);
                                files.add(newFile);
                            }
                        } else {
                            files.add(file);
                        }

                        findViewById(R.id.container)
                                .post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress++;
                                        if (progress == total) { //全部下载完成
                                            if (files.isEmpty()) {
                                                showFail();
                                                return;
                                            }
                                            showShareDialog(files, goodBitmap);
                                        }
                                    }
                                });

                    }
                });


            }

            @Override
            public void fail(String url) {
                progress++;
                if (progress == total) { //全部下载完成
                    if (files.isEmpty()) {
                        showFail();
                        return;
                    }
                    showShareDialog(files, goodBitmap);
                }
            }
        });
    }


    @SuppressLint("CheckResult")
    private void showShareDialog(List<File> files, Bitmap goodBitmap) {
        dissmissDialog();

        BottomInDialog bottomInDialog = new BottomInDialog(getContext());
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.community_share_dialog, null);

        RxView.clicks(dialogView.findViewById(R.id.ll_wx))
                .throttleFirst(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        shareToPlatform(files, goodBitmap, SHARE_MEDIA.WEIXIN);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

        RxView.clicks(dialogView.findViewById(R.id.ll_circle))
                .throttleFirst(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        shareToPlatform(files, goodBitmap, SHARE_MEDIA.WEIXIN_CIRCLE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

        RxView.clicks(dialogView.findViewById(R.id.ll_qq))
                .throttleFirst(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        shareToPlatform(files, goodBitmap, SHARE_MEDIA.QQ);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        RxView.clicks(dialogView.findViewById(R.id.ll_wb))
                .throttleFirst(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        shareToPlatform(files, goodBitmap, SHARE_MEDIA.SINA);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

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

    private boolean hasAddGoodBitmap;

    private void shareToPlatform(List<File> files, Bitmap goodBitmap, SHARE_MEDIA share_media) {

        if (share_media == SHARE_MEDIA.WEIXIN_CIRCLE) { //朋友圈
            showLoading();

            File file = null;
            if (null != goodBitmap) {
                file = ImgUtils.saveImageToGalleryCheckExist(getActivity(), goodBitmap, communityEntity.getGoods().getItem_image());
            } else {
                file = files.get(0);
            }
            if (null != file) {
                ShareManagerUtil.shareSingleFile(mContext, file, share_media);
            } else {
                ToastUtil.show("分享失败");
                dissmissDialog();
            }
        } else {
            if (null == files || files.isEmpty()) { //只有商品图
                File file = ImgUtils.saveImageToGalleryCheckExist(getActivity(), goodBitmap, communityEntity.getGoods().getItem_image());
                if (null != file) {
                    ShareManagerUtil.shareSingleFile(mContext, file, share_media);
                } else {
                    ToastUtil.show("分享失败");
                    dissmissDialog();
                }
            } else {
                if (null != goodBitmap && !hasAddGoodBitmap) { //商品分享图
                    File goodImgFile = ImgUtils.INSTANCE.saveImageRestoreToGallery(getActivity(), goodBitmap, communityEntity.getGoods().getItem_image());
                    if (null != goodImgFile)
                        files.add(goodImgFile);
                    hasAddGoodBitmap = true;
                }
                ShareUtil.shareFiles(getActivity(), files, share_media);
            }
        }
    }

    /**
     * 获取商品图片
     *
     * @return
     */
    private String authId;

    LoadingDialog loadingDialog;
    CommunityEntity communityEntity;

    @SuppressLint("CheckResult")
    private void createGoodBitmap(CommunityEntity communityEntity) {
        this.authId = null;
        this.communityEntity = communityEntity;
        GoodsEntity goodEntity = communityEntity.getGoods();
        String id = goodEntity.get_id();
        String item_source = goodEntity.getItem_source();
        UserEntity user = UserClient.getUser();
        if (user != null && TextUtils.equals(item_source, Constant.BusinessType.TB) || TextUtils.equals(item_source, Constant.BusinessType.TM)) { //淘宝、天猫
            if (user.has_bind_tb == 1) { //已授权
                mGoodViewModel.doPromotionLink(GoodsDetailActivity.LINK_TYPE, id, item_source, "", "1");
            } else { //获取授权url 进行授权
                HomeClient.getAuthUrl()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new BaseResponseObserver<ResponseDataObject<String>>() {
                            @Override
                            public void onSuccess(ResponseDataObject<String> value) {
                                if (!TextUtils.isEmpty(value.data)) {
                                    authId = id;
                                    AliAuthActivity.start(mContext, value.data);
                                } else {
                                    LogUtil.d("请求失败，请重试");
                                    if (downloading) {
                                        showDownloadingDialog(communityEntity, null, null);
                                    }
                                }
                            }

                            @Override
                            public void onError(HttpResponseException e) {
                                e.printStackTrace();
                                LogUtil.d("请求失败，请重试");
                                if (downloading) {
                                    showDownloadingDialog(communityEntity, null, null);
                                }
                            }

                            @Override
                            public void onEnd() {
                                dissmissDialog();
                            }
                        });
            }
        } else {
            mGoodViewModel.doPromotionLink(GoodsDetailActivity.LINK_TYPE, id, item_source, "", "1");
        }
    }


    private void shareGoodsBitmap(String pwd) {
        good_share_view.setCallBack(new CommunityShareCallBack() {
            @Override
            public void loadComplete() {
                ViewGroup viewGroup = findViewById(R.id.scrollview);
                View view = viewGroup.getChildAt(0);
                Bitmap goodBitmap = ImageUtils.view2Bitmap(view);
                if (downloading) {
                    showDownloadingDialog(communityEntity, goodBitmap, null);
                } else {
                    shareMutiImages(communityEntity, goodBitmap);
                }

            }

            @Override
            public void loadFail() {
                if (downloading) {
                    showDownloadingDialog(communityEntity, null, null);
                } else {
                    showToastShort("图片加载失败请重试");
                    dissmissDialog();
                }
            }
        });
        good_share_view.setData(communityEntity.getGoods(), pwd);
    }


    @Override
    public void onResume() {
        super.onResume();
        dissmissDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        dissmissDialog();
    }

}
