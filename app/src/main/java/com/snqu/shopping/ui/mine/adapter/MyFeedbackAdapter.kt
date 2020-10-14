package com.snqu.shopping.ui.mine.adapter

import android.database.Cursor
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.makeramen.roundedimageview.RoundedImageView
import com.snqu.shopping.R
import com.snqu.shopping.data.user.entity.FeedbackEntity

class MyFeedbackAdapter : BaseQuickAdapter<FeedbackEntity, BaseViewHolder>(R.layout.my_feedback_item) {

    override fun convert(helper: BaseViewHolder, item: FeedbackEntity) {

        helper.addOnClickListener(R.id.icon_close)

        val roundedImageView = helper.getView<RoundedImageView>(R.id.icon_pic)

        if (item.bitmap != null) {
            val dataString = item.bitmap.toString()
            if (dataString.contains("video") || dataString.contains(".mp4")) {
                //视频选择
                val filePathColumn = arrayOf(MediaStore.Video.Media.DATA)
                val cursor: Cursor? = mContext.contentResolver?.query(item.bitmap,
                        filePathColumn, null, null, null)
                if (cursor != null) {
                    cursor.moveToFirst()
                    val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                    val path = cursor.getString(columnIndex)
                    cursor.close()
                    val media = MediaMetadataRetriever()
                    media.setDataSource(path);// videoPath 本地视频的路径
                    val bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    roundedImageView.setImageBitmap(bitmap)
                    helper.getView<View>(R.id.icon_play).visibility = View.VISIBLE
                }
            } else {
                val bitmap = BitmapFactory.decodeStream(
                        mContext.contentResolver?.openInputStream(item.bitmap))
                roundedImageView.setImageBitmap(bitmap)
                helper.getView<View>(R.id.icon_play).visibility = View.GONE
            }
            helper.getView<View>(R.id.icon_pic_empty).visibility = View.GONE
            helper.getView<View>(R.id.icon_close).visibility = View.VISIBLE
        }else{
            roundedImageView.setImageBitmap(null)
            helper.getView<View>(R.id.icon_pic_empty).visibility = View.VISIBLE
            helper.getView<View>(R.id.icon_close).visibility = View.GONE
        }
    }
}