package com.snqu.shopping.ui.vip.adapter;

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.VipRightEntity;
import com.snqu.shopping.util.GlideUtil;

public class VipImgAdapter extends BaseQuickAdapter<VipRightEntity.VipMoreImg, BaseViewHolder> {


    public VipImgAdapter() {
        super(R.layout.vip_img_item);
    }


    @Override
    protected void convert(BaseViewHolder helper, VipRightEntity.VipMoreImg item) {

        ImageView imageView = helper.getView(R.id.item_img);
        GlideUtil.loadBitmap(imageView.getContext(), item.icon, new BitmapImageViewTarget(imageView) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                try {
                    Bitmap bitmap = resource;
                    int vw = LContext.screenWidth - DeviceUtil.dip2px(LContext.getContext(), 20);
                    int vh = (int) (bitmap.getHeight() * vw * 1.0f / (bitmap.getWidth() * 1.0f));

                    ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                    if (layoutParams.height != vh) {
                        layoutParams.height = vh;
                        imageView.setLayoutParams(layoutParams);
                    }
                    imageView.setImageBitmap(resource);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onResourceReady(resource, transition);
            }
        });

    }

}
