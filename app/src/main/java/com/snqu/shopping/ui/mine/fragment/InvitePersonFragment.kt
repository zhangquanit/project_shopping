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
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.ToastUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.RecommendSuperior
import com.snqu.shopping.data.user.entity.InviterInfo
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.mine.callback.ConfirmCallBack
import com.snqu.shopping.ui.mine.dialog.ConfirmDialog
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ext.onClick
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.invite_person_fragment.*
import org.greenrobot.eventbus.EventBus

/**
 * desc:
 * time: 2019/9/12
 * @author 银进
 */
class InvitePersonFragment : SimpleFrag() {
    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }
    private var inviterInfo: InviterInfo? = null

    var loadingDialog: LoadingDialog? = null

    var bean: RecommendSuperior? = null

    var code: String? = null

    private var recommed = 0

    private fun showLoading() {
        loadingDialog = LoadingDialog.showDialog(mContext, "获取邀请码中...")
    }

    private fun dissmissDialog() {
        if (null != loadingDialog) loadingDialog!!.dismiss()
    }

    override fun getLayoutId() = R.layout.invite_person_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)

        val user = UserClient.getUser()
        if (!TextUtils.isEmpty(user.inviter)) {
            invited_layout_one.visibility = View.VISIBLE
            invited_layout_two.visibility = View.GONE
            tv_invited.text = "邀请码：${user.inviter_link_code}"
            GlideUtil.loadPic(icon_inviter, user.inviter_avatar, R.drawable.icon_default_head, R.drawable.icon_default_head)
            tv_name.text = user.inviter
        } else {
            invited_layout_one.visibility = View.GONE
            invited_layout_two.visibility = View.VISIBLE
            invite_edit.textSize = 14F
            invite_edit.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s == null || s.isEmpty()) {
                        code = ""
                        invite_edit.textSize = 14F
                        return
                    }
                    recommed = 0
                    invite_edit.textSize = 18F
                    if (s.length == 5 || s.length == 11) {
                        code = s.toString()
                        userViewModel.doInviterInfo(s.toString())
                    } else {
                        tv_ensure.isEnabled = false
                        invited_person_layout.visibility = View.GONE
                    }
                }
            })
//            showLoading()
            userViewModel.getRecommendCode()
        }


        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.INVITE_CODE -> {
                    dissmissDialog()
                    if (it.successful) {
                        val user = UserClient.getUser()
                        user.inviter = inviterInfo?.username
                        user.invite_link = "1"
                        user.inviter_avatar = inviterInfo?.avatar
                        UserClient.updateUser(user)
                        ToastUtil.show("上级邀请码设置成功")
                        invite_layout.postDelayed({
                            EventBus.getDefault().post(PushEvent(Constant.Event.BIND_INVITE_SUCCESS))
                            finish()
                        }, 500)
                    } else {
                        showToastShort(it.message)
                    }
                }
                ApiHost.INVITER_INFO_NO_AUTH -> {
                    dissmissDialog()
                    if (it.successful) {
                        invited_person_layout.visibility = View.VISIBLE
                        tv_ensure.isEnabled = true
                        inviterInfo = it.data as InviterInfo
                        GlideUtil.loadPic(icon_inviter_two, inviterInfo?.avatar, R.drawable.icon_default_head, R.drawable.icon_default_head)
                        tv_name_two.text = inviterInfo?.username ?: ""
                    }
                    showToastShort(it.message)
                }
                ApiHost.GET_RECOMMEND_CODE -> {
                    dissmissDialog()
                    if (it.successful && it.data != null) {
                        bean = it.data as RecommendSuperior
                        if (!TextUtils.isEmpty(bean?.inviterCode)) {
                            tv_recommend.visibility = View.VISIBLE
                        }
                    } else {
                        ToastUtils.showShort(it.message)
                    }
                }
            }
        })
        tv_ensure.onClick {
            ConfirmDialog().apply {
                callBack = object : ConfirmCallBack {
                    override fun ensure() {
                        showLoading()
                        userViewModel.doInviteCode(code ?: "",recommed)
                    }

                    override fun cancel() {
                    }
                }
            }.show(childFragmentManager, "ConfirmDialog")
        }
        tv_recommend.onClick {
            if(bean!=null) {
                invite_edit.setText(bean?.inviterCode)
                recommed = 1
            }
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("设置上级邀请码",
                    InvitePersonFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }
}