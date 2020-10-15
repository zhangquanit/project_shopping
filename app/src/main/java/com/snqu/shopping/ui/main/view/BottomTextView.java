package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 有时候控件太多可能获取不到焦点，导致跑马灯跑不起来，因此需要重写Textview
 */
public class BottomTextView extends AppCompatTextView {
    public BottomTextView(Context context) {
        super(context);
    }

    public BottomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {  //此方法直接返回true
        return true;
    }
}