package com.snqu.shopping.ui.mine.adapter;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.entity.TutorShareContract;
import com.snqu.shopping.util.DateUtil;
import com.snqu.shopping.util.GlideUtil;

/**
 * @author liuming
 */
public class MeTutorShareAdapter extends BaseQuickAdapter<TutorShareContract, BaseViewHolder> {

    private String mStatus; // 状态，空代表全部文档，为2代表展示文档

    public MeTutorShareAdapter(String status) {
        super(R.layout.item_me_tutor_share);
        this.mStatus = status;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TutorShareContract item) {
        if (!TextUtils.isEmpty(item.logo)) {
            GlideUtil.loadPic(helper.getView(R.id.item_icon), item.logo, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        }

        if (!TextUtils.isEmpty(item.title)) {
            helper.setText(R.id.item_title, item.title);
        }

        if (item.itime > 0) {
            helper.setText(R.id.item_time, "创建于 " + DateUtil.getFriendlyTimeStr2(item.itime * 1000L));
        }

        helper.addOnClickListener(R.id.icon_top);
        helper.addOnClickListener(R.id.icon_bottom);
        helper.addOnClickListener(R.id.item_more);

//        if(helper.getAdapterPosition()==0){
//            item.is_top = 1;
//        }

        View icon_top = helper.getView(R.id.icon_top);
        View icon_bottom = helper.getView(R.id.icon_bottom);
        View tv_top_tip = helper.getView(R.id.tv_top_tip);
        View icon_look = helper.getView(R.id.icon_look);


        if (TextUtils.equals(mStatus, "2")) {
            icon_top.setVisibility(View.VISIBLE);
            icon_bottom.setVisibility(View.VISIBLE);
            icon_look.setVisibility(View.GONE);
            tv_top_tip.setVisibility(View.GONE);

            //代表是最后一个
            if (helper.getAdapterPosition() == getData().size() - 1) {
                icon_top.setVisibility(View.VISIBLE);
                icon_bottom.setVisibility(View.GONE);
            }
            //代表置顶
            if (item.is_top != 0) {
                tv_top_tip.setVisibility(View.VISIBLE);
                icon_top.setVisibility(View.GONE);
                icon_bottom.setVisibility(View.GONE);
            } else {
                if (getData().size() == 1) {
                    icon_top.setVisibility(View.GONE);
                    icon_bottom.setVisibility(View.GONE);
                }
            }
            //代表是第一个
            if (item.isFirst) {
                icon_top.setVisibility(View.GONE);
                icon_bottom.setVisibility(View.VISIBLE);
            }
            //如果只有一个，则不能移动
            if(getData().size()==1){
                icon_top.setVisibility(View.GONE);
                icon_bottom.setVisibility(View.GONE);
            }
        } else {
            icon_top.setVisibility(View.GONE);
            icon_bottom.setVisibility(View.GONE);
            tv_top_tip.setVisibility(View.GONE);
            //是否展示
            if (item.status == 2) {
                icon_look.setVisibility(View.VISIBLE);
            } else {
                icon_look.setVisibility(View.GONE);
            }
        }
    }

}
