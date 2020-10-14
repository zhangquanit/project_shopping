package com.snqu.shopping.ui.mall.view;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.R;
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

import java.util.List;

/**
 * 推荐-商品item
 */
public class MallGoodItemView extends RelativeLayout {
    ImageView imageView;
    TextView titleView;
    TextView tagView;
    TextView priceView;

    public MallGoodItemView(Context context) {
        super(context);
        init(context);
    }

    public MallGoodItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MallGoodItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.mall_recommend_good_item, this);
        imageView = findViewById(R.id.item_img);
        titleView = findViewById(R.id.item_title);
        tagView = findViewById(R.id.item_tag);
        priceView = findViewById(R.id.item_price);
    }

    public void setData(ShopGoodsEntity goodsBean) {
        List<String> banner_img_txt = goodsBean.banner_img_txt;
        if (null == banner_img_txt || banner_img_txt.isEmpty()) {
            imageView.setImageResource(R.drawable.icon_min_default_pic);
        } else {
            GlideUtil.loadPic(imageView, goodsBean.getImage(), R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        }

        titleView.setText(goodsBean.name);
        priceView.setText(getPrice(goodsBean.selling_price));
    }

    private SpannableStringBuilder getPrice(long price) {
        String priceStr = NumberUtil.saveTwoPoint(price);
        String[] split = priceStr.split("\\.");
        SpannableStringBuilder stringBuilder = new SpanUtils()
                .append("¥").setFontSize(10, true).setBold()
                .append(split[0]).setFontSize(14, true).setBold()
                .append("." + split[1]).setFontSize(10, true).setBold()
                .create();
        return stringBuilder;
    }

}
