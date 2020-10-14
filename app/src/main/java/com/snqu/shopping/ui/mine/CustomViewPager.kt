package com.snqu.shopping.ui.mine

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * desc:
 * time: 2019/9/4
 * @author 银进
 */
class CustomViewPager : androidx.viewpager.widget.ViewPager {
    var isCanScroll=true
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev)&&isCanScroll
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return super.onTouchEvent(ev)&&isCanScroll
    }
}