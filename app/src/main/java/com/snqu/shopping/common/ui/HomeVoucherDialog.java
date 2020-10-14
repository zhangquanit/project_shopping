package com.snqu.shopping.common.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;



/**
 * 底部弹框
 *
 * @author 张全
 */
public class HomeVoucherDialog extends Dialog {
    public HomeVoucherDialog(@NonNull Context context) {
        super(context, common.widget.R.style.Dialog_untran);
    }

    public HomeVoucherDialog(@NonNull Context context, int themeResId) {
        super(context, common.widget.R.style.Dialog_untran);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true); // 点击屏幕Dialog以外的地方是否消失
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
//        view.post(new Runnable() {
//            @Override
//            public void run() {
//                if (view.getHeight() >= LContext.screenHeight * 0.8) {
//                    WindowManager.LayoutParams attributes = getWindow().getAttributes();
//                    attributes.height = (int) (LContext.screenHeight * 0.8);
//                    getWindow().setAttributes(attributes);
//                }
//            }
//        });
    }
}
