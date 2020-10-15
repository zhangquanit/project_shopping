package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.date.DateFormatUtil
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.blankj.utilcode.util.SpanUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.AlertDialogView
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.ListBalanceRecodeEntity
import com.snqu.shopping.data.user.entity.AccountTipsEntity
import com.snqu.shopping.data.user.entity.BalanceInfoEntity
import com.snqu.shopping.data.user.entity.BalanceRecodeEntity
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.mine.adapter.AccountDetailAdapter
import com.snqu.shopping.util.NumberUtil
import com.snqu.shopping.util.ext.onClick
import common.widget.dialog.EffectDialogBuilder
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.self_balance_fragment.*
import kotlinx.android.synthetic.main.self_balance_head.view.*
import java.util.*

/**
 * desc:
 * time: 2019/8/14
 * @author 银进
 */
class SelfBalanceFragment : SimpleFrag() {

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    private var type: String? = null //金额明细 type=null， 结算收益 type = 100 ，提现记录 type = 20

    private var mLoadingDialog: LoadingDialog? = null

    private val row = 20

    private val accountDetailAdapter by lazy {
        AccountDetailAdapter().apply {
            setHeaderAndEmpty(true)
            setOnLoadMoreListener({
                loadMoreData()
            }, recycler_view)
        }
    }

    private val loadingStatusView by lazy {
        LoadingStatusView(activity!!).apply {
            setOnBtnClickListener {
                refreshData()
            }
        }
    }
    private val headView by lazy {
        layoutInflater.inflate(R.layout.self_balance_head, null)
    }
    private val hashMapRecode by lazy {
        hashMapOf<String, ListBalanceRecodeEntity>()
    }

    //    private val currentYearMonth: String by lazy {
//        DateFormatUtil.yyyy_MM_Two().format(Date())
//    }
    private var yearMonth: String = DateFormatUtil.yyyy_MM_Two().format(Date())
    private var selectDate: Date = Date()
    private var balanceInfoEntity: BalanceInfoEntity? = null
    override fun getLayoutId() = R.layout.self_balance_fragment

    override fun init(savedInstanceState: Bundle?) {
        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.GET_ACCOUNT_TIPS -> {
                    if (it.successful && it.data != null) {
                        val accountTipsEntity = it.data as AccountTipsEntity
                        if (accountTipsEntity != null && accountTipsEntity.tips != null && !TextUtils.isEmpty(accountTipsEntity.tips.msg)) {
                            val dialogView: AlertDialogView = AlertDialogView(activity)
                                    .setTitle(accountTipsEntity.tips.title)
                                    .setContent(accountTipsEntity.tips.msg)
                                    .setRightBtn("确定")
                            EffectDialogBuilder(activity)
                                    .setContentView(dialogView)
                                    .setCancelable(false)
                                    .setCancelableOnTouchOutside(false)
                                    .show()
                        }
                    }
                }
                ApiHost.BALANCE_INFO -> {
                    if (it.successful && it.data != null) {
                        balanceInfoEntity = it.data as BalanceInfoEntity
                        if (balanceInfoEntity?.amount_useable != null) {
                            UserClient.canWithdrawal = balanceInfoEntity?.amount_useable
                        }
                        if (balanceInfoEntity?.unsettled_amount != null) {
                            UserClient.unsettled_amount = balanceInfoEntity?.unsettled_amount
                        }
                        if (balanceInfoEntity?.amount_useable != null) {
                            UserClient.amount_useable = balanceInfoEntity?.amount_useable
                        }
                        refreshView()
                        refresh_layout.finishRefresh(true)
                    }
                }
                ApiHost.BALANCE_RECODE -> {

                    val extraList = it.extra.toString().split("\$")
                    var key = extraList[0]
                    var page = extraList[1].toInt()
                    refresh_layout.finishRefresh(it?.successful)
                    if (it.successful) {
                        val data = it.data as ArrayList<BalanceRecodeEntity>
                        if (hashMapRecode[key] == null) {
                            hashMapRecode[key] = ListBalanceRecodeEntity(true)
                        }

                        if (page == 1) {
                            hashMapRecode[key]?.listBalanceRecodeEntity?.clear()   //清空数据源
                            if (data.isNotEmpty()) {
                                hashMapRecode[key]?.listBalanceRecodeEntity?.addAll(data) //添加到缓存
                            }
                            showNormalView(data)

                        } else if (data.isNotEmpty()) {
                            hashMapRecode[key]?.listBalanceRecodeEntity?.addAll(data)  //添加到缓存
                            addDataView(data)
                        }

                        //加载下一页
                        if (data.isNotEmpty()) {
                            hashMapRecode[key]?.hasNext = true
                            hashMapRecode[key]?.page = page + 1
                            accountDetailAdapter.loadMoreComplete(); //刷新成功
                        } else {
                            hashMapRecode[key]?.hasNext = false
                            accountDetailAdapter.loadMoreEnd(page == 1);//无下一页
                        }


                        if (page == 1 && data.isEmpty()) {
                            //第一页 无数据
                            showEmptyView()
                        }

                    } else {
                        if (page > 1) { //加载下一页数据失败
                            accountDetailAdapter.loadMoreFail()
                        } else if (accountDetailAdapter.data.isEmpty()) { //第一页加载失败
                            showErrorView()
                        } else { //下拉刷新失败
                            ToastUtil.show(it?.message)
                        }
                    }
                }
            }
        })
        initView()
        refreshData()
        userViewModel.getAccountTips();
    }

    /**
     * 加载更多，先判断缓存是否下一页在请求接口添加缓存
     */
    private fun loadMoreData() {
        //有缓存数据
        if (hashMapRecode.containsKey(yearMonth)) {
            //有下一页
            if (hashMapRecode[yearMonth]?.hasNext == true) {
                //网络请求就去加载下一页，然后添加缓存里面
                userViewModel.doBalanceRecode(yearMonth, hashMapRecode[yearMonth]?.page
                        ?: return, row, type)
            } else {
                //加载完成了
                accountDetailAdapter.loadMoreEnd(false)
            }
        } else {
            //这种情况基本上不存在，但是为了安全还是要写(默认加载第一页)
            userViewModel.doBalanceRecode(yearMonth, 1, row, type)
        }
    }

    /**
     * 刷新头部view
     */
    private fun refreshView() {
        headView.apply {
            //可提现
            tv_money.text = NumberUtil.saveTwoPoint(balanceInfoEntity?.amount_useable)
            //累计收益
            tv_all_money.text = NumberUtil.saveTwoPoint(balanceInfoEntity?.amount_total)
            //已提现金额
            tv_future_money.text = NumberUtil.saveTwoPoint(balanceInfoEntity?.withdraw_success_amount)
            //未结算金额
            tv_today_money.text = NumberUtil.saveTwoPoint(balanceInfoEntity?.unsettled_amount)
//            tv_settlement_day_num.text = "返利结算时间: ${UserClient.getUser()?.config?.xlt_rebate_time?:"0"}天"
            //冻结金
            tv_freeze_money.text = SpanUtils()
                    .append("提现中：").setForegroundColor(Color.parseColor("#25282D")).setFontSize(12, true)
                    .append(NumberUtil.saveOnePoint(balanceInfoEntity?.freeze_amount)).setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(12, true)
                    .append("元").setForegroundColor(Color.parseColor("#25282D")).setFontSize(12, true)
                    .create()
        }

    }

    /**
     * 刷新数据
     */
    private fun refreshData() {
        userViewModel.doBalanceInfo()
        if (TextUtils.isEmpty(type)) {
            userViewModel.doBalanceRecode(yearMonth, 1, row, type)
        } else {
            userViewModel.doBalanceRecode("", 1, row, type)
        }
    }

    /**
     * 初始化View
     */
    private fun initView() {
        StatusBar.setStatusBar(activity, false)
        titleBar.visibility = View.GONE

        val typeFace = Typeface.createFromAsset(activity?.assets, "fonts/withdrawal_font.ttf")
        headView.apply {
            tv_money.typeface = typeFace
            tv_all_money.typeface = typeFace
            tv_future_money.typeface = typeFace
            tv_today_money.typeface = typeFace

            money_detail_layout.onClick {
                money_detail_layout.isSelected = !money_detail_layout.isSelected
                if (money_detail_layout.isSelected) {
                    withdraw_layout.isSelected = false
                    settle_account_layout.isSelected = false
                    tv_settle_account.background = null
                    tv_withdraw.background = null
                    tv_settle_account.setTextColor(Color.parseColor("#25272D"))
                    tv_withdraw.setTextColor(Color.parseColor("#25272D"))
                    tv_money_detail.setTextColor(Color.parseColor("#FFFFFF"))
                    tv_money_detail.setBackgroundResource(R.drawable.self_balance_select_bg)
                    account_layout.visibility = View.VISIBLE
                    type = null
                    userViewModel.doBalanceRecode(yearMonth, 1, row, type)
                }
            }

            settle_account_layout.onClick {
                settle_account_layout.isSelected = !settle_account_layout.isSelected
                if (settle_account_layout.isSelected) {
                    withdraw_layout.isSelected = false
                    money_detail_layout.isSelected = false
                    tv_money_detail.background = null
                    tv_withdraw.background = null
                    tv_money_detail.setTextColor(Color.parseColor("#25272D"))
                    tv_withdraw.setTextColor(Color.parseColor("#25272D"))
                    tv_settle_account.setTextColor(Color.parseColor("#FFFFFF"))
                    tv_settle_account.setBackgroundResource(R.drawable.self_balance_select_bg)
                    account_layout.visibility = View.GONE
                    type = "100"
                    userViewModel.doBalanceRecode("", 1, row, type)
                }
            }

            withdraw_layout.onClick {
                withdraw_layout.isSelected = !withdraw_layout.isSelected
                if (withdraw_layout.isSelected) {
                    settle_account_layout.isSelected = false
                    money_detail_layout.isSelected = false
                    tv_money_detail.background = null
                    tv_settle_account.background = null
                    tv_money_detail.setTextColor(Color.parseColor("#25272D"))
                    tv_settle_account.setTextColor(Color.parseColor("#25272D"))
                    tv_withdraw.setTextColor(Color.parseColor("#FFFFFF"))
                    tv_withdraw.setBackgroundResource(R.drawable.self_balance_select_bg)
                    account_layout.visibility = View.GONE
                    type = "20"
                    userViewModel.doBalanceRecode("", 1, row, type)
                }
            }
        }




        refresh_layout.setOnRefreshListener {
            refreshData()
        }
        img_back.onClick {
            finish()
        }

        headView.run {
            tv_select_time.text = DateFormatUtil.yyyy_MM().format(Date())
            tv_select_time.onClick {
                val endCalendar = Calendar.getInstance()
                val startCalendar = Calendar.getInstance().apply {
                    set(endCalendar.get(Calendar.YEAR) - 6, endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH))
                }
                TimePickerBuilder(activity) { date, v ->
                    selectDate = date
                    tv_select_time.text = DateFormatUtil.yyyy_MM().format(date)
                    yearMonth = DateFormatUtil.yyyy_MM_Two().format(date)
//                    if (yearMonth == currentYearMonth) {
//                        //当前月一定要刷新
//                        userViewModel.doBalanceRecode(yearMonth, 1, row)
//                    } else {
                    //非当前月如果没有缓存就刷新
                    if (hashMapRecode.containsKey(yearMonth)) {
                        val value = hashMapRecode[yearMonth]
                        if (value?.listBalanceRecodeEntity?.isNullOrEmpty() == true) {
                            showEmptyView()
                        } else {
                            //先加载数据源到适配器里面
                            showNormalView(value?.listBalanceRecodeEntity ?: arrayListOf())
//                            //判断下一页是否可以加载
//                            if (value?.hasNext == true) {
//                                //说明有下一页
//                                accountDetailAdapter.loadMoreComplete()
//                            }
                        }
                    } else {
                        userViewModel.doBalanceRecode(yearMonth, 1, row, type)
                    }
//                    }

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
                        .isCyclic(false)//是否循环滚动
                        .setRangDate(startCalendar, endCalendar)
                        .setDate(Calendar.getInstance().apply {
                            time = selectDate
                        })
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
        }
        img_back.onClick {
            finish()
        }
        recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        recycler_view.adapter = accountDetailAdapter
        accountDetailAdapter.addHeaderView(headView)
    }


    /**
     * 展示正常返回的数据数据
     */
    private fun showNormalView(data: List<BalanceRecodeEntity>) {
        accountDetailAdapter.removeAllFooterView()
        accountDetailAdapter.setNewData(data)
    }

    /**
     * 加载下一页的数据数据
     */
    private fun addDataView(data: List<BalanceRecodeEntity>) {
        accountDetailAdapter.removeAllFooterView()
        //只是刷新改变了的部分
        accountDetailAdapter.addData(data)
    }

    /**
     * 展示null数据
     */
    private fun showEmptyView() {
        accountDetailAdapter.removeAllFooterView()
        accountDetailAdapter.setNewData(null)
        accountDetailAdapter.addFooterView(loadingStatusView.apply {
            setStatus(LoadingStatusView.Status.EMPTY.apply {
                text = "您还没有账户记录哦～"
            })
        })
    }

    /**
     * 展示网络或者请求错误数据
     */
    private fun showErrorView() {
        accountDetailAdapter.removeAllFooterView()
        accountDetailAdapter.setNewData(null)
        accountDetailAdapter.addFooterView(loadingStatusView.apply {
            setStatus(LoadingStatusView.Status.FAIL)
        })
    }


    fun showLoadingDialog(content: String?) {
        mLoadingDialog = LoadingDialog.showBackCancelableDialog(activity, content)
    }


    fun closeLoadDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog?.dismiss()
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("我的余额",
                    SelfBalanceFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }
}