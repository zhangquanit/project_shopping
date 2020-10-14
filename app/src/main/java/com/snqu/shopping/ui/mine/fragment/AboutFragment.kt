package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.android.util.LContext
import com.android.util.os.NetworkUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.BuildConfig
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.data.user.UserClient
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
 * desc:
 * time: 2019/8/20
 * @author 银进
 */
class AboutFragment : SimpleFrag() {
    private var appVersion: AppVersion? = null
    private var loadUrlState = false
    private var isCheckVersion: Boolean = false
    private val dialog by lazy {
        LoadingDialog(activity, getString(R.string.version_checking), true)
    }

    override fun getLayoutId() = R.layout.about_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        forwardUpdate()
        rl_update.onClick {
            update()
        }
        rl_privacy_agreement.onClick {
            val webViewParam = WebViewFrag.WebViewParam()
            webViewParam.url = Constant.WebPage.PRIVACY_PROTOCAL
            WebViewFrag.start(mContext, webViewParam)
        }
        rl_user_agreement.onClick {
            val webViewParam = WebViewFrag.WebViewParam()
            webViewParam.url = Constant.WebPage.USER_AGREEMENT
            WebViewFrag.start(mContext, webViewParam)
        }
        tv_version_code.text = "V${BuildConfig.VERSION_NAME}"
        //检测版本号
        var newVersion = ""
        view_circle.visibility = View.GONE
        if (AppDownloadClient.hasNewVersion()) {
            newVersion = "发现新版本V" + AppDownloadClient.getFromDB().versionName
        }
        tv_new_version.text = newVersion

        rl_user_account_cancellation.onClick {
            UserClient.getUser()
                    ?.let {
                        if (TextUtils.equals("1", it.is_logout)) {
                            UserCanceledFrag.start(activity)
                        } else if (TextUtils.equals("0", it.is_logout)) {
                            UserCancellationFragment.start(activity)
                        }
                    }
        }
    }

    /**
     * 版本更新
     */
    private fun update() {
        if (!NetworkUtil.isNetworkAvailable(activity)) {
            showToastShort(R.string.net_noconnection)
            return
        }
        if (loadUrlState) {
            if (appVersion == null) {
                showToastShort(R.string.version_newest)
            } else {
                showUpDateDialog()
            }
            return
        }
        if (isCheckVersion) {
            return
        }
        isCheckVersion = true
        dialog.show()
        AppDownloadClient.doCheckVersion(object : VersionUpdateListener {

            override fun onNoVersionReturned() {
                tv_new_version.text = ""
                loadUrlState = true
                isCheckVersion = false
                showToastShort(R.string.version_newest)
                dialog.dismiss()
            }

            override fun fail() {
                loadUrlState = false
                isCheckVersion = false
                dialog.dismiss()
            }

            override fun onNewVersionReturned(appVersion: AppVersion) {
                loadUrlState = true
                isCheckVersion = false
                dialog.dismiss()
                //检测版本号
                var newVersion = ""
                view_circle.visibility = View.GONE
                if (appVersion.versionCode > LContext.versionCode) {
                    newVersion = "发现新版本V" + appVersion.versionName
                    view_circle.visibility = View.VISIBLE
                }
                this@AboutFragment.appVersion = appVersion
                tv_new_version.text = newVersion
                this@AboutFragment.appVersion = appVersion
                showUpDateDialog()
            }
        })
    }

    private fun showUpDateDialog() {
        val upDateDialogFragment = UpDateDialogFragment()
        upDateDialogFragment.arguments = Bundle().apply {
            putSerializable("appversion", appVersion)
        }
        upDateDialogFragment.isCancelable = false
        val ft = childFragmentManager.beginTransaction()
        ft.add(upDateDialogFragment, "UpDateDialogFragment")
        ft.commitAllowingStateLoss()
    }

    /**
     * 版本更新
     */
    private fun forwardUpdate() {
        if (!NetworkUtil.isNetworkAvailable(activity)) {
            tv_new_version.text = ""
            return
        }
        AppDownloadClient.doCheckVersion(object : VersionUpdateListener {
            override fun onNoVersionReturned() {
                loadUrlState = true
                tv_new_version?.text = ""
            }

            override fun fail() {
                loadUrlState = false
            }

            override fun onNewVersionReturned(appVersion: AppVersion) {
                //检测版本号
                var newVersion = ""
                view_circle.visibility = View.GONE
                if (appVersion.versionCode > LContext.versionCode) {
                    newVersion = "发现新版本V" + appVersion.versionName
                    view_circle.visibility = View.VISIBLE
                }
                this@AboutFragment.appVersion = appVersion
                tv_new_version?.text = newVersion
                loadUrlState = true
            }
        })
    }


    override fun onDestroy() {
        AppDownloadClient.stopCheckVersion()
        super.onDestroy()
    }

    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("关于星乐桃",
                    AboutFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }

}