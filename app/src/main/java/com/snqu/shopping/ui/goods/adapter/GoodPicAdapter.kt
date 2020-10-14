package com.snqu.shopping.ui.goods.adapter

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.ui.goods.fragment.GoodRecommendFrag
import com.snqu.shopping.util.GlideUtil
import com.yanzhenjie.album.AlbumFile

class GoodPicAdapter : BaseQuickAdapter<AlbumFile, BaseViewHolder>(R.layout.good_pic_item) {
    override fun convert(helper: BaseViewHolder, item: AlbumFile) {
//        if (!TextUtils.isEmpty(item?.item_image)) {
//            GlideUtil.loadPic(helper.getView(R.id.icon_pic_empty), item?.item_image, R.drawable.icon_min_default_pic, R.drawable.icon_max_default_pic)
//        }

        helper.addOnClickListener(R.id.icon_close)
        val roundedImageView = helper.getView<ImageView>(R.id.icon_pic)
        if (!TextUtils.isEmpty(item.path)) {
            if (TextUtils.equals(item.bucketName, GoodRecommendFrag.TYPE_NETWORK)) {
                GlideUtil.loadPic(roundedImageView, item.path, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
            } else {
                GlideUtil.loadLocalPic(roundedImageView, item.path, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
            }
            helper.getView<View>(R.id.icon_pic_empty).visibility = View.GONE
            helper.getView<View>(R.id.icon_close).visibility = View.VISIBLE
        } else {
            roundedImageView.setImageBitmap(null)
            helper.getView<View>(R.id.icon_pic_empty).visibility = View.VISIBLE
            helper.getView<View>(R.id.icon_close).visibility = View.GONE
        }


//        val roundedImageView = helper.getView<ImageView>(R.id.icon_pic)
//
//        //网络图片加载
//        if (item.local_show) {
//            helper.getView<View>(R.id.icon_pic_empty).visibility = View.GONE
//            helper.getView<View>(R.id.icon_close).visibility = View.VISIBLE
//            GlideUtil.loadPic(roundedImageView, item.bitmap.toString(), R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
//        } else {
//            if (item.bitmap != null) {
//                val dataString = item.bitmap.toString()
//                val bitmap = BitmapFactory.decodeStream(
//                        mContext.contentResolver?.openInputStream(item.bitmap))
//                roundedImageView.setImageBitmap(bitmap)
//                helper.getView<View>(R.id.icon_pic_empty).visibility = View.GONE
//                helper.getView<View>(R.id.icon_close).visibility = View.VISIBLE
//            } else {
//                roundedImageView.setImageBitmap(null)
//                helper.getView<View>(R.id.icon_pic_empty).visibility = View.VISIBLE
//                helper.getView<View>(R.id.icon_close).visibility = View.GONE
//            }
//        }


    }
}