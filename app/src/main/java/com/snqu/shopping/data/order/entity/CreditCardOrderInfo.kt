package com.snqu.shopping.data.order.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CreditCardOrderInfo(
        val _id:String,
        val phone:String,//申请人手机号码
        val real_name:String, //申请人姓名
        val card_name:String, //信用卡名称
        val bank:String, //银行
        val accept_time:String ,//通过时间
        var status:String, //状态  1审核中 2已通过 3 未通过
        val itime:String , //申请时间
        val status_txt:String //状态
) : Parcelable {
    override fun toString(): String {
        return "CreditCardOrderInfo(_id='$_id', phone='$phone', real_name='$real_name', card_name='$card_name', bank='$bank', accept_time='$accept_time', status='$status', itime='$itime', status_txt='$status_txt')"
    }
}