package com.snqu.shopping.ui.goods.adapter

import android.graphics.Bitmap
import android.view.View
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.bean.SelectedPicBean
import com.snqu.shopping.util.GlideUtil
import kotlinx.android.synthetic.main.share_pic_item.view.*

/**
 * desc:详情图片Adapter
 * time: 2019/1/30
 * @author 银进
 */
class SharePicAdapter : BaseQuickAdapter<SelectedPicBean, BaseViewHolder>(R.layout.share_pic_item) {
    val glideBitmap by lazy {
        hashMapOf<Int, Bitmap?>()
    }

    override fun convert(helper: BaseViewHolder, item: SelectedPicBean?) {
        helper?.itemView?.apply {
            if (helper.adapterPosition == 0) {
                tv_blur.visibility = View.VISIBLE
            } else {
                tv_blur.visibility = View.GONE
            }
            img_select.isSelected = item?.selected == true
            view_blur.visibility = if(item?.selected == true){View.VISIBLE} else {View.GONE}
            GlideUtil.loadBitmapPic(img_pic_item, item?.url, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic, object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    glideBitmap[helper.adapterPosition] = null
                    return false
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    glideBitmap[helper.adapterPosition] = resource
                    return false
                }
            })
        }
    }

}