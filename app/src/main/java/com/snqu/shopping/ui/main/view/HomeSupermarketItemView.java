package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.util.GlideUtil;

/**
 * @author 张全
 */
public class HomeSupermarketItemView extends RelativeLayout {
    private ImageView imageView;
    private TextView tv_privice;
    private TextView tv_privice_old;
    private TextView item_coupon;
    private int d5;
    GoodsEntity goodsEntitiy;

    public HomeSupermarketItemView(Context context) {
        super(context);
        init(context);
    }

    public HomeSupermarketItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HomeSupermarketItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        d5 = DeviceUtil.dip2px(context, 5);
        LayoutInflater.from(context).inflate(R.layout.home_supermarket_img_item, this);
        imageView = findViewById(R.id.item_img);
        item_coupon = findViewById(R.id.item_coupon);
        tv_privice = findViewById(R.id.item_price_now);
        tv_privice_old = findViewById(R.id.item_price_old);
    }

    private static int imageW;

    public void setData(GoodsEntity goodsEntitiy) {
        this.goodsEntitiy = goodsEntitiy;
        GlideUtil.loadPic(imageView, goodsEntitiy.getItem_image(), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        if (imageW == 0) {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageW = imageView.getMeasuredWidth();
                    ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                    if (layoutParams.height != imageW) {
                        layoutParams.height = imageW;
                        imageView.setLayoutParams(layoutParams);
                    }
                }
            });
        } else {
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            if (layoutParams.height != imageW) {
                layoutParams.height = imageW;
                imageView.setLayoutParams(layoutParams);
            }
        }


        //优惠券
        String coupon = goodsEntitiy.getCouponPrice();
        if (!TextUtils.isEmpty(coupon)) {
            item_coupon.setVisibility(View.VISIBLE);
            item_coupon.setText("优惠券" + coupon + "元");
        } else {
            item_coupon.setVisibility(View.GONE);
        }

        String fan_price = goodsEntitiy.getRebatePrice();
        tv_privice_old.setText(getPrice1(fan_price, goodsEntitiy.getOld_price()));

        String now_price = goodsEntitiy.getNow_price();
        if (!TextUtils.isEmpty(now_price)) {
            tv_privice.setText(getPrice2(now_price));
            tv_privice.setVisibility(View.VISIBLE);
        } else {
            tv_privice.setVisibility(View.GONE);
        }
    }

    private SpannableStringBuilder getPrice1(String price1, String price2) {
        if (TextUtils.isEmpty(price1)) {
            return new SpanUtils()
                    .append(price2).setForegroundColor(Color.parseColor("#ff848487")).setStrikethrough()
                    .create();
        }
        return new SpanUtils()
                .append("返" + price1).setForegroundColor(Color.parseColor("#F73737"))
                .appendSpace(d5)
                .append(price2).setForegroundColor(Color.parseColor("#ff848487")).setStrikethrough()
                .create();
    }

    private SpannableStringBuilder getPrice2(String price) {
        SpanUtils spanUtils = new SpanUtils();
        if (!TextUtils.equals(goodsEntitiy.getNow_price(), goodsEntitiy.getOld_price())) {
            spanUtils = spanUtils.append("券后");
        }
        return spanUtils.setForegroundColor(Color.parseColor("#25282D"))
                .append(price).setForegroundColor(Color.parseColor("#F73737")).setBold()
                .create();
    }
}
