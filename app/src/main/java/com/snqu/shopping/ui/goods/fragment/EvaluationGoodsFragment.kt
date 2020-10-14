package com.snqu.shopping.ui.goods.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.goods.entity.RateBase
import com.snqu.shopping.data.goods.entity.RateBaseCountEntity
import com.snqu.shopping.ui.goods.adapter.EvaluationGoodsAdapter
import com.snqu.shopping.ui.goods.helper.RecodeHelper
import com.snqu.shopping.ui.goods.vm.GoodsViewModel
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.evaluation_goods_fragment.*

/**
 * desc:评价Fragment
 * time: 2019/8/23
 * @author 银进
 */
class EvaluationGoodsFragment : SimpleFrag() {
    private var index = 1
    private val evaluationGoodsAdapter by lazy {
        EvaluationGoodsAdapter().apply {
            setOnLoadMoreListener({
                loadMoreData()
            }, recycler_view)
            setOnItemChildClickListener { adapter, view, position ->
                val rateBase = data[position] as RateBase
//                RecodeHelper.detailBannerSource.clear()
//                if (!(rateBase.images.isNullOrEmpty())) {
//                    rateBase.images.forEach { img ->
//                        index++
//                        RecodeHelper.detailBannerSource[index] = img
//                    }
//                }
//                VideoImageDetailActivity.start(activity)
            }
        }
    }
    private val loadingStatusView by lazy {
        LoadingStatusView(activity!!).apply {
            setOnBtnClickListener {
                refreshData()
            }
        }
    }

    private val goodsViewModel by lazy {
        ViewModelProviders.of(this).get(GoodsViewModel::class.java)
    }
    private val row = 20
    private var page = 1
    private var type:Int = 0
    private val goodsId by lazy {
        arguments?.getString(EXTRA_GOODS_ID)?:""
    }
    private val itemSource by lazy {
        arguments?.getString(EXTRA_GOODS_ITEM_SOURCE)?:""
    }
    override fun getLayoutId()= R.layout.evaluation_goods_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        goodsViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.GOODS_RATES->{
                    if (page == 1) {

                        //第一次加載
                        if (it.successful) {
                            refresh_layout.finishRefresh(true)
                            val data = it.data as ArrayList<RateBase>
                            if (data.isNotEmpty()) {
                                showNormalView(data)
                                if (data.size < row) {
                                    evaluationGoodsAdapter.loadMoreEnd(false)
                                } else {
                                    evaluationGoodsAdapter.loadMoreComplete()
                                    page++
                                }

                            } else {
                                showEmpty()
                            }
                        } else {
                            refresh_layout.finishRefresh(false)
                            showErrorView()
                        }
                    } else {
                        //加載更多
                        if (it.successful) {
                            val data = it.data as ArrayList<RateBase>
                            if (data.isNotEmpty()) {
                                addData(data)
                                if (data.size < row) {
                                    evaluationGoodsAdapter.loadMoreEnd(false)
                                } else {
                                    evaluationGoodsAdapter.loadMoreComplete()
                                    page++
                                }
                            } else {
                                evaluationGoodsAdapter.loadMoreEnd(false)
                            }
                        } else {
                            evaluationGoodsAdapter.loadMoreFail()
                        }
                    }
                }
                ApiHost.GOODS_RATES_COUNT->{
                    if (it.successful&&it.data!=null) {
                        val data = it.data as RateBaseCountEntity
                        tv_evaluation_all.text="全部(${RecodeHelper.itemFeedCount})"
                        tv_evaluation_add.text="追评(${data.feed_count?:"0"})"
                        tv_evaluation_pic.text="有图(${data.has_image_count?:"0"})"
                    }
                }
            }
        })
        initView()
        refreshData()
    }
    private fun refreshData() {
        page=1
        goodsViewModel.doGoodsRates(goodsId,type,row, page,itemSource)
        goodsViewModel.doGoodsRatesCount(goodsId,itemSource)
    }
    private fun loadMoreData() {
        goodsViewModel.doGoodsRates(goodsId,type,row, page,itemSource)
    }
    /**
     * 初始化View
     */
    private fun initView() {
        refresh_layout.setOnRefreshListener {
            refreshData()
        }
        tv_evaluation_all.isSelected = true
        tv_evaluation_all.onClick {
            if (type != 0) {
                type = 0
                refreshData()
                tv_evaluation_all.isSelected = true
                tv_evaluation_add.isSelected = false
                tv_evaluation_pic.isSelected = false
            }

        }
        tv_evaluation_add.onClick {
            if (type != 1) {
                type = 1
                refreshData()
                tv_evaluation_all.isSelected = false
                tv_evaluation_add.isSelected = true
                tv_evaluation_pic.isSelected = false
            }

        }
        tv_evaluation_pic.onClick {
            if (type != 2) {
                type = 2
                refreshData()
                tv_evaluation_all.isSelected = false
                tv_evaluation_add.isSelected = false
                tv_evaluation_pic.isSelected = true
            }
        }
        recycler_view.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
            adapter = evaluationGoodsAdapter
        }
    }

    /**
     * 展示null数据
     */
    private fun showEmpty() {
        evaluationGoodsAdapter.setNewData(null)
        loadingStatusView.setStatus(LoadingStatusView.Status.EMPTY.apply {
            text = "竟然一条评论都没有"
            iconRes = R.drawable.icon_no_evaluation
        })
        evaluationGoodsAdapter.emptyView = loadingStatusView
    }

    /**
     * 展示正常返回的数据数据
     */
    private fun showNormalView(data: MutableList<RateBase>) {
        evaluationGoodsAdapter.setNewData(data)
    }

    /**
     * 展示正常返回的数据数据
     */
    private fun addData(data: MutableList<RateBase>) {
        evaluationGoodsAdapter.addData(data)
    }

    /**
     * 展示网络或者请求错误数据
     */
    private fun showErrorView() {
        evaluationGoodsAdapter.setNewData(null)
        evaluationGoodsAdapter.emptyView= loadingStatusView.apply {
            setStatus(LoadingStatusView.Status.FAIL)
        }
    }
    companion object {
        fun start(context: Context?,goods_id:String,item_source:String) {
            val fragParam = SimpleFragAct.SimpleFragParam("全部评价",
                    EvaluationGoodsFragment::class.java,Bundle().apply {
                putString(EXTRA_GOODS_ID,goods_id)
                putString(EXTRA_GOODS_ITEM_SOURCE,item_source)
            })
            SimpleFragAct.start(context, fragParam)
        }
        const val EXTRA_GOODS_ID="EXTRA_GOODS_ID"
        const val EXTRA_GOODS_ITEM_SOURCE="EXTRA_GOODS_ITEM_SOURCE"
    }
}