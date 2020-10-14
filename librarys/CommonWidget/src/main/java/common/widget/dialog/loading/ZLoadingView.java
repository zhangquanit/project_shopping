package common.widget.dialog.loading;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import common.widget.R;

/**
 * Created by zyao89 on 2017/3/19.
 * Contact me at 305161066@qq.com or zyao89@gmail.com
 * For more projects: https://github.com/zyao89
 * My Blog: http://zyao89.me
 */
public class ZLoadingView extends ImageView {
    private ZLoadingDrawable mZLoadingDrawable;
    protected ZLoadingBuilder mZLoadingBuilder;

    public ZLoadingView(Context context) {
        this(context, null);
    }

    public ZLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ZLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        try {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ZLoadingView);
            int typeId = ta.getInt(R.styleable.ZLoadingView_z_type, 0);
            int color = ta.getColor(R.styleable.ZLoadingView_z_color, Color.BLACK);
            ta.recycle();
            setLoadingBuilder(Z_TYPE.values()[typeId]);
            setColorFilter(color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLoadingBuilder(@NonNull Z_TYPE builder) {
        mZLoadingBuilder = builder.newInstance();
        mZLoadingDrawable = new ZLoadingDrawable(mZLoadingBuilder);
        mZLoadingDrawable.initParams(getContext());
        setImageDrawable(mZLoadingDrawable);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        final boolean visible = visibility == VISIBLE && getVisibility() == VISIBLE;
        if (visible) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    private void startAnimation() {
        if (mZLoadingDrawable != null) {
            mZLoadingDrawable.start();
        }
    }

    private void stopAnimation() {
        if (mZLoadingDrawable != null) {
            mZLoadingDrawable.stop();
        }
    }
}
