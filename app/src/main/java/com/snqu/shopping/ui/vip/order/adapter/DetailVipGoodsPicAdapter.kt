package com.snqu.shopping.ui.vip.order.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.util.GlideUtil

/**
 * desc:详情Fragment适配器
 * time: 2019/1/30
 * @author 银进
 */
class DetailVipGoodsPicAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.detail_goods_pic_item) {
    override fun convert(helper: BaseViewHolder, item: String?) {
        helper?.itemView?.apply {
            GlideUtil.loadPicFit(this.findViewById(R.id.img_item) as ImageView, item, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)

        }
    }


}