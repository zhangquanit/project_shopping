package com.snqu.shopping.ui.mall.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.mall.entity.flow.FlowDetailEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 物流列表
 */
public class MallFlowListAdapter extends BaseQuickAdapter<FlowDetailEntity, BaseViewHolder> {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateFormat3 = new SimpleDateFormat("HH:mm EEEE");

    public MallFlowListAdapter() {
        super(R.layout.mall_flow_list_item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FlowDetailEntity item) {

        try {
            Date date = dateFormat.parse(item.time);
            String yyyyMMdd = dateFormat2.format(date);
            String hhmmWeek = dateFormat3.format(date);
            helper.setText(R.id.item_date, yyyyMMdd + "\n" + hhmmWeek);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int pos = helper.getAdapterPosition();
        int last = getData().size() - 1;
        helper.setGone(R.id.item_line1, pos != 0);
        helper.setGone(R.id.item_line2, pos != last);

        if (pos == 0) {
            helper.setImageResource(R.id.item_icon, R.drawable.mall_point_p);
        } else if (pos == last) {
            helper.setImageResource(R.id.item_icon, R.drawable.mall_point_n);
        } else {
            helper.setImageResource(R.id.item_icon, R.drawable.mall_up_n);
        }

        helper.setText(R.id.item_content, item.context);
    }

}
