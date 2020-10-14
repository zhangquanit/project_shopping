package com.snqu.shopping.ui.goods.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.entity.AppendFeed
import kotlinx.android.synthetic.main.item_append_evaluation_item.view.*

/**
 * desc:追加评论
 * time: 2019/9/3
 * @author 银进
 */
class AppendEvaluation : FrameLayout {
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        addView(LayoutInflater.from(context).inflate(R.layout.item_append_evaluation_item, null))
    }
    constructor(context: Context) : super(context) {
        addView(LayoutInflater.from(context).inflate(R.layout.item_append_evaluation_item, null))
    }
    fun setData(item: AppendFeed?) {
        tv_include_evaluation_item_add_time.text = "用户${item?.intervalDay ?: "0"}天后追评"
        tv_include_evaluation_item_add_content.text = item?.appendedFeedback ?: ""
    }
}