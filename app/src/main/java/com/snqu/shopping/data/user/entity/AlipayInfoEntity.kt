package com.snqu.shopping.data.user.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * desc:
 * time: 2019/8/27
 * @author 银进
 */
@Parcelize
data class AlipayInfoEntity(val realname:String?=null,val account:String?=null) : Parcelable