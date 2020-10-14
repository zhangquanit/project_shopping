package com.snqu.shopping.ui.main.frag.channel.reds.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.red.entity.RedGoodeEntity;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;

/**
 * 好物说
 *
 * @author 张全
 */
public class RedGoodsAdapter extends BaseQuickAdapter<RedGoodeEntity, BaseViewHolder> {
    public RedGoodsAdapter() {
        super(R.layout.reds_goods_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, RedGoodeEntity item) {
        GoodsEntity goodsEntity = item.good_info;
        GlideUtil.loadPic(helper.getView(R.id.item_img), goodsEntity.getItem_image(), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        helper.setText(R.id.item_name, goodsEntity.getItem_title());

        helper.setText(R.id.item_price, CommonUtil.getPrice(goodsEntity));
        helper.setText(R.id.item_pay_count, goodsEntity.getSell_count() + "人付款");


        //优惠券
        if (!TextUtils.isEmpty(goodsEntity.getCouponPrice())) {
            helper.setGone(R.id.item_coupon, true);
            helper.setText(R.id.item_coupon, goodsEntity.getCouponPrice() + "元券");
        } else {
            helper.setGone(R.id.item_coupon, false);
        }

        //返利金
        if (!TextUtils.isEmpty(goodsEntity.getRebatePrice())) {
            helper.setVisible(R.id.item_fan, true);
            helper.setText(R.id.item_fan, "返" + goodsEntity.getRebatePrice());
        } else {
            helper.setVisible(R.id.item_fan, false);
        }

        if (TextUtils.isEmpty(item.recommend_content)) {
            helper.setGone(R.id.item_line, false);
            helper.setGone(R.id.item_intro, false);
        } else {
            helper.setGone(R.id.item_line, true);
            helper.setGone(R.id.item_intro, true);
            helper.setText(R.id.item_intro, item.recommend_content);
        }

    }
}
