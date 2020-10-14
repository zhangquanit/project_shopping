package com.snqu.shopping.data.goods.entity

import android.os.Parcelable
import com.snqu.shopping.util.NumberUtil
import kotlinx.android.parcel.Parcelize

/**
 * desc:
 * time: 2019/12/3
 * @author 银进
 */
@Parcelize
data class VipOrderEntity(
        val _id:String?=null,
        val user_id:String?=null,
        val goods_id:String?=null,//商品id
        val goods_info:VipGoodsEntity?=null,//商品信息
        val amount:Long?=null,//金额
        val out_trade_no:String?=null,//订单号
        val user_addr:VipGoodsAddressEntity?=null,//用户信息
        val itime:Long?=null,//订单创建时间
        val express_status:String?=null,//快递状态[0:未发货,1:发货]
        val express_no:String?=null,//运单号
        val express_com:String?=null,//快递公司
        val num:String?=null

) : Parcelable {
    fun getOrderPrice()= NumberUtil.saveTwoPoint(amount)
}