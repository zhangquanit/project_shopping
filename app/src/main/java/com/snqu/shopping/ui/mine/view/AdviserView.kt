package com.snqu.shopping.ui.mine.view

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.android.util.ext.ToastUtil
import com.makeramen.roundedimageview.RoundedImageView
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.AlertDialogView
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.ui.goods.util.JumpUtil.openWechat
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ext.onClick

class AdviserView(ctx: Activity) : AlertDialogView(ctx) {

    override fun getLayoutId(): Int = R.layout.adviser_dialog

    override fun initView(view: View?) {
        val user = UserClient.getUser()
        val icon_inviter = view?.findViewById<RoundedImageView>(R.id.icon_inviter)
        val tv_name = view?.findViewById<TextView>(R.id.tv_name)
        val tv_tip = view?.findViewById<TextView>(R.id.tv_tip)
        if(!TextUtils.isEmpty(user.tutor_inviter_avatar)) {
            GlideUtil.loadPic(icon_inviter, user.tutor_inviter_avatar, R.drawable.icon_default_head, R.drawable.icon_default_head)
        }
        if(!TextUtils.isEmpty(user.username)) {
            tv_name?.text = "嗨，${user.username}"
        }
        tv_tip?.text = "我是你的专属导师，加我微信可领免费课程\n 一对一指导~快来加我的微信吧 \n ${user.tutor_wechat_show_uid}"

        view?.findViewById<View>(R.id.btn_left)
                ?.onClick {
                    dismiss()
                }

        view?.findViewById<View>(R.id.btn_right)
                ?.onClick {
                    dismiss()
                    try {
                        val clipData = ClipData.newPlainText(null, user.tutor_wechat_show_uid)
                        val clip = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clip.primaryClip = clipData
                        ToastUtil.show("微信号复制成功")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ToastUtil.show("复制失败")
                    }
                    try {
                        openWechat(ctx)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ToastUtil.show("打开微信失败，请确认是否安装微信客户端")
                    }
                }
    }

}