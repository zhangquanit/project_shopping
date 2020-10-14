package com.snqu.shopping.ui.mall.goods.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity;

import java.util.List;

public class CartCategoryListAdapter extends BaseQuickAdapter<ShopGoodsEntity.StandardBean, BaseViewHolder> {

    public CartCategoryListener listener;

    public interface CartCategoryListener {
        void resetCartCategory(int oldPos, int newPos);
    }

    public void setListener(CartCategoryListener listener) {
        this.listener = listener;
    }

    // 规格选中index
    private int checkIndex = -1;

    public CartCategoryListAdapter(@Nullable List<ShopGoodsEntity.StandardBean> data) {
        super(R.layout.view_cart_category, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShopGoodsEntity.StandardBean item) {

        helper.setText(R.id.cart_category_name, item.name);
        if (checkIndex == helper.getAdapterPosition()) {
            helper.setBackgroundRes(R.id.cart_category_name, R.drawable.shop_cb_true);
            helper.setTextColor(R.id.cart_category_name, Color.parseColor("#FF8202"));
        } else {
            helper.setBackgroundRes(R.id.cart_category_name, R.drawable.shop_cb_false);
            helper.setTextColor(R.id.cart_category_name, Color.parseColor("#25282D"));
        }
        helper.getView(R.id.cart_category_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.resetCartCategory(checkIndex, helper.getAdapterPosition());
                }
                checkIndex = helper.getAdapterPosition();
                v.setBackgroundResource(R.drawable.shop_cb_true);
                ((TextView) v).setTextColor(Color.parseColor("#FF8202"));
            }
        });
//        helper.addOnClickListener(R.id.cart_category_name);

    }

}