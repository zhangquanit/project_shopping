package com.snqu.shopping.ui.order.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import kotlinx.android.synthetic.main.find_order_failure_fragment.*

/**
 * desc:找回订单失败
 * time: 2019/10/10
 * @author 银进
 */
class FindOrderFailureFragment : SimpleFrag() {
    override fun getLayoutId() = R.layout.find_order_failure_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        initView()
    }

    /**
     * 初始化View
     */
    private fun initView() {
        tv_content.text = "1、订单有延迟，建议下单15分钟后再查询\n" +
                "2、非您购买的订单\n" +
                "3、不是通过星乐桃APP或星乐桃小程序下单的订单\n" +
                "4、输入的订单号，是已归属的订单\n" +
                "5、订单号输入不正确\n"

    }

    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("找回订单",
                    FindOrderFailureFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }
}