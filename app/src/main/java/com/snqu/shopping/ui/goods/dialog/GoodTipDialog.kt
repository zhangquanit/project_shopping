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
import kotlinx.android.synthetic.main.balance_detail_dialog.*


/**
 * desc:余额说明
 * time: 2019/2/1
 * @author 银进
 */
class GoodTipDialog : androidx.fragment.app.DialogFragment() {


    private var title: String? = null
    private var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.balance_detail_dialog, container)
        return view
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog.window
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        tv_content?.text = "余额：指已结算的返利金，可进行提现\n" +
                "冻结金额：即提现中的返利金，提现到账时间1~7天"
        tv_know.onClick {
            dismiss()
        }
        if(title!=null){
            tv_title.text = title
        }
        if(content!=null){
            tv_content.text =content
        }
    }

    fun setContent(title: String, content: String) {
        this.title = title
        this.content = content
    }


}
