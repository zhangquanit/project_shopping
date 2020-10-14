package com.snqu.shopping.data.user.entity

/**
 * desc:
 * time: 2019/11/25
 * @author 银进
 */
data class AccountInfoEntity(
        val username: String? = null,//昵称
        val avatar: String? = null,//头像
        val itime: Long? = null,//注册时间
        val today_total: Long? = null,//今日收益
        val today_estimate: Long? = null,//今日预估收益
        val yesterday_total: Long? = null,//昨日收益
        val yesterday_estimate: Long? = null,//昨日预估收益
        val nowmonth_estimate: Long = 0L,//当月预估收益
        val nowmonth_total: Long = 0L,//当月收益
        val lastmonth_total: Long = 0L,//上月收益
        val lastmonth_estimate: Long = 0L,//上月预估收益
        val unsettled_amount: Long? = null,//总未结算佣金
        val helptext: String? = null,//帮助说明
        val vaild_direct_vip: Long? = 0L, //有效直接会员
        val invaild_direct_vip: Long? = 0L, //无效直接会员
        val valid_order_count_total: Long? = 0L, //有效订单数
        val invalid_order_count_total: Long? = 0L //无效订单数

) {
}