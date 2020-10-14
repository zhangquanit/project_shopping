package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.snqu.shopping.R;

import common.widget.dialog.DialogView;

/**
 * @author 张全
 */
public class TipDialogView extends DialogView {
    String title, content;
    int gravity;
    private boolean emiste;//预估收入说明
    View.OnClickListener onClickListener;

    public TipDialogView(Context ctx, String title, String content) {
        super(ctx);
        this.title = title;
        this.content = content;
        this.gravity = gravity;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setEmite() {
        emiste = true;
    }

    @Override
    protected void initView(View view) {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);

        TextView tv_content = findViewById(R.id.tv_content);
        tv_content.setText(content);
//        tv_content.setGravity(gravity);

        TextView tv_know = findViewById(R.id.tv_know);
        if (emiste) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv_know.getLayoutParams();
            layoutParams.topMargin = DeviceUtil.dip2px(LContext.getContext(), 5);
            layoutParams.bottomMargin = DeviceUtil.dip2px(LContext.getContext(), 10);
            tv_know.setLayoutParams(layoutParams);
            tv_content.setTextSize(13);
        }
        tv_know.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != onClickListener) {
                    onClickListener.onClick(v);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.tip_dialog;
    }
}
