package com.snqu.shopping.ui.mine.adapter

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.android.util.date.DateFormatUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.data.user.entity.FeedbackEntity
import java.util.*

class FeedbackAdapter : BaseQuickAdapter<FeedbackEntity, BaseViewHolder>(R.layout.feedback_item) {

    override fun convert(helper: BaseViewHolder, item: FeedbackEntity?) {
        helper.getView<TextView>(R.id.item_content).text = item?.content
        val dateTime = Date((item?.itime ?: 0) * 1000L)
        helper.getView<TextView>(R.id.item_time).text = DateFormatUtil.yyyyMMdd().format(dateTime)
        when (item?.status) {
            2 -> {
                //已回复
                if (TextUtils.isEmpty(item.view_time)) {
                    helper.getView<View>(R.id.item_oval_view).visibility = View.VISIBLE
                } else {
                    helper.getView<View>(R.id.item_oval_view).visibility = View.GONE
                }
                helper.getView<TextView>(R.id.item_status).text = "已回复"
                helper.getView<TextView>(R.id.item_status).setTextColor(Color.parseColor("#FF22B66E"))
            }
            1 -> {
                //等待回复
                helper.getView<View>(R.id.item_oval_view).visibility = View.GONE
                helper.getView<TextView>(R.id.item_status).text = "待回复"
                helper.getView<TextView>(R.id.item_status).setTextColor(Color.parseColor("#FF25282D"))
            }
        }
    }
}