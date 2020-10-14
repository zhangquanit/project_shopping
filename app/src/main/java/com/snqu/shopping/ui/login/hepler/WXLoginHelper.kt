package com.snqu.shopping.ui.login.hepler

import com.snqu.shopping.data.user.entity.UserEntity

/**
 * desc:
 * time: 2019/11/28
 * @author 银进
 */
object WXLoginHelper {
    @JvmStatic
    val FROM_LOGINFRAGMENT = 1 //登录页--微信登录
    @JvmStatic
    val FROM_MSGCODE = 2  //验证码--微信登录
    @JvmStatic
    val FROM_INVITECODE = 3 //邀请码--微信登录
    @JvmStatic
    var codePage = 1 
}