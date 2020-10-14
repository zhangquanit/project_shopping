package com.snqu.shopping.ui.goods.dialog

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.android.util.ext.ToastUtil
import com.makeramen.roundedimageview.RoundedImageView
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.AlertDialogView
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.ui.goods.util.JumpUtil.openWechat
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ext.onClick

class GoodRecmDialogView(ctx: Activity) : AlertDialogView(ctx) {

    override fun getLayoutId(): Int = R.layout.good_recm_success_dialog

    override fun initView(view: View?) {

        view?.findViewById<View>(R.id.btn_left)
                ?.onClick {
                    dismiss()
                }

        view?.findViewById<View>(R.id.btn_right)
                ?.onClick {
                }
    }

}