package com.snqu.shopping.ui.splash;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.android.util.log.LogUtil;
import com.snqu.shopping.data.DataConfig;

/**
 * xlts:// 回调页面
 * 淘宝、拼多多等
 *
 * @author 张全
 */
public class CallbackActivity extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        LogUtil.d("CallbackActivity", "data=" + data);
        if (null == data) {
            finish();
            return;
        }
        try {
            if (TextUtils.equals(data.getScheme(), "xkd")
                    || (DataConfig.API_HOST.contains(data.getHost()) && data.getPath().startsWith("/app"))
            ) {
                finish();
//                String param = data.getQueryParameter("param");
//                if (TextUtils.isEmpty(param)) {
//                    finish();
//                    return;
//                }
//                String pageInfo = URLDecoder.decode(param.trim(), "UTF-8");
//                DispatchUtil.goToPage(this, pageInfo);
            }
        } catch (Exception e) {
            LogUtil.e("DispatchActivity", "打开页面失败 data=" + data);
            e.printStackTrace();
        }
        finish();
    }
}
