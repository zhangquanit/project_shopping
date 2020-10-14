package com.snqu.shopping.ui.goods.helper

import com.snqu.shopping.ui.goods.GoodsDetailActivity

/**
 * desc:
 * time: 2019/8/29
 * @author 银进
 */
object RecodeHelper {
     val goodsDetailPage = arrayListOf<GoodsDetailActivity>()
     var itemFeedCount = 0
     var mainActivityIsExist = false
     // 商品详情页最大保存个数
     var historySize = 3
}