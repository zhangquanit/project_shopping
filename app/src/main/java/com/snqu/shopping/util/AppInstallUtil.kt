package com.snqu.shopping.util

import android.content.Context
import com.blankj.utilcode.util.AppUtils

/**
 * desc:
 * time: 2019/9/6
 * @author 银进
 */
object AppInstallUtil {
    @JvmStatic
    fun isAppInstalled(context: Context?, packageName: String): Boolean {
        return AppUtils.isAppInstalled(packageName) || isAppInstalled1(context, packageName)
    }


    @JvmStatic
    fun isAppInstalled1(context: Context?, packageName: String): Boolean {
        val packageManager = context?.packageManager// 获取packagemanager
        val pinfo = packageManager?.getInstalledPackages(0)// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (i in pinfo.indices) {
                val pn = pinfo[i].packageName
                if (pn.equals(packageName, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }
}