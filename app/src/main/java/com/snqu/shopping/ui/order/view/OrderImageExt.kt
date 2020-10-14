package com.snqu.shopping.ui.order.view

import android.graphics.Color
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.android.util.os.DeviceUtil

fun AppCompatImageView.orderSelectTab(isChecked: Boolean) {
    val layoutParams = this.layoutParams
    layoutParams.width = DeviceUtil.dip2px(this.context, if (isChecked) 47.5F else 40F)
    layoutParams.height = DeviceUtil.dip2px(this.context, if (isChecked) 47.5F else 40F)
    this.layoutParams = layoutParams
}


fun TextView.orderSelectTab(isChecked: Boolean) {
    this.textSize = if (isChecked) 16F else 13F
    this.setTextColor(Color.parseColor(if (isChecked) "#FFFF8202" else "#ff848487"))
}


