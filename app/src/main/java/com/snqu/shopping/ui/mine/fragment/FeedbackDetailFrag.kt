package com.snqu.shopping.ui.mine.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.goods.bean.DetailImageBean
import com.snqu.shopping.data.goods.entity.VideoBean
import com.snqu.shopping.data.user.entity.FeedbackEntity
import com.snqu.shopping.data.user.entity.KefuEntity
import com.snqu.shopping.ui.goods.player.VideoImageActivity
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.mine.adapter.FeedbackDetailAdapter
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.feedback_detail_fragment.*

class FeedbackDetailFrag : SimpleFrag() {


    companion object {
        const val EXTRA_FEED = "FEED_BEAN"

        @JvmStatic
        fun start(context: Context, feedbackEntity: FeedbackEntity) {
            val fragParam = SimpleFragAct.SimpleFragParam("我的反馈",
                    FeedbackDetailFrag::class.java, Bundle().apply {
                putParcelable(EXTRA_FEED, feedbackEntity)
            })
            SimpleFragAct.start(context, fragParam)
        }
    }

    override fun getLayoutId(): Int = R.layout.feedback_detail_fragment
    private lateinit var mAdapter: FeedbackDetailAdapter
    private var kefuEntity: KefuEntity? = null

    private val feedbackEntity by lazy {
        arguments?.getParcelable<FeedbackEntity>(EXTRA_FEED)
    }

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        titleBar.setLeftBtnDrawable(R.drawable.feedback_close)
        if (feedbackEntity == null) {
            return
        }
        initValue()
    }

    private fun initValue() {
        container.visibility = View.GONE
        loadingStatusView.setStatus(LoadingStatusView.Status.LOADING)

        loadingStatusView.apply {
            setOnBtnClickListener {
                loadingStatusView.setStatus(LoadingStatusView.Status.LOADING)
                loadingData()
            }
        }

        mAdapter = FeedbackDetailAdapter().apply {
            this.setOnItemClickListener { adapter, view, position ->

                val list = ArrayList<DetailImageBean>()
                if (adapter.data[position].toString().contains("mp4")) {
                    val detailImageBean = DetailImageBean(true, adapter.data[position].toString(), VideoBean(adapter.data[position].toString(), adapter.data[position].toString()))
                    list.add(detailImageBean)
                } else {
                    val detailImageBean = DetailImageBean(false, adapter.data[position].toString(), null)
                    list.add(detailImageBean)
                }
                VideoImageActivity.start(activity, list)
            }
        }
//
        detail_listview.apply {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 4)
            adapter = mAdapter
        }

        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.GET_CONFIG_KEFU -> {
                    if (it.successful && it.data != null) {
                        kefuEntity = it.data as KefuEntity
                        if (kefuEntity != null && kefuEntity?.wx != null) {
                            text_kefu.text = "还有其他疑问，请联系官方微信客服\n" + kefuEntity?.wx
                        }
                        if (container.visibility == View.VISIBLE) {
                            kefu.visibility = View.VISIBLE
                        }
                    }
                }
                ApiHost.GET_FEEDBACK_DETAIL -> {

                    if (it.successful && it.data != null) {
                        if (kefuEntity != null) {
                            kefu.visibility = View.VISIBLE
                        }
                        container.visibility = View.VISIBLE
                        loadingStatusView.visibility = View.GONE
                        val feedbackEntity = it.data as FeedbackEntity
                        if (!TextUtils.isEmpty(feedbackEntity.reply_content)) {
                            my_feedback_input.text = feedbackEntity.reply_content
                            empty_layout.visibility = View.GONE
                            my_feedback_input.visibility = View.VISIBLE
                        } else {
                            empty_layout.visibility = View.VISIBLE
                            my_feedback_input.visibility = View.GONE
                        }
                        if (!TextUtils.isEmpty(feedbackEntity.phone)) {
                            phone_num.text = feedbackEntity.phone
                        }else{
                            phone_layout.visibility = View.GONE
                            view2.visibility = View.GONE
                        }
                        if (!TextUtils.isEmpty(feedbackEntity.content)) {
                            content.text = feedbackEntity.content
                        }
                        if (feedbackEntity.enclosure != null && feedbackEntity.enclosure.size > 0) {
                            view1.visibility = View.VISIBLE
                            view2.visibility = View.VISIBLE
                            mAdapter.setNewData(feedbackEntity.enclosure.asList())
                        } else {
                            view1.visibility = View.GONE
                            view2.visibility = View.GONE
                        }


                    } else {
                        ToastUtil.show(it.message)
                        container.visibility = View.GONE
                        loadingStatusView.setStatus(LoadingStatusView.Status.FAIL)
                    }
                }
            }
        })

        loadingData()

        btn_copy.onClick {
            if (kefuEntity != null && kefuEntity?.wx != null) {
                try {
                    val clipboardManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText(null, kefuEntity?.wx)
                    clipboardManager.primaryClip = clipData
                    ToastUtil.show("微信号复制成功")
                } catch (e: Exception) {
                    e.printStackTrace()
                    ToastUtil.show("微信号复制失败")
                }
            } else {
                ToastUtil.show("微信号复制失败")
            }
        }
    }

    private fun loadingData() {
        feedbackEntity?._id?.let {
            userViewModel.getFeedbackDetail(it)
            userViewModel.getConfigKefu()
        }
    }

}