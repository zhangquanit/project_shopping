package com.snqu.shopping.ui.mall.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.mall.entity.address.AddressEntity;

import java.util.List;

/**
 * 地址列表
 */
public class AddressManagerAdapter extends BaseQuickAdapter<AddressEntity, BaseViewHolder> {
    private int selPos = -1;

    public AddressManagerAdapter() {
        super(R.layout.address_manager_list_item);
    }

    public void refreshData(List<AddressEntity> data, int selPos) {
        this.selPos = selPos;
        replaceData(data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, AddressEntity item) {

        helper.setText(R.id.item_name, item.name);
        helper.setText(R.id.item_phone, item.phone_txt);
        helper.setText(R.id.item_detail, item.address_txt);
        helper.setGone(R.id.item_default, item.is_default == 1);
        helper.setGone(R.id.item_status, helper.getAdapterPosition() == selPos);
        helper.addOnClickListener(R.id.item_edit);
    }

}
