package com.snqu.shopping.ui.goods.adapter;


import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.data.home.entity.CommunityRewardEntity;
import com.snqu.shopping.ui.main.view.ItemNameView;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

import java.text.SimpleDateFormat;

/**
 * @author 张全
 */
public class RewardListAdapter extends BaseQuickAdapter<CommunityRewardEntity, BaseViewHolder> {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    public RewardListAdapter() {
        super(R.layout.good_recomm_reward_item);
    }


    @Override
    protected void convert(BaseViewHolder helper, CommunityRewardEntity item) {
        TextView item_rank = helper.getView(R.id.rank);
        if (!TextUtils.isEmpty(item.rank)) {
            item_rank.setVisibility(View.VISIBLE);
            int rank = Integer.valueOf(item.rank);
            if (rank > 100) {
                item_rank.setText("商品排名:100+");
            } else {
                item_rank.setText("商品排名:" + rank);
            }
        } else {
            item_rank.setVisibility(View.GONE);
        }


        TextView item_time = helper.getView(R.id.item_time);
        TextView item_status = helper.getView(R.id.item_status);

        String rewardText = "获得：";


        if (item.status == 1 || item.status == 4) { //等待结算
            item_time.setTextColor(Color.parseColor("#25282D"));
            item_time.setText("待结算时间：" + dateFormat.format(item.set_time * 1000));

            item_status.setTextColor(Color.parseColor("#FF8202"));
            item_status.setText("待结算");
            rewardText = "预估：";

        } else if (item.status == 2) { //已结算
            item_time.setTextColor(Color.parseColor("#848487"));
            item_time.setText("结算时间：" + dateFormat.format(item.set_time * 1000));

            item_status.setTextColor(Color.parseColor("#848487"));
            item_status.setText("已结算");


        } else if (item.status == 3) { //已失效
//            item_time.setTextColor(Color.parseColor("#25282D"));
//            item_time.setText("结算时间：" + dateFormat.format(item.set_time * 1000));
            item_time.setText(null);

            item_status.setTextColor(Color.parseColor("#848487"));
            item_status.setText("已失效");
            rewardText = "预估：";
        }

        GlideUtil.loadRoundPic(helper.getView(R.id.item_img), item.item_image, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic, 5);

        ItemNameView itemNameView = helper.getView(R.id.item_name);
        itemNameView.item_title.setTextSize(14);
        itemNameView.setText(ItemSourceClient.getItemSourceName(item.item_source), item.item_title);

        helper.setText(R.id.item_recomm_time, "推荐时间：" + dateFormat.format(item.recm_itime * 1000));

        SpannableStringBuilder stringBuilder = new SpanUtils().append(rewardText)
                .setForegroundColor(Color.parseColor("#25282D")).setFontSize(12, true)
                .append("¥" + NumberUtil.saveTwoPoint(item.reward_amount)).setForegroundColor(Color.parseColor("#F34264")).setFontSize(16, true).setBold()
                .create();
        // 不正常结算的时候，显示-
        if (item.status == 1 || item.status == 4) {
            if (TextUtils.equals(item.settle_type, "2")) {
                stringBuilder = new SpanUtils().append(rewardText)
                        .setForegroundColor(Color.parseColor("#25282D")).setFontSize(12, true)
                        .append("—").setForegroundColor(Color.parseColor("#F34264")).setFontSize(16, true).setBold()
                        .create();
            }
        }
        helper.setText(R.id.item_reward, stringBuilder);

        helper.setText(R.id.item_order_count, "下单量：" + item.order_count);
    }


}
