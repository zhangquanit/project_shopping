package com.snqu.shopping.ui.main.frag.classification.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.data.home.entity.ClassficationEntity;
import com.snqu.shopping.ui.main.view.ClassficationItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类-左侧
 */
public class ClassficationListAdapter extends BaseQuickAdapter<CategoryEntity, BaseViewHolder> {
    public ClassficationEntity selEntity;
    private int marginTop;

    public ClassficationListAdapter() {
        super(R.layout.classfication_list_item);
        marginTop = DeviceUtil.dip2px(LContext.getContext(), 20);
    }

    public void setSelId(ClassficationEntity selEntity) {
        this.selEntity = selEntity;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, CategoryEntity item) {
        helper.setText(R.id.item_title, item.name);
        LinearLayout item_container = helper.getView(R.id.item_container);
        item_container.removeAllViews();

        List<CategoryEntity> dataList = new ArrayList<>(item.childList);
        int index = 0;
        int spanCount = 3;
        Context context = item_container.getContext();
        while (dataList.size() >= spanCount) {
            ClassficationItemView itemView = new ClassficationItemView(context);
            itemView.setItems(dataList.subList(0, spanCount));
            if (index > 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = marginTop;
                item_container.addView(itemView, layoutParams);
            } else {
                item_container.addView(itemView);
            }
            dataList = dataList.subList(spanCount, dataList.size());
            index++;
        }
        if (dataList.size() > 0) {
            ClassficationItemView itemView = new ClassficationItemView(context);
            itemView.setItems(dataList);
            if (index > 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.topMargin = marginTop;
                item_container.addView(itemView, layoutParams);
            } else {
                item_container.addView(itemView);
            }
        }
    }
}