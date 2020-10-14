package com.snqu.shopping.ui.mall.adapter;

import android.text.SpannableStringBuilder;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.snqu.shopping.R;
import com.snqu.shopping.data.mall.entity.MallRecommendEntity;
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

/**
 * 推荐-商品
 */

public class MallRecommendGoodProvider extends BaseItemProvider<MallRecommendEntity, BaseViewHolder> {

    @Override
    public int viewType() {
        return MallRecommendAdapter.TYPE_GOOD;
    }

    @Override
    public int layout() {
        return R.layout.mall_recommend_goods;
    }

    @Override
    public void convert(BaseViewHolder helper, MallRecommendEntity data, int position) {
        ShopGoodsEntity goodsBean = data.goods.get(0);

        GlideUtil.loadPic(helper.getView(R.id.imageview), data.images_url, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        helper.setText(R.id.item_title, goodsBean.name);
        helper.setText(R.id.item_desc, goodsBean.describe);
        helper.setText(R.id.item_price, getPrice(goodsBean.selling_price));
        helper.setText(R.id.item_sell_count, "已抢" + NumberUtil.sellCount(goodsBean.sold) + "件");

    }

    private SpannableStringBuilder getPrice(long price) {
        String priceStr = NumberUtil.saveTwoPoint(price);
        String[] split = priceStr.split("\\.");
        SpannableStringBuilder stringBuilder = new SpanUtils()
                .append("¥").setFontSize(10, true).setBold()
                .append(split[0]).setFontSize(21, true).setBold()
                .append("." + split[1]).setFontSize(10, true).setBold()
                .create();
        return stringBuilder;
    }

    @Override
    public void onClick(BaseViewHolder helper, MallRecommendEntity data, int position) {
    }

    @Override
    public boolean onLongClick(BaseViewHolder helper, MallRecommendEntity data, int position) {
        return true;
    }
}
