package com.snqu.shopping.ui.main.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.util.GlideUtil;

import java.util.List;

/**
 * @author liuming
 */
public class HomeTypeSevenAdapter extends BaseQuickAdapter<AdvertistEntity, BaseViewHolder> {

    int itemWidth;

    public HomeTypeSevenAdapter(@Nullable List<AdvertistEntity> data) {
        super(R.layout.home_item_seven, data);
        itemWidth = (ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(35)) / 4;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, AdvertistEntity item) {
        TextView titleView = helper.getView(R.id.six_title);
        TextView labelView = helper.getView(R.id.six_sub_title);
        TextView subTitleView = helper.getView(R.id.three_title);

        titleView.setVisibility(View.INVISIBLE);
        subTitleView.setVisibility(View.INVISIBLE);
        labelView.setVisibility(View.INVISIBLE);
        if (item.attribute != null) {
            if (!TextUtils.isEmpty(item.attribute.title)) {
                titleView.setVisibility(View.VISIBLE);
                titleView.setText(item.attribute.title);
                if (!TextUtils.isEmpty(item.attribute.title_font_color) && item.attribute.title_font_color.length() >= 7) {
                    titleView.setTextColor(Color.parseColor(item.attribute.title_font_color));
                }
            }
            if (!TextUtils.isEmpty(item.attribute.sub_title)) {
                subTitleView.setVisibility(View.VISIBLE);
                subTitleView.setText(item.attribute.sub_title);
                if (!TextUtils.isEmpty(item.attribute.sub_title_font_color) && item.attribute.sub_title_font_color.length() >= 7) {
                    subTitleView.setTextColor(Color.parseColor(item.attribute.sub_title_font_color));
                }
            }
            if (!TextUtils.isEmpty(item.attribute.label)) {
                labelView.setVisibility(View.VISIBLE);
                labelView.setText(item.attribute.label);
                if (!TextUtils.isEmpty(item.attribute.label_font_color) && item.attribute.label_font_color.length() >= 7) {
                    labelView.setTextColor(Color.parseColor(item.attribute.label_font_color));
                    GradientDrawable gradientDrawable = (GradientDrawable) labelView.getBackground();
                    if (gradientDrawable != null) {
                        gradientDrawable.setStroke(ConvertUtils.dp2px(0.5F), Color.parseColor(item.attribute.label_font_color));
                    }
                }

            }
        }
        helper.getView(R.id.item_one).setVisibility(View.GONE);
        helper.getView(R.id.item_two).setVisibility(View.GONE);
        List<GoodsEntity> goodsEntities = item.goodsList;
        if (goodsEntities != null && goodsEntities.size() > 0) {
            if (goodsEntities.size() == 1) {
                setItemOne(helper, goodsEntities);
            } else if (goodsEntities.size() == 2) {
                setItemOne(helper, goodsEntities);
                setItemTwo(helper, goodsEntities);
            }
        }

    }

    private void setItemTwo(@NonNull BaseViewHolder helper, List<GoodsEntity> goodsEntities) {
        helper.getView(R.id.item_two).setVisibility(View.VISIBLE);
        GoodsEntity goodsEntity = goodsEntities.get(1);
        ImageView imageView = helper.getView(R.id.six_img_two);
        goodsEntity.setItem_image(GlideUtil.checkUrl(goodsEntity.getItem_image()));
        GlideUtil.loadBitmap(mContext, goodsEntity.getItem_image(), new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.width = itemWidth;
                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(resource);
            }
        });

        SpanUtils spanUtils = new SpanUtils();
        if (!TextUtils.isEmpty(goodsEntity.getCouponPrice())) {
            spanUtils.append("券后 ").setFontSize(11, true)
                    .setForegroundColor(Color.parseColor("#25282D"));
        }
        spanUtils.append(goodsEntity.getNow_price())
                .setFontSize(13, true)
                .setForegroundColor(Color.parseColor("#F73737"))
                .setBold();
        TextView tv_quan = helper.getView(R.id.six_quan_two);
        tv_quan.setText(spanUtils.create());
    }

    private void setItemOne(@NonNull BaseViewHolder helper, List<GoodsEntity> goodsEntities) {
        helper.getView(R.id.item_one).setVisibility(View.VISIBLE);
        GoodsEntity goodsEntity = goodsEntities.get(0);
        ImageView imageView = helper.getView(R.id.six_img);
        goodsEntity.setItem_image(GlideUtil.checkUrl(goodsEntity.getItem_image()));
        GlideUtil.loadBitmap(mContext, goodsEntity.getItem_image(), new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.width = itemWidth;
                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(resource);
            }
        });
//        GlideUtil.loadPic(imageView, goodsEntity.getItem_image());
        SpanUtils spanUtils = new SpanUtils();
        if (!TextUtils.isEmpty(goodsEntity.getCouponPrice())) {
            spanUtils.append("券后 ").setFontSize(11, true)
                    .setForegroundColor(Color.parseColor("#25282D"));
        }
        spanUtils.append(goodsEntity.getNow_price())
                .setFontSize(13, true)
                .setForegroundColor(Color.parseColor("#F73737"))
                .setBold();
        TextView tv_quan = helper.getView(R.id.six_quan);
        tv_quan.setText(spanUtils.create());
    }
}
