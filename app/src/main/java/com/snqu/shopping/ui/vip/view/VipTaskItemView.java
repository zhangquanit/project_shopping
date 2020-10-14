package com.snqu.shopping.ui.vip.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.VipTaskEntity;

/**
 * VIP任务进度
 *
 * @author 张全
 */
public class VipTaskItemView extends RelativeLayout {
    private TextView tv_title;
    private TextView tv_progress;
    private ProgressBar pb;

    public VipTaskItemView(Context context) {
        super(context);
        init(context);
    }

    public VipTaskItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VipTaskItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.vip_task_item, this);
        tv_title = findViewById(R.id.tv_title);
        tv_progress = findViewById(R.id.tv_progress);
        pb = findViewById(R.id.pb);
    }

    public void setData(VipTaskEntity.Rule rule) {
        tv_title.setText(rule.desc);


        if (!TextUtils.isEmpty(rule.max_value) && !TextUtils.isEmpty(rule.has_value)) {
            tv_progress.setText(rule.has_value + "/" + rule.max_value);
            double max_value = Double.valueOf(rule.max_value);
            double has_value = Double.valueOf(rule.has_value);
            int max = (int) (max_value * 100);
            int now = (int) (has_value * 100);
            int progress = now;
            if (progress >= max) {
                progress = max;
            }
            pb.setMax(max);
            pb.setProgress(progress);
        }
    }


}
