package com.snqu.shopping.common.ui;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RecycleView的item间隔，使用于LinearLayoutManager
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int oriention;
    private boolean firstSpace;

    public SpacesItemDecoration(int space, int oriention) {
        this.space = space;
        this.oriention = oriention;
    }

    public SpacesItemDecoration(int space, int oriention, boolean firstSpace) {
        this.space = space;
        this.oriention = oriention;
        this.firstSpace = firstSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        if (oriention == LinearLayoutManager.HORIZONTAL) { //水平
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = firstSpace ? space : 0;
            } else {
                outRect.left = space;
            }
        } else { //垂直
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = firstSpace ? space : 0;
            } else {
                outRect.top = space;
            }
        }
    }
}