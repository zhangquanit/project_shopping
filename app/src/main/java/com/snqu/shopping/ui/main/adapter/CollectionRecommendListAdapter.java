package com.snqu.shopping.ui.main.adapter;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.CollectionGoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

public class CollectionRecommendListAdapter extends BaseQuickAdapter<CollectionGoodsEntity, BaseViewHolder> {
    private boolean editable;
    private boolean invalidate;//是否失效
    private BaseViewHolder helper;

    public CollectionRecommendListAdapter(boolean invalidate) {
        super(R.layout.recommend_goods_item);
        this.invalidate = invalidate;
    }

    public boolean setEditable() {
        this.editable = !editable;
        notifyDataSetChanged();
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, CollectionGoodsEntity collectionGoodsEntity) {


        this.helper = helper;
        helper.setGone(R.id.iv_check, editable ? true : false);
        //多加个参数来做状态
        helper.itemView.findViewById(R.id.iv_check).setSelected(collectionGoodsEntity.isSelected());
        GoodsEntity item = collectionGoodsEntity.getGoods();
        ImageView itemImage = helper.getView(R.id.item_img);
        GlideUtil.loadPic(itemImage, item.getItem_image(), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        if (invalidate) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            itemImage.setColorFilter(filter);
            itemImage.setAlpha(0.5f);
            itemImage.clearColorFilter();
        }
        helper.setText(R.id.item_title, item.getItem_title());
        helper.setText(R.id.item_price, CommonUtil.getPrice(item));
        helper.setText(R.id.item_pay_count, item.getSell_count() + "人付款"); //销量
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

        if (collectionGoodsEntity.getReduce_price() != null && collectionGoodsEntity.getReduce_price() >= 100) {
            TextView tv = helper.getView(R.id.tv_collection);
            tv.setVisibility(View.VISIBLE);
            String reducePrice = NumberUtil.saveTwoPoint(collectionGoodsEntity.getReduce_price());
            tv.setText("比收藏时下降 ¥" + reducePrice);
        }else{
            TextView tv = helper.getView(R.id.tv_collection);
            tv.setVisibility(View.GONE);
        }
//            helper.getView(R.id.tv_collection).setVisibility(View.VISIBLE);
//            String reducePrice = NumberUtil.saveTwoPoint(collectionGoodsEntity.getReduce_price());
//            helper.setText(R.id.tv_collection, "比收藏时下降 ¥" + reducePrice);
//        } else {
//            helper.getView(R.id.tv_collection).setVisibility(View.GONE);
//        }

        helper.addOnClickListener(R.id.iv_check);
    }

}