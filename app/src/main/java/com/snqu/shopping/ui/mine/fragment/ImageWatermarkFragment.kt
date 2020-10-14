package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.image_watermark_fragment.*

/**
 * desc:
 * time: 2019/8/15
 * @author 银进
 */
class ImageWatermarkFragment : SimpleFrag() {

    private val imgData by lazy {
        arguments?.getString(EXTRA_IMAGE_DATA) ?: ""
    }


    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(mContext, false,titleBar)
        titleBar.visibility = View.GONE

        GlideUtil.loadPic(imageview , BitmapFactory.decodeFile(imgData), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)

        img_delete.onClick {
            finish()
        }
    }

    override fun getLayoutId() = R.layout.image_watermark_fragment

    companion object {
        const val EXTRA_IMAGE_DATA = "data"
        const val EXTRA_IMAGE_URL  = "url"

        fun start(context: Context?, bitmap: String) {
            val fragParam = SimpleFragAct.SimpleFragParam("",
                    ImageWatermarkFragment::class.java)
            val bundle = Bundle()
            bundle.putString(EXTRA_IMAGE_DATA, bitmap)
            fragParam.paramBundle = bundle
            SimpleFragAct.start(context, fragParam)
        }

        fun startUrl(context: Context?, url: String) {
            val fragParam = SimpleFragAct.SimpleFragParam("",
                    ImageWatermarkFragment::class.java)
            val bundle = Bundle()
            bundle.putString(EXTRA_IMAGE_URL,url)
            fragParam.paramBundle = bundle
            SimpleFragAct.start(context, fragParam)
        }
    }
}