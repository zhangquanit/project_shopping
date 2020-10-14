package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.util.GlideUtil;

public class ChannelItemView extends RelativeLayout {
    private ImageView imageView;
    private TextView textView;

    public ChannelItemView(Context context) {
        super(context);
        init(context);
    }

    public ChannelItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChannelItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.market_channel_list_item, this);
        imageView = findViewById(R.id.item_img);
        textView = findViewById(R.id.item_name);
    }

    public void setData(CategoryEntity categoryEntity) {
        if (TextUtils.equals(categoryEntity.getName(), LContext.getString(R.string.watch_more))) {
            imageView.setImageResource(R.drawable.icon_more);
            textView.setText(categoryEntity.getName());
            setImageSize(35);
        } else {
            GlideUtil.loadPic(imageView, categoryEntity.icon, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
            textView.setText(categoryEntity.getName());
        }
    }

    public void setImageSize(int size) {
        LayoutParams layoutParams = (LayoutParams) imageView.getLayoutParams();
        layoutParams.width = DeviceUtil.dip2px(getContext(), size);
        layoutParams.height = DeviceUtil.dip2px(getContext(), size);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageView.setLayoutParams(layoutParams);
    }
}
