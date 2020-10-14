package com.snqu.shopping.ui.bringgood.adapter;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.bringgoods.BringGoodsItemBean;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.ui.goods.player.MyJzVideo;
import com.snqu.shopping.util.GlideUtil;

import cn.jzvd.Jzvd;

/**
 * @author 张全
 */
public class BringVideoListAdapter extends BaseQuickAdapter<BringGoodsItemBean, BaseViewHolder> {
    public final static String TAG = "BringVideoListAdapter";

    public BringVideoListAdapter() {
        super(R.layout.bring_video_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BringGoodsItemBean item) {

        MyJzVideo myJzVideo = helper.getView(R.id.item_video_player);

//        String url = VideoCache.getProxyUrl(item.dy_video_url);
        String url = item.dy_video_url;
        myJzVideo.setUp(url, "", Jzvd.SCREEN_NORMAL);
//        myJzVideo.setFullScreenTouable(false); //全屏模式不允许快进快退
        Jzvd.SAVE_PROGRESS = false;
        myJzVideo.setCanEnterFullScreen(false);
        myJzVideo.setVolume(true);
        myJzVideo.hideProgressBar = true; //底部播放进度条
        myJzVideo.bottom_seek_progress.setVisibility(View.GONE);//隐藏拖动进度条
        myJzVideo.isLooping = true;
        myJzVideo.setVideoImageDisplayType(Jzvd.VIDEO_IMAGE_DISPLAY_TYPE_RATIO);
//        myJzVideo.startVideo();

//        GlideUtil.loadPic(helper.getView(R.id.thumb), item.first_frame);


        helper.setText(R.id.item_name, item.dy_video_title);

        GoodsEntity goods_info = item.goods_info;
        GlideUtil.loadPic(helper.getView(R.id.item_img), goods_info.getItem_image(), R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        helper.setText(R.id.item_title, goods_info.getItem_title());
        SpannableStringBuilder stringBuilder = new SpanUtils().append("¥").setFontSize(11, true)
                .append(goods_info.getNow_price()).create();
        helper.setText(R.id.item_price, stringBuilder);
        //优惠券
        if (!TextUtils.isEmpty(goods_info.getCouponPrice())) {
            helper.setGone(R.id.item_coupon, true);
            helper.setText(R.id.item_coupon, goods_info.getCouponPrice() + "元券");
        } else {
            helper.setGone(R.id.item_coupon, false);
        }
        if (!TextUtils.isEmpty(goods_info.getRebatePrice())) {
            helper.setVisible(R.id.item_fan, true);
            helper.setText(R.id.item_fan, "分享赚" + goods_info.getRebatePrice());
        } else {
            helper.setVisible(R.id.item_fan, false);
        }


        helper.addOnClickListener(R.id.item_goodbar);
        helper.addOnClickListener(R.id.item_share);
        helper.addOnClickListener(R.id.item_copy);

    }
}
