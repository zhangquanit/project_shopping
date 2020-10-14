package com.snqu.shopping.ui.mine.adapter;

import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.entity.TeamIncomeEntity;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

/**
 * 成员贡献榜
 */
public class TeamIncomeAdapter extends BaseQuickAdapter<TeamIncomeEntity, BaseViewHolder> {
    private String label;
    private int page;

    public TeamIncomeAdapter(int page) {
        super(R.layout.team_income_item);
        this.page = page;
        setType(page);
    }

    public void setType(int type) {
        if (type == 0) {
            label = "总预估佣金：";
        } else if (type == 1) {
            label = "本月预估佣金：";
        } else if (type == 2) {
            label = "近七日拉新：";
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, TeamIncomeEntity item) {
        GlideUtil.loadPic(helper.getView(R.id.item_img), item.avatar, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        ImageView tv_vip = helper.getView(R.id.item_level);
        setVipText(item.level, tv_vip);
        helper.setText(R.id.item_username, item.username);

        helper.setText(R.id.item_type_text, label);
        if (page == 2) {
            helper.setText(R.id.item_price, item.fans_all + "人");
        } else {
            helper.setText(R.id.item_price, getPrice(NumberUtil.saveTwoPoint(item.total_amount)));
        }

        if(helper.getAdapterPosition()==0){
            helper.getView(R.id.item_layout).setBackgroundResource(R.drawable.bg_top_white);
        }else{
            helper.getView(R.id.item_layout).setBackgroundResource(R.color.white);
        }

    }

    private SpannableStringBuilder getPrice(String price) {
        return new SpanUtils().append("¥").setFontSize(10, true)
                .append(price).setFontSize(13, true)
                .create();
    }

    private void setVipText(int level, ImageView tv_vip) {
        switch (level) {
            case 2:
                tv_vip.setBackgroundResource(R.drawable.icon_person_vip);
                break;
            case 3:
                tv_vip.setBackgroundResource(R.drawable.icon_person_svip);
                break;
            case 4:
                tv_vip.setBackgroundResource(R.drawable.icon_person_md);
                break;
            default:
                tv_vip.setVisibility(View.GONE);
                break;
        }
    }

}