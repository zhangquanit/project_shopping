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

/**
 * 爆款
 *
 * @author 张全
 */
public class ChannelListAdapter2 extends BaseQuickAdapter<GoodsEntity, BaseViewHolder> {
    public ChannelListAdapter2() {
        super(R.layout.channel_list_item_2);
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsEntity item) {

        GlideUtil.loadPic(helper.getView(R.id.item_img), item.getItem_image(), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        ItemNameView itemNameView = helper.getView(R.id.item_name);
        itemNameView.item_title.setTextSize(14);
        itemNameView.setText(ItemSourceClient.getItemSourceName(item.getShopType()), item.getItem_title());

        helper.setText(R.id.item_price, CommonUtil.getPrice(item));
        helper.setText(R.id.item_pay_count, "爆卖" + item.getSell_count() + "件"); //销量
//        helper.setText(R.id.item_shop_tag, ItemSource.getSource(item.getItem_source()).title); //淘宝 天猫  京东
//        helper.setText(R.id.item_shop, null != item.getSeller() ? item.getSeller().getSeller_shop_name() : ""); //店铺名称

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
}
