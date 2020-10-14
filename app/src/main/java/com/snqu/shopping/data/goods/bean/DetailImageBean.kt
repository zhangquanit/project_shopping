package com.snqu.shopping.data.goods.bean

import android.os.Parcelable
import androidx.annotation.Keep
import com.snqu.shopping.data.goods.entity.VideoBean
import kotlinx.android.parcel.Parcelize

/**
 * desc:imgUrl 和videoBean互斥（要么有图片要么有视频）
 * time: 2019/12/19
 * @author 银进
 */
@Keep
@Parcelize
data class DetailImageBean(var hasVideo:Boolean=false, var  imgUrl:String?=null, var videoBean: VideoBean?=null) : Parcelable