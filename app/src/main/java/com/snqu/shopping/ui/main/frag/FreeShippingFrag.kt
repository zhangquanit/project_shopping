package com.snqu.shopping.ui.main.frag

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.google.android.material.tabs.TabLayout
import com.snqu.shopping.R
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.fragment_free_shipping.*


/**
 * 新9.9包邮
 */
class FreeShippingFrag : SimpleFrag() {

    val tabsTitles = arrayOf("精选", "居家百货", "美食", "服饰", "配饰", "美妆", "内衣", "母婴", "箱包", "数码配件", "文娱车品")
    val tabsIds = arrayOf("-1", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    var itemSource: String = ""
    override fun init(savedInstanceState: Bundle?) {
        initTitleBar()
        tabsTitles.forEach {
            val tab = free_tabs.newTab()
            tab.text = it
            val tabView: LinearLayout = tab.view
            tabView.background = null
            free_tabs.addTab(tab)
        }

        free_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val tabView: LinearLayout = tab?.view as LinearLayout
                tabView.background = null
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabView: LinearLayout = tab?.view as LinearLayout
                tabView.background = resources.getDrawable(R.drawable.bg_free_tab, null)
                viewpager.currentItem = tab.position
            }
        })

        free_tabs.post {
            val tabView = free_tabs.getTabAt(0)?.view as LinearLayout
            tabView.background = resources.getDrawable(R.drawable.bg_free_tab, null)
        }
        viewpager.setSmoothScroll(false)
        viewpager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                free_tabs.getTabAt(position)?.select()
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })


        item_tabobao.onClick {
            if (!TextUtils.equals(itemSource, "C")) {
                itemSource = "C"
                free_tabs.visibility = View.VISIBLE

                //淘宝
                item_tabobao.setBackgroundResource(R.drawable.bg_free_white_left)
                item_tabobao.setTextColor(Color.parseColor("#FFFF8202"))

                item_bg.setImageResource(R.drawable.icon_free_left)
                item_bg2.setImageDrawable(null)

                //拼多多
                item_pdd.setTextColor(Color.parseColor("#FFFFFFFF"))
                item_pdd.background = null
                //京东
                item_jd.setTextColor(Color.parseColor("#FFFFFFFF"))
                item_jd.background = null

                viewpager.setPagingEnabled(true)
                setAdapter()
            }
        }

        item_pdd.onClick {
            if (!TextUtils.equals(itemSource, "P")) {
                itemSource = "P"
                free_tabs.visibility = View.GONE
                //淘宝
                item_tabobao.background = null
                item_tabobao.setTextColor(Color.parseColor("#FFFFFFFF"))

                item_bg.setImageResource(R.drawable.icon_free_right)
                item_bg2.setImageResource(R.drawable.icon_free_left)


                //拼多多
                item_pdd.setBackgroundColor(Color.parseColor("#f5f5f7"))
                item_pdd.setTextColor(Color.parseColor("#FFFF8202"))


                item_jd.background = null
                item_jd.setTextColor(Color.parseColor("#FFFFFFFF"))

                viewpager.setPagingEnabled(false)
                setAdapter()
            }
        }


        item_jd.onClick {
            if (!TextUtils.equals(itemSource, "D")) {
                itemSource = "D"
                free_tabs.visibility = View.GONE

                //淘宝
                item_tabobao.background = null
                item_tabobao.setTextColor(Color.parseColor("#FFFFFFFF"))

                item_bg.setImageDrawable(null)
                item_bg2.setImageResource(R.drawable.icon_free_right)

                //拼多多
                item_pdd.setTextColor(Color.parseColor("#FFFFFFFF"))
                item_pdd.background = null

                //京东
                item_jd.setBackgroundResource(R.drawable.bg_free_white_right)
                item_jd.setTextColor(Color.parseColor("#FFFF8202"))

                viewpager.setPagingEnabled(false)
                setAdapter()
            }
        }

        item_tabobao.performClick()
    }

    private fun setAdapter() {
        viewpager.adapter = object : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {
            override fun getItem(position: Int): androidx.fragment.app.Fragment {
                val page = FreelItemFrag()
                page.arguments = FreelItemFrag.getParam(tabsIds[position], itemSource)
                return page
            }

            override fun getCount(): Int {
                return tabsIds.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return tabsTitles[position]
            }
        }
    }

    private fun initTitleBar() {
        StatusBar.setStatusBar(mContext, false, titleBar)
        titleBar.setTitleTextColor(R.color.white)
        titleBar.setLeftBtnDrawable(R.drawable.back_white)
    }

    override fun getLayoutId(): Int = R.layout.fragment_free_shipping


    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("9.9包邮",
                    FreeShippingFrag::class.java).showBg()
            SimpleFragAct.start(context, fragParam)
        }
    }
}
