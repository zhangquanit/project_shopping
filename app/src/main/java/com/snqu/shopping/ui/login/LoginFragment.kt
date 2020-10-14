package com.snqu.shopping.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.LContext
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.SPUtils
import com.jakewharton.rxbinding2.view.RxView
import com.snqu.shopping.App
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.InvitedEntity
import com.snqu.shopping.data.user.entity.PushEntity
import com.snqu.shopping.data.user.entity.UserEntity
import com.snqu.shopping.ui.login.hepler.WXLoginHelper
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.main.MainActivity
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.util.ThirdLoginUtil
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.statistics.StatisticInfo
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.login_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


/**
 * desc:
 * time: 2019/8/23
 * @author 银进
 */
class LoginFragment : SimpleFrag() {

    private var pushEntity: PushEntity? = null

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }
    private val loadingDialog by lazy {
        LoadingDialog(context, "跳转中...", true)
    }

    //微信的sid做中转用
    private var sid: String? = null

    override fun getLayoutId() = R.layout.login_fragment

    @SuppressLint("CheckResult", "SetTextI18n")
    override fun init(savedInstanceState: Bundle?) {

        pushEntity = arguments?.get("push_data") as PushEntity?
        titleBar.visibility = View.GONE
        addAction(Constant.Event.LOGIN_SUCCESS)
        addAction(Constant.Event.WX_CODE)
        StatusBar.setStatusBar(activity, true)

        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                //  手机号登录
                ApiHost.SEND_BIND_CODE -> {
                    showToastShort(it.message)
                    // 手机正常登录，跳转验证码界面
                    if (it.successful) {
                        val invitedEntity = it.data as InvitedEntity
                        SPUtils.getInstance().put(Constant.PREF.IS_NEW, invitedEntity.is_new)
                        val phone = et_phone.text.toString().replace(" ", "")
                        val isWechatLogin = ll_wx_login.visibility == View.GONE
                        MsgCodeFragment.start(activity, phone, sid
                                ?: "", invitedEntity.invited, invitedEntity.canSkipInvited, invitedEntity.is_new, isWechatLogin)
                    }
                }
                // 微信登录
                ApiHost.LOGIN_WX -> {
                    if (it.successful) {
                        val userEntity = it.data as UserEntity
                        // 如果token为空，表示是微信登录的新用户，则直接走手机号注册流程
                        if (userEntity.token == null) {
                            sid = userEntity.sid
                        } else {
                            // 微信登录的老用户
                            if (userEntity.inviter != null && userEntity.invited == "1") {
                                EventBus.getDefault().post(PushEvent(Constant.Event.LOGIN_SUCCESS))
                                StatisticInfo().login(userEntity._id, userEntity.is_new)
                                showToastShort(it.message)
                            } else {  //未绑定邀请码，看看
                                if (TextUtils.equals(userEntity.invite_link, "0")) {
                                    InviteCodeFragment.start(activity,
                                            userEntity.phone ?: "",
                                            "",
                                            userEntity.token,
                                            userEntity._id ?: "",
                                            userEntity.canSkipInvited)
                                } else {
                                    UserClient.saveLoginUser(userEntity)
                                    EventBus.getDefault().post(PushEvent(Constant.Event.LOGIN_SUCCESS))
                                    StatisticInfo().login(userEntity._id, userEntity.is_new)
                                    showToastShort("登录成功")
                                }
                            }
                        }
                        ll_wx_login.visibility = View.GONE
                    } else {
                        showToastShort(it.message)
                    }
                }
            }

        })
        initView()
    }

    /**
     * 初始化View
     */
    @SuppressLint("CheckResult")
    private fun initView() {
        et_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                if (charSequence == null || charSequence.isEmpty()) {
                    img_clear.visibility = View.GONE
                    tv_send_msg_code.isEnabled = false
                    return
                } else {
                    img_clear.visibility = View.VISIBLE
                }

                val stringBuilder = StringBuilder()
                for (i in charSequence.indices) {
                    if (i != 3 && i != 8 && charSequence[i] == ' ') {
                        continue
                    } else {
                        stringBuilder.append(charSequence[i])
                        if ((stringBuilder.length == 4 || stringBuilder.length == 9) && stringBuilder[stringBuilder.length - 1] != ' ') {
                            stringBuilder.insert(stringBuilder.length - 1, ' ')
                        }
                    }
                }
                if (stringBuilder.toString() != charSequence.toString()) {
                    var index = start + 1
                    if (stringBuilder[start] == ' ') {
                        if (before == 0) {
                            index++
                        } else {
                            index--
                        }
                    } else {
                        if (before == 1) {
                            index--
                        }
                    }
                    et_phone.setText(stringBuilder.toString())
                    et_phone.setSelection(index)
                }
                tv_send_msg_code.isEnabled = stringBuilder.length == 13
            }
        })
        img_clear.onClick {
            et_phone.setText("")
        }
        img_wx_login.onClick {
            ThirdLoginUtil.loginWX(loadingDialog, WXLoginHelper.FROM_LOGINFRAGMENT)
        }
        img_back.onClick {
            finish()
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
        RxView.clicks(tv_send_msg_code)
                .throttleFirst(1500, TimeUnit.MILLISECONDS)
                .subscribe {
                    val phone = et_phone.text.toString().replace(" ", "")
                    if (phone.matches(Regex(Constant.Match.PHONE_MATCH))) {
                        userViewModel.doNewLoginCode(phone)
                    } else {
                        showToastShort("手机号格式不正确")
                    }
                }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        when (event?.action) {
            Constant.Event.LOGIN_SUCCESS -> {
                if (!TextUtils.isEmpty(App.mApp.umengDeviceToken)) {
                    userViewModel.bindDevice(App.mApp.umengDeviceToken);
                }
                if (pushEntity != null) {
                    val intent = Intent(LContext.getContext(), MainActivity::class.java)
                    intent.action = "push"
                    intent.putExtra("push_data", pushEntity)
                    startActivity(intent)
                }
                finish()
            }
            Constant.Event.WX_CODE -> {
                if (WXLoginHelper.codePage != WXLoginHelper.FROM_LOGINFRAGMENT) {
                    return
                }
                when (event.data.toString()) {
                    "0", "1" -> {
                        loadingDialog.dismiss()
                    }
                    else -> {
                        userViewModel.doLoginWX(event.data.toString())
                        loadingDialog.dismiss()
                    }
                }
            }
        }
    }


    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("",
                    LoginFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }

        @JvmStatic
        fun start(context: Context?, pushEntity: PushEntity) {
            val fragParam = SimpleFragAct.SimpleFragParam("",
                    LoginFragment::class.java)
            fragParam.paramBundle = Bundle()
            fragParam.paramBundle.putSerializable("push_data", pushEntity)
            SimpleFragAct.start(context, fragParam)
        }
    }
}