package com.snqu.shopping.ui.goods.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.SpanUtils
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.entity.GoodsEntity
import com.snqu.shopping.util.GlideUtil
import kotlinx.android.synthetic.main.shop_recommended_goods.view.*

/**
 * desc:
 * time: 2019/8/22
 * @author 银进
 */
class ShopRecommendedGoodsView : FrameLayout {
    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet){
        LayoutInflater.from(context).inflate(R.layout.shop_recommended_goods,this,true)
    }

    fun setData(goodsEntity: GoodsEntity) {
        getChildAt(0).apply {
            val price = SpanUtils()

            //优惠券
            if (goodsEntity.getCouponPrice().isNullOrBlank()) {
                tv_goods_shop_recommended_goods_coupon.visibility = View.GONE
            } else {
                price.append("券后").setForegroundColor(Color.parseColor("#25282D")).setFontSize(14, true)
                tv_goods_shop_recommended_goods_coupon.visibility = View.VISIBLE
                tv_goods_shop_recommended_goods_coupon.text="优惠券:${goodsEntity.getCouponPrice()}元"
            }
            //返利
            if (goodsEntity.getRebatePrice()=="") {
                tv_goods_shop_recommended_goods_rebate_money.visibility=View.GONE
            } else {
                tv_goods_shop_recommended_goods_rebate_money.visibility=View.VISIBLE
                tv_goods_shop_recommended_goods_rebate_money.text="返￥${goodsEntity.getRebatePrice()}"

            }
            //价格
            tv_goods_shop_recommended_goods_rebate_after_money.text= price
                    .append(goodsEntity.getNow_price()).setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(14, true)
                    .create()
            tv_goods_shop_recommended_goods_price.text= SpanUtils()
                    .append("￥${goodsEntity.getOld_price()}").setForegroundColor(Color.parseColor("#848487")).setFontSize(11, true).setStrikethrough()
                    .create()
            GlideUtil.loadPic(img_goods_shop_recommended_goods, goodsEntity.item_image
                    ?: "", R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)
        }
    }

}