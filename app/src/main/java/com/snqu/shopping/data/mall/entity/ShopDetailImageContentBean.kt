package com.snqu.shopping.data.mall.entity

import androidx.annotation.Keep
import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * desc:
 * time: 2019/9/3
 * @author 银进
 */
/**
 * desc:分组适配器的实体类（区分是分子还是图片）
 * time: 2019/9/3
 * @author 银进
 */
@Keep
class ShopDetailImageContentBean(var data:String, var type: Int) : MultiItemEntity {

    override fun getItemType() = type

    companion object {
        const val HEADER = 0
        const val DESC_TEXT = HEADER + 1
        const val DESC_IMG = HEADER + 2
    }
}