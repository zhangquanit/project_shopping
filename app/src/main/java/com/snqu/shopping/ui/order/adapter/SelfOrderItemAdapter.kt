package com.snqu.shopping.ui.order.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.data.order.entity.OrderEntity
import com.snqu.shopping.ui.order.bean.Type
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.NumberUtil
import kotlinx.android.synthetic.main.self_order_item.view.*
import java.text.SimpleDateFormat

/**
 * desc:我的订单
 * time: 2019/8/19
 * @author 银进
 */
class SelfOrderItemAdapter(private val isShowTag: Boolean = false) : BaseQuickAdapter<OrderEntity, BaseViewHolder>(R.layout.self_order_item) {

    @SuppressLint("SetTextI18n")
    override fun convert(helper: BaseViewHolder, item: OrderEntity?) {

        helper.itemView.apply {

            //订单编号
            tv_order_number.text = "订单编号：${item?.third_order_id ?: ""}"

            //是否是机器人订单
            if (TextUtils.equals(item?.from, "wx_robot")) {
                tv_order_number.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_robot, 0)
            } else {
                tv_order_number.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            // 星币展示券
            if (TextUtils.isEmpty(item?.activity_voucher_tips)) {
                xb_order_item.visibility = View.GONE
            } else {
                xb_order_item.visibility = View.VISIBLE
                xb_order_item.text = item?.activity_voucher_tips
            }

            //返利金
            ll_rebate.setPadding(0, ConvertUtils.dp2px(18F), 0, 0)
            if ((item?.xlt_total_amount ?: 0L) == 0L || item?.xlt_refund_status == "1") {
                tv_estimated_rebate_amount.visibility = View.GONE
            } else {
                tv_estimated_rebate_amount.visibility = View.VISIBLE
                if (item?.creditCardOrderInfo == null) {
                    tv_estimated_rebate_amount.text = SpanUtils()
                            .append("预估返：").setForegroundColor(Color.parseColor("#25282D")).setFontSize(11, true)
                            .append("¥ ").setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(11, true)
                            .append(item?.estimatedRebateAmount
                                    ?: "").setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(11, true)
                            .create()
                } else {
                    tv_estimated_rebate_amount.text = SpanUtils()
                            .append("预估返 ").setForegroundColor(Color.parseColor("#26282E")).setFontSize(14, true)
                            .setBold()
                            .append("¥ " + item.estimatedRebateAmount).setForegroundColor(Color.parseColor("#FE2A22")).setFontSize(16, true).setBold().create()
                }
            }


            //订单图片
            GlideUtil.loadPic(img_pic, item?.item_image, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)

            //订单title
            if (isShowTag) {
                tv_title.setText(item?.item_source_text, item?.item_title ?: "")
                tv_title.setLineSpacing(0f, 1.2f)
            } else {
                tv_title.setText(item?.item_title ?: "")
            }

            //商品价格
            if (item?.creditCardOrderInfo == null) {
                tv_parameter.text = "商品价格：${item?.price ?: ""}  数量：${item?.item_num ?: 0}"
                if (item?.item_source != null) {
                    if (TextUtils.equals(item.item_source.toString(), Type.CZB.name)) {
                        tv_parameter.text = "商品价格：${item.price ?: ""}  折扣：${NumberUtil.saveTwoPoint(item.discount)}"
                    }
                }
            } else {
                //申请人
                tv_parameter.text = "申请人 " + item.creditCardOrderInfo.phone ?: ""
            }


            if (item?.creditCardOrderInfo == null) {
                if (item?.create_time == null) {
                    tv_create_order_time.text = "下单时间："
                } else {
                    tv_create_order_time.text = "下单时间：${item.create_time}"
                }
            } else {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val spanUtils = SpanUtils()
                ll_rebate.setPadding(0, ConvertUtils.dp2px(14F), 0, 0)
                if (!item.creditCardOrderInfo.itime.isNullOrEmpty()) {
                    spanUtils.append("申请时间  " + dateFormat.format((item.creditCardOrderInfo.itime).toLong() * 1000L))
                }
                if (!item.creditCardOrderInfo.accept_time.isNullOrEmpty()) {
                    ll_rebate.setPadding(0, 0, 0, 0)
                    spanUtils.appendLine()
                    when (item.creditCardOrderInfo.status){
                        "2" -> {
                            spanUtils.append("通过时间  " + dateFormat.format((item.creditCardOrderInfo.accept_time).toLong() * 1000L))
                        }
                        "3" -> {
                            spanUtils.append("未通过时间  " + dateFormat.format((item.creditCardOrderInfo.accept_time).toLong() * 1000L))
                        }
                    }
                }
                tv_create_order_time.text = spanUtils.create()
            }

            tv_status.text = item?.xlt_settle_status_text ?: ""
            if (!TextUtils.isEmpty(item?.text_color) && item?.text_color?.contains("#")!!) {
                tv_status.setTextColor(Color.parseColor(item.text_color))
            }


            //维权状态 0 无维权，1 维权创建[维权处理中] 2 维权成功，3 维权失败
            when (item?.xlt_refund_status) {
                "0" -> {
                    //正常订单，需要根据后台配置，来显示对应的文案，同时进行倒计时     注：这里的数字需要特别颜色标记
                    val description = item.xlt_order_tips ?: ""
                    if (TextUtils.isEmpty(description)) {
                        tv_description.visibility = View.GONE
                    } else {
                        tv_description.visibility = View.VISIBLE
                    }
                    if (item.highlight.isNullOrEmpty()) {
                        tv_description.setTextColor(Color.parseColor("#25282D"))
                        tv_description.text = description
                    } else {
                        val splitDescription = description.split(item.highlight)
                        //这儿只有捕获异常然后进行截取放在程序崩溃
                        try {
                            tv_description.text = SpanUtils()
                                    .append(splitDescription[0]).setForegroundColor(Color.parseColor("#25282D"))
                                    .append(item.highlight).setForegroundColor(Color.parseColor("#FFF73737"))
                                    .append(splitDescription[1]).setForegroundColor(Color.parseColor("#25282D"))
                                    .create()
                        } catch (e: Exception) {
                            tv_description.setTextColor(Color.parseColor("#25282D"))
                            tv_description.text = description
                        }
                    }

                }
                else -> {
                    tv_description.setTextColor(Color.parseColor("#FFF73737"))
                    tv_description.text = item?.xlt_order_tips ?: ""
                }
            }
//            if (item?.creditCardOrderInfo != null) {
//                when (item.creditCardOrderInfo.status) {
//                    "2" -> {
//                        tv_description.visibility = View.VISIBLE
//                    }
//                    "1", "3" -> {
//                        tv_description.visibility = View.GONE
//                    }
//                }
//            }


        }
    }
}