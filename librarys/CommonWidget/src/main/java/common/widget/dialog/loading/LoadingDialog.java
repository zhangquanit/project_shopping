package common.widget.dialog.loading;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import common.widget.R;
import common.widget.dialog.EffectDialogBuilder;

/**
 * 加载进度框
 *
 * @author zhangquan
 */
public class LoadingDialog {
    private EffectDialogBuilder builder;
    private LoadingLoadingView dialogView;

    public LoadingDialog(Context ctx, String text) {
        this(ctx, text, false);
    }

    public LoadingDialog(Context ctx, String text, boolean cancelable) {
        builder = new EffectDialogBuilder(ctx, R.style.Dialog_tran);
        builder.setCancelableOnTouchOutside(cancelable);
        builder.setCancelable(cancelable);
        dialogView = new LoadingLoadingView(
                ctx, text);
        builder.setContentView(dialogView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window win = builder.mDialog.getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明状态栏
            // 状态栏字体设置为深色，SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 为SDK23增加
            win.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            // 部分机型的statusbar会有半透明的黑色背景
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            win.setStatusBarColor(Color.TRANSPARENT);// SDK21
        }
    }

    public LoadingDialog show() {
//        dialogView.startAnimal();
        builder.show();
        return this;
    }

    public boolean isShowing() {
        return builder.isShowing();
    }

    public LoadingDialog setCancelableOnTouchOutside(boolean cancelable) {
        builder.setCancelableOnTouchOutside(cancelable);
        return this;
    }

    public LoadingDialog setCancelable(boolean cancelable) {
        builder.setCancelable(cancelable);
        return this;
    }

    public LoadingDialog dismiss() {
        dialogView.stop();
        builder.dismiss();
        return this;
    }

    public static LoadingDialog showDialog(Context context, String text) {
        return new LoadingDialog(context, text).show();
    }

    public static LoadingDialog showBackCancelableDialog(Context context, String text) {
        return new LoadingDialog(context, text).setCancelableOnTouchOutside(false).setCancelable(true).show();
    }

    public static LoadingDialog showCancelableDialog(Context context, String text) {
        return new LoadingDialog(context, text, true).show();
    }
}
