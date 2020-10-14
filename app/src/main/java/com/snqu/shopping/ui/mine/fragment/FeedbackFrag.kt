package com.snqu.shopping.ui.mine.fragment

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
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.base.ResponseDataArray
import com.snqu.shopping.data.user.entity.FeedbackEntity
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.mine.adapter.FeedbackAdapter
import com.snqu.shopping.util.ext.clickWithTrigger
import kotlinx.android.synthetic.main.feedback_fragment.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FeedbackFrag : SimpleFrag() {

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("问题反馈",
                    FeedbackFrag::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }

    override fun getLayoutId(): Int = R.layout.feedback_fragment

    private lateinit var loadingStatusView: LoadingStatusView
    private lateinit var mAdapter: FeedbackAdapter
    private var page = 1

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        initView()
    }

    private fun initView() {
        addAction(Constant.Event.FEED_SUCCESS)
        loadingStatusView = findViewById(R.id.loadingStatusView)
        loadingStatusView.apply {
            setOnBtnClickListener {
                loadingStatusView.setStatus(LoadingStatusView.Status.LOADING)
                refreshData()
            }
        }

        mAdapter = FeedbackAdapter().apply {
            addHeaderView(View.inflate(activity, R.layout.feedback_header, null))
            this.setOnItemClickListener { adapter, view, position ->
                val data = mAdapter.data[position]
                if (data != null) {
                    FeedbackDetailFrag.start(activity!!, data)
                }
            }
        }

        mAdapter.setOnLoadMoreListener({
            refreshData()
        }, feed_recyclerView)

        feed_recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
            adapter = mAdapter
        }

        rl_feedback.clickWithTrigger(1000) {
            MyFeedbackFrag.start(activity)
        }

        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.GET_FEEDBACK_LIST -> {


                    refresh_layout.finishRefresh(true)

                    if (it.successful) {
                        val data = it.data as (ResponseDataArray<FeedbackEntity>)

                        if (page == 1) {
                            feed_recyclerView.visibility = View.VISIBLE
                            loadingStatusView.visibility = View.GONE
                            mAdapter.setNewData(data.dataList)
                        } else {
                            feed_recyclerView.visibility = View.VISIBLE
                            loadingStatusView.visibility = View.GONE
                            mAdapter.addData(data.dataList)
                        }

                        if (data.hasMore()) {
                            page++
                            mAdapter.loadMoreComplete() //刷新成功
                        } else {
                            mAdapter.loadMoreEnd(false)
                        }


                        if (page == 1 && data.dataList.isEmpty()) { //第一页 无数据
                            feed_recyclerView.visibility = View.GONE
                            loadingStatusView.setStatus(LoadingStatusView.Status.EMPTY)
                            loadingStatusView.tv_text.text = "您还没有反馈记录哦～"
                            loadingStatusView.tv_text.setTextColor(Color.parseColor("#FFB5B2B1"))
                            loadingStatusView.tv_text.textSize = 12F
                        }

                    } else {
                        when {
                            page > 1 -> { //加载下一页数据失败
                                mAdapter.loadMoreFail()
                            }
                            mAdapter.data.isEmpty() -> { //第一页  无数据
                                mAdapter.setNewData(null)
                                feed_recyclerView.visibility = View.GONE
                                loadingStatusView.setStatus(LoadingStatusView.Status.FAIL)
                            }
                            else -> { //下拉刷新失败
                                ToastUtil.show(it.message)
                            }
                        }
                    }

                }
            }
        })

        refresh_layout.setOnRefreshListener {
            page = 1
            refreshData()
        }

        feed_recyclerView.visibility = View.GONE
        loadingStatusView.setStatus(LoadingStatusView.Status.LOADING)
        refreshData()
    }

    /**
     * 刷新数据
     */
    private fun refreshData() {
        userViewModel.getFeedbackList(page)
    }

    /**
     * 展示null数据
     */
    private fun showEmptyView() {
        mAdapter.setNewData(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(pushEvent: PushEvent) {
        if (TextUtils.equals(pushEvent.action, Constant.Event.FEED_SUCCESS)) {
            feed_recyclerView.visibility = View.GONE
            loadingStatusView.setStatus(LoadingStatusView.Status.LOADING)
            page = 1
            refreshData()
        }
    }


}