package com.snqu.shopping.util

import android.app.Activity
import com.snqu.shopping.data.order.entity.OrderEntity
import com.snqu.shopping.ui.goods.GoodsDetailActivity

object OrderUtil {

    @JvmStatic
    fun jumpToGoodsDetail(activity: Activity, orderEntity: OrderEntity) {
        // 不为1的时候，因为没有商品详情，就不允许点击下钻
        if (orderEntity.type != 1) {
            GoodsDetailActivity.start(activity, orderEntity.goods_id, orderEntity.item_source, orderEntity.item_id
                    ?: "")
        }
    }

}