package com.anroid.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.android.util.ext.SPUtil;
import com.android.util.os.NetworkUtil;
import com.anroid.base.service.CoreService;

/**
 * 守护广播
 *
 * @author zhangquan
 */
public class ProguardReceiver extends BroadcastReceiver {
    private static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String KEY_NETCHANGE = "KEY_NETCHANGE"; // 网络变化监听

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(CONNECTIVITY_CHANGE_ACTION)) {
            if (NetworkUtil.isNetworkAvailable(context)) {
                String lastConnected = SPUtil.getString(KEY_NETCHANGE);// 网络变化监听,防止多次监听调用,本地保存连接状态
                if (!"connected".equals(lastConnected)) {
                    SPUtil.setString(KEY_NETCHANGE, "connected");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //android8.0以上，APP处于后台时不允许打开Service
                        //先不处理
                    } else {
                        CoreService.startCoreService(context, CoreService.NETCHAGE);
                    }
                }
            } else {
                SPUtil.setString(KEY_NETCHANGE, "disconnected");
            }
        } else {
            if (action.equals(Intent.ACTION_DATE_CHANGED) || action.equals(Intent.ACTION_TIME_CHANGED)) {
            }
//			CoreService.startCoreService(context, CoreService.ACTIVE);
        }
    }


}
