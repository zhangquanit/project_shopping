package com.snqu.shopping.ui.mine.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.util.GlideUtil

class FeedbackDetailAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.feedback_detail_item) {

    override fun convert(helper: BaseViewHolder, item: String) {
        if (item.contains("jpg") || item.contains("png")||item.contains("jpeg")) {
            helper.getView<View>(R.id.icon_feedback_play).visibility = View.GONE
            GlideUtil.loadRoundPic(helper.getView(R.id.icon_img), item, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic, 7)
        } else {
            helper.getView<View>(R.id.icon_feedback_play).visibility = View.VISIBLE
            GlideUtil.loadRoundPic(helper.getView(R.id.icon_img), item, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic, 7)
        }
    }
}