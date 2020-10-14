package com.snqu.shopping.ui.goods.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.data.goods.entity.ConsumerProtection
import com.snqu.shopping.ui.goods.adapter.ParameterAdapter
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.cecurity_dialog.*
import kotlinx.android.synthetic.main.parameter_dialog.recycler_view
import kotlinx.android.synthetic.main.parameter_dialog.tv_complete


/**
 * desc:商品详情参数
 * time: 2019/2/1
 * @author 银进
 */
@SuppressLint("ValidFragment")
class SecurityDialog @SuppressLint("ValidFragment") constructor(val itemSource: String?) : androidx.fragment.app.DialogFragment() {

    var securityList = mutableListOf<ConsumerProtection>()
    private val parameterAdapter by lazy {
        ParameterAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.cecurity_dialog, container)

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

        offscreen.onClick {
            dismiss()
        }

        if (itemSource == Constant.BusinessType.PDD) {
            recycler_view.visibility = View.GONE
            pdd_text.visibility = View.VISIBLE
            val str = StringBuffer()
            for (i in 0 until securityList.size) {
                str.append(securityList[i].desc ?: securityList[i].title)
                if (i != securityList.size - 1) {
                    str.append(",")
                }
            }
            pdd_text.text = str
        } else {
            recycler_view.visibility = View.VISIBLE
            pdd_text.visibility = View.GONE
            recycler_view.apply {
                layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
                adapter = parameterAdapter
            }
            parameterAdapter.setNewData(securityList)
        }


    }


}
