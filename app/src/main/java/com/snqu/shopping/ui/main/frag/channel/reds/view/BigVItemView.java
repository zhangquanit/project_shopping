package com.snqu.shopping.ui.main.frag.channel.reds.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.main.view.ItemNameView;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.statistics.DataCache;

/**
 * @author 张全
 */
public class BigVItemView extends RelativeLayout {
    private ImageView imageView;
    private ItemNameView itemNameView;
    private TextView tv_price;
    private TextView tv_pay_count;
    private TextView tv_coupon;

    public BigVItemView(Context context) {
        super(context);
        init(context);
    }

    public BigVItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BigVItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.reds_big_v_goods_item, this);
        imageView = findViewById(R.id.item_img);
        itemNameView = findViewById(R.id.item_name);
        tv_price = findViewById(R.id.item_price);
        tv_pay_count = findViewById(R.id.item_pay_count);
        tv_coupon = findViewById(R.id.item_coupon);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsDetailActivity.Companion.start(getContext(), goodsEntity.get_id(), goodsEntity.getItem_source(), CommonUtil.PLATE, CommonUtil.PLATE_CHILD, 1,goodsEntity);
                DataCache.reportGoodsByPlate(goodsEntity, 0);
            }
        });
    }

    GoodsEntity goodsEntity;

    public void setData(GoodsEntity item) {
        goodsEntity = item;
        GlideUtil.loadPic(imageView, item.getItem_image(), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);

        itemNameView.item_title.setTextSize(14);
        itemNameView.setText(ItemSourceClient.getItemSourceName(item.getItem_source()), item.getItem_title());

        tv_price.setText(CommonUtil.getPrice(item));
        tv_pay_count.setText(item.getSell_count() + "人付款");


        //优惠券
        String couponPrice = item.getCouponPrice();
        if (!TextUtils.isEmpty(couponPrice)) {
            tv_coupon.setVisibility(View.VISIBLE);
            tv_coupon.setText("优惠券" + couponPrice + "元");
        } else {
            tv_coupon.setVisibility(View.GONE);
        }
    }
}
