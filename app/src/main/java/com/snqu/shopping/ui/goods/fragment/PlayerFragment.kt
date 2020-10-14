package com.snqu.shopping.ui.goods.fragment

import android.os.Bundle
import cn.jzvd.Jzvd
import com.anroid.base.SimpleFrag
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.bean.DetailImageBean
import com.snqu.shopping.data.goods.entity.VideoBean
import com.snqu.shopping.ui.goods.player.CallBack
import com.snqu.shopping.ui.goods.player.VideoImageDetailActivity
import com.snqu.shopping.util.GlideUtil
import kotlinx.android.synthetic.main.player_fragment.*

/**
 * desc:
 * time: 2019/8/15
 * @author 银进
 */
class PlayerFragment:SimpleFrag() {
    private val videoBean by  lazy {
        arguments?.getParcelable(EXTRA_VIDEO_URL)?:VideoBean()
    }
    private val videoDetailIsfFull by lazy {
        arguments?.getBoolean(EXTRA_VIDEO_FULL)?:false
    }
    private val detailSource by  lazy {
        arguments?.getParcelableArrayList<DetailImageBean>(VideoImageDetailActivity.EXTRA_DETAIL_SOURCE)?: arrayListOf()
    }
    override fun init(savedInstanceState: Bundle?) {
        jz_video.setUp(
                videoBean.url,
                ""
        )
        Jzvd.SAVE_PROGRESS = true
        GlideUtil.loadPic(jz_video.thumbImageView, videoBean.thumbnail, R.drawable.icon_max_default_pic,R.drawable.icon_max_default_pic)
        if (!videoDetailIsfFull) {
            jz_video.callBack=object :CallBack{
                override fun gotoScreenFullscreen() {
                    VideoImageDetailActivity.start(activity, detailSource,-1)
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
    override fun onStop() {
        super.onStop()
        pause()
    }
    override fun getLayoutId()= R.layout.player_fragment
    companion object{
        const val EXTRA_VIDEO_URL="EXTRA_VIDEO_URL"
        const val EXTRA_VIDEO_FULL="EXTRA_VIDEO_FULL"
    }
}