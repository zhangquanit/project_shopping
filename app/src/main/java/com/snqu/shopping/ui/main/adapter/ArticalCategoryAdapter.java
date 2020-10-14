package com.snqu.shopping.ui.main.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.artical.ArticalCategoryEntity;
import com.snqu.shopping.util.GlideUtil;

public class ArticalCategoryAdapter extends BaseQuickAdapter<ArticalCategoryEntity, BaseViewHolder> {


    public ArticalCategoryAdapter() {
        super(R.layout.community_school_c_item);

    }


    @Override
    protected void convert(BaseViewHolder helper, ArticalCategoryEntity item) {
        GlideUtil.loadPic(helper.getView(R.id.item_img), item.ico_url, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        helper.setText(R.id.item_name, item.name);

//        RelativeLayout container = helper.getView(R.id.container);
//        if (helper.getAdapterPosition() % 3 == 0) {
//            container.setGravity(Gravity.LEFT);
//        } else if (helper.getAdapterPosition() % 3 == 1) {
//            container.setGravity(Gravity.CENTER);
//        } else if (helper.getAdapterPosition() % 3 == 2) {
//            container.setGravity(Gravity.RIGHT);
//        }

    }

}