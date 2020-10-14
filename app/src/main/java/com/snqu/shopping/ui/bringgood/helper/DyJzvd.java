package com.snqu.shopping.ui.bringgood.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.snqu.shopping.ui.goods.player.MyJzVideo;

/**
 * @author 张全
 */
public class DyJzvd extends MyJzVideo {
    public DyJzvd(Context context) {
        super(context);
        init();
    }

    public DyJzvd(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        textureViewContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE_PLAYING) {
                    startButton.performClick();
                } else if (state == STATE_PAUSE) {
                    startButton.performClick();
                    startButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
