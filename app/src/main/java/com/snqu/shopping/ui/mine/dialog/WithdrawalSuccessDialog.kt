package com.snqu.shopping.ui.mine.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.SpanUtils
import com.snqu.shopping.R
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.withdrawal_success_dialog.*


/**
 * desc:提现成功
 * time: 2019/2/1
 * @author 银进
 */
class WithdrawalSuccessDialog : androidx.fragment.app.DialogFragment() {
     var callBack:CallBack?=null
    private val money by lazy {
        arguments?.getString(EXTRA_MONEY)?:""
    }
    private val tip by lazy{
        arguments?.getString(EXTRA_TIP)?:"请及时到支付宝中确认"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.withdrawal_success_dialog, container)

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog.window
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        tv_money.text=SpanUtils()
                .append("到账金额：").setFontSize(13,true).setForegroundColor(Color.BLACK)
                .append("¥").setFontSize(10,true).setForegroundColor(Color.parseColor("#F34264"))
                .append("$money ").setFontSize(15,true).setForegroundColor(Color.parseColor("#F34264"))
                .append(tip).setFontSize(13,true).setForegroundColor(Color.BLACK)
                .create()
        tv_know.onClick {
            dismiss()
        }


    }

    override fun dismiss() {
        callBack?.dismissSuccess()
        super.dismiss()
    }
    companion object{
        const val EXTRA_MONEY="EXTRA_MONEY"
        const val EXTRA_TIP = "EXTRA_TIP"
    }
}
interface CallBack {
    fun dismissSuccess()
}

