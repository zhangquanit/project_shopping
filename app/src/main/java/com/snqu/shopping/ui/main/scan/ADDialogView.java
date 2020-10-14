package com.snqu.shopping.ui.main.scan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;

import java.util.List;

import common.widget.dialog.DialogView;

/**
 * @author 张全
 */
public class ADDialogView extends DialogView {
    Bitmap bitmap;
    List<AdvertistEntity> adEntities;
    private int vw, vh;

    public ADDialogView(Context ctx) {
        super(ctx);
    }

    @Override
    protected void initView(View view) {

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        LinearLayout indictorsLayout = (LinearLayout) findViewById(R.id.indictors);
        int dw = DeviceUtil.dip2px(getContext(), 6);
        int leftMargin = DeviceUtil.dip2px(getContext(), 4);

        if (adEntities.size() > 1) {
            for (int i = 0; i < adEntities.size(); i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(R.drawable.ad_indicator_bg);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dw, dw);
                layoutParams.leftMargin = leftMargin;
                indictorsLayout.addView(imageView, layoutParams);
            }
            indictorsLayout.getChildAt(0).setSelected(true);
        }

        vw = LContext.screenWidth - DeviceUtil.dip2px(getContext(), 40);
        vh = (int) (vw * bitmap.getHeight() * 1.0f / bitmap.getWidth());
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewPager.getLayoutParams();
        layoutParams.height = vh;
        layoutParams.width = vw;
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        viewPager.setLayoutParams(layoutParams);

        AdPagerAdapter adPagerAdapter = new AdPagerAdapter();
        viewPager.setAdapter(adPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int pos) {
                if (adEntities.size() > 1) {
                    int childCount = indictorsLayout.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View chidView = indictorsLayout.getChildAt(i);
                        chidView.setSelected(i == pos);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        View btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setAd(List<AdvertistEntity> adEntities, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.adEntities = adEntities;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ad_dialog;
    }

    private class AdPagerAdapter extends PagerAdapter {
        LayoutInflater layoutInflater;

        public AdPagerAdapter() {
            layoutInflater = LayoutInflater.from(getContext());
        }

        @Override
        public int getCount() {
            return adEntities.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = (ImageView) layoutInflater.inflate(R.layout.ad_item, null);
            AdvertistEntity adEntity = adEntities.get(position);
//            GlideUtil.loadBitmap(getContext(), adEntity.image, new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
//                    imageView.setImageBitmap(bitmap);
//                }
//            });
            GlideUtil.loadPic(imageView, adEntity.image);

            container.addView(imageView);

            //点击事件
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtil.startWebFrag((Activity) getContext(), adEntity);
                    dismiss();
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}
