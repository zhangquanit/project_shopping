package com.snqu.shopping.ui.main.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.util.GlideUtil;

import java.util.List;

/**
 * @author liuming
 */
public class HomeTypeHalfAdapter extends BaseQuickAdapter<AdvertistEntity, BaseViewHolder> {

    private int itemHeight;

    public HomeTypeHalfAdapter(@Nullable List<AdvertistEntity> data, int height) {
        super(R.layout.home_item_half_pic, data);
        this.itemHeight = height;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, AdvertistEntity item) {
        ImageView imageView = helper.getView(R.id.icon);
        item.image = GlideUtil.checkUrl(item.image);
        GlideUtil.loadPic(imageView, item.image);
        float sc = ((float) item.height) / item.width;
        int vw = ((ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(25)) / 2);
        int vh = (int) (sc * vw);
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = vw;
        layoutParams.height = vh;
        helper.getView(R.id.layout).setLayoutParams(layoutParams);
//        GlideUtil.loadBitmap(mContext, item.image, new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                float sc = ((float) resource.getHeight()) / resource.getWidth();
//                int vw = ((ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(25)) / 2);
//                int vh = (int) (sc * vw);
//                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
//                layoutParams.width = vw;
//                layoutParams.height = vh;
//                imageView.setLayoutParams(layoutParams);
//                imageView.setImageBitmap(resource);
//                imageView.setCornerRadius(ConvertUtils.dp2px(6));
//            }
//        });
    }
}
