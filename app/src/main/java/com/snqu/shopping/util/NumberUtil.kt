package com.snqu.shopping.util

import android.text.TextUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * desc:
 * time: 2019/8/26
 * @author 银进
 */
object NumberUtil {
    @JvmStatic
    fun saveTwoPoint(num: Long?): String {
        if (num == null) {
            return "0.00"
        }
        if (num == 0L) {
            return "0.00"
        }
        val doubleNum = num / 100.00
        val result = DecimalFormat("0.00").format(doubleNum)
        if (result == null || result == "null") {
            return "0.00"
        }
        return result
    }

    @JvmStatic
    fun saveOnePoint(num: Long?): String {
        if (num == null) {
            return "0.0"
        }
        if (num == 0L) {
            return "0.0"
        }
        val doubleNum = Math.abs(num) / 100.00
        return DecimalFormat("0.00").format(doubleNum)
    }

    @JvmStatic
    fun sellCount(num: Int?): String {
        if (num == null) {
            return "0"
        }

        if (num >= 10000) {
            val doubleNum = Math.abs(num) / 10000.00
            return DecimalFormat("0.0").format(doubleNum) + "万"
        }
        return num.toString()
    }

    @JvmStatic
    fun couponPrice(num: Int?): String {
        if (num == null) {
            return "0"
        }
        val doubleNum = Math.abs(num) / 100.00
        return formatDouble(doubleNum)
    }

    /**
     * 如果是整数就返回整数，否则返回两位小数
     */
    @JvmStatic
    fun formatDouble(d: Double): String {
        val bg = BigDecimal(d).setScale(2, RoundingMode.FLOOR)
        val num = bg.toDouble()
        if (Math.round(num) - num == 0.0) {
            return num.toLong().toString()
        }
        return num.toString()
    }

    /**
     * 如果是整数就返回整数，否则返回两位小数
     */
    @JvmStatic
    fun getDouble(d: String?): Double {
        if (d == null || d == "") {
            return 0.0
        }
        val bd = BigDecimal(d.toDouble())
        val dd: BigDecimal? = bd.setScale(2, BigDecimal.ROUND_HALF_UP)
        var num = dd?.toDouble() ?: 0.0
        //如果为正整数
        d.let {
            if (it.contains("0.")) {
                num = num * 100
            }
        }
        return num
    }

    @JvmStatic
    fun getDoubleTwo(d1: String?, d2: String?): Pair<Double, Double> {
        var d_1 = d1
        var d_2 = d2
        if (TextUtils.isEmpty(d_1)) {
            d_1 = "0.0"
        }
        if (TextUtils.isEmpty(d_2)) {
            d_2 = "0.0"
        }
        // 进行数据处理
        val bd = BigDecimal(d_1!!.toDouble())
        val dd: BigDecimal? = bd.setScale(2, BigDecimal.ROUND_HALF_UP)
        var num = dd?.toDouble() ?: 0.0

        val bd2 = BigDecimal(d_2!!.toDouble())
        val dd2: BigDecimal? = bd2.setScale(2, BigDecimal.ROUND_HALF_UP)
        var num2 = dd2?.toDouble() ?: 0.0

        if (d_1.contains(".") || d_2.contains(".")) {
            num *= 100
            num2 *= 100
        }
        return Pair(num, num2)
    }

}