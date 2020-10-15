package com.snqu.shopping.ui.order

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.android.util.date.DateFormatUtil
import com.anroid.base.BaseActivity
import com.anroid.base.ui.StatusBar
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.google.android.material.tabs.TabLayout
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.home.ItemSourceClient
import com.snqu.shopping.data.home.entity.ItemSourceEntity
import com.snqu.shopping.ui.main.MainActivity
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.ui.order.fragment.FindOrderFragment
import com.snqu.shopping.ui.order.fragment.GroupOrderItemFragment
import com.snqu.shopping.ui.order.fragment.OrderSearchFragment
import com.snqu.shopping.ui.order.fragment.SelfOrderItemFragment
import com.snqu.shopping.ui.order.vm.OrderViewModel
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.statistics.SndoData
import kotlinx.android.synthetic.main.self_order_fragment.*
import java.util.*
import kotlin.collections.HashMap

/**
 * desc:
 * time: 2019/8/16
 * @author 银进
 */
class OrderActivity : BaseActivity() {
    private var selectDate: Date? = null
    private val index by lazy {
        intent?.getIntExtra(ORDER_INDEX, 0) ?: 0
    }
    private val isSelf by lazy {
        intent?.getBooleanExtra(IS_SELF, true) ?: true
    }
    private lateinit var itemSources: List<ItemSourceEntity>
    private val selfOrderItemFragmentList by lazy {
        arrayOf(
                SelfOrderItemFragment().apply {
                    arguments = Bundle().apply {
                        putInt(SelfOrderItemFragment.EXTRA_ITEM_INDEX, 0)
                    }
                },
                SelfOrderItemFragment().apply {
                    arguments = Bundle().apply {
                        putInt(SelfOrderItemFragment.EXTRA_ITEM_INDEX, 1)
                    }
                },
                SelfOrderItemFragment().apply {
                    arguments = Bundle().apply {
                        putInt(SelfOrderItemFragment.EXTRA_ITEM_INDEX, 2)
                    }
                },
                SelfOrderItemFragment().apply {
                    arguments = Bundle().apply {
                        putInt(SelfOrderItemFragment.EXTRA_ITEM_INDEX, 10)
                    }
                }

        )
    }

    private val groupOrderItemFragmentList by lazy {
        arrayOf(
                GroupOrderItemFragment().apply {
                    arguments = Bundle().apply {
                        putInt(GroupOrderItemFragment.EXTRA_ITEM_INDEX, 0)
                    }
                },
                GroupOrderItemFragment().apply {
                    arguments = Bundle().apply {
                        putInt(GroupOrderItemFragment.EXTRA_ITEM_INDEX, 1)
                    }
                },
                GroupOrderItemFragment().apply {
                    arguments = Bundle().apply {
                        putInt(GroupOrderItemFragment.EXTRA_ITEM_INDEX, 2)
                    }
                },
                GroupOrderItemFragment().apply {
                    arguments = Bundle().apply {
                        putInt(GroupOrderItemFragment.EXTRA_ITEM_INDEX, 10)
                    }
                }

        )
    }

    private val orderViewModel by lazy {
        ViewModelProviders.of(this).get(OrderViewModel::class.java)
    }


    override fun getLayoutId() = R.layout.self_order_fragment
    var platform: String? = "全部"
    var typeName: String? = "全部"

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(this, true)
        initOrderResult()
        initView()
    }

    override fun onBackPressedSupport() {
        MainActivity.startForPage(this, 4)
    }


    private fun initOrderResult() {
        orderViewModel.dataResult.observe(this, androidx.lifecycle.Observer {
            if (it?.tag == ApiHost.ORDER_LIST) {
                closeLoadDialog()
                if (it.successful) {
                    refresh_layout.finishRefresh(true)
                } else {
                    refresh_layout.finishRefresh(false)
                }
            } else if (it?.tag == ApiHost.ORDER_LIST_GROUP) {
                closeLoadDialog()
                if (it.successful) {
                    refresh_layout.finishRefresh(true)
                } else {
                    refresh_layout.finishRefresh(false)
                }
            }
        })
    }

    /**
     * 初始化View
     */
    private fun initView() {
        order_tip.onClick {
            val webViewParam = WebViewFrag.WebViewParam()
            webViewParam.url = Constant.WebPage.ORDER_TIP
            WebViewFrag.start(mContext, webViewParam)
        }

        val dataLis = HashMap<String, String>()

        itemSources = ItemSourceClient.getOrderItemSource()
        for (sourceEntity in itemSources) {
            dataLis[sourceEntity.name] = sourceEntity.code
        }
        itemSources.forEach {
            if (!TextUtils.isEmpty(it?.name)) {
                // 去掉点击背景
                val tab = order_tabs.newTab()
                tab.text = it.name
                val tabView: LinearLayout = tab.view
                if (tabView != null) {
                    tabView.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent))
                }
                order_tabs.addTab(tab)
            }
        }



        order_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabText = tab.text
                when (dataLis[tabText]) {
                    "all" -> {
                        selectOrderType(null)
                        platform = "全部"
                        report()
                    }
                    else -> {
                        val code = dataLis[tabText]
                        if (!TextUtils.isEmpty(code)) {
                            selectOrderType(code)
                            platform = tabText.toString()
                            report()
                        } else {
                            selectOrderType("none")
                        }
                    }
                }
            }
        })

        // 进行初始化监听，当被选中的时候才执行网络调用
        rg_order.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.self_order -> {
                    if (self_order.isChecked) {
                        group_order.isChecked = false
                        self_order.setTextColor(Color.WHITE)
                        group_order.setTextColor(Color.parseColor("#25282D"))
                        view_pager.adapter = object : androidx.fragment.app.FragmentStatePagerAdapter(supportFragmentManager) {
                            override fun getItem(p0: Int) = selfOrderItemFragmentList[p0]
                            override fun getCount() = 4
                        }
                        initOrderStatus()
                    } else {
                        self_order.setTextColor(Color.parseColor("#25282D"))
                        group_order.setTextColor(Color.WHITE)
                    }
                }
                R.id.group_order -> {
                    if (group_order.isChecked) {
                        SndoData.event(SndoData.XLT_EVENT_GROUP_ORDER,
                                SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
                                SndoData.XLT_ITEM_THIRDCate_TITLE, "null",
                                SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
                                SndoData.XLT_GOOD_NAME, "null",
                                SndoData.XLT_GOOD_ID, "null",
                                SndoData.XLT_ITEM_SOURCE, platform
                        )
                        self_order.isChecked = false
                        group_order.setTextColor(Color.WHITE)
                        self_order.setTextColor(Color.parseColor("#25282D"))
                        view_pager.adapter = object : androidx.fragment.app.FragmentStatePagerAdapter(supportFragmentManager) {
                            override fun getItem(p0: Int) = groupOrderItemFragmentList[p0]
                            override fun getCount() = 4
                        }
                        initOrderStatus()
                    } else {
                        group_order.setTextColor(Color.parseColor("#25282D"))
                        self_order.setTextColor(Color.WHITE)
                    }
                }
            }
        }

        selectOrderType(null)
        refresh_layout.setOnRefreshListener {
            if (view_pager != null) {
                if (self_order.isChecked) {
                    selfOrderItemFragmentList[view_pager.currentItem].refreshData()
                } else {
                    groupOrderItemFragmentList[view_pager.currentItem].refreshData()
                }
            }
        }
        img_back.onClick {
            MainActivity.startForPage(this, 4)
        }
        img_calendar.onClick {
            selectTime()
        }
        img_search.onClick {
            OrderSearchFragment.start(this)
        }
        ll_order_all.onClick {
            view_pager.currentItem = 0
        }
        ll_order_future.onClick {
            view_pager.currentItem = 1
        }
        ll_order_already.onClick {
            view_pager.currentItem = 2
        }
        ll_order_failure.onClick {
            view_pager.currentItem = 3
        }

        et_search.onClick {
            FindOrderFragment.start(mContext)
        }


        view_pager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(p0: Int) {
                changeStatus()
                typeName = "全部"
                when (p0) {
                    0 -> selectedAll()
                    1 -> {
                        selectedFuture()
                        typeName = "即将到账"
                    }
                    2 -> {
                        selectedAlready()
                        typeName = "已到账"
                    }
                    3 -> {
                        selectedFailure()
                        typeName = "已失效"
                    }
                    else -> {
                        selectedAll()
                    }
                }
                report()
            }
        })
        view_pager.offscreenPageLimit = 4
        view_pager.currentItem = index

        if (isSelf) {
            self_order.isChecked = true
            group_order.isChecked = false
            view_pager.adapter = object : androidx.fragment.app.FragmentStatePagerAdapter(supportFragmentManager) {
                override fun getItem(p0: Int) = selfOrderItemFragmentList[p0]
                override fun getCount() = 4
            }
        } else {
            self_order.isChecked = false
            group_order.isChecked = true
            view_pager.adapter = object : androidx.fragment.app.FragmentStatePagerAdapter(supportFragmentManager) {
                override fun getItem(p0: Int) = groupOrderItemFragmentList[p0]
                override fun getCount() = 4
            }
        }
        orderViewModel.orderItemSource.value = null
        changeStatus()
    }

    private fun initOrderStatus() {
        if (typeName.equals("全部")) {
            view_pager.currentItem = 0
        } else if (typeName.equals("即将到账")) {
            view_pager.currentItem = 1
        } else if (typeName.equals("已到账")) {
            view_pager.currentItem = 2
        } else if (typeName.equals("已失效")) {
            view_pager.currentItem = 3
        }
    }

    private fun report() {
//        if (!self_order.isChecked) {
//            SndoData.event(SndoData.XLT_EVENT_GROUP_ORDER,
//                    SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
//                    SndoData.XLT_ITEM_THIRDCate_TITLE, "null",
//                    SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
//                    SndoData.XLT_GOOD_NAME, "null",
//                    SndoData.XLT_GOOD_ID, "null",
//                    SndoData.XLT_ITEM_SOURCE, platform
//            )
//        }
    }

    private fun selectOrderType(type: String?) {
        orderViewModel.orderItemSource.value = type
    }


    private fun changeStatus() {
        if (selectDate == null) {
            ll_order_time_and_type.visibility = View.GONE
        } else {
            ll_order_time_and_type.visibility = View.VISIBLE
            val orderType = when (view_pager.currentItem) {
                0 -> "全部"
                1 -> "即将到账"
                2 -> "已到账"
                3 -> "已失效"
                else -> "全部"
            }
            tv_order_time_and_type.text = DateFormatUtil.yyyy_MM().format(selectDate) + orderType + "订单"
        }

    }

    /**
     * 已失效
     */
    private fun selectedFailure() {

        tv_order_all.setTextColor(Color.parseColor("#25282D"))

        tv_order_future.setTextColor(Color.parseColor("#25282D"))

        tv_order_already.setTextColor(Color.parseColor("#25282D"))

        tv_order_failure.setTextColor(Color.parseColor("#FFFF8202"))

        tv_order_all.isEnabled = false
        tv_order_future.isEnabled = false
        tv_order_already.isEnabled = false
        tv_order_failure.isEnabled = true
    }

    /**
     * 已到账
     */
    private fun selectedAlready() {
        tv_order_all.setTextColor(Color.parseColor("#25282D"))

        tv_order_future.setTextColor(Color.parseColor("#25282D"))

        tv_order_already.setTextColor(Color.parseColor("#FFFF8202"))

        tv_order_failure.setTextColor(Color.parseColor("#25282D"))

        tv_order_all.isEnabled = false
        tv_order_future.isEnabled = false
        tv_order_already.isEnabled = true
        tv_order_failure.isEnabled = false
    }

    /**
     * 选中即将到账
     */
    private fun selectedFuture() {
        tv_order_all.setTextColor(Color.parseColor("#25282D"))
        tv_order_future.setTextColor(Color.parseColor("#FFFF8202"))
        tv_order_already.setTextColor(Color.parseColor("#25282D"))
        tv_order_failure.setTextColor(Color.parseColor("#25282D"))
        tv_order_all.isEnabled = false
        tv_order_future.isEnabled = true
        tv_order_already.isEnabled = false
        tv_order_failure.isEnabled = false
    }

    /**
     * 选中全部
     */
    private fun selectedAll() {
        tv_order_all.setTextColor(Color.parseColor("#FFFF8202"))

        tv_order_future.setTextColor(Color.parseColor("#25282D"))

        tv_order_already.setTextColor(Color.parseColor("#25282D"))

        tv_order_failure.setTextColor(Color.parseColor("#25282D"))

        tv_order_all.isEnabled = true
        tv_order_future.isEnabled = false
        tv_order_already.isEnabled = false
        tv_order_failure.isEnabled = false
    }


    /**
     * 选择日期
     */
    private fun selectTime() {
        val endCalendar = Calendar.getInstance()
        val startCalendar = Calendar.getInstance().apply {
            set(endCalendar.get(Calendar.YEAR) - 6, endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH))
        }
        TimePickerBuilder(this) { date, v ->
            val currentDate = DateFormatUtil.yyyy_MM_Two().format(date)
            selectDate = date
            changeStatus()
            orderViewModel.orderMonth.value = currentDate

        }.setType(BooleanArray(6).apply {
            set(0, true)
            set(1, true)
            set(2, false)
            set(3, false)
            set(4, false)
            set(5, false)
        })// 默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setTitleSize(14)//标题文字大小
                .setTitleText("")//标题文字
                .setContentTextSize(16)
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(true)//是否循环滚动
                .setRangDate(startCalendar, endCalendar)
                .setDate(
                        if (selectDate == null) {
                            endCalendar
                        } else {
                            Calendar.getInstance().apply {
                                time = selectDate
                            }
                        }
                )
                .setSubmitColor(Color.parseColor("#25282D"))//确定按钮文字颜色
                .setCancelColor(Color.parseColor("#C3C4C7"))//取消按钮文字颜色
                .setTitleBgColor(Color.parseColor("#F6F6F6"))//标题背景颜色 Night mode
                .setTitleColor(Color.parseColor("#25282D"))
                .setTitleText("选择日期")
                .setBgColor(Color.parseColor("#F6F6F6"))//滚轮背景颜色 Night mode
                .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build().show()
    }

    companion object {
        const val ORDER_INDEX = "ORDER_INDEX"
        const val IS_SELF = "IS_SELF"

        @JvmStatic
        fun start(context: Context?, position: Int, isSelf: Boolean) {
            SndoData.event(SndoData.XLT_EVENT_USER_ORDER)
            context?.startActivity(Intent(context, OrderActivity::class.java).apply {
                putExtra(ORDER_INDEX, position)
                putExtra(IS_SELF, isSelf)
            })
        }
    }
}