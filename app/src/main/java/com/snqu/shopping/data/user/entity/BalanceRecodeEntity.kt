package com.snqu.shopping.data.user.entity

/**
 * desc:
 * time: 2019/8/28
 * @author 银进
 */
data class BalanceRecodeEntity(
        /**
        "_id":"5dd67d3602dce6d4d2000dc4",                //类型：String  必有字段  备注：无
        "type":20,                //类型：Number  必有字段  备注：类型[10:商品返利 12:直接下级用户分佣 13:直接二代下级用户分佣 14:订单失效 15:金额变动 16:维权扣款 20:提现申请 21:提现拒绝 30:vip奖励 40:关系变动补偿]
        "total_amount":-100,                //类型：Number  必有字段  备注：变动金额 有正负数
        "withdraw_status":0,                //类型：Number  必有字段  备注：当type=20时出现 申请中的各个状态-1:申请已拒绝1:冻结中2:审核通过3:付款完成99:付款失败
        "alipay_account":"13398208750",                //类型：String  必有字段  备注：无
        "itime":1574337846,                //类型：Number  必有字段  备注：记录生成时间
        "item_title":"mixed",                //类型：Mixed  必有字段  备注：说明 如商品名或提现账号
        "type_text":"提现",                //类型：String  必有字段  备注：类型说明
        "withdraw_status_text":"冻结中",                //类型：String  必有字段  备注：当type=20时出现 申请中的各个状态说明
        "item_source":"mock"                //类型：String  可有字段  备注：商品平台[D:京东，C：淘宝，B：天猫]
         */
        val _id: String? = null,
        val type: String? = null,//类型[10:商品返利  12:直接下级用户分佣   13:直接二代下级用户分佣 14:订单失效 15:金额变动 16:维权扣款 20:提现申请 21:提现拒绝 30:vip奖励 40:关系变动补偿]
        val total_amount: Long = 0,
        val withdraw_status: Long = 0,
        val alipay_account: String,
        val itime: Int? = null,//时间
        val item_title: String? = null,
        val type_text: String? = null,
        val withdraw_status_text: String? = null,
        val item_source: String? = null,
        val reason: String? = null,
        val icon_url: String? = null
) {
        override fun toString(): String {
                return "BalanceRecodeEntity(_id=$_id, type=$type, total_amount=$total_amount, withdraw_status=$withdraw_status, alipay_account='$alipay_account', itime=$itime, item_title=$item_title, type_text=$type_text, withdraw_status_text=$withdraw_status_text, item_source=$item_source, reason=$reason, icon_url=$icon_url)"
        }
}
