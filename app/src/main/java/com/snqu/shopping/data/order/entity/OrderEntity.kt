package com.snqu.shopping.data.order.entity

import android.os.Parcelable
import com.snqu.shopping.util.NumberUtil
import kotlinx.android.parcel.Parcelize

/**
 * desc:
 * time: 2019/8/30
 * @author 银
 */
@Parcelize
data class OrderEntity(
        val _id: String?,
        val item_id: String?,
        val goods_id: String?,
        val xlt_total_amount: Long?,//预估返利金额
        val item_image: String?,//商品图片
        val item_num: Int?,//商品数量
        val item_price: Int?,//商品价格
        val item_source: String?,
        val item_source_text: String?,
        val highlight: String?,//高亮文字字段
        val item_title: String?,//商品名称
        val paid_amount: Long?,//支付金额
        val create_time: String?,//下单时间
        val xlt_refund_status: String?,//维权状态 0 无维权，1 维权创建[维权处理中] 2 维权成功，3 维权失败
        val xlt_refund_status_text: String?,//维权状态 0 无维权，1 维权创建[维权处理中] 2 维权成功，3 维权失败
        val xlt_estimated_settle_time: String?,//剩余多少天
        val xlt_order_tips: String?,//文案
        val xlt_settle_status: Int?,//订单状态[1:即将到账,2:已到账,10:已失效]
        val xlt_settle_status_text: String?,//订单状态[1:即将到账,2:已到账,10:已失效]
        val third_order_id: String?,//订单编号,
        val type: Int, //区分订单来自于哪个平台
        val discount: Long, // 折扣
        var activity_voucher_tips: String,// 星币商品 tips 显示的文案
        val text_color: String, //订单状态颜色
        var from: String, //订单来源
        val creditCardOrderInfo:CreditCardOrderInfo //信用卡信息
) : Parcelable {
    val price: String
        //商品价格转换后
        get() = NumberUtil.saveTwoPoint(paid_amount)
    val estimatedRebateAmount: String
        //预估返利金额转换后
        get() = NumberUtil.saveTwoPoint(xlt_total_amount)
}
