package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.util.GlideUtil;

public class ChannelCircleItemView extends RelativeLayout {
    private RoundedImageView imageView;
    private TextView textView;

    public ChannelCircleItemView(Context context) {
        super(context);
        init(context);
    }

    public ChannelCircleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChannelCircleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.home_channel_list_item, this);
        imageView = findViewById(R.id.item_img);
        textView = findViewById(R.id.item_name);
    }

    public void setData(AdvertistEntity advertistEntity) {
        GlideUtil.loadPic(imageView, advertistEntity.image, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        textView.setText(advertistEntity.name);
    }
}
