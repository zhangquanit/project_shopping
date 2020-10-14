package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.android.util.os.DeviceUtil;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.ShopItemEntity;

/**
 * 店铺等级
 *
 * @author 张全
 */
public class ShopLevelView extends LinearLayout {
    public ShopLevelView(Context context) {
        super(context);
    }

    public ShopLevelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ShopLevelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private static float credit_level = 0.5f;

    public void setData(ShopItemEntity shopItemEntity) {
        removeAllViews();
        setVisibility(View.VISIBLE);
        String seller_type = shopItemEntity.seller_type;
        float level = shopItemEntity.credit_level;
        int margin = DeviceUtil.dip2px(getContext(), 2);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = margin;
        layoutParams.gravity = Gravity.CENTER_VERTICAL;

        if (TextUtils.equals(seller_type, "C") || TextUtils.equals(seller_type, "B")) { // 淘宝/天猫
            int drawbleRes;
            if (level <= 5) {
                drawbleRes = R.drawable.tb_level1;
                addTBLevelView(drawbleRes, (int) level, layoutParams);
            } else if (level <= 10) {
                drawbleRes = R.drawable.tb_level2;
                addTBLevelView(drawbleRes, (int) (level - 5), layoutParams);
            } else if (level <= 15) {
                drawbleRes = R.drawable.tb_level3;
                addTBLevelView(drawbleRes, (int) (level - 10), layoutParams);
            } else {
                drawbleRes = R.drawable.tb_level4;
                addTBLevelView(drawbleRes, (int) (level - 15), layoutParams);
            }

        } else if ((TextUtils.equals(seller_type, "D"))) { //京东
            if (shopItemEntity.jd_self == 1) { //自营
                setVisibility(View.GONE);
                return;
            }

            if (level > 0) { //有等级
                int lev = (int) (level * 2);
                int hNum = lev % 2;
                int pNum = lev / 2;


                for (int i = 0; i < 5; i++) {  //0-5.0f
                    if (i < pNum) {
                        ImageView imageView = new ImageView(getContext());
                        imageView.setImageResource(R.drawable.star_p);
                        if (i > 0) {
                            addView(imageView, layoutParams);
                        } else {
                            addView(imageView);
                        }
                    } else {
                        if (hNum == 1) {
                            ImageView imageView = new ImageView(getContext());
                            imageView.setImageResource(R.drawable.star_half);
                            if (i > 0) {
                                addView(imageView, layoutParams);
                            } else {
                                addView(imageView);
                            }
                            hNum = 0;
                        } else {
                            ImageView imageView = new ImageView(getContext());
                            imageView.setImageResource(R.drawable.star_n);
                            addView(imageView, layoutParams);
                        }
                    }

                }
            } else { //无等级的 展示京东好店
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(R.drawable.jd_shop);
                addView(imageView);

            }
        }
    }

    private void addTBLevelView(int drawbleRes, int num, LayoutParams layoutParams) {
        for (int i = 1; i <= num; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(drawbleRes);
            if (i > 1) {
                addView(imageView, layoutParams);
            } else {
                addView(imageView);
            }
        }
    }
}
