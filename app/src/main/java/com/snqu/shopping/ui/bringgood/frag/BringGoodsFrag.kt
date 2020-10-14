package com.snqu.shopping.ui.bringgood.frag

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.google.android.material.tabs.TabLayout
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.bringgoods.BringGoodsBean
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.home_bring_goods_frag.*

class BringGoodsFrag() : SimpleFrag() {

    private lateinit var mHomeViewModel: HomeViewModel

    override fun getLayoutId(): Int {
        return R.layout.home_bring_goods_frag
    }

    override fun init(savedInstanceState: Bundle?) {
        initTitleBar()
        initView()
    }

    private fun initTitleBar(){
        StatusBar.setStatusBar(activity, false, titleBar)
        titleBar.setBackgroundResource(R.color.c_17151b)
        titleBar.findViewById<TextView>(R.id.title_text_view).setTextColor(Color.WHITE)
        titleBar.findViewById<ImageView>(R.id.title_left_button).setImageResource(R.drawable.back_white)
    }

    private fun initView() {
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        initData()
    }

    private fun initTabs(titleList: List<BringGoodsBean>) {
        titleList.forEach { bean ->
            val tab: TabLayout.Tab = tabLayout.newTab().setText(bean.title)
            tabLayout.addTab(tab)
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                updateTabTextView(tab, false)
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.text == null) {
                    return
                }
                updateTabTextView(tab,true)
                viewpager.currentItem = tab.position
            }
        })
        viewpager.adapter = object : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {
            override fun getItem(position: Int): androidx.fragment.app.Fragment {
                return BringGoodsItemFrag(titleList[position].cid)
            }

            override fun getCount(): Int {
                return titleList.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titleList[position].title
            }
        }
        viewpager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                tabLayout.getTabAt(position)?.select()
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }


    private fun initData() {
        mHomeViewModel.mNetReqResultLiveData.observe(lifecycleOwner, Observer {
            when (it?.tag) {
                ApiHost.DYDH_CATEGORY -> {
                    if (it.successful && it.data != null) {
                        val titleList = it.data as List<BringGoodsBean>
                        if (titleList != null && titleList.isNotEmpty()) {
                            loadingview.visibility = View.GONE
                            content_layout.visibility = View.VISIBLE
                            initTabs(titleList)
                        } else {
                            content_layout.visibility = View.GONE
                            loadingview.setStatus(LoadingStatusView.Status.EMPTY)
                        }
                    } else {
                        content_layout.visibility = View.GONE
                        loadingview.setStatus(LoadingStatusView.Status.FAIL)
                    }
                }
            }
        })

        loadingview.setOnBtnClickListener {
            loadingData()
        }

        loadingData()
    }

    private fun loadingData() {
        loadingview.visibility = View.VISIBLE
        loadingview.setStatus(LoadingStatusView.Status.LOADING)
        content_layout.visibility = View.GONE
        mHomeViewModel.getDydhCatergory()
    }


    private fun updateTabTextView(tab: TabLayout.Tab, flag: Boolean) {
        val tabText = tab.text.toString().trim { it <= ' ' }
        val spannableString = SpannableString(tabText)
        var styleSpan: StyleSpan? = null
        styleSpan = if (flag) {
            StyleSpan(Typeface.BOLD)
        } else {
            StyleSpan(Typeface.NORMAL)
        }
        spannableString.setSpan(styleSpan, 0, tabText.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        tab.text = spannableString
    }

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("抖券",
                    BringGoodsFrag::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }

}