package com.snqu.shopping.ui.main.frag.channel.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.ui.main.view.ItemNameView;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;

/**
 * 大额券
 *
 * @author 张全
 */
public class ChannelListAdapter3 extends BaseQuickAdapter<GoodsEntity, BaseViewHolder> {
    public ChannelListAdapter3() {
        super(R.layout.channel_list_item_3);
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsEntity item) {
        GlideUtil.loadPic(helper.getView(R.id.item_img), item.getItem_image(), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        ItemNameView itemNameView = helper.getView(R.id.item_name);
        itemNameView.item_title.setTextSize(14);
        itemNameView.setText(ItemSourceClient.getItemSourceName(item.getShopType()), item.getItem_title());

        helper.setText(R.id.item_price, CommonUtil.getPrice(item));

        helper.setText(R.id.item_coupon, item.getCouponPrice());

    }
}
