package com.snqu.shopping.ui.order.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import com.android.util.ext.ToastUtil
import com.android.util.os.KeyboardUtils
import com.android.util.text.StringUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.ui.order.adapter.ViewPagerAdapter
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.order_search_fragment.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * desc:
 * time: 2019/8/19
 * @author 银进
 */
class OrderSearchFragment : SimpleFrag() {

    override fun getLayoutId() = R.layout.order_search_fragment
    private var keyword: String = ""
    private val searchFragment by lazy {
        SearchFragment()
    }
    private val groupSearchFragment by lazy {
        GroupSearchFragment()
    }

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true)
        titleBar.visibility = View.GONE
        addAction(Constant.Event.ORDER_SEARCH)
        initView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        if (TextUtils.equals(event!!.action, Constant.Event.ORDER_SEARCH)) {
            if (order_et_search != null) {
                val keyword = event.data as String
                if (!TextUtils.isEmpty(keyword)) {
                    stop_tabs.visibility = View.GONE
                    v_tab.visibility = View.VISIBLE
                    this.order_et_search.setText(keyword)
                    this.order_et_search.setSelection(keyword.length)
                    KeyboardUtils.hideSoftInput(order_et_search)
                }
            }
        }
    }

    private fun initView() {

        search_viewpager.setPagingEnabled(false)
        val pageAdapter = ViewPagerAdapter(fragmentManager!!)
        pageAdapter.addFragment(searchFragment, "我的订单")
        pageAdapter.addFragment(groupSearchFragment, "粉丝订单")
        search_viewpager.adapter = pageAdapter
        stop_tabs.setupWithViewPager(search_viewpager)
        search_viewpager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                if (p0 == 0) {
                    searchFragment.setTagAdapter()
                } else if (p0 == 1) {
                    groupSearchFragment.setTagAdapter()
                }
            }
        })



        img_clear_all.onClick {
            order_et_search.setText("")
        }
        tv_cancel.onClick {
            finish()
        }

        order_et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.isNotEmpty()) {

                    img_clear_all.visibility = View.VISIBLE
                } else {
                    img_clear_all.visibility = View.GONE
                    stop_tabs.visibility = View.VISIBLE
                    v_tab.visibility = View.GONE
//                    if (search_viewpager.currentItem == 0) {
                    searchFragment.hide()
//                    } else {
                    groupSearchFragment.hide()
//                    }
                }
            }
        })



        order_et_search.setOnEditorActionListener { v, keyCode, event ->
            if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE || keyCode == EditorInfo.IME_ACTION_GO || keyCode == KeyEvent.KEYCODE_ENTER) {
                keyword = StringUtil.trim(order_et_search.text.toString())
                if (TextUtils.isEmpty(keyword)) {
                    ToastUtil.show("请输入搜索关键字")
                } else {
                    stop_tabs.visibility = View.GONE
                    v_tab.visibility = View.VISIBLE
                    search()
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun search() {
        KeyboardUtils.hideSoftInput(order_et_search)
        if (search_viewpager.currentItem == 0) {
            searchFragment.search(keyword)
        } else if (search_viewpager.currentItem == 1) {
            groupSearchFragment.search(keyword)
        }
    }


    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("",
                    OrderSearchFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }
}