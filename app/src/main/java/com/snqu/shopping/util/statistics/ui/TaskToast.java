package com.snqu.shopping.util.statistics.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.util.LContext;
import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.R;
import com.snqu.shopping.util.CommonUtil;

import java.lang.reflect.Field;

/**
 * @author 张全
 */
public class TaskToast {

    public static void show(String title, String reward) {
        if (TextUtils.isEmpty(reward)) {
            return;
        }
        Context context = LContext.getContext();
        Toast toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_task_reward, null);
        TextView tv_title = view.findViewById(R.id.title);
        TextView tv_coin = view.findViewById(R.id.coin);
        TextView tv_desc = view.findViewById(R.id.desc);

        tv_title.setText(title);

        SpannableStringBuilder stringBuilder = new SpanUtils()
                .setVerticalAlign(SpanUtils.ALIGN_BASELINE)
                .append("+").setFontSize(17, true).setBold()
                .append(reward).setFontSize(25, true).setBold()
                .create();
        tv_coin.setText(stringBuilder);

        stringBuilder = new SpanUtils()
                .append("获得 ").setForegroundColor(Color.WHITE).setFontSize(15, true)
                .append(reward).setForegroundColor(Color.parseColor("#FFF80A")).setFontSize(25, true)
                .append(" 星币").setForegroundColor(Color.WHITE).setFontSize(15, true)
                .create();
        tv_desc.setText(stringBuilder);

        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);


        //点击事件
        try {
            Object mTN;
            mTN = getField(toast, "mTN");
            if (mTN != null) {
                Object mParams = getField(mTN, "mParams");
                if (mParams != null
                        && mParams instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
                    //显示与隐藏动画
//                    params.windowAnimations = R.style.ClickToast;
                    //Toast可点击
                    params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

                    mTN.getClass().getDeclaredField("mParams");
                    //设置viewgroup宽高
//                    params.width = WindowManager.LayoutParams.MATCH_PARENT; //设置Toast宽度为屏幕宽度
//                    params.height = WindowManager.LayoutParams.WRAP_CONTENT; //设置高度
                }

                view.findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtil.jumpToTaskPage(v.getContext());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        toast.show();
    }

    private static Object getField(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (field != null) {
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }

}
