package com.snqu.shopping.util.statistics;

import com.android.util.LContext;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * 友盟统计
 *
 * @author 张全
 */
public class UmengAnalysisUtil {
    //微信登录失败
    public static final String WEIXIN_LOGIN = "weixin_login";

    public static void onEvent(String eventName, String... keyvalues) {
        if (LContext.isDebug) {
            return;
        }
        try {
            if (null == keyvalues || keyvalues.length == 0) {
                MobclickAgent.onEvent(LContext.getContext(), eventName);
            } else {
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < keyvalues.length; i += 2) {
                    map.put(keyvalues[i], keyvalues[i + 1]);
                }
                MobclickAgent.onEvent(LContext.getContext(), eventName, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
