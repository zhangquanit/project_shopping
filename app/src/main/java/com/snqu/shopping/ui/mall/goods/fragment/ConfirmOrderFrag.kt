package com.snqu.shopping.ui.mall.goods.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.SparseArray
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SpanUtils
import com.blankj.utilcode.util.ToastUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.base.NetReqResult
import com.snqu.shopping.data.mall.entity.PayDataEntity
import com.snqu.shopping.data.mall.entity.PayResultDataEntity
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity
import com.snqu.shopping.data.mall.entity.address.AddressEntity
import com.snqu.shopping.ui.main.view.TipDialogView
import com.snqu.shopping.ui.mall.address.AddressManagerFrag
import com.snqu.shopping.ui.mall.viewmodel.AddressViewModel
import com.snqu.shopping.ui.mall.viewmodel.MallViewModel
import com.snqu.shopping.util.CommonUtil
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.NumberUtil
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.pay.OrderPay
import common.widget.dialog.EffectDialogBuilder
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.confirm_order_fragment.*
import kotlinx.android.synthetic.main.confirm_order_fragment.price
import kotlinx.android.synthetic.main.view_shop_goods_detail_head.*
import kotlinx.android.synthetic.main.view_shop_order_user_type.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.properties.Delegates

/**
 * 确认订单
 */
class ConfirmOrderFrag : SimpleFrag() {

    companion object {
        const val EXTRA_NOT_FLOW = "not_flow" //物流不能达到的区域
        const val EXTRA_GOOD_ID = "good_id" //商品ID
        const val EXTRA_CATEGORY = "category"//规格
        const val EXTRA_NUMBER = "number" //购买数量

        @JvmStatic
        fun start(context: Context?, not_flow: String,
                  good_id: String, category: String, number: Int) {
            val fragParam = SimpleFragAct.SimpleFragParam("确认订单",
                    ConfirmOrderFrag::class.java, Bundle().apply {
                putString(EXTRA_NOT_FLOW, not_flow)
                putString(EXTRA_GOOD_ID, good_id)
                putString(EXTRA_CATEGORY, category)
                putInt(EXTRA_NUMBER, number)
            })
            SimpleFragAct.start(context, fragParam)
        }
    }

    //请求自营的ViewModel
    private val mallViewModel by lazy {
        ViewModelProviders.of(this).get(MallViewModel::class.java)
    }

    private val addressViewModel by lazy {
        ViewModelProviders.of(this).get(AddressViewModel::class.java)
    }

    // 物流不能到达的区域
    private val noFlow by lazy {
        arguments?.getString(EXTRA_NOT_FLOW) ?: ""
    }

    private val good_id by lazy {
        arguments?.getString(EXTRA_GOOD_ID) ?: ""
    }

    private val category by lazy {
        arguments?.getString(EXTRA_CATEGORY) ?: ""
    }

    private val num by lazy {
        arguments?.getInt(EXTRA_NUMBER) ?: 0
    }

    private var mLoadingDialog: LoadingDialog? = null

    private var addressItemSelect = false

    var liveData = MutableLiveData<NetReqResult>()

    private var shopGoodsEntity: ShopGoodsEntity? = null

    private var payResultDataEntity: PayResultDataEntity? = null

    /**
     * 购买数量监听
     */
    private var buyNum: Int by Delegates.observable(0, { _, oldValue, newValue ->
        if (totalNum == 1) {
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
        changeGoodPrice()
    })

    private var totalNum = 0;//库存量

    private val payDataEntity = PayDataEntity()

    private val userTypes: SparseArray<PayDataEntity.UserType> = SparseArray()

    override fun getLayoutId(): Int = R.layout.confirm_order_fragment

    override fun init(savedInstanceState: Bundle?) {
        // 沉浸式状态栏设置
        StatusBar.setStatusBar(mContext, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)

        addAction(Constant.Event.ADDRESS_MANAGER_ITEM)
        addAction(Constant.Event.ADDRESS_UPDATE)
        addAction(Constant.Event.ORDER_BUY_SUCCESS);
        addAction(Constant.Event.ORDER_BUY_CANCEL);
        addAction(Constant.Event.ORDER_BUY_FAIL);

        //设定事件
        settingListener()

        //设置数据
        buyNum = num
        addressItemSelect = false

        // 加载数据
        loadingData()

        mallViewModel.mNetReqResultLiveData.observe(this, Observer {
            when (it.tag) {
                ApiHost.MALL_ORDER_GOPAY -> {
                    closeLoadDialog()
                    if (it.successful) {
                        payResultDataEntity = it.data as PayResultDataEntity
                        payResultDataEntity?.let { pay ->
                            pay.sign?.let { sign ->
                                OrderPay().alipay(mContext, sign)
                            }
                        }
                    } else {
                        ToastUtils.showShort(it.message)
                    }

                }
            }
        })

        addressViewModel.mNetReqResultLiveData.observe(this, Observer { it ->
            if (TextUtils.equals(it.tag, AddressViewModel.TAG_LIST)) {
                if(!addressItemSelect) {
                    var data = it.data as? List<AddressEntity>
                    data?.let { data ->
                        if (data.isNotEmpty()) {
                            addressViewModel.updateUserAddress(data)
                            shopGoodsEntity?.addressEntity = data[0]
                            updateAddress()
                        } else {
                            shopGoodsEntity?.addressEntity = null
                            no_address_layout.visibility = View.VISIBLE
                            address_layout.visibility = View.GONE
                        }
                    }
                }
            }
        })

        liveData.observe(this, Observer {
            when (it.tag) {
                ApiHost.MALL_ORDER_BUYNOW -> {
                    if (it.successful) {
                        loadingview.visibility = View.GONE
                        shopGoodsEntity = it.data as ShopGoodsEntity
                        generateOrderView()
                    } else {
                        loadingview.apply {
                            setStatus(LoadingStatusView.Status.FAIL)
                            setOnBtnClickListener {
                                loadingData()
                            }
                        }
                        ToastUtils.showShort(it.message)
                    }
                }
            }
        })


    }

    private fun settingListener() {
        no_address_layout.onClick {
            address_layout.performClick()
//            AddressAddFrag.start(mContext)
        }
        address_layout.onClick {
            addressItemSelect = false
            AddressManagerFrag.startForOrder(mContext, null)
        }
        cart_num_del.onClick {
            buyNum--
            if (buyNum <= 1) {
                buyNum = 1
            }
        }
        cart_num_plus.onClick {
            buyNum++
            if (buyNum > totalNum) {
                buyNum = totalNum
                ToastUtils.showShort("数量超过库存，请重新选择")
            }
        }
        //支付
        go_pay.onClick {

            if (!CommonUtil.checkAliPayInstalled(mContext)) { //未安装支付宝
                ToastUtil.show(R.string.alipay_not_support)
            } else {
                //检查订单需要的参数是否齐全
                shopGoodsEntity?.let { goodsEntity ->
                    //1.检测是否填写了地址，为1的时候才进行检查。
                    if (goodsEntity.is_address == 1) {
                        if (goodsEntity.addressEntity == null || goodsEntity.addressEntity._id == null) {
                            ToastUtil.show("请选择地址")
                            return@onClick
                        } else {
                            //2.检测地址是否在可到达区域
                            val flowList = noFlow.split("、")
                            goodsEntity.addressEntity.provinces?.let {
                                if (it.size > 0) {
                                    var province = it[0]
                                    if (province in flowList) {
                                        val tipDialogView = TipDialogView(mContext, "物流无法到达区域", "请更换地址，或在商品详情页查看物流配送范围")
                                        EffectDialogBuilder(mContext)
                                                .setContentView(tipDialogView)
                                                .show()
                                        return@onClick
                                    }
                                }
                            }
                        }
                    }

                    //3.检测是否有必填项，如果有，需要填写才能生成订单
                    goodsEntity.user_type_in?.let {
                        if (it.size != userTypes.size()) {
                            ToastUtil.show("您还有输入项未填写")
                            return@onClick
                        } else {
                            val dataList = ArrayList<PayDataEntity.UserType>()
                            // 输入项
                            it.forEachIndexed { index, s ->
                                dataList.add(userTypes[index])
                            }
                            payDataEntity.user_type_in = dataList
                        }
                    }

                    // 商品ID
                    payDataEntity._id = goodsEntity._id
                    // 购买数量
                    payDataEntity.number = buyNum
                    // 规格名
                    payDataEntity.standard_name = goodsEntity.standard_name
                    // 支付方式,默认为支付宝
                    payDataEntity.pay_type = "2"
                    // 地址ID
                    payDataEntity.address_id = goodsEntity.addressEntity._id
                }
                showLoadingDialog("生成订单中，请稍等...")
                mallViewModel.goPay(payDataEntity, liveData)
            }
        }
    }

    private fun loadingData() {
        loadingview.setStatus(LoadingStatusView.Status.LOADING)
        mallViewModel.orderBuy(good_id, category, buyNum, liveData)
    }

    private fun generateOrderView() {
        shopGoodsEntity?.let { goodsEntity ->
            //1. 显示地址，有默认地址，展示默认地址，无地址，显示“请选择地址”：
            updateAddress()

            //2.动态添加输入框
            order_user_type_layout.visibility = View.GONE
            goodsEntity.user_type_in?.let { data ->
                if (data.size > 0) {
                    order_user_type_layout.visibility = View.VISIBLE
                    order_user_type_layout.removeAllViews()
                    val maxLengh = data.maxBy {
                        it.length
                    }?.length
                    data.forEachIndexed { index, userType ->
                        //让输入框之间对齐
                        var userKey = userType
                        maxLengh?.let {
                            if (it > userType.length) {
                                val space = StringBuffer()
                                val len = it - userType.length
                                for (i in 0 until len) {
                                    space.append("  ")
                                }
                                userKey += space.toString()
                            }
                        }
                        userKey?.let {
                            val itemView = View.inflate(mContext, R.layout.view_shop_order_user_type, null)
                            itemView.apply {
                                tv_user_key.text = userKey
                                edit_user_type.hint = "请输入${userKey}"
                                if (index == data.lastIndex) {
                                    user_type_line.visibility = View.GONE
                                }
                                edit_user_type.addTextChangedListener(object : TextWatcher {
                                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                                    }

                                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                    }

                                    override fun afterTextChanged(s: Editable?) {
                                        if (!TextUtils.isEmpty(s)) {
                                            val key = edit_user_type.hint.toString().replace("请输入", "")
                                            val value = s.toString()
                                            val userType = PayDataEntity.UserType(key, value)
                                            userTypes.put(index, userType)
                                        }
                                    }
                                })
                            }
                            order_user_type_layout.addView(itemView)
                        }
                    }
                }
            }

            //3. 加载商品信息
            goodsEntity.banner_img_txt?.let {
                GlideUtil.loadPic(order_good_pic, it[it.lastIndex], R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)
            }
            order_good_name.text = goodsEntity.name ?: ""
            tv_order_category.text = goodsEntity.standard_name ?: ""
            cart_num.text = goodsEntity.number.toString()
            SpanUtils().let {
                it.append(NumberUtil.saveTwoPoint(goodsEntity.selling_price))
                        .setForegroundColor(Color.parseColor("#FF25282D"))
                        .setFontSize(16, true)
                if (goodsEntity.selling_price != goodsEntity.original_price) {
                    it.append("  ").append(NumberUtil.saveTwoPoint(goodsEntity.original_price))
                            .setForegroundColor(Color.parseColor("#FFC3C4C7"))
                            .setFontSize(12, true)
                            .setStrikethrough()
                }
                tv_order_good_price.text = it.create()
            }

            buyNum = goodsEntity.number
            totalNum = goodsEntity.inv

            //4.生成商品总额
            changeGoodPrice()

            //6.生成7天退款提示
            tv_return.visibility = View.GONE
            when (goodsEntity.serven_reason) {
                -1 -> {
                    tv_return.visibility = View.VISIBLE
                    tv_return_good.text = "支持7天无理由退货"
                }
                1 -> {
                    tv_return.visibility = View.VISIBLE
                    tv_return_good.text = "不支持7天无理由退货"
                }
            }
        }
    }

    private fun updateAddress() {
        if(shopGoodsEntity!=null){
            when (shopGoodsEntity?.is_address) {
                //是否显示地址 1-是 -1 否
                -1 -> {
                    root_address_layout.visibility = View.GONE
                }
                1 -> {
                    root_address_layout.visibility = View.VISIBLE
                    //表示有默认地址
                    if (shopGoodsEntity?.addressEntity != null) {
                        no_address_layout.visibility = View.GONE
                        address_layout.visibility = View.VISIBLE
                        val address = "地址：${shopGoodsEntity?.addressEntity?.address_txt ?: ""}"
                        order_address_phone.text = "${shopGoodsEntity?.addressEntity?.name ?: ""}  ${shopGoodsEntity?.addressEntity?.phone_txt}"
                        order_address_name.text = address
                    } else {
                        no_address_layout.visibility = View.VISIBLE
                        address_layout.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun changeGoodPrice() {
        shopGoodsEntity?.let { good ->
            val priceText = NumberUtil.saveTwoPoint(good.selling_price * buyNum)
            val priceSpanUtil = SpanUtils()
            priceSpanUtil
                    .append("需付款：")
                    .setForegroundColor(Color.parseColor("#FF131413"))
                    .setFontSize(13, true)
                    .append(priceText)
                    .setForegroundColor(Color.parseColor("#FFFD2921"))
                    .setFontSize(16, true)
                    .let {
                        new_price.text = it.create()
                    }
            price.text = priceText
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        when (event?.action) {
            //跳转支付中间页
            Constant.Event.ORDER_BUY_SUCCESS, Constant.Event.ORDER_BUY_CANCEL, Constant.Event.ORDER_BUY_FAIL -> {
                OrderMiddlePageFrag.start(mContext, payResultDataEntity?._id ?: "", event.action)
                finish()
            }
            Constant.Event.ADDRESS_MANAGER_ITEM -> {
                //编辑地址
                val addressEntity = event.data as? AddressEntity
                shopGoodsEntity?.addressEntity = addressEntity
                updateAddress()
                addressItemSelect = true
            }
            Constant.Event.ADDRESS_UPDATE -> {
                addressViewModel.getAddressList()
            }

        }
    }


    fun showLoadingDialog(content: String?) {
        mLoadingDialog = LoadingDialog.showBackCancelableDialog(mContext, content)
    }


    fun closeLoadDialog() {
        mLoadingDialog?.dismiss()
    }


}