package com.snqu.shopping.ui.main.scan;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.snqu.shopping.R;
import com.snqu.shopping.ui.main.frag.search.SearchFrag;

import common.widget.dialog.DialogView;

/**
 * 剪贴板搜索
 *
 * @author 张全
 */
public class ClipboardSearchDialogView extends DialogView {
    private String search;
    private String itemSource;

    public ClipboardSearchDialogView(Context ctx, String search,String itemSource) {
        super(ctx);
        this.search = search;
        this.itemSource = itemSource;
    }

    @Override
    protected void initView(View view) {


        TextView textView = findViewById(R.id.text);
        textView.setText(search);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开搜索页
                SearchFrag.startFromSearch(getContext(), search,itemSource);
                dismiss();
            }
        });
        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.clipboard_search_dialog;
    }
}
