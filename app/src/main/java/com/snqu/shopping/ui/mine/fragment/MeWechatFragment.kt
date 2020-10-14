package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.snqu.shopping.R
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.statistics.task.NewTaskType
import com.snqu.shopping.util.statistics.task.TaskReport
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.mewechat_fragment.*


/**
 * 我的微信
 */
class MeWechatFragment : SimpleFrag() {

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    private val loadingDialog by lazy {
        LoadingDialog(activity, "加载中", true)
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN or
//                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//        super.onCreate(savedInstanceState)
//    }

    override fun getLayoutId(): Int {
        return R.layout.mewechat_fragment
    }

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)

        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.SET_WECHAT_ID -> {
                    loadingDialog.dismiss()
                    ToastUtil.show(it.message)
                    if (it.successful) {
                        UserClient.getUser().wechat_show_uid = edit_wechat_num.text.toString()
                        UserClient.saveLoginUser(UserClient.getUser())
                        //新手任务汇报
                        TaskReport.newTaskReport(mContext, NewTaskType.BIND_WX)
                    }
                }
            }
        })

        edit_wechat_num.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null || s.isEmpty()) {
                    tv_wechat_sure.isEnabled = false
                    edit_wechat_num.textSize = 13F
                    return
                }
                edit_wechat_num.textSize = 16F
                tv_wechat_sure.isEnabled = s.length >= 6
            }
        })

        tv_wechat_sure.onClick {
            if (!edit_wechat_num.text.toString().matches(Regex("^[a-zA-z].*"))) {
                ToastUtil.show("微信号必须以字母开头，请填写正确的微信号")
            } else {
                loadingDialog.show()
                userViewModel.setWechatId(edit_wechat_num.text.toString())
            }
        }

        if (!TextUtils.isEmpty(UserClient.getUser().wechat_show_uid)) {
            edit_wechat_num.setText(UserClient.getUser().wechat_show_uid)
            TaskReport.newTaskReport(mContext, NewTaskType.BIND_WX)
        }


        // 动态修改底部bottom的位置，避免被软键盘遮挡。
//        KeyboardUtils.registerSoftInputChangedListener(activity) { height ->
//            val lp = tv_wechat_sure.layoutParams as RelativeLayout.LayoutParams
//            if (height > 0) {
//                lp.bottomMargin = ConvertUtils.dp2px(50F) + height
//            } else {
//                lp.bottomMargin = ConvertUtils.dp2px(50F)
//            }
//            tv_wechat_sure.layoutParams = lp
//        }


    }


    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("我的微信",
                    MeWechatFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }

}