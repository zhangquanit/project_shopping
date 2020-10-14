package com.snqu.shopping.ui.order.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.order.entity.OrderEntity
import com.snqu.shopping.ui.order.adapter.FindOrderSuccessAdapter
import com.snqu.shopping.ui.order.vm.OrderViewModel
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.find_order_success_fragment.*

/**
 * desc:找回订单失败
 * time: 2019/10/10
 * @author 银进
 */
class FindOrderSuccessFragment : SimpleFrag() {
    private val findOrderSuccessAdapter by lazy {
        FindOrderSuccessAdapter()
    }
    private val orderData by lazy {
        arguments?.getParcelableArrayList<OrderEntity>(EXTRA_DATA) ?: arrayListOf()
    }
    private val orderViewModel by lazy {
        ViewModelProviders.of(this).get(OrderViewModel::class.java)
    }

    override fun getLayoutId() = R.layout.find_order_success_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        initView()
    }

    /**
     * 初始化View
     */
    private fun initView() {
        orderViewModel.dataResult.observe(this, Observer {
            if (it?.tag == ApiHost.ORDER_RETRIEVE) {
                if (it.successful) {
                    FindOrderSuccessFrag.start(activity)
                    finish()
                } else {
                    showToastShort(it.message)
                }
            }
        })
        recycler_view.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
            adapter = findOrderSuccessAdapter
        }
        findOrderSuccessAdapter.setNewData(orderData)
        tv_sure.onClick {
            orderViewModel.doOrderRetrieve(orderData.map {
                it._id ?: ""
            }.toList())
        }
    }

    companion object {
        const val EXTRA_DATA = "EXTRA_DATA"
        fun start(context: Context?, orderData: ArrayList<OrderEntity>) {
            val fragParam = SimpleFragAct.SimpleFragParam("找回订单",
                    FindOrderSuccessFragment::class.java, Bundle().apply {
                putParcelableArrayList(EXTRA_DATA, orderData)
            })
            SimpleFragAct.start(context, fragParam)
        }
    }
}