package com.snqu.shopping.ui.vip.adapter;

import android.text.SpannableStringBuilder;
import android.view.View;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.VipGoodEntity;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

public class VipGoodListAdapter extends BaseQuickAdapter<VipGoodEntity, BaseViewHolder> {
    private int d5, d10;

    public VipGoodListAdapter() {
        super(R.layout.vip_good_item);
        d5 = DeviceUtil.dip2px(LContext.getContext(), 5);
        d10 = DeviceUtil.dip2px(LContext.getContext(), 10);
    }


    @Override
    protected void convert(BaseViewHolder helper, VipGoodEntity item) {

        int pos=helper.getAdapterPosition()-getHeaderLayoutCount();
        View container = helper.getView(R.id.item_container);
        if (pos % 2 == 0) {
            container.setPadding(d10, 0, d5, d10);
        } else if (pos % 2 == 1) {
            container.setPadding(d5, 0, d10, d10);
        }

        RoundedImageView imageView = helper.getView(R.id.item_img);
        String url = "";
        if (null != item.banner && !item.banner.isEmpty()) {
            url = item.banner.get(0);
        }
        GlideUtil.loadPic(imageView, url, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        helper.setText(R.id.item_title, item.title);
        helper.setText(R.id.item_price, getPrice(NumberUtil.saveTwoPoint(item.price)));
    }

    private SpannableStringBuilder getPrice(String price) {
        return new SpanUtils().append("¥").setFontSize(11, true)
                .append(price).setFontSize(16, true)
                .create();
    }

}