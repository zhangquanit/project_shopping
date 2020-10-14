package com.snqu.shopping.ui.order.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.data.order.entity.OrderEntity
import com.snqu.shopping.util.GlideUtil
import kotlinx.android.synthetic.main.self_find_order_item.view.*

/**
 * desc:找回订单的适配器
 * time: 2019/10/10
 * @author 银进
 */
class FindOrderSuccessAdapter: BaseQuickAdapter<OrderEntity, BaseViewHolder>(R.layout.self_find_order_item)  {
    override fun convert(helper: BaseViewHolder, item: OrderEntity?) {
        helper?.itemView?.apply {
            GlideUtil.loadPic(img_pic,item?.item_image,R.drawable.icon_min_default_pic,R.drawable.icon_min_default_pic)
            tv_order_number.text="订单编号：${item?.third_order_id?:""}"
            tv_title.text = item?.item_title ?: ""
            tv_price.text="商品价格：${item?.price?:""}"
            tv_num.text="数量：${item?.item_num?:0}"
            if (item?.create_time == null) {
                tv_create_order_time.text = "下单时间："
            } else {
                tv_create_order_time.text = "下单时间：${item?.create_time}"
            }
        }
    }
}