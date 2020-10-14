package com.snqu.shopping.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.artical.ArticalEntity;
import com.snqu.shopping.util.GlideUtil;

import java.text.SimpleDateFormat;

public class ArticalListAdapter extends BaseQuickAdapter<ArticalEntity, BaseViewHolder> {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private LayoutInflater layoutInflater;
    private int space;

    public ArticalListAdapter(Context ctx) {
        super(R.layout.community_school_list_item);
        layoutInflater = LayoutInflater.from(ctx);
        space = DeviceUtil.dip2px(ctx, 6);
    }


    @Override
    protected void convert(BaseViewHolder helper, ArticalEntity item) {
//        item.share_wechat_open = 1;
//        ArrayList<String> list = new ArrayList<>();
//        list.add("视频教程");
//        list.add("其他");
//        item.tag = list;

        GlideUtil.loadPic(helper.getView(R.id.item_img), item.cover_image, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        helper.setText(R.id.item_title, item.title);

        String date = "";
        if (item.itime > 0) {
            date = dateFormat.format(item.itime * 1000);
        }
        helper.setText(R.id.item_time, date);

        TextView tagFlowLayout = helper.getView(R.id.item_tags);
        if (null != item.tag && !item.tag.isEmpty()) {
            tagFlowLayout.setVisibility(View.VISIBLE);
            SpanUtils spanUtils = new SpanUtils();
            for (String tag : item.tag) {
                spanUtils.append("#" + tag + "#").appendSpace(space);
            }
            tagFlowLayout.setText(spanUtils.create());
        } else {
            tagFlowLayout.setVisibility(View.GONE);
        }


        helper.setVisible(R.id.item_copy, item.share_wechat_open == 1);
        helper.addOnClickListener(R.id.item_copy);

    }

}