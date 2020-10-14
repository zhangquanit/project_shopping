package com.snqu.shopping.ui.goods.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.date.DateFormatUtil
import com.android.util.ext.ToastUtil
import com.android.util.os.DeviceUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter.RequestLoadMoreListener
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.goods.entity.GoodRecmEntity
import com.snqu.shopping.data.goods.entity.GoodRecmInfoEntity
import com.snqu.shopping.data.goods.entity.GoodsQueryParam
import com.snqu.shopping.data.home.entity.CommunityEntity
import com.snqu.shopping.ui.goods.adapter.GoodsMyRecommendListAdapter
import com.snqu.shopping.ui.goods.dialog.DelGoodRecmDialog
import com.snqu.shopping.ui.goods.dialog.GoodTipDialog
import com.snqu.shopping.ui.goods.vm.GoodsViewModel
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView
import com.snqu.shopping.ui.main.view.FansStatusView.FansLoadingStatus
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.NumberUtil
import com.snqu.shopping.util.RecycleViewScrollToTop
import com.snqu.shopping.util.ext.onClick
import common.widget.dialog.EffectDialogBuilder
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.fans_empty_layout.view.*
import kotlinx.android.synthetic.main.my_good_recm.*
import kotlinx.android.synthetic.main.my_good_recm_header.*
import kotlinx.android.synthetic.main.my_good_recm_header.view.*
import java.util.*

/**
 * 我的推荐
 */
class GoodRecmMySelfFrag : SimpleFrag() {

    private val queryParam = GoodsQueryParam()
    private var sort = GoodsQueryParam.Sort.NONE
    private lateinit var mAdapter: GoodsMyRecommendListAdapter
    private var d5: Int = 0
    private var normolColor = 0
    private var selColor: Int = 0
    private var selIndex = 0
    private var currentIndex = -1
    private var yearMonth: String = DateFormatUtil.yyyy_MM_Two().format(Date())
    private var selectDate: Date = Date()
    private var mLoadingDialog: LoadingDialog? = null
    private var delPos = -1
    private var communityEntity: CommunityEntity? = null
    var goodRecmInfoEntity: GoodRecmInfoEntity? = null

    //请求的ViewModel
    private val goodsViewModel by lazy {
        ViewModelProviders.of(this).get(GoodsViewModel::class.java)
    }

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.my_good_recm_header, null)
    }

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("我的推荐",
                    GoodRecmMySelfFrag::class.java)
            fragParam.mutliPage = false
            SimpleFragAct.start(context, fragParam)
        }
    }


    override fun getLayoutId(): Int = R.layout.my_good_recm

    override fun init(savedInstanceState: Bundle?) {
        setTheme()
        initUi()
        initValue()
    }

    private fun setTheme() {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        titleBar.mTitleTextView.textSize = 19F
        titleBar.mTitleTextView.setTextColor(Color.parseColor("#333333"))
        titleBar.setRightText("奖励记录")
        titleBar.mRightTxtView.onClick {
            GoodRecommRewardListFrag.start(mContext)
        }
        titleBar.mRightTxtView.textSize = 14F
        titleBar.mRightTxtView.setTextColor(Color.parseColor("#333333"))
    }

    private fun initUi() {

        d5 = DeviceUtil.dip2px(activity, 5f)
        normolColor = Color.parseColor("#000000")
        selColor = Color.parseColor("#FF8202")

        headerView.apply {
            tv_order_count.onClick {
                itemClick(tv_order_count.id)
            }
            tv_jiangli.onClick {
                itemClick(tv_jiangli.id)
            }
            tv_filter.onClick {
                itemClick(tv_filter.id)
            }
            recm_tip.onClick {
                val webViewParam = WebViewFrag.WebViewParam()
                webViewParam.url = Constant.WebPage.REWARD_RULE
                WebViewFrag.start(mContext, webViewParam)
            }
        }

        mAdapter = GoodsMyRecommendListAdapter(this).apply {
            addHeaderView(headerView)
        }
        mAdapter.setHeaderAndEmpty(true)
        good_listview.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
            adapter = mAdapter
        }
        RecycleViewScrollToTop.addScroolToTop(good_listview, findViewById(R.id.scroll_to_top))
        mAdapter.setLoadMoreView(CommonLoadingMoreView())
        mAdapter.setOnLoadMoreListener(RequestLoadMoreListener { loadListData() }, good_listview)
        mAdapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            when (view.id) {
                R.id.item_look -> {
                    if (view.tag != null) {
                        val examine_content = view.tag as String
                        if (!TextUtils.isEmpty(examine_content)) {
                            val goodTipDialog = GoodTipDialog()
                            goodTipDialog.setContent("审核不通过", examine_content)
                            goodTipDialog.show(childFragmentManager, "goodtip")
                        }
                    }
                }
                R.id.item_del -> {
                    val builder = EffectDialogBuilder(activity)
                            .setCancelable(false)
                            .setCancelableOnTouchOutside(false)
                    val dialog = DelGoodRecmDialog()
                    dialog.setClick(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            dialog.dismiss()
                            showLoadingDialog("删除中")
                            delPos = position
                            mAdapter.data[position]._id?.let { it1 -> goodsViewModel.delGoodRecm(it1) }
                        }
                    })
                    dialog.show(childFragmentManager, "del")
                }
                R.id.item_recm -> {
                    communityEntity = mAdapter.data[position]
                    val goodEntity = communityEntity?.goods
                    if (goodEntity != null) {
                        if (!TextUtils.isEmpty(goodEntity.item_source) && !TextUtils.isEmpty(goodEntity._id)) {
                            showLoadingDialog("加载中")
                            goodsViewModel.communityGoodsRecm(goodEntity._id!!, goodEntity.item_source!!)
                        }
                    }
                }
            }
        }

        list_statusView.empty_btn.onClick {
            loadListData()
        }

        refresh.setOnRefreshListener {
            initQueryParam()
            loadData()
        }

        loadingStatusView.apply {
            setOnBtnClickListener {
                if (container != null) {
                    container.visibility = View.GONE
                }
                loadingStatusView.setStatus(LoadingStatusView.Status.LOADING)
                loadData()
            }
        }

    }


    private fun itemClick(id: Int) {
        var drawRes = 0
        when (id) {
            R.id.tv_order_count -> {
                drawRes = getSelDrawRes(0)
                tv_order_count.text = getText("下单量", selColor, drawRes)
                tv_jiangli.text = getText("奖励", normolColor, R.drawable.filter_icon_normal)
                tv_filter.text = getText("筛选", normolColor, R.drawable.filter_icon_screen)
                currentIndex = 0
                queryParam.page = 1
                getSort()
                loadListData()
            }
            R.id.tv_jiangli -> {
                drawRes = getSelDrawRes(1)
                tv_order_count.text = getText("下单量", normolColor, R.drawable.filter_icon_normal)
                tv_jiangli.text = getText("奖励", selColor, drawRes)
                tv_filter.text = getText("筛选", normolColor, R.drawable.filter_icon_screen)
                currentIndex = 1
                queryParam.page = 1
                getSort()
                loadListData()
            }
            R.id.tv_filter -> {
                val endCalendar = Calendar.getInstance()
                val startCalendar = Calendar.getInstance().apply {
                    set(endCalendar.get(Calendar.YEAR) - 2, endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH))
                }
                TimePickerBuilder(activity) { date, v ->
                    selectDate = date
                    yearMonth = DateFormatUtil.yyyy_MM_Two().format(date)
                    queryParam.goodsDate = yearMonth
                    queryParam.page = 1
                    getSort()
                    loadListData()
                }.setType(BooleanArray(6).apply {
                    set(0, true)
                    set(1, true)
                    set(2, false)
                    set(3, false)
                    set(4, false)
                    set(5, false)
                })// 默认全部显示
                        .setCancelText("取消")//取消按钮文字
                        .setSubmitText("确定")//确认按钮文字
                        .setTitleSize(14)//标题文字大小
                        .setTitleText("")//标题文字
                        .setContentTextSize(16)
                        .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                        .isCyclic(false)//是否循环滚动
                        .setRangDate(startCalendar, endCalendar)
                        .setDate(Calendar.getInstance().apply {
                            time = selectDate
                        })
                        .setSubmitColor(Color.parseColor("#25282D"))//确定按钮文字颜色
                        .setCancelColor(Color.parseColor("#C3C4C7"))//取消按钮文字颜色
                        .setTitleBgColor(Color.parseColor("#F6F6F6"))//标题背景颜色 Night mode
                        .setTitleColor(Color.parseColor("#25282D"))
                        .setTitleText("选择日期")
                        .setBgColor(Color.parseColor("#F6F6F6"))//滚轮背景颜色 Night mode
                        .setLabel("年", "月", "日", "时", "分", "秒")//默认设置为年月日时分秒
                        .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                        .isDialog(false)//是否显示为对话框样式
                        .build().show()

            }
        }
    }

    private fun setUserInfo() {
        goodRecmInfoEntity?.let { data ->
            val typeFace = Typeface.createFromAsset(activity?.assets, "fonts/withdrawal_font.ttf")
            headerView.apply {
                tv_name.typeface = typeFace
                left_price.typeface = typeFace
                right_price.typeface = typeFace
                GlideUtil.loadPic(user_icon, data.avatar, R.drawable.icon_default_head, R.drawable.icon_default_head)
                tv_name.text = data.nickname
                if (TextUtils.isEmpty(data.settlement)) {
                    left_price.text = "0.0"
                } else {
                    left_price.text = NumberUtil.saveTwoPoint(data.settlement.toLong())
                }
                if (TextUtils.isEmpty(data.wait_settlement)) {
                    right_price.text = "0.0"
                } else {
                    right_price.text = NumberUtil.saveTwoPoint(data.wait_settlement.toLong())
                }
                if (queryParam.sort == GoodsQueryParam.Sort.NONE) {
                    tv_order_count.text = getText("下单量", normolColor, R.drawable.filter_icon_normal)
                    tv_jiangli.text = getText("奖励", normolColor, R.drawable.filter_icon_normal)
                    tv_filter.text = getText("筛选", normolColor, R.drawable.filter_icon_screen)
                }
            }
        }
    }

    private fun initValue() {
        initQueryParam()

        goodsViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.COMMUNITY_GOODS_RECM -> {
                    closeLoadDialog()
                    if (it.successful) {
                        val goodRecmEntity = it.data as GoodRecmEntity
                        when (goodRecmEntity.share_type) {
                            // 可推荐
                            "1" -> {
                                if (communityEntity != null && communityEntity?.goods != null) {
                                    GoodRecommendFrag.start(activity!!, communityEntity?.goods!!, communityEntity?.images, communityEntity?.images_url, communityEntity?.content, goodRecmEntity.share_advance)
                                }
                            }
                            else -> {
                                showToastShort(it.message)
                            }
                        }
                    } else {
                        showToastShort(it.message)
                    }
                }
                ApiHost.DEL_GOOD_RECM -> {
                    closeLoadDialog()
                    if (it.successful) {
                        mAdapter.remove(delPos)
                    } else {
                        showToastShort(it.message)
                    }
                }
                ApiHost.GET_MY_GOOD_RECM_INFO -> {
                    if (it.successful && it.data != null) {
                        container.visibility = View.VISIBLE
                        loadingStatusView.visibility = View.GONE
                        goodRecmInfoEntity = it.data as GoodRecmInfoEntity
                        setUserInfo()
                    } else {
                        ToastUtil.show(it.message)
                        container.visibility = View.GONE
                        loadingStatusView.setStatus(LoadingStatusView.Status.FAIL)
                    }
                }
                ApiHost.GET_MY_GOOD_RECM_LIST -> {
                    refresh.finishRefresh(true)
                    if (it.successful && it.data != null) {
                        val dataList = it.data as List<CommunityEntity>
                        if (queryParam.page == 1) {
                            mAdapter.setNewData(dataList)
                        } else if (dataList.isNotEmpty()) {
                            mAdapter.addData(dataList)
                        }
                        if (dataList.size >= queryParam.row) {
                            queryParam.page++
                            mAdapter.loadMoreComplete() //刷新成功
                        } else {
                            mAdapter.loadMoreEnd(queryParam.page == 1) //无下一页
                        }
                        if (queryParam.page == 1 && dataList.isEmpty()) { //第一页 无数据
                            val status = FansLoadingStatus.EMPTY
                            list_statusView.setStatus(status)
                            list_statusView.tv_invite.visibility = View.GONE
                            list_statusView.tv_text.text = "您还没有推荐商品哦~"
                        } else {
                            list_statusView.visibility = View.GONE
                        }
                    } else {
                        if (queryParam.page > 1) { //加载下一页数据失败
                            mAdapter.loadMoreFail()
                        } else if (mAdapter.data.isEmpty()) { //第一页  无数据
//                            mAdapter.loadMoreFail()
                            val status = FansLoadingStatus.EMPTY
                            list_statusView.setStatus(status)
                            list_statusView.tv_invite.visibility = View.GONE
                            list_statusView.tv_text.text = "您还没有推荐商品哦~"
//                            list_statusView.onClick {
//                                if(status==FansLoadingStatus.FAIL){
//                                    loadListData()
//                                }
//                            }
                        } else { //下拉刷新失败
                            ToastUtil.show(it.message)
                        }
                    }
                }
            }
        })

        if (container != null) {
            container.visibility = View.GONE
        }
        loadingStatusView.setStatus(LoadingStatusView.Status.LOADING)
        loadData()
    }

    private fun initQueryParam() {
        queryParam.page = 1
        queryParam.row = 10
    }

    private fun loadData() {
        goodsViewModel.getMyGoodRecmInfo()
        loadListData()
    }

    private fun loadListData() {
        goodsViewModel.getMyGoodRecmList(queryParam)
    }

    private fun getText(text: String, textColor: Int, drawabeRes: Int): SpannableStringBuilder? {
        val spanUtils = SpanUtils()
                .append(text).setForegroundColor(textColor)
        if (drawabeRes != -1) {
            spanUtils.appendSpace(d5).appendImage(drawabeRes, SpanUtils.ALIGN_CENTER)
        }
        return spanUtils.create()
    }

    private fun getSelDrawRes(pos: Int): Int {
        if (currentIndex != pos) {
            selIndex = 1
        } else {
            selIndex++
            if (selIndex > 2) {
                selIndex = 1
            }
        }
        return if (pos == 0) { //下单量
            if (selIndex == 1) R.drawable.filter_icon_down else R.drawable.filter_icon_up
        } else if (pos == 1) { // 销量
            if (selIndex == 1) R.drawable.filter_icon_down else R.drawable.filter_icon_up
        } else { //筛选
            return R.drawable.filter_icon_screen
        }
    }

    fun getSort() {
        sort = when (currentIndex) {
            0 -> {
                if (selIndex == 1) GoodsQueryParam.Sort.ORDER_DOWN else GoodsQueryParam.Sort.ORDER_UP

            }
            1 -> {
                if (selIndex == 1) GoodsQueryParam.Sort.REWARD_DOWN else GoodsQueryParam.Sort.REWARD_UP
            }
            else -> {
                GoodsQueryParam.Sort.NONE
            }
        }
        queryParam.sort = sort
    }

    fun showLoadingDialog(content: String?) {
        mLoadingDialog = LoadingDialog.showDialog(activity, content)
        mLoadingDialog?.setCancelable(false)
        mLoadingDialog?.setCancelableOnTouchOutside(false)
    }

    fun closeLoadDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog?.dismiss()
        }
    }


}