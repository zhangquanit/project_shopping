package com.snqu.shopping.util

import com.snqu.shopping.data.goods.entity.GoodsEntity

object GoodsMathUtil {

    @JvmStatic
    fun calcCoupon_amount(goodsEntity: GoodsEntity?):Any{
        return goodsEntity?.rebate?.xkd_amount ?: "null"
    }

    @JvmStatic
    fun calcRebate_amount(goodsEntity: GoodsEntity?):Any{
        return goodsEntity?.coupon?.amount ?: "null"
    }

}