package com.snqu.shopping.ui.mine.adapter;

import android.text.SpannableStringBuilder;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.entity.XltIncomeEntity;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

/**
 * 乐淘收入榜第一名
 */

public class XltNo1ItemProvider extends BaseItemProvider<XltIncomeEntity, BaseViewHolder> {

    @Override
    public int viewType() {
        return XltIncomeAdapter.TYPE_NO1;
    }

    @Override
    public int layout() {
        return R.layout.xlt_income_no1;
    }

    @Override
    public void convert(BaseViewHolder helper, XltIncomeEntity data, int position) {
        GlideUtil.loadPic(helper.getView(R.id.item_img), data.avatar, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        helper.setText(R.id.item_nickname, data.nick);
        helper.setText(R.id.item_price, getPrice(NumberUtil.saveTwoPoint(data.income.longValue())));
    }

    private SpannableStringBuilder getPrice(String price) {
        return new SpanUtils().append("¥").setFontSize(10, true)
                .append(price).setFontSize(13, true)
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
