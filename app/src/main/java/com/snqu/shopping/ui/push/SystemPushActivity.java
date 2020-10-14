package com.snqu.shopping.ui.push;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.snqu.shopping.App;
import com.snqu.shopping.util.PushUtil;

public class SystemPushActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("push", "SystemPushActivity  onCreate");
        Intent intent = getIntent();
        if (null != intent) {
            Bundle extras = intent.getExtras();
            LogUtil.d("push", "SystemPushActivity  extras=" + extras);
            try {
                String custom = extras.getString("custom");
                LogUtil.d("push", "SystemPushActivity  value=" + custom);

                if (!TextUtils.isEmpty(custom)) {
                    PushUtil.parseMessage(custom);
                }

                if (App.devEnv) {
                    ToastUtil.showLong("来自OPPO系统推送");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            try {
//                Set<String> keySet = extras.keySet();
//                for (String key : keySet) {
//                    String value = extras.getString(key);
//                    System.out.println(key + "=" + value);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        finish();
    }
}
