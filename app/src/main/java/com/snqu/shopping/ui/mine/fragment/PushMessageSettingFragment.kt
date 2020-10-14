package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.ConvertUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.PushMessageEntity
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.mine.view.ItemDecoration
import com.snqu.shopping.util.NotificationPageHelper
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.pushsetting_fragment.*


/**
 * 推送设置
 */
class PushMessageSettingFragment : SimpleFrag() {

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    override fun getLayoutId() = R.layout.pushsetting_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)

        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.UMENG_ACTION_LIST -> {
                    if (it.successful) {
                        if (it.data != null) {
                            val list = it.data as List<PushMessageEntity>
                            if (list != null && list.isNotEmpty()) {
                                val adapter = object : BaseQuickAdapter<PushMessageEntity, BaseViewHolder>(R.layout.pushsetting_item, list) {
                                    override fun convert(helper: BaseViewHolder, item: PushMessageEntity?) {
                                        helper.getView<TextView>(R.id.tv).text = item?.title
                                        val cb = (helper.getView<AppCompatCheckedTextView>(R.id.cb))
                                        cb.isChecked = item?.enable.equals("1")
                                        cb.onClick {
                                            cb.isChecked = !cb.isChecked
                                            if (cb.isChecked) {
                                                userViewModel.banPushMessage(item!!._id, UserClient.getUser()._id, "1")
                                            } else {
                                                userViewModel.banPushMessage(item!!._id, UserClient.getUser()._id, "0")
                                            }
                                        }
                                        helper.getView<View>(R.id.linearLayout).onClick {
                                            cb.performClick()
                                        }
                                    }
                                }
                                recy_list.adapter = adapter
                                val footerView = TextView(activity)
                                footerView.text = "请开启系统通知，以免错过各种重要消息"
                                footerView.setTextColor(Color.parseColor("#B0B0B0"))
                                footerView.textSize = 12F
                                footerView.setPadding(ConvertUtils.dp2px(15F), ConvertUtils.dp2px(20F), 0, ConvertUtils.dp2px(20F))
                                adapter.setFooterView(footerView)
                                loadingview.visibility = View.GONE
                                NotificationPageHelper.open(activity)
                            } else {
                                loadingview.setStatus(LoadingStatusView.Status.EMPTY)
                            }
                        } else {
                            loadingview.setStatus(LoadingStatusView.Status.EMPTY)
                        }
                    } else {
                        loadingview?.apply {
                            setStatus(LoadingStatusView.Status.FAIL)
                            setOnBtnClickListener {
                                loadingview.setStatus(LoadingStatusView.Status.LOADING)
                                userViewModel.getActionList(UserClient.getUser()._id)
                            }
                        }
                    }
                }
            }
        })
        loadingview.setStatus(LoadingStatusView.Status.LOADING)
        recy_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(mContext)
        recy_list.addItemDecoration(ItemDecoration(activity))
        userViewModel.getActionList(UserClient.getUser()._id)
    }

    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("推送设置",
                    PushMessageSettingFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }

}