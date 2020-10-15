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
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.ToastUtils
import com.snqu.shopping.App
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.base.ResponseDataArray
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.TutorShareContract
import com.snqu.shopping.ui.goods.util.JumpUtil
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.ui.mine.adapter.TutorShareAdapter
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.statistics.UmengAnalysisUtil
import kotlinx.android.synthetic.main.tutor_share_frag.*
import kotlinx.android.synthetic.main.tutor_share_header_view.view.*

/**
 *
 * 导师分享
 */
class TutorShareFrag : SimpleFrag() {

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("导师分享",
                    TutorShareFrag::class.java, Bundle().apply {
            })
            SimpleFragAct.start(context, fragParam)
        }
    }

    override fun getLayoutId(): Int = R.layout.tutor_share_frag

    // 数据适配器
    private lateinit var mAdapter: TutorShareAdapter

    // 当前页
    private var page = 1

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    private val headerView by lazy {
        layoutInflater.inflate(R.layout.tutor_share_header_view, null)
    }

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)

        //数据统计，查看我的导师分享
        UmengAnalysisUtil.onEvent("tutor_share_view")

        initView()

        loadingBar.setStatus(LoadingStatusView.Status.LOADING)
        refreshData()

        userViewModel.dataResult.observe(this, Observer {
            when (it.tag) {
                ApiHost.TUTOR_SHARE_LIST -> {
                    refresh_layout.finishRefresh(true)
                    if (it.successful) {
                        val data = it.data as (ResponseDataArray<TutorShareContract>)
                        if (page == 1) {
                            loadingBar.visibility = View.GONE
                            mAdapter.setNewData(data.dataList)
                        } else {
                            loadingBar.visibility = View.GONE
                            mAdapter.addData(data.dataList)
                        }

                        if (data.hasMore()) {
                            page++
                            mAdapter.loadMoreComplete() //刷新成功
                        } else {
                            mAdapter.loadMoreEnd(false)
                        }

                        if (page == 1 && data.dataList.isEmpty()) { //第一页 无数据
                            loadingBar.setStatus(LoadingStatusView.Status.EMPTY)
                            loadingBar.setText("导师暂无分享内容")
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

    private fun initView() {

        setTeacherInfo()

        mAdapter = TutorShareAdapter().apply {
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
            addHeaderView(headerView)
        }

        mAdapter.setOnLoadMoreListener({
            refreshData()
        }, teacher_tutor_recyclerView)

        teacher_tutor_recyclerView.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        teacher_tutor_recyclerView.adapter = mAdapter

        refresh_layout.setOnRefreshListener {
            page = 1
            refreshData()
        }

        loadingBar.setOnBtnClickListener {
            page = 1
            loadingBar.setStatus(LoadingStatusView.Status.LOADING)
            refreshData()
        }
    }

    /**
     * 刷新数据
     */
    private fun refreshData() {
        userViewModel.getTutorShareList(page)
    }


    /**
     * 设置用户导师信息
     */
    private fun setTeacherInfo() {
        headerView.apply {
            val user = UserClient.getUser()
            user?.let { user ->
                if (TextUtils.isEmpty(user.tutor_wechat_show_uid)) {
                    item_copy.visibility = View.GONE
                    teacher_tutor_icon_layout.visibility = View.GONE
                } else {
                    teacher_tutor_icon_layout.visibility = View.VISIBLE
                    item_copy.visibility = View.VISIBLE
                    GlideUtil.loadPic(teacher_tutor_icon, user.tutor_inviter_avatar, R.drawable.icon_default_head, R.drawable.icon_default_head)
                    teacher_tutor_name.text = user.tutor_inviter_username ?: ""
                    teacher_tutor_wechat_name.text = user.tutor_wechat_show_uid

                    item_copy.onClick {
                        try {
                            val clipboardManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = ClipData.newPlainText(null, user.tutor_wechat_show_uid)
                            clipboardManager.primaryClip = clipData
                            ToastUtil.show("已复制微信号：" + user.tutor_wechat_show_uid)
                            if (App.mApp.iwxapi.isWXAppInstalled) {
                                activity?.let { it1 -> JumpUtil.openWechat(it1) }
                            } else {
                                ToastUtils.showShort("您的设备未安装微信客户端")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            ToastUtil.show("复制失败")
                        }
                    }
                }
            }
        }
    }

}