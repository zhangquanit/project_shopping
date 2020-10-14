package com.snqu.shopping.data.goods.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * desc:
 * time: 2019/12/2
 * @author 银进
 */
@Parcelize
data class VipGoodsAddressEntity(
        var _id:String?=null,
        var user_id:String?=null,
        var city_code:String?=null,
        var city_name:String?=null,
        var county_code:String?=null,
        var county_name:String?=null,
        var name:String?=null,
        var phone:String?=null,
        var province_id:String?=null,
        var province_name:String?=null,
        var city_origin_code:String?=null,
        var county_origin_code:String?=null,
        var province_origin_code:String?=null,
        var sketch:String?=null,
        var full_addr:String?=null//详细地址
) : Parcelable,Cloneable