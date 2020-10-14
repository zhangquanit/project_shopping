package com.snqu.shopping.ui.mine.adapter;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.data.user.entity.FansEntity;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;

public class MyTeamFansListAdapter extends BaseQuickAdapter<FansEntity, BaseViewHolder> {
    private boolean hideArrow;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public MyTeamFansListAdapter() {
        super(R.layout.myteam_fans_item);
    }

    public void hideArrow() {
        hideArrow = true;
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
        helper.setGone(R.id.item_arrow, !hideArrow);


        if (TextUtils.isEmpty(item.inviter_username)) {
            helper.getView(R.id.item_inviater).setVisibility(View.INVISIBLE);
        } else {
            helper.getView(R.id.item_inviater).setVisibility(View.VISIBLE);
            stringBuilder = new SpanUtils().append("邀请人")
                    .append(" " + item.inviter_username)
                    .create();
            helper.setText(R.id.item_inviater, stringBuilder);
        }


        if (item.itime == 0) {
            helper.setText(R.id.item_time, "");
        } else {
            try {
                helper.setText(R.id.item_time, format.format(item.itime * 1000));
            } catch (Exception e) {
                helper.setText(R.id.item_time, "");
            }
        }

        helper.getView(R.id.item_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PushEvent(Constant.Event.MY_TEAM_CLICK, item));
            }
        });

        helper.getView(R.id.progress_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.level != 4) {
                    helper.getView(R.id.item_btn).performClick();
                } else {
                    if (helper.getLayoutPosition() - 1 >= 0) {
                        EventBus.getDefault().post(new PushEvent(Constant.Event.MY_TEAM_ITEM_CLICK, helper.getLayoutPosition() - 1));
                    }
                }
            }
        });

        //不是运营总监的时候
        if (item.level == 4) {
            helper.getView(R.id.tip_layout).setVisibility(View.GONE);
            helper.getView(R.id.item_btn).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.item_btn).setVisibility(View.VISIBLE);
            TextView item_tip = helper.getView(R.id.item_tip);
            ProgressBar progressBar = helper.getView(R.id.progress);
            if (TextUtils.isEmpty(item.process_explain)) {
                helper.getView(R.id.tip_layout).setVisibility(View.GONE);
            } else {
                helper.getView(R.id.tip_layout).setVisibility(View.VISIBLE);
                item_tip.setText(item.process_explain);
            }
            int progress = (int) (NumberUtil.getDouble(String.valueOf(item.process)));
            progressBar.setProgress(Math.min(progress, 100));
        }

        if (TextUtils.equals(item.recommed, "1")) {
            helper.getView(R.id.view_recommend).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.view_recommend).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(item.register_from) && TextUtils.equals(item.register_from, "4")) {
            helper.getView(R.id.wechat_tip).setVisibility(View.VISIBLE);
            helper.getView(R.id.progress_layout).setVisibility(View.GONE);
        } else {
            helper.getView(R.id.wechat_tip).setVisibility(View.GONE);
            helper.getView(R.id.progress_layout).setVisibility(View.VISIBLE);
        }
        //代表注销
        helper.getView(R.id.icon_user_cancel).setVisibility(View.GONE);
        if (!TextUtils.isEmpty(item.status)) {
            if (TextUtils.equals(item.status, "-1")) {
                helper.getView(R.id.icon_user_cancel).setVisibility(View.VISIBLE);
            }
        }
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