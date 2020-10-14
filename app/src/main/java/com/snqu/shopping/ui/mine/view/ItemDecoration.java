package com.snqu.shopping.ui.mine.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.snqu.shopping.R;

public class ItemDecoration
        extends RecyclerView.ItemDecoration {
    private Context mContext;
    private final Paint mPaint;
    private final Resources mRes;

    public ItemDecoration(Context context) {
        mContext = context;
        mRes = mContext.getResources();
        mPaint = new Paint();
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPaint.setColor(mRes.getColor(R.color.c_f8fafa, null));
        } else {
            mPaint.setColor(mRes.getColor(R.color.c_f8fafa));
        }
    }

    public ItemDecoration(Context context, int color) {
        mContext = context;
        mRes = mContext.getResources();
        mPaint = new Paint();
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
        mPaint.setColor(ContextCompat.getColor(mContext, color));
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int count = parent.getChildCount();
        for (int i = 1; i < count; i++) {
            View view = parent.getChildAt(i);
            c.drawLine(parent.getPaddingLeft(), view.getTop(),
                    parent.getMeasuredWidth(), view.getTop(), mPaint);
        }
    }
}