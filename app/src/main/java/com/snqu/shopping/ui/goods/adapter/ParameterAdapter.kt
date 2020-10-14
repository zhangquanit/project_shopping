package com.snqu.shopping.ui.goods.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.entity.ConsumerProtection
import kotlinx.android.synthetic.main.parameter_item.view.*

/**
 * desc:
 * time: 2019/8/23
 * @author 银进
 */
class ParameterAdapter : BaseQuickAdapter<ConsumerProtection, BaseViewHolder>(R.layout.parameter_item) {
    override fun convert(helper: BaseViewHolder, item: ConsumerProtection?) {
        helper?.itemView?.apply {
            tv_parameter_title.text=item?.title?:""
            tv_parameter_content.text=item?.desc?:""
        }
    }
}