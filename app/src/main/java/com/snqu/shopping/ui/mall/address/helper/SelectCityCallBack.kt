package com.snqu.shopping.ui.mall.address.helper

import com.snqu.shopping.data.mall.entity.address.AreaEntity

/**
 * desc:回调适配器
 * time: 2019/12/3
 * @author 银进
 */
interface SelectCityCallBack {
    fun selected(areaEntity: AreaEntity?)
}