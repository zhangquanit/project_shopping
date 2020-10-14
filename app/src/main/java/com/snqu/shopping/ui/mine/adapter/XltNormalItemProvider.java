package com.snqu.shopping.ui.mine.adapter;

import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.util.LContext;
import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.entity.XltIncomeEntity;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

/**
 * 乐淘收入榜
 */

public class XltNormalItemProvider extends BaseItemProvider<XltIncomeEntity, BaseViewHolder> {

    @Override
    public int viewType() {
        return XltIncomeAdapter.TYPE_NORMAL;
    }

    @Override
    public int layout() {
        return R.layout.xlt_income_item;
    }

    @Override
    public void convert(BaseViewHolder helper, XltIncomeEntity data, int position) {
        GlideUtil.loadPic(helper.getView(R.id.item_img), data.avatar, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        helper.setText(R.id.item_username, data.nick);

        TextView item_price = helper.getView(R.id.item_price);
        item_price.setText(getPrice(NumberUtil.saveTwoPoint(data.income.longValue())));

        TextView item_tv_no = helper.getView(R.id.item_tv_no);
        item_tv_no.setText(String.valueOf(position + 1));

        ImageView item_no = helper.getView(R.id.item_no);
        if (position == 1) {
            int color = LContext.getColor(R.color.c_FFF73737);
            item_no.setVisibility(View.VISIBLE);
            item_no.setImageResource(R.drawable.xlt_income_no2);
            item_tv_no.setTextColor(color);
            item_price.setTextColor(color);
        } else if (position == 2) {
            int color = LContext.getColor(R.color.c_FFFF8202);
            item_no.setVisibility(View.VISIBLE);
            item_no.setImageResource(R.drawable.xlt_income_no3);
            item_tv_no.setTextColor(color);
            item_price.setTextColor(color);
        } else {
            item_no.setVisibility(View.INVISIBLE);
            int color = LContext.getColor(R.color.c_FF25282D);
            item_tv_no.setTextColor(color);
            item_price.setTextColor(color);
        }
    }

    private SpannableStringBuilder getPrice(String price) {
        return new SpanUtils().append("¥").setFontSize(10, true)
                .append(price).setFontSize(14, true)
                .create();
    }

    @Override
    public void onClick(BaseViewHolder helper, XltIncomeEntity data, int position) {
    }

    @Override
    public boolean onLongClick(BaseViewHolder helper, XltIncomeEntity data, int position) {
        return true;
    }
}
