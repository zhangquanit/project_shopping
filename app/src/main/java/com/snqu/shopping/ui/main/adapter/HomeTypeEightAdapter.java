package com.snqu.shopping.ui.main.adapter;

import android.graphics.Bitmap;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.util.GlideUtil;

import java.util.List;

/**
 * 大额神券样式
 */
public class HomeTypeEightAdapter extends BaseQuickAdapter<AdvertistEntity, BaseViewHolder> {

    public HomeTypeEightAdapter(@Nullable List<AdvertistEntity> data) {
        super(R.layout.home_item_eight, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, AdvertistEntity item) {
        RoundedImageView imageView = helper.getView(R.id.pic);

        item.image = GlideUtil.checkUrl(item.image);

        GlideUtil.loadBitmap(mContext, item.image, new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                float sc = ((float) resource.getHeight()) / resource.getWidth();
                int vw = ((ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(35)) / 4);
                int vh = (int) (sc * vw);
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.width = vw;
                layoutParams.height = vh;
                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap(resource);
                imageView.setCornerRadius(ConvertUtils.dp2px(6));
            }
        });
    }
}
