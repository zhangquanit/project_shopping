package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.android.util.LContext
import com.android.util.os.NetworkUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.BuildConfig
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.ui.mine.dialog.UpDateDialogFragment
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.log.LogClient
import common.widget.dialog.loading.LoadingDialog
import component.update.AppDownloadClient
import component.update.AppVersion
import component.update.VersionUpdateListener
import kotlinx.android.synthetic.main.about_fragment.*

/**
 * 手机验证
 *
 */
class UserVerificationPhoneFragment : SimpleFrag() {

    override fun getLayoutId() = R.layout.user_verification_phone_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)


    }

    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("手机号验证",
                    UserVerificationPhoneFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }

}