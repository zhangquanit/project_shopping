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
import kotlinx.android.synthetic.main.rebate_description_dialog.*


/**
 * desc:余额说明
 * time: 2019/2/1
 * @author 银进
 */
class RebateDescriptionDialog : androidx.fragment.app.DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.rebate_description_dialog, container)

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog.window
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        tv_content.text="1、星乐桃返利为预估返利值，实际返利金额根据比例按下单支付金额进行计算\n" +
                "\n" +
                "2、部分红包和购物券会影响优惠\n" +
                "a. 不可返利的红包/卡券，包含且不仅限于以下几种，使用他们在星乐桃下单时整单没有返利！如：超级红包、天猫新人红包 、新人专享红包、新人福利社红包、淘礼金红包、淘红包、分享奖励红包、天猫购物券\n" +
                "\n" +
                "b. 可以返利的红包/卡券，卡券红包抵扣金额不能返利，该类型订单的返利，根据宝贝实际最终支付金额计算，如：店铺/商品优惠券、店铺红包 、类目购物券、旅行券等各种优惠券、购物津贴、淘金币、运费、税费（含报税商品）"
        tv_know.onClick {
            dismiss()
        }


    }


}
