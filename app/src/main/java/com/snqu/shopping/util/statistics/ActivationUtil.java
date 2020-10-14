package com.snqu.shopping.util.statistics;

import com.android.util.LContext;
import com.android.util.ext.SPUtil;
import com.android.util.log.LogUtil;
import com.snqu.shopping.data.base.RestClient;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ActivationUtil {
    private static final String TAG = "ActivationUtil";
    private static final String ACTIVE = "ACTIVE_APP";
    private static String URL = "http://accept.sdmp.sndo.com/data.php";

    public static void activate() {
        boolean active = SPUtil.getBoolean(ACTIVE, false);
        LogUtil.d(TAG, "active=" + active);
        if (!active) {
            URL = URL + "?c=" + LContext.channel + "&deviceId=" + AnalysisUtil.getUniqueId();
            Request request = new Request.Builder().url(URL)
                    .get()
                    .build();
            Call call = RestClient.getHttpClient().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, "APPA渠道激活汇报 失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    LogUtil.d(TAG, "APPA渠道激活汇报 成功");
                    SPUtil.setBoolean(ACTIVE, true);
                }
            });
        }
    }
}
