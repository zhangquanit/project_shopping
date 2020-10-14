package com.snqu.shopping.util.statistics.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.R;
import com.snqu.shopping.util.CommonUtil;

import common.widget.dialog.DialogView;
import common.widget.dialog.EffectDialogBuilder;

/**
 * @author 张全
 */
public class TaskRewardDialog extends DialogView {
    private String reward;
    private String title;

    public TaskRewardDialog(Context ctx, String title, String reward) {
        super(ctx);
        this.title = title;
        this.reward = reward;
    }

    @Override
    protected void initView(View view) {
        TextView tv_title = findViewById(R.id.title);
        TextView tv_coin = findViewById(R.id.coin);
        TextView tv_desc = findViewById(R.id.desc);

        tv_title.setText(title);

        SpannableStringBuilder stringBuilder = new SpanUtils()
                .setVerticalAlign(SpanUtils.ALIGN_BASELINE)
                .append("+").setFontSize(17, true).setBold()
                .append(reward).setFontSize(25, true).setBold()
                .create();
        tv_coin.setText(stringBuilder);

        stringBuilder = new SpanUtils()
                .append("获得 ").setForegroundColor(Color.WHITE).setFontSize(15, true)
                .append(reward).setForegroundColor(Color.parseColor("#FFF80A")).setFontSize(25, true)
                .append(" 星币").setForegroundColor(Color.WHITE).setFontSize(15, true)
                .create();
        tv_desc.setText(stringBuilder);

        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.jumpToTaskPage(v.getContext());
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_task_reward;
    }

    public static void show(Activity ctx, String title, String reward) {
        if (ctx.isFinishing() || ctx.isDestroyed()) {
            return;
        }
        TaskRewardDialog taskRewardDialog = new TaskRewardDialog(ctx, title, reward);
        new EffectDialogBuilder(ctx, R.style.Dialog_tran)
                .setContentView(taskRewardDialog)
                .show(true, 2000);
    }
}
