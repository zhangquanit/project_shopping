package com.snqu.shopping.ui.order.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.data.order.entity.OrderEntity
import com.snqu.shopping.ui.order.OrderActivity
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.find_order_success.*

class FindOrderSuccessFrag : SimpleFrag() {
    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        tv_to_order.onClick {
            OrderActivity.start(activity, 0, true)
            finish()
        }
    }

    override fun getLayoutId(): Int = R.layout.find_order_success

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("找回订单",
                    FindOrderSuccessFrag::class.java, Bundle().apply {
            })
            SimpleFragAct.start(context, fragParam)
        }
    }

}