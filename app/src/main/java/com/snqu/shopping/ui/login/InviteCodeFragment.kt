package com.snqu.shopping.ui.login

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.jakewharton.rxbinding2.widget.RxTextView
import com.snqu.shopping.App
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.InviterInfo
import com.snqu.shopping.data.user.entity.UserEntity
import com.snqu.shopping.ui.login.dialog.BindWXDialog
import com.snqu.shopping.ui.login.dialog.CallBack
import com.snqu.shopping.ui.login.hepler.WXLoginHelper
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ThirdLoginUtil
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.statistics.SndoData
import com.snqu.shopping.util.statistics.StatisticInfo
import common.widget.dialog.loading.LoadingDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.invite_code_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

/**
 * desc:
 * time: 2019/9/12
 * @author 银进
 */
class InviteCodeFragment : SimpleFrag() {

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    private val phone by lazy {
        arguments?.getString(EXTRA_PHONE) ?: ""
    }
    private val code by lazy {
        arguments?.getString(EXTRA_CODE) ?: ""
    }
    private val sid by lazy {
        arguments?.getString(EXTRA_SID) ?: ""
    }
    private val userToken by lazy {
        arguments?.getString(EXTRA_TOKEN) ?: ""
    }
    private val userId by lazy {
        arguments?.getString(EXTRA_ID) ?: ""
    }
    private val loadingDialog by lazy {
        LoadingDialog(context, "跳转中...", true)
    }

    private val canSkipInvited by lazy {
        arguments?.getInt(EXTRA_SKIP_INVITED) ?: 0
    }

    private var validInvCode = false

    private val bindWXDialog by lazy {
        BindWXDialog().apply {
            callBack = object : CallBack {
                override fun bindWx() {
                    ThirdLoginUtil.loginWX(loadingDialog, WXLoginHelper.FROM_INVITECODE)
                }
            }
        }
    }
    private var inviterInfo: InviterInfo? = null
    private var userEntity: UserEntity? = null
    override fun getLayoutId() = R.layout.invite_code_fragment

    override fun init(savedInstanceState: Bundle?) {
        addAction(Constant.Event.WX_CODE)
        titleBar.visibility = View.GONE
        StatusBar.setStatusBar(activity, true)

        //允许用户出现跳过
        if (canSkipInvited == 1) {
            tv_jump.visibility = View.VISIBLE
        } else {
            tv_jump.visibility = View.GONE
        }

        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.BIND_INVITE -> {
                    loadingDialog.dismiss()
                    if (it.successful) {
                        userEntity = it.data as UserEntity
                        loginSuccess()
                    } else {
                        showToastShort(it.message)
                    }
                }
                ApiHost.NEW_CODE_LOGIN, ApiHost.WX_LOGIN_BIND_PHONE -> {
                    if (it.successful && it.data != null) {
                        userEntity = it.data as UserEntity
                        if (!TextUtils.isEmpty(userEntity?.is_new) && TextUtils.equals(userEntity?.is_new, "1")) {
                            SPUtils.getInstance().put(Constant.PREF.IS_NEW, userEntity?.is_new)
                            SPUtils.getInstance().put(Constant.PREF.IS_FREE, "0")
                            SndoData.event(
                                    "xlt_event_signup",
                                    "higher_phone", "null",
                                    "phone", userEntity?.phone ?: "null",
                                    "signup_time", System.currentTimeMillis()
                            )
                        }
                        if (userEntity?.wechat_info == "0") {
                            if (!(activity?.isFinishing!!)) {
                                bindWXDialog.show(fragmentManager, "BindWXDialog")
                            }
                        } else if (userEntity?.wechat_info == "1") {
                            loginSuccess()
                        }
                    } else {
                        if (it.data != null) {
                            val code = it.data as Int
                            showToastShort(it.message)
                            if (code == 400001) {
                                layout_invite_code.postDelayed({
                                    finish()
                                }, 1000)
                            }
                        } else {
                            showToastShort(it.message)
                        }
                    }
                }
                ApiHost.INVITER_INFO_NO_AUTH -> {
                    if (it.successful) {
                        cos_invite.visibility = View.VISIBLE
                        tv_ensure.isEnabled = true
                        inviterInfo = it.data as InviterInfo
                        GlideUtil.loadPic(img_inviter_pic, inviterInfo?.avatar, R.drawable.icon_default_head, R.drawable.icon_default_head)
                        tv_invited_name.text = inviterInfo?.username ?: ""
                        validInvCode = true
                    } else {
                        validInvCode = false
                        cos_invite.visibility = View.GONE
                        tv_ensure.isEnabled = false
                    }
                    showToastShort(it.message)
                }
                ApiHost.BIND_WX -> {
                    loadingDialog.dismiss()
                    bindWXDialog.dismiss()
                    if (it.successful) {
                        loginSuccess()
                    } else {
                        showToastShort(it.message)
                    }
                }
                ApiHost.INVITE_CODE -> if (it.successful) {
                    loadingDialog.dismiss()
                    val user = UserClient.getUser()
                    user.inviter = inviterInfo?.username
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
        tv_jump.onClick {
            if (userEntity?.wechat_info == "0") {
                //没有绑定微信
                if (!(activity?.isFinishing!!)) {
                    bindWXDialog.show(fragmentManager, "BindWXDialog")
                }
            } else {
                if (TextUtils.isEmpty(sid)) {
                } else {
                    userViewModel.doWXCodeLogin(phone, code, sid)
                }
            }
        }
        img_back.onClick {
            finish()
        }
        tv_ensure.onClick {
            // 如果有sid，代表是新用户，没有sid代表是老用户
            if (!validInvCode) {
                ToastUtil.show("邀请码不存在,请重新输入")
            } else {
                loadingDialog.show()
                userViewModel.bindInvite(et_invite_code.text.toString(), userId)
            }
        }

        RxTextView.textChanges(et_invite_code)
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object : io.reactivex.Observer<CharSequence> {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: CharSequence) {
                        val inviterInfoDisposable = userViewModel.inviterInfoDisposable
                        if (t == null || t.isEmpty()) {
                            img_clear.visibility = View.GONE
                            tv_ensure.isEnabled = false
                            if (inviterInfoDisposable != null && !inviterInfoDisposable.isDisposed) {
                                inviterInfoDisposable.dispose()
                            }
                            return
                        } else {
                            img_clear.visibility = View.VISIBLE
                        }
                        if (t.length == 5 || t.length == 11) {
                            userViewModel.doInviterInfo(t.toString())
                        } else {
                            if (inviterInfoDisposable != null && !inviterInfoDisposable.isDisposed) {
                                inviterInfoDisposable.dispose()
                            }
                            cos_invite.visibility = View.GONE
                            tv_ensure.isEnabled = false
                        }
                    }

                    override fun onError(e: Throwable) {
                    }

                })
        img_clear.onClick {
            et_invite_code.setText("")
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        when {
            event?.action == Constant.Event.WX_CODE -> {
                if (WXLoginHelper.codePage != WXLoginHelper.FROM_INVITECODE) {
                    return
                }
                when (event.data.toString()) {
                    "0", "1" -> {
                        loadingDialog.dismiss()
                    }
                    else -> {
                        // 如果有sid，代表是新用户，没有sid代表是老用户
                        userViewModel.doBindWX(event.data.toString(), userEntity?.token ?: "")
                    }
                }

            }
        }
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

    companion object {
        private const val EXTRA_PHONE = "EXTRA_PHONE" // 手机号
        private const val EXTRA_CODE = "EXTRA_INVITE_CODE" //验证码
        private const val EXTRA_SID = "EXTRA_SID" //微信登录sid
        private const val EXTRA_TOKEN = "EXTRA_token" //用户信息
        private const val EXTRA_ID = "EXTRA_ID" //用户ID
        private const val EXTRA_SKIP_INVITED = "EXTRA_SKIP_INVITED" //是否允许出现跳过按钮，为1则可以跳过

        fun start(context: Context?, phone: String, sid: String, token: String, id: String, canSkipInvited: Int) {
            val fragParam = SimpleFragAct.SimpleFragParam("",
                    InviteCodeFragment::class.java, Bundle().apply {
                putString(EXTRA_PHONE, phone)
                putString(EXTRA_SID, sid)
                putString(EXTRA_TOKEN, token)
                putString(EXTRA_ID, id)
                putInt(EXTRA_SKIP_INVITED, canSkipInvited)
            })
            SimpleFragAct.start(context, fragParam)
        }
    }
}