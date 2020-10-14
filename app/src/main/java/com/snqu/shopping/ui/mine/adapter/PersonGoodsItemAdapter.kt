package com.snqu.shopping.ui.mine.adapter

import android.graphics.Color
import android.view.View
import com.android.util.os.DeviceUtil
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.entity.GoodsEntity
import com.snqu.shopping.data.home.ItemSourceClient
import com.snqu.shopping.util.GlideUtil
import kotlinx.android.synthetic.main.person_goods_item.view.*

/**
 * desc:
 * time: 2019/8/13
 * @author 银进
 */
class PersonGoodsItemAdapter(private val isCollection: Boolean = false) : BaseQuickAdapter<GoodsEntity, BaseViewHolder>(R.layout.person_goods_item) {
    override fun convert(helper: BaseViewHolder, item: GoodsEntity?) {
        helper?.itemView?.apply {
            if (isCollection) {
                if (helper.adapterPosition % 2 == 0) {
                    helper.itemView.setPadding(0, 0, DeviceUtil.dip2px(context, 5f), 0)
                } else {
                    helper.itemView.setPadding(DeviceUtil.dip2px(context, 5f), 0, 0, 0)
                }
            }
            GlideUtil.loadPic(img_person_item_pic, item?.item_image, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
            tv_person_item_title.text = item?.item_title ?: ""
            val price = SpanUtils()
            if (item?.getCouponPrice().isNullOrBlank()) {
                tv_person_coupon.visibility = View.GONE
            } else {
                tv_person_coupon.visibility = View.VISIBLE
                tv_person_coupon.text = item?.getCouponPrice() + "元券"
                price.append("券后 ").setForegroundColor(Color.parseColor("#25282D")).setFontSize(12, true)
            }
            tv_person_item_money.text = price.append("${item?.getNow_price()} ").setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(16, true).setBold()
                    .append("${item?.getOld_price()}").setForegroundColor(Color.parseColor("#848487")).setFontSize(11, true).setStrikethrough()
                    .create()
            //返利
            if (item?.getRebatePrice().isNullOrEmpty()) {
                tv_person_rebate_money.visibility = View.GONE
            } else {
                tv_person_rebate_money.visibility = View.VISIBLE
                tv_person_rebate_money.text = "返${item?.getRebatePrice()}"
            }

            tv_person_pay_person_num.text = item?.getSell_count() + "人付款"
            //店铺
            if (item?.seller_shop_name.isNullOrBlank()) {
                tv_person_goods_shop_name.visibility = View.INVISIBLE
            } else {
                tv_person_goods_shop_name.visibility = View.VISIBLE
                tv_person_goods_shop_name.text = item?.seller_shop_name ?: ""
            }
            //店铺类型BCD
            if (item?.item_source.isNullOrBlank()) {
                tv_person_goods_from.visibility = View.INVISIBLE
            } else {
                tv_person_goods_from.visibility = View.VISIBLE
                tv_person_goods_from.text = ItemSourceClient.getItemSourceName(item?.item_source)
            }
        }
    }
}