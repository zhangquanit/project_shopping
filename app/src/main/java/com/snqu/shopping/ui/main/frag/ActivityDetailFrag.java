package com.snqu.shopping.ui.main.frag;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.goods.entity.PromotionLinkEntity;
import com.snqu.shopping.data.home.entity.ActivityDetailEntity;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.ui.goods.AliAuthActivity;
import com.snqu.shopping.ui.goods.util.JumpUtil;
import com.snqu.shopping.ui.goods.vm.GoodsViewModel;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import common.widget.OvalCoverBar;

/**
 * 活动页面
 *
 * @author 张全
 */
public class ActivityDetailFrag extends SimpleFrag {
    private LoadingStatusView loadingStatusView;
    ImageView imageView;
    OvalCoverBar ovalCoverBar;
    TextView tv_btn_enter;
    TextView tv_btn_copy;
    TextView tv_content;
    private HomeViewModel homeViewModel;
    private GoodsViewModel goodsViewModel;

    private static final String PARAM = "PARAM";
    private ActivityDetailEntity detailEntity;
    //    private AdConvertEntity adConvertEntity;
    private PromotionLinkEntity mPromotionLinkEntity;
    private String code;

    public static void start(Context ctx, String title, String code) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM, code);
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("", ActivityDetailFrag.class, bundle));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        addAction(Constant.Event.AUTH_SUCCESS);
        StatusBar.setStatusBar(mContext, true, getTitleBar());
        code = getArguments().getString(PARAM);
        initView();
        initData();
    }

    private void initView() {
        loadingStatusView = findViewById(R.id.loadingBar);
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        imageView = findViewById(R.id.iv);
        ovalCoverBar = findViewById(R.id.coverBar);
        tv_btn_enter = findViewById(R.id.btn_enter);
        tv_btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detailEntity != null) {
                    AdvertistEntity advertistEntity = new AdvertistEntity();
                    advertistEntity.platform = detailEntity.platform;
                    advertistEntity.needLogin = 1;
                    if ("C".equals(detailEntity.platform) || "B".equals(detailEntity.platform)) {
                        advertistEntity.needAuth = 1;
                    } else {
                        advertistEntity.needAuth = 0;
                    }
                    advertistEntity.direct_protocal = detailEntity.direct_protocal;
                    advertistEntity.open_third_app = detailEntity.open_third_app;
                    advertistEntity.direct = detailEntity.direct;
                    advertistEntity.link_type = detailEntity.link_type;
                    advertistEntity.link_url = detailEntity.link_url;
                    advertistEntity.tid = detailEntity.tid;
                    advertistEntity.item_source = detailEntity.platform;
                    CommonUtil.startWebFrag(getActivity(), advertistEntity);
                }
            }
        });
        tv_btn_copy = findViewById(R.id.btn_copy);
        tv_btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("C".equals(detailEntity.platform) || "B".equals(detailEntity.platform)) {
                    if (UserClient.getUser().has_bind_tb == 0) {
                        if (mPromotionLinkEntity != null && !TextUtils.isEmpty(mPromotionLinkEntity.getAuth_url())) {
                            AliAuthActivity.start(getActivity(),
                                    mPromotionLinkEntity.getAuth_url());
                        } else {
                            goodsViewModel.doPromotionLink(detailEntity.link_type, detailEntity.tid, detailEntity.platform, detailEntity.link_url, "1");
                        }
                    } else {
                        copyCode();
                    }
                } else {
                    copyCode();
                }
            }
        });

        tv_content =

                findViewById(R.id.tv_content);

    }

    private void copyCode() {
        String content = tv_content.getText().toString();
        try {
            ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText(null, content);
            clipboardManager.setPrimaryClip(clipData);
            ToastUtil.show("复制成功");
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("复制失败");
        }
    }

    private void initData() {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        goodsViewModel = ViewModelProviders.of(this).get(GoodsViewModel.class);
        goodsViewModel.getDataResult().observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, ApiHost.PROMOTION_LINK)) {
                    if (netReqResult.successful && null != netReqResult.data) {
                        loadingStatusView.setVisibility(View.GONE);
                        mPromotionLinkEntity = (PromotionLinkEntity) netReqResult.data;
                        if(mPromotionLinkEntity.getAuth_url()!=null&&detailEntity.platform.equals(Constant.BusinessType.PDD)){
                            JumpUtil.authPdd(getActivity(),
                                    mPromotionLinkEntity.getAuth_url());
                            finish();
                        }else {
                            updateUI();
                        }
                    } else {
                        mPromotionLinkEntity = (PromotionLinkEntity) netReqResult.data;
                        if (mPromotionLinkEntity != null) {
                            if (detailEntity.platform.equals("C") || detailEntity.platform.equals("B")) {
                                if (mPromotionLinkEntity.getAuth_url() != null) {
                                    AliAuthActivity.start(getActivity(),
                                            mPromotionLinkEntity.getAuth_url());
                                }
                            } else if (detailEntity.platform.equals(Constant.BusinessType.PDD)) {
                                JumpUtil.authPdd(getActivity(),
                                        mPromotionLinkEntity.getAuth_url());
                                finish();
                            }
                        } else {
                            loadingStatusView.setStatus(LoadingStatusView.Status.FAIL);
                            ToastUtil.show(netReqResult.message);
                        }
                    }
                }
            }
        });
        homeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, HomeViewModel.TAG_ACTIVITY_DETAIL)) {
                    if (netReqResult.successful && null != netReqResult.data) {
                        detailEntity = (ActivityDetailEntity) netReqResult.data;
                        goodsViewModel.doPromotionLink(detailEntity.link_type, detailEntity.tid, detailEntity.platform, detailEntity.link_url, "1");
//                        homeViewModel.adConvertUrl(detailEntity.link, detailEntity.platform);
                    } else {
                        loadingStatusView.setStatus(LoadingStatusView.Status.FAIL);
                    }
                }
            }
        });

        getData();
    }

    private void getData() {
        loadingStatusView.setStatus(LoadingStatusView.Status.LOADING);
        if (null == detailEntity) {
            homeViewModel.getActivityDetail(code);
        } else {
            goodsViewModel.doPromotionLink(detailEntity.link_type, detailEntity.tid, detailEntity.platform, detailEntity.link_url, "1");
        }
    }

    private void updateUI() {

        getTitleBar().setTitleText(detailEntity.name);
        //背景图片
        imageView.setImageResource(R.drawable.icon_max_default_pic);
        GlideUtil.loadBitmap(mContext, detailEntity.style.bgImage_url, new BitmapImageViewTarget(imageView) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                try {
                    Bitmap bitmap = resource;
                    int vw = LContext.screenWidth;
                    int vh = (int) (bitmap.getHeight() * vw * 1.0f / (bitmap.getWidth() * 1.0f));

                    ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                    if (layoutParams.height != vh) {
                        layoutParams.height = vh;
                        imageView.setLayoutParams(layoutParams);
                    }
                    imageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onResourceReady(resource, transition);
            }
        });
//        GlideUtil.loadPic(imageView, detailEntity.style.bgImage_url, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);

        //背景颜色
        findViewById(R.id.container).setBackgroundColor(Color.parseColor(detailEntity.style.bgcolor));
        ovalCoverBar.setColor(detailEntity.style.bgcolor);
        ovalCoverBar.setVisibility(View.VISIBLE);

        //按钮
        List<ActivityDetailEntity.ActivityButtonBean> buttons = detailEntity.button;
        if (null != buttons && !buttons.isEmpty()) {
            for (ActivityDetailEntity.ActivityButtonBean button : buttons) {

                Drawable bgDrawable = getResources().getDrawable(R.drawable.btn_activity_detail);
                int textColor = Color.parseColor("#EF362E");

                //按钮背景
                try {
                    if (!TextUtils.isEmpty(button.bgColor)) {
                        GradientDrawable gradientDrawable = new GradientDrawable();
                        gradientDrawable.setCornerRadius(DeviceUtil.dip2px(mContext, 23));
                        gradientDrawable.setColor(Color.parseColor(button.bgColor));
                        bgDrawable = gradientDrawable;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //按钮文字颜色
                try {
                    if (!TextUtils.isEmpty(button.color)) {
                        textColor = Color.parseColor(button.color);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (button.type == 2) {
                    tv_btn_enter.setVisibility(View.VISIBLE);
                    tv_btn_enter.setText(button.name);
                    tv_btn_enter.setTextColor(textColor);
                    tv_btn_enter.setBackground(bgDrawable);
                } else if (button.type == 1) {
                    tv_btn_copy.setVisibility(View.VISIBLE);
                    tv_btn_copy.setText(button.name);
                    tv_btn_copy.setTextColor(textColor);
                    tv_btn_copy.setBackground(bgDrawable);
                }
            }

            //内容
            if (tv_btn_copy.getVisibility() == View.VISIBLE) {
                tv_content.setVisibility(View.VISIBLE);
                if (TextUtils.equals(detailEntity.platform, Constant.BusinessType.TB) || TextUtils.equals(detailEntity.platform, Constant.BusinessType.TM)) {
                    if (!(TextUtils.isEmpty(mPromotionLinkEntity.getCode()))) {
                        tv_content.setText(detailEntity.content + "\n\n" + "复制口令进【掏宝】抢购：" + mPromotionLinkEntity.getCode());
                    } else {
                        tv_content.setText(detailEntity.content);
                    }
                } else {
                    tv_content.setText(detailEntity.content + "\n\n" + "抢购地址：" + mPromotionLinkEntity.getClick_url());
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        if (TextUtils.equals(event.getAction(), Constant.Event.AUTH_SUCCESS)) {
            getData();
        }
    }


}
