package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.SpanUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.entity.AlipayInfoEntity
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.bind_alipay_success_fragment.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * desc:
 * time: 2019/8/20
 * @author 银进
 */
class BindAlipaySuccessFragment : SimpleFrag() {
    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    private var alipayInfoEntity:AlipayInfoEntity?=null
    override fun getLayoutId() = R.layout.bind_alipay_success_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        addAction(Constant.Event.BIND_ALIPAY_SUCCESS)
        userViewModel.dataResult.observe(this, Observer {
            if (it?.tag == ApiHost.ALIPAY_INFO) {
                 alipayInfoEntity = it.data as AlipayInfoEntity
                if (it.successful) {
                    tv_account_name.text = SpanUtils()
                            .append("提现人：").setForegroundColor(Color.parseColor("#848487")).setFontSize(14,true)
                            .append(alipayInfoEntity?.realname?:"").setForegroundColor(Color.parseColor("#25282D")).setFontSize(14,true)
                            .create()
                    tv_account_num.text = SpanUtils()
                            .append("支付宝：").setForegroundColor(Color.parseColor("#848487")).setFontSize(14,true)
                            .append(alipayInfoEntity?.account?:"").setForegroundColor(Color.parseColor("#25282D")).setFontSize(14,true)
                            .create()
                } else {
                    showToastShort(it.message)
                }
            }
        })
        initView()
        refreshData()
    }

    /**
     * 刷新接口
     */
    private fun refreshData() {
        userViewModel.doAlipayInfo()
    }


    /**
     * 初始化View
     */
    private fun initView() {
        tv_account_change.onClick {
            BindAlipayFragment.start(activity,alipayInfoEntity,true)
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        if (event?.action == Constant.Event.BIND_ALIPAY_SUCCESS) {
            refreshData()
        }
    }

    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("绑定支付宝",
                    BindAlipaySuccessFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }
}