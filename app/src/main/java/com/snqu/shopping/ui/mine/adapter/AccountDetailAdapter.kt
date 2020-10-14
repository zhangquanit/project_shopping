package com.snqu.shopping.ui.mine.adapter

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import com.android.util.date.DateFormatUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.data.home.ItemSourceClient
import com.snqu.shopping.data.user.entity.BalanceRecodeEntity
import com.snqu.shopping.ui.main.view.TipDialogView
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.NumberUtil
import common.widget.dialog.EffectDialogBuilder
import kotlinx.android.synthetic.main.account_details_item.view.*
import java.util.*

/**
 * desc:
 * time: 2019/8/14
 * @author 银进
 */
class AccountDetailAdapter : BaseQuickAdapter<BalanceRecodeEntity, BaseViewHolder>(R.layout.account_details_item) {
    override fun convert(helper: BaseViewHolder, item: BalanceRecodeEntity?) {
        helper?.itemView?.apply {
            img_type.setImageResource(
                    when (item?.type ?: "1") {
                        //备注：类型 10:商品返利 12:直接下级用户分佣 13:直接二代下级用户分佣 14:订单失效 15:金额变动 16:维权扣款 17:退款/售后 返还返利金(部分) 18:退款/售后 返还返利金 19:维权失败 20:提现申请 21:提现拒绝 30:vip奖励 40:关系变动补偿
                        "20", "21", "22" -> R.drawable.icon_alipay
                        "30", "40" -> R.drawable.icon_balance_vip  //
                        else -> R.drawable.icon_freeze  //默认
                    }
            )
            //如果有item_source就优先考虑
            if (!item?.icon_url.isNullOrEmpty()) {
                GlideUtil.loadPic(img_type, item?.icon_url, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
            } else {
                if (!item?.item_source.isNullOrEmpty()) {
                    var itemSourceEntity = ItemSourceClient.getItemSourceEntity(ItemSourceClient.ItemSourceType.EARN, item?.item_source!!)
                    if (null != itemSourceEntity) {
                        GlideUtil.loadPic(img_type, itemSourceEntity.icon, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
                    } else {
                        img_type.setImageResource(R.drawable.icon_min_default_pic)
                    }
                }
            }
            tv_type.text = item?.type_text ?: ""
            tv_content.text = item?.item_title ?: ""

            tv_time.text = if (item?.itime == null) {
                ""
            } else {
                DateFormatUtil.yyyy_MM_dd_HH_mm().format(Date(item.itime * 1000L))
            }

            tv_type.setTextColor(Color.parseColor("#ff25282d"))

            if (!TextUtils.isEmpty(item?.reason)) {
                tv_account_reason.visibility = View.VISIBLE
                tv_account_reason.setOnClickListener {
                    val tipDialogView = TipDialogView(mContext, item?.withdraw_status_text ?: "提现失败", item?.reason
                            ?: "")
                    EffectDialogBuilder(mContext)
                            .setContentView(tipDialogView)
                            .show()
                }
                when (item?.type) {
                    "20" -> {//提现失败
                        tv_type.setTextColor(Color.parseColor("#fff34264"))
                    }
                }
            } else {
                tv_account_reason.visibility = View.GONE
            }

            tv_state.text = item?.withdraw_status_text ?: ""
            if ((item?.total_amount ?: 0) >= 0) {
                tv_money.text = "+${NumberUtil.saveTwoPoint(item?.total_amount)}"
                tv_money.setTextColor(Color.parseColor("#25282D"))
            } else {
                tv_money.text = "${NumberUtil.saveTwoPoint(item?.total_amount)}"
                tv_money.setTextColor(Color.parseColor("#F73737"))
            }


        }
    }
}