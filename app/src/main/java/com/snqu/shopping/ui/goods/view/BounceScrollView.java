package com.snqu.shopping.ui.goods.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.core.widget.NestedScrollView;

/**
 * desc:
 * time: 2019/9/17
 *
 * @author 银进
 */
public class BounceScrollView extends NestedScrollView {
    private boolean isCalled;
    private Callback mCallback;
    /** 包含的View */
    private View mView;
    /** 存储正常时的位置 */
    private Rect mRect = new Rect();
    /**  y坐标 */
    private int y;
    private boolean isFirst = true;

    public BounceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @重写方法名:onFinishInflate
     * @父类:@see android.view.View#onFinishInflate()
     * @方法说明:根据 XML 生成视图工作完成.该函数在生成视图的最后调用，在所有子视图添加完之后. 即使子类覆盖了 onFinishInflate
     * 方法，也应该调用父类的方法，使该方法得以执行.
     */
    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0)
            mView = getChildAt(0);
        super.onFinishInflate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mView != null) {
            commonOnTouch(ev);
        }
        return super.onTouchEvent(ev);
    }

    private void commonOnTouch(MotionEvent ev) {
        int action = ev.getAction();
        int cy = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            /** 跟随手指移动 */
            case MotionEvent.ACTION_MOVE:

                int dy = cy - y;
                if (isFirst) {
                    dy = 0;
                    isFirst = false;
                }
                y = cy;

                if (isNeedMove()) {
                    if (mRect.isEmpty()) {
                        /**  记录移动前的位置 */
                        mRect.set(mView.getLeft(), mView.getTop(),
                                mView.getRight(), mView.getBottom());
                    }

                    mView.layout(mView.getLeft(), mView.getTop() + 2 * dy / 3,
                            mView.getRight(), mView.getBottom() + 2 * dy / 3);

                    if (shouldCallBack(dy)) {
                        if (mCallback != null) {
                            if (!isCalled) {
                                isCalled = true;
                                resetPosition();
//                                mCallback.callback();
                            }
                        }
                    }
                }

                break;
            /** 反弹回去 */
            case MotionEvent.ACTION_UP:
                if (!mRect.isEmpty()) {
                    Log.e("计算结果1==", mRect.bottom + "");
                    Log.e("计算结果2==", mView.getBottom() + "");
                    if ((mRect.bottom - mView.getBottom()) > 200) {
                        mCallback.callback();
                    }
                    resetPosition();
                }
                break;

        }
    }

    /**
     * @方法说明:当从上往下，移动距离达到一半时，回调接口
     * @方法名称:shouldCallBack
     * @param dy
     * @return
     * @返回值:boolean
     */
    private boolean shouldCallBack(int dy) {
        if (dy > 0 && mView.getTop() >200 / 2)
            return true;
        return false;
    }

    /**
     * @方法说明:重置坐标
     * @方法名称:resetPosition
     * @返回值:void
     */
    private void resetPosition() {
        Animation animation = new TranslateAnimation(0, 0, mView.getTop(),
                mRect.top);
        animation.setDuration(200);
        animation.setFillAfter(true);
        mView.startAnimation(animation);
        mView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
        mRect.setEmpty();
        isFirst = true;
        isCalled = false;
    }

    /**
     * @方法说明:是否需要移动布局 inner.getMeasuredHeight():获取的是控件的总高度
     * @方法名称:isNeedMove
     * @return，getHeight()：获取的是屏幕的高度
     * @返回值:boolean
     */
    public boolean isNeedMove() {
        int offset = mView.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        // 0是顶部，后面那个是底部
        if (scrollY == 0 || scrollY == offset) {
            return true;
        }
        return false;
    }

    /**
     * @方法说明:当scrollView下拉到一定程度后，进行的回调方法
     * @方法名称:setCallBack
     * @param callback
     * @返回值:void
     */
    public void setCallBack(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void callback();
    }
}

