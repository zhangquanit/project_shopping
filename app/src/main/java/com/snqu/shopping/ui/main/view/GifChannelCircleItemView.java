package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.util.GlideUtil;

public class GifChannelCircleItemView extends RelativeLayout {
    private ImageView imageView;
    private TextView textView;

    public GifChannelCircleItemView(Context context) {
        super(context);
        init(context);
    }

    public GifChannelCircleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GifChannelCircleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.home_channel_gif_list_item, this);
        imageView = findViewById(R.id.item_img);
        textView = findViewById(R.id.item_name);
    }

    public void setData(AdvertistEntity advertistEntity) {
//        GlideUtil.loadRoundPic(imageView, channelEntity.icon, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        GlideUtil.loadPic(imageView, advertistEntity.image, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        textView.setText(advertistEntity.name);
    }
}
