package com.snqu.shopping.ui.goods.player

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import cn.jzvd.Jzvd
import com.anroid.base.BaseActivity
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.bean.DetailImageBean
import com.snqu.shopping.data.goods.entity.VideoBean
import com.snqu.shopping.ui.goods.fragment.ImageDetailFragment
import com.snqu.shopping.ui.goods.fragment.PlayerDetailFragment
import com.snqu.shopping.util.ext.clickWithTrigger
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.activity_video_img_detail_layout.*

class VideoImageDetailActivity : BaseActivity() {
    private var videoSize = 0
    private val fragments by lazy {
        mutableListOf<androidx.fragment.app.Fragment>()
    }
    private val detailSource by lazy {
        intent?.getParcelableArrayListExtra<DetailImageBean>(EXTRA_DETAIL_SOURCE) ?: arrayListOf()
    }
    private val imgIndex by lazy {
        intent?.extras?.getInt(EXTRA_INDEX) ?: -1
    }

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(this, false)
        initView()
    }


    override fun getLayoutId(): Int = R.layout.activity_video_img_detail_layout

    /**
     * 初始化View
     */
    private fun initView() {
        if (detailSource.size != 0 && detailSource[0].hasVideo) {
            videoSize = 1
            selectVideo()
            tv_video_title.visibility = View.VISIBLE
            view_video_title_line.visibility = View.VISIBLE
            //有视频不显示这个图片导航标签
            tv_banner_indicator.visibility = View.GONE
        } else {
            videoSize = 0
            tv_video_title.visibility = View.GONE
            view_video_title_line.visibility = View.GONE
            tv_banner_indicator.visibility = View.VISIBLE
            tv_banner_indicator.text = "1/${detailSource.size}"
            selectImg()
        }

        img_delete.onClick {
            onBackPressedSupport()
        }
        tv_video_title.onClick {
            selectVideo()
            view_pager.currentItem = 0
        }
        tv_img_title.onClick {
            selectImg()
            view_pager.currentItem = videoSize
        }
        detailSource.forEach {
            if (it.hasVideo && it.videoBean != null) {
                fragments.add(PlayerDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(PlayerDetailFragment.EXTRA_VIDEO_URL, it.videoBean as VideoBean)
                    }
                })

            } else {
                fragments.add(ImageDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(ImageDetailFragment.EXTRA_IMAGE_URL, it.imgUrl.toString())
                    }
                })
            }
        }
        view_pager.adapter = object : androidx.fragment.app.FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int): androidx.fragment.app.Fragment = fragments[p0]

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                Log.e(",,,,", "destroyItem")
                super.destroyItem(container, position, `object`)
            }

            override fun getCount() = detailSource.size
        }
        view_pager.offscreenPageLimit = detailSource.size
        view_pager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {

                if (fragments[position] is PlayerDetailFragment) {
                    save_btn.text = "保存视频"
                    selectVideo()
                } else {
                    save_btn.text = "保存图片"
                    selectImg()
                }
                if (videoSize == 1) {
                    if (position == 0) {
                        tv_banner_indicator.visibility = View.GONE
                    } else {
                        tv_banner_indicator.visibility = View.VISIBLE
                    }
                    tv_banner_indicator.text = "$position/${detailSource.size - 1}"
                } else {
                    tv_banner_indicator.visibility = View.VISIBLE
                    tv_banner_indicator.text = "${position + 1}/${detailSource.size}"
                }
                for (i in 0 until fragments.size) {
                    if (i == position) {
                        try {
                            (fragments[i] as PlayerDetailFragment).start()

                        } catch (e: Exception) {
                            //说明不是视频而是图片
                        }
                    } else {
                        try {
                            (fragments[i] as PlayerDetailFragment).pause()
                        } catch (e: Exception) {
                            //说明不是视频而是图片
                        }

                    }
                }

            }
        })
        if (imgIndex != -1) {
            view_pager.currentItem = imgIndex
        }
        save_btn.clickWithTrigger(1000) {
            if (save_btn.text == "保存视频") {
                val fragment = fragments[view_pager.currentItem] as? PlayerDetailFragment
                fragment?.let { fragment.saveVideo() }
            } else {
                val fragment = fragments[view_pager.currentItem] as? ImageDetailFragment
                fragment?.let { fragment.saveImage() }
            }
        }
    }

    fun selectVideo() {
        tv_video_title.setTextColor(Color.parseColor("#FFFF8202"))
        tv_img_title.setTextColor(Color.parseColor("#888888"))
        view_video_title_line.visibility = View.VISIBLE
        view_img_title_line.visibility = View.GONE
    }

    fun selectImg() {
        tv_video_title.setTextColor(Color.parseColor("#888888"))
        tv_img_title.setTextColor(Color.parseColor("#FFFF8202"))
        view_video_title_line.visibility = View.GONE
        view_img_title_line.visibility = View.VISIBLE
    }

    override fun onBackPressedSupport() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressedSupport()
    }

    override fun onPause() {
        super.onPause()
    }

    companion object {
        //视频图片详情数据源
        const val EXTRA_DETAIL_SOURCE = "EXTRA_DETAIL_SOURCE"
        const val EXTRA_INDEX = "EXTRA_INDEX"

        //启动Activity
        @JvmStatic
        fun start(context: Context?, detailSource: ArrayList<DetailImageBean>, index: Int) {
            context?.startActivity(Intent(context, VideoImageDetailActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_DETAIL_SOURCE, detailSource)
                putExtra(EXTRA_INDEX, index)
            })
        }
    }
}
