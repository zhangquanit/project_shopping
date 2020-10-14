package com.snqu.shopping.ui.main.frag.channel.reds.adapter;

import android.text.SpannableStringBuilder;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.red.entity.BigVEntity;
import com.snqu.shopping.ui.main.frag.channel.reds.view.BigVItemView;
import com.snqu.shopping.util.GlideUtil;

/**
 * @author 张全
 */
public class BigVAdapter extends BaseQuickAdapter<BigVEntity, BaseViewHolder> {
    int margin;

    public BigVAdapter() {
        super(R.layout.reds_big_v_item);
        margin = DeviceUtil.dip2px(LContext.getContext(), 3);
    }

    @Override
    protected void convert(BaseViewHolder helper, BigVEntity item) {
        GlideUtil.loadPic(helper.getView(R.id.item_img), item.avatar, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        helper.setText(R.id.item_name, item.name);
        helper.setText(R.id.item_intro, getIntro(item.getSourceDrawable(), item.getSourceText()));

        if (null == item.good_info || item.good_info.isEmpty()) {
            helper.setGone(R.id.item_goods1, false);
            helper.setGone(R.id.item_goods2, false);
        } else {
            helper.setGone(R.id.item_goods1, true);
            BigVItemView bigVItemView = helper.getView(R.id.item_goods1);
            bigVItemView.setData(item.good_info.get(0));

            if (item.good_info.size() > 1) {
                helper.setGone(R.id.item_goods2, true);
                bigVItemView = helper.getView(R.id.item_goods2);
                bigVItemView.setData(item.good_info.get(1));
            } else {
                helper.setGone(R.id.item_goods2, false);
            }
        }

        helper.addOnClickListener(R.id.item_more);
        helper.addOnClickListener(R.id.item_top);
    }

    private SpannableStringBuilder getIntro(int res, String intro) {
        if (res == -1) { //没找到图片
            return new SpanUtils()
                    .append(intro)
                    .create();
        }
        return new SpanUtils()
                .appendImage(res, SpanUtils.ALIGN_CENTER)
                .appendSpace(margin)
                .append(intro)
                .create();
    }
}
