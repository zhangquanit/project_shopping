package com.snqu.shopping.ui.main.adapter;

import android.text.TextUtils;
import android.view.View;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;

public class CategoryListAdapter extends BaseQuickAdapter<GoodsEntity, BaseViewHolder> {
    private boolean showPostage;
    private boolean showRobBtb;
    private int d5,d10;

    public CategoryListAdapter() {
        super(R.layout.home_category_list_item);
        d5 = DeviceUtil.dip2px(LContext.getContext(), 5);
        d10=2*d5;
    }

    public void showPostage(boolean showPostage) {
        this.showPostage = showPostage;
    }

    public void showRobBtn() {
        showRobBtb = true;
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsEntity item) {

        int pos=helper.getAdapterPosition()-getHeaderLayoutCount();
        View container = helper.getView(R.id.item_container);
        if (pos % 2 == 0) {
            container.setPadding(d10, 0, d5, d10);
        } else if (pos % 2 == 1) {
            container.setPadding(d5, 0, d10, d10);
        }

        RoundedImageView imageView = helper.getView(R.id.item_img);

        GlideUtil.loadPic(imageView, item.getItem_image(), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        helper.setText(R.id.item_title, item.getItem_title());
        helper.setText(R.id.item_price, CommonUtil.getPrice(item));
        helper.setText(R.id.item_pay_count, item.getSell_count() + "人付款"); //销量
        helper.setText(R.id.item_shop_tag, ItemSourceClient.getItemSourceName(item.getItem_source())); //商品名称
        helper.setText(R.id.item_shop, item.getShopName()); //店铺名称

        if (showPostage && item.isSupportPostage()) {
            helper.setVisible(R.id.item_email_tag, true);
        } else {
            helper.setVisible(R.id.item_email_tag, false);
        }

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

        if (showRobBtb) {
            helper.setVisible(R.id.item_btn_rob, true);
        }
    }

}