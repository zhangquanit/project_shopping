package cn.jzvd;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * desc:
 * time: 2019/9/4
 *
 * @author 银进
 */
public class JCResizableImageView extends ImageView {

    public JCResizableImageView(Context context) {
        super(context);
    }

    public JCResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable d = getDrawable();
        if (d != null) {
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