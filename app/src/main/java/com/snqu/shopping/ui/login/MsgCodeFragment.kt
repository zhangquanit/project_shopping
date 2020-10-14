package com.snqu.shopping.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.os.KeyboardUtils
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.snqu.shopping.App
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.UserEntity
import com.snqu.shopping.ui.login.dialog.BindWXDialog
import com.snqu.shopping.ui.login.dialog.CallBack
import com.snqu.shopping.ui.login.hepler.WXLoginHelper
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.util.CommonUtil
import com.snqu.shopping.util.ThirdLoginUtil
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.statistics.StatisticInfo
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.msg_code_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


/**
 * desc:
 * time: 2019/8/23
 * @author 银进
 */
class MsgCodeFragment : SimpleFrag() {
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
    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }
    private val phone by lazy {
        arguments?.getString(EXTRA_PHONE) ?: ""
    }
    private val sid by lazy {
        arguments?.getString(EXTRA_SID) ?: ""
    }
    private val invCode by lazy {
        arguments?.getString(EXTRA_INVCODE) ?: "1"
    }
    private val canSkipInvited by lazy {
        arguments?.getInt(EXTRA_SKIP_INVITED) ?: 0
    }

    private val isWechatLogin by lazy {
        arguments?.getBoolean(EXTRA_IS_WECHAT_LOGIN) ?: false
    }

    private var userToken: String = ""

    private val loadingDialog by lazy {
        LoadingDialog(context, "跳转中...", true)
    }
    private var userEntity: UserEntity? = null
    private val bindWXDialog by lazy {
        BindWXDialog().apply {
            callBack = object : CallBack {
                override fun bindWx() {
                    ThirdLoginUtil.loginWX(loadingDialog, WXLoginHelper.FROM_MSGCODE)
                }
            }
        }
    }

    override fun getLayoutId() = R.layout.msg_code_fragment


    override fun init(savedInstanceState: Bundle?) {
        addAction(Constant.Event.WX_CODE)
        KeyboardUtils.showSoftInput(activity)
        titleBar.visibility = View.GONE
        StatusBar.setStatusBar(activity, true)
        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.VERIFY_CODE -> {
                    loadingDialog.dismiss()
                    if (it.successful) {
                        userEntity = it.data as UserEntity

                        userToken = userEntity?.token ?: ""
                        if (App.devEnv) {
                            userEntity?.wechat_info = "1"
                        }

                        if (TextUtils.equals(userEntity?.wechat_info, "0")) {
                            //没有绑定微信
                            if (!(activity?.isFinishing!!)) {
                                bindWXDialog.show(childFragmentManager, "BindWXDialog")
                            }
                        } else if (TextUtils.equals(userEntity?.invite_link, "0")) {
                            InviteCodeFragment.start(activity,
                                    phone,
                                    sid,
                                    userToken,
                                    userEntity?._id ?: "",
                                    canSkipInvited)
                        } else {
                            loginSuccess()
                        }
                    } else {
                        showToastShort(it.message)
                    }
                }
                // 登录流程
                ApiHost.NEW_CODE_LOGIN, ApiHost.WX_LOGIN_BIND_PHONE -> {
                    loadingDialog.dismiss()
                    if (it.successful && it.data != null) {
                        userEntity = it.data as UserEntity
                        userEntity?.token?.let {
                            userToken = it
                        }
                        if (TextUtils.equals(userEntity?.wechat_info, "0")) {
                            //没有绑定微信
                            if (!(activity?.isFinishing!!)) {
                                bindWXDialog.show(childFragmentManager, "BindWXDialog")
                            }
                        } else {
                            val code = et_code.text.toString()
                            if (TextUtils.equals(userEntity?.invite_link, "0")) {
                                InviteCodeFragment.start(activity,
                                        phone,
                                        sid,
                                        userToken,
                                        userEntity?._id ?: "",
                                        canSkipInvited)
                            } else {
                                loginSuccess()
                            }
                        }
                    } else {
                        showToastShort(it.message)
                    }

                }

                ApiHost.SEND_BIND_CODE -> {
                    if (it.successful) {
                        countDownTimer.start()
                    }
                    showToastShort(it.message)
                }
                ApiHost.BIND_WX -> {
                    bindWXDialog.dismiss()
                    if (it.successful) {
                        if (it.data == null) {
                            userViewModel.doWXCodeLogin(phone, et_code.text.toString(), sid)
                        } else {
                            userEntity = it.data as UserEntity
                            if (TextUtils.equals(userEntity?.invite_link, "0")) {
                                InviteCodeFragment.start(activity,
                                        phone,
                                        sid,
                                        userToken,
                                        userEntity?._id ?: "",
                                        canSkipInvited)
                            } else {
                                loginSuccess()
                            }
                        }
                    } else {
                        showToastShort(it.message)
                    }
                }
                ApiHost.FIND_INVITER_CODE -> {
                    loadingDialog.dismiss()
                    if (it.successful) {
                        if (it.data == null) {
                            userViewModel.doWXCodeLogin(phone, et_code.text.toString(), sid)
                        } else {
                            userEntity = it.data as UserEntity
                            userEntity?.token?.let {
                                userToken = it
                            }
                            if (TextUtils.equals(userEntity?.invite_link, "0")) {
                                InviteCodeFragment.start(activity,
                                        phone,
                                        sid,
                                        userToken,
                                        userEntity?._id ?: "",
                                        canSkipInvited)
                            } else {
                                loginSuccess()
                            }
                        }
                    } else {
                        showToastShort(it.message)
                    }
                }
                ApiHost.INVITE_CODE -> if (it.successful) {
                    loadingDialog.dismiss()
                    val user = UserClient.getUser()
                    user.invite_link = "1"
                    UserClient.updateUser(user)
                    EventBus.getDefault().post(PushEvent(Constant.Event.BIND_INVITE_SUCCESS))
                    EventBus.getDefault().post(PushEvent(Constant.Event.LOGIN_SUCCESS))
                    StatisticInfo().login(user._id, user.is_new)
                    finish()
                } else {
                    showToastShort(it.message)
                }
            }

        })
        initView()
    }

    override fun onBackPressedSupport(): Boolean {
        UserClient.loginOut()
        return super.onBackPressedSupport()
    }

    /**
     * 登录成功
     */
    private fun loginSuccess() {
        if (!TextUtils.isEmpty(userToken)) {
            userEntity?.token = userToken
        }
        UserClient.saveLoginUser(userEntity)
        StatisticInfo().login(userEntity?._id ?: "0", userEntity?.is_new ?: "0")
        EventBus.getDefault().post(PushEvent(Constant.Event.LOGIN_SUCCESS))
        showToastShort("登录成功")
        finish()
    }

    /**
     * 初始化View
     */
    @SuppressLint("CheckResult", "SetTextI18n")
    private fun initView() {
        tv_phone.text = "验证码已发送至+86 $phone"
        countDownTimer.start()
        RxTextView.textChanges(et_code).subscribe {
            if (it.isNullOrBlank()) {
                img_clear.visibility = View.GONE
            } else {
                img_clear.visibility = View.VISIBLE
            }
            tv_ensure.isEnabled = et_code.length() == 6
        }

        tv_ensure.onClick {
            loadingDialog.show()

            var cooperationChannel = CommonUtil.getCooperationChannel()

            val code = et_code.text.toString()
            if (TextUtils.isEmpty(cooperationChannel)) {
                if (isWechatLogin && !TextUtils.isEmpty(sid)) {
                    var code = invCode
                    // 为0，走新注册流程
                    if (code == "0") {
//                        userViewModel.doVerifyCode(phone, et_code.text.toString())
                        userViewModel.doInviterCode(phone, et_code.text.toString(), sid)
                    } else if (code == "1") {
                        userViewModel.doNewCodeLogin(phone, et_code.text.toString())
                    }
//                    userViewModel.doWXCodeLogin(phone, et_code.text.toString(), sid)
                } else {
                    userViewModel.doVerifyCode(phone, et_code.text.toString())
                }
            } else {
                // 二维码
                if (TextUtils.isEmpty(sid)) {
                    userViewModel.doNewCodeLogin(phone, code, cooperationChannel)
                } else {
                    userViewModel.doWXCodeLogin(phone, code, sid, cooperationChannel)
                }
            }


        }
        img_back.onClick {
            finish()
        }
        img_clear.onClick {
            et_code.setText("")
        }

        RxView.clicks(tv_send_code)
                .throttleFirst(1500, TimeUnit.MILLISECONDS)
                .subscribe {
                    userViewModel.doNewLoginCode(phone)
                }

        tv_agreement.onClick {
            val webViewParam = WebViewFrag.WebViewParam()
            webViewParam.url = Constant.WebPage.USER_AGREEMENT
            WebViewFrag.start(mContext, webViewParam)
        }

        tv_privacy_policy.onClick {
            val webViewParam = WebViewFrag.WebViewParam()
            webViewParam.url = Constant.WebPage.PRIVACY_PROTOCAL
            WebViewFrag.start(mContext, webViewParam)
        }
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        when {
            event?.action == Constant.Event.LOGIN_SUCCESS -> {
                finish()
            }
            event?.action == Constant.Event.WX_CODE -> {
                if (WXLoginHelper.codePage != WXLoginHelper.FROM_MSGCODE) {
                    return
                }
                when (event.data.toString()) {
                    "0", "1" -> {
                        loadingDialog.dismiss()
                    }
                    else -> {
                        userViewModel.doBindWX(event.data.toString(), userEntity?.token ?: "")
                        loadingDialog.dismiss()
                    }
                }

            }
        }
    }

    companion object {
        private const val EXTRA_PHONE = "EXTRA_PHONE"
        private const val EXTRA_SID = "EXTRA_SID"
        private const val EXTRA_INVCODE = "EXTRA_INVCODE" // 是否绑定邀请
        private const val EXTRA_SKIP_INVITED = "EXTRA_SKIP_INVITED" //是否允许出现跳过按钮，为1则可以跳过
        private const val EXTRA_IS_NEW = "EXTRA_IS_NEW"
        private const val EXTRA_IS_WECHAT_LOGIN = "EXTRA_IS_WECHAT_LOGIN"

        @JvmStatic
        fun start(context: Context?, phone: String, sid: String?, invCode: String, canSkipInvited: Int, is_new: String, is_wechatLogin: Boolean) {
            val fragParam = SimpleFragAct.SimpleFragParam("",
                    MsgCodeFragment::class.java, Bundle().apply {
                putString(EXTRA_PHONE, phone.replace(" ", ""))
                if (!TextUtils.isEmpty(sid)) {
                    putString(EXTRA_SID, sid)
                }
                putString(EXTRA_INVCODE, invCode)
                putInt(EXTRA_SKIP_INVITED, canSkipInvited)
                putString(EXTRA_IS_NEW, is_new)
                putBoolean(EXTRA_IS_WECHAT_LOGIN, is_wechatLogin)
            })
            SimpleFragAct.start(context, fragParam)
        }
    }
}