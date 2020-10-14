package com.snqu.shopping.data.mall.entity.address

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

/**
 * desc:json解析城市库
 * time: 2019/12/2
 * @author 银进
 */
@Parcelize
@Keep
class AreaEntity(var provinceEntity: ProvinceEntity? = null, var countyEntity: ProvinceEntity? = null, var cityEntity: ProvinceEntity? = null) : Parcelable

@Parcelize
@Keep
open class ProvinceEntity constructor(val name: String, val id: String) : Parcelable
