package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.SpanUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.common.ui.AlertDialogView
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.AlipayInfoEntity
import com.snqu.shopping.data.user.entity.PigContract
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.ui.main.frag.WebViewFrag.WebViewParam
import com.snqu.shopping.ui.mine.dialog.CallBack
import com.snqu.shopping.ui.mine.dialog.WithdrawalSuccessDialog
import com.snqu.shopping.util.NumberUtil
import com.snqu.shopping.util.ext.onClick
import common.widget.dialog.EffectDialogBuilder
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.withdrawal_fragment.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * desc:
 * time: 2019/8/14
 * @author 银进
 */
class WithdrawalFragment : SimpleFrag() {
    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }
    private var minWithdrawalMoney = 0L
    private var alipayInfoEntity: AlipayInfoEntity? = null
    private var loadingDialog: LoadingDialog? = null
    override fun getLayoutId() = R.layout.withdrawal_fragment

    override fun init(savedInstanceState: Bundle?) {
        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.ALIPAY_INFO -> {
                    if (it.successful && it.data != null) {
                        loadingview.visibility = View.GONE
                        alipayInfoEntity = it.data as AlipayInfoEntity
                        tv_account_name.text = SpanUtils()
                                .append("提现人：").setForegroundColor(Color.parseColor("#848487")).setFontSize(14, true)
                                .append(alipayInfoEntity?.realname
                                        ?: "").setForegroundColor(Color.parseColor("#25282D")).setFontSize(14, true)
                                .create()
                        tv_account_num.text = SpanUtils()
                                .append("支付宝：").setForegroundColor(Color.parseColor("#848487")).setFontSize(14, true)
                                .append(alipayInfoEntity?.account
                                        ?: "").setForegroundColor(Color.parseColor("#25282D")).setFontSize(14, true)
                                .create()

                    } else {
                        loadingview.setStatus(LoadingStatusView.Status.FAIL)
                    }
                }

                ApiHost.BALANCE_WITHDRAW -> {
                    dissmissDialog()
                    if (it.successful) {
                        tv_balance.text = "余额：¥${NumberUtil.saveTwoPoint(UserClient.canWithdrawal - Math.round(et_input_money.text.toString().toFloat() * 100))}"
                        WithdrawalSuccessDialog().apply {
                            arguments = Bundle().apply {
                                putString(WithdrawalSuccessDialog.EXTRA_MONEY, this@WithdrawalFragment.et_input_money.text.toString())
                                putString(WithdrawalSuccessDialog.EXTRA_TIP, it?.data?.toString())
                            }
                            callBack = object : CallBack {
                                override fun dismissSuccess() {
                                    this@WithdrawalFragment.et_input_money.setText("")
                                }
                            }
                        }.show(childFragmentManager, "WithdrawalSuccessDialog")
                    } else {
                        showToastShort(it.message)
                    }
                }
                ApiHost.GET_CHECK_CONTRACT -> {
                    dissmissDialog()
                    if (it.successful) {
                        if (it.data != null) {
                            val pigContract = it.data as PigContract
                            if (pigContract.requireCheck == 1) {
                                if (!TextUtils.isEmpty(pigContract.url)) {
                                    val dialogView: AlertDialogView = AlertDialogView(activity)
                                            .setTitle("提现服务协议签订")
                                            .setContent(pigContract.tips)
                                            .setLeftBtn("取消")
                                            .setRightBtn("去签订") {
                                                val webViewParam = WebViewParam()
                                                webViewParam.url = pigContract.url
                                                WebViewFrag.start(mContext, webViewParam)
                                            }
                                    EffectDialogBuilder(activity)
                                            .setContentView(dialogView)
                                            .setCancelable(false)
                                            .setCancelableOnTouchOutside(false)
                                            .show()
                                }
                            } else {
                                showLoading()
                                userViewModel.doWithdraw(Math.round(et_input_money.text.toString().toFloat() * 100), UserClient.canWithdrawal)
                            }
                        }
                    } else {
                        showToastShort(it.message)
                    }
                }
                "Tag_Tip" -> {
                    if (it.successful) {
                        if (it.data != null) {
                            val pigContract = it.data as PigContract
                            if (TextUtils.isEmpty(pigContract.url)) {
                                tv_tip.visibility = View.GONE
                            } else {
                                tv_tip.visibility = View.VISIBLE
                                tv_tip.onClick {
                                       val webViewParam = WebViewParam()
                                    webViewParam.url = pigContract.url
                                    WebViewFrag.start(mContext, webViewParam)
                                }
                            }
                        }
                    }
                }
            }
        })
        addAction(Constant.Event.BIND_ALIPAY_SUCCESS)
        initView()
        refreshData()
    }

    private fun initView() {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        minWithdrawalMoney = UserClient.getUser().config?.xlt_min_withdraw_amount ?: 500L
        tv_balance.text = "余额：¥${NumberUtil.saveTwoPoint(UserClient.canWithdrawal)}"
        tv_all.onClick {
            et_input_money.setText(NumberUtil.saveTwoPoint(UserClient.canWithdrawal))
        }
        img_add_account.onClick {
            BindAlipayFragment.start(activity, true)
        }
        //都是条状到绑定支付宝页面
        tv_account_change.onClick {
            BindAlipayFragment.start(activity, alipayInfoEntity, true)
        }
        img_delete.onClick {
            et_input_money.setText("")
        }
        tv_sure.onClick {
            showLoading()
            // 先判断是否需要签署协议
            if (UserClient.getUser().document_id != 1) {
                userViewModel.checkContract(et_input_money.text.toString().toFloat() * 100)
            } else {
                userViewModel.doWithdraw(Math.round(et_input_money.text.toString().toFloat() * 100), UserClient.canWithdrawal)
            }
        }
        et_input_money.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 0) {
                    tv_sure.isEnabled = false
                    et_input_money.textSize = 18F
                    tv_notice_error.visibility = View.INVISIBLE
                    img_delete.visibility = View.INVISIBLE
                } else {
                    tv_sure.isEnabled = et_input_money.text.toString().toFloat() != 0F
                    img_delete.visibility = View.VISIBLE
                    et_input_money.textSize = 30f
                    when {
                        et_input_money.text.toString().toDouble() < ((minWithdrawalMoney * 1.00) / 100) -> {
                            tv_notice_error.visibility = View.VISIBLE
                            tv_notice_error.text = "提现金额不能少于${NumberUtil.saveTwoPoint(minWithdrawalMoney)}元"
                        }
                        et_input_money.text.toString().toDouble() > ((UserClient.canWithdrawal * 1.00) / 100) -> {
                            tv_notice_error.visibility = View.VISIBLE
                            tv_notice_error.text = "余额不足 "
                        }
                        else -> tv_notice_error.visibility = View.INVISIBLE
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {

                    var str = s.toString()
                    //输入两位.
                    if (str.length >= 2 && str.endsWith(".")) {
                        if (str.indexOf(".", 0, false) != str.length - 1) {
                            str = str.substring(0, str.length - 1)
                            et_input_money.setText(str)
                            et_input_money.setSelection(str.length) //光标移到最后
                            return
                        }
                    }

                    //删除“.”后面超过2位后的数据
                    if (str.contains(".")) {
                        if (s.length - 1 - str.indexOf(".") > 2) {
                            val s = str.subSequence(0,
                                    str.indexOf(".") + 2 + 1)
                            et_input_money.setText(s)
                            et_input_money.setSelection(s.length) //光标移到最后
                        }
                    }
                    //如果"."在起始位置,则起始位置自动补0
                    if (str.trim().startsWith(".")) {
                        val s = "0$s"
                        et_input_money.setText(s)
                        et_input_money.setSelection(2)
                        return
                    }

                    //如果起始位置为0,且第二位跟的不是".",则无法后续输入
                    if (str.startsWith("0")
                            && str.trim().length > 1) {
                        if (str.substring(1, 2) != ".") {
                            et_input_money.setText(s.subSequence(0, 1))
                            et_input_money.setSelection(1)
                            return
                        }
                    }
                }
            }
        })

        loadingview.btn.onClick {
            refreshData()
        }

        userViewModel.checkContract()
    }

    /**
     * 刷新接口
     */
    private fun refreshData() {
        if (UserClient.getUser().bind_alipay == "1") {
            loadingview.visibility = View.VISIBLE
            loadingview.setStatus(LoadingStatusView.Status.LOADING)
            userViewModel.doAlipayInfo()
            cos_have_account.visibility = View.VISIBLE
            rl_no_account.visibility = View.GONE
        } else {
            loadingview.visibility = View.GONE
            cos_have_account.visibility = View.GONE
            rl_no_account.visibility = View.VISIBLE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        if (event?.action == Constant.Event.BIND_ALIPAY_SUCCESS) {
            loadingview.visibility = View.VISIBLE
            loadingview.setStatus(LoadingStatusView.Status.LOADING)
            userViewModel.doAlipayInfo()
            cos_have_account.visibility = View.VISIBLE
            rl_no_account.visibility = View.GONE
        }
    }

    private fun showLoading() {
        loadingDialog = LoadingDialog.showDialog(mContext, "正在提交中...")
    }

    private fun dissmissDialog() {
        if (null != loadingDialog) loadingDialog!!.dismiss()
    }

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("立即提现",
                    WithdrawalFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }
}