package com.snqu.shopping.ui.main.adapter;


import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;

public class GoodListAdapter extends BaseQuickAdapter<GoodsEntity, BaseViewHolder> {

    private ItemType type = ItemType.NORMAL;

    public GoodListAdapter() {
        super(R.layout.goods_item);
    }

    public void setShowType(ItemType type) {
        this.type = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsEntity item) {

        GlideUtil.loadPic(helper.getView(R.id.item_img), item.getItem_image(), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        helper.setText(R.id.item_title, item.getItem_title());
        helper.setText(R.id.item_price, CommonUtil.getPrice(item));
        if(TextUtils.equals(item.getItem_source(), Constant.BusinessType.S)){
            helper.setText(R.id.item_pay_count, ""); //销量
        }else {
            helper.setText(R.id.item_pay_count, item.getSell_count() + "人付款"); //销量
        }
        helper.setText(R.id.item_shop_tag, ItemSourceClient.getItemSourceName(item.getItem_source())); //淘宝 天猫  京东
        helper.setText(R.id.item_shop, item.getShopName()); //店铺名称

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


        if (type == ItemType.BUY) {
            helper.setVisible(R.id.item_btn_buy, true);
        }
    }

    public static enum ItemType {
        NORMAL(), //
        BUY(); //立即购买
    }
}