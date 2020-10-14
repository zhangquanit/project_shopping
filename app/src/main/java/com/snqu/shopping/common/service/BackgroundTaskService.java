package com.snqu.shopping.common.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.snqu.shopping.BuildConfig;

/**
 * 后台任务
 *
 * @author 张全
 */
public class BackgroundTaskService extends Service {
    private static final String APK = "APK";

    /**
     * APK校验
     *
     * @param ctx
     * @param tag
     */
    public static void start(Context ctx, int tag) {
        Intent intent = new Intent(ctx, BackgroundTaskService.class);
        intent.putExtra(APK, tag);
        ctx.startService(intent);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            if (intent.hasExtra(APK)) {
                if (!BuildConfig.DEBUG) {
                    checkAPK();
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    //------------------------------APP安全机制------------------------------------
    private void checkAPK() {

    }


}
