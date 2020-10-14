package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.ui.login.LoginFragment
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ThirdLoginUtil
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.statistics.SndoData
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.setting_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat

/**
 * desc:
 * time: 2019/8/20
 * @author 银进
 */
class SettingFragment : SimpleFrag() {

    var dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm")

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }
    private val loadingDialog by lazy {
        LoadingDialog(context, "跳转中...", true)
    }

    override fun getLayoutId() = R.layout.setting_fragment

    override fun init(savedInstanceState: Bundle?) {
        addAction(Constant.Event.BIND_INVITE_SUCCESS)
        addAction(Constant.Event.WX_CODE)
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        userViewModel.dataResult.observe(this, Observer {
            if (it?.tag == ApiHost.LOGIN_OUT) {
                UserClient.loginOut()
                SndoData.loginOut()
                EventBus.getDefault().post(PushEvent(Constant.Event.LOGIN_OUT))
                finish()
            } else if (it?.tag == ApiHost.BIND_WX) {
                if (it.successful) {
                    EventBus.getDefault().post(PushEvent(Constant.Event.BIND_WX_SUCCESS))
                    showToastShort("绑定成功")
                } else {
                    showToastShort(it.message)
                }
            }
        })
        initView()
    }

    private fun initView() {

        tv_invited_name.text = UserClient.getUser().inviter ?: "未绑定"
        if (TextUtils.isEmpty(UserClient.getUser().inviter)) {
            item_img.visibility = View.GONE
        } else {
            item_img.visibility = View.VISIBLE
            GlideUtil.loadRoundPic(item_img, UserClient.getUser().inviter_avatar, R.drawable.icon_default_head, R.drawable.icon_default_head)
        }
//        tv_wx_name.text = UserClient.getUser().wechat_show_uid ?: "未填"

        if (UserClient.getUser().level >= 3) rl_wx_person.visibility = View.VISIBLE else {
            rl_wx_person.visibility = View.GONE
        }

        rl_bind_wx.isEnabled = ("0" == UserClient.getUser().wechat_info)
        tv_login_out.onClick {
            userViewModel.doLoginOut()
        }
        rl_bind_alipay.onClick {
            if (TextUtils.equals(UserClient.getUser().bind_alipay, "1")) {
                BindAlipaySuccessFragment.start(activity)
            } else {
                BindAlipayFragment.start(activity)
            }

        }
        rl_bind_wx.onClick {
            ThirdLoginUtil.loginWX(loadingDialog, -1)
        }
        rl_self_invite_person.onClick {
            InvitePersonFragment.start(activity)
        }
        rl_change_phone.onClick {
            ChangePhoneFragment.start(activity)
        }
        rl_about.onClick {
            AboutFragment.start(activity)
        }

        rl_wx_person.onClick {
            MeWechatFragment.start(activity)
        }

        rl_message_push_person.onClick {
            PushMessageSettingFragment.start(activity)
        }

        rl_invitation_code.onClick {
            ChangeInviCodeFragment.start(activity)
        }

        rl_question.onClick {
            FeedbackFrag.start(activity)
        }

        UserClient.getUser()?.apply {
            if (this.itime > 0) {
                rl_register_time.visibility = View.VISIBLE
                tv_time.text = dateFormat.format(this.itime * 1000L)
            } else {
                rl_register_time.visibility = View.GONE
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        when {
            event?.action == Constant.Event.BIND_INVITE_SUCCESS -> {
                rl_self_invite_person.isEnabled = true
                img_right.visibility = View.GONE
                tv_invited_name.text = UserClient.getUser().inviter ?: ""
                item_img.visibility = View.VISIBLE
                GlideUtil.loadRoundPic(item_img, UserClient.getUser().inviter_avatar, R.drawable.icon_default_head, R.drawable.icon_default_head)
            }
            event?.action == Constant.Event.BIND_WX_SUCCESS -> {
                rl_bind_wx.isEnabled = false
            }
            event?.action == Constant.Event.WX_CODE -> {
                when (event.data.toString()) {
                    "0", "1" -> {
                        loadingDialog.dismiss()
                    }
                    else -> {
                        userViewModel.doBindWX(event.data.toString(), UserClient.getToken())
                        loadingDialog.dismiss()
                    }
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (tv_wx_name != null) {
            tv_wx_name.text = UserClient.getUser()?.wechat_show_uid ?: "未填"
        }
    }

    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("设置",
                    SettingFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }


}