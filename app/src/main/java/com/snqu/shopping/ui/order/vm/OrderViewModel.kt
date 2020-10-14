package com.snqu.shopping.ui.order.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.snqu.shopping.common.viewmodel.BaseAndroidViewModel
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.base.BaseResponseObserver
import com.snqu.shopping.data.base.HttpResponseException
import com.snqu.shopping.data.base.NetReqResult
import com.snqu.shopping.data.base.ResponseDataObject
import com.snqu.shopping.data.order.OrderClient
import com.snqu.shopping.data.order.entity.OrderEntity
import com.snqu.shopping.data.order.entity.OrderShareCodeEntity

/**
 * desc:
 * time: 2019/8/30
 * @author 银进
 */
class OrderViewModel : BaseAndroidViewModel {
    constructor(application: Application) : super(application)
    val dataResult = MutableLiveData<NetReqResult>()
    val orderItemSource = MutableLiveData<String>()
    val orderMonth = MutableLiveData<String>()

    /**
     * 获取订单列表 单个订单 加_id参数即可
     * item_source C淘宝 B天猫 D京东
     * status 9384订单状态：1即将到账、2已到账、3已失效
     * year_month 2018-09
     */
    fun doOrderList(item_source: String?, status: Int?, row: Int?, page: Int?, yearMonth: String?,keyword:String?,retrieve_order_id:String?){
        executeNoMapHttp(OrderClient.doOrderList(item_source, status, row, page, yearMonth,keyword,retrieve_order_id), object :BaseResponseObserver<ResponseDataObject<List<OrderEntity>>>(){
            override fun onSuccess(value:ResponseDataObject<List<OrderEntity>>?) {
                dataResult.value=NetReqResult(ApiHost.ORDER_LIST, "", true,value?.data?: arrayListOf<OrderEntity>()).apply {
                    extra=status
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value=NetReqResult(ApiHost.ORDER_LIST, e?.alert, false).apply {
                    extra=status
                }
            }

            override fun onEnd() {
            }

        })
    }

/**
     * 获取订单列表 单个订单 加_id参数即可
     * item_source C淘宝 B天猫 D京东
     * status 9384订单状态：1即将到账、2已到账、3已失效
     * year_month 2018-09
     */
    fun doOrderListGroup(item_source: String?, status: Int?, row: Int?, page: Int?, yearMonth: String?,keyword:String?,retrieve_order_id:String?){
        executeNoMapHttp(OrderClient.doOrderListGroup(item_source, status, row, page, yearMonth,keyword,retrieve_order_id), object :BaseResponseObserver<ResponseDataObject<List<OrderEntity>>>(){
            override fun onSuccess(value:ResponseDataObject<List<OrderEntity>>?) {
                dataResult.value=NetReqResult(ApiHost.ORDER_LIST_GROUP, "", true,value?.data?: arrayListOf<OrderEntity>()).apply {
                    extra=status
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value=NetReqResult(ApiHost.ORDER_LIST_GROUP, e?.alert, false).apply {
                    extra=status
                }
            }

            override fun onEnd() {
            }

        })
    }

    /**
     * 获取邀请链接
     */
    fun doOrderShareCode(order_id: String?){
        executeNoMapHttp(OrderClient.doOrderShareCode(order_id), object :BaseResponseObserver<ResponseDataObject<OrderShareCodeEntity>>(){
            override fun onSuccess(value: ResponseDataObject<OrderShareCodeEntity>?) {
                if (value == null) {
                    dataResult.value = NetReqResult(ApiHost.ORDER_SHARE_CODE, value?.message, false)
                } else {
                    dataResult.value = NetReqResult(ApiHost.ORDER_SHARE_CODE, "", true, value.data)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value=NetReqResult(ApiHost.ORDER_SHARE_CODE, e?.alert, false)
            }

            override fun onEnd() {
            }

        })
    }

    /**
     * 订单找回
     */
    fun doOrderRetrieve(order_id: List<String>?){
        executeNoMapHttp(OrderClient.doOrderRetrieve(order_id), object :BaseResponseObserver<ResponseDataObject<Any>>(){
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value=NetReqResult(ApiHost.ORDER_RETRIEVE, "找回成功，请到星乐桃订单中查看", true)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value=NetReqResult(ApiHost.ORDER_RETRIEVE, e?.alert, false)
            }

            override fun onEnd() {
            }

        })
    }
}