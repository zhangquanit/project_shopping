package com.snqu.shopping.ui.goods.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import cn.jzvd.Jzvd
import com.anroid.base.SimpleFrag
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.entity.VideoBean
import com.snqu.shopping.data.home.entity.CommunityEntity
import com.snqu.shopping.ui.main.frag.community.CommunityDownloadDialogView
import com.snqu.shopping.util.GlideUtil
import common.widget.dialog.EffectDialogBuilder
import kotlinx.android.synthetic.main.player_fragment.*

/**
 * desc:
 * time: 2019/8/15
 * @author 银进
 */
class PlayerDetailFragment : SimpleFrag() {
    private val videoBean by lazy {
        arguments?.getParcelable(EXTRA_VIDEO_URL) ?: VideoBean()
    }
    private var isPaused = false

    override fun init(savedInstanceState: Bundle?) {
        jz_video.setUp(
                videoBean.url,
                ""
        )
        jz_video.isVolume = true
        Jzvd.SAVE_PROGRESS = true
        if (videoBean.thumbnail?.startsWith("content://") == true) {
            GlideUtil.loadPic(jz_video.thumbImageView, Uri.parse(videoBean.thumbnail), R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)
            jz_video.visibility = View.GONE
            val uri = Uri.parse(videoBean.thumbnail)
            textureview.visibility = View.VISIBLE
            try {
                val uri = Uri.parse(videoBean.thumbnail)
                textureview.setVideoURI(uri)
                textureview.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            GlideUtil.loadPic(jz_video.thumbImageView, videoBean.thumbnail, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)
        }

    }

    fun saveVideo() {
        val communityEntity = CommunityEntity()
        communityEntity.videos.add(videoBean.url)
        val dialogView = CommunityDownloadDialogView(mContext, communityEntity, "视频下载")
        EffectDialogBuilder(mContext)
                .setContentView(dialogView)
                .setCancelable(true)
                .show()
    }

    override fun onResume() {
        super.onResume()
        start()
        if (textureview.visibility == View.VISIBLE) {
            try {
                if (isPaused) {
                    textureview.start()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun start() {
        if (jz_video?.mediaInterface != null) {
            jz_video.mediaInterface.start()
        }
    }

    fun pause() {
        if (jz_video?.mediaInterface != null) {
            jz_video.mediaInterface.pause()
        }
    }

    override fun onPause() {
        super.onPause()
        if (textureview.visibility == View.VISIBLE) {
            textureview.pause()
            isPaused = true
        }
    }

    override fun onStop() {
        super.onStop()
        pause()
    }

    override fun getLayoutId() = R.layout.player_fragment

    companion object {
        const val EXTRA_VIDEO_URL = "EXTRA_VIDEO_URL"
    }
}