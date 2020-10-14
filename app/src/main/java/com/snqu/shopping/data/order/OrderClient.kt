package com.snqu.shopping.data.order

import android.text.TextUtils
import com.android.util.ext.SPUtil
import com.snqu.shopping.data.base.ResponseDataObject
import com.snqu.shopping.data.base.RestClient
import com.snqu.shopping.data.order.entity.OrderEntity
import com.snqu.shopping.data.order.entity.OrderShareCodeEntity
import com.snqu.shopping.data.order.request.FindOrderRequest
import io.reactivex.Observable
import java.util.*

/**
 * desc:订单
 * time: 2019/8/30
 * @author 银进
 */
object OrderClient {
    /**
     * 获取订单列表 单个订单 加_id参数即可
     * item_source C淘宝 B天猫 D京东
     * status 9384订单状态：1即将到账、2已到账、3已失效
     * year_month 2018-09
     */
    fun doOrderList(item_source: String?, status: Int?, row: Int?, page: Int?, yearMonth: String?, keyword: String?, retrieve_order_id: String?): Observable<ResponseDataObject<List<OrderEntity>>> = RestClient.getService<OrderApi>(OrderApi::class.java).orderList(item_source, status, row, page, yearMonth, keyword, retrieve_order_id)

  /**
     * 获取订单列表 单个订单 加_id参数即可团队
     * item_source C淘宝 B天猫 D京东
     * status 9384订单状态：1即将到账、2已到账、3已失效
     * year_month 2018-09
     */
    fun doOrderListGroup(item_source: String?, status: Int?, row: Int?, page: Int?, yearMonth: String?, keyword: String?, retrieve_order_id: String?): Observable<ResponseDataObject<List<OrderEntity>>> = RestClient.getService<OrderApi>(OrderApi::class.java).orderListGroup(item_source, status, row, page, yearMonth, keyword, retrieve_order_id)

    /**
     * 分享的邀请码
     */
    fun doOrderShareCode(order_id: String?): Observable<ResponseDataObject<OrderShareCodeEntity>> = RestClient.getService<OrderApi>(OrderApi::class.java).orderShareCode(order_id)



 /**
     * 订单找回
     */
 fun doOrderRetrieve(order_id: List<String>?): Observable<ResponseDataObject<Any>> = RestClient.getService<OrderApi>(OrderApi::class.java).orderRetrieve(FindOrderRequest(order_id
         ?: arrayListOf()))

    //-----------------------------本地缓存--------------
    private val SEARCH_HISTORY = "ORDER_SEARCH_HISTORY"

    /**
     * 获取历史搜索
     *
     * @return
     */
    fun getSearchHistory(): List<String> {
        var searchList: List<String> = ArrayList()
        val flightHistory = SPUtil.getString(SEARCH_HISTORY, null)
        if (TextUtils.isEmpty(flightHistory)) {
            return searchList
        }
        val items = flightHistory.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        searchList = Arrays.asList(*items)

        //倒序
        Collections.reverse(searchList)
        //去重
        var listTemp: MutableList<String> = ArrayList()
        for (i in searchList.indices) {
            if (!listTemp.contains(searchList[i])) {
                listTemp.add(searchList[i])
            }
        }
        //最多10个
        if (listTemp.size > 10) {
            listTemp = listTemp.subList(0, 10)

            //只保留10个
            val savedList = ArrayList(listTemp)
            Collections.reverse(savedList)
            var stringBuffer = StringBuffer()
            for (item in savedList) {
                stringBuffer.append(item).append(",")
            }
            stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length - 1)
            SPUtil.setString(SEARCH_HISTORY, stringBuffer.toString())
        }

        return listTemp
    }

    /**
     * 保存搜索历史
     */
    fun addSearchHistory(keyword: String) {
        var searchHistory = SPUtil.getString(SEARCH_HISTORY, null)
        if (!TextUtils.isEmpty(searchHistory)) {
            searchHistory += ",$keyword"
        } else {
            searchHistory = keyword
        }
        SPUtil.setString(SEARCH_HISTORY, searchHistory)
    }

    /**
     * 清除搜索历史
     */
    fun clearSearchHistory() {
        SPUtil.setString(SEARCH_HISTORY, null)
    }

    //-----------------------------本地缓存团队订单搜索--------------
    private val GROUP_SEARCH_HISTORY = "GROUP_SEARCH_HISTORY"

    /**
     * 获取历史搜索
     *
     * @return
     */
    fun getGroupSearchHistory(): List<String> {
        var searchList: List<String> = ArrayList()
        val flightHistory = SPUtil.getString(GROUP_SEARCH_HISTORY, null)
        if (TextUtils.isEmpty(flightHistory)) {
            return searchList
        }
        val items = flightHistory.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        searchList = Arrays.asList(*items)

        //倒序
        Collections.reverse(searchList)
        //去重
        var listTemp: MutableList<String> = ArrayList()
        for (i in searchList.indices) {
            if (!listTemp.contains(searchList[i])) {
                listTemp.add(searchList[i])
            }
        }
        //最多10个
        if (listTemp.size > 10) {
            listTemp = listTemp.subList(0, 10)

            //只保留10个
            val savedList = ArrayList(listTemp)
            Collections.reverse(savedList)
            var stringBuffer = StringBuffer()
            for (item in savedList) {
                stringBuffer.append(item).append(",")
            }
            stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length - 1)
            SPUtil.setString(GROUP_SEARCH_HISTORY, stringBuffer.toString())
        }

        return listTemp
    }

    /**
     * 保存搜索历史
     */
    fun addGroupSearchHistory(keyword: String) {
        var searchHistory = SPUtil.getString(GROUP_SEARCH_HISTORY, null)
        if (!TextUtils.isEmpty(searchHistory)) {
            searchHistory += ",$keyword"
        } else {
            searchHistory = keyword
        }
        SPUtil.setString(GROUP_SEARCH_HISTORY, searchHistory)
    }

    /**
     * 清除搜索历史
     */
    fun clearGroupSearchHistory() {
        SPUtil.setString(GROUP_SEARCH_HISTORY, null)
    }
}