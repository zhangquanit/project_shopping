package com.snqu.shopping.ui.mall.goods

import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.android.util.date.DateFormatUtil
import com.anroid.base.BaseActivity
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.constant.TimeConstants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.goods.bean.DetailImageBean
import com.snqu.shopping.data.goods.bean.DetailImageContentBean
import com.snqu.shopping.data.mall.entity.MallGoodShareInfoEntity
import com.snqu.shopping.data.mall.entity.ShopDetailImageContentBean
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.ui.goods.player.VideoImageDetailActivity
import com.snqu.shopping.ui.login.LoginFragment
import com.snqu.shopping.ui.main.view.TipDialogView
import com.snqu.shopping.ui.mall.goods.adapter.CartCategoryListAdapter
import com.snqu.shopping.ui.mall.goods.adapter.FlowLayoutManager
import com.snqu.shopping.ui.mall.goods.adapter.ShopDetailGoodsPicAdapter
import com.snqu.shopping.ui.mall.goods.fragment.ConfirmOrderFrag
import com.snqu.shopping.ui.mall.goods.fragment.ShopImageFragment
import com.snqu.shopping.ui.mall.goods.fragment.ShopShareFrag
import com.snqu.shopping.ui.mall.viewmodel.MallViewModel
import com.snqu.shopping.util.DateUtil
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.NumberUtil
import com.snqu.shopping.util.ext.onClick
import common.widget.dialog.EffectDialogBuilder
import kotlinx.android.synthetic.main.activity_shop_goods_detail.*
import kotlinx.android.synthetic.main.confirm_order_fragment.*
import kotlinx.android.synthetic.main.home_frag.loadingview
import kotlinx.android.synthetic.main.view_cart_layout.*
import kotlinx.android.synthetic.main.view_cart_layout.cart_num
import kotlinx.android.synthetic.main.view_cart_layout.cart_num_del
import kotlinx.android.synthetic.main.view_cart_layout.cart_num_plus
import kotlinx.android.synthetic.main.view_shop_goods_detail_head.*
import kotlinx.android.synthetic.main.view_shop_goods_detail_head.price
import kotlinx.android.synthetic.main.view_shop_goods_detail_head.tv_title
import kotlin.properties.Delegates


/**
 * 自营商品详情
 */
class ShopGoodsDetailActivity : BaseActivity() {


    companion object {
        const val EXTRA_GOOD_ID = "GOOD_ID"

        @JvmStatic
        fun start(context: Context?, id: String?) {
            context?.startActivity(Intent(context, ShopGoodsDetailActivity::class.java).apply {
                putExtra(EXTRA_GOOD_ID, id)
            })
        }
    }

    //请求自营的ViewModel
    private val mallViewModel by lazy {
        ViewModelProviders.of(this).get(MallViewModel::class.java)
    }

    //商品信息banner
    private val bannerFragments by lazy {
        mutableListOf<ShopImageFragment>()
    }

    //banner数据源
    private val bannerSource by lazy {
        arrayListOf<DetailImageBean>()
    }

    /**
     * 购买数量监听
     */
    private var buyNum: Int by Delegates.observable(0, { _, oldValue, newValue ->
        cart_layout?.let {
            if (totalNum == 0) {
                cart_num_del.setImageResource(R.drawable.cart_del_n)
                cart_num_plus.setImageResource(R.drawable.cart_plus)
            } else {
                cart_num_plus.setImageResource(R.drawable.cart_plus_p)
                if (newValue == 1) {
                    cart_num_del.setImageResource(R.drawable.cart_del_n)
                } else {
                    cart_num_del.setImageResource(R.drawable.cart_del_p)
                    if (newValue == totalNum) {
                        cart_num_plus.setImageResource(R.drawable.cart_plus)
                    }
                }
            }
            // 设置数量
            cart_num.text = newValue.toString()
        }
    })

    /**
     * 库存数量监听
     */
    private var totalNum: Int by Delegates.observable(0, { _, oldValue, newValue ->
        cart_layout?.let {
            if (totalNum == 0) {
                tv_sure.isEnabled = false
                tv_sure.text = "商品已抢光"
                buyNum = 0
            } else {
                tv_sure.isEnabled = true
                tv_sure.text = "确定"
            }
            cart_inv.text = "库存：${totalNum}件"
        }
    })

    // 商品ID
    private var goodsId = ""

    private lateinit var mAdapter: ShopDetailGoodsPicAdapter

    private var shopGoodsEntity: ShopGoodsEntity? = null

    private var mTimer: CountDownTimer? = null

    private var checkIndex = -1  // 规格选中index

    private var checkContent = "" // 规格选中内容

    private var shareInfoEntity: MallGoodShareInfoEntity? = null

    private val headView by lazy {
        layoutInflater.inflate(R.layout.view_shop_goods_detail_head, null)
    }

    private var millisUntilFinished: Long = 0

    override fun getLayoutId(): Int = R.layout.activity_shop_goods_detail

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(mContext, true)

        goodsId = intent?.getStringExtra(EXTRA_GOOD_ID) ?: ""

        if (TextUtils.isEmpty(goodsId)) {
            finish()
        } else {
            // 进行界面初始化
            initView()

            // 加载数据
            millisUntilFinished = 0

            mallViewModel.mNetReqResultLiveData.observe(this, Observer {
                when (it.tag) {
                    ApiHost.MALL_HOME_SHARE -> {
                        closeLoadDialog()
                        if (it.successful) {
                            shareInfoEntity = it.data as MallGoodShareInfoEntity
                            ShopShareFrag.start(mContext, shareInfoEntity)
                        } else {
                            ToastUtils.showShort(it.message)
                        }
                    }
                    ApiHost.MALL_GOOD_DETAIL -> {

                        if (it.successful) {
                            refresh_layout.finishRefresh(true)
                            loadingview.visibility = View.GONE
                            shopGoodsEntity = it.data as ShopGoodsEntity
                            //设置详情数据
                            setDetailView()
                            //设置购物车数据
                            setCartView()
                        } else {
                            loadingview.apply {
                                setStatus(LoadingStatusView.Status.FAIL)
                                setOnBtnClickListener {
                                    loadGoodsData()
                                }
                            }
                            refresh_layout.finishRefresh(false)
                            ToastUtils.showShort(it.message)
                        }
                    }
                }
            })
            loadGoodsData()
        }
    }

    private fun initView() {

        recycler_view.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
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

        cart_close_layout.onClick {
            cart_close.performClick()
        }

        cart_close.onClick {
            hideCartView()
        }


        shop_detail_buy.onClick {
            shopGoodsEntity?.let {
                showCartView()
            }
        }

        img_goods_back.onClick {
            finish()
        }

        shop_tab_home.onClick {
            finish()
        }

        tv_sure.onClick {
            if (UserClient.isLogin()) {
                if (TextUtils.isEmpty(checkContent)) {
                    ToastUtils.showShort("请选择商品规格")
                } else {
                    if (buyNum == 0) {
                        ToastUtils.showShort("购买数量不能为0")
                    } else {
                        ConfirmOrderFrag.start(mContext,
                                shopGoodsEntity?.not_flow
                                        ?: "台湾省、香港特别行政区、澳门特别行政区", shopGoodsEntity?._id
                                ?: "", checkContent, buyNum)
                    }
                }
            } else {
                LoginFragment.start(mContext)
            }
        }

        cart_num_del.onClick {
            if (totalNum > 0) {
                buyNum--
                if (buyNum <= 1) {
                    buyNum = 1
                }
            }
        }

        cart_num_plus.onClick {
            if (totalNum > 0) {
                buyNum++
                if (buyNum > totalNum) {
                    buyNum = totalNum
                    ToastUtils.showShort("数量超过库存，请重新选择")
                }
            }
        }

        shop_share.onClick {
            if (UserClient.isLogin()) {
                if (shareInfoEntity == null) {
                    showLoadingDialog("加载中")
                    mallViewModel.getShareInfo(goodsId)
                } else {
                    ShopShareFrag.start(mContext, shareInfoEntity)
                }
            } else {
                LoginFragment.start(mContext)
            }
        }

        refresh_layout.setOnRefreshListener {
            mallViewModel.getGoodDetail(goodsId)
        }

        val data = arrayListOf<ShopDetailImageContentBean>()
        mAdapter = ShopDetailGoodsPicAdapter(data)
        recycler_view.adapter = mAdapter
        // 对recycler_view相关参数进行初始化
        var layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        recycler_view.layoutManager = layoutManager
        val spanSizeLookup: androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup = object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // 根据返回值类型进行处理，return layoutManager.spanCount 代表独占1行。
                if (position == 0) {
                    return layoutManager.spanCount
                }
                val item = mAdapter.data[position - 1]
                when (item.type) {
                    DetailImageContentBean.DESC_TEXT, DetailImageContentBean.DESC_IMG, DetailImageContentBean.HEADER, DetailImageContentBean.BOTTOM_RECOMMEND_TITLE, DetailImageContentBean.DESC, DetailImageContentBean.END -> return layoutManager.spanCount
                }
                return 1
            }
        }
        layoutManager.spanSizeLookup = spanSizeLookup
        mAdapter.addHeaderView(headView)
    }

    private fun loadGoodsData() {
        loadingview.setStatus(LoadingStatusView.Status.LOADING)
        mallViewModel.getGoodDetail(goodsId)
    }

    private fun setDetailView() {
        headView.apply {
            shopGoodsEntity?.let { it ->
                // 加载banner图,轮播最多8张图片
                loadBannner(it)
                // 商品价格
                val spanUtils = SpanUtils()
                spanUtils.append("¥").setForegroundColor(Color.parseColor("#FFFFFF")).setFontSize(15, true).setBold()
                        .append(it.newPrice.substring(0, it.newPrice.indexOf("."))).setForegroundColor(Color.parseColor("#FFFFFF")).setFontSize(30, true).setBold()
                        .append(it.newPrice.substring(it.newPrice.indexOf("."))).setForegroundColor(Color.parseColor("#FFFFFF")).setFontSize(15, true).setBold()
                if (it.original_price != it.selling_price) {
                    spanUtils
                            .appendSpace(30)
                            .append("¥ ${it.oldPrice}").setForegroundColor(Color.parseColor("#B3FFFFFF")).setFontSize(14, true)
                            .setStrikethrough()
                }
                price.text = spanUtils.create()

                //商品名称，这里为了排版，增加了空格字符串
                tv_title.text = "　　    " + (it.name ?: "")

                //卖点文案，配了就显示，不配就不显示
                if (TextUtils.isEmpty(it.describe)) {
                    tv_desc.visibility = View.GONE
                } else {
                    tv_desc.visibility = View.VISIBLE
                    tv_desc.text = it.describe
                }

                //不支持7天无理由退货、支持7天无理由退货，具体由后台配置，物流不到达区域提示
                //是否支持7天无理由退货 1-是 -1 否
                tv_return_good.text = it.reasonText

                tv_wuliu.onClick {
                    it.not_flow?.let { not_flow ->
                        val tipDialogView = TipDialogView(mContext, "物流无法到达区域", not_flow)
                        EffectDialogBuilder(mContext)
                                .setContentView(tipDialogView)
                                .show()
                    }
                }

                //点击弹出规格页面
                view_cart.onClick {
                    showCartView()
                }

                // 设置商品详情tip
                val tipSpanUtils = SpanUtils()
                tipSpanUtils.append("—").setForegroundColor(Color.parseColor("#B9B9BF")).setFontSize(16, true)
                        .append(" 商品详情 ").setForegroundColor(Color.parseColor("#25282D")).setFontSize(16, true).setBold()
                        .append("—").setForegroundColor(Color.parseColor("#B9B9BF")).setFontSize(16, true)

                shop_goods_detail_tip.text = tipSpanUtils.create()

                //设置商品图片
                it.goods_img_txt?.let { goodsImg ->
                    val detailImageContentBeanList = arrayListOf<ShopDetailImageContentBean>()
                    goodsImg.forEach { imgUrl ->
                        var dettailBean = ShopDetailImageContentBean(imgUrl, ShopDetailImageContentBean.DESC_IMG)
                        detailImageContentBeanList.add(dettailBean)
                    }
                    mAdapter.setNewData(detailImageContentBeanList)
                }
                //根据服务器时间计算倒计时时间
                timer()
            }
        }
    }

    private fun loadBannner(it: ShopGoodsEntity) {
        bannerFragments.clear()
        bannerSource.clear()
        it.banner_img_txt?.forEachIndexed { index, imgUrl ->
            bannerSource.add(DetailImageBean(false, imgUrl
                    ?: "", null))
            bannerFragments.add(ShopImageFragment().apply {
                arguments = Bundle().apply {
                    putString(ShopImageFragment.EXTRA_IMAGE_URL, imgUrl ?: "")
                    putInt(ShopImageFragment.EXTRA_IMAGE_INDEX, index)
                    putParcelableArrayList(VideoImageDetailActivity.EXTRA_DETAIL_SOURCE, bannerSource)
                }
            })
        }
        if (bannerFragments.size > 8) {
            bannerFragments.subList(0, 8)
            bannerSource.subList(0, 8)
        }
        if (bannerFragments.size > 0) {
            tv_shop_banner_indicator.visibility = View.VISIBLE
            tv_shop_banner_indicator.text = "1/${bannerFragments.size}"
        }
        view_pager.apply {
            adapter = object : FragmentPagerAdapter(supportFragmentManager) {
                override fun getItem(index: Int): Fragment = bannerFragments[index]

                override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                    super.destroyItem(container, position, `object`)
                }

                override fun getCount() = bannerFragments.size
            }
            offscreenPageLimit = bannerFragments.size + 1 //避免No view found for id
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {

                }

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                }

                override fun onPageSelected(index: Int) {
                    if (bannerFragments.size > 0) {
                        tv_shop_banner_indicator.visibility = View.VISIBLE
                        tv_shop_banner_indicator.text = "${index + 1}/${bannerFragments.size}"
                    }
                }
            })
        }
    }

    private fun timer() {
        shopGoodsEntity?.let {
            mTimer?.cancel()
            val timestamp = System.currentTimeMillis() / 1000
            var totalTime = (it.hit_end_time - timestamp) * 1000L
            if (totalTime > 0) {
                //如果剩余时间超过7天，从第7天倒计时，到时间后按照剩余时间继续
                // 如果有millisUntilFinished，代表页面没有销毁，继续刚才的计时
                if (this@ShopGoodsDetailActivity.millisUntilFinished > 0) {
                    totalTime = this@ShopGoodsDetailActivity.millisUntilFinished
                }
                mTimer = object : CountDownTimer(totalTime, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        this@ShopGoodsDetailActivity.millisUntilFinished = millisUntilFinished
                        val hour = millisUntilFinished / 1000 / 60 / 60
                        val minute = millisUntilFinished / 1000 / 60 % 60
                        val second = millisUntilFinished / 1000 % 60
                        if (hour >= 24) {
                            tv_time.text = "距结束剩${hour/24}天${hour%24}:${minute}:${second}"
                        } else {
                            tv_time.text = "距结束剩${hour}:${minute}:${second}"
                        }
                    }

                    override fun onFinish() {
                        tv_time.text = "秒杀结束"
                    }
                }
                mTimer?.start()
            }
        }
    }

    //显示购物车
    private fun showCartView() {
        cart_layout.isClickable = true
        cart_layout.visibility = View.VISIBLE
        val showAction = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f)
        showAction.duration = 300
        shop_cart_view.startAnimation(showAction)
    }

    //隐藏购物车
    private fun hideCartView() {
        val closeAction = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f)
        closeAction.duration = 300
        closeAction.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                cart_layout.isClickable = false
                cart_layout.visibility = View.GONE
            }
        })
        shop_cart_view.startAnimation(closeAction)
    }

    //为购物车添加内容
    private fun setCartView() {

        shopGoodsEntity?.let { entity ->

            // 购物车-图片，使用banner列表中的最后一张
            entity.banner_img_txt?.let {
                if (it.size > 0) {
                    GlideUtil.loadPic(cart_img, it[it.lastIndex], R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
                }
            }
            //售价
            entity.selling_price.let {
                val spanUtils = SpanUtils()
                spanUtils.append("¥").setForegroundColor(Color.parseColor("#FF2A38")).setFontSize(18, true).setBold()
                        .append(" ").setForegroundColor(Color.parseColor("#FF2A38")).setFontSize(13, true).setBold()
                        .append(NumberUtil.saveTwoPoint(it)).setForegroundColor(Color.parseColor("#FF2A38")).setFontSize(25, true).setBold()
                cart_price.text = spanUtils.create()
            }
            //库存
            entity.inv.let {
                totalNum = it
            }
            //生成分类
            entity.standard?.let {
                checkIndex = -1
                buyNum = 1
                val categoryListAdapter = CartCategoryListAdapter(it)
                cart_categorys.layoutManager = FlowLayoutManager(mContext, true)
                cart_categorys.adapter = categoryListAdapter
                categoryListAdapter.setListener { oldPos, newPos ->
                    if (oldPos != -1) {
                        val tv = cart_categorys.getChildAt(oldPos).findViewById<TextView>(R.id.cart_category_name)
                        tv?.let { view ->
                            view.setBackgroundResource(R.drawable.shop_cb_false)
                            view.setTextColor(Color.parseColor("#25282D"))
                        }
                    }
                    checkContent = categoryListAdapter.data[newPos].name
                    totalNum = categoryListAdapter.data[newPos].inv
                    if (totalNum > 0) {
                        buyNum = 1
                    }
                    val spanUtils = SpanUtils()
                    spanUtils.append("¥").setForegroundColor(Color.parseColor("#FF2A38")).setFontSize(18, true).setBold()
                            .append(" ").setForegroundColor(Color.parseColor("#FF2A38")).setFontSize(13, true).setBold()
                            .append(NumberUtil.saveTwoPoint(entity.standard[newPos].selling_price)).setForegroundColor(Color.parseColor("#FF2A38")).setFontSize(25, true).setBold()
                    cart_price.text = spanUtils.create()
                }
            }

        }
    }


    override fun onPause() {
        super.onPause()
        mTimer?.cancel()
    }

    override fun onResume() {
        super.onResume()
        timer()
    }

    override fun onBackPressedSupport() {
        if (cart_layout.visibility == View.VISIBLE) {
            hideCartView()
        } else {
            finish()
        }
    }

}

