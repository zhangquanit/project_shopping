package com.snqu.shopping.ui.goods.fragment

import android.os.Bundle
import com.anroid.base.SimpleFrag
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.bean.DetailImageBean
import com.snqu.shopping.ui.goods.player.VideoImageDetailActivity
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.image_fragment.*

/**
 * desc:
 * time: 2019/8/15
 * @author 银进
 */
class ImageFragment:SimpleFrag() {
    private val imgUrl by  lazy {
        arguments?.getString(EXTRA_IMAGE_URL)?:""
    }
    private val imgIndex by lazy {
        arguments?.getInt(EXTRA_IMAGE_INDEX)?:-1
    }
    private val detailSource by  lazy {
        arguments?.getParcelableArrayList<DetailImageBean>(VideoImageDetailActivity.EXTRA_DETAIL_SOURCE)?: arrayListOf()
    }
    override fun init(savedInstanceState: Bundle?) {
        GlideUtil.loadPic( img_item,imgUrl,R.drawable.icon_max_default_pic,R.drawable.icon_max_default_pic)
        img_item.onClick {
            VideoImageDetailActivity.start(activity,detailSource,imgIndex)
        }
    }

    override fun getLayoutId()= R.layout.image_fragment
    companion object{
        const val EXTRA_IMAGE_URL="EXTRA_IMAGE_URL"
        const val EXTRA_IMAGE_INDEX="EXTRA_IMAGE_INDEX"
    }
}