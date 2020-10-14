package com.snqu.shopping.ui.mall.goods.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.bean.DetailImageContentBean
import com.snqu.shopping.data.mall.entity.ShopDetailImageContentBean
import com.snqu.shopping.ui.main.frag.community.ZoomPicFrag
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ext.onClick
import java.util.*

/**
 * desc:详情Fragment图文的适配器
 * time: 2019/1/30
 * @author 银进
 */
class ShopDetailGoodsPicAdapter(data: List<ShopDetailImageContentBean>) : BaseMultiItemQuickAdapter<ShopDetailImageContentBean, BaseViewHolder>(data) {

    init {
        addItemType(DetailImageContentBean.DESC_IMG, R.layout.detail_goods_pic_item)
    }

    override fun convert(helper: BaseViewHolder, item: ShopDetailImageContentBean?) {
        helper.itemView.apply {
            when (item?.type) {
                DetailImageContentBean.DESC_IMG -> {
                    val imageView: ImageView = findViewById(R.id.img_item)
                    GlideUtil.loadDetailPic(imageView, item.data, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)
                    imageView.onClick {
                        val list = ArrayList<String>()
                        list.add(item.data)
                        ZoomPicFrag.start(mContext, 0, list)
                    }
                }
            }
        }
    }

}