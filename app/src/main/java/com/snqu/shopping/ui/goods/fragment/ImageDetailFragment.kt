package com.snqu.shopping.ui.goods.fragment

import android.net.Uri
import android.os.Bundle
import com.anroid.base.SimpleFrag
import com.snqu.shopping.R
import com.snqu.shopping.ui.order.util.ImgUtils
import com.snqu.shopping.util.GlideUtil
import kotlinx.android.synthetic.main.image_detail_fragment.*


/**
 * desc:
 * time: 2019/8/15
 * @author 银进
 */
class ImageDetailFragment : SimpleFrag() {

    private val imgUrl by lazy {
        arguments?.getString(EXTRA_IMAGE_URL) ?: ""
    }

    override fun init(savedInstanceState: Bundle?) {
        if (imgUrl.startsWith("content://")) {
            GlideUtil.loadPic(img_item, Uri.parse(imgUrl), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)
        } else if (imgUrl.contains("good://")) {
            val url = imgUrl.replace("good://", "")
            GlideUtil.loadLocalPic(img_item, url, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)
        } else {
            GlideUtil.loadPic(img_item, imgUrl, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)
        }
    }

    fun saveImage() {
        if (img_item != null) {
            ImgUtils.saveImageToGalleryCheckExist(img_item.context, ImgUtils.viewToBitmap(img_item), imgUrl)
            showToastShort("保存相册成功")
        }
    }

    override fun getLayoutId() = R.layout.image_detail_fragment

    companion object {
        const val EXTRA_IMAGE_URL = "EXTRA_IMAGE_URL"
    }
}