package com.snqu.shopping.data.user.entity

/**
 * desc:
 * time: 2019/8/26
 * @author 银进
 */
data class BalanceInfoEntity(
        //我的收益
        val amount_total: Long? = null, //总收益
        val freeze_amount: Long? = null,  //冻结金额(提现中金额)
        val unsettled_amount: Long? = null, //未结算金额
        val withdraw_amount: Long? = null, //总提现
        val amount_useable: Long? = null, //可提现余额 = 总收益- 总提现
        val withdraw_success_amount: Long? = null, //已提现
        val helptext: String? = null
)