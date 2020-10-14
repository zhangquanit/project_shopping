package com.snqu.shopping.data.goods.entity

import android.os.Parcelable
import com.snqu.shopping.util.NumberUtil
import kotlinx.android.parcel.Parcelize


/**
 * desc:
 * time: 2019/8/28
 * @author 银进
 */
@Parcelize
data class VipGoodsEntity(
        val _id: String? = null,
        val title: String? = null,//名称
        val spec: String? = null,//规格
        val price: Long? = null,//售价
        val banner: List<String>? = null,//banner图片
        val detail:  List<String>? = null,//明细图片
        val itime: Long? = null,
        val out_trade_no: String? = null,//订单编号
        val sale_count:  Int? = null//销量
        ) : Parcelable{
        fun getVipPrice()=NumberUtil.saveTwoPoint(price)
}