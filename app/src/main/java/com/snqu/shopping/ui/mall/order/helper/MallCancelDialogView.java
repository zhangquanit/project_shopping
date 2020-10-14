package com.snqu.shopping.ui.mall.order.helper;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.snqu.shopping.R;

import common.widget.dialog.DialogView;

/**
 * 取消订单弹框
 */
public class MallCancelDialogView extends DialogView implements View.OnClickListener {
    private View.OnClickListener mLeftClickListener;
    private TextView btn_right, btn_left;
    private String content;

    public MallCancelDialogView(Context ctx) {
        super(ctx);
    }

    @Override
    protected void initView(View view) {
        btn_left = (TextView) findViewById(R.id.btn_left);
        btn_right = (TextView) findViewById(R.id.btn_right);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);

        TextView dialog_content = findViewById(R.id.dialog_content);
        if (!TextUtils.isEmpty(content)) {
            dialog_content.setText(content);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mall_cancel_dialog;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v == btn_left) {
            if (null != mLeftClickListener) {
                mLeftClickListener.onClick(v);
            }
        } else if (v == btn_right) {

        }
    }

    public MallCancelDialogView setContent(String content) {
        this.content = content;
        return this;
    }

    public MallCancelDialogView setLeftClickListener(View.OnClickListener clickListener) {
        this.mLeftClickListener = clickListener;
        return this;
    }
}
