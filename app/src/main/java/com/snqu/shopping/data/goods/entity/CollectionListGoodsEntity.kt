package com.snqu.shopping.data.goods.entity

import com.snqu.shopping.util.NumberUtil

/**
 * desc:商品收藏
 * time: 2019/9/5
 * @author 银进
 */

//data class CollectionListGoodsEntity(
//        val fail_list:List<CollectionGoodsEntity>,
//        val list:List<CollectionGoodsEntity>,
//        val frugal:Long?=null//省了多少钱
//
//){
//    val frugalMoney: String?
//        //省了多少钱
//        get() = NumberUtil.saveTwoPoint(frugal)
//}

data class CollectionGoodsEntity(
       val _id:String?=null,
       var isSelected:Boolean=false,
       val goods:GoodsEntity?=null,
       val reduce_price:Long?=null
) {
    override fun toString(): String {
        return "CollectionGoodsEntity(_id=$_id, isSelected=$isSelected, goods=$goods, reduce_price=$reduce_price)"
    }
}
