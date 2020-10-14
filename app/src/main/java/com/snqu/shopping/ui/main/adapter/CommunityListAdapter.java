package com.snqu.shopping.ui.main.adapter;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jakewharton.rxbinding2.view.RxView;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.CommunityEntity;
import com.snqu.shopping.ui.main.frag.community.CommunityListFrag;
import com.snqu.shopping.ui.main.frag.community.ZoomPicFrag;
import com.snqu.shopping.ui.video.PlayerVideoActivity;
import com.snqu.shopping.util.DateUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class CommunityListAdapter extends BaseQuickAdapter<CommunityEntity, BaseViewHolder> {
    final int maxLines = 8;
    private int imageW;
    private CommunityListFrag communityListFrag;
    private int textWidth, d5;

    public CommunityListAdapter(CommunityListFrag communityListFrag) {
        super(R.layout.community_list_item);
        this.communityListFrag = communityListFrag;
        imageW = Math.round((LContext.screenWidth - DeviceUtil.dip2px(LContext.getContext(), 30)) / 3.0f);
        textWidth = LContext.screenWidth - DeviceUtil.dip2px(LContext.getContext(), 20);
        d5 = DeviceUtil.dip2px(LContext.getContext(), 5);
    }


    @SuppressLint("CheckResult")
    @Override
    protected void convert(BaseViewHolder helper, CommunityEntity item) {

        if(TextUtils.isEmpty(item.flag_txt)){
            helper.getView(R.id.item_invalide).setVisibility(View.GONE);
        }else{
            helper.getView(R.id.item_invalide).setVisibility(View.VISIBLE);
            helper.setText(R.id.item_invalide,item.flag_txt);
        }

        GlideUtil.loadPic(helper.getView(R.id.item_img), item.avatar, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        helper.setText(R.id.item_recom, item.recom_title);

        if (item.start_time > 0) {
            helper.setText(R.id.item_time, DateUtil.getFriendlyTimeStr(item.start_time * 1000));
        }
        helper.setText(R.id.item_count, NumberUtil.INSTANCE.sellCount(item.counts));
        if (item.getGoods() != null) {
            helper.setText(R.id.label1, item.getGoods().getShare_code());
        }
        TextView textView = helper.getView(R.id.item_content);
        TextView item_switch = helper.getView(R.id.item_switch);
        textView.setMaxLines(maxLines);
        textView.setText(item.content);

        if (TextUtils.isEmpty(item.content)) {
            textView.setVisibility(View.GONE);
            helper.getView(R.id.item_view).setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
            helper.getView(R.id.item_view).setVisibility(View.GONE);
        }

        textView.post(new Runnable() {
            @Override
            public void run() {
                boolean b = getLastIndexForLimit(textView, item.content);
                if (b) {
                    item_switch.setVisibility(View.VISIBLE);
                    item_switch.setText("全文");
                } else {
                    item_switch.setVisibility(View.GONE);
                }
            }
        });

        item_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.getMaxLines() == maxLines) {
                    item_switch.setText("收起");
                    textView.setMaxLines(10000);
                } else {
                    item_switch.setText("全文");
                    textView.setMaxLines(maxLines);
                }
            }
        });

        RecyclerView picListView = helper.getView(R.id.item_pics);
        boolean hasVideo = item.hasVideos();
        if (!item.hasImgs() && !hasVideo && null == item.getGoods()) { //无图片/无视频/无商品
            picListView.setVisibility(View.GONE);
            helper.setGone(R.id.item_download, false);
            helper.setGone(R.id.item_countbar, false);
        } else {
            picListView.setVisibility(View.VISIBLE);
            helper.setGone(R.id.item_download, true);
            helper.setGone(R.id.item_countbar, true);


            List<String> itemList = item.getItemList();
            if (itemList == null) {
                return;
            }
            final int column = itemList.size();
            GridLayoutManager gridLayoutManager = null;
            int resId = 0;
            if (column == 1) {
                gridLayoutManager = new GridLayoutManager(mContext, 1, GridLayoutManager.VERTICAL, false);
                resId = R.layout.community_list_pic_item_one;
            } else if (column == 2 || column == 4) {
                gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
                resId = R.layout.community_list_pic_item_two;
            } else {
                gridLayoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
                resId = R.layout.community_list_pic_item_three;
            }

            picListView.setLayoutManager(gridLayoutManager);
            PicListAdapter picListAdapter = new PicListAdapter(hasVideo, resId);
            picListAdapter.setOnItemClickListener((adapter, view, position) -> {
                if (hasVideo) {
                    PlayerVideoActivity.Companion.start(view.getContext(), item.videos.get(position));
                } else {
                    ZoomPicFrag.start(view.getContext(), position, (ArrayList) item.images);
                }
            });
            picListView.setAdapter(picListAdapter);
            picListAdapter.setNewData(itemList);
        }


        GoodsEntity goodsEntity = item.getGoods();
        if (null != goodsEntity) {
            helper.setGone(R.id.item_goodbar, true);
            GlideUtil.loadPic(helper.getView(R.id.item_good_img), goodsEntity.getItem_image(), R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
            helper.setText(R.id.item_good_title, goodsEntity.getItem_title());
            //优惠券
            if (!TextUtils.isEmpty(goodsEntity.getCouponPrice())) {
                helper.setGone(R.id.item_fan, true);
                helper.setText(R.id.item_fan, "优惠券:¥" + goodsEntity.getCouponPrice());
                helper.setVisible(R.id.item_coupon, true);
                helper.setText(R.id.item_coupon, "券后价:¥" + goodsEntity.getNow_price());
                helper.setGone(R.id.item_old_price, false);
                helper.setGone(R.id.item_cover, false);
            } else {
                helper.setGone(R.id.item_fan, false);
                helper.setVisible(R.id.item_coupon, false);
                helper.setGone(R.id.item_old_price, true);
                helper.setText(R.id.item_old_price, "¥" + goodsEntity.getItemPrice());
                helper.setGone(R.id.item_cover, true);
            }


            //返利金
            String rebatePrice = goodsEntity.getRebatePrice();
            if (!TextUtils.isEmpty(rebatePrice)) {
                helper.setVisible(R.id.item_earn, true);
                helper.setText(R.id.item_earn, getRebatePriceText(rebatePrice));
            } else {
                helper.setVisible(R.id.item_earn, false);
            }

            helper.setText(R.id.item_goods_count, goodsEntity.getSell_count() + "人购买");


        } else {
            helper.setGone(R.id.item_goodbar, false);
        }

        helper.addOnClickListener(R.id.goods_detail);
        helper.addOnClickListener(R.id.item_download);
        helper.addOnClickListener(R.id.item_copy);

//        helper.addOnClickListener(R.id.item_countbar);
        RxView.clicks(helper.getView(R.id.item_countbar))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        communityListFrag.onCountBarClick(item);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });


        helper.addOnLongClickListener(R.id.item_content);
    }

    private SpannableStringBuilder getRebatePriceText(String price) {
        return new SpanUtils().append("分享赚 ").setFontSize(12, true).setForegroundColor(Color.parseColor("#25282D"))
                .append("¥" + price).setFontSize(16, true).setForegroundColor(Color.parseColor("#F73737")).setBold()
                .create();
    }

    private boolean getLastIndexForLimit(TextView tv, String content) {
        //实例化StaticLayout 传入相应参数
        StaticLayout staticLayout = new StaticLayout(content, tv.getPaint(), textWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        return staticLayout.getLineCount() > maxLines;
    }


    public class PicListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        private boolean isVideo;

        public PicListAdapter(boolean isVideo, int resId) {
            super(resId);
            this.isVideo = isVideo;
        }


        @Override
        protected void convert(BaseViewHolder helper, String item) {
            GlideUtil.loadRoundPic(helper.getView(R.id.item_img), item, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic, 5);
            helper.setGone(R.id.item_play_icon, isVideo);
        }
    }

}


