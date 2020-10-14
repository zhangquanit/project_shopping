package com.snqu.shopping.ui.mine.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.snqu.shopping.R
import com.snqu.shopping.data.user.UserClient
import kotlinx.android.synthetic.main.vip_level_layout.view.*

/**
 * desc:用户等级vip
 * time: 2019/12/9
 * @author 银进
 */
class VipLevelView : FrameLayout {
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context) : super(context)

    init {
        LayoutInflater.from(context).inflate(R.layout.vip_level_layout, this)
    }

    /**
     * 刷新Vip等级的数据
     */
    fun refreshView() {
        if (UserClient.isLogin()) {
            tv_user_level.text = UserClient.vipLevel()
            tv_user_flag.text = UserClient.vipTagLevel()
            visibility = View.VISIBLE
            when (UserClient.getUser().level) {
                1 -> {
                    visibility = View.GONE
                }
                2 -> {
                    ll_user_level.setBackgroundResource(R.drawable.vip_level2_left_bg)
                    tv_user_level.setTextColor(Color.parseColor("#FFFFFFFF"))
                    tv_user_flag.setTextColor(Color.parseColor("#FFFF9B01"))
                }
                3 -> {
                    ll_user_level.setBackgroundResource(R.drawable.vip_level3_left_bg)
                    tv_user_level.setTextColor(Color.parseColor("#FFFFFFFF"))
                    tv_user_flag.setTextColor(Color.parseColor("#FFE3BB71"))
                }
                4 -> {
                    ll_user_level.setBackgroundResource(R.drawable.vip_level4_left_bg)
                    tv_user_level.setTextColor(Color.parseColor("#FFE8C48B"))
                    tv_user_flag.setTextColor(Color.parseColor("#FF292724"))
                }
            }
        } else {
            visibility = View.GONE
        }

    }


    fun refreshVipView() {
        if (UserClient.isLogin()) {
            tv_user_level.text = UserClient.vipLevel()
            tv_user_flag.text = UserClient.vipTagLevel()
            visibility = View.VISIBLE
            when (UserClient.getUser().level) {
                1 -> {
                    visibility = View.GONE
                }
                2 -> {
                    tv_user_flag.background=resources.getDrawable(R.drawable.vip_level3_flag_left_bg,null)
                    tv_user_flag.setTextColor(Color.parseColor("#FFFF9B01"))

                    ll_user_level.setBackgroundResource(R.drawable.vip_level2_left_bg)
                    tv_user_level.setTextColor(Color.parseColor("#FFFFFFFF"))
                }
                3 -> {
                    tv_user_flag.background=resources.getDrawable(R.drawable.vip_level3_flag_left_bg,null)
                    tv_user_flag.setTextColor(Color.parseColor("#FFE3BB71"))

                    ll_user_level.setBackgroundResource(R.drawable.vip_level3_left_bg)
                    tv_user_level.setTextColor(Color.parseColor("#FFFFFFFF"))
                }
                4 -> {
                    tv_user_flag.background=resources.getDrawable(R.drawable.vip_level4_flag_left_bg,null)
                    tv_user_flag.setTextColor(Color.parseColor("#121110"))

                    ll_user_level.setBackgroundResource(R.drawable.vip_level4_left_bg)
                    tv_user_level.setTextColor(Color.parseColor("#FFE8C48B"))
                }
            }
        } else {
            visibility = View.GONE
        }

    }
}