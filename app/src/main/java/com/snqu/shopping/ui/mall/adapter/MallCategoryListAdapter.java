package com.snqu.shopping.ui.mall.adapter;

import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.snqu.shopping.R;
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

import java.util.List;

/**
 * 分类列表
 */
public class MallCategoryListAdapter extends BaseQuickAdapter<ShopGoodsEntity, BaseViewHolder> {
    private int d5, d10;

    public MallCategoryListAdapter() {
        super(R.layout.mall_list_item);
        d5 = DeviceUtil.dip2px(LContext.getContext(), 5);
        d10 = 2 * d5;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ShopGoodsEntity item) {
        int pos = helper.getAdapterPosition() - getHeaderLayoutCount();
        View container = helper.getView(R.id.item_container);
        if (pos % 2 == 0) {
            container.setPadding(d10, 0, d5, d10);
        } else if (pos % 2 == 1) {
            container.setPadding(d5, 0, d10, d10);
        }

        RoundedImageView imageView = helper.getView(R.id.item_img);
        List<String> banner_img_txt = item.banner_img_txt;
        if (null == banner_img_txt || banner_img_txt.isEmpty()) {
            imageView.setImageResource(R.drawable.icon_max_default_pic);
        } else {
            GlideUtil.loadPic(imageView, item.getImage(), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        }

        TextView item_status = helper.getView(R.id.item_status);

        helper.setText(R.id.item_title, item.name);
        helper.setText(R.id.item_price, getPrice(item.selling_price));
        helper.setText(R.id.item_sell_count, "已抢" + NumberUtil.sellCount(item.sold) + "件");

    }

    private SpannableStringBuilder getPrice(long price) {
        String priceStr = NumberUtil.saveTwoPoint(price);
        String[] split = priceStr.split("\\.");
        SpannableStringBuilder stringBuilder = new SpanUtils()
                .append("¥").setFontSize(10, true).setBold()
                .append(split[0]).setFontSize(18, true).setBold()
                .append("." + split[1]).setFontSize(10, true).setBold()
                .create();
        return stringBuilder;
    }
}
