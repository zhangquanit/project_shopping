package com.snqu.shopping.ui.goods.view

import android.content.Context
import android.util.AttributeSet

/**
 * desc:
 * time: 2019/8/23
 * @author 银进
 */
class SelfViewPager : androidx.viewpager.widget.ViewPager {
    private var currentIndex:Int=0
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (childCount > 0) {
            var height=0
            val child = getChildAt(currentIndex)
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val h = child.measuredHeight
            if (h > height) {
                height = h
            }
            val heightMeasureSpec1 = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec1)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

    }
    fun setCurrentIndex(index: Int) {
        if (currentIndex != index) {
            currentIndex = index
            requestLayout()
        }

    }
}