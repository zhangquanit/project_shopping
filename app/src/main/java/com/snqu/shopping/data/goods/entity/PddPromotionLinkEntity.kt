package com.snqu.shopping.data.goods.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PddPromotionLinkEntity(val click_url: String, val item_url: String, val goods_id: String,
                                  val share_text: String? = null) : Parcelable

