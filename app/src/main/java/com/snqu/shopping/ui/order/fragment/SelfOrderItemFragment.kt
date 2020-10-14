package com.snqu.shopping.ui.order.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.BaseFragment
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.order.entity.OrderEntity
import com.snqu.shopping.ui.order.adapter.SelfOrderItemAdapter
import com.snqu.shopping.ui.order.vm.OrderViewModel
import com.snqu.shopping.util.OrderUtil
import kotlinx.android.synthetic.main.self_order_item_fragment.*
import java.util.*

/**
 * desc:item_source C淘宝 B天猫 D京东
 * time: 2019/8/16
 * @author 银进
 */
class SelfOrderItemFragment : BaseFragment() {
    private val selfIndex by lazy {
        arguments?.getInt(EXTRA_ITEM_INDEX) ?: 0
    }
    private var orderViewModel: OrderViewModel? = null
        get() {
            if (field == null) {
                if (activity != null) {
                    field = ViewModelProviders.of(activity!!).get(OrderViewModel::class.java)
                } else {
                    return null
                }
            }
            return field
        }
    private var loadingStatusView: LoadingStatusView? = null
    private var selfOrderItemAdapter: SelfOrderItemAdapter? = null

    private val row = 20
    private var page = 1

    override fun getLayoutId() = R.layout.self_order_item_fragment

    override fun init(savedInstanceState: Bundle?) {
        orderViewModel?.orderItemSource?.observe(this, Observer {
            refreshData()
        })

        orderViewModel?.orderMonth?.observe(this, Observer {
            refreshData()
        })
        orderViewModel?.dataResult?.observe(this, Observer {
            if (it?.tag == ApiHost.ORDER_LIST && selfIndex == it.extra) {
                if (page == 1) {
                    //第一次加載
                    if (it.successful) {
                        val data = it.data as ArrayList<OrderEntity>
                        if (data.isNotEmpty()) {
                            showNormalView(data)
                            if (data.size < row) {
                                selfOrderItemAdapter?.loadMoreEnd(false)
                            } else {
                                selfOrderItemAdapter?.loadMoreComplete()
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
                                selfOrderItemAdapter?.loadMoreEnd(false)
                            } else {
                                selfOrderItemAdapter?.loadMoreComplete()
                                page++
                            }
                        } else {
                            selfOrderItemAdapter?.loadMoreEnd(false)
                        }
                    } else {
                        selfOrderItemAdapter?.loadMoreFail()
                    }
                }

            }

        })
        initView()
    }

    fun refreshData() {
        if (null != selfOrderItemAdapter) {
            loadingStatusView?.post {
                loadingStatusView!!.setStatus(LoadingStatusView.Status.LOADING)
            }
            selfOrderItemAdapter?.setNewData(null)
            selfOrderItemAdapter?.setEnableLoadMore(true)
            page = 1
            orderViewModel?.doOrderList(orderViewModel?.orderItemSource?.value, selfIndex, row, page, orderViewModel?.orderMonth?.value, null, null)
        }
    }

    private fun loadMoreData() {
        loadingStatusView?.post {
            loadingStatusView!!.setStatus(LoadingStatusView.Status.LOADING)
        }
        orderViewModel?.doOrderList(orderViewModel?.orderItemSource?.value, selfIndex, row, page, orderViewModel?.orderMonth?.value, null, null)
    }

    /**
     * 初始化View
     */
    private fun initView() {
        loadingStatusView = LoadingStatusView(activity!!).apply {
            setOnBtnClickListener {
                refreshData()
            }
        }

        selfOrderItemAdapter = SelfOrderItemAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                val orderEntity = adapter.data[position] as OrderEntity
                OrderUtil.jumpToGoodsDetail(activity!!, orderEntity)
            }
            setOnLoadMoreListener({
                loadMoreData()
            }, recycler_view)
            emptyView = loadingStatusView?.apply {
                setStatus(LoadingStatusView.Status.LOADING)
            }
        }
        recycler_view.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
            adapter = selfOrderItemAdapter
        }
    }

    /**
     * 展示正常返回的数据数据
     */
    private fun showNormalView(data: MutableList<OrderEntity>) {
        selfOrderItemAdapter?.setNewData(data)
    }

    /**
     * 展示正常返回的数据数据
     */
    private fun addData(data: MutableList<OrderEntity>) {
        selfOrderItemAdapter?.addData(data)
    }

    /**
     * 展示null数据
     */
    private fun showEmptyView() {
        selfOrderItemAdapter?.setNewData(null)
        selfOrderItemAdapter?.emptyView = loadingStatusView?.apply {
            setStatus(LoadingStatusView.Status.EMPTY.apply {
                text = "您还没有订单记录哦～"
                iconRes = R.drawable.empty_order_icon
            })
        }
    }

    /**
     * 展示网络或者请求错误数据
     */
    private fun showErrorView() {
        selfOrderItemAdapter?.setNewData(null)
        selfOrderItemAdapter?.emptyView = loadingStatusView?.apply {
            setStatus(LoadingStatusView.Status.FAIL)
        }
    }

    companion object {
        const val EXTRA_ITEM_INDEX = "EXTRA_ITEM_INDEX"
    }
}