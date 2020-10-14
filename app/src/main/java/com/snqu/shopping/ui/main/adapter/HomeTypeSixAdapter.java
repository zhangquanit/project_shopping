package com.snqu.shopping.ui.main.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.util.GlideUtil;

import java.util.List;

/**
 * 大额神券样式
 */
public class HomeTypeSixAdapter extends BaseQuickAdapter<GoodsEntity, BaseViewHolder> {

    public HomeTypeSixAdapter(@Nullable List<GoodsEntity> data) {
        super(R.layout.home_type_six_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, GoodsEntity item) {


        ImageView imageView = helper.getView(R.id.six_img);
        GlideUtil.loadPic(imageView, item.getItem_image());
        TextView tv_rebate = helper.getView(R.id.six_rebate);
        TextView tv_quan = helper.getView(R.id.six_quan);
        SpanUtils spanUtils = new SpanUtils();
        // 1. 如果没有优惠券,只显示价格
        if (TextUtils.isEmpty(item.getCouponPrice())) {
            tv_rebate.setVisibility(View.INVISIBLE);
            tv_quan.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(item.getRebatePrice())) {
                tv_rebate.setVisibility(View.VISIBLE);
                spanUtils.append("返" + item.getRebatePrice())
                        .setFontSize(11, true)
                        .setForegroundColor(Color.parseColor("#FE3B3B"))
                        .append(" ");
                tv_rebate.setText(spanUtils.create());
                spanUtils = new SpanUtils();
            }
            spanUtils.append(item.getOld_price())
                    .setFontSize(11, true)
                    .setForegroundColor(Color.parseColor("#FE3B3B"))
                    .setBold();
            tv_quan.setText(spanUtils.create());
        } else {
            //2. 有优惠券，无返利金
            if (!TextUtils.isEmpty(item.getRebatePrice())) {
                spanUtils.append("返" + item.getRebatePrice())
                        .setFontSize(11, true)
                        .setForegroundColor(Color.parseColor("#FE3B3B"))
                        .append(" ");
            }
            if (TextUtils.equals(item.getOld_price(), item.getRebatePrice())) {
                tv_rebate.setVisibility(View.INVISIBLE);
                tv_quan.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(item.getRebatePrice())) {
                    tv_rebate.setVisibility(View.VISIBLE);
                    tv_rebate.setText(spanUtils.create());
                    spanUtils = new SpanUtils();
                }
                spanUtils.append(item.getOld_price())
                        .setFontSize(11, true)
                        .setForegroundColor(Color.parseColor("#FE3B3B"))
                        .setBold();
                tv_quan.setText(spanUtils.create());
            } else {
                tv_rebate.setVisibility(View.VISIBLE);
                tv_quan.setVisibility(View.VISIBLE);
                spanUtils.append(item.getOld_price())
                        .setFontSize(10, true)
                        .setForegroundColor(Color.parseColor("#BCBCBC"))
                        .setStrikethrough();
                tv_rebate.setText(spanUtils.create());
                spanUtils = new SpanUtils();
                spanUtils.append("券后 ")
                        .setFontSize(12, true)
                        .setForegroundColor(Color.parseColor("#25282D"))
                        .append(item.getNow_price())
                        .setFontSize(12, true)
                        .setForegroundColor(Color.parseColor("#FE3B3B"))
                        .setBold();
                tv_quan.setText(spanUtils.create());
            }
        }
        TextView tv_tip = helper.getView(R.id.six_tip);
        if (TextUtils.isEmpty(item.getCouponPrice())) {
            tv_tip.setVisibility(View.INVISIBLE);
        } else {
            tv_tip.setVisibility(View.VISIBLE);
            tv_tip.setText("优惠券" + item.getCouponPrice());
        }

        if (helper.getAdapterPosition() == getData().size() - 1) {
            helper.getView(R.id.root_layout).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.root_layout).setVisibility(View.VISIBLE);
        }
    }
}
