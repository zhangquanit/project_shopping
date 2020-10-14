package com.snqu.shopping.ui.mine.view

import android.app.Activity
import android.view.View
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.AlertDialogView
import com.snqu.shopping.ui.mine.fragment.UserCancelPhoneValideFrag
import com.snqu.shopping.util.ext.onClick
import kotlin.collections.ArrayList

class UserCancelReviewView(val ctx: Activity, val list: ArrayList<String>, val other: String) : AlertDialogView(ctx) {

    override fun getLayoutId(): Int = R.layout.user_cancel_review_dialog

    override fun initView(view: View?) {

        view?.findViewById<View>(R.id.btn_left)
                ?.onClick {
                    dismiss()
                    UserCancelPhoneValideFrag.start(context,list,other)
                }

        view?.findViewById<View>(R.id.btn_right)
                ?.onClick {
                    dismiss()
                }
    }


}