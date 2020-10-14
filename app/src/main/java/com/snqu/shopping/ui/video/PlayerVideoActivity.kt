package com.snqu.shopping.ui.video

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import cn.jzvd.Jzvd
import com.anroid.base.BaseActivity
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.ui.goods.player.Completion
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.player_video_activity.*

/**
 * 视频播放页面
 * time: 2019/8/15
 */
class PlayerVideoActivity : BaseActivity() {
    private val videoUrl by lazy {
        intent?.getStringExtra(EXTRA_VIDEO_URL)
    }
    private val thumb by lazy {
        intent?.getStringExtra(EXTRA_VIDEO_THUMB)
    }

    private val disableClosePage by lazy {
        intent?.getBooleanExtra(EXTRA_CLOSEPAGE, false)
    }

    var playComplete: Boolean? = false
    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(this, false)
        jz_video.setUp(
                videoUrl,
                ""
        )
        jz_video.isVolume = true
        jz_video.canEnterFullScreen = false
        Jzvd.SAVE_PROGRESS = false
        GlideUtil.loadPic(jz_video.thumbImageView, videoUrl)
//        jz_video.thumbImageView.setImageResource(R.drawable.icon_video_thumb)
//        GlideUtil.loadPic(, , R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic, object : RequestListener<Drawable> {
//            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                return false
//            }
//
//            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                val width = resource?.intrinsicWidth ?: 0
//                val height = resource?.intrinsicHeight ?: 0
//                val rate = width * 1f / height
//                val layoutParams: ViewGroup.LayoutParams = jz_video.thumbImageView.layoutParams
//                layoutParams.width = DeviceUtil.getScreenWidthPx(this@PlayerVideoActivity)
//                layoutParams.height = ((DeviceUtil.getScreenWidthPx(this@PlayerVideoActivity)) / rate - 1).toInt()
//                jz_video.thumbImageView.layoutParams = layoutParams
//                return false
//            }
//        })

        img_delete.onClick {
            onBackPressedSupport()
        }

        if (disableClosePage!!) {
            container.background = resources.getDrawable(R.drawable.transparent)
            img_delete.visibility = View.GONE
            jz_video.bottom_seek_progress.visibility = View.GONE //隐藏拖动进度条
            jz_video.canEnterFullScreen = true
//            jz_video.gotoScreenFullscreen()
            jz_video.completion = object : Completion {
                override fun onComplete() {
                    playComplete = true
                    img_delete.visibility = View.VISIBLE
                    finish()
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        start()
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

    override fun onDestroy() {
        Jzvd.releaseAllVideos()
        super.onDestroy()
    }


    override fun onStop() {
        super.onStop()
        pause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (disableClosePage!! && !playComplete!!) {
//                ToastUtil.show("亲，请播放完成后再关闭页面哦")
                return true
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun getLayoutId() = R.layout.player_video_activity

    companion object {
        const val EXTRA_VIDEO_URL = "EXTRA_VIDEO_URL"
        const val EXTRA_VIDEO_THUMB = "EXTRA_VIDEO_THUMB"
        const val EXTRA_CLOSEPAGE = "EXTRA_CLOSEPAGE"

        fun start(context: Context?, videoUrl: String, thumb: String?, closePage: Boolean) {
            context?.startActivity(Intent(context, PlayerVideoActivity::class.java).apply {
                putExtra(EXTRA_VIDEO_URL, videoUrl)
                putExtra(EXTRA_VIDEO_THUMB, thumb)
                putExtra(EXTRA_CLOSEPAGE, closePage)
            })
        }

        fun start(context: Context?, videoUrl: String, thumb: String?) {
            start(context, videoUrl, null, false)
        }

        fun start(context: Context?, videoUrl: String) {
            start(context, videoUrl, null, false)
        }

        fun startByHelpVideo(context: Context?, videoUrl: String) {
            start(context, videoUrl, null, true)
        }
    }
}