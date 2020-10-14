package com.snqu.shopping.ui.goods.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.gif.GifDrawable;

/**
 * desc:
 * time: 2019/9/4
 *
 * @author 银进
 */
public class ResizableImageView extends ImageView {

    public ResizableImageView(Context context) {
        super(context);
    }

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
            try {
                if (d instanceof GifDrawable) {
                    ((GifDrawable) d).stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (d.getIntrinsicWidth() < 10 || d.getIntrinsicHeight() < 10) {
                setMeasuredDimension(1, 1);
            } else {
                //高度根据使得图片的宽度充满屏幕计算而得
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
                setMeasuredDimension(width, height);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}