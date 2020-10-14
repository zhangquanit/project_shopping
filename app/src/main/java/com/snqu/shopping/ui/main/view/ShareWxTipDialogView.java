package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.view.View;

import com.snqu.shopping.R;

import common.widget.dialog.DialogView;
import common.widget.dialog.EffectDialogBuilder;

/**
 * @author 张全
 */
public class ShareWxTipDialogView extends DialogView {
    View.OnClickListener onClickListener;

    public static void show(Context ctx, View.OnClickListener listener) {
        ShareWxTipDialogView shareWxTipDialogView = new ShareWxTipDialogView(ctx);
        shareWxTipDialogView.setOnClickListener(listener);
        new EffectDialogBuilder(ctx)
                .setContentView(shareWxTipDialogView)
                .show();
    }

    public ShareWxTipDialogView(Context ctx) {
        super(ctx);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    @Override
    protected void initView(View view) {

        findViewById(R.id.tv_know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != onClickListener) {
                    onClickListener.onClick(v);
                }

            }
        });

        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.community_wx_tip_dialog;
    }
}
