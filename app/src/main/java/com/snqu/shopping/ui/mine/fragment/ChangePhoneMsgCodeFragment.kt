package com.snqu.shopping.ui.mine.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.os.KeyboardUtils
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.jakewharton.rxbinding2.widget.RxTextView
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.msg_code_fragment.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * desc:
 * time: 2019/8/23
 * @author 银进
 */
class ChangePhoneMsgCodeFragment : SimpleFrag() {
    private val countDownTimer by lazy {
        object : CountDownTimer(60000, 1000) {
            override fun onFinish() {
                tv_send_code.isEnabled = true
                tv_send_code.text = "获取验证码"
            }

            override fun onTick(millisUntilFinished: Long) {
                tv_send_code.isEnabled = false
                tv_send_code.text = "重新发送(${millisUntilFinished / 1000}s)"
            }

        }
    }
    private val phone by lazy {
        arguments?.getString(EXTRA_PHONE) ?: ""
    }
    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    override fun getLayoutId() = R.layout.change_phone_msg_code_fragment


    override fun init(savedInstanceState: Bundle?) {
        KeyboardUtils.showSoftInput(activity)
        titleBar.visibility = View.GONE
        StatusBar.setStatusBar(activity, true)
        addAction(Constant.Event.CHANGE_PHONE_SUCCESS)
        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.CHANGE_PHONE_VERIFY_CODE->{
                    showToastShort(it.message)
                    if (it.successful) {
                       ChangePhoneBindFragment.start(activity)
                    }
                }
                ApiHost.CHANGE_PHONE_CODE->{
                    if (it.successful) {
                        countDownTimer.start()
                    }
                    showToastShort(it.message)
                }
            }

        })
        initView()

    }

    /**
     * 初始化View
     */
    @SuppressLint("CheckResult", "SetTextI18n")
    private fun initView() {
        countDownTimer.start()
        tv_phone.text = "验证码已发送至+86 $phone"
        RxTextView.textChanges(et_code).subscribe {
            if (it.isNullOrBlank()) {
                img_clear.visibility = View.GONE
            } else {
                img_clear.visibility = View.VISIBLE
            }
            tv_ensure.isEnabled = et_code.length() == 6
        }

        tv_ensure.onClick {
            userViewModel.doChangePhoneVerifyCode(phone.replace(" ", ""), et_code.text.toString())
        }
        img_clear.onClick {
            et_code.setText("")
        }
        img_back.onClick {
            finish()
        }
        tv_send_code.onClick {
            userViewModel.doChangePhoneCode(phone.replace(" ", ""))
        }
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        if (event?.action == Constant.Event.CHANGE_PHONE_SUCCESS) {
            finish()
        }
    }
    companion object {
        private const val EXTRA_PHONE = "EXTRA_PHONE"
        fun start(context: Context?, phone: String) {
            val fragParam = SimpleFragAct.SimpleFragParam("",
                    ChangePhoneMsgCodeFragment::class.java, Bundle().apply {
                putString(EXTRA_PHONE, phone)
            })
            SimpleFragAct.start(context, fragParam)
        }
    }
}