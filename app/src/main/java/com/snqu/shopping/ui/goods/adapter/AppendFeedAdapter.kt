package com.snqu.shopping.ui.goods.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.entity.AppendFeed
import kotlinx.android.synthetic.main.item_append_evaluation_item.view.*

/**
 * desc:
 * time: 2019/8/23
 * @author 银进
 */
class AppendFeedAdapter : BaseQuickAdapter<AppendFeed, BaseViewHolder>(R.layout.item_append_evaluation_item) {
    override fun convert(helper: BaseViewHolder, item: AppendFeed?) {
        helper?.itemView?.apply {
            tv_include_evaluation_item_add_time.text = "用户${item?.intervalDay ?: "0"}天后追评"
            tv_include_evaluation_item_add_content.text = item?.appendedFeedback ?: ""
        }
    }
}