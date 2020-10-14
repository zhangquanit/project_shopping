package com.snqu.shopping.ui.vip.order.dialog

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.util.ext.ToastUtil
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.entity.VipOrderEntity
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.logistics_dialog.*


/**
 * desc:物流dialog
 * time: 2019/2/1
 * @author 银进
 */
class LogisticsDialog : androidx.fragment.app.DialogFragment() {
    private var vipOrderEntity:VipOrderEntity?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.logistics_dialog, container)

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    /**
     * 初始化View
     */
    @SuppressLint("SetTextI18n")
    private fun initView() {
        vipOrderEntity=arguments?.getParcelable<VipOrderEntity>(EXTRA_ORDER_ENTITY)
        val window = dialog.window
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        tv_user_info.text="${vipOrderEntity?.user_addr?.name}  ${vipOrderEntity?.user_addr?.phone}"
        tv_address.text=vipOrderEntity?.user_addr?.full_addr
        tv_logistics_shop.text=vipOrderEntity?.express_com
        tv_logistics_id.text=vipOrderEntity?.express_no

        if(TextUtils.isEmpty(vipOrderEntity?.express_com)){
            tv_logistics_shop.visibility=View.GONE
            textView5.visibility=View.GONE
        }
        if(TextUtils.isEmpty(vipOrderEntity?.express_no)){
            tv_logistics_id.visibility=View.GONE
            textView6.visibility=View.GONE
            tv_copy_logistics_id.visibility=View.GONE
        }

        tv_copy_logistics_id.onClick {
            val cmb = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cmb.primaryClip = ClipData.newPlainText(null, tv_logistics_id.text.toString())
            ToastUtil.show("复制成功")
        }
        tv_complete.onClick {
            dismiss()
        }
    }

    companion object{
        const val EXTRA_ORDER_ENTITY="EXTRA_ORDER_ENTITY"
    }
}
