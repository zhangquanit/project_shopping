package com.snqu.shopping.ui.mine.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snqu.shopping.R
import com.snqu.shopping.ui.main.view.UpdateDialogView
import com.snqu.shopping.ui.main.view.UpdateDialogView.fileSize
import com.snqu.shopping.util.VersionUpdate
import com.snqu.shopping.util.ext.onClick
import common.widget.dialog.EffectDialogBuilder
import component.update.AppDownloadClient
import component.update.AppVersion
import kotlinx.android.synthetic.main.update_dialog_fragment.*


/**
 * desc:
 * time: 2019/2/1
 * @author 银进
 */
class UpDateDialogFragment : androidx.fragment.app.DialogFragment() {
    private val appVersion by lazy {
        arguments?.getSerializable("appversion") as AppVersion?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.update_dialog_fragment, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog.window
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        tv_content.text = appVersion?.desc ?: ""
        tv_version.text = "V" + appVersion?.versionName ?: ""

        tv_cancel.onClick {
            dismiss()
            if (appVersion?.forceUpdate == 1) {
                Process.killProcess(Process.myPid())
            }
        }
        if (appVersion?.forceUpdate == 1) {
            tv_cancel.visibility = View.GONE
        }
        tv_ensure.onClick {
            if (appVersion?.forceUpdate == 1) {
                val updateFile = AppDownloadClient.getUpdateFile()
                if (null != updateFile && fileSize != -1L && fileSize == updateFile.length()) { //已下载
                    AppDownloadClient.installAPK()
                    return@onClick
                }
                val dialogView = UpdateDialogView(context, appVersion)
                EffectDialogBuilder(context)
                        .setContentView(dialogView)
                        .setCancelable(false)
                        .setCancelableOnTouchOutside(false)
                        .show()
            } else {
                dismiss()
                VersionUpdate.update(context, appVersion)
            }
        }

    }


}