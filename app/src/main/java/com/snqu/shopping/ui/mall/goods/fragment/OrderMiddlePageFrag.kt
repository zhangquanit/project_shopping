package com.snqu.shopping.ui.mall.goods.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.SpanUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.mall.entity.PayResultDataEntity
import com.snqu.shopping.ui.main.MainActivity
import com.snqu.shopping.ui.mall.order.MallOrderFrag
import com.snqu.shopping.ui.mall.viewmodel.MallViewModel
import com.snqu.shopping.util.CommonUtil
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.pay.OrderPay
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.order_middle_fragment.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 订单支付中间页
 */
class OrderMiddlePageFrag : SimpleFrag() {

    companion object {

        const val EXTRA_ORDER_ID = "order_id" //支付订单
        const val EXTRA_ORDER_STATE = "order_state" //支付状态

        @JvmStatic
        fun start(context: Context?, order_id: String, pay_action: String) {
            val fragParam = SimpleFragAct.SimpleFragParam("确认订单",
                    OrderMiddlePageFrag::class.java, Bundle().apply {
                putString(EXTRA_ORDER_ID, order_id)
                putString(EXTRA_ORDER_STATE, pay_action)
            })
            SimpleFragAct.start(context, fragParam)
        }
    }

    //请求自营的ViewModel
    private val mallViewModel by lazy {
        ViewModelProviders.of(this).get(MallViewModel::class.java)
    }

    private val orderId by lazy {
        arguments?.getString(EXTRA_ORDER_ID) ?: ""
    }

    private val payState by lazy {
        arguments?.getString(EXTRA_ORDER_STATE) ?: ""
    }

    private var mLoadingDialog: LoadingDialog? = null

    override fun getLayoutId(): Int = R.layout.order_middle_fragment

    override fun init(savedInstanceState: Bundle?) {
        // 沉浸式状态栏设置
        StatusBar.setStatusBar(mContext, true, titleBar)
        titleBar.visibility = View.GONE

        addAction(Constant.Event.ORDER_BUY_SUCCESS)
        addAction(Constant.Event.ORDER_BUY_CANCEL)
        addAction(Constant.Event.ORDER_BUY_FAIL)

        if (orderId.isEmpty()) {
            finish()
        } else {

            mallViewModel.mNetReqResultLiveData.observe(this, Observer {
                when (it.tag) {
                    ApiHost.MALL_ORDER_RE_PAY -> {
                        closeLoadDialog()
                        if (it.successful) {
                            val payResultDataEntity = it.data as PayResultDataEntity
                            payResultDataEntity.let { pay ->
                                pay.sign?.let { sign ->
                                    OrderPay().alipay(mContext, sign)
                                }
                            }
                        } else {
                            ToastUtil.show(it.message)
                        }
                    }
                }
            })

            when (payState) {
                Constant.Event.ORDER_BUY_SUCCESS -> {
                    pay_title.text = "付款成功"
                    pay_icon.setImageResource(R.drawable.pay_success)
                    buy_btn.text = "继续购物"
                }
                Constant.Event.ORDER_BUY_CANCEL, Constant.Event.ORDER_BUY_FAIL -> {
                    pay_title.text = "付款失败"
                    pay_icon.setImageResource(R.drawable.pay_fail)
                    buy_btn.text = "重新支付"
                }
            }


            // 订单编号
            val spanUtils = SpanUtils()
            spanUtils.append("订单编号:").setForegroundColor(Color.parseColor("#848487"))
                    .setFontSize(13, true)
                    .append(orderId).setForegroundColor(Color.parseColor("#25282D")).setFontSize(13, true)
            order_num.text = spanUtils.create()

            //复制
            copy_num.onClick {
                val clipboardManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(null, orderId)
                clipboardManager.primaryClip = clipData
                ToastUtil.show("复制订单编号成功")
                CommonUtil.setClipboardText(clipData.toString())
            }

            order_btn.onClick {
                MallOrderFrag.start(mContext)
                finish()
            }

            buy_btn.onClick {
                if (TextUtils.equals(buy_btn.text, "继续购物")) {
                    MainActivity.startForPage(mContext,2)
                    finish()
                } else if (TextUtils.equals(buy_btn.text, "重新支付")) {
                    showLoadingDialog("加载中")
                    mallViewModel.goRePay(orderId)
                }
            }

            img_back.onClick {
                finish()
            }

            back_layout.onClick {
                img_back.performClick()
            }


        }


    }

    fun showLoadingDialog(content: String?) {
        mLoadingDialog = LoadingDialog.showBackCancelableDialog(mContext, content)
    }


    fun closeLoadDialog() {
        mLoadingDialog?.dismiss()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        when (event?.action) {
            Constant.Event.ORDER_BUY_SUCCESS -> {
                ToastUtil.show("支付成功")
                pay_title.text = "付款成功"
                pay_icon.setImageResource(R.drawable.pay_success)
                buy_btn.text = "继续购物"
            }
            Constant.Event.ORDER_BUY_CANCEL -> {
                ToastUtil.show("取消支付")
                pay_title.text = "付款失败"
                pay_icon.setImageResource(R.drawable.pay_fail)
                buy_btn.text = "重新支付"
            }
            Constant.Event.ORDER_BUY_FAIL -> {
                ToastUtil.show("支付失败")
                pay_title.text = "付款失败"
                pay_icon.setImageResource(R.drawable.pay_fail)
                buy_btn.text = "重新支付"
            }
        }
    }


}