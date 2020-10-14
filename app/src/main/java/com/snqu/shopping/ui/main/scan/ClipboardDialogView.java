package com.snqu.shopping.ui.main.scan;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.util.GlideUtil;

import common.widget.dialog.DialogView;

/**
 * 确认对话框
 *
 * @author 张全
 */
public class ClipboardDialogView extends DialogView implements OnClickListener {
    private DismissListener dismissListener;
    private GoodsEntity goodsEntity;

    public ClipboardDialogView(Context ctx, GoodsEntity goodsEntity) {
        super(ctx);
        this.goodsEntity = goodsEntity;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.clipboard_good_dialog;
    }

    @Override
    public void initView(View view) {
        ImageView item_img = (ImageView) findViewById(R.id.item_img);
        GlideUtil.loadPic(item_img, goodsEntity.getItem_image(), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);

        TextView item_name = (TextView) findViewById(R.id.item_name);
        item_name.setText(goodsEntity.getItem_title());

        //优惠券
        TextView item_coupon = (TextView) findViewById(R.id.item_coupon);
        if (!TextUtils.isEmpty(goodsEntity.getCouponPrice())) {
            item_coupon.setVisibility(View.VISIBLE);
            item_coupon.setText(goodsEntity.getCouponPrice() + "元券");
        }

        //返利金
        TextView item_fan = (TextView) findViewById(R.id.item_fan);
        if (!TextUtils.isEmpty(goodsEntity.getRebatePrice())) {
            item_fan.setVisibility(View.VISIBLE);
            item_fan.setText("返" + goodsEntity.getRebatePrice());
        }

        if (TextUtils.isEmpty(goodsEntity.getCouponPrice()) && TextUtils.isEmpty(goodsEntity.getRebatePrice())) {
            findViewById(R.id.fan_banner).setVisibility(View.GONE);
        }

        //价格
        TextView tv_price = (TextView) findViewById(R.id.tv_price);
        tv_price.setText(getPrice(goodsEntity));


        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_sure).setOnClickListener(this);
    }

    public static SpannableStringBuilder getPrice(GoodsEntity item) {
        String price = item.getNow_price();
        String oldPrice = item.getOld_price();
        int d7 = DeviceUtil.dip2px(LContext.getContext(), 7);
        SpanUtils spanUtils = new SpanUtils();
        if (!TextUtils.isEmpty(item.getCouponPrice())) {
            //有优惠券显示券后
            spanUtils.append("券后").setForegroundColor(Color.parseColor("#25282D")).setFontSize(12, true);
        }

        if (!TextUtils.equals(price, oldPrice)) {
            //有返利金显示原件
            spanUtils.append(price).setForegroundColor(Color.parseColor("#F73737")).setFontSize(16, true).setBold().appendSpace(d7)
                    .append(oldPrice).setForegroundColor(Color.parseColor("#848487")).setFontSize(11, true).setStrikethrough();
        } else {
            spanUtils.append(price).setForegroundColor(Color.parseColor("#F73737")).setFontSize(16, true).setBold();
        }
        return spanUtils.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                GoodsDetailActivity.Companion.start(getContext(), goodsEntity.getGoods_id(), goodsEntity.getItem_source(), goodsEntity);
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }

    public void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public interface DismissListener {
        void dismiss();
    }
}
