package com.snqu.shopping.ui.splash.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.privacy_dialog.*


/**
 * desc:隐私政策弹窗
 */
class PrivacyDialog : androidx.fragment.app.DialogFragment() {
    var callBack: CallBack? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.privacy_dialog, container)

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog.window
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        tv_user_agreement.onClick {
            val webViewParam = WebViewFrag.WebViewParam()
            webViewParam.url = Constant.WebPage.USER_AGREEMENT
            WebViewFrag.start(activity, webViewParam)
        }
        tv_user_privacy.onClick {
            val webViewParam = WebViewFrag.WebViewParam()
            webViewParam.url = Constant.WebPage.PRIVACY_PROTOCAL
            WebViewFrag.start(activity, webViewParam)
        }
        tv_user_promote.onClick {
            val webViewParam = WebViewFrag.WebViewParam()
            webViewParam.url = Constant.WebPage.PROMOTE
            WebViewFrag.start(activity, webViewParam)
        }
        tv_know.onClick {
            callBack?.dismiss()
        }
    }


}

interface CallBack {
    fun dismiss()
}
