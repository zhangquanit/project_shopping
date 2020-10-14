package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.google.android.material.tabs.TabLayout
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.ui.main.frag.WebViewFrag.WebViewParam
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.statistics.UmengAnalysisUtil
import kotlinx.android.synthetic.main.me_tutor_share_frag.*
import kotlinx.android.synthetic.main.me_tutor_share_page_empty.*
import kotlinx.android.synthetic.main.tutor_share_frag.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 * 导师分享
 */
class MeTutorShareFrag : SimpleFrag() {

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("导师分享",
                    MeTutorShareFrag::class.java, Bundle().apply {
            })
            SimpleFragAct.start(context, fragParam)
        }
    }

    override fun getLayoutId(): Int = R.layout.me_tutor_share_frag

    private var isJumpWeb = false

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)

        //我的-导师分享
        UmengAnalysisUtil.onEvent("tutor_share")

        addAction(Constant.Event.TUTOR_SHARE_NO_DATA)
        addAction(Constant.Event.TUTOR_SHARE_SHOW)

        // 查看自己的导师分享
        btn_look.onClick {
            TutorShareFrag.start(activity)
        }

        //创建文档
        btn_create.onClick {
            val webViewParam = WebViewParam()
            webViewParam.url = Constant.WebPage.TUTORSHARE
            WebViewFrag.start(mContext, webViewParam)
            isJumpWeb = true
            //新建文档
            UmengAnalysisUtil.onEvent("tutor_share_create")
        }

        tutor_share_layout.onClick {
            btn_create.performClick()
        }

        val tabs = arrayOf("所有文档", "展示中的文档")
        tabs.forEach {
            // 去掉点击背景
            val tab = tabs_layout.newTab()
            tab.text = it
            val tabView: LinearLayout = tab.view
            tabView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent))
            tabs_layout.addTab(tab)
        }

        tabs_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                tab_viewpager.currentItem = tab.position
            }
        })

        tab_viewpager.adapter = MeTutorPagerAdapter(childFragmentManager)
        tab_viewpager.offscreenPageLimit = 4
        tab_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    if (loadingBar.visibility == View.GONE) {
                        tutor_share_layout.visibility = View.VISIBLE
                    }
                } else {
                    tutor_share_layout.visibility = View.GONE
                }
                EventBus.getDefault().post(PushEvent(Constant.Event.TUTOR_SHARE_REFRESH))
                tabs_layout?.let {
                    val tab: TabLayout.Tab? = it.getTabAt(position)
                    tab?.select()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })


//        userViewModel.createTutorShare("http://backend.xin1.cn/img/logo.7505308e.png","122121我查看我的导师分享","查看我的导师分享")
//        userViewModel.createTutorShare("http://backend.xin1.cn/img/logo.7505308e.png","3333333我查看我的导师分享","查看我的导师分享")
//        userViewModel.createTutorShare("http://backend.xin1.cn/img/logo.7505308e.png","44444我查看我的导师分享","查看我的导师分享")
//        userViewModel.createTutorShare("http://backend.xin1.cn/img/logo.7505308e.png","1我查看我的导师分享1","查看我的导师分享")
//        userViewModel.createTutorShare("http://backend.xin1.cn/img/logo.7505308e.png","2我查看我的导师分享2","查看我的导师分享")
//        userViewModel.createTutorShare("http://backend.xin1.cn/img/logo.7505308e.png","3我查看我的导师分享","查看我的导师分享")
//        userViewModel.createTutorShare("http://backend.xin1.cn/img/logo.7505308e.png","4我查看我的导师分享","查看我的导师分享")
//        userViewModel.createTutorShare("http://backend.xin1.cn/img/logo.7505308e.png","5我查看我的导师分享","查看我的导师分享")

    }

    private class MeTutorPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            val frag = MeTutorPageFrag()
            if (position == 0) {
                frag.arguments = frag.setParam("")
            } else if (position == 1) {
                frag.arguments = frag.setParam("2")
            }
            return frag
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return ""
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: PushEvent) {
        if (TextUtils.equals(event.action, Constant.Event.TUTOR_SHARE_NO_DATA)) {
            data_list_layout.visibility = View.GONE
            me_tutor_share_no_data.visibility = View.VISIBLE
        } else if (TextUtils.equals(event.action, Constant.Event.TUTOR_SHARE_SHOW)) {
            data_list_layout.visibility = View.VISIBLE
            me_tutor_share_no_data.visibility = View.GONE
            if (tab_viewpager.currentItem == 0) {
                tutor_share_layout.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isJumpWeb) {
            EventBus.getDefault().post(PushEvent(Constant.Event.TUTOR_SHARE_REFRESH))
        }
        isJumpWeb = false
    }


}