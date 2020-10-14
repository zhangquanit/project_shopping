package com.snqu.shopping.common.ui;

import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;

public class ViewStyleSetter {

    private View mView;

    public ViewStyleSetter(View view) {
        this.mView = view;
    }

    public void setRound(float radius) {
        this.mView.setClipToOutline(true);//用outline裁剪内容区域
        this.mView.setOutlineProvider(new RoundViewOutlineProvider(radius));
    }

    public void setOval() {
        this.mView.setClipToOutline(true);//用outline裁剪内容区域
        this.mView.setOutlineProvider(new OvalViewOutlineProvider());
    }

    public void clearShapeStyle() {
        this.mView.setClipToOutline(false);
    }

    public class OvalViewOutlineProvider extends ViewOutlineProvider {


        public OvalViewOutlineProvider() {
        }

        @Override
        public void getOutline(final View view, final Outline outline) {
            Rect selfRect;
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);
            selfRect = getOvalRect(rect);
            outline.setOval(selfRect);
        }

        /**
         * 以矩形的中心点为圆心,较短的边为直径画圆
         *
         * @param rect
         * @return
         */
        private Rect getOvalRect(Rect rect) {
            int width = rect.right - rect.left;
            int height = rect.bottom - rect.top;
            int left, top, right, bottom;
            int dW = width / 2;
            int dH = height / 2;
            if (width > height) {
                left = dW - dH;
                top = 0;
                right = dW + dH;
                bottom = dH * 2;
            } else {
                left = dH - dW;
                top = 0;
                right = dH + dW;
                bottom = dW * 2;
            }
            return new Rect(left, top, right, bottom);
        }

    }

    /**
     * 给ViewGroup设置圆角效果
     */
    public class RoundViewOutlineProvider extends ViewOutlineProvider {

        private float mRadius;//圆角弧度

        public RoundViewOutlineProvider(float radius) {
            this.mRadius = radius;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            Rect rect = new Rect();
            view.getGlobalVisibleRect(rect);//将view的区域保存在rect中
            Rect selfRect = new Rect(0, 0, rect.right - rect.left, rect.bottom - rect.top);//绘制区域
            outline.setRoundRect(selfRect, mRadius);
        }
    }
}