package com.snqu.shopping.ui.vip.order.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.common.viewmodel.BaseAndroidViewModel
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.base.BaseResponseObserver
import com.snqu.shopping.data.base.HttpResponseException
import com.snqu.shopping.data.base.NetReqResult
import com.snqu.shopping.data.base.ResponseDataObject
import com.snqu.shopping.data.goods.GoodsClient
import com.snqu.shopping.data.goods.entity.VipGoodsAddressEntity
import com.snqu.shopping.data.goods.entity.VipOrderEntity
import com.snqu.shopping.util.pay.OrderPayResponse
import org.greenrobot.eventbus.EventBus

/**
 * desc:viewModel
 * time: 2019/12/2
 * @author 银进
 */
class VipViewModel : BaseAndroidViewModel {
    constructor(application: Application) : super(application)

    val dataResult = MutableLiveData<NetReqResult>()

    /**
     * 获取收货地址
     */
    fun doVipGoodsAddress() {
        executeNoMapHttp(GoodsClient.doVipGoodsAddress(), object : BaseResponseObserver<ResponseDataObject<VipGoodsAddressEntity?>>() {
            override fun onSuccess(value: ResponseDataObject<VipGoodsAddressEntity?>) {
                if (value.isSuccessful && value.data != null) {
                    dataResult.value = NetReqResult(ApiHost.VIP_GOODS_ADDRESS, "", true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.VIP_GOODS_ADDRESS, "", false)
                }

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.VIP_GOODS_ADDRESS, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 保存/修改收货地址
     */
    fun doVipGoodsAddressChange(vipGoodsAddressEntity: VipGoodsAddressEntity) {
        executeNoMapHttp(GoodsClient.doVipGoodsAddressChange(vipGoodsAddressEntity), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>) {
                if (value.isSuccessful) {
                    EventBus.getDefault().post(PushEvent(Constant.Event.CHANGE_ADDRESS_SUCCESS))
                    dataResult.value = NetReqResult(ApiHost.VIP_GOODS_ADDRESS_CHANGE, "", true)
                } else {
                    dataResult.value = NetReqResult(ApiHost.VIP_GOODS_ADDRESS_CHANGE, "", false)
                }

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.VIP_GOODS_ADDRESS_CHANGE, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * VIP商品列表
     */
    fun doVipGoodsList(page: Int, row: Int) {
        executeNoMapHttp(GoodsClient.doVipGoodsList(page, row), object : BaseResponseObserver<ResponseDataObject<List<VipOrderEntity>>>() {
            override fun onSuccess(value: ResponseDataObject<List<VipOrderEntity>>) {
                if (value.isSuccessful) {
                    EventBus.getDefault().post(PushEvent(Constant.Event.CHANGE_ADDRESS_SUCCESS))
                    dataResult.value = NetReqResult(ApiHost.VIP_GOODS_LIST, "", true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.VIP_GOODS_LIST, "", false)
                }

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.VIP_GOODS_LIST, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 下VIP订单
     */
    fun doVipGoodsBuy(pay_way: String, goods_id: String, trade_type: String) {
        executeNoMapHttp(GoodsClient.doVipGoodsBuy(pay_way, goods_id, trade_type), object : BaseResponseObserver<ResponseDataObject<OrderPayResponse>>() {
            override fun onSuccess(value: ResponseDataObject<OrderPayResponse>) {
                if (value.isSuccessful) {
                    dataResult.value = NetReqResult(ApiHost.VIP_GOODS_BUY, "", true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.VIP_GOODS_BUY, "", false)
                }

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.VIP_GOODS_BUY, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }


}