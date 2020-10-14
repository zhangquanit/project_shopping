package com.snqu.shopping.ui.main.frag.channel.adapter;


import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.ui.main.view.ItemNameView;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;

public class RedListAdapter extends BaseQuickAdapter<GoodsEntity, BaseViewHolder> {


    public RedListAdapter() {
        super(R.layout.red_goods_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsEntity item) {

        GlideUtil.loadPic(helper.getView(R.id.item_img), item.getItem_image(), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);

        ItemNameView itemNameView = helper.getView(R.id.item_title);
        itemNameView.item_title.setTextSize(14);
        itemNameView.setText(ItemSourceClient.getItemSourceName(item.getItem_source()), item.getItem_title());

        helper.setText(R.id.item_price, CommonUtil.getPrice(item));
        helper.setText(R.id.item_pay_count, "网红同款，已售" + item.getSell_count() + "件"); //销量

        //优惠券
        if (!TextUtils.isEmpty(item.getCouponPrice())) {
            helper.setGone(R.id.item_coupon, true);
            helper.setText(R.id.item_coupon, item.getCouponPrice() + "元券");
        } else {
            helper.setGone(R.id.item_coupon, false);
        }

        //返利金
        if (!TextUtils.isEmpty(item.getRebatePrice())) {
            helper.setVisible(R.id.item_fan, true);
            helper.setText(R.id.item_fan, "返" + item.getRebatePrice());
        } else {
            helper.setVisible(R.id.item_fan, false);
        }
    }

    public static enum ItemType {
        NORMAL(), //
        BUY(); //立即购买
    }
}