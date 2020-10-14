package com.snqu.shopping.ui.goods

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.jiguang.jmlinksdk.api.annotation.JMLinkRouter
import cn.jzvd.Jzvd
import com.android.util.date.DateFormatUtil
import com.android.util.ext.ToastUtil
import com.anroid.base.BaseActivity
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.SpanUtils
import com.kepler.jd.Listener.OpenAppAction
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.base.NetReqResult
import com.snqu.shopping.data.base.ResponseDataArray
import com.snqu.shopping.data.goods.bean.DetailImageBean
import com.snqu.shopping.data.goods.bean.DetailImageContentBean
import com.snqu.shopping.data.goods.entity.*
import com.snqu.shopping.data.home.ItemSourceClient
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.ui.goods.adapter.DetailGoodsPicAdapter
import com.snqu.shopping.ui.goods.dialog.CopyPasswordSuccessDialog
import com.snqu.shopping.ui.goods.dialog.ParameterDialog
import com.snqu.shopping.ui.goods.dialog.RebateDescriptionDialog
import com.snqu.shopping.ui.goods.dialog.SecurityDialog
import com.snqu.shopping.ui.goods.fragment.GoodRecommendFrag
import com.snqu.shopping.ui.goods.fragment.ImageFragment
import com.snqu.shopping.ui.goods.fragment.PlayerFragment
import com.snqu.shopping.ui.goods.fragment.ShareFragment
import com.snqu.shopping.ui.goods.helper.RecodeHelper
import com.snqu.shopping.ui.goods.player.VideoImageDetailActivity
import com.snqu.shopping.ui.goods.util.JumpUtil
import com.snqu.shopping.ui.goods.vm.GoodsViewModel
import com.snqu.shopping.ui.login.LoginFragment
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.main.MainActivity
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.ui.main.frag.channel.reds.frag.ShopDetialFrag
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel
import com.snqu.shopping.ui.mine.adapter.PersonGoodsItemAdapter
import com.snqu.shopping.ui.vip.frag.VipFrag
import com.snqu.shopping.util.DispatchUtil
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.GoodsMathUtil
import com.snqu.shopping.util.NumberUtil
import com.snqu.shopping.util.ext.clickWithTrigger
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.log.LogClient
import com.snqu.shopping.util.statistics.SndoData
import com.snqu.shopping.util.statistics.StatisticInfo
import com.umeng.socialize.UMShareAPI
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.goods_detail_fragment.*
import kotlinx.android.synthetic.main.goods_detail_head_one_item.*
import kotlinx.android.synthetic.main.goods_detail_head_one_item.view.*
import kotlinx.android.synthetic.main.include_goods_detail_foot.*
import kotlinx.android.synthetic.main.include_goods_detail_foot.view.*
import kotlinx.android.synthetic.main.include_goods_shop.view.*
import kotlinx.android.synthetic.main.include_goods_sold_out.*
import kotlinx.android.synthetic.main.include_goods_sold_out.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList


/**
 * desc:
 * time: 2019/8/15
 * @author 银进
 */
@JMLinkRouter(keys = ["goods_detail"])
class GoodsDetailActivity : BaseActivity() {
    //商品实体
    private var goodsEntity: GoodsEntity? = null

    //商品详情实体
    private var goodsEntityDesc: GoodsEntity? = null

    //标签标记商品详情是否请求成功
    private var loadGoodsDetailDes: Boolean = false

    //标签标记商品基础数据是否请求成功
    private var loadGoodsDetailBase: Boolean = false

    //用于控制显示右上角推荐图标
    private var goodRecmEntity: GoodRecmEntity? = null

    //请求的ViewModel
    private val goodsViewModel by lazy {
        ViewModelProviders.of(this).get(GoodsViewModel::class.java)
    }

    //banner数据源
    private val bannerSource by lazy {
        arrayListOf<DetailImageBean>()
    }

    //请求推荐商品的ViewModel
    private val homeViewModel by lazy {
        ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    //京东跳转的回调
    private val mOpenAppAction by lazy {
        OpenAppAction { p0 ->
            runOnUiThread {
                if (p0 == OpenAppAction.OpenAppAction_start) {//开始状态未必一定执行，
                    showLoadingDialog("页面跳转中...")
                } else {
                    closeLoadDialog()
                }
            }
        }
    }

    //进入页面的加载loading
    private val loadingDialog by lazy {
        LoadingDialog(this, "数据加载中", true)
    }

    //是否第一次加载
    private var isFirstLoad = true

    //猜你喜欢一页多少个
    private val row = 99

    //猜你喜欢目前的页数
    private var page = 1

    //是否执行登录
    private var loginAction = false

    //区分是点击分享赚还是下单返利
    private var isClickShare = false

    //商品信息banner
    private val bannerFragments by lazy {
        mutableListOf<androidx.fragment.app.Fragment>()
    }

    //总滑动距离
    private var totalDy = 0

    //推荐商品的一页个数
    private val rowRecommended = 99

    //推荐商品的总页数
    private var pageRecommended = 1

    //个人推荐商品adapter
    private val personGoodsItemAdapter by lazy {
        PersonGoodsItemAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                start(this@GoodsDetailActivity, data[position]._id, data[position].item_source, data[position])
            }
            setOnLoadMoreListener({
                loadMoreData()
            }, include_goods_sold_out.recycler_view_goods_sold_out)
            setLoadMoreView(CommonLoadingMoreView())
        }
    }

    //商品ID
    private var goodsId: String = ""

    //商品来源BCD
    private var item_source: String = ""

    private val itemId: String by lazy {
        intent?.getStringExtra(EXTRA_ITEM_ID) ?: ""
    }

    //分类信息id 有就传递
    private val reportType by lazy {
        intent?.getIntExtra(EXTRA_REPORT_TYPE, 0)
    }

    //板块id  有就需要传递
    private val plate by lazy {
        intent?.getStringExtra(EXTRA_PLATE)
    }

    //二级板块id
    private val subPlate by lazy {
        intent?.getStringExtra(EXTRA_SUB_PLATE)
    }

    //商品是否下架
    private var isOverTime = false
        set(value) {
            if (value) {
                include_goods_detail_foot.ll_normal_bottom.visibility = View.GONE
                include_goods_detail_foot.ll_over_time_bottom.visibility = View.VISIBLE
            } else {
                include_goods_detail_foot.ll_normal_bottom.visibility = View.VISIBLE
                include_goods_detail_foot.ll_over_time_bottom.visibility = View.GONE
            }
            field = value
        }

    //商品是否有效
    private var goodsEnable = true
        set(value) {
            if (value) {
                headTypeOneView.view_pager.visibility = View.VISIBLE
                include_goods_detail_foot.visibility = View.VISIBLE
                include_goods_sold_out.visibility = View.GONE
            } else {
                refreshNoGoodsData()
                headTypeOneView.view_pager.visibility = View.GONE
                include_goods_detail_foot.visibility = View.GONE
                include_goods_sold_out.visibility = View.VISIBLE
            }
            field = value
        }

    //加载失败
    private fun loadFail(value: Boolean) {
        if (value) {
            include_goods_sold_out.visibility = View.GONE
            loadingview?.apply {
                setStatus(LoadingStatusView.Status.FAIL)
                setOnBtnClickListener {
                    refreshData()
                }
            }
        } else {
            loadingview.visibility = View.GONE
        }
    }

    //是否有视频
    private var hasVideo = false

    //第一个head(展示商品信息的)
    private val headTypeOneView by lazy {
        layoutInflater.inflate(R.layout.goods_detail_head_one_item, null)
    }

    private var mPromotionLinkEntity: PromotionLinkEntity? = null
    private var clickType = 1//1代表复制淘口令，2代表下单,3,获取淘口令
    override fun getLayoutId() = R.layout.goods_detail_fragment
    private lateinit var detailGoodsPicAdapter: DetailGoodsPicAdapter

    override fun init(savedInstanceState: Bundle?) {

        // 沉浸式状态栏设置
        StatusBar.setStatusBar(mContext, true)

        // 获取从主界面或浏览器跳转过来的的参数值
        initExtraData()

        // 对控件进行一些必要的初始化工作
        initView()
        settingListener()

        // 进行网络请求和网络数据处理
        loadingRemoteData()
    }

    private fun initExtraData() {
        addAction(Constant.Event.AUTH_SUCCESS)
        addAction(Constant.Event.LOGIN_SUCCESS)
        RecodeHelper.goodsDetailPage.add(this)
        if (RecodeHelper.goodsDetailPage.size > 3) { //最多保留3个
            RecodeHelper.goodsDetailPage.removeAt(0).finish()
        }
        if (intent != null) {
            goodsId = intent?.getStringExtra(EXTRA_ID) ?: ""
            item_source = intent?.getStringExtra(EXTRA_ITEM_SOURCE) ?: ""
            goodsEntity = intent?.getParcelableExtra(EXTRA_GOODS)
        }

        //获取第三方浏览器跳转过来的参数
        if (goodsId == null) {
            // 获取用户自定义参数
            val bundle = intent.extras
            if (bundle != null && bundle.keySet().size > 0) {
                val stringBuilder = StringBuilder()
                for (key in bundle.keySet()) {
                    stringBuilder.append(key).append(" - ").append(bundle.getSerializable(key)).append("\n")
                    if (key == "goods_id") {
                        goodsId = bundle.getString(key)

                    }
                }
                Log.e("bundle", stringBuilder.toString())
            }
        }
    }

    private fun settingListener() {
        // 刷新数据
        refresh_layout.setOnRefreshListener {
            refreshData()
        }

        // 退出界面
        img_goods_back.onClick {
            finish()
        }

        //未过期、过期回到首页
        ll_goods_detail_home.onClick {
            backToMainActivity()
        }

        ll_over_time_goods_detail_home.onClick {
            backToMainActivity()
        }

        //复制淘口令
        ll_goods_detail_password.onClick {
            SndoData.event(SndoData.XLT_EVENT_GOODDETAIL_COPYLINK,
                    SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
                    SndoData.XLT_ITEM_THIRDCate_TITLE, "null",
                    SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
                    SndoData.XLT_GOOD_NAME, goodsEntity?.item_title ?: "null",
                    SndoData.XLT_GOOD_ID, goodsEntity?._id,
                    SndoData.XLT_ITEM_SOURCE, goodsEntity?.item_source ?: "null"
            )
            if (UserClient.isLogin()) {
                if (mPromotionLinkEntity == null) {
                    clickType = 1
                    showLoadingDialog("加载中")
                    goodsViewModel.doPromotionLink(LINK_TYPE, goodsId, item_source, "", "1")
                } else {
                    copyTBPassword()
                }
            } else {
                LoginFragment.start(this@GoodsDetailActivity)
            }
        }


        //收藏
        img_goods_save.onClick {
            if (UserClient.isLogin()) {
                if ((img_goods_save.isSelected)) {//收藏了
                    goodsViewModel.deleteCollectionGoodsItem(goodsId, item_source)
                } else {
                    goodsViewModel.doAddCollectionGoods(goodsId, item_source)
                }
                SndoData.event(SndoData.XLT_EVENT_GOODDETAIL_FAVORITE,
                        SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
                        SndoData.XLT_ITEM_THIRDCate_TITLE, "null",
                        SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
                        SndoData.XLT_GOOD_NAME, goodsEntity?.item_title ?: "null",
                        SndoData.XLT_GOOD_ID, goodsId,
                        SndoData.XLT_ITEM_SOURCE, item_source
                )
            } else {
                LoginFragment.start(this@GoodsDetailActivity)
            }
        }
        ll_goods_save.onClick {
            img_goods_save.performClick()
        }
        ll_goods_recommend.onClick {
            showLoadingDialog("加载中")
            if (!TextUtils.isEmpty(goodsId) && !TextUtils.isEmpty(item_source)) {
                goodsViewModel.clickCommunityGoodsRecm(goodsId, item_source);
            } else {
                showToastShort("商品信息有误，无法推荐")
            }
        }
        //下单
        tv_goods_detail_rebate.clickWithTrigger(1000) {
            SndoData.event(SndoData.XLT_EVENT_GOODDETAIL_BUY,
                    SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
                    SndoData.XLT_ITEM_THIRDCate_TITLE, "null",
                    SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
                    SndoData.XLT_GOOD_NAME, goodsEntity?.item_title ?: "null",
                    SndoData.XLT_GOOD_ID, goodsEntity?._id ?: "null",
                    SndoData.XLT_ITEM_COUPON_AMOUNT, GoodsMathUtil.calcCoupon_amount(goodsEntity),
                    SndoData.XLT_ITEM_REBATE_AMOUNT, GoodsMathUtil.calcRebate_amount(goodsEntity),
                    SndoData.XLT_ITEM_SOURCE, goodsEntity?.item_source,
                    "push_id", "null",
                    "Landing_Page_title", "null",
                    "Landing_Page_url", "null"
            )


            isClickShare = true
            jumpToThird()
        }
        //分享赚
        tv_goods_detail_share.clickWithTrigger(1000) {
            isClickShare = false
            jumpToShare()
        }

        img_goods_sold_out_back.onClick {
            onBackPressedSupport()
        }


        tv_goods_detail_share.isEnabled = false
        tv_goods_detail_rebate.isEnabled = false

    }

    private fun loadingRemoteData() {
        goodsViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.DELETE_COLLECTION_GOODS_ITEM -> {
                    deleteCollection(it)
                }
                ApiHost.ADD_COLLECTION_GOODS -> {
                    addCollection(it)
                }
                ApiHost.DELETE_COLLECTION_GOODS -> {
                    if (it.successful) {
                        StatisticInfo().cancelCollect(plate, subPlate, goodsEntity?.item_id, goodsEntity?.item_source)
                    }
                }
                ApiHost.PROMOTION_LINK -> {
                    closeLoadDialog()
                    if (goodsEntity?.item_source.equals("C") || goodsEntity?.item_source.equals("B")) {
                        // 走淘宝，天猫的跳转逻辑
                        if (it.successful && it.data != null) {
                            mPromotionLinkEntity = it.data as PromotionLinkEntity
                            jumpToTB(it)
                            isClickShare = false
                        } else {
                            showToastShort(it.message)
                            LogClient.log(TAG, "转链失败,message=" + it.message)
                        }
                    } else {
                        if (it.successful && it.data != null) {
                            mPromotionLinkEntity = it.data as PromotionLinkEntity
                            if (it.extra == false && !TextUtils.isEmpty(mPromotionLinkEntity?.auth_url) && goodsEntity?.item_source.equals(Constant.BusinessType.PDD)) {
                                JumpUtil.authPdd(mContext, mPromotionLinkEntity?.auth_url)
                            } else {
                                if (mPromotionLinkEntity != null && isClickShare) {
                                    jumpToThird()
                                } else {
                                    jumpToShare()
                                }
                            }
                            isClickShare = false
                        } else {
                            showToastShort(it.message)
                            LogClient.log(TAG, "转链失败,message=" + it.message)
                        }
                    }
                    if (mPromotionLinkEntity != null) {
                        LogClient.log(TAG, "转链成功,参数=" + mPromotionLinkEntity.toString())
                    }
                }
                ApiHost.GOODS_FAV -> {
                    //刷新收藏情况
                    var isFav = false
                    if (it.successful && it.data != null) {
                        val goodsFavEntity = it.data as GoodsFavEntity
                        isFav = goodsFavEntity.is_fav == 1
                    }
                    refreshFav(isFav)
                }
                ApiHost.GOODS_DETAIL_DESC -> {
                    //刷新详情数据
                    if (it.successful && it.data != null) {
                        val goodsEntity = it.data as GoodsEntity
                        goodsEntityDesc = goodsEntity
                    }
                    //无论加载成功还是失败，加载失败也要加载因为banner还要取首图第一张
                    if (loadGoodsDetailBase) {
                        refreshGoodsDetailView()
                    }
                    loadGoodsDetailDes = true
                }
                ApiHost.GOODS_REC_TEXT -> {
                    if (it.successful) {
                        val data = it.data as GoodsRecmText
                        if (!TextUtils.isEmpty(data.recommend_text)) {
                            val spanUtils = SpanUtils()
                            data.recommend_text?.let { it1 ->
                                spanUtils.append(it1)
                                        .setFontSize(12, true)
                                        .setForegroundColor(Color.parseColor("#333333"))
                                        .append(" ")
                                        .append("点我复制")
                                        .setFontSize(13, true)
                                        .setForegroundColor(Color.parseColor("#FF8202"))
                                tip.text = spanUtils.create()
                                tip.onClick {
                                    val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cmb.primaryClip = ClipData.newPlainText(null, data.recommend_text)
                                    ToastUtil.show("复制成功")
                                }
                            }
                            headTypeOneView?.let {
                                layout_recm_good.visibility = View.VISIBLE
                                view_line_recm.visibility = View.VISIBLE
                            }
                        } else {
                            headTypeOneView?.let {
                                layout_recm_good.visibility = View.GONE
                                view_line_recm.visibility = View.GONE
                            }
                        }
                    }
                }
                ApiHost.GOODS_DETAIL -> {
//                    loadingDialog.dismiss()
                    if (!isFinishing) {
                        if (it.successful && it.data != null) {
                            loadFail(false)
                            refresh_layout.finishRefresh(true)
                            val goodsEntity = it.data as GoodsEntity
                            this.goodsEntity = goodsEntity
                            val timestamp = System.currentTimeMillis() / 1000
                            if ((timestamp > (goodsEntity?.presale?.end_time
                                            ?: 0L))) {
                                goodsEntity.presale = null
                            }
                            if ((timestamp < (goodsEntity?.presale?.start_time
                                            ?: 0L))) {
                                goodsEntity.presale = null
                            }

                            if (!TextUtils.isEmpty(goodsEntity.item_source)) {
                                item_source = goodsEntity.item_source.toString()
                            }
                            if (!TextUtils.isEmpty(goodsEntity._id)) {
                                goodsId = goodsEntity._id.toString()
                            }

                            // 商品推荐
                            goodsEntity.status?.let { it ->
                                if (it == 1) {
                                    goodsViewModel.communityGoodsRecm(goodsId, item_source)
                                }
                            }
                            // 足迹
                            addGoodsRecode()
                            // 收藏
                            if (UserClient.isLogin()) {
                                goodsViewModel.doGoodsFav(goodsId, item_source)
                            }
                            //进行推荐理由请求
                            if (!TextUtils.isEmpty(goodsEntity.reload_rec_text) && TextUtils.equals("1", goodsEntity.reload_rec_text)) {
                                goodsViewModel.getGoodsRecText(goodsId, item_source)
                            } else {
                                if (!TextUtils.isEmpty(goodsEntity.recommend_text)) {
                                    val spanUtils = SpanUtils()
                                    goodsEntity.recommend_text?.let { it1 ->
                                        spanUtils.append(it1)
                                                .setFontSize(12, true)
                                                .setForegroundColor(Color.parseColor("#333333"))
                                                .append(" ")
                                                .append("点我复制")
                                                .setFontSize(13, true)
                                                .setForegroundColor(Color.parseColor("#FF8202"))
                                        tip.text = spanUtils.create()
                                        tip.onClick {
                                            val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            cmb.primaryClip = ClipData.newPlainText(null, goodsEntity.recommend_text)
                                            ToastUtil.show("复制成功")
                                        }
                                    }
                                    layout_recm_good.visibility = View.VISIBLE
                                    view_line_recm.visibility = View.VISIBLE
                                } else {
                                    layout_recm_good.visibility = View.GONE
                                    view_line_recm.visibility = View.GONE
                                }
                            }


                            //汇报
                            StatisticInfo().viewGoodDetailPage(plate, subPlate, goodsEntity.item_id, goodsEntity.item_source, reportType!!)
                            refreshGoodsDetailBaseView()
//                        //详情页数据加载成功
                            if (loadGoodsDetailDes) {
                                refreshGoodsDetailView()
                                if (goodsEntityDesc == null || goodsEntityDesc?.item_desc == null || goodsEntityDesc?.item_desc?.content?.size == 0) {
                                    goodsViewModel.doGoodsDetailDesc(goodsId, item_source)
                                }
                            }

                        } else {
                            //产品需求，默认进来先loading，然后请求网络数据，如果网络数据没有的话就显示传进来的数据
                            //如果是第二次其你去接口失败的话，保持上一次数据不变
                            if (isFirstLoad) {
                                if (goodsEntity != null) {
                                    refreshGoodsDetailBaseView()
                                } else if (!it.successful) {
                                    // 当商品未找到的时候，显示没有数据。
                                    include_goods_sold_out.visibility = View.GONE
                                    loadingview?.apply {
                                        val status = LoadingStatusView.Status.EMPTY
                                        status.text = "商品未找到"
                                        setStatus(status)
                                        setOnBtnClickListener {
                                            refreshData()
                                        }
                                    }
                                } else { //空数据
                                    goodsEnable = false
                                }
                            }
                            //详情页数据加载成功
                            if (loadGoodsDetailDes) {
                                refreshGoodsDetailView()
                            }
                            refresh_layout.finishRefresh(false)
                        }
                        loadGoodsDetailBase = true
                        isFirstLoad = false
                    }
                }
                ApiHost.SHOP_RECOMMEND -> {
                    refreshShopRecommendView(it)
                }
                ApiHost.GOODS_DETAIL_RECOMMEND -> {
                    refreshRecommendDataView(it)
                }
                ApiHost.COMMUNITY_GOODS_RECM -> {
                    goodRecmEntity = GoodRecmEntity()
                    if (it.successful) {
                        goodRecmEntity = it.data as GoodRecmEntity
                        showRecmImageView()
                    }
                }
                ApiHost.COMMUNITY_GOODS_RECM_CLICK -> {
                    closeLoadDialog()
                    if (it.successful) {
                        goodRecmEntity = it.data as GoodRecmEntity
                        val shareType = goodRecmEntity?.share_type
                        if (!TextUtils.isEmpty(shareType)) {
                            when (shareType) {
                                "1" -> {
                                    if (goodsEntity != null) {
                                        goodsEntity?.item_id = itemId
                                        GoodRecommendFrag.start(this, goodsEntity, goodRecmEntity?.share_advance)
                                    }
                                }
                                "2" -> {
                                    showToastShort("每天最多只能推荐15个商品哦~")
                                }
                                "5" -> {
                                    showToastShort(it.message)
                                }
                                "6" -> {
                                    showToastShort(it.message)
                                }
                                "3" -> {
                                    showToastShort("商品已被其他人推荐,请08:00以后再推荐吧")
                                }
                                "7" -> {
                                    showToastShort("商品已推荐 请到[发圈]-[我的推荐]中查看管理")
                                }
                            }
                        }
                    } else {
                        showToastShort(it.message)
                    }
                }
            }
        })
        homeViewModel.mNetReqResultLiveData.observe(this, Observer {
            refreshEmptyDataAdapterView(it)
        })

        loadingDialog.setCancelable(true)
        loadingDialog.setCancelableOnTouchOutside(true)
        bannerSource.clear()
        refreshData()
    }

    /**
     * 刷新商品是否已经收藏
     */
    private fun refreshFav(isFav: Boolean) {
        if (UserClient.isLogin()) {
            //登录了表示要刷新一下這個商品是否收藏
            img_goods_save.isSelected = isFav
            setImageSave()
        }
    }

    private fun setImageSave() {
        if (toolbar != null) {
            if (toolbar.background.alpha >= 255) {
                img_goods_save.setImageResource(R.drawable.tab_collection)
            } else {
                if (img_goods_save.isSelected) {
                    img_goods_save.setImageResource(R.drawable.tab_icon_home_collection_p)
                } else {
                    img_goods_save.setImageResource(R.drawable.tab_collection_t_n)
                }
            }
        }
    }

    /**
     * 刷新详情参数规格以及banner和详情图片等信息
     */
    @SuppressLint("SetTextI18n")
    private fun refreshGoodsDetailView() {
        //防止刷新得时候出现重复添加数据
        bannerSource.clear()


        if (goodsEntityDesc != null) {
            //处理文本图片
            val descList = goodsEntityDesc?.item_desc?.content ?: arrayListOf()
            var goodsDetailList = mutableListOf<DetailImageContentBean>()
            descList.forEach { desc ->
                if (!TextUtils.isEmpty(desc)) {
                    val goodsEntity = GoodsEntity()
                    goodsEntity.desc_content = desc
                    try {
                        if (desc.subSequence(0, 11).contains("//")) {
                            if (!desc.contains(".gif")) {
                                goodsDetailList.add(DetailImageContentBean(goodsEntity, DetailImageContentBean.DESC_IMG))
                            }
                        } else {
                            goodsDetailList.add(DetailImageContentBean(goodsEntity, DetailImageContentBean.DESC_TEXT))
                        }
                    } catch (e: Exception) {
                        goodsDetailList.add(DetailImageContentBean(goodsEntity, DetailImageContentBean.DESC_TEXT))
                    }
                }
            }
            if (goodsDetailList.size > 0) {
                goodsDetailList.add(0, DetailImageContentBean(GoodsEntity(), DetailImageContentBean.DESC))
                detailGoodsPicAdapter.addData(0, goodsDetailList)
            }
        }

        //恢复默认状态
        hasVideo = false
        //基础信息
        //首页视频图片啥的
        if (null != goodsEntityDesc?.item_video && goodsEntityDesc?.item_video?.isNotEmpty() == true && goodsEntityDesc?.item_video?.get(0) != null) {
            hasVideo = true
            bannerSource.add(DetailImageBean(hasVideo, null, goodsEntityDesc?.item_video?.get(0)))
        }
        if (!(goodsEntityDesc?.item_images.isNullOrEmpty())) {
            goodsEntityDesc?.item_images?.forEach { img ->
                bannerSource.add(DetailImageBean(hasVideo, img, null))
            }
        } else {
            if (goodsEntity?.item_image != null) {
                bannerSource.add(DetailImageBean(hasVideo, goodsEntity?.item_image
                        ?: "", null))
            }
        }


        // 避免首页图重复添加的标志
        if (!hasVideo && goodsEntity?.item_image != null) {
            var isRepeatAdd = false
            var itemImageValue = getCompareValue(goodsEntity?.item_image!!)
            bannerSource.forEach {
                if (!isRepeatAdd && !TextUtils.isEmpty(it.imgUrl)) {
                    var imgUrl = getCompareValue(it.imgUrl!!)
                    if (TextUtils.equals(itemImageValue, imgUrl)) {
                        isRepeatAdd = true
                    }
                }
            }

            if (!isRepeatAdd) {
                bannerSource.add(0, DetailImageBean(hasVideo, goodsEntity?.item_image
                        ?: "", null))
            }
        }

        headTypeOneView.apply {

            //有视频不显示这个图片导航标签
            if (hasVideo) {
                tv_banner_indicator.visibility = View.GONE
            } else {
                if (bannerSource.size > 1) {
                    tv_banner_indicator.visibility = View.VISIBLE
                    tv_banner_indicator.text = "1/${bannerSource.size}"
                }
            }
            setBannerAdapter()


            //服務
            val consumerProtectionList = goodsEntityDesc?.consumer_protection
                    ?: ArrayList()

            if (consumerProtectionList.isEmpty()) {
                cos_click_security.visibility = View.GONE
            } else {
                cos_click_security.visibility = View.VISIBLE
                val securityDetail = StringBuilder()
                for (k in consumerProtectionList.indices) {
                    if (k == consumerProtectionList.size - 1) {
                        securityDetail.append(consumerProtectionList[k].title ?: "")
                    } else {
                        securityDetail.append(consumerProtectionList[k].title ?: "")
                        securityDetail.append(" · ")
                    }
                }
                tv_security_detail.text = securityDetail

                cos_click_security.onClick {

                    SndoData.event(SndoData.XLT_EVENT_GOODDETAIL_GUARANTEE,
                            SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
                            SndoData.XLT_ITEM_THIRDCate_TITLE, "null",
                            SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
                            SndoData.XLT_GOOD_NAME, goodsEntity?.item_title ?: "null",
                            SndoData.XLT_GOOD_ID, goodsId,
                            SndoData.XLT_ITEM_SOURCE, item_source
                    )

                    SecurityDialog(item_source).apply {
                        securityList.addAll(consumerProtectionList)
                    }.show(supportFragmentManager, "SecurityDialog")
                }
            }
            //參數
            if (goodsEntityDesc?.item_props?.isNullOrEmpty() == true) {
                cos_click_parameter.visibility = View.GONE
            } else {
                val itemProps = goodsEntityDesc?.item_props?.get(0)?.keys
                if (itemProps.isNullOrEmpty()) {
                    cos_click_parameter.visibility = View.GONE
                } else {
                    cos_click_parameter.visibility = View.VISIBLE
                    cos_click_parameter.onClick {
                        SndoData.event(SndoData.XLT_EVENT_GOODDETAIL_PARAMETER,
                                SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
                                SndoData.XLT_ITEM_THIRDCate_TITLE, "null",
                                SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
                                SndoData.XLT_GOOD_NAME, goodsEntity?.item_title ?: "null",
                                SndoData.XLT_GOOD_ID, goodsId,
                                SndoData.XLT_ITEM_SOURCE, item_source
                        )
                        ParameterDialog().apply {
                            parameterMap = goodsEntityDesc?.item_props?.get(0)
                                    ?: mapOf()
                        }.show(supportFragmentManager, "ParameterDialog")
                    }
                    val parameterDetail = StringBuilder()
                    itemProps.forEach { ip ->
                        parameterDetail.append(ip)
                        parameterDetail.append(" ")
                    }
                    tv_parameter_detail.text = parameterDetail.toString().trim()
                }
            }
            //下划线显示与否
            if (cos_click_parameter.visibility == View.GONE) {
                view_line4.visibility = View.GONE
            } else {
                view_line4.visibility = View.VISIBLE
            }
            if (cos_click_security.visibility == View.GONE && cos_click_parameter.visibility == View.GONE) {
                view_line3.visibility = View.GONE
            } else {
                view_line3.visibility = View.VISIBLE
            }
            if (cos_click_send_goods.visibility == View.GONE && cos_click_security.visibility == View.GONE && cos_click_parameter.visibility == View.GONE) {
                view_line5.visibility = View.GONE
            } else {
                view_line5.visibility = View.VISIBLE
            }
        }
    }


    /**
     * 刷新基础详情页View价格标题等
     */
    @SuppressLint("SetTextI18n")
    private fun refreshGoodsDetailBaseView() {
        goodsEnable = true
        bannerSource.clear()


        tv_goods_detail_share.isEnabled = true
        tv_goods_detail_rebate.isEnabled = true

        headTypeOneView.apply {
            icon_type.visibility = View.GONE
            tv_img_rebate_text.visibility = View.GONE
            img_goods_save.visibility = View.VISIBLE
            rl_member_tip.onClick {
                if (UserClient.isLogin()) {
                    VipFrag.startFromSearch(mContext, true)
                } else {
                    LoginFragment.start(mContext)
                }
            }

            if (goodsEntity?.next_level != null && goodsEntity?.next_level?.level != null) {
                val tip = SpanUtils()
                tip.append("成为运营总监, 下单最高可返利").setForegroundColor(Color.parseColor("#DF7100"))
                        .setFontSize(11, true)
                        .setBold()
                tip.append("￥" + NumberUtil.saveTwoPoint(goodsEntity?.next_level?.rebate?.xkd_amount
                        ?: 0L)).setForegroundColor(Color.parseColor("#F73737"))
                        .setFontSize(11, true)
                if (goodsEntity?.next_level?.level!! <= 4) {
                    rl_member_tip?.visibility = View.VISIBLE
                    layout_detail_vip_bg?.visibility = View.VISIBLE
                    icon_vip.visibility = View.VISIBLE
                    GlideUtil.loadPic(icon_vip, R.drawable.icon_vip)
                    detail_vip_text?.text = tip.create()
                }
            }

            bannerSource.add(DetailImageBean(hasVideo, goodsEntity?.item_image
                    ?: "", null))

            //有视频不显示这个图片导航标签
            if (hasVideo) {
                tv_banner_indicator.visibility = View.GONE
            } else {
                if (bannerSource.size > 1) {
                    tv_banner_indicator.visibility = View.VISIBLE
                    tv_banner_indicator.text = "1/${bannerSource.size}"
                }
            }
            setBannerAdapter()

            isOverTime = goodsEntity?.status != 1

            if ((goodsEntity?.item_source
                            ?: "") == Constant.BusinessType.TB || (goodsEntity?.item_source
                            ?: "") == Constant.BusinessType.TM) {
                include_goods_detail_foot.ll_goods_detail_password.visibility = View.VISIBLE
            } else {
                include_goods_detail_foot.ll_goods_detail_password.visibility = View.GONE
            }

            //标题
            tv_name.text = goodsEntity?.item_title ?: ""
            tv_name.setOnLongClickListener {
                val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cmb.primaryClip = ClipData.newPlainText(null, tv_name.text)
                showToastShort("商品标题已复制")
                true
            }


            //价格
            val price = SpanUtils()
            price.setVerticalAlign(SpanUtils.ALIGN_TOP)
            //优惠券
            if (goodsEntity?.getCouponPrice()?.isBlank() == true) {
                cos_coupons.visibility = View.GONE
            } else {
                cos_coupons.visibility = View.VISIBLE
                price.append("券后").setForegroundColor(Color.parseColor("#F34264")).setFontSize(13, true)
                var spanUtils = SpanUtils()
                        .append("￥").setForegroundColor(Color.parseColor("#FFFFFF")).setFontSize(13, true)
                        .append(goodsEntity?.getCouponPrice()
                                ?: "").setForegroundColor(Color.parseColor("#FFFFFF")).setFontSize(19, true)
                        .append("优惠券").setForegroundColor(Color.parseColor("#FFFFFF")).setFontSize(12, true)
                        .create()
                if (!TextUtils.isEmpty(goodsEntity?.coupon?.info)) {
                    spanUtils = SpanUtils()
                            .append("￥").setForegroundColor(Color.parseColor("#FFFFFF")).setFontSize(13, true)
                            .append(goodsEntity?.getCouponPrice()
                                    ?: "").setForegroundColor(Color.parseColor("#FFFFFF")).setFontSize(19, true)
                            .append(" " + goodsEntity?.coupon?.info).setForegroundColor(Color.parseColor("#FFFFFF")).setFontSize(12, true)
                            .create()
                }
                tv_coupons.text = spanUtils
                tv_effective_date.text = "有效期:${
                    DateFormatUtil.yyyyMMdd().format(Date((goodsEntity?.coupon?.start_time
                            ?: 0) * 1000L))
                }- ${
                    DateFormatUtil.yyyyMMdd().format(Date((goodsEntity?.coupon?.end_time
                            ?: 0) * 1000L))
                }"
                tv_get_coupon.onClick {
                    SndoData.event(SndoData.XLT_EVENT_GOODDETAIL_MANAGE,
                            SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
                            SndoData.XLT_ITEM_THIRDCate_TITLE, "null",
                            SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
                            SndoData.XLT_GOOD_NAME, goodsEntity?.item_title ?: "null",
                            SndoData.XLT_GOOD_ID, goodsEntity?._id ?: "null",
                            SndoData.XLT_ITEM_COUPON_AMOUNT, GoodsMathUtil.calcCoupon_amount(goodsEntity),
                            SndoData.XLT_ITEM_COUPON_ID, goodsEntity?.coupon?.coupon_id,
                            SndoData.XLT_ITEM_SOURCE, goodsEntity?.item_source
                    )
                    if (UserClient.isLogin()) {
                        isClickShare = true
                        jumpToThird()
                    } else {
                        LoginFragment.start(this@GoodsDetailActivity)
                    }
                }

            }
            price.append("￥").setForegroundColor(Color.parseColor("#F34264")).setFontSize(14, true).setBold()
            try {
                val nowPrice = goodsEntity?.getNow_price()?.split(".")
                if (nowPrice?.size == 2) {
                    price.append(nowPrice[0]).setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(23, true).setBold()
                    price.append(".${nowPrice[1]} ").setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(16, true).setBold()
                } else {
                    price.append("${
                        goodsEntity?.getNow_price()
                                ?: ""
                    } ").setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(23, true).setBold()
                }
            } catch (e: Exception) {
                price.append("${goodsEntity?.getNow_price()} ").setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(23, true).setBold()
            }
//            // 原价
            if (TextUtils.equals(goodsEntity?.getNow_price(), goodsEntity?.getOld_price())) {
                tv_price.text = price.create()
            } else {
                price.appendLine()
                tv_price.text =
                        price.append("原价:").setForegroundColor(Color.parseColor("#C3C4C7")).setFontSize(12, true)
                                .append("￥${
                                    goodsEntity?.getOld_price()
                                            ?: ""
                                }").setForegroundColor(Color.parseColor("#C3C4C7")).setFontSize(12, true).setStrikethrough()

                                .create()
            }

            //返利
            if ((goodsEntity?.getRebatePrice() ?: "") == "") {
                tv_img_rebate_text.visibility = View.GONE
                img_rebate_description.visibility = View.GONE
            } else {
                val priceText = SpanUtils()
                priceText.append("返").setFontSize(14, true)
                        .setBold()
                        .append("￥${
                            goodsEntity?.getRebatePrice()
                                    ?: ""
                        }")
                        .setFontSize(15, true)
                        .setBold()

                tv_img_rebate_text.visibility = View.VISIBLE

                //jd返利展示情况
                if (goodsEntity?.isJDSelf() == true) {
                    priceText.append("\n")
                    priceText.append("(Plus:￥${goodsEntity?.getJDPlusPrice()})")
                            .setFontSize(11, true)
                    priceText.appendLine()
                }

                img_rebate_description.visibility = View.VISIBLE
                img_rebate_description.onClick {
                    RebateDescriptionDialog().show(supportFragmentManager, "RebateDescriptionDialog")
                }

//                priceText.append("\n")
//                priceText.append("(平台返利+奖励)")

                if (TextUtils.equals(goodsEntity?.hight_rebate, "1")) {
//                    tv_img_rebate.text = "下单高返约"
//                    tv_img_rebate_text.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_7046E0))
//                    priceText.appendLine()
//                    priceText.append("(平台返利+奖励)")
//                            .setFontSize(11, true)
//                    tv_jd_rebate_des.text = "(平台返利+奖励)"
                } else {
//                    tv_img_rebate_text.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_FF8202))
                }
                tv_img_rebate_text.text = priceText
                        .create()
            }
            changeBottomPrice(goodsEntity)

            if (goodsEntity?.item_source == "D" || goodsEntity?.item_delivery_from.isNullOrBlank() || goodsEntity?.item_delivery_postage == null) {
                cos_click_send_goods.visibility = View.GONE
            } else {
                cos_click_send_goods.visibility = View.VISIBLE
                //发货地
                tv_send_goods_address.text = goodsEntity?.item_delivery_from
                //邮费
                tv_send_goods_price.text = "快递${goodsEntity?.getDelivery_postage() ?: ""}元"
            }

            //店铺名称展示
            if (!TextUtils.isEmpty(goodsEntity?.seller_shop_name)) {
                tv_shop_name.visibility = View.VISIBLE
                tv_shop_name.text = goodsEntity?.seller_shop_name
            } else if (null != goodsEntity?.seller) {
                tv_shop_name.visibility = View.VISIBLE
                tv_shop_name.text = goodsEntity?.seller?.seller_shop_name
            } else {
                tv_shop_name.visibility = View.GONE
            }


            //店铺销量展示
            if (goodsEntity?.item_sell_count == null) {
                tv_shop_sales.visibility = View.GONE
            } else {
                tv_shop_sales.visibility = View.VISIBLE
                tv_shop_sales.text = "销量" + goodsEntity?.getSell_count()
            }
            //店铺类型BCD展示
            if (goodsEntity?.item_source == null) {
                tv_icon.visibility = View.INVISIBLE
            } else {
                tv_icon.visibility = View.VISIBLE

                tv_icon.text = ItemSourceClient.getItemSourceName(goodsEntity?.item_source)
            }


            //店铺
            include_goods_shop.apply {

                if (goodsEntity?.isShowSeller() != true) {
                    this.visibility = View.GONE
                } else {
                    goodsViewModel.doShopRecommend(goodsEntity?.seller?.seller_shop_id
                            ?: "", goodsId, goodsEntity?.item_source)
                    shop_level.setData(goodsEntity?.seller)
                    tv_hot_person.visibility = View.VISIBLE
                    tv_hot_person.text = SpanUtils()
                            .append(goodsEntity?.seller?.fans
                                    ?: "0").setForegroundColor(Color.parseColor("#FFFF8202"))
                            .append("人关注").setForegroundColor(Color.parseColor("#25282D"))
                            .create()
//                    }
                    tv_goods_shop_icon.text = ItemSourceClient.getItemSourceName(goodsEntity?.item_source)

                    tv_goods_shop_name.text = goodsEntity?.seller?.seller_shop_name
                            ?: ""
                    GlideUtil.loadPic(img_goods_shop_pic, goodsEntity?.seller?.seller_shop_icon, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
                    tv_goods_shop_enter.onClick {
                        jumpShopDetail()
                    }
                    tv_goods_shop_recommended_look_all.onClick {
                        jumpShopDetail()
                    }
                    if (null == goodsEntity?.seller?.scoreDesc) {
                        tv_goods_shop_description.visibility = View.GONE
                        tv_goods_shop_service.visibility = View.GONE
                        tv_goods_shop_logistics.visibility = View.GONE
                    } else {
                        tv_goods_shop_description.visibility = View.VISIBLE
                        tv_goods_shop_service.visibility = View.VISIBLE
                        tv_goods_shop_logistics.visibility = View.VISIBLE
                        tv_goods_shop_description.text = goodsEntity?.seller?.scoreDesc
                        tv_goods_shop_service.text = goodsEntity?.seller?.scoreServ
                        tv_goods_shop_logistics.text = goodsEntity?.seller?.scorePost
                    }
                }

                if (item_source == Constant.BusinessType.PDD && goodsEntity?.seller != null) {
                    tv_hot_person.visibility = View.GONE

                    if ((goodsEntity?.seller?.sell_count
                                    ?: "0") != "0") {
                        shop_pdd_sellcount.visibility = View.VISIBLE
                        val stringBuilder = SpanUtils()
                                .append("已拼").setForegroundColor(Color.parseColor("#25282D"))
                                .append((goodsEntity?.seller?.sell_count
                                        ?: 0).toString()).setForegroundColor(Color.parseColor("#F73737"))
                                .append("件").setForegroundColor(Color.parseColor("#25282D"))
                                .create()
                        shop_pdd_sellcount.text = stringBuilder
                    }

                    if (!TextUtils.isEmpty(goodsEntity?.seller?.seller_fans) && !goodsEntity?.seller?.fans.equals("0")) {
                        shop_pdd_fans.visibility = View.VISIBLE
                        val stringBuilder = SpanUtils()
                                .append((goodsEntity?.seller?.fans).toString()).setForegroundColor(Color.parseColor("#F73737"))
                                .append("人关注").setForegroundColor(Color.parseColor("#25282D"))
                                .create()
                        shop_pdd_fans.text = stringBuilder
                    }
                }

                include_goods_shop.onClick {
                    jumpShopDetail()
                }


            }
        }
    }

    private fun jumpShopDetail() {
        if (TextUtils.isEmpty(goodsEntity?.seller?.seller_shop_url)) {
            ShopDetialFrag.start(this@GoodsDetailActivity, goodsEntity?.seller_shop_id, goodsEntity?.item_source)
        } else {
            if (item_source == Constant.BusinessType.TM || item_source == Constant.BusinessType.TB || item_source == Constant.BusinessType
                            .PDD) {
                val webViewParam = WebViewFrag.WebViewParam()
                webViewParam.url = goodsEntity?.seller?.seller_shop_url
                // 拼多多由于需要登录，因此只能打开APP，淘宝/天猫直接在本地打开
                if (item_source != Constant.BusinessType
                                .PDD) {
                    webViewParam.isDetailJump = false
                    webViewParam.isPromotionLinkJump = true
                } else {
                    webViewParam.isAuthJump = true
                }
                WebViewFrag.start(mContext, webViewParam)
            } else {
                ShopDetialFrag.start(this@GoodsDetailActivity, goodsEntity?.seller_shop_id, goodsEntity?.item_source)
            }
        }
        SndoData.event(SndoData.XLT_EVENT_GOODDETAIL_LOOKALL,
                SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
                SndoData.XLT_ITEM_THIRDCate_TITLE, "null",
                SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
                SndoData.XLT_GOOD_NAME, goodsEntity?.item_title ?: "null",
                SndoData.XLT_GOOD_ID, goodsId,
                SndoData.XLT_ITEM_SOURCE, item_source,
                SndoData.XLT_ITEM_SHOP_ID, goodsEntity?.seller?._id,
                SndoData.XLT_ITEM_SHOP_TITLE, goodsEntity?.seller?.seller_shop_name,
                SndoData.XLT_ITEM_SHOP_TYPE, goodsEntity?.seller?.seller_type
        )
    }

    private fun getCompareValue(str: String): String {
        try {
            var str = str.substring(0, str.lastIndexOf("."))
            var lastIndex = str.lastIndexOf("/")
            var lastString = str.substring(lastIndex, str.length)
            var subString = str.subSequence(0, lastIndex)
            var subIndex = subString.lastIndexOf("/")
            var subValue = subString.substring(subIndex + 1, subString.length)

            return subValue + lastString
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun getShareImgs(): ArrayList<String?> {
        val imgs = ArrayList<String?>()
        if (!(goodsEntityDesc?.item_images.isNullOrEmpty())) {
            goodsEntityDesc?.item_images?.forEach { img ->
                imgs.add(img)
            }
        }

        if (imgs.isNotEmpty()) {
            var isRepeatAdd = false
            var itemImageValue = getCompareValue(goodsEntity?.item_image!!)
            imgs.forEach {
                if (!isRepeatAdd && !TextUtils.isEmpty(it)) {
                    var imgUrl = getCompareValue(it!!)
                    if (TextUtils.equals(itemImageValue, imgUrl)) {
                        isRepeatAdd = true
                    }
                }
            }
            if (!isRepeatAdd) {
                imgs.add(0, goodsEntity?.item_image)
            }
        } else {
            imgs.add(goodsEntity?.item_image)
        }
        return imgs
    }

    /**
     * 控制显示右上角推荐图标的样式
     */
    private fun showRecmImageView() {
        if (goodRecmEntity != null && toolbar != null) {
            if (goodRecmEntity?.can_show == true) {
                ll_goods_recommend.visibility = View.VISIBLE
                val shareType = goodRecmEntity?.share_type
                if (!TextUtils.isEmpty(shareType)) {
                    if (TextUtils.equals("1", shareType)) {
                        if (toolbar.background.alpha >= 255) {
                            iv_goods_recommend.setImageResource(R.drawable.icon_good_recommend_p)
                        } else {
                            iv_goods_recommend.setImageResource(R.drawable.icon_good_recommend_p_bg)
                        }
                    } else if (TextUtils.equals("3", shareType)) {
                        if (toolbar.background.alpha >= 255) {
                            iv_goods_recommend.setImageResource(R.drawable.icon_good_not_recommend_n)
                        } else {
                            iv_goods_recommend.setImageResource(R.drawable.icon_good_not_recommend_n_bg)
                        }
                    } else if (TextUtils.equals("7", shareType)) {
                        if (toolbar.background.alpha >= 255) {
                            iv_goods_recommend.setImageResource(R.drawable.icon_good_not_recommend_p)
                        } else {
                            iv_goods_recommend.setImageResource(R.drawable.icon_good_not_recommend_p_bg)
                        }
                    } else if (TextUtils.equals("2", shareType)) {
                        if (toolbar.background.alpha >= 255) {
                            iv_goods_recommend.setImageResource(R.drawable.icon_good_not_recommend_n)
                        } else {
                            iv_goods_recommend.setImageResource(R.drawable.icon_good_not_recommend_n_bg)
                        }
                    } else {
                        ll_goods_recommend.visibility = View.GONE
                    }
                }
            } else {
                ll_goods_recommend.visibility = View.GONE
            }


        }
    }

    /**
     * 底部分享赚的文案
     */
    @SuppressLint("SetTextI18n")
    private fun changeBottomPrice(price: GoodsEntity?) {


        if (isOverTime) {
            ll_detail_rebate.visibility = View.GONE
            return
        }

        if (null == goodsEntity) {
            ll_detail_rebate.visibility = View.GONE
        } else {
            if (!goodsEntity?.hasCommissionPrice()!! && !goodsEntity?.hasSubsidyPrice()!!) {
                ll_detail_rebate.visibility = View.GONE
            } else {
                ll_detail_rebate.visibility = View.VISIBLE
                tv_detail_rebate.text = "返利￥${goodsEntity?.getCommissionPrice()} + 平台补贴￥${goodsEntity?.getSubsidyPrice()}"
            }
        }


        //若商品即没优惠券又没返利时，显示立即购买
        val flag = TextUtils.isEmpty(price?.getRebatePrice()) && TextUtils.isEmpty(price?.getCouponPrice())
        if (flag) {
            this@GoodsDetailActivity.include_goods_detail_foot.tv_goods_detail_rebate.text = "立即购买"
        } else {
            this@GoodsDetailActivity.include_goods_detail_foot.tv_goods_detail_rebate.text =
                    price?.getEconomPrice()?.let {
                        var tip = "立即购买"
                        if (!TextUtils.isEmpty(price.getCouponPrice())) {
                            tip = "立即领券"
                        }
                        SpanUtils()
                                .append("省").setFontSize(16, true).setBold()
                                .append(it).setFontSize(18, true).setBold()
                                .append("元").setFontSize(16, true).setBold()
                                .append("\n").setFontSize(6, true)
                                .append(tip).setFontSize(10, true)
                                .setForegroundColor(Color.parseColor("#CCFFFFFF"))
                                .create()
                    }
        }

        //分享赚
        this@GoodsDetailActivity.include_goods_detail_foot.tv_goods_detail_share.text =
                SpanUtils()
                        .append("赚").setFontSize(16, true).setBold()
                        .append(if (price?.getRebatePrice() == "") "0.00" else (price?.getRebatePrice()
                                ?: "0.00")).setFontSize(18, true).setBold()
                        .append("元").setFontSize(16, true).setBold()
                        .append("\n").setFontSize(6, true)
                        .append("立即分享").setFontSize(10, true)
                        .setForegroundColor(Color.parseColor("#CCFFFFFF"))
                        .create()


        if (TextUtils.equals(goodsEntity?.hight_rebate, "1")) {
            icon_type.visibility = View.VISIBLE
            icon_type.setImageResource(R.drawable.icon_hight_rebate)
            this@GoodsDetailActivity.include_goods_detail_foot.tv_goods_detail_rebate.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_7046E0))
            tv_detail_rebate.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_7046E0))
            tv_detail_rebate_down.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_7046E0))
            this@GoodsDetailActivity.include_goods_detail_foot.tv_goods_detail_rebate.text =
                    price?.getEconomPrice()?.let {
                        var tip = "立即购买"
                        if (!TextUtils.isEmpty(price.getCouponPrice())) {
                            tip = "立即领券"
                        }
                        SpanUtils()
                                .append("省").setFontSize(16, true).setBold()
                                .append(it).setFontSize(18, true).setBold()
                                .append("元").setFontSize(16, true).setBold()
                                .append("\n").setFontSize(6, true)
                                .append(tip).setFontSize(10, true)
                                .setForegroundColor(Color.parseColor("#CCFFFFFF"))
                                .create()
                    }

            tv_detail_rebate.text = "平台返利￥${goodsEntity?.getCommissionPrice()} + 奖励￥${goodsEntity?.getSubsidyPrice()}"
            ll_detail_rebate.visibility = View.VISIBLE
            herald_tip.visibility = View.GONE
            headTypeOneView.apply {
                rl_price.visibility = View.VISIBLE
            }
        } else {
            include_goods_detail_foot.tv_goods_detail_rebate.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_F73737))
            tv_detail_rebate.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_F73737))
            tv_detail_rebate_down.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_F73737))
            val timestamp = System.currentTimeMillis() / 1000
            // 预售
            if (goodsEntity?.presale != null) {
                rl_price.visibility = View.VISIBLE
                ll_due.visibility = View.VISIBLE
                icon_type.visibility = View.VISIBLE
                icon_type.setImageResource(R.drawable.icon_presell)
                if ((goodsEntity?.presale?.tail_start_time
                                ?: 0) > 0 && (goodsEntity?.presale?.tail_end_time
                                ?: 0) > 0) {
                    discount_time.visibility = View.VISIBLE
                    discount_time.text = SpanUtils().append("支付尾款时间：")
                            .append(DateFormatUtil.yyyy_MM_dd_two().format(goodsEntity?.presale?.tail_start_time?.times(1000L)?.let { Date(it) }))
                            .append("~")
                            .append(DateFormatUtil.yyyy_MM_dd_two().format(goodsEntity?.presale?.tail_end_time?.times(1000L)?.let { Date(it) }))
                            .create()
                } else {
                    discount_time.visibility = View.GONE
                }
                if (TextUtils.isEmpty(goodsEntity?.presale?.discount_fee_text)) {
                    discount_fee_text.visibility = View.GONE
                } else {
                    discount_fee_text.visibility = View.VISIBLE
                    discount_fee_text.text = goodsEntity?.presale?.discount_fee_text
                }
                ll_detail_rebate.visibility = View.GONE
                include_goods_detail_foot.tv_goods_detail_rebate.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_F34264))
                herald_tip.visibility = View.VISIBLE
                val time = DateFormatUtil.MM_dd_HH_mm().format(goodsEntity?.presale?.end_time?.times(1000L)?.let { Date(it) })
                herald_tip.text = time + " 付定金结束"
                herald_tip.setBackgroundColor(Color.parseColor("#FDF1F3"))
                herald_tip.setTextColor(Color.parseColor("#F34264"))
                tv_goods_detail_rebate.text =
                        price?.getEconomPrice()?.let {
                            var tip = "立即付定金"
                            SpanUtils()
                                    .append("省").setFontSize(16, true).setBold()
                                    .append(it).setFontSize(18, true).setBold()
                                    .append("元").setFontSize(16, true).setBold()
                                    .append("\n").setFontSize(6, true)
                                    .append(tip).setFontSize(10, true)
                                    .setForegroundColor(Color.parseColor("#CCFFFFFF"))
                                    .create()
                        }
            } else {
                headTypeOneView.apply {
                    rl_price.visibility = View.VISIBLE
                }
                include_goods_detail_foot.tv_goods_detail_rebate.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_F73737))
                tv_detail_rebate.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_F73737))
                tv_detail_rebate_down.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_F73737))
                if (goodsEntity?.getCouponPrice()?.isBlank() == false) {
                    //预告券
                    val startTime = goodsEntity?.coupon?.start_time ?: 0
                    val idahosHerald = (System.currentTimeMillis() / 1000) < startTime
                    if (idahosHerald) {
                        icon_type.visibility = View.VISIBLE
                        icon_type.setImageResource(R.drawable.icon_advance)
                        tv_goods_detail_rebate.setBackgroundColor(Color.parseColor("#FF13AB5A"));
                        tv_goods_detail_rebate.text =
                                price?.getRebatePrice()?.let {
                                    var tip = "提前领券"
                                    SpanUtils()
                                            .append("省").setFontSize(16, true).setBold()
                                            .append(it).setFontSize(18, true).setBold()
                                            .append("元").setFontSize(16, true).setBold()
                                            .append("\n").setFontSize(6, true)
                                            .append(tip).setFontSize(10, true)
                                            .setForegroundColor(Color.parseColor("#CCFFFFFF"))
                                            .create()
                                }
                        ll_detail_rebate.visibility = View.GONE
                        herald_tip.setBackgroundColor(Color.parseColor("#FFE5FFE7"))
                        herald_tip.setTextColor(Color.parseColor("#FF20B766"))
                        herald_tip.visibility = View.VISIBLE
                        herald_tip.text = DateFormatUtil.yyyyMMddCh().format(Date((goodsEntity?.coupon?.start_time
                                ?: 0) * 1000L)) + "以后可用，请提前领券"
                    } else {
                        ll_detail_rebate.visibility = View.VISIBLE
                        herald_tip.visibility = View.GONE
                    }
                }
            }
        }
    }

    /**
     * 刷新null详情数据的时候展示的猜你喜欢的view
     */
    private fun refreshEmptyDataAdapterView(it: NetReqResult?) {
        if (it?.tag == ApiHost.LIKE_GOODS) {
            if (it.successful) {
                val responseDataArray = it.data as ResponseDataArray<GoodsEntity>
                val data = responseDataArray.dataList ?: arrayListOf<GoodsEntity>()
                if (page == 1) {
                    //第一次加載
                    if (it.successful) {
                        if (data.isNotEmpty()) {
                            showNormalView(data)
                            if (data.size < row) {
                                personGoodsItemAdapter.loadMoreEnd(false)
                            } else {
                                personGoodsItemAdapter.loadMoreComplete()
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
                        if (data.isNotEmpty()) {
                            addData(data)
                            if (data.size < row) {
                                personGoodsItemAdapter.loadMoreEnd(false)
                            } else {
                                personGoodsItemAdapter.loadMoreComplete()
                                page++
                            }
                        } else {
                            personGoodsItemAdapter.loadMoreEnd(false)
                        }
                    } else {
                        personGoodsItemAdapter.loadMoreFail()
                    }
                }
            } else {
                personGoodsItemAdapter.loadMoreFail()
            }

        }
    }

    /**
     * 刷新推荐商品的RecycleView
     */
    private fun refreshRecommendDataView(it: NetReqResult) {
        if (page == 1) {
            //第一次加載
            if (it.successful) {
                refresh_layout.finishRefresh(true)
                val data = it.data as ArrayList<GoodsEntity>
                if (data.isNotEmpty()) {
                    val detailImageContentBeanList = arrayListOf<DetailImageContentBean>()
                    detailImageContentBeanList.add(DetailImageContentBean(GoodsEntity(), DetailImageContentBean.BOTTOM_RECOMMEND_TITLE))
                    data.forEach {
                        var dettailBean = DetailImageContentBean(it, DetailImageContentBean.BOTTOM)
                        detailImageContentBeanList.add(dettailBean)
                    }

                    showRecommendNormalView(detailImageContentBeanList)
                    if (data.size < row) {
                        detailGoodsPicAdapter.loadMoreEnd(false)
                    } else {
                        //产品说不需要分页加载
                        detailGoodsPicAdapter.loadMoreEnd(false)
                    }
                } else {
                    refresh_layout.finishRefresh(false)
                }
            } else {
                //加載更多
                if (it.successful) {
                    val data = it.data as ArrayList<GoodsEntity>
                    if (data.isNotEmpty()) {
                        val detailImageContentBeanList = arrayListOf<DetailImageContentBean>()
                        data.forEach {
                            var dettailBean = DetailImageContentBean(it, DetailImageContentBean.BOTTOM)
                            detailImageContentBeanList.add(dettailBean)
                        }
                        addRecommendData(detailImageContentBeanList)
                        if (data.size < row) {
                            detailGoodsPicAdapter.loadMoreEnd(false)
                        } else {
                            //产品说不需要分页加载
                            detailGoodsPicAdapter.loadMoreEnd(false)
                        }
                    } else {
                        detailGoodsPicAdapter.loadMoreEnd(false)
                    }
                } else {
                    detailGoodsPicAdapter.loadMoreFail()
                }
            }
        }
    }


    /**
     * 返回主界面
     */
    private fun backToMainActivity() {
        MainActivity.start(this@GoodsDetailActivity)
        SndoData.event(SndoData.XLT_EVENT_GOODDETAIL_HOME,
                SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
                SndoData.XLT_ITEM_THIRDCate_TITLE, "null",
                SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
                SndoData.XLT_GOOD_NAME, goodsEntity?.item_title ?: "null",
                SndoData.XLT_GOOD_ID, goodsEntity?._id,
                SndoData.XLT_ITEM_SOURCE, goodsEntity?.item_source ?: "null"
        )
    }

    /**
     * 产品需求如果没有推荐商品，就不需要添加推荐商品得header，为了防止重复添加就先判断是否已经添加了这个header，防止刷新得时候重复添加
     *
     * 是否将推荐得header添加到适配器了
     */
    private fun judgeIsAddHeader(headView: View): Boolean {
        val childCount = detailGoodsPicAdapter.headerLayout.childCount
        for (i in 0 until childCount) {
            if (detailGoodsPicAdapter.headerLayout.getChildAt(i) == headView) {
                return true
            }
        }
        return false
    }

    /**
     * 刷新店铺推荐UI
     */
    private fun refreshShopRecommendView(it: NetReqResult) {
        if (it.successful) {
            val shopRecommendedList = it.data as List<GoodsEntity>
            if (shopRecommendedList.isEmpty()) {
                headTypeOneView.include_goods_shop.visibility = View.GONE
            } else {
                //店铺推荐商品
                headTypeOneView.include_goods_shop.apply {
                    this.visibility = View.VISIBLE
                    goods_shop_recommended_goods1?.onClick {
                        SndoData.reportGoods(shopRecommendedList[0], 0, SndoData.PLACE.item_details_recommend.name)
                        start(this@GoodsDetailActivity, shopRecommendedList[0]._id
                                ?: "", shopRecommendedList[0].item_source
                                ?: "", shopRecommendedList[0])
                    }
                    goods_shop_recommended_goods2?.onClick {
                        SndoData.reportGoods(shopRecommendedList[1], 1, SndoData.PLACE.item_details_recommend.name)
                        start(this@GoodsDetailActivity, shopRecommendedList[1]._id
                                ?: "", shopRecommendedList[1].item_source
                                ?: "", shopRecommendedList[1])
                    }
                    goods_shop_recommended_goods3?.onClick {
                        SndoData.reportGoods(shopRecommendedList[2], 2, SndoData.PLACE.item_details_recommend.name)
                        start(this@GoodsDetailActivity, shopRecommendedList[2]._id
                                ?: "", shopRecommendedList[2].item_source
                                ?: "", shopRecommendedList[2])
                    }
                    when (shopRecommendedList.size) {
                        0 -> {
                            goods_shop_recommended_goods1?.visibility = View.GONE
                            goods_shop_recommended_goods2?.visibility = View.GONE
                            goods_shop_recommended_goods3?.visibility = View.GONE

                        }
                        1 -> {
                            goods_shop_recommended_goods1?.visibility = View.VISIBLE
                            goods_shop_recommended_goods2?.visibility = View.INVISIBLE
                            goods_shop_recommended_goods3?.visibility = View.INVISIBLE
                            goods_shop_recommended_goods1?.setData(shopRecommendedList[0])

                        }
                        2 -> {
                            goods_shop_recommended_goods1?.visibility = View.VISIBLE
                            goods_shop_recommended_goods2?.visibility = View.VISIBLE
                            goods_shop_recommended_goods3?.visibility = View.INVISIBLE
                            goods_shop_recommended_goods1?.setData(shopRecommendedList[0])
                            goods_shop_recommended_goods2?.setData(shopRecommendedList[1])
                        }
                        else -> {
                            goods_shop_recommended_goods1?.visibility = View.VISIBLE
                            goods_shop_recommended_goods2?.visibility = View.VISIBLE
                            goods_shop_recommended_goods3?.visibility = View.VISIBLE
                            goods_shop_recommended_goods1?.setData(shopRecommendedList[0])
                            goods_shop_recommended_goods2?.setData(shopRecommendedList[1])
                            goods_shop_recommended_goods3?.setData(shopRecommendedList[2])
                        }
                    }
                }
            }
        } else {
            headTypeOneView.include_goods_shop.visibility = View.GONE
        }
    }

    private fun jumpToPdd() {
        JumpUtil.jumpToPdd(this, mPromotionLinkEntity?.click_url
                ?: (mPromotionLinkEntity?.item_url
                        ?: ""), true)
        StatisticInfo().orderPDD(plate, subPlate, goodsEntity?.item_id, goodsEntity?.item_source, mPromotionLinkEntity?.click_url
                ?: (mPromotionLinkEntity?.item_url
                        ?: ""), "", reportType!!)
    }


    /**
     * 跳转到TB
     */
    private fun jumpToTB(it: NetReqResult) {

        if (it.successful) {
//            val tbPromotionLinkEntity = it.data as TBPromotionLinkEntity
            if (it.extra == true) {
                //代表授权
//                this@GoodsDetailActivity.tbPromotionLinkEntity = tbPromotionLinkEntity
                //下单
                if (mPromotionLinkEntity != null) {
                    LogClient.log(TAG, "跳转淘宝下单,平台=" + item_source + ",商品id=" + goodsId + ",转链信息=" + (mPromotionLinkEntity.toString()))
                } else {
                    LogClient.log(TAG, "跳转淘宝下单,平台=$item_source,商品id=$goodsId")
                }
                when (clickType) {
                    1 -> copyTBPassword()//复制淘口令
                    3 -> ShareFragment.start(this, mPromotionLinkEntity?.code, goodsEntity, getShareImgs(), mPromotionLinkEntity?.share_text, mPromotionLinkEntity?.share_code)//分享
                    else -> {
                        val couponClickUrl = mPromotionLinkEntity?.click_url
                                ?: (mPromotionLinkEntity?.item_url ?: "")
                        StatisticInfo().orderTB(plate, subPlate, goodsEntity?.item_id, goodsEntity?.item_source, couponClickUrl, mPromotionLinkEntity?.relation_id, reportType!!)
                        JumpUtil.jumpToToAli(this@GoodsDetailActivity, couponClickUrl, true)
                    }
                }
            } else {
                //先去授权
                val promotionLinkEntity = it.data as PromotionLinkEntity
                if (promotionLinkEntity != null) {
                    LogClient.log(TAG, "跳转淘宝授权，平台=$item_source,商品id=$goodsId,转链信息=$promotionLinkEntity")
                } else {
                    LogClient.log(TAG, "跳转淘宝授权，平台=$item_source,商品id=$goodsId")
                }
                if (promotionLinkEntity?.auth_url != null) {
                    AliAuthActivity.start(this@GoodsDetailActivity, promotionLinkEntity.auth_url)
                }
            }

        } else {
            showToastShort(it.message)
        }
    }


    /**
     * 添加收藏
     */
    private fun addCollection(it: NetReqResult) {
        if (it.successful) {
            EventBus.getDefault().post(PushEvent(Constant.Event.COLLECTION_CHANGE))
            img_goods_save.isSelected = true
            setImageSave()
            StatisticInfo().collect(plate, subPlate, goodsEntity?.item_id, goodsEntity?.item_source)
        }
        showToastShort(it.message)
    }

    /**
     * 删除收藏，取消收藏
     */
    private fun deleteCollection(it: NetReqResult) {
        if (it.successful) {
            EventBus.getDefault().post(PushEvent(Constant.Event.COLLECTION_CHANGE))
            img_goods_save.isSelected = false
            setImageSave()
        }
        showToastShort(it.message)
    }

    /**
     * 推荐商品加载
     */
    private fun refreshRecommendedData() {
        pageRecommended = 1
        goodsViewModel.doGoodsDetailRecommend(goodsId, rowRecommended, pageRecommended, item_source)
    }

    /**
     *加载更多推荐商商品
     */
    private fun loadMoreRecommendedData() {
        goodsViewModel.doGoodsDetailRecommend(goodsId, rowRecommended, pageRecommended, item_source)
    }

    /**
     * 添加浏览记录，不关心结果
     */
    private fun addGoodsRecode() {
        if (UserClient.isLogin()) {
            //浏览记录，不关心结果
            goodsViewModel.doAddGoodsRecode(goodsId, item_source)
        }
    }

    override fun onDestroy() {
        RecodeHelper.goodsDetailPage.remove(this)
        super.onDestroy()
    }

    /**
     * 初始化View
     */
    private fun initView() {
        val datas = arrayListOf<DetailImageContentBean>()
        detailGoodsPicAdapter = DetailGoodsPicAdapter(datas)
        recycler_view.adapter = detailGoodsPicAdapter
        // 对recycler_view相关参数进行初始化
        var layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@GoodsDetailActivity, 2)
        recycler_view.layoutManager = layoutManager
        val spanSizeLookup: androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup = object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // 根据返回值类型进行处理，return layoutManager.spanCount 代表独占1行。
                if (position == 0) {
                    return layoutManager.spanCount
                }
                val item = detailGoodsPicAdapter.data[position - 1]
                when (item.type) {
                    DetailImageContentBean.DESC_TEXT, DetailImageContentBean.DESC_IMG, DetailImageContentBean.HEADER, DetailImageContentBean.BOTTOM_RECOMMEND_TITLE, DetailImageContentBean.DESC, DetailImageContentBean.END -> return layoutManager.spanCount
                }
                return 1
            }
        }
        layoutManager.spanSizeLookup = spanSizeLookup



        detailGoodsPicAdapter.addHeaderView(headTypeOneView)

        detailGoodsPicAdapter.setOnItemClickListener { adapter, view, position ->
            if (detailGoodsPicAdapter.data[position].type == DetailImageContentBean.BOTTOM) {
                SndoData.reportGoods(detailGoodsPicAdapter.data[position].goodsEntity, position, SndoData.PLACE.item_details_popular.name)
                start(this@GoodsDetailActivity, detailGoodsPicAdapter.data[position].goodsEntity._id, detailGoodsPicAdapter.data[position].goodsEntity.item_source, detailGoodsPicAdapter.data[position].goodsEntity)
            }
        }

        // 隐藏loading界面
        loadingview.visibility = View.GONE

        val itemSource = goodsEntity?.item_source ?: item_source

        when {
            (itemSource ?: "") == Constant.BusinessType.TM -> {
                include_goods_detail_foot.ll_goods_detail_password.visibility = View.VISIBLE
            }
            (itemSource ?: "") == Constant.BusinessType.TB -> {
                include_goods_detail_foot.ll_goods_detail_password.visibility = View.VISIBLE
            }
            else -> {
                include_goods_detail_foot.ll_goods_detail_password.visibility = View.GONE
            }
        }

        if (include_goods_detail_foot != null) {
            include_goods_detail_foot.tv_goods_detail_rebate.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_F73737))
        }
        tv_detail_rebate.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_F73737))
        tv_detail_rebate_down.background.setTintList(ContextCompat.getColorStateList(this@GoodsDetailActivity, R.color.c_F73737))



        recycler_view.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val h = recyclerView.computeVerticalScrollOffset()
                // 当大于titlebar的高度时，显示白色背景titlebar
                if (h > ConvertUtils.dp2px(120F)) {
                    img_goods_back.setImageResource(R.drawable.icon_detail_back_other)
                    tv_title.visibility = View.VISIBLE
                    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    toolbar.background.alpha = 255
                    tv_title.alpha = 1f
                    setImageSave()
                    showRecmImageView()
                } else {
                    val al = 255F / ConvertUtils.dp2px(120F)
                    val alpha = (h * al).toInt()
                    val tvAlpha = h / 255F
                    img_goods_back.setImageResource(R.drawable.icon_detail_back)
                    toolbar.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    toolbar.background.alpha = alpha
                    tv_title.alpha = tvAlpha
                    setImageSave()
                    showRecmImageView()
                }
            }

            override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //获得recyclerView的线性布局管理器
                //获取到第一个item的显示的下标  不等于0表示第一个item处于不可见状态 说明列表没有滑动到顶部 显示回到顶部按钮
                //获得recyclerView的线性布局管理器
                val manager = recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager?
                //获取到第一个item的显示的下标  不等于0表示第一个item处于不可见状态 说明列表没有滑动到顶部 显示回到顶部按钮
                val firstVisibleItemPosition = manager!!.findFirstVisibleItemPosition()
                // 当不滚动时
                if (newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE) {
                    // 判断是否滚动超过一屏
                    if (firstVisibleItemPosition == 0) {
                        img_back_top.visibility = View.GONE
                    } else {
                        //显示回到顶部按钮
                        img_back_top.visibility = View.VISIBLE
                    }
                    //获取RecyclerView滑动时候的状态
                }
            }
        })
        img_back_top.setOnClickListener(View.OnClickListener {
            recycler_view.scrollToPosition(0)
            img_back_top.visibility = View.GONE
        })


//        RecycleViewScrollToTop.addScroolToTop(recycler_view, img_back_top)


    }


    /**
     * 分享赚
     */
    private fun jumpToShare() {
        if (null != goodsEntity) {
            SndoData.event(SndoData.XLT_EVENT_GOODDETAIL_SHARE,
                    SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
                    SndoData.XLT_ITEM_THIRDCate_TITLE, "null",
                    SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
                    SndoData.XLT_GOOD_NAME, goodsEntity?.item_title ?: "null",
                    SndoData.XLT_GOOD_ID, goodsId,
                    SndoData.XLT_ITEM_SOURCE, item_source,
                    SndoData.XLT_ITEM_REBATE_AMOUNT, GoodsMathUtil.calcRebate_amount(goodsEntity)
            )
        }
        if (UserClient.isLogin()) {
            if (mPromotionLinkEntity != null) {
                LogClient.log(TAG, "点击分享赚,平台=" + item_source + ",商品id=" + goodsId + ",转链信息=" + (mPromotionLinkEntity.toString()))
            } else {
                LogClient.log(TAG, "点击分享赚,平台=$item_source,商品id=$goodsId")
            }
            clickType = 3
            if (item_source == Constant.BusinessType.TB || item_source == Constant.BusinessType.TM) {
                //淘宝,天猫
                if (mPromotionLinkEntity?.code.isNullOrBlank()) {
                    showLoadingDialog("加载中")
                    goodsViewModel.doPromotionLink(LINK_TYPE, goodsId, item_source, "", "1")
                } else {
                    ShareFragment.start(this, mPromotionLinkEntity?.code, goodsEntity, getShareImgs(), mPromotionLinkEntity?.share_text, mPromotionLinkEntity?.share_code)
                }
            } else {
                if (mPromotionLinkEntity?.click_url == null) {
                    showLoadingDialog("加载中")
                    goodsViewModel.doPromotionLink(LINK_TYPE, goodsId, item_source, "", "1")
                } else {
                    ShareFragment.start(this,
                            mPromotionLinkEntity?.click_url
                                    ?: (mPromotionLinkEntity?.item_url
                                            ?: ""), goodsEntity, getShareImgs(), mPromotionLinkEntity?.share_text, mPromotionLinkEntity?.share_code)
                }
            }
        } else {
            LoginFragment.start(this@GoodsDetailActivity)
        }
    }

    /**
     * 复制淘口令显示弹窗
     */
    private fun copyTBPassword() {
        if (mPromotionLinkEntity?.code.isNullOrEmpty()) {
            showToastShort("暂无口令")
            return
        }

        //汇报
        StatisticInfo().copyTKL(plate, subPlate, goodsEntity?.item_id, goodsEntity?.item_source, reportType!!)
        val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val password = mPromotionLinkEntity?.share_code
        LogClient.log(TAG, "复制口令,口令=$password")
        cmb.primaryClip = ClipData.newPlainText(null, password)
        CopyPasswordSuccessDialog().show(supportFragmentManager, "CopyPasswordSuccessDialog")

    }

    /**
     * 跳转到第三方app
     */
    private fun jumpToThird() {
        if (!UserClient.isLogin()) {
            LoginFragment.start(this)
            return
        }

        if (mPromotionLinkEntity != null) {
            LogClient.log(TAG, "jumpToThird 跳转下单,平台=" + item_source + ",商品id=" + goodsId + ",转链信息=" + (mPromotionLinkEntity.toString()))
        } else {
            LogClient.log(TAG, "jumpToThird 跳转下单,平台=$item_source,商品id=$goodsId")
        }

        if (item_source == Constant.BusinessType.TB || item_source == Constant.BusinessType.TM) {
            clickType = 2
            val couponClickUrl = this@GoodsDetailActivity.mPromotionLinkEntity?.click_url
                    ?: (mPromotionLinkEntity?.item_url ?: "")
            if (couponClickUrl.isBlank()) {
                showLoadingDialog("加载中")
                goodsViewModel.doPromotionLink(LINK_TYPE, goodsId, item_source, "", "1")
            } else {
                StatisticInfo().orderTB(plate, subPlate, goodsEntity?.item_id, goodsEntity?.item_source, couponClickUrl, mPromotionLinkEntity?.relation_id, reportType!!)
                JumpUtil.jumpToToAli(this@GoodsDetailActivity, couponClickUrl, true)
            }
        } else if (item_source == Constant.BusinessType.PDD) {
            if (mPromotionLinkEntity != null && mPromotionLinkEntity?.click_url != null) {
                jumpToPdd()
            } else {
                showLoadingDialog("加载中")
                goodsViewModel.doPromotionLink(LINK_TYPE, goodsId, item_source, "", "1")
            }
        } else if (item_source == Constant.BusinessType.JD) {
            if (mPromotionLinkEntity != null && mPromotionLinkEntity?.click_url != null) {
                jumpToJDPage()
            } else {
                showLoadingDialog("加载中")
                goodsViewModel.doPromotionLink(LINK_TYPE, goodsId, item_source, "", "1")
            }
        } else if (item_source == Constant.BusinessType.V) {
            if (mPromotionLinkEntity != null && mPromotionLinkEntity?.click_url != null) {
                jumpToV();
            } else {
                showLoadingDialog("加载中")
                goodsViewModel.doPromotionLink(LINK_TYPE, goodsId, item_source, "", "1")
            }
        } else {
            if (mPromotionLinkEntity != null && mPromotionLinkEntity?.click_url != null) {
                JumpUtil.jumpToH5(this, mPromotionLinkEntity?.app_url, mPromotionLinkEntity?.click_url
                        ?: (mPromotionLinkEntity?.item_url ?: ""), true)
            } else {
                showLoadingDialog("加载中")
                goodsViewModel.doPromotionLink(LINK_TYPE, goodsId, item_source, "", "1")
            }
        }

    }


    /**
     * 跳转唯品会界面
     */
    private fun jumpToV() {
        JumpUtil.jumpToVPage(this, mPromotionLinkEntity?.app_url, mPromotionLinkEntity?.click_url
                ?: (mPromotionLinkEntity?.item_url ?: ""), true)
        StatisticInfo().orderJD(plate, subPlate, goodsEntity?.item_id, goodsEntity?.item_source, mPromotionLinkEntity?.click_url
                ?: (mPromotionLinkEntity?.item_url
                        ?: ""), mPromotionLinkEntity?.position_id, reportType!!)
    }

    /**
     * 跳转到京东页面
     */
    private fun jumpToJDPage() {
        JumpUtil.jumpToJdCouponsPage(this, mPromotionLinkEntity?.click_url
                ?: (mPromotionLinkEntity?.item_url ?: ""), mOpenAppAction)
        StatisticInfo().orderJD(plate, subPlate, goodsEntity?.item_id, goodsEntity?.item_source, mPromotionLinkEntity?.click_url
                ?: (mPromotionLinkEntity?.item_url
                        ?: ""), mPromotionLinkEntity?.position_id, reportType!!)
    }


    /**
     * 刷新数据（）
     */
    fun refreshData() {
        detailGoodsPicAdapter.setNewData(null)
        loadGoodsDetailBase = false//需要刷新的话那么需要重新请求接口，防止页面重复渲染
        loadGoodsDetailDes = false//需要刷新的话那么需要重新请求接口，防止页面重复渲染
        //获取商品基础详情
        goodsViewModel.doGoodsDetail(goodsId, item_source, itemId)
        //获取商品详情
        goodsViewModel.doGoodsDetailDesc(goodsId, item_source)
        //获取推荐商品
        refreshRecommendedData()
    }

    /**
     * 没有数据的时候加载刷新下面的推荐商品
     */
    private fun refreshNoGoodsData() {
        page = 1
        loadMoreNoGoodsData()
    }

    private fun loadMoreData() {
        loadMoreNoGoodsData()
    }

    /**
     * 没有数据的时候加载更多下面的推荐商品
     */
    private fun loadMoreNoGoodsData() {
        homeViewModel.likeGoods(page, row)
    }

    //设置banner的adapter
    private fun setBannerAdapter() {
        this@GoodsDetailActivity.headTypeOneView.apply {
            if (hasVideo) {
                tv_video_tag.visibility = View.VISIBLE
                tv_img_tag.visibility = View.VISIBLE
                tv_video_tag.isSelected = true
                tv_img_tag.isSelected = false
            } else {
                tv_video_tag.visibility = View.GONE
                tv_img_tag.visibility = View.GONE
                tv_img_tag.isSelected = true
            }
            tv_video_tag.onClick {
                view_pager.currentItem = 0
            }

            tv_img_tag.onClick {
                if (hasVideo) {
                    view_pager.currentItem = 1
                }
            }
            bannerFragments.clear()
            for (i in 0 until bannerSource.size) {
                val bean = bannerSource[i]
                if (bean.videoBean != null) {
                    bannerFragments.add(PlayerFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable(PlayerFragment.EXTRA_VIDEO_URL, bean.videoBean as VideoBean)
                            putParcelableArrayList(VideoImageDetailActivity.EXTRA_DETAIL_SOURCE, bannerSource)
                        }
                    })
                } else {
                    bannerFragments.add(ImageFragment().apply {
                        arguments = Bundle().apply {
                            putString(ImageFragment.EXTRA_IMAGE_URL, bean.imgUrl ?: "")
                            putInt(ImageFragment.EXTRA_IMAGE_INDEX, i)
                            putParcelableArrayList(VideoImageDetailActivity.EXTRA_DETAIL_SOURCE, bannerSource)
                        }
                    })
                }
            }
            view_pager.apply {
                adapter = object : FragmentPagerAdapter(supportFragmentManager) {
                    override fun getItem(p0: Int): androidx.fragment.app.Fragment = bannerFragments[p0]

                    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                        super.destroyItem(container, position, `object`)
                    }

                    override fun getCount() = bannerSource.size
                }
                offscreenPageLimit = bannerSource.size + 2 //避免No view found for id
                addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(p0: Int) {

                    }

                    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onPageSelected(p0: Int) {
                        this@GoodsDetailActivity.headTypeOneView.apply {
                            if (hasVideo) {
                                if (p0 == 0) {
                                    tv_banner_indicator.visibility = View.GONE
                                } else {
                                    if (bannerSource.size > 1) {
                                        tv_banner_indicator.visibility = View.VISIBLE
                                    }
                                }
                                tv_banner_indicator.text = "$p0/${bannerSource.size - 1}"
                            } else {
                                if (bannerSource.size > 1) {
                                    tv_banner_indicator.visibility = View.VISIBLE
                                    tv_banner_indicator.text = "${p0 + 1}/${bannerSource.size}"
                                }
                            }
                            for (i in 0 until bannerFragments.size) {
                                if (i == p0) {
                                    try {
                                        (bannerFragments[i] as PlayerFragment).start()
                                        tv_video_tag.isSelected = true
                                        tv_img_tag.isSelected = false
                                    } catch (e: Exception) {
                                        //说明不是视频而是图片
                                        tv_video_tag.isSelected = false
                                        tv_img_tag.isSelected = true
                                    }
                                } else {
                                    try {
                                        (bannerFragments[i] as PlayerFragment).pause()
                                    } catch (e: Exception) {
                                        //说明不是视频而是图片
                                    }

                                }
                            }
                        }

                    }
                })
            }
        }

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
    }

    /**
     * 展示网络或者请求错误数据
     */
    private fun showErrorView() {
        personGoodsItemAdapter.setNewData(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        if (event?.action == Constant.Event.AUTH_SUCCESS) {
            showLoadingDialog("加载中")
            goodsViewModel.doPromotionLink(LINK_TYPE, goodsId, item_source, "", "1")
        } else if (event?.action == Constant.Event.LOGIN_SUCCESS) {
            loginAction = true
            refreshData()
            addGoodsRecode()
        } else if (event?.action == Constant.Event.REFRESH_RECM) {
            ll_goods_recommend.visibility = View.GONE
            goodsViewModel.communityGoodsRecm(goodsId, item_source)
        }
    }

    /**
     * 展示正常返回的数据数据
     */
    private fun showRecommendNormalView(data: MutableList<DetailImageContentBean>) {
        data.add(DetailImageContentBean(GoodsEntity(), DetailImageContentBean.END))
        detailGoodsPicAdapter.addData(data)
    }

    /**
     * 展示正常返回的数据数据
     */
    private fun addRecommendData(data: MutableList<DetailImageContentBean>) {
        detailGoodsPicAdapter.addData(data)
    }

    override fun onBackPressedSupport() {
        if (Jzvd.backPress()) {
            return
        }
        if (!RecodeHelper.mainActivityIsExist) {
            // 说明系统中不存在这个activity
            MainActivity.start(this@GoodsDetailActivity)
        }


        return super.onBackPressedSupport()
    }

    override fun finish() {
        super.finish()
        if (loginAction) {
            MainActivity.start(this@GoodsDetailActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        //浏览商品
        if (null != DispatchUtil.taskInfo && TextUtils.equals(DispatchUtil.taskInfo.type, "1")) {
            val taskInfo = DispatchUtil.taskInfo
            DispatchUtil.taskInfo = null
            taskProgressView.setTaskInfo(taskInfo)
        }
    }

    /**
     * 单例方法
     */
    companion object {
        const val TAG = "GoodsDetailActivity"
        const val EXTRA_ID = "EXTRA_ID"
        const val EXTRA_GOODS = "EXTRA_GOODS"
        const val EXTRA_ITEM_SOURCE = "EXTRA_ITEM_SOURCE"
        const val EXTRA_PLATE = "EXTRA_PLATE"
        const val EXTRA_SUB_PLATE = "EXTRA_SUB_PLATE"
        const val EXTRA_REPORT_TYPE = "EXTRA_REPORT_TYPE"
        const val EXTRA_ITEM_ID = "EXTRA_ITEM_ID"

        @JvmField
        val LINK_TYPE = arrayOf("2", "1") //转链接口需要的link_type

        @JvmStatic
        fun start(context: Context?, id: String?, item_source: String?, item_id: String?) {
            context?.startActivity(Intent(context, GoodsDetailActivity::class.java).apply {
                putExtra(EXTRA_ID, id)
                putExtra(EXTRA_ITEM_SOURCE, item_source)
                putExtra(EXTRA_ITEM_ID, item_id)
            })
        }

        @JvmStatic
        fun start(context: Context?, id: String?, item_source: String?, goodsEntity: GoodsEntity?) {
            context?.startActivity(Intent(context, GoodsDetailActivity::class.java).apply {
                putExtra(EXTRA_ID, id)
                putExtra(EXTRA_ITEM_SOURCE, item_source)
                putExtra(EXTRA_GOODS, goodsEntity)
                putExtra(EXTRA_ITEM_ID, goodsEntity?.item_id)
            })
        }

        @JvmStatic
        fun start(context: Context?, id: String, item_source: String?, plate: String?, subPlate: String?, reportType: Int?, item_id: String?) {
            context?.startActivity(Intent(context, GoodsDetailActivity::class.java).apply {
                putExtra(EXTRA_ID, id)
                putExtra(EXTRA_PLATE, plate)
                putExtra(EXTRA_SUB_PLATE, subPlate)
                putExtra(EXTRA_REPORT_TYPE, reportType)
                putExtra(EXTRA_ITEM_SOURCE, item_source)
                putExtra(EXTRA_ITEM_ID, item_id)
            })
        }

        @JvmStatic
        fun start(context: Context?, id: String, item_source: String?, plate: String?, subPlate: String?, reportType: Int?, goodsEntity: GoodsEntity?) {
            context?.startActivity(Intent(context, GoodsDetailActivity::class.java).apply {
                putExtra(EXTRA_ID, id)
                putExtra(EXTRA_PLATE, plate)
                putExtra(EXTRA_SUB_PLATE, subPlate)
                putExtra(EXTRA_REPORT_TYPE, reportType)
                putExtra(EXTRA_ITEM_SOURCE, item_source)
                putExtra(EXTRA_GOODS, goodsEntity)
                putExtra(EXTRA_ITEM_ID, goodsEntity?.item_id)
            })
        }
    }
}