package com.snqu.shopping.ui.mine.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.TextPaint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.blankj.utilcode.util.SpanUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.common.ui.AlertDialogView
import com.snqu.shopping.common.ui.BottomInDialog
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.base.ResponseDataArray
import com.snqu.shopping.data.user.entity.TutorShareContract
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.ui.mine.adapter.MeTutorShareAdapter
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.statistics.UmengAnalysisUtil
import common.widget.dialog.EffectDialogBuilder
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.me_tutor_share_bottom_dialog.view.*
import kotlinx.android.synthetic.main.me_tutor_share_page_frag.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MeTutorPageFrag : SimpleFrag() {

    override fun getLayoutId(): Int = R.layout.me_tutor_share_page_frag

    private lateinit var mAdapter: MeTutorShareAdapter

//    private var movePos = -1 // 移动的item
//
//    private var nextPos = -1 // 移动后的item

    private var page = 1     // 当前页

//    private var changePos = -1 //当前进行操作的item

    private var isJumpWeb = false

    private val EXTRA_STATUS = "status"

    private var status = ""

    fun setParam(status: String): Bundle? {
        val bundle = Bundle()
        bundle.putString(EXTRA_STATUS, status)
        return bundle
    }

    //进入页面的加载loading
    private val loadingDialog by lazy {
        LoadingDialog(mContext, "数据加载中", true)
    }

    /**
     * 用户请求接口
     */
    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    override fun init(savedInstanceState: Bundle?) {

        addAction(Constant.Event.TUTOR_SHARE_REFRESH)

        initView()

        userViewModel.dataResult.observe(this, Observer {
            when (it.tag) {
                //置顶接口回调
                ApiHost.TUTOR_SHARE_TOP -> {

                    if (it.successful) {
                        when (it.message) {
                            "0" -> {
                                ToastUtil.show("取消置顶成功")
                            }
                            "1" -> {
                                ToastUtil.show("置顶成功")
                            }
                        }
                        page = 1
                        refreshData()
                    } else {
                        if (loadingDialog.isShowing) {
                            loadingDialog.dismiss()
                        }
                        ToastUtil.show(it.message)
                    }
                }
                ApiHost.TUTOR_SHARE_SET_STATUS -> {
                    if (it.successful) {
                        when (it.message) {
                            "1" -> {
                                ToastUtil.show("文档已取消展示")
                            }
                            "2" -> {
                                ToastUtil.show("文档展示成功")
                            }
                            "-1" -> {
                                ToastUtil.show("文档删除成功")
                            }
                        }
                        page = 1
//                        loadingBar.setStatus(LoadingStatusView.Status.LOADING)
                        refreshData()
                    } else {
                        if (loadingDialog.isShowing) {
                            loadingDialog.dismiss()
                        }
                        ToastUtil.show(it.message)
                    }
                }
                ApiHost.TUTOR_SHARE_MOVE -> {
//                    if (loadingDialog.isShowing) {
//                        loadingDialog.dismiss()
//                    }
                    if (it.successful) {
//                        if (movePos != -1 && nextPos != -1) {
//                            val dataList: List<TutorShareContract> = mAdapter?.data as List<TutorShareContract>
//                            dataList[movePos].isFirst = false
//                            dataList[nextPos].isFirst = false
                        page = 1
//                        loadingBar.setStatus(LoadingStatusView.Status.LOADING)
                        refreshData()
//                            Collections.swap(mAdapter.data, movePos, nextPos)
//                            calcData(dataList)
//                            mAdapter.notifyDataSetChanged()
//                        }
                    } else {
                        if (loadingDialog.isShowing) {
                            loadingDialog.dismiss()
                        }
                        ToastUtil.show(it.message)
                    }
                }
                ApiHost.TUTOR_SHARE_ME_LIST -> {
                    refresh_layout.finishRefresh(true)
                    if (loadingDialog.isShowing) {
                        loadingDialog.dismiss()
                    }
                    if (it.successful) {
                        val data = it.data as (ResponseDataArray<TutorShareContract>)

                        if (page == 1) {
                            loadingBar.visibility = View.GONE
                            mAdapter.setNewData(data.dataList)
                            if (TextUtils.equals("", status)) {
                                EventBus.getDefault().post(PushEvent(Constant.Event.TUTOR_SHARE_SHOW, "no_data"))
                            }
                        } else {
                            loadingBar.visibility = View.GONE
                            mAdapter.addData(data.dataList)
                            mAdapter.notifyDataSetChanged()
                        }

                        //对数据进行过滤计算
                        val allDataList = mAdapter.data
                        if (!allDataList.isNullOrEmpty()) {
                            calcData(allDataList)
                        }

                        if (data.hasMore()) {
                            page++
                            mAdapter.loadMoreComplete() //刷新成功
                        } else {
                            mAdapter.loadMoreEnd(false)
                        }

                        if (page == 1 && data.dataList.isEmpty()) { //第一页 无数据
                            loadingBar.setStatus(LoadingStatusView.Status.EMPTY)
                            loadingBar.setText("暂无展示中的文档")
                            if (TextUtils.equals("", status)) {
                                EventBus.getDefault().post(PushEvent(Constant.Event.TUTOR_SHARE_NO_DATA, "no_data"))
                            }
                        }

                    } else {
                        when {
                            page > 1 -> { //加载下一页数据失败
                                mAdapter.loadMoreFail()
                            }
                            mAdapter.data.isEmpty() -> { //第一页  无数据
                                mAdapter.setNewData(null)
                                loadingBar.setStatus(LoadingStatusView.Status.FAIL)
                            }
                            else -> { //下拉刷新失败
                                ToastUtil.show(it.message)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun calcData(dataList: List<TutorShareContract>) {
        val index = dataList.indexOfFirst {
            it.is_top == 0
        }
        if (index != -1) {
            dataList[index].isFirst = true
        }
    }

    private fun initView() {
        status = arguments?.getString(EXTRA_STATUS) ?: ""
//        movePos = -1
//        changePos = -1`
        mAdapter = MeTutorShareAdapter(status).apply {
            setOnItemClickListener { adapter, view, position ->
                val data = mAdapter.data[position]
                data?.url.let { url ->
                    val webViewParam = WebViewFrag.WebViewParam()
                    webViewParam.url = url
                    webViewParam.sensorOriention = true
                    WebViewFrag.start(mContext, webViewParam)
                    //数据统计，查看详情
                    UmengAnalysisUtil.onEvent("tutor_share_detail")
                }
            }
            setOnItemChildClickListener { adapter, view, position ->
                val data = adapter.data[position] as TutorShareContract
                when (view.id) {
                    R.id.icon_top -> {
//                        movePos = position
//                        nextPos = movePos
                        loadingDialog.show()
//                        nextPos--
                        userViewModel.moveTutorShare(data._id, "1")
                    }
                    R.id.icon_bottom -> {
//                        movePos = position
//                        nextPos = movePos
                        loadingDialog.show()
//                        nextPos++
                        userViewModel.moveTutorShare(data._id, "2")
                    }
                    R.id.item_more -> {
//                        changePos = position
                        val bottomInDialog = BottomInDialog(mContext)
                        val dialogView = LayoutInflater.from(mContext).inflate(R.layout.me_tutor_share_bottom_dialog, null)
                        //如果是全部文档，显示展示和删除
                        //如果是展示文档，显示取消展示和置顶
                        dialogView.apply {
                            if (TextUtils.equals(status, "")) {
                                if (data.status == 2) {
                                    this.item_tv_one.text = "取消展示"
                                } else {
                                    this.item_tv_one.text = "展示"
                                }
                                this.item_tv_two.text = "删除"
                            } else if (TextUtils.equals(status, "2")) {
                                if (data.is_top == 1) {
                                    this.item_tv_two.text = "不置顶"
                                } else {
                                    this.item_tv_two.text = "置顶"
                                }
                            }
                            //取消
                            this.btn_cancel.onClick {
                                bg_view.performClick()
                            }
                            this.bg_view.onClick {
                                bottomInDialog.cancel()
                            }
                            //编辑
                            this.item_tv_three.onClick {
                                val webViewParam = WebViewFrag.WebViewParam()
                                webViewParam.url = Constant.WebPage.TUTOR_SHARE_EDIT + data._id
                                WebViewFrag.start(mContext, webViewParam)
                                isJumpWeb = true
                                bottomInDialog.cancel()
                            }
                            // 取消展示
                            this.item_tv_one.onClick {
                                val spanUtils = SpanUtils()
                                spanUtils.append("是否")
                                        .append(this.item_tv_one.text)
                                        .append("文档")
                                        .appendLine()
                                        .append(data.title)
                                val dialogBuilder = EffectDialogBuilder(mContext)
                                val dialogView: AlertDialogView = AlertDialogView(mContext)
                                        .setContent(spanUtils.create()) //
                                        .setRightBtn("确定") {
                                            bottomInDialog.cancel()
                                            when (this.item_tv_one.text) {
                                                "展示" -> {
                                                    loadingDialog.show()
                                                    userViewModel.changeTutorShareStatus(data._id, "2")
                                                }
                                                "取消展示" -> {
                                                    loadingDialog.show()
                                                    userViewModel.changeTutorShareStatus(data._id, "1")
                                                }
                                            }
                                        }
                                        .setLeftBtn("取消") { dialogBuilder.dismiss() }
                                dialogBuilder
                                        .setCancelable(false)
                                        .setCancelableOnTouchOutside(false)
                                        .setContentView(dialogView).show()
                                dialogView.tv_content.maxWidth = 20
                                dialogView.tv_content.ellipsize = TextUtils.TruncateAt.END
                                dialogView.tv_content.textSize = 17F
                                dialogView.tv_content.setTextColor(Color.parseColor("#25282D"))
                                val tp: TextPaint = dialogView.tv_content.paint
                                tp.isFakeBoldText = true
                            }
                            //置顶/取消置顶
                            this.item_tv_two.onClick {
                                val spanUtils = SpanUtils()
                                spanUtils.append("是否")
                                        .append(this.item_tv_two.text)
                                        .append("文档")
                                        .appendLine()
                                        .append(data.title)
                                val dialogBuilder = EffectDialogBuilder(mContext)
                                val dialogView: AlertDialogView = AlertDialogView(mContext)
                                        .setContent(spanUtils.create()) //
                                        .setRightBtn("确定") {
                                            bottomInDialog.cancel()
                                            when (this.item_tv_two.text) {
                                                "删除" -> {
                                                    loadingDialog.show()
                                                    userViewModel.changeTutorShareStatus(data._id, "-1")
                                                }
                                                "置顶" -> {
                                                    loadingDialog.show()
                                                    userViewModel.changeTutorShareTop(data._id, "1")
                                                }
                                                "不置顶" -> {
                                                    loadingDialog.show()
                                                    userViewModel.changeTutorShareTop(data._id, "0")
                                                }
                                            }
                                        }
                                        .setLeftBtn("取消") { dialogBuilder.dismiss() }
                                dialogBuilder
                                        .setCancelable(false)
                                        .setCancelableOnTouchOutside(false)
                                        .setContentView(dialogView).show()
                                dialogView.tv_content.maxWidth = 20
                                dialogView.tv_content.ellipsize = TextUtils.TruncateAt.END
                                dialogView.tv_content.textSize = 17F
                                dialogView.tv_content.setTextColor(Color.parseColor("#25282D"))
                                val tp: TextPaint = dialogView.tv_content.paint
                                tp.isFakeBoldText = true
                            }
                        }
                        bottomInDialog.setCanceledOnTouchOutside(true)
                        bottomInDialog.setContentView(dialogView)
                        bottomInDialog.show()
                    }
                }
            }
        }
        mAdapter.setOnLoadMoreListener({
            refreshData()
        }, tutor_recyclerView)

        tutor_recyclerView.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }

        refresh_layout.setOnRefreshListener {
            page = 1
            refreshData()
        }

        loadingBar.setOnBtnClickListener {
            page = 1
            loadingBar.setStatus(LoadingStatusView.Status.LOADING)
            refreshData()
        }

        loadingBar.setStatus(LoadingStatusView.Status.LOADING)
        refreshData()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: PushEvent) {
        if (TextUtils.equals(event.action, Constant.Event.TUTOR_SHARE_REFRESH)) {
            page = 1
            loadingBar.setStatus(LoadingStatusView.Status.LOADING)
            refreshData()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isJumpWeb) {
            EventBus.getDefault().post(PushEvent(Constant.Event.TUTOR_SHARE_REFRESH))
        }
        isJumpWeb = false
    }


    /**
     * 刷新数据
     */
    private fun refreshData() {
        userViewModel.getMeTutorShareList(status, page)
    }


}