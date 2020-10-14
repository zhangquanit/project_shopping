package com.snqu.shopping.ui.main.scan;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.snqu.shopping.R;

import common.widget.dialog.DialogView;

/**
 * 扫描内容提示框
 *
 * @author 张全
 */
public class ParseTextDialogView extends DialogView implements OnClickListener {
    private DismissListener dismissListener;
    private String text;

    public ParseTextDialogView(Context ctx, String text) {
        super(ctx);
        this.text = text;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.parse_text_dialog;
    }

    @Override
    public void initView(View view) {
        TextView tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText(text);

        findViewById(R.id.tv_know).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_know:
                dismiss();
                if (null != dismissListener) {
                    dismissListener.dismiss();
                }
                break;
        }
    }

    public void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public interface DismissListener {
        void dismiss();
    }
}
