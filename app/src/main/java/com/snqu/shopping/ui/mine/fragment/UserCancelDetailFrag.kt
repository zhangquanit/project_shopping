package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.SpanUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.util.NumberUtil
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.user_cancel_detail_fragment.*

class UserCancelDetailFrag : SimpleFrag() {

    private val list by lazy {
        arguments?.getStringArrayList(Constant.Bundle.USER_CANCEL_CHECK_LIST)
    }

    private val other by lazy {
        arguments?.getString(Constant.Bundle.USER_CANCEL_OTHER) ?: ""
    }


    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        val spanUtils = SpanUtils()
        /**
         *
        1.您的账户将无法登录与使用
        2.您的账户信息和会员权益将永久删除且无法恢复
        3.您的收益与团队成员将永久清空且无法恢复
        4.您账户所关联的订单将无法查询与找回
         */
        spanUtils.append("1.您的账户将无法登录与使用")
                .appendLine()
                .append("2.您的账户信息和会员权益将永久删除且无法恢复")
                .appendLine()
                .append("3.您的收益与团队成员将永久清空且无法恢复")
                .appendLine()
                .append("4.您账户所关联的订单将无法查询与找回")
                .appendLine()
                .append("5.您绑定的微信号和手机号将无法短时间内再次注册星乐桃")
                .setForegroundColor(Color.parseColor("#F73737"))
        tv_detail.text = spanUtils.create()

        tv_unsettled_amount.text = "¥${NumberUtil.saveTwoPoint(UserClient.unsettled_amount)}"
        tv_amount_useable.text = "¥${NumberUtil.saveTwoPoint(UserClient.amount_useable)}"

        tv_next.onClick {
            UserCancelReviewFrag.start(mContext,list!!,other)
        }
    }

    override fun getLayoutId(): Int = R.layout.user_cancel_detail_fragment

    companion object {
        fun start(context: Context?, list: ArrayList<String>, other: String) {
            val fragParam = SimpleFragAct.SimpleFragParam("账号注销",
                    UserCancelDetailFrag::class.java)
            fragParam.paramBundle = Bundle().apply {
                putStringArrayList(Constant.Bundle.USER_CANCEL_CHECK_LIST, list)
                putString(Constant.Bundle.USER_CANCEL_OTHER, other)
            }
            SimpleFragAct.start(context, fragParam)
        }
    }

}