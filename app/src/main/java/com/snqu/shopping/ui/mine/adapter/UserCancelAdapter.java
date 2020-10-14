package com.snqu.shopping.ui.mine.adapter;

import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedHashMap;
import java.util.List;

public class UserCancelAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private LinkedHashMap dataList = new LinkedHashMap<String, Boolean>();

    public LinkedHashMap<String, Boolean> getDataList() {
        return dataList;
    }

    public UserCancelAdapter(@Nullable List<String> data) {
        super(R.layout.item_user_cancel, data);
        for (String key : data) {
            dataList.put(key, false);
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        helper.setText(R.id.text, item);

        CheckBox cb = helper.getView(R.id.cb);
        helper.getView(R.id.item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb.setChecked(!cb.isChecked());
            }
        });
        cb.setChecked((Boolean) dataList.get(item));
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dataList.put(item, isChecked);
            EventBus.getDefault().post(new PushEvent(Constant.Event.USER_CANCEL_CHECK));
        });
    }
}
