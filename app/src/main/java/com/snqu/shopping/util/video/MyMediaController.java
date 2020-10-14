package com.snqu.shopping.util.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.MediaController;

/**
 * 因为默认的mediacontroller只显示3秒，因此需要修改显示时间，让它需要的时候一直显示。
 */
public class MyMediaController extends MediaController {

    public MyMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public MyMediaController(Context context) {
        super(context);
    }

    @Override
    public void show(int timeout) {
        super.show(0);
    }

}