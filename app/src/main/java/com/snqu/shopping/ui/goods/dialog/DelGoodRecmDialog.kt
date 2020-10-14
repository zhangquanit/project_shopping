package com.snqu.shopping.ui.goods.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snqu.shopping.R
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.del_good_rcm_dialog.*


class DelGoodRecmDialog : androidx.fragment.app.DialogFragment() {

    var clickLister: View.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.del_good_rcm_dialog, container)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val window = dialog.window
////        window?.apply {
////            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
////            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
////        }
        btn_cancel.onClick {
            dialog.cancel()
        }
        if(clickLister!=null){
            btn_sure.setOnClickListener(clickLister)
        }
    }

    fun setClick(clickLister: View.OnClickListener) {
        this.clickLister = clickLister
//        btn_sure.setOnClickListener(clickLister)
    }

    override fun show(manager: androidx.fragment.app.FragmentManager?, tag: String?) {
        super.show(manager, tag)
    }

}
