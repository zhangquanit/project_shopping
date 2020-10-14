package com.snqu.shopping.data.user.entity

import androidx.annotation.Keep
import com.snqu.shopping.data.user.entity.BalanceRecodeEntity

/**
 * desc:
 * time: 2019/11/23
 * @author 银进
 */
@Keep
data class ListBalanceRecodeEntity(var hasNext:Boolean=false, var page:Int=1, val listBalanceRecodeEntity: ArrayList<BalanceRecodeEntity> = arrayListOf())