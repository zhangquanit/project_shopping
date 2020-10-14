package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SpanUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.ui.AlertDialogView
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.ui.mine.view.AdviserView
import com.snqu.shopping.ui.mine.view.UserCancelReviewView
import com.snqu.shopping.util.NumberUtil
import com.snqu.shopping.util.ext.onClick
import common.widget.dialog.DialogView
import common.widget.dialog.EffectDialogBuilder
import kotlinx.android.synthetic.main.user_cancel_detail_fragment.*

class UserCancelReviewFrag : SimpleFrag() {

    private val list by lazy {
        arguments?.getStringArrayList(Constant.Bundle.USER_CANCEL_CHECK_LIST)
    }

    private val other by lazy {
        arguments?.getString(Constant.Bundle.USER_CANCEL_OTHER) ?: ""
    }

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)

        tv_next.onClick {

            val dialogView:DialogView = UserCancelReviewView(mContext,list!!,other)
            EffectDialogBuilder(mContext)
                    .setContentView(dialogView)
                    .setCancelable(false)
                    .setCancelableOnTouchOutside(false)
                    .show()
        }

    }

    override fun getLayoutId(): Int = R.layout.user_cancel_review_fragment

    companion object {
        fun start(context: Context?, list: ArrayList<String>, other: String) {
            val fragParam = SimpleFragAct.SimpleFragParam("审核申请",
                    UserCancelReviewFrag::class.java)
            fragParam.paramBundle = Bundle().apply {
                putStringArrayList(Constant.Bundle.USER_CANCEL_CHECK_LIST, list)
                putString(Constant.Bundle.USER_CANCEL_OTHER, other)
            }
            SimpleFragAct.start(context, fragParam)
        }
    }

}