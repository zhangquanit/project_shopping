package com.snqu.shopping.data.goods.bean

import androidx.annotation.Keep
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.snqu.shopping.data.goods.entity.GoodsEntity

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
class DetailImageContentBean(var goodsEntity: GoodsEntity, var type: Int) : MultiItemEntity {

    override fun getItemType() = type

    companion object {
        const val HEADER = 0
        const val DESC_TEXT = HEADER + 1
        const val DESC_IMG = HEADER + 2
        const val BOTTOM = HEADER + 3
        const val BOTTOM_RECOMMEND_TITLE = HEADER + 4
        const val DESC = HEADER + 5
        const val END  = HEADER + 6
    }
}