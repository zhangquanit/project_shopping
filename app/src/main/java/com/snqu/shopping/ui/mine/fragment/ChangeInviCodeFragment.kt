package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SpanUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.common.ui.AlertDialogView
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.InviteCodeEntity
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.mine.adapter.InviteCodeAdapter
import com.snqu.shopping.util.ext.onClick
import common.widget.dialog.EffectDialogBuilder
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.changeinvicode_fragment.*
import kotlinx.android.synthetic.main.pushsetting_fragment.loadingview
import org.greenrobot.eventbus.EventBus

/**
 * 更改邀请码
 */
class ChangeInviCodeFragment : SimpleFrag() {

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    private val loadingDialog by lazy {
        LoadingDialog(activity, "加载中", true)
    }

    private var inviteCodeEntity: InviteCodeEntity? = null

    private val mAdapter = InviteCodeAdapter()

    override fun getLayoutId(): Int = R.layout.changeinvicode_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)

        getInviteCode()

        userViewModel.dataResult.observe(this, Observer {
            if ((activity?.isFinishing!!)) {
                return@Observer
            }
            when (it?.tag) {
                ApiHost.SET_INVITE_CODE -> {
                    loadingDialog.dismiss()
                    if (!TextUtils.isEmpty(it.message)) {
                        ToastUtil.show(it.message)
                    }
                    if (it.successful) {
                        rand_layout.visibility = View.GONE
                        invite_tip.visibility = View.GONE
                        if (inviteCodeEntity?.can_set != null) inviteCodeEntity?.can_set = inviteCodeEntity?.can_set?.minus(1)
                        inviteCodeEntity?.invite_link_code = edit_invite_code.text.toString().trim()
                        val userEntity = UserClient.getUser()
                        userEntity.invite_link_code = edit_invite_code.text.toString().trim()
                        UserClient.saveLoginUser(userEntity)
                        tv_invitation_code.text = "当前邀请码：${inviteCodeEntity?.invite_link_code}"
                        setInvitedText()
                        EventBus.getDefault().post(PushEvent(Constant.Event.BIND_WX_SUCCESS))
                    }
                }
                ApiHost.GET_INVITE_CODE -> {
                    if (it.successful) {
                        if (it.data != null) {
                            inviteCodeEntity = it.data as InviteCodeEntity
//                            inviteCodeEntity?.can_set = 1
                            if (inviteCodeEntity != null) {
                                if (((inviteCodeEntity?.can_set ?: 0) > 0)) {
                                    edit_invite_code.post {
                                        edit_invite_code.isEnabled = true
                                        item_rand.isEnabled = false
                                        recycler_view.visibility = View.GONE
                                        item_invite_loading.visibility = View.VISIBLE
                                        loading_line.visibility = View.VISIBLE
                                        item_rand.setTextColor(Color.parseColor("#FF9C9C9C"))
                                        item_img.setImageResource(R.drawable.icon_invite_n)
                                        val drawable = resources.getDrawable(R.drawable.icon_invite_n, null)
                                        item_rand.setCompoundDrawables(drawable, null, null, null)
                                        userViewModel.getRandCode()
                                    }
                                } else {
                                    edit_invite_code.isEnabled = false
                                }
                                tv_invitation_code.text = "当前邀请码：${inviteCodeEntity?.invite_link_code}"
                                tv_invitation_tip.text = inviteCodeEntity?.show_content
                                setInvitedText()
                                loadingview.visibility = View.GONE
                                rl.visibility = View.VISIBLE
                            } else {
                                loadingview.setStatus(LoadingStatusView.Status.EMPTY)
                            }
                        } else {
                            loadingview.setStatus(LoadingStatusView.Status.EMPTY)
                        }
                    } else {
                        loadingview?.apply {
                            setStatus(LoadingStatusView.Status.FAIL)
                            setOnBtnClickListener {
                                getInviteCode()
                            }
                        }
                    }
                }
                ApiHost.GET_RAND_CODE -> {
                    item_rand.isEnabled = true
                    rand_layout.visibility = View.VISIBLE
                    recycler_view.visibility = View.VISIBLE
                    item_invite_loading.visibility = View.GONE
                    item_rand.setTextColor(Color.parseColor("#FF25282D"))
                    loading_line.visibility = View.GONE
                    item_img.setImageResource(R.drawable.icon_invite_p)
                    if (it.successful) {
                        val data = it.data as List<String>
                        mAdapter.setNewData(data)
                    }
                }
                ApiHost.CHECK_INVITE_CODE -> {
                    if (!it.successful && it.data != null) {
                        if (!TextUtils.isEmpty(it.message)) {
                            invite_tip.text = it.message
                        }
                        invite_tip.visibility = View.VISIBLE
                    } else {
                        invite_tip.visibility = View.GONE
                    }
                }
            }
        })


        // 动态修改底部bottom的位置，避免被软键盘遮挡。
        KeyboardUtils.registerSoftInputChangedListener(activity) { height ->
            var lp = tv_set_invitecode.layoutParams as RelativeLayout.LayoutParams
            if (height > 0) {
                lp.bottomMargin = ConvertUtils.dp2px(50F) + height
            } else {
                lp.bottomMargin = ConvertUtils.dp2px(50F)
            }
            tv_set_invitecode.layoutParams = lp
            lp = tv_tip.layoutParams as RelativeLayout.LayoutParams
            if (height > 0) {
                lp.bottomMargin = ConvertUtils.dp2px(25F) + height
            } else {
                lp.bottomMargin = ConvertUtils.dp2px(25F)
            }
            tv_tip.layoutParams = lp
        }

        edit_invite_code.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null || s.isEmpty()) {
                    tv_set_invitecode.isEnabled = false
                    edit_invite_code.textSize = 12F
                    return
                }
                edit_invite_code.textSize = 18F
                if ((inviteCodeEntity?.can_set ?: 0) > 0) {
                    tv_set_invitecode.isEnabled = s.length >= 5
                    if (s.length >= 5) {
                        userViewModel.checkInviteCode(s.toString())
                    } else {
                        invite_tip.visibility = View.GONE
                    }
                }
            }
        })

        tv_set_invitecode.onClick {
            if (inviteCodeEntity != null) {
                if ((inviteCodeEntity?.can_set ?: 0) > 0) {
                    val dialogView: AlertDialogView = AlertDialogView(activity)
                            .setContent(SpanUtils().append("更改后将不能改回之前的邀请码，").setFontSize(14, true)
                                    .setForegroundColor(Color.parseColor("#8A8A8B")).setFontSize(14, true)
                                    .append("更改之前的邀请码保留7天的有效期").setForegroundColor(Color.parseColor("#f16c60")).setFontSize(14, true)
                                    .append("，有效期过后将作废，确定要改吗？").setForegroundColor(Color.parseColor("#8A8A8B")).setFontSize(14, true)
                                    .create())
                            .setLeftBtn("取消")
                            .setRightBtn("确定更改") {
                                loadingDialog.show()
                                userViewModel.setInviteCode(edit_invite_code.text.toString().trim())
                            }
                    EffectDialogBuilder(activity)
                            .setContentView(dialogView)
                            .setCancelable(false)
                            .setCancelableOnTouchOutside(false)
                            .show()
                }
            }

        }


        item_rand.onClick {
            item_rand.isEnabled = false
            recycler_view.visibility = View.GONE
            item_invite_loading.visibility = View.VISIBLE
            loading_line.visibility = View.VISIBLE
            item_rand.setTextColor(Color.parseColor("#FF9C9C9C"))
            item_img.setImageResource(R.drawable.icon_invite_n)
            val drawable = resources.getDrawable(R.drawable.icon_invite_n, null)
            item_rand.setCompoundDrawables(drawable, null, null, null)
            item_rand.postDelayed({
                userViewModel.getRandCode()
            }
                    , 2000)
        }

        recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        recycler_view.adapter = mAdapter
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val code = adapter.data[position].toString()
            this.edit_invite_code.setText(code)
        }

    }

    private fun setInvitedText() {
        if (inviteCodeEntity?.can_set == null) {
            tv_set_invitecode.text = "暂无权限"
            tv_set_invitecode.isEnabled = false
        } else if ((inviteCodeEntity?.can_set ?: 0) <= 0) {
            tv_set_invitecode.text = "权限已用完"
            tv_set_invitecode.isEnabled = false
        }
    }

    private fun getInviteCode() {
        rl.visibility = View.GONE
        loadingview.setStatus(LoadingStatusView.Status.LOADING)
        userViewModel.getInviteCode()
    }


    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("更改邀请码",
                    ChangeInviCodeFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }

}