package com.snqu.shopping.data.goods.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * desc:
 * time: 2019/9/10
 * @author 银进
 */
@Parcelize
data class TBPromotionLinkEntity(
        val coupon_click_url:String?=null,//优惠劵短链接
        val sclick_url:String?=null,//点击地址 返利地址
        val item_url:String?=null,//无优惠券短连接的时候点击跳转这个链接
        val tbk_pwd:String?=null,//淘口令
        val relation_id:String?=null,//
        val auth_url :String?=null,//授权地址,
        val share_text:String?=null //分享文本
) : Parcelable