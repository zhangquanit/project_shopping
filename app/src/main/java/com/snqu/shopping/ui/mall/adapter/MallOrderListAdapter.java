package com.snqu.shopping.ui.mall.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.mall.entity.MallOrderEntity;
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity;
import com.snqu.shopping.ui.mall.order.helper.MallOrderType;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

/**
 * 订单列表
 */
public class MallOrderListAdapter extends BaseQuickAdapter<MallOrderEntity, BaseViewHolder> {
    public MallOrderListAdapter() {
        super(R.layout.mall_order_list_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MallOrderEntity item) {
        helper.setText(R.id.item_orderNo, "订单编号：" + item._id);
        helper.setText(R.id.item_status, MallOrderType.getMallOrderType(item.status));//状态

        ShopGoodsEntity goodsInfoBean = item.goods_info.get(0);
        GlideUtil.loadPic(helper.getView(R.id.item_img), goodsInfoBean.getImage(), R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        helper.setText(R.id.item_name, goodsInfoBean.name);
        helper.setText(R.id.item_price, "￥" + NumberUtil.saveTwoPoint(goodsInfoBean.selling_price));
        helper.setText(R.id.item_stand, goodsInfoBean.standard_name); //规格
        helper.setText(R.id.inv_add_sub, "x" + goodsInfoBean.number);

        helper.setText(R.id.item_number, "共" + item.total_goods + "件商品");

        if (item.status == MallOrderType.PAY.status) { //待支付
            helper.setText(R.id.item_price_now, "应付 ￥" + NumberUtil.saveTwoPoint(item.total_price));
            helper.setGone(R.id.item_operationBar, true);
        } else {
            helper.setText(R.id.item_price_now, "实付 ￥" + NumberUtil.saveTwoPoint(item.total_price));
            helper.setGone(R.id.item_operationBar, false);
        }

        helper.addOnClickListener(R.id.item_btn_cancel);
        helper.addOnClickListener(R.id.item_btn_pay);
    }
}
