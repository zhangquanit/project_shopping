package com.snqu.shopping.ui.mine.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snqu.shopping.ui.mine.callback.ConfirmCallBack
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.confirm_dialog.*

/**
 * 对话框
 *
 * @author zhangquan
 */
class ConfirmDialog : androidx.fragment.app.DialogFragment() {
    var callBack:ConfirmCallBack?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, com.snqu.shopping.R.style.update_dialog_style)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(com.snqu.shopping.R.layout.confirm_dialog, container)

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog.window
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog_cancel.onClick {
            callBack?.cancel()
            dismiss()
        }

        dialog_yes.onClick {
            dismiss()
            callBack?.ensure()
        }


    }
}
