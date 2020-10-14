package com.snqu.shopping.ui.main.frag.channel.reds.view;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;

/**
 * 网红店
 *
 * @author 张全
 */
public class RedShopItemView extends RelativeLayout {
    private ImageView item_img;
    TextView item_coupon;
    TextView item_price;
    TextView item_old_price;
    TextView item_fan;
    private boolean reportPlate;

    public RedShopItemView(Context context) {
        super(context);
        init(context);
    }

    public RedShopItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RedShopItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.reds_shop_goods_item, this);
        item_img = findViewById(R.id.item_img);
        item_coupon = findViewById(R.id.item_coupon);
        item_price = findViewById(R.id.item_price);
        item_old_price = findViewById(R.id.item_old_price);
        item_fan = findViewById(R.id.item_fan);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reportPlate) {
                    GoodsDetailActivity.Companion.start(ctx, goodsEntitiy.get_id(), goodsEntitiy.getItem_source(), CommonUtil.PLATE, CommonUtil.PLATE_CHILD, 1,goodsEntitiy);
                } else {
                    GoodsDetailActivity.Companion.start(ctx, goodsEntitiy.get_id(), goodsEntitiy.getItem_source(),goodsEntitiy);
                }
            }
        });
    }

    public void reportPlate(boolean reportPlate) {
        this.reportPlate = reportPlate;
    }


    GoodsEntity goodsEntitiy;

    public void setData(GoodsEntity goodsEntitiy) {
        this.goodsEntitiy = goodsEntitiy;

        GlideUtil.loadPic(item_img, goodsEntitiy.getItem_image(), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        //优惠券
        String couponPrice = goodsEntitiy.getCouponPrice();
        if (!TextUtils.isEmpty(couponPrice)) {
            item_coupon.setVisibility(View.VISIBLE);
            item_coupon.setText("优惠券" + goodsEntitiy.getCoupon().getPrice() + "元");
        } else {
            item_coupon.setVisibility(View.GONE);
        }

        String fan_price = goodsEntitiy.getRebatePrice();
        if (!TextUtils.isEmpty(fan_price)) {
            item_fan.setText("返" + fan_price);
            item_fan.setVisibility(View.VISIBLE);
        } else {
            item_fan.setVisibility(View.GONE);
        }

        String now_price = goodsEntitiy.getNow_price();
        if (!TextUtils.isEmpty(now_price)) {
            item_price.setVisibility(View.VISIBLE);
            item_price.setText(getPrice(now_price));
        } else {
            item_price.setVisibility(View.GONE);
        }
        item_old_price.setText(getOldPrice(goodsEntitiy.getOld_price()));
    }


    private SpannableStringBuilder getOldPrice(String oldPrice) {
        return new SpanUtils().append(oldPrice).setStrikethrough().create();
    }

    private SpannableStringBuilder getPrice(String price) {
        SpanUtils spanUtils = new SpanUtils();
        if (!TextUtils.equals(goodsEntitiy.getNow_price(), goodsEntitiy.getOld_price())) {
            spanUtils = spanUtils.append("券后");
        }
        return spanUtils.setForegroundColor(Color.parseColor("#25282D")).setFontSize(12, true)
                .append(price).setForegroundColor(Color.parseColor("#FF8202")).setFontSize(14, true).setBold()
                .create();
    }
}
