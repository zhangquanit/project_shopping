package com.snqu.shopping.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.util.log.LogUtil;
import com.google.gson.Gson;
import com.snqu.shopping.data.user.entity.PushEntity;
import com.snqu.shopping.util.PushUtil;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

public class UmengPushActivity extends UmengNotifyClickActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        onMessage(getIntent());
    }

    /**
     * 华为、opoo、vivo、小米对后台进程做了诸多限制。若使用一键清理，应用的channel进程被清除，将接收不到推送。为了增加推送的送达率，可选择接入华为托管弹窗功能。通知将由华为系统托管弹出，点击通知栏将跳转到指定的Activity。该Activity需继承自UmengNotifyClickActivity，同时实现父类的onMessage方法，对该方法的intent参数进一步解析即可，该方法异步调用，不阻塞主线程。
     *
     * @param intent
     */
    @Override
    public void onMessage(Intent intent) {
        if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey((AgooConstants.MESSAGE_BODY)))
        {
            //此方法必须调用，否则无法统计打开数
            super.onMessage(intent);
            String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            LogUtil.d("onMessage=" + body);
            if (!TextUtils.isEmpty(body)) {
                try {
                    JSONObject jsonObject = new JSONObject(body);
                    String message = jsonObject.optString("extra");
                    if (!TextUtils.isEmpty(message)) {
                        jsonObject = new JSONObject(message);
                        message = jsonObject.optString("body");
                        if (!TextUtils.isEmpty(message)) {
                            PushUtil.umengDataAdd(message);
                            PushEntity pushEntity = new Gson().fromJson(message, PushEntity.class);
                            if (pushEntity != null) {
                                Intent mainIntent = new Intent(this, MainActivity.class);
                                mainIntent.setAction("push");
                                mainIntent.putExtra("push_data", pushEntity);
                                startActivity(mainIntent);
                            }
                        }
                    }
                } catch (JSONException e) {
                    LogUtil.e("推送json解析错误");
                }
            }
            finish();
        }
    }
}