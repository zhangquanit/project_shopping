package com.snqu.shopping.ui.mine.fragment

import android.animation.ArgbEvaluator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.BaseFragment
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.SpanUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.snqu.shopping.App
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.home.entity.AdvertistEntity
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.AccountInfoEntity
import com.snqu.shopping.data.user.entity.BalanceInfoEntity
import com.snqu.shopping.data.user.entity.MandatoryServiceBean
import com.snqu.shopping.ui.goods.fragment.GoodRecmMySelfFrag
import com.snqu.shopping.ui.login.LoginFragment
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.ui.main.frag.collection.CollectionFrag
import com.snqu.shopping.ui.mine.adapter.MandatoryAdapter
import com.snqu.shopping.ui.mine.dialog.BalanceDetailDialog
import com.snqu.shopping.ui.mine.dialog.SwitchEnvrionmentDialog
import com.snqu.shopping.ui.mine.view.AdviserView
import com.snqu.shopping.ui.order.OrderActivity
import com.snqu.shopping.ui.order.fragment.FindOrderFragment
import com.snqu.shopping.ui.vip.frag.TutorWechatFrag
import com.snqu.shopping.ui.vip.frag.VipFrag
import com.snqu.shopping.util.CommonUtil
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.NumberUtil
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.statistics.SndoData
import common.widget.dialog.EffectDialogBuilder
import common.widget.viewpager.BannerImageLoader
import kotlinx.android.synthetic.main.person_fragment.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.abs

/**
 * desc:个人中心
 * time: 2019/8/13
 * @author 银进
 */
class PersonFragment : BaseFragment(), AppBarLayout.OnOffsetChangedListener {

    private val personGoodsCommendItemFragment by lazy {
        PersonGoodsCommendItemFragment()
    }
    private val personGoodsFootItemFragment by lazy {
        PersonGoodsFootItemFragment()
    }
    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    private var balanceInfoEntity: BalanceInfoEntity? = null
    private var accountInfoEntity: AccountInfoEntity? = null
    override fun getLayoutId() = R.layout.person_fragment

    override fun init(savedInstanceState: Bundle?) {
        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.BALANCE_INFO -> {
                    if (it.successful && it.data != null) {
                        balanceInfoEntity = it.data as BalanceInfoEntity
                        if (balanceInfoEntity?.amount_useable != null) {
                            UserClient.canWithdrawal = balanceInfoEntity?.amount_useable
                        }
                        if (balanceInfoEntity?.unsettled_amount != null) {
                            UserClient.unsettled_amount = balanceInfoEntity?.unsettled_amount
                        }
                        if (balanceInfoEntity?.amount_useable != null) {
                            UserClient.amount_useable = balanceInfoEntity?.amount_useable
                        }
                        initUserView()
                    }
                }
                ApiHost.USER_INFO -> {
                    initUserView()
                }
                ApiHost.ACCOUNT_INFO -> {
                    if (it.successful && it.data != null) {
                        accountInfoEntity = it.data as AccountInfoEntity

                        initUserView()
                    }
                }
                ApiHost.AD_LIST -> {
                    if (it.successful && it.data != null) {
                        val adList = it.data as ArrayList<AdvertistEntity>
                        if (adList.isEmpty()) {
                            img_person_ad.visibility = View.GONE
                        } else {
                            img_person_ad.visibility = View.VISIBLE
                            img_person_ad.dataList = adList
                        }
                    }

                }
            }
        })
        initView()

    }


    private fun refreshData() {
        userViewModel.doAdList()
        if (!UserClient.isLogin()) {
            return
        }
        userViewModel.doUserInfo()
        userViewModel.doBalanceInfo()
        userViewModel.doAccountInfo(UserClient.getUser()._id)
    }

    /**
     * 初始化View
     */
    private fun initView() {

        initMandatoryService()

        addAction(Constant.Event.LOGIN_SUCCESS)
        addAction(Constant.Event.LOGIN_OUT)
        addAction(Constant.Event.BIND_INVITE_SUCCESS)
        addAction(Constant.Event.BIND_WX_SUCCESS)
        addAction(Constant.Event.PERSON_TAP_TOP)
        val typeFace = Typeface.createFromAsset(activity?.assets, "fonts/withdrawal_font.ttf")
//        tv_all_money.typeface = typeFace
//        tv_future_money.typeface = typeFace
//        tv_today_money.typeface = typeFace
        tv_self_money.typeface = typeFace

        initUserView()
        ll_money_layout.onClick {
            if (!UserClient.isLogin()) {
                LoginFragment.start(activity)
            } else {
                CommonUtil.jumpToEarningPage(mContext)
            }
        }
        refresh_layout.setOnRefreshListener {
            refresh_layout.finishRefresh(1000)
            personGoodsCommendItemFragment.refreshData()
            personGoodsFootItemFragment.refreshData()
            refreshData()
        }
        img_head.onClick {
            if (!UserClient.isLogin()) {
                LoginFragment.start(activity)
            }
        }
        tv_name.onClick {
            if (!UserClient.isLogin()) {
                LoginFragment.start(activity)
            }
        }
        img_other_head.onClick {
            if (!UserClient.isLogin()) {
                LoginFragment.start(activity)
            }
        }
        img_setting.onClick {
            SndoData.event(SndoData.XLT_EVENT_USER_SETTING)
            if (UserClient.isLogin()) {
//                FreeShippingFrag.start(activity)
                SettingFragment.start(activity)
            } else {
                LoginFragment.start(activity)
            }
        }
        fl_person_money.onClick {
            if (UserClient.isLogin()) {
                SelfBalanceFragment.start(activity)
            } else {
                LoginFragment.start(activity)
            }
        }
        tv_withdrawal.onClick {
            if (UserClient.isLogin()) {
                WithdrawalFragment.start(activity)
            } else {
                LoginFragment.start(activity)
            }
        }
        ll_question.onClick {
            BalanceDetailDialog().show(childFragmentManager, "BalanceDetailDialog")
        }
//        ll_collection.onClick {
//            SndoData.event(SndoData.XLT_EVENT_USER_COLLECTION)
//            if (UserClient.isLogin()) {
//                CollectionFrag.start(activity)
//            } else {
//                LoginFragment.start(activity)
//            }
//        }
        //找回订单
//        tv_person_self_order.onClick {
//            SndoData.event(SndoData.XLT_EVENT_GETBACK_ORDER)
//            if (UserClient.isLogin()) {
//                FindOrderFragment.start(activity)
//            } else {
//                LoginFragment.start(activity)
//            }
//        }
        ll_person_all_order.onClick {
            if (UserClient.isLogin()) {
                OrderActivity.start(activity, 0, true)
            } else {
                LoginFragment.start(activity)
            }
        }
        ll_person_future_order.onClick {
            if (UserClient.isLogin()) {
                OrderActivity.start(activity, 1, true)
            } else {
                LoginFragment.start(activity)
            }
        }
        ll_person_already_order.onClick {
            if (UserClient.isLogin()) {
                OrderActivity.start(activity, 2, true)
            } else {
                LoginFragment.start(activity)
            }
        }
        ll_person_failure_order.onClick {
            if (UserClient.isLogin()) {
                OrderActivity.start(activity, 3, true)
            } else {
                LoginFragment.start(activity)
            }
        }

        tv_goods_left.onClick {
            view_pager.currentItem = 0
        }
        ll_vip_center_money.onClick {
            CommonUtil.jumpToEarningPage(mContext)
        }
        ll_vip_center_order.onClick {
            if (UserClient.isLogin()) {
                OrderActivity.start(activity, 0, true)
            } else {
                LoginFragment.start(activity)
            }
        }
        ll_vip_center_group.onClick {

            if (UserClient.isLogin()) {
                MyTeamFragment.start(activity)
            } else {
                LoginFragment.start(activity)
            }
        }
        ll_vip_center_invite_person.onClick {
            if (UserClient.isLogin()) {
                InvitateFrag.start(mContext)
            } else {
                LoginFragment.start(activity)
            }
        }

        tv_person_invite_code.onClick {
            if (!UserClient.isLogin()) {
                LoginFragment.start(activity)
            }
        }
        tv_copy.onClick {
            val cmb = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val inviteCode = UserClient.inviteCode()
            cmb.primaryClip = ClipData.newPlainText(null, inviteCode)
            showToastShort("复制成功")
        }
        tv_goods_right.onClick {
            if (UserClient.isLogin()) {
                view_pager.currentItem = 1
            } else {
                LoginFragment.start(activity)
            }

        }
        banner_img.onClick {
            if (UserClient.getUser() != null && UserClient.isLogin()) {
                VipFrag.startFromSearch(mContext, true)
            }
        }
        view_pager.isCanScroll = UserClient.isLogin()
        view_pager.adapter = object : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {
            override fun getItem(p0: Int): androidx.fragment.app.Fragment {
                return if (p0 == 0) {
                    personGoodsCommendItemFragment
                } else {
                    personGoodsFootItemFragment
                }

            }

            override fun getCount() = 2
        }
        view_pager.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {
                when (p0) {
                    0 -> {
                        selectLeft()
                    }
                    else -> {
                        selectRight()
                    }
                }
            }
        })

        if (App.devEnv) {
            tv_checkEnvironment.visibility = View.VISIBLE
            var dev = App.getDev()
            when (dev) {
                0 -> tv_checkEnvironment.text = "正式环境"
                1 -> tv_checkEnvironment.text = "测试环境"
                2 -> tv_checkEnvironment.text = "开发环境"
                3 -> tv_checkEnvironment.text = "预发环境"
            }
            tv_checkEnvironment.onClick {
                val switchEnvrionmentDialog = SwitchEnvrionmentDialog(mContext)
                switchEnvrionmentDialog.setCanceledOnTouchOutside(true)
                switchEnvrionmentDialog.setCancelable(true)
                switchEnvrionmentDialog.show()
            }
        }
        ll_wechat_edit.onClick {
            ChangeInviCodeFragment.start(activity)
        }
        tv_title.onClick {
            app_bar_layout.setExpanded(true, true);
        }
        app_bar_layout.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, pos ->
            if (scroll_to_top != null) {
                if (pos > -2000) {
                    scroll_to_top.visibility = View.GONE
                } else {
                    scroll_to_top.visibility = View.VISIBLE
                }
            }
        })
        scroll_to_top.onClick {
            tv_title.performClick()
        }

        // 设置广告
        img_person_ad.setImageLoader(object : BannerImageLoader<ImageView?, AdvertistEntity?> {
            override fun displayView(ctx: Context?, data: AdvertistEntity?, view: ImageView?, pos: Int, count: Int) {
                data?.image = GlideUtil.checkUrl(data?.image)
                GlideUtil.loadPic(view, data?.image)
            }

            override fun createView(ctx: Context): ImageView {
                return layoutInflater.inflate(R.layout.home_banner_item_two, null) as ImageView
            }
        })
        img_person_ad.setmOnItemClickListener { parent, view, position, id ->
            val data = img_person_ad.dataList[position] as? AdvertistEntity
            data?.let {
                userViewModel.adClick(data._id)
                CommonUtil.startWebFrag(context, data)
                SndoData.reportAd(data)
            }
        }
        img_person_ad.interval = 3 * 1000.toLong()
        img_person_ad.startAutoScroll(2 * 1000.toLong())

    }

    private fun initMandatoryService() {
        // 赚钱工具
        initMoneyTools()
        // 必备服务
        initServices()
        // 其他服务
        initOtherServices()
    }

    private fun initOtherServices() {
        val user = UserClient.getUser()
        val mServices = ArrayList<MandatoryServiceBean>()
        mServices.add(MandatoryServiceBean(0, R.drawable.icon_normal_question, "常见问题"))
        mServices.add(MandatoryServiceBean(1, R.drawable.icon_cooperation, "商家合作"))
        mServices.add(MandatoryServiceBean(2, R.drawable.icon_tuiguangguifan, "推广规范"))
        mServices.add(MandatoryServiceBean(3, R.drawable.icon_about, "联系我们"))
        val adapter = MandatoryAdapter(R.layout.person_grid_item, mServices)
        recy_three.layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 4, androidx.recyclerview.widget.GridLayoutManager.VERTICAL, false)
        recy_three.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            when (view.id) {
                0 -> {
                    SndoData.event(SndoData.XLT_EVENT_USER_PROBLEM)
                    if (UserClient.isLogin()) {
                        val webViewParam = WebViewFrag.WebViewParam()
                        webViewParam.url = Constant.WebPage.NORMAL_QUESTION
                        WebViewFrag.start(activity, webViewParam)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
                1 -> {
                    SndoData.event(SndoData.XLT_EVENT_USER_SPINCHAIN)
                    if (UserClient.isLogin()) {
                        val webViewParam = WebViewFrag.WebViewParam()
                        webViewParam.url = Constant.WebPage.BUSINESS_COOPERATION
                        WebViewFrag.start(activity, webViewParam)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
                2 -> {
                    if (UserClient.isLogin()) {
                        val webViewParam = WebViewFrag.WebViewParam()
                        webViewParam.url = Constant.WebPage.PROMOTION_STANDARD
                        WebViewFrag.start(activity, webViewParam)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
                3 -> {
                    if (UserClient.isLogin()) {
                        val webViewParam = WebViewFrag.WebViewParam()
                        webViewParam.url = Constant.WebPage.CONTACT_US
                        WebViewFrag.start(activity, webViewParam)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
            }
        }
    }

    private fun initServices() {
        val user = UserClient.getUser()
        val mServices = ArrayList<MandatoryServiceBean>()
        mServices.add(MandatoryServiceBean(0, R.drawable.person_collection, "我的收藏"))
        mServices.add(MandatoryServiceBean(1, R.drawable.person_retrieve_order, "找回订单"))
        mServices.add(MandatoryServiceBean(2, R.drawable.icon_zl, "批量转链"))
        mServices.add(MandatoryServiceBean(3, R.drawable.icon_watermark, "专属水印"))
        mServices.add(MandatoryServiceBean(4, R.drawable.icon_chudan, "出单榜"))
//        if (user != null && user.level >= 3) {
        //icon_wechat
        mServices.add(MandatoryServiceBean(5, R.drawable.wechat_share, "导师分享"))
//        }
        mServices.add(MandatoryServiceBean(6, R.drawable.icon_xinshou, "新手教程"))
        if (user != null && user.level >= 3) {
            mServices.add(MandatoryServiceBean(7, R.drawable.icon_myrecm, "我的推荐"))
        }
        val adapter = MandatoryAdapter(R.layout.person_grid_item, mServices)
        recy_two.layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 4, androidx.recyclerview.widget.GridLayoutManager.VERTICAL, false)
        recy_two.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            when (view.id) {
                0 -> {
                    SndoData.event(SndoData.XLT_EVENT_USER_COLLECTION)
                    if (UserClient.isLogin()) {
                        CollectionFrag.start(activity)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
                1 -> {
                    SndoData.event(SndoData.XLT_EVENT_GETBACK_ORDER)
                    if (UserClient.isLogin()) {
                        FindOrderFragment.start(activity)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
                2 -> {
                    SndoData.event(SndoData.XLT_EVENT_USER_SPINCHAIN)
                    if (UserClient.isLogin()) {
                        val webViewParam = WebViewFrag.WebViewParam()
                        webViewParam.url = Constant.WebPage.TRANSFORM_URL
                        WebViewFrag.start(activity, webViewParam)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
                3 -> {
                    if (UserClient.isLogin()) {
                        ExclusiveWatermarkFrag.start(activity)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
                4 -> {
                    SndoData.event(SndoData.XLT_EVENT_USER_STANDLIST)
                    if (UserClient.isLogin()) {
                        if (TextUtils.isEmpty(UserClient.getUser().inviter)) {
                            InvitePersonFragment.start(activity)
                        } else {
                            val webViewParam = WebViewFrag.WebViewParam()
                            webViewParam.url = Constant.WebPage.CHUDAN
                            WebViewFrag.start(activity, webViewParam)
                        }
                    } else {
                        LoginFragment.start(activity)
                    }
                }
                5 -> {

                    if (UserClient.isLogin()) {
                        if (user != null && user.level >= 3) {
                            MeTutorShareFrag.start(activity)
                        } else {
                            TutorShareFrag.start(activity)
                        }
                    } else {
                        LoginFragment.start(activity)
                    }
                }
                6 -> {
                    if (UserClient.isLogin()) {
                        val webViewParam = WebViewFrag.WebViewParam()
                        webViewParam.url = Constant.WebPage.GUIDE
                        WebViewFrag.start(activity, webViewParam)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
                7 -> {
                    if (UserClient.isLogin()) {
                        GoodRecmMySelfFrag.start(activity)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
            }
        }
    }

    private fun initMoneyTools() {
        val user = UserClient.getUser()
        val mServices = ArrayList<MandatoryServiceBean>()
        mServices.add(MandatoryServiceBean(0, R.drawable.icon_task, "任务中心"))
        mServices.add(MandatoryServiceBean(1, R.drawable.icon_ditui, "地推宣传"))
        mServices.add(MandatoryServiceBean(2, R.drawable.icon_fadan, "云发单"))
        if (user != null && !TextUtils.isEmpty(user.tutor_wechat_show_uid) && !TextUtils.equals(user.tutor_wechat_show_uid, "null")) {
            mServices.add(MandatoryServiceBean(3, R.drawable.icon_tutor, "导师微信"))
        }
        val adapter = MandatoryAdapter(R.layout.person_grid_item, mServices)
        recy.layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 4, androidx.recyclerview.widget.GridLayoutManager.VERTICAL, false)
        recy.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            when (view.id) {
                0 -> {
                    SndoData.event(SndoData.XLT_EVENT_USER_TASK)
                    CommonUtil.jumpToTaskPage(activity)
                }
                1 -> {
                    SndoData.event(SndoData.XLT_EVENT_USER_GroundPush)
                    if (UserClient.isLogin()) {
                        val webViewParam = WebViewFrag.WebViewParam()
                        webViewParam.url = Constant.WebPage.GROUND_PUSH
                        WebViewFrag.start(activity, webViewParam)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
                2 -> {
                    SndoData.event(SndoData.XLT_EVENT_USER_STANDLIST)
                    if (UserClient.isLogin()) {
                        val webViewParam = WebViewFrag.WebViewParam()
                        webViewParam.url = Constant.WebPage.ORDER_ASSISTANT
                        WebViewFrag.start(activity, webViewParam)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
                3 -> {
                    if (UserClient.isLogin()) {
                        TutorWechatFrag.start(mContext)
                    } else {
                        LoginFragment.start(activity)
                    }
                }
//                4 -> {
//                    SndoData.event(SndoData.XLT_EVENT_USER_SERVICE)
//                    if (UserClient.isLogin()) {
//                        val webViewParam = WebViewFrag.WebViewParam()
//                        webViewParam.url = Constant.WebPage.CUSTOMER_SERVICE
//                        WebViewFrag.start(activity, webViewParam)
//                    } else {
//                        LoginFragment.start(activity)
//                    }
//                }
//                5 -> {
//                    SndoData.event(SndoData.XLT_EVENT_USER_PROBLEM)
//                    if (UserClient.isLogin()) {
//                        val webViewParam = WebViewFrag.WebViewParam()
//                        webViewParam.url = Constant.WebPage.NORMAL_QUESTION
//                        WebViewFrag.start(activity, webViewParam)
//                    } else {
//                        LoginFragment.start(activity)
//                    }
//                }
//                6 -> {
//                    SndoData.event(SndoData.XLT_EVENT_USER_GroundPush)
//                    if (UserClient.isLogin()) {
//                        val webViewParam = WebViewFrag.WebViewParam()
//                        webViewParam.url = Constant.WebPage.GROUND_PUSH
//                        WebViewFrag.start(activity, webViewParam)
//                    } else {
//                        LoginFragment.start(activity)
//                    }
//                }
//                8 -> {
//                    SndoData.event(SndoData.XLT_EVENT_USER_SPINCHAIN)
//                    if (UserClient.isLogin()) {
//
//                        val webViewParam = WebViewFrag.WebViewParam()
//                        webViewParam.url = Constant.WebPage.BUSINESS_COOPERATION
//                        WebViewFrag.start(activity, webViewParam)
//                    } else {
//                        LoginFragment.start(activity)
//                    }
//                }
//                10 -> {
//                    SndoData.event(SndoData.XLT_EVENT_USER_STANDLIST)
//                    if (UserClient.isLogin()) {
//                        val webViewParam = WebViewFrag.WebViewParam()
//                        webViewParam.url = Constant.WebPage.ORDER_ASSISTANT
//                        WebViewFrag.start(activity, webViewParam)
//                    } else {
//                        LoginFragment.start(activity)
//                    }
//                }
//                12 -> {
//                    if (UserClient.isLogin()) {
//                        GoodRecmMySelfFrag.start(activity)
//                    } else {
//                        LoginFragment.start(activity)
//                    }
//                }
//                13 -> {
//                    if (UserClient.isLogin()) {
//                        val webViewParam = WebViewFrag.WebViewParam()
//                        webViewParam.url = Constant.WebPage.PROMOTION_STANDARD
//                        WebViewFrag.start(activity, webViewParam)
//                    } else {
//                        LoginFragment.start(activity)
//                    }
//                }
//                14 -> {
//                    if (UserClient.isLogin()) {
//                        val webViewParam = WebViewFrag.WebViewParam()
//                        webViewParam.url = Constant.WebPage.CONTACT_US
//                        WebViewFrag.start(activity, webViewParam)
//                    } else {
//                        LoginFragment.start(activity)
//                    }
//                }
//                15 -> {
//                    if (UserClient.isLogin()) {
//                        val webViewParam = WebViewFrag.WebViewParam()
//                        webViewParam.url = Constant.WebPage.GUIDE
//                        WebViewFrag.start(activity, webViewParam)
//                    } else {
//                        LoginFragment.start(activity)
//                    }
//                }
            }
        }
    }

    /**
     * 初始化用户信息
     */
    private fun initUserView() {
        if (UserClient.isLogin()) {
            if (UserClient.isVip()) {
                img_vip_flag.visibility = View.VISIBLE
                setCopyText()
            } else {
                img_vip_flag.visibility = View.GONE
                tv_copy.setBackgroundResource(R.drawable.person_copy_bg)
                tv_copy.setTextColor(Color.parseColor("#FFFFFFFF"))
            }
            val user = UserClient.getUser()
            GlideUtil.loadPic(img_head, user.avatar, R.drawable.icon_default_head, R.drawable.icon_default_head)
            GlideUtil.loadPic(img_other_head, user.avatar, R.drawable.icon_default_head, R.drawable.icon_default_head)
            tv_copy.visibility = View.VISIBLE
            tv_name.text = user.username
            if (TextUtils.equals(user.isTutor, "1")) {
                icon_teacher.visibility = View.VISIBLE
            } else {
                icon_teacher.visibility = View.GONE
            }

            tv_person_invite_code.text = "邀请码："
            tv_code.visibility = View.VISIBLE
            tv_code.text = UserClient.inviteCode()


            img_notice_instructions.visibility = View.VISIBLE
            //今日预估
            if (accountInfoEntity?.today_estimate == 0L) {
                tv_all_money.text = "0.00"
            } else {
//                tv_all_money.setTextColor(Color.parseColor("#FFF73737"))
                tv_all_money.text = NumberUtil.saveTwoPoint(accountInfoEntity?.today_estimate)
            }
            //昨日预估
            if (accountInfoEntity?.yesterday_estimate == 0L) {
                yesterday_estimate.text = "0.00"
            } else {
                yesterday_estimate.text = NumberUtil.saveTwoPoint(accountInfoEntity?.yesterday_estimate)
            }
            //本月预估
            if (accountInfoEntity?.nowmonth_estimate == 0L) {
//                tv_future_money.setTextColor(Color.parseColor("#25282D"))
                tv_future_money.text = "0.00"
            } else {
//                tv_future_money.setTextColor(Color.parseColor("#FFF73737"))
                tv_future_money.text = NumberUtil.saveTwoPoint(accountInfoEntity?.nowmonth_estimate)
            }
            //上月预估
            if (accountInfoEntity?.lastmonth_estimate == 0L) {
//                tv_today_money.setTextColor(Color.parseColor("#25282D"))
                tv_today_money.text = "0.00"
            } else {
//                tv_today_money.setTextColor(Color.parseColor("#FFF73737"))
                tv_today_money.text = NumberUtil.saveTwoPoint(accountInfoEntity?.lastmonth_estimate)
            }

            tv_withdrawal.visibility = View.VISIBLE


            tv_self_money.text = SpanUtils()
                    .append(NumberUtil.saveTwoPoint(balanceInfoEntity?.amount_useable)).setFontSize(40, true)
                    .create()
            if ((balanceInfoEntity?.freeze_amount ?: 0L) != 0L) {
                tv_freeze_money.visibility = View.VISIBLE
                tv_freeze_money.text = SpanUtils()
                        .append("提现中:").setFontSize(9, true)
                        .append(" ¥ ").setFontSize(12, true)
                        .append(NumberUtil.saveTwoPoint(balanceInfoEntity?.freeze_amount)).setFontSize(12, true)
                        .create()
            } else {
                tv_freeze_money.visibility = View.GONE
            }

            //说明
            tv_helptext.visibility = View.VISIBLE
            tv_helptext.text = balanceInfoEntity?.helptext

            ll_wechat_edit.visibility = View.VISIBLE

            // 显示会员banner
            banner_img.visibility = View.VISIBLE
            when (user.level) {
                1, 2 -> {
                    banner_img.setImageResource(R.drawable.person_banner_svip)
                }
                3 -> {
                    banner_img.setImageResource(R.drawable.person_banner_level3)
                }
                4 -> {
                    banner_img.setImageResource(R.drawable.person_banner_cfo)
                }
            }

            head_bg.visibility = View.VISIBLE

        } else {
            banner_img.visibility = View.GONE
            img_head.setImageResource(R.drawable.icon_default_head)
            img_other_head.setImageResource(R.drawable.icon_default_head)
            tv_name.text = "登录/注册"
            icon_teacher.visibility = View.GONE
            tv_person_invite_code.text = "美好生活，从这里开始"
            tv_code.visibility = View.GONE
            tv_all_money.text = "0.00"
            yesterday_estimate.text = "0.00"
            tv_future_money.text = "0.00"
            tv_today_money.text = "0.00"
            tv_self_money.text = "0.00"
            tv_copy.visibility = View.GONE
            tv_helptext.visibility = View.GONE
            tv_withdrawal.visibility = View.GONE
            img_vip_flag.visibility = View.GONE
            img_notice_instructions.visibility = View.GONE
            tv_freeze_money.visibility = View.GONE
            head_bg.visibility = View.GONE
            ll_wechat_edit.visibility = View.GONE
        }
        initMandatoryService()
    }

    /**
     * 选择左边
     */
    private fun selectLeft() {
        tv_goods_left.setTextColor(Color.parseColor("#FFFF8202"))
        tv_goods_right.setTextColor(Color.parseColor("#25282D"))

    }

    /**
     * 设置复制的颜色和背景
     */
    private fun setCopyText() {
        when (UserClient.getUser().level) {
            2 -> {
                img_vip_flag.setImageResource(R.drawable.icon_person_vip)
                head_bg.setImageResource(R.drawable.person_vip_bg)
//                tv_copy.setBackgroundResource(R.drawable.vip_level2_right_bg)
//                tv_copy.setTextColor(Color.parseColor("#FFFFFFFF"))
            }
            3 -> {
                img_vip_flag.setImageResource(R.drawable.icon_person_svip)
                head_bg.setImageResource(R.drawable.person_svip_bg)
//                tv_copy.setBackgroundResource(R.drawable.vip_level3_right_bg)
//                tv_copy.setTextColor(Color.parseColor("#FFFFFFFF"))
            }
            4 -> {
                img_vip_flag.setImageResource(R.drawable.icon_person_md)
                head_bg.setImageResource(R.drawable.person_cfo_bg)
//                tv_copy.setBackgroundResource(R.drawable.vip_level4_right_bg)
//                tv_copy.setTextColor(Color.parseColor("#FFE8C48B"))
            }
        }
    }

    /**
     * 选择右边
     */
    private fun selectRight() {
        tv_goods_right.setTextColor(Color.parseColor("#FFFF8202"))
        tv_goods_left.setTextColor(Color.parseColor("#25282D"))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        when {
            event?.action == Constant.Event.LOGIN_SUCCESS -> {
                view_pager.isCanScroll = UserClient.isLogin()
                tv_name.text = UserClient.getUser().username
                refreshData()
                personGoodsFootItemFragment.refreshData()
//                showTeacherDialog()
            }
            event?.action == Constant.Event.BIND_WX_SUCCESS -> {
                refreshData()
            }
            event?.action == Constant.Event.LOGIN_OUT -> {
                initUserView()
                view_pager.isCanScroll = UserClient.isLogin()
            }
            event?.action == Constant.Event.BIND_INVITE_SUCCESS -> {
                userViewModel.doUserInfo()
            }
            event?.action == Constant.Event.CHANGE_PHONE_SUCCESS -> {
//                tv_name.levelText = UserClient.getUser().nick.replace(Regex("(\\d{3})\\d{4}(\\d{4})"), "\$1****\$2")
                tv_name.text = UserClient.getUser().username
            }
            event?.action == Constant.Event.WITHDRAWAL_SUCCESS -> {
                userViewModel.doBalanceInfo()
            }
            event?.action == Constant.Event.PERSON_TAP_TOP -> {
                scroll_to_top.performClick()
            }
        }
    }


    override fun restorePage() {
        StatusBar.setStatusBar(mContext, true)
        refreshData()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mContext ?: return
        if (!hidden) {
            StatusBar.setStatusBar(mContext, false)
            refreshData()
            showTeacherDialog()
            if (null != tv_title) tv_title.performClick()
        }
    }

    /**
     * 显示导师对话框
     */
    private fun showTeacherDialog() {
        if (UserClient.getUser() != null && UserClient.isLogin()) {
            if (!TextUtils.isEmpty(UserClient.getUser().tutor_wechat_show_uid) && !TextUtils.equals(UserClient.getUser().tutor_wechat_show_uid, "null")) {
                var isNew = SPUtils.getInstance().getString(Constant.PREF.IS_NEW)
                // 是新用户，并且上级导师有微信
                if (TextUtils.equals(isNew, "1")) {
                    var count = SPUtils.getInstance().getString(Constant.PREF.IS_NEW_DATA)
                    if (TextUtils.isEmpty(count)) {
                        SPUtils.getInstance().put(Constant.PREF.IS_NEW_DATA, "is_new_count")
                    } else {
                        if (TextUtils.equals(count, "is_new_count")) {
                            val dialogView = activity?.let {
                                AdviserView(it)
                            }
                            dialogView?.setLeftBtn("我已添加")
                            dialogView?.setRightBtn("复制微信号")
                            EffectDialogBuilder(mContext)
                                    .setContentView(dialogView)
                                    .setCancelable(false)
                                    .setCancelableOnTouchOutside(false)
                                    .show()
                            SPUtils.getInstance().put(Constant.PREF.IS_NEW_DATA, "is_old_count")
                            SPUtils.getInstance().put(Constant.PREF.IS_NEW, "0")
                        }
                    }
                }
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        app_bar_layout.addOnOffsetChangedListener(this)
    }

    override fun onOffsetChanged(p0: AppBarLayout?, v: Int) {
        toolbar?.let {
            if (abs(v) <= 400) {
                val evaluate: Int = ArgbEvaluator().evaluate((abs(v) / (1f * 400)), Color.parseColor("#00FFFFFF"), Color.parseColor("#FFFFFFFF")) as Int
                toolbar.setBackgroundColor(evaluate)
                tv_title.visibility = View.GONE
                img_other_head.visibility = View.GONE
                StatusBar.setStatusBar(mContext, false)
            } else {
                toolbar.setBackgroundColor(Color.parseColor("#FFffffff"))
                tv_title.visibility = View.VISIBLE
                img_other_head.visibility = View.VISIBLE
                StatusBar.setStatusBar(mContext, true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (app_bar_layout != null) {
            app_bar_layout.removeOnOffsetChangedListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
        resumeScroll()
    }

    override fun onPause() {
        super.onPause()
        pauseScroll()
    }

    private fun resumeScroll() {
        img_person_ad?.resumeAutoScroll()
    }

    private fun pauseScroll() {
        img_person_ad?.pauseAutoScroll()
    }


}