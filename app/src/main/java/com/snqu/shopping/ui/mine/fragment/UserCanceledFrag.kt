package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.AccountCancelEntity
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.main.MainActivity
import com.snqu.shopping.util.ext.onClick
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.user_canceled_fragment.*
import java.text.SimpleDateFormat

class UserCanceledFrag : SimpleFrag() {

    private var mLoadingDialog: LoadingDialog? = null

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)

        userViewModel.dataResult.observe(this, Observer {
            when (it.tag) {
                ApiHost.ACCOUNT_LOGOUT_DETAILS -> {
                    if (it.successful) {
                        loadingview.visibility = View.GONE
                        val data = it.data as AccountCancelEntity
                        logout_time.text = "提交时间：" + dateFormat.format(data.itime * 1000)
                    } else {
                        loadingview.setStatus(LoadingStatusView.Status.FAIL)
                    }
                }
                ApiHost.LOGOUT_REVOCATION -> {
                    closeLoadDialog()
                    if (!TextUtils.isEmpty(it.message)) {
                        ToastUtil.show(it.message)
                    }
                    if (it.successful) {
                        val user = UserClient.getUser()
                        user.is_logout = "0" //正常用户
                        UserClient.updateUser(user)
                        MainActivity.startForPage(mContext, 4)
                        finish()
                    }
                }
            }
        })
        loadingview.setOnBtnClickListener {
            loadingData()
        }
        loadingData()
        tv_account_cancel_btn.onClick {
            showLoadingDialog("提交中")
            userViewModel.logoutRevocation()
        }
    }

    private fun loadingData() {
        loadingview.setStatus(LoadingStatusView.Status.LOADING)
        userViewModel.getLogoutDetails()
    }

    override fun getLayoutId(): Int = R.layout.user_canceled_fragment

    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("账号注销",
                    UserCanceledFrag::class.java)
            SimpleFragAct.start(context, fragParam)
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