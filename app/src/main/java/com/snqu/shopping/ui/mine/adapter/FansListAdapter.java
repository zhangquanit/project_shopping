package com.snqu.shopping.ui.mine.adapter;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.entity.FansEntity;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

public class FansListAdapter extends BaseQuickAdapter<FansEntity, BaseViewHolder> {
    private boolean hideArrow;

    public FansListAdapter() {
        super(R.layout.fans_item);
    }

    public void hideArrow() {
        hideArrow = true;
    }

    public void showArrow(){
        hideArrow = false;
    }

    @Override
    protected void convert(BaseViewHolder helper, FansEntity item) {
        ImageView imageView = helper.getView(R.id.item_img);
        GlideUtil.loadPic(imageView, item.avatar, R.drawable.icon_default_head, R.drawable.icon_default_head);
        helper.setText(R.id.tv_nickname, item.username);

        SpannableStringBuilder stringBuilder = new SpanUtils().append("粉丝人数").setForegroundColor(Color.parseColor("#25282D"))
                .append(" " + item.fans_all).setForegroundColor(Color.parseColor("#FF8202"))
                .create();
        helper.setText(R.id.fans_onetwo, stringBuilder);

        stringBuilder = new SpanUtils().append("累计预估收益").setForegroundColor(Color.parseColor("#25282D"))
                .append(" ¥" + NumberUtil.saveTwoPoint(item.estimate_total)).setForegroundColor(Color.parseColor("#FF8202"))
                .create();
        helper.setText(R.id.item_income, stringBuilder);

        ImageView tv_vip = helper.getView(R.id.iv_vip);
        setVipText(item.level, tv_vip);

        helper.setGone(R.id.tv_recent, TextUtils.equals(item.recent, "1"));

        helper.setGone(R.id.item_arrow, false);

    }

    public static void setVipText(int level, ImageView tv_vip) {
        switch (level) {
            case 2:
                tv_vip.setImageResource(R.drawable.icon_person_vip);
                break;
            case 3:
                tv_vip.setImageResource(R.drawable.icon_person_svip);
                break;
            case 4:
                tv_vip.setImageResource(R.drawable.icon_person_md);
                break;
            default:
                tv_vip.setImageDrawable(null);
                break;
        }
    }

    private SpannableStringBuilder getSpanText(String text1, String text2) {
        return new SpanUtils().append(text1).setForegroundColor(Color.parseColor("#A5A5A6"))
                .append(text2).setForegroundColor(Color.parseColor("#25282D"))
                .create();
    }

}