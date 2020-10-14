package com.snqu.shopping.ui.main.frag.channel.reds.adapter;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.data.home.entity.ShopItemEntity;
import com.snqu.shopping.ui.main.frag.channel.reds.view.RedShopItemView;
import com.snqu.shopping.ui.main.view.ItemNameView;
import com.snqu.shopping.ui.main.view.ShopLevelView;
import com.snqu.shopping.util.GlideUtil;

import java.util.List;

/**
 * 网红店
 *
 * @author 张全
 */
public class RedShopAdapter extends BaseQuickAdapter<ShopItemEntity, BaseViewHolder> {
    private boolean hideUserBanner;
    private boolean reportPlate;

    public RedShopAdapter() {
        super(R.layout.reds_shop_item);
    }

    public void reportPlate(boolean reportPlate) {
        this.reportPlate = reportPlate;
    }

    public void hideUserBanner() {
        hideUserBanner = true;
    }

    @Override
    protected void convert(BaseViewHolder helper, ShopItemEntity item) {

//        if (TextUtils.isEmpty(item.seller_shop_id)) {
//            test(helper);
//            return;
//        }

        GlideUtil.loadPic(helper.getView(R.id.item_img), item.seller_shop_icon, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);

        ItemNameView itemNameView = helper.getView(R.id.item_name);
        itemNameView.item_title.setTextSize(15);
        itemNameView.item_title.setSingleLine();
        itemNameView.item_title.getPaint().setFakeBoldText(true);
        itemNameView.setText(ItemSourceClient.getItemSourceName(item.seller_type), item.seller_shop_name);

        helper.setText(R.id.item_count, getFocusCount(item.getFans()));
        helper.addOnClickListener(R.id.item_detail);

        if (TextUtils.isEmpty(item.getScoreDesc())) {
            helper.setGone(R.id.item_soces, false);
        } else {
            helper.setGone(R.id.item_soces, true);
            helper.setText(R.id.item_tv1, item.getScoreDesc());
            helper.setText(R.id.item_tv2, item.getScoreServ());
            helper.setText(R.id.item_tv3, item.getScorePost());
        }

        ShopLevelView shopLevelView = helper.getView(R.id.shop_level);
        shopLevelView.setData(item);

        List<GoodsEntity> goods = item.goods;
        if (null == goods || goods.isEmpty()) {
            helper.setGone(R.id.item_imgs, false);
        } else {
            helper.setGone(R.id.item_imgs, true);
            helper.setVisible(R.id.item_img1, false);
            helper.setVisible(R.id.item_img2, false);
            helper.setVisible(R.id.item_img3, false);
            if (goods.size() >= 1) {
                helper.setVisible(R.id.item_img1, true);
                RedShopItemView itemView1 = helper.getView(R.id.item_img1);
                itemView1.reportPlate(reportPlate);
                itemView1.setData(goods.get(0));
            }
            if (goods.size() >= 2) {
                helper.setVisible(R.id.item_img2, true);
                RedShopItemView itemView2 = helper.getView(R.id.item_img2);
                itemView2.reportPlate(reportPlate);
                itemView2.setData(goods.get(1));
            }

            if (goods.size() >= 3) {
                helper.setVisible(R.id.item_img3, true);
                RedShopItemView itemView3 = helper.getView(R.id.item_img3);
                itemView3.reportPlate(reportPlate);
                itemView3.setData(goods.get(2));
            }
        }

        if (hideUserBanner) {
            helper.setGone(R.id.item_user_banner, false);
            helper.setGone(R.id.item_goods_count, false);
        } else {
            if (null == item.scroll || item.scroll.isEmpty()) {
                helper.setGone(R.id.item_user_banner, false);
            } else {
                helper.setGone(R.id.item_user_banner, true);
                setScrollItem(helper.getView(R.id.flipper), item.scroll);
            }
            helper.setText(R.id.item_goods_count, "共" + item.goods_count + "件商品");
        }

        helper.addOnClickListener(R.id.item_top);

    }

    private SpannableStringBuilder getFocusCount(String count) {
        if (TextUtils.isEmpty(count)) return null;
        return new SpanUtils()
                .append(count).setForegroundColor(Color.parseColor("#FF8202")).setFontSize(13, true).setBold()
                .append("人关注").setForegroundColor(Color.parseColor("#25282D")).setFontSize(13, true).setBold()
                .create();
    }


    /**
     * 滚动内容
     */
    private void setScrollItem(ViewFlipper viewFlipper, List<ShopItemEntity.ScorllItem> scorllItems) {
        viewFlipper.stopFlipping();
        viewFlipper.removeAllViews();
        if (null == scorllItems || scorllItems.isEmpty()) {
            viewFlipper.setVisibility(View.GONE);
            return;
        }
        viewFlipper.setVisibility(View.VISIBLE);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        for (int i = 0; i < scorllItems.size(); i++) {
            ShopItemEntity.ScorllItem scorllItem = scorllItems.get(i);
            View view = layoutInflater.inflate(R.layout.shop_scroll_item, null);
            ImageView item_user_icon = view.findViewById(R.id.item_user_icon);
            TextView textView = view.findViewById(R.id.item_fans);

            GlideUtil.loadPic(item_user_icon, scorllItem.img_url, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
            textView.setText(scorllItem.content);

            viewFlipper.addView(view);
        }
        viewFlipper.startFlipping();
    }
}
