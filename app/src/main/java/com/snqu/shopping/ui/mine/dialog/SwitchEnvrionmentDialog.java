package com.snqu.shopping.ui.mine.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.snqu.shopping.App;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.UserClient;

/**
 * 切换环境
 *
 * @author 张全
 */
public class SwitchEnvrionmentDialog extends Dialog {

    public SwitchEnvrionmentDialog(Context context) {
        super(context, R.style.Dialog_untran);
        init(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((WindowManager.LayoutParams) params);

    }

    public void init(Context context) {
        View view = View.inflate(context, R.layout.environment_dialog, null);
        setContentView(view);

        TextView api_online = view.findViewById(R.id.api_online);
        TextView api_test = view.findViewById(R.id.api_test);
        TextView api_dev = view.findViewById(R.id.api_dev);
        TextView api_preDev = view.findViewById(R.id.api_predev);

        int dev = App.getDev();
        if (dev == 0) {
            api_online.setSelected(true);
            api_online.setText("正式环境(当前)");
            api_online.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if (dev == 1) {
            api_test.setSelected(true);
            api_test.setText("测试环境(当前)");
            api_test.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if (dev == 2) {
            api_dev.setSelected(true);
            api_dev.setText("开发环境(当前)");
            api_dev.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else if (dev == 3) {
            api_preDev.setSelected(true);
            api_preDev.setText("预发环境(当前)");
            api_preDev.setBackgroundColor(Color.parseColor("#4CAF50"));
        }
        api_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDev(0);
            }
        });

        api_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDev(1);
            }
        });

        api_dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDev(2);
            }
        });
        api_preDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeDev(3);
            }
        });
    }


    private void changeDev(int dev) {
        App.setDev(dev);
        UserClient.loginOut();
        Process.killProcess(Process.myPid());
    }
}
