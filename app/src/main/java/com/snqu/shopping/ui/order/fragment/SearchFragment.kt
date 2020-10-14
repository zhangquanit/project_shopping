package com.snqu.shopping.ui.order.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.order.OrderClient
import com.snqu.shopping.data.order.entity.OrderEntity
import com.snqu.shopping.ui.order.adapter.SelfOrderItemAdapter
import com.snqu.shopping.ui.order.dialog.ShareRuleDialog
import com.snqu.shopping.ui.order.vm.OrderViewModel
import com.snqu.shopping.util.OrderUtil
import com.snqu.shopping.util.ext.onClick
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.search_fragment.*
import org.greenrobot.eventbus.EventBus
import java.util.*


/**
 * desc:
 * time: 2019/8/19
 * @author 银进
 */
class SearchFragment : SimpleFrag() {
    private val orderViewModel by lazy {
        ViewModelProviders.of(this).get(OrderViewModel::class.java)
    }
    private val selfOrderItemAdapter by lazy {
        SelfOrderItemAdapter(true).apply {
            setOnItemChildClickListener { adapter, view, position ->
                ShareRuleDialog().apply {
                    arguments = Bundle().apply {
                        val orderEntity = adapter.data[position] as OrderEntity
                        putString(ShareRuleDialog.EXTRA_ORDER_ID, orderEntity._id)
                    }
                }.show(childFragmentManager, "ShareRuleDialog")
            }
            setOnItemClickListener { adapter, view, position ->
                val orderEntity = adapter.data[position] as OrderEntity
                OrderUtil.jumpToGoodsDetail(activity!!, orderEntity)
            }
            setOnLoadMoreListener({
                loadMoreData()
            }, recycler_view)
        }
    }
    private val row = 20
    private var page = 1
    private var keyword: String = ""
    private val loadingStatusView by lazy {
        LoadingStatusView(activity!!).apply {
            setOnBtnClickListener {
                refreshData()
            }
        }
    }

    override fun getLayoutId() = R.layout.search_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true)
        titleBar.visibility = View.GONE
        orderViewModel.dataResult.observe(this, Observer {
            if (it?.tag == ApiHost.ORDER_LIST) {
                if (page == 1) {
                    //第一次加載
                    if (it.successful) {
                        val data = it.data as ArrayList<OrderEntity>
                        if (data.isNotEmpty()) {
                            showNormalView(data)
                            if (data.size < row) {
                                selfOrderItemAdapter.loadMoreEnd(false)
                            } else {
                                selfOrderItemAdapter.loadMoreComplete()
                                page++
                            }
                        } else {
                            showEmptyView()
                        }
                    } else {
                        showErrorView()
                    }
                } else {
                    //加載更多
                    if (it.successful) {
                        val data = it.data as ArrayList<OrderEntity>
                        if (data.isNotEmpty()) {
                            addData(data)
                            if (data.size < row) {
                                selfOrderItemAdapter.loadMoreEnd(false)
                            } else {
                                selfOrderItemAdapter.loadMoreComplete()
                                page++
                            }
                        } else {
                            selfOrderItemAdapter.loadMoreEnd(false)
                        }
                    } else {
                        selfOrderItemAdapter.loadMoreFail()
                    }
                }

            }

        })
        initView()
    }

    private fun initView() {

        setTagAdapter()

        recycler_view.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
            adapter = selfOrderItemAdapter
        }
        img_delete_all.onClick {
            OrderClient.clearSearchHistory()
            setTagAdapter()
        }

        tag_flow.setOnTagClickListener { view, position, parent ->
            val item = tag_flow.adapter.getItem(position) as String
            loadingStatusView.setStatus(LoadingStatusView.Status.LOADING)
            EventBus.getDefault().post(PushEvent(Constant.Event.ORDER_SEARCH, item))
            search(item)
            true
        }
    }

    fun hide() {
        recycler_view.visibility = View.GONE
        ll_history.visibility = View.VISIBLE
    }


    fun search(key: String) {
        selfOrderItemAdapter.setNewData(null)
        keyword = key
        recycler_view.visibility = View.VISIBLE
        ll_history.visibility = View.GONE
        OrderClient.addSearchHistory(key)
        refreshData()
        setTagAdapter()
    }

    private fun refreshData() {
        loadingStatusView.setStatus(LoadingStatusView.Status.LOADING)
        selfOrderItemAdapter.setEnableLoadMore(true)
        page = 1
        orderViewModel.doOrderList(null, 0, row, page, null, keyword, null)
    }

    private fun loadMoreData() {
        selfOrderItemAdapter.setEnableLoadMore(true)
        orderViewModel.doOrderList(null, 0, row, page, null, keyword, null)
    }

    fun setTagAdapter() {
        val searchHistory = OrderClient.getSearchHistory()
        if (searchHistory.isEmpty()) {
            img_delete_all.visibility = View.GONE
        } else {
            img_delete_all.visibility = View.VISIBLE
        }
        tag_flow.adapter = object : TagAdapter<String>(searchHistory) {
            override fun getView(parent: FlowLayout?, position: Int, t: String?): View {
                val tvContent = layoutInflater.inflate(R.layout.search_history_flow_item, null) as TextView
                tvContent.text = t ?: ""
                return tvContent
            }
        }
    }


    /**
     * 展示正常返回的数据数据
     */
    private fun addData(data: MutableList<OrderEntity>) {
        selfOrderItemAdapter.addData(data)
    }

    /**
     * 展示正常返回的数据数据
     */
    private fun showNormalView(data: MutableList<OrderEntity>) {
        recycler_view.visibility = View.VISIBLE
        ll_history.visibility = View.GONE
        selfOrderItemAdapter.setNewData(data)
    }

    /**
     * 展示null数据
     */
    private fun showEmptyView() {
        recycler_view.visibility = View.VISIBLE
        ll_history.visibility = View.GONE
        selfOrderItemAdapter.setNewData(null)
        selfOrderItemAdapter.emptyView = loadingStatusView.apply {
            setStatus(LoadingStatusView.Status.EMPTY.apply {
                text = "暂无搜索结果～"
                iconRes = R.drawable.icon_no_evaluation
            })
        }
    }

    /**
     * 展示网络或者请求错误数据
     */
    private fun showErrorView() {
        recycler_view.visibility = View.VISIBLE
        ll_history.visibility = View.GONE
        selfOrderItemAdapter.setNewData(null)
        selfOrderItemAdapter.emptyView = loadingStatusView.apply {
            setStatus(LoadingStatusView.Status.FAIL.apply {
                text = "数据错误～"
                iconRes = R.drawable.icon_fail
            })
        }
    }

    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("",
                    SearchFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }
}