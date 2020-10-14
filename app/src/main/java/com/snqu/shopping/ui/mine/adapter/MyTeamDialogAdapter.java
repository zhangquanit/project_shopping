package com.snqu.shopping.ui.mine.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.VipTaskEntity;
import com.snqu.shopping.util.NumberUtil;

import java.util.List;

public class MyTeamDialogAdapter extends BaseQuickAdapter<VipTaskEntity.Rule, BaseViewHolder> {

    public MyTeamDialogAdapter(@Nullable List<VipTaskEntity.Rule> data) {
        super(R.layout.item_myteam_dialog, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, VipTaskEntity.Rule item) {
        VipTaskEntity.Rule rule = item;
        SpanUtils spanUtils = new SpanUtils();
        spanUtils.append(String.valueOf(rule.has_value)).setForegroundColor(Color.parseColor("#FF25282D")).setFontSize(12, true)
                .append("/" + rule.max_value)
                .setForegroundColor(Color.parseColor("#FFA5A5A6")).setFontSize(12, true);
        helper.setText(R.id.item_tip, spanUtils.create());
        helper.setText(R.id.item_tip_title, rule.desc);
        ProgressBar progressBar = helper.getView(R.id.progress_bar);

        if (!TextUtils.isEmpty(rule.max_value)) {
            if (rule.max_value.contains(".") || rule.has_value.contains(".")) {
                double bd = NumberUtil.getDoubleTwo(item.max_value, item.has_value).getFirst();
                int progress = (int) bd;
                progressBar.setMax(progress);
                bd = NumberUtil.getDoubleTwo(item.max_value, item.has_value).getSecond();
                progress = (int) bd;
                progressBar.setProgress(progress);
            } else {
                progressBar.setProgress(Integer.parseInt(rule.has_value));
                progressBar.setMax(Integer.parseInt(rule.max_value));
            }
        }
    }
}
