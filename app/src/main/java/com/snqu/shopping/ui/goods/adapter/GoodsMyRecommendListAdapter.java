package com.snqu.shopping.ui.goods.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
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
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.CommunityEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.goods.fragment.GoodRecmMySelfFrag;
import com.snqu.shopping.ui.main.frag.community.ZoomPicFrag;
import com.snqu.shopping.ui.video.PlayerVideoActivity;
import com.snqu.shopping.util.DateUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张全
 */
public class GoodsMyRecommendListAdapter extends BaseQuickAdapter<CommunityEntity, BaseViewHolder> {
    //    final int maxLines = 8;
    private int imageW;
    private GoodRecmMySelfFrag goodRecmMySelfFrag;
    private int textWidth, d5;

    public GoodsMyRecommendListAdapter(GoodRecmMySelfFrag goodRecmMySelfFrag) {
        super(R.layout.good_community_list_item);
        this.goodRecmMySelfFrag = goodRecmMySelfFrag;
        imageW = Math.round((LContext.screenWidth - DeviceUtil.dip2px(LContext.getContext(), 30)) / 3.0f);
        textWidth = LContext.screenWidth - DeviceUtil.dip2px(LContext.getContext(), 20);
        d5 = DeviceUtil.dip2px(LContext.getContext(), 5);
    }


    @SuppressLint("CheckResult")
    @Override
    protected void convert(BaseViewHolder helper, CommunityEntity item) {

        if (TextUtils.isEmpty(item.flag_txt)) {
            helper.getView(R.id.item_invalide).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.item_invalide).setVisibility(View.VISIBLE);
            helper.setText(R.id.item_invalide, item.flag_txt);
        }

        //头像背景
        helper.setVisible(R.id.item_rank_bg, item.rank <= 3);

        //商品排名
        TextView item_good_rank = helper.getView(R.id.item_good_rank);
        if (item.rank > 100) {
            item_good_rank.setText("商品排名:100+");
        } else {
            item_good_rank.setText("商品排名:" + item.rank);
        }

        //下单排名
        TextView item_order_count = helper.getView(R.id.item_order_count);
        item_order_count.setText("下单量:" + item.order_count);

        //预估奖励
        TextView item_price = helper.getView(R.id.item_price);
        if (item.reward_amount != 0) {
            item_price.setVisibility(View.VISIBLE);
            if (TextUtils.equals(item.settle_type, "1")) {
                item_price.setText(getItemPrice(NumberUtil.saveTwoPoint(item.reward_amount)));
            } else {
                item_price.setText(new SpanUtils().append("预估奖励:").setFontSize(14, true).setForegroundColor(Color.parseColor("#848487"))
                        .append("—").setFontSize(14, true).setForegroundColor(Color.parseColor("#ED362F")).setBold()
                        .create());
            }

//            if (goodRecmMySelfFrag.getGoodRecmInfoEntity() != null) {
//                if (!TextUtils.isEmpty(goodRecmMySelfFrag.getGoodRecmInfoEntity().is_nomal_settle)) {
//                    if (TextUtils.equals(goodRecmMySelfFrag.getGoodRecmInfoEntity().is_nomal_settle, "2")) {
//                        item_price.setText(new SpanUtils().append("预估奖励:").setFontSize(14, true).setForegroundColor(Color.parseColor("#848487"))
//                                .append("—").setFontSize(14, true).setForegroundColor(Color.parseColor("#ED362F")).setBold()
//                                .create());
//                    }
//                }
//            }
        } else {
            item_price.setVisibility(View.GONE);
        }


        //文本
        TextView textView = helper.getView(R.id.item_content);
        TextView item_switch = helper.getView(R.id.item_switch);
//        textView.setMaxLines(maxLines);
        textView.setText(item.content);

        if (TextUtils.isEmpty(item.content)) {
            textView.setVisibility(View.GONE);
            helper.getView(R.id.item_view).setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
            helper.getView(R.id.item_view).setVisibility(View.GONE);
        }
//        textView.post(() -> {
//            boolean b = getLastIndexForLimit(textView, item.content);
//            if (b) {
//                item_switch.setVisibility(View.VISIBLE);
//                item_switch.setText("全文");
//            } else {
//                item_switch.setVisibility(View.GONE);
//            }
//        });
//        item_switch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (textView.getMaxLines() == maxLines) {
//                    item_switch.setText("收起");
//                    textView.setMaxLines(10000);
//                } else {
//                    item_switch.setText("全文");
//                    textView.setMaxLines(maxLines);
//                }
//            }
//        });

        //加载头像和设置昵称
        GlideUtil.loadRoundPic(helper.getView(R.id.item_img), UserClient.getUser().avatar, R.drawable.icon_default_head, R.drawable.icon_default_head, 16);
        helper.setText(R.id.item_recom, UserClient.getUser().username);

        //设置创建时间
        if (item.itime > 0) {
            helper.setText(R.id.item_time, DateUtil.getFriendlyTimeStr(item.itime * 1000));
        }

        //审核不通过
        if (!TextUtils.isEmpty(item.status) && TextUtils.equals("2", item.status)) {
            helper.setVisible(R.id.item_label, true);
            helper.getView(R.id.item_look).setTag(item.examine_content);
            helper.addOnClickListener(R.id.item_look);
        } else {
            helper.getView(R.id.item_label).setVisibility(View.GONE);
//            helper.setVisible(R.id.item_label, false);
        }

        // 加载图片列表
        RecyclerView picListView = helper.getView(R.id.item_pics);
        boolean hasVideo = item.hasVideos();
        if (item.images_url != null && item.images_url.size() > 0 && !hasVideo && null == item.getGoods()) {
            //无图片/无视频/无商品
            picListView.setVisibility(View.GONE);
        } else {
            picListView.setVisibility(View.VISIBLE);

            List<String> itemList = item.images_url;
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
            GoodsMyRecommendListAdapter.PicListAdapter picListAdapter = new GoodsMyRecommendListAdapter.PicListAdapter(hasVideo, resId);
            picListAdapter.setOnItemClickListener((adapter, view, position) -> {
                if (hasVideo) {
                    PlayerVideoActivity.Companion.start(view.getContext(), item.videos.get(position));
                } else {
                    ZoomPicFrag.start(view.getContext(), position, new ArrayList<String>(item.images_url));
                }
            });
            picListView.setAdapter(picListAdapter);
            picListAdapter.setNewData(itemList);
        }


        GoodsEntity goodsEntity = item.getGoods();
        if (null != goodsEntity) {
            if (goodsEntity.getStatus() == 0) {
                helper.getView(R.id.item_recm).setBackgroundResource(R.drawable.shape_corner_f7f7f7);
                TextView tv = helper.getView(R.id.tv_recm);
                tv.setTextColor(Color.parseColor(
                        "#25282D"
                ));
            } else if (goodsEntity.getStatus() == 1) {
                helper.getView(R.id.item_recm).setBackgroundResource(R.drawable.shape_corner_ff8202);
                TextView tv = helper.getView(R.id.tv_recm);
                tv.setTextColor(Color.parseColor(
                        "#FFFFFF"
                ));
            }
            helper.setGone(R.id.item_goodbar, true);
            GlideUtil.loadPic(helper.getView(R.id.item_good_img), goodsEntity.getItem_image(), R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
            helper.setText(R.id.item_good_title, goodsEntity.getItem_title());
            //优惠券
            if (!getCouponPrice(goodsEntity)) {
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
//                if (TextUtils.equals(goodsEntity.getItem_source(), "V")) {
//                    helper.setGone(R.id.item_invalide, false);
//                } else {
//                    helper.setGone(R.id.item_invalide, true);
//                }
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
            helper.getView(R.id.goods_detail)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//
//                            if (goodsEntity.getStatus() == 0) { //优惠券过期
//                                ToastUtil.show("商品已过期");
//                            } else if (goodsEntity.getStatus() == 1) {  //商品详情
                            GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), goodsEntity);
//                            }
                        }
                    });
        } else {
            helper.setGone(R.id.item_goodbar, false);
        }


        // 设置点击事件
        helper.addOnClickListener(R.id.item_del);
        helper.addOnClickListener(R.id.item_recm);
    }

    private SpannableStringBuilder getRebatePriceText(String price) {
        return new SpanUtils().append("分享赚 ").setFontSize(12, true).setForegroundColor(Color.parseColor("#25282D"))
                .append("¥" + price).setFontSize(16, true).setForegroundColor(Color.parseColor("#F73737")).setBold()
                .create();
    }

    /**
     * 预估奖励
     *
     * @param price
     * @return
     */
    private SpannableStringBuilder getItemPrice(String price) {
        return new SpanUtils().append("预估奖励:").setFontSize(14, true).setForegroundColor(Color.parseColor("#848487"))
                .append("¥" + price).setFontSize(14, true).setForegroundColor(Color.parseColor("#ED362F")).setBold()
                .create();
    }

//    private boolean getLastIndexForLimit(TextView tv, String content) {
//        //实例化StaticLayout 传入相应参数
//        StaticLayout staticLayout = new StaticLayout(content, tv.getPaint(), textWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
//        return staticLayout.getLineCount() > maxLines;
//    }

    public boolean getCouponPrice(GoodsEntity goodsEntity) {
        if (goodsEntity != null) {
            if (TextUtils.isEmpty(goodsEntity.getCouponPrice())) {
                return true;
            }
        }
        return false;
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
