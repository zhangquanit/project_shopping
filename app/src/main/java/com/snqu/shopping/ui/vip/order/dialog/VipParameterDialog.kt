package com.snqu.shopping.ui.vip.order.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snqu.shopping.R
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.vip_parameter_dialog.*


/**
 * desc:商品详情参数
 * time: 2019/2/1
 * @author 银进
 */
class VipParameterDialog : androidx.fragment.app.DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.vip_parameter_dialog, container)

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog.window
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        tv_complete.onClick {
            dismiss()
        }

    }


}
