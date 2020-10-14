package com.snqu.shopping.ui.main.frag.classification.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.CategoryEntity;

/**
 * 分类-左侧
 */
public class ClassficationTypeAdapter extends BaseQuickAdapter<CategoryEntity, BaseViewHolder> {
    public CategoryEntity selEntity;

    public ClassficationTypeAdapter() {
        super(R.layout.classfication_type_item);
    }

    public void setSelId(CategoryEntity selEntity) {
        this.selEntity = selEntity;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, CategoryEntity item) {

        View item_tag = helper.getView(R.id.item_tag);
        View item_container = helper.getView(R.id.item_container);
        TextView item_name = helper.getView(R.id.item_name);

        if ((null == selEntity && helper.getAdapterPosition() == 0) || (null != selEntity && TextUtils.equals(item.name, selEntity.name))) {
            item_tag.setVisibility(View.VISIBLE);
            item_container.setBackgroundColor(Color.WHITE);
            item_name.setTextColor(Color.parseColor("#25282D"));
        } else {
            item_tag.setVisibility(View.GONE);
            item_container.setBackgroundColor(Color.parseColor("#F5F5F7"));
            item_name.setTextColor(Color.parseColor("#848487"));
        }
        helper.setText(R.id.item_name, item.name);
    }
}