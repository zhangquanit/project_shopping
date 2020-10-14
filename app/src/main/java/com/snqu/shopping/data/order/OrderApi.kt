package com.snqu.shopping.data.order

import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.base.ResponseDataObject
import com.snqu.shopping.data.order.entity.OrderEntity
import com.snqu.shopping.data.order.entity.OrderShareCodeEntity
import com.snqu.shopping.data.order.request.FindOrderRequest
import io.reactivex.Observable
import retrofit2.http.*

/**
 * desc:
 * time: 2019/8/30
 * @author 银进
 */
interface OrderApi {

    /**
     * 获取订单列表 单个订单 加_id参数即可
     *item_source C淘宝 B天猫 D京东
     *status 9384订单状态：1即将到账、2已到账、3已失效
     *year_month 2018-09
     * @return
     */
    @GET(ApiHost.ORDER_LIST)
    fun orderList(@Query("item_source") item_source: String?, @Query("status") status: Int?, @Query("row") row: Int?, @Query("page") page: Int?, @Query("year_month") yearMonth: String?, @Query("keyword") keyword: String?, @Query("retrieve_order_id") retrieve_order_id: String?): Observable<ResponseDataObject<List<OrderEntity>>>
    /**
     * 获取订单列表 单个订单 加_id参数即可团队
     *item_source C淘宝 B天猫 D京东
     *status 9384订单状态：1即将到账、2已到账、3已失效
     *year_month 2018-09
     * @return
     */
    @GET(ApiHost.ORDER_LIST_GROUP)
    fun orderListGroup(@Query("item_source") item_source: String?, @Query("status") status: Int?, @Query("row") row: Int?, @Query("page") page: Int?, @Query("year_month") yearMonth: String?, @Query("keyword") keyword: String?, @Query("retrieve_order_id") retrieve_order_id: String?): Observable<ResponseDataObject<List<OrderEntity>>>

    /**
     * 添加分享记录
     *
     */
    @FormUrlEncoded
    @POST(ApiHost.ORDER_SHARE_CODE)
    fun orderShareCode(@Field("order_id") order_id: String?): Observable<ResponseDataObject<OrderShareCodeEntity>>




    /**
     * 订单找回
     *
     */
    @POST(ApiHost.ORDER_RETRIEVE)
    fun orderRetrieve(@Body  order_id: FindOrderRequest): Observable<ResponseDataObject<Any>>
}