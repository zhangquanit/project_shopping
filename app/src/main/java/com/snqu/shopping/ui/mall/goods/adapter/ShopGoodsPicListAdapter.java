package com.snqu.shopping.ui.mall.goods.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.ui.main.frag.community.ZoomPicFrag;
import com.snqu.shopping.util.GlideUtil;

import java.util.ArrayList;
import java.util.List;

public class ShopGoodsPicListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public ShopGoodsPicListAdapter(@Nullable List<String> data) {
        super(R.layout.detail_goods_pic_item, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper,final String item) {
        ImageView imageView = helper.getView(R.id.img_item);
        GlideUtil.loadDetailPic(imageView, item, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(item);
                ZoomPicFrag.start(mContext, 0, list);
            }
        });
    }
}