package com.snqu.shopping.ui.login.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snqu.shopping.R
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.bind_wx_dialog.*


/**
 * desc:提现成功
 * time: 2019/2/1
 * @author 银进
 */
class BindWXDialog : androidx.fragment.app.DialogFragment() {
    var callBack: CallBack? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.bind_wx_dialog, container)

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog.window
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        tv_bind.onClick {
            callBack?.bindWx()
        }
    }

//    fun isShow():Boolean{
//        return dialog.isShowing
//    }

    override fun show(manager: androidx.fragment.app.FragmentManager?, tag: String?) {
        try {
            //在每个add事务前增加一个remove事务，防止连续的add
            manager?.beginTransaction()?.remove(this)?.commit();
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

interface CallBack {
    fun bindWx()
}

