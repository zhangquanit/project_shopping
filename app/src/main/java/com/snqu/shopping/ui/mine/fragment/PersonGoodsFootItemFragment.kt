package com.snqu.shopping.ui.mine.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.BaseFragment
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.goods.entity.GoodsEntity
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.ui.goods.GoodsDetailActivity
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.mine.adapter.PersonGoodsItemAdapter
import com.snqu.shopping.util.statistics.SndoData
import kotlinx.android.synthetic.main.person_goods_item_fragment.*

/**
 * desc:
 * time: 2019/8/13
 * @author 银进
 */
class PersonGoodsFootItemFragment : BaseFragment() {
    private var userViewModel: UserViewModel? = null
    private val loadingStatusView by lazy {
        LoadingStatusView(activity!!).apply {
            setOnBtnClickListener {
                refreshData()
            }
        }
    }
    private val row = 30
    private var page = 1
    private val personGoodsItemAdapter by lazy {
        PersonGoodsItemAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                GoodsDetailActivity.start(activity, data[position]._id, data[position].item_source, data[position])
                var goodsEntity = data[position]
                SndoData.event(SndoData.XLT_EVENT_USER_CATEGORY,
                        SndoData.XLT_GOOD_ID, goodsEntity.goods_id,
                        "xlt_item_firstcate_title", "null",
                        "xlt_item_thirdcate_title", "null",
                        "xlt_item_secondcate_title", "null",
                        "good_name", goodsEntity.item_title,
                        SndoData.XLT_ITEM_PLACE, (position + 1).toString(),
                        SndoData.XLT_ITEM_SOURCE, goodsEntity.item_source
                )
            }
            //我的足迹暂不做分页
//            setOnLoadMoreListener({
//                loadMoreData()
//            }, recycler_view)
//            setLoadMoreView(CommonLoadingMoreView())
        }
    }

    override fun getLayoutId() = R.layout.person_goods_item_fragment

    override fun init(savedInstanceState: Bundle?) {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        userViewModel?.dataResult?.observe(this, Observer {
            if (it?.tag == ApiHost.SELF_FOOT_GOODS) {
//                if (page == 1) {
                //第一次加載
                if (it.successful) {
                    val data = it.data as ArrayList<GoodsEntity>
                    if (data.isNotEmpty()) {
                        showNormalView(data)
//                            if (data.size < row) {
//                                personGoodsItemAdapter.loadMoreEnd(false)
//                            } else {
//                                personGoodsItemAdapter.loadMoreComplete()
//                                page++
//                            }
                    } else {
                        showEmptyView()
                    }
                } else {
                    showErrorView()
                }
//                }
//                else {
//                    //加載更多
//                    if (it.successful) {
//                        val data = it.data as ArrayList<GoodsEntity>
//                        if (data.isNotEmpty()) {
//                            addData(data)
//                            if (data.size < row) {
//                                personGoodsItemAdapter.loadMoreEnd(false)
//                            } else {
//                                personGoodsItemAdapter.loadMoreComplete()
//                                page++
//                            }
//                        } else {
//                            personGoodsItemAdapter.loadMoreEnd(false)
//                        }
//                    } else {
//                        personGoodsItemAdapter.loadMoreFail()
//                    }
//                }
            }
        })
        recycler_view.layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 2)
        recycler_view.adapter = personGoodsItemAdapter
        refreshData()
    }

    fun refreshData() {
        if (!UserClient.isLogin()) {
            return
        }
        page = 1
        userViewModel?.doSelfFootGoods(page, row)
    }

    private fun loadMoreData() {
        userViewModel?.doSelfFootGoods(page, row)
    }

    /**
     * 展示正常返回的数据数据
     */
    private fun showNormalView(data: MutableList<GoodsEntity>) {
        personGoodsItemAdapter.setNewData(data)
    }

    /**
     * 展示正常返回的数据数据
     */
    private fun addData(data: MutableList<GoodsEntity>) {
        personGoodsItemAdapter.addData(data)
    }

    /**
     * 展示null数据
     */
    private fun showEmptyView() {
        personGoodsItemAdapter.setNewData(null)
        personGoodsItemAdapter.emptyView = loadingStatusView.apply {
            setStatus(LoadingStatusView.Status.EMPTY.apply {
                text = "暂无预览商品～"
                iconRes = R.drawable.empty_icon
            })
        }
    }

    /**
     * 展示网络或者请求错误数据
     */
    private fun showErrorView() {
        personGoodsItemAdapter.setNewData(null)
        personGoodsItemAdapter.emptyView = loadingStatusView.apply {
            setStatus(LoadingStatusView.Status.FAIL)
        }
    }
}