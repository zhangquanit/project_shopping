package com.snqu.shopping.ui.order.dialog

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.SPUtil
import com.android.util.ext.ToastUtil
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.order.entity.OrderShareCodeEntity
import com.snqu.shopping.ui.order.vm.OrderViewModel
import com.snqu.shopping.util.ShareUtil
import com.snqu.shopping.util.ext.onClick
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.order_share_dialog_item.view.*
import kotlinx.android.synthetic.main.share_rule_dialog.*
import kotlinx.android.synthetic.main.share_rule_item.*


/**
 * desc:
 * time: 2019/8/19
 * @author 银进
 */
class ShareRuleDialog : androidx.fragment.app.DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.update_dialog_style)
    }
    private val orderViewModel by lazy {
        ViewModelProviders.of(this).get(OrderViewModel::class.java)
    }
    private val orderId by lazy {
        arguments?.getString(EXTRA_ORDER_ID)
    }
    private var isSelected = false
    private var isJump = false
    private var isType = 0
    private var orderShareCodeEntity:OrderShareCodeEntity? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.share_rule_dialog, container)

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val window = dialog.window
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        orderViewModel.dataResult.observe(this, Observer {
            if (it?.tag == ApiHost.ORDER_SHARE_CODE) {
                if (it.successful) {
                    orderShareCodeEntity= it.data as OrderShareCodeEntity?
                    if (isJump) {
                        when (isType) {
                            0-> shareThird(SHARE_MEDIA.WEIXIN)
                            1-> shareThird(SHARE_MEDIA.QQ)
                            2-> shareThird(SHARE_MEDIA.SINA)
                            3->copyUrl()
                        }
                    }
                }
            }
        })
        orderViewModel.doOrderShareCode(orderId)
        isSelected = SPUtil.getBoolean(SELECTED_STATUS, false)
        if (isSelected) {
            cos_center.visibility = View.GONE
            val inflate  = cos_bottom.inflate()
            inflate.ll_wx.onClick {
                isType=0
                if (orderShareCodeEntity == null) {
                    isJump=true
                    return@onClick
                }
                isJump=false
                shareThird(SHARE_MEDIA.WEIXIN)
            }
            inflate.ll_qq.onClick {
                isType=1
                if (orderShareCodeEntity == null) {
                    isJump=true
                    return@onClick
                }
                isJump=false
                shareThird(SHARE_MEDIA.QQ)
            }
            inflate.ll_wb.onClick {
                isType=2
                if (orderShareCodeEntity == null) {
                    isJump=true
                    return@onClick
                }
                isJump=false
                shareThird(SHARE_MEDIA.SINA)
            }
            inflate.ll_url.onClick {
                isType=3
                if (orderShareCodeEntity == null) {
                    isJump=true
                    return@onClick
                }
                isJump=false
                copyUrl()
            }
            inflate.tv_cancel.onClick {
                dismiss()
            }
        }
        changeSelectedStatus()
        img_check.onClick {
            isSelected=!isSelected
            SPUtil.setBoolean(SELECTED_STATUS, isSelected)
            changeSelectedStatus()
        }
        tv_know.onClick {
            cos_center.visibility = View.GONE
            val inflate  = cos_bottom.inflate()
            inflate.ll_wx.onClick {
                isType=0
                if (orderShareCodeEntity == null) {
                    isJump=true
                    return@onClick
                }
                isJump=false
                shareThird(SHARE_MEDIA.WEIXIN)
            }
            inflate.ll_qq.onClick {
                isType=1
                if (orderShareCodeEntity == null) {
                    isJump=true
                    return@onClick
                }
                isJump=false
                shareThird(SHARE_MEDIA.QQ)
            }
            inflate.ll_wb.onClick {
                isType=2
                if (orderShareCodeEntity == null) {
                    isJump=true
                    return@onClick
                }
                isJump=false
                shareThird(SHARE_MEDIA.SINA)
            }
            inflate.ll_url.onClick {
                isType=3
                if (orderShareCodeEntity == null) {
                    isJump=true
                    return@onClick
                }
                isJump=false
                copyUrl()
            }
            inflate.tv_cancel.onClick {
               dismiss()
            }
        }

    }

    private fun copyUrl() {
        val cmb = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cmb.primaryClip = ClipData.newPlainText(null, Constant.WebPage.SHARE_INVITE_URL + orderShareCodeEntity?.invite_code)
        ToastUtil.show("复制成功")
        dismiss()
    }

    private fun shareThird(media: SHARE_MEDIA) {
        ShareUtil.share(activity, orderShareCodeEntity?.invite_code,media )
        dismiss()
    }

    private fun changeSelectedStatus() {
        img_check.setImageResource(if (!isSelected) {
            R.drawable.icon_rule_unselected
        } else {
            R.drawable.icon_rule_selected
        }
        )
    }

    companion object {
        const val SELECTED_STATUS = "SELECTED_STATUS"
        const val EXTRA_ORDER_ID = "EXTRA_ORDER_ID"
    }



}
