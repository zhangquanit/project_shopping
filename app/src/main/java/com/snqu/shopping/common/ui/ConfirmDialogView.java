package com.snqu.shopping.common.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.snqu.shopping.R;

import common.widget.dialog.DialogView;

/**
 * 确认对话框
 *
 * @author 张全
 */
public class ConfirmDialogView extends DialogView implements OnClickListener {
    private ConfirmListener listener;
    private String content;
    private String sureTxt;
    private String cancelTxt;

    public ConfirmDialogView(Context ctx) {
        super(ctx);
    }

    public ConfirmDialogView(Context ctx, String content, ConfirmListener listener) {
        super(ctx);
        this.listener = listener;
        this.content = content;
    }

    public ConfirmDialogView(Context ctx, String content, String sureTxt,
                             String cancelTxt, ConfirmListener listener) {
        super(ctx);
        this.listener = listener;
        this.content = content;
        this.sureTxt = sureTxt;
        this.cancelTxt = cancelTxt;
    }

    @Override
    protected int getLayoutId() {

        return R.layout.dialog_confirm;
    }

    @Override
    public void initView(View view) {
        TextView tv_cancel = findViewById(R.id.dialog_cancel);
        tv_cancel.setOnClickListener(this);
        if (!TextUtils.isEmpty(cancelTxt)) {
            tv_cancel.setText(cancelTxt);
        }
        TextView tv_sure = findViewById(R.id.dialog_yes);
        tv_sure.setOnClickListener(this);
        if (!TextUtils.isEmpty(sureTxt)) {
            tv_cancel.setText(sureTxt);
        }

        TextView tvContent = findViewById(R.id.dialog_content);
        tvContent.setText(content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel:
                dismiss();
                if (null != listener) {
                    listener.cancel();
                }
                break;
            case R.id.dialog_yes:
                dismiss();
                if (null != listener) {
                    listener.sure();
                    break;
                }
        }
    }
}
