package com.snqu.shopping.data.goods.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PromotionLinkBodyEntity(
        val link_type:Array<String>,
        val tid:String,
        val link_url:String,
        val item_source:String,
        val need_code:String
) : Parcelable {
}
