package com.snqu.shopping.ui.mine.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;
import java.util.Map;

public class UserCancellationAdapter extends BaseQuickAdapter<Map<String,Boolean>, BaseViewHolder> {

    public UserCancellationAdapter(@Nullable List<Map<String, Boolean>> data) {
        super(data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Map<String, Boolean> item) {

    }
}
