package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.SPUtil
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.LogUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.main.MainActivity
import com.snqu.shopping.util.ext.onClick
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.account_phone_valide_frag.*

/**
 * 账号注销-手机号验证
 * @author zhangquan
 */
class UserCancelPhoneValideFrag : SimpleFrag() {

    companion object {
        fun start(context: Context?, list: ArrayList<String>, other: String) {
            val fragParam = SimpleFragAct.SimpleFragParam("手机号验证",
                    UserCancelPhoneValideFrag::class.java)
            fragParam.paramBundle = Bundle().apply {
                putStringArrayList(Constant.Bundle.USER_CANCEL_CHECK_LIST, list)
                putString(Constant.Bundle.USER_CANCEL_OTHER, other)
            }
            SimpleFragAct.start(context, fragParam)
        }
    }

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    private val list by lazy {
        arguments?.getStringArrayList(Constant.Bundle.USER_CANCEL_CHECK_LIST)
    }

    private val other by lazy {
        arguments?.getString(Constant.Bundle.USER_CANCEL_OTHER) ?: ""
    }

    var isGetCode = false
    var countDownTimer: CountDownTimer? = null
    val TIME = "ACCOUNT_CODE_TIME"

    private var mLoadingDialog: LoadingDialog? = null

    override fun getLayoutId(): Int {
        return R.layout.account_phone_valide_frag
    }


    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        startTimer()
        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.LOGOUT_SENDCODE -> {
                    isGetCode = false
//                    if (!it.successful) {
                    if (!TextUtils.isEmpty(it.message)) {
                        ToastUtil.show(it.message)
                    }
//                    }
                }
                ApiHost.ACCOUNT_LOGOUT -> {
                    closeLoadDialog()
                    if (!TextUtils.isEmpty(it.message)) {
                        ToastUtil.show(it.message)
                    }
                    if (it.successful) {
                        val user = UserClient.getUser()
                        user.is_logout = "1" //冻结中
                        UserClient.updateUser(user)
                        MainActivity.startForPage(mContext, 4)
                        finish()
                    }
                }
            }
        })
        et_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (null != s && s.isNotEmpty()) {
                    iv_clear.visibility = View.VISIBLE
                } else {
                    iv_clear.visibility = View.INVISIBLE
                }

                if (s?.length == 11 && !isGetCode) {
                    tv_getCode.isEnabled = true
                } else {
                    tv_getCode.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        et_code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (null != s && s.isNotEmpty()) {
                    iv_clear2.visibility = View.VISIBLE
                } else {
                    iv_clear2.visibility = View.INVISIBLE
                }

                if (s?.length == 6 && et_phone.length() == 11) {
                    tv_sure.isEnabled = true
                } else {
                    tv_sure.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        tv_getCode.onClick {
            val phone = et_phone.text.toString()
            getCode(phone)
        }

        tv_sure.onClick {
            clearAccount()
        }

        et_phone.post {
            et_phone.setText(UserClient.getUser().phone)
        }

        iv_clear.onClick {
            et_phone.setText("")
        }
    }

    fun getCode(phone: String) {
        isGetCode = true
        setTime()
        userViewModel.logoutSendCode(phone)
    }

    fun clearAccount() {
        var code = et_code.text.toString()
        if (TextUtils.isEmpty(code) || code.length != 6) {
            ToastUtil.show("请输入正确的验证码")
        } else {
            showLoadingDialog("验证中")
            list?.let {
                if (it.contains("其他") && !TextUtils.isEmpty(other)) {
                    it.remove("其他")
                    it.add(other)
                }
                it.toTypedArray().let { reasonList ->
                    var reason = StringBuffer()
                    reasonList
                            .forEachIndexed { index, s ->
                                if (index == reasonList.size - 1) {
                                    reason.append("$s")
                                } else {
                                    reason.append("$s,")
                                }
                            }
                    userViewModel.accountLogou(
                            et_phone.text.toString(),
                            code,
                            reason.toString())
                }
            }
        }
    }

    fun setTime() {
        SPUtil.setLong(TIME, System.currentTimeMillis() / 1000)
        startTimer()
    }

    fun startTimer() {
        val now = System.currentTimeMillis() / 1000
        val last = SPUtil.getLong(TIME, 0)
        if (last == 0L) {
            return
        }
        val diff = now - last
        if (diff < 6) {
            tv_getCode.isEnabled = false
            var count: Long = 6 - diff.toLong()
            countDownTimer = object : CountDownTimer(count * 1000, 1000) {
                override fun onFinish() {
                    tv_getCode.isEnabled = true
                    tv_getCode.text = "获取验证码"
                }

                override fun onTick(millisUntilFinished: Long) {
                    tv_getCode.isEnabled = false
                    tv_getCode.text = "重新获取(${millisUntilFinished / 1000}s)"
                }

            }
        }
    }

    fun showLoadingDialog(content: String?) {
        mLoadingDialog = LoadingDialog.showBackCancelableDialog(mContext, content)
    }


    fun closeLoadDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog?.dismiss()
        }
    }

}