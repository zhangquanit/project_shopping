package com.anroid.base.receiver;

import android.content.IntentFilter;

/**
 * 监听一个action的广播
 *
 * @author 张全
 */
public abstract class SimpleReceiver extends BaseReceiver {
    protected String mAction;

    public SimpleReceiver(String action) {
        this.mAction = action;
    }

    @Override
    protected IntentFilter getIntentFilter() {
        return new IntentFilter(mAction);
    }
}
