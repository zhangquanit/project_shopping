package com.snqu.shopping.data.goods.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PromotionLinkEntity(
        val app_url: String? = null,  // 原生跳转
        val click_url: String? = null,
        val position_id: String? = null,
        val item_url: String? = null,
        val share_text: String? = null,
        val code: String? = null,//淘口令
        val share_code: String? = null,//复制口令
        val relation_id: String? = null,//
        val auth_url: String? = null,//授权地址,
        val template_code: String? = null,//自定义口令模板
        val template_share: String? = null //自定义分享赚模板
) : Parcelable {
    override fun toString(): String {
        return "PromotionLinkEntity(app_url=$app_url, click_url=$click_url, position_id=$position_id, item_url=$item_url, share_text=$share_text, code=$code, share_code=$share_code, relation_id=$relation_id, auth_url=$auth_url, template_code=$template_code, template_share=$template_share)"
    }
}

@Parcelize
data class ShareText(val value: String, val type: String, val item_source: String) : Parcelable