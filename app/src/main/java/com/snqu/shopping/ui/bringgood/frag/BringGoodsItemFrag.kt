package com.snqu.shopping.ui.bringgood.frag

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.base.ResponseDataArray
import com.snqu.shopping.data.bringgoods.BringGoodsItemBean
import com.snqu.shopping.ui.bringgood.adapter.BringGoodsItemAdapter
import com.snqu.shopping.ui.main.view.CommonLoadingMoreWhiteView
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel
import com.snqu.shopping.util.RecycleViewScrollToTop
import kotlinx.android.synthetic.main.home_bring_goods_item_frag.*

class BringGoodsItemFrag(@SuppressLint("ValidFragment") val cid: String) : SimpleFrag() {

    private lateinit var mHomeViewModel: HomeViewModel
    private lateinit var mAdapter: BringGoodsItemAdapter
    private var page = 1
    private val row = 20

    override fun init(savedInstanceState: Bundle?) {
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        mAdapter = BringGoodsItemAdapter()

        val gridLayoutManager = androidx.recyclerview.widget.StaggeredGridLayoutManager(2, androidx.recyclerview.widget.GridLayoutManager.VERTICAL)
        list_view.layoutManager = gridLayoutManager
        list_view.adapter = mAdapter
        RecycleViewScrollToTop.addScroolToTop(list_view, findViewById(R.id.scroll_to_top), gridLayoutManager)

        val loadingView: LoadMoreView = CommonLoadingMoreWhiteView()
        mAdapter.setLoadMoreView(loadingView)
        mAdapter.setOnLoadMoreListener({
            loadingData()
        }, list_view)

        mAdapter.setOnItemClickListener { adapter, view, position ->
            BringVideoAct.start(activity, adapter.data as MutableList<BringGoodsItemBean>?, cid, position, page, loadingView.loadMoreStatus == LoadMoreView.STATUS_END)
        }

        refresh_layout.setOnRefreshListener {
            page = 1
            mHomeViewModel.getDydhList(cid, page)
        }

        loadingStatusView.setOnBtnClickListener {
            container.visibility = View.GONE
            loadingStatusView.setStatus(LoadingStatusView.Status.LOADING)
            loadingData()
        }

        initData()
    }

    private fun initData() {
        mHomeViewModel.mNetReqResultLiveData.observe(lifecycleOwner, Observer {
            when (it?.tag) {
                ApiHost.DYDH_LIST -> {

                    refresh_layout.finishRefresh(true)

                    if (it.successful) {
                        val data = it.data as (ResponseDataArray<BringGoodsItemBean>)
                        if (page == 1) {
                            container.visibility = View.VISIBLE
                            loadingStatusView.visibility = View.GONE
                            mAdapter.setNewData(data.dataList)
                        } else {
                            container.visibility = View.VISIBLE
                            loadingStatusView.visibility = View.GONE
                            mAdapter.addData(data.dataList)
                        }

                        if (data.hasMore()) {
                            page++
                            mAdapter.loadMoreComplete() //刷新成功
                        } else {
                            mAdapter.loadMoreEnd(false)
//                            mAdapter.loadMoreEnd(page == 1) //无下一页
                        }

                        if (page == 1 && data.dataList.isEmpty()) { //第一页 无数据
                            container.visibility = View.GONE
                            loadingStatusView.setStatus(LoadingStatusView.Status.EMPTY)
                            loadingStatusView.setText("暂无数据")
                        }
                    } else {
                        when {
                            page > 1 -> { //加载下一页数据失败
                                mAdapter.loadMoreFail()
                            }
                            mAdapter.data.isEmpty() -> { //第一页  无数据
                                mAdapter.setNewData(null)
                                container.visibility = View.GONE
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
        container.visibility = View.GONE
        loadingStatusView.setStatus(LoadingStatusView.Status.LOADING)
        loadingData()
    }

    private fun loadingData() {
        mHomeViewModel.getDydhList(cid, page)
    }

    override fun getLayoutId(): Int {
        return R.layout.home_bring_goods_item_frag
    }

    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("抖音带货",
                    BringGoodsFrag::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }


}