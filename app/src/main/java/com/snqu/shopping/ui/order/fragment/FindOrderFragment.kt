package com.snqu.shopping.ui.order.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.ToastUtil
import com.android.util.text.StringUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.jakewharton.rxbinding2.widget.RxTextView
import com.snqu.shopping.R
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.order.entity.OrderEntity
import com.snqu.shopping.ui.order.vm.OrderViewModel
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.find_order_fragment.*
import java.util.*

/**
 * desc:找回订单
 * time: 2019/10/10
 * @author 银进
 */
class FindOrderFragment : SimpleFrag() {
    private val orderViewModel by lazy {
        ViewModelProviders.of(this).get(OrderViewModel::class.java)
    }
    override fun getLayoutId() = R.layout.find_order_fragment

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
            if (it?.tag == ApiHost.ORDER_LIST ) {
                et_search.setText("")
                //第一次加載
                if (it.successful) {
                    val data = it.data as ArrayList<OrderEntity>
                    if (data.isNotEmpty()) {
                        FindOrderSuccessFragment.start(activity,data)
                    } else {
                        FindOrderFailureFragment.start(activity)
                    }
                } else {
                    FindOrderFailureFragment.start(activity)
                }

            }

        })
        tv_find_order_rule.text = "1、付款后10分钟内会自动同步订单，无需手动找回\n" +
                "2、只有通过星乐桃APP/小程序下单的商品，才能用到该功能\n" +
                "3、付款时使用红包抵扣，则无法获得返利金，建议退款重拍\n" +
                "4、当有多个用户输入同一订单号，以第一个用户输入的为准\n" +
                "5、已归属的订单，不支持继续查询\n" +
                "6、若下单后订单不显示，可联系客服查询详细原因"
        img_clear_all.onClick {
            et_search.setText("")
        }
        RxTextView.textChanges(et_search).subscribe {
            if (it.isNullOrBlank()) {
                img_clear_all.visibility = View.GONE
            } else {
                img_clear_all.visibility = View.VISIBLE
            }
        }
        et_search.setOnEditorActionListener { v, keyCode, event ->
            if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE || keyCode == EditorInfo.IME_ACTION_GO || keyCode == KeyEvent.KEYCODE_ENTER) {
                if (TextUtils.isEmpty(StringUtil.trim(et_search.text.toString()))) {
                    ToastUtil.show("请输入订单编号")
                } else {
                    search()
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    /**
     * 搜索要找回的订单
     */
    private fun search() {
        orderViewModel.doOrderList(null, null, 50, 1, null, null, et_search.text.toString())
    }

    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("找回订单",
                    FindOrderFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }
}