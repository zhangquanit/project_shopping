package com.snqu.shopping.ui.main.scan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
public class UrlOpenDialogView extends DialogView implements OnClickListener {
    private DismissListener dismissListener;
    private String url;

    public UrlOpenDialogView(Context ctx, String url) {
        super(ctx);
        this.url = url;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.scan_url_open_dialog;
    }

    @Override
    public void initView(View view) {
        TextView tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText(url);

        findViewById(R.id.btn_open).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open:

                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(url));
                v.getContext().startActivity(intent);

                dismiss();
                if (null != dismissListener) {
                    dismissListener.dismiss();
                }
                break;
            case R.id.btn_cancel:
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
