package com.snqu.shopping.ui.splash;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.android.util.log.LogUtil;
import com.snqu.shopping.data.DataConfig;
import com.snqu.shopping.util.DispatchUtil;

import java.net.URLDecoder;

/**
 * DeepLink解析
 *
 * @author 张全
 */
public class HttpUrlActivity extends Activity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        LogUtil.d("DispatchActivity", "HttpUrlActivity，data=" + data);
        if (null == data) {
            finish();
            return;
        }
        try {
            if (DataConfig.API_HOST.contains(data.getHost()) && data.getPath().startsWith("/app")
            ) {
                String param = data.getQueryParameter("param");
                if (TextUtils.isEmpty(param)) {
                    finish();
                    return;
                }
                String pageInfo = URLDecoder.decode(param.trim(), "UTF-8");
                DispatchUtil.goToPage(this, pageInfo);
            }
        } catch (Exception e) {
            LogUtil.e("DispatchActivity", "HttpUrlActivity，打开页面失败 data=" + data);
            e.printStackTrace();
        }
        finish();
    }
}
