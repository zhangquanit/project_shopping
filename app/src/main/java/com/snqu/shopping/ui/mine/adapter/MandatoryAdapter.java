package com.snqu.shopping.ui.mine.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.entity.MandatoryServiceBean;

import java.util.List;

public class MandatoryAdapter extends BaseQuickAdapter<MandatoryServiceBean, BaseViewHolder> {

    public MandatoryAdapter(int layoutResId, @Nullable List<MandatoryServiceBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MandatoryServiceBean item) {
        helper.itemView.setId(item.getId());
        ((ImageView)helper.itemView.findViewById(R.id.item_img)).setImageResource(item.getRes());
        ((TextView)helper.itemView.findViewById(R.id.item_title)).setText(item.getTitle());
    }
}