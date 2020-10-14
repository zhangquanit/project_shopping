package com.snqu.shopping.ui.goods.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snqu.shopping.R
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.copy_password_success_dialog.*


/**
 * desc:复制口令成功
 * time: 2019/2/1
 * @author 银进
 */
class CopyPasswordSuccessDialog : androidx.fragment.app.DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.copy_password_success_dialog, container)

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog.window
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        tv_content.text="口令复制后，打开淘宝APP，\n" +
                "在弹窗中点击“打开”领取优惠券，再进行购买"
        tv_know.onClick {
            dismiss()
        }


    }


}
