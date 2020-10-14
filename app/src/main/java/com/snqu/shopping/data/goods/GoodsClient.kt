package com.snqu.shopping.data.goods

import android.text.TextUtils
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.DataConfig
import com.snqu.shopping.data.base.ResponseDataArray
import com.snqu.shopping.data.base.ResponseDataObject
import com.snqu.shopping.data.base.RestClient.getService
import com.snqu.shopping.data.goods.bean.GoodsParamBean
import com.snqu.shopping.data.goods.entity.*
import com.snqu.shopping.data.home.entity.CommunityEntity
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.util.pay.OrderPayResponse
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * desc:
 * time: 2019/8/28
 * @author 银进
 */
object GoodsClient {


    /**
     * 获取我推荐的奖励信息
     */
    fun getMyGoodRecmInfo(): Observable<ResponseDataObject<GoodRecmInfoEntity>> {
        return getService(GoodsApi::class.java).getMyGoodRecmInfo()
    }

    /**
     * 获取问题反馈列表
     *
     * @return
     */
    fun getMyGoodRecmList(queryParam: GoodsQueryParam): Observable<ResponseDataArray<CommunityEntity>> {
        val sb = StringBuffer(DataConfig.API_HOST + ApiHost.GET_MY_GOOD_RECM_LIST).append("?")
        sb.append("page=" + queryParam.page)
        sb.append("&row=10")
        if (null != queryParam.sort && queryParam.sort != GoodsQueryParam.Sort.NONE) {
            sb.append("&sort=", queryParam.sort.value)
        }
        if (!TextUtils.isEmpty(queryParam.goodsStatus)) {
            sb.append("page=" + queryParam.goodsStatus)
        }
        if (!TextUtils.isEmpty(queryParam.goodsDate)) {
            sb.append("&date=" + queryParam.goodsDate)
        }
        return getService(GoodsApi::class.java).getMyGoodsReamList(sb.toString())
    }

    /**
     * 提交好物推荐
     */
    fun submitGoodRecm(pathList: List<String>, share_content: String, goods_id: String, item_source: String, item_id: String, tomorrow: Int
    ): Observable<ResponseDataObject<Any>> {
        val goodRecmBody = GoodRecmBody()
        goodRecmBody.images = pathList.toTypedArray()
        goodRecmBody.share_content = share_content
        goodRecmBody.item_id = item_id
        goodRecmBody.goods_id = goods_id
        goodRecmBody.item_source = item_source
        goodRecmBody.tomorrow = tomorrow
        return getService<GoodsApi>(GoodsApi::
        class.java).submitGoodRecm(goodRecmBody)
    }

    fun uploadGoodsRecm(file: File, type: String): Observable<ResponseDataArray<GoodsRecmEntity>> {
        val files: Map<String, RequestBody> = HashMap()
        var media_type = ""
        if (type == "images") {
            media_type = "image/*"
        }
        val uploadBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("save_type", type)
                .addFormDataPart("files", file.name, RequestBody.create(MediaType.parse(media_type), file))
                .build()
        return getService<GoodsApi>(GoodsApi::class.java).uploadGoodsRecm(uploadBody.part(0), uploadBody.part(1))
    }

    /**
     * 添加收藏
     *
     * @return
     */
    fun doAddCollectionGoods(id: String, item_source: String?): Observable<ResponseDataObject<Any>> = getService<GoodsApi>(GoodsApi::class.java).addCollectionGoods(id, item_source)

    /**
     * 删除我的推荐
     */
    fun delGoodRecm(recm_uid: String): Observable<ResponseDataObject<Any>> {
        return getService<GoodsApi>(GoodsApi::class.java).delGoodRecm(recm_uid)
    }

    /**
     * 删除收藏
     *
     * @return
     */
    fun doDeleteCollectionGoods(ids: String): Observable<ResponseDataObject<Any>> = getService<GoodsApi>(GoodsApi::class.java).deleteCollectionGoods(ids)

    /**
     * 删除收藏更具商品id
     *
     * @return
     */
    fun deleteCollectionGoodsItem(id: String, item_source: String?): Observable<ResponseDataObject<Any>> = getService<GoodsApi>(GoodsApi::class.java).deleteCollectionGoodsItem(id, item_source)

    /**
     * 清除失效收藏
     *
     * @return
     */
    fun doClearFailCollectionGoods(): Observable<ResponseDataObject<Any>> = getService<GoodsApi>(GoodsApi::class.java).clearFailCollectionGoods()

    /**
     * 获取收藏列表
     *
     * @return
     */
    fun doCollectionGoodsList(): Observable<ResponseDataObject<CollectionListGoodsEntity>> = getService<GoodsApi>(GoodsApi::class.java).collectionGoodsList()

    /**
     * 获取商品基础详情数据
     *
     * @return
     */
    fun doGoodsDetail(id: String, item_source: String?, item_id: String): Observable<ResponseDataObject<GoodsEntity?>> = getService<GoodsApi>(GoodsApi::class.java).goodsDetail(id, UserClient.getUser()?._id, item_source, item_id)

    fun doGoodsDetailByItemId(itemId: String, item_source: String?): Observable<ResponseDataObject<GoodsEntity?>> = getService<GoodsApi>(GoodsApi::class.java).goodsDetailByItemId(itemId, UserClient.getUser()?._id, item_source)

    /**
     * 获取商品更新|获取推荐语
     */
    fun getGoodsRecText(goods_id: String, item_source: String?): Observable<ResponseDataObject<GoodsRecmText>> {
        return getService<GoodsApi>(GoodsApi::class.java).getGoodsRecText(goods_id, item_source)
    }

    /**
     * 获取商品详情数据
     *
     * @return
     */
    fun doGoodsDetailDesc(id: String, item_source: String?): Observable<ResponseDataObject<GoodsEntity?>> = getService<GoodsApi>(GoodsApi::class.java).goodsDetailDesc(id, item_source)

    /**
     * 商品列表获取
     */
    fun getGoodsList(page: Int, source_type0: String, source_type1: String, tid: String,
                     item_source: String, category_id: String, item_category_id: String,
                     nine_cid: String): Observable<ResponseDataArray<GoodsEntity?>> {
        val goodsParamBean = GoodsParamBean()
        goodsParamBean.page = page.toString()
        goodsParamBean.pageSize = "10"
        goodsParamBean.source_type = arrayOf("1", "4")
        goodsParamBean.tid = tid
        goodsParamBean.item_source = item_source
        goodsParamBean.nine_cid = nine_cid
        val map = HashMap<String, String>()
        map["page"] = goodsParamBean.page
        map["pageSize"] = goodsParamBean.pageSize
        map["tid"] = goodsParamBean.tid
        map["item_source"] = goodsParamBean.item_source
        map["nine_cid"] = goodsParamBean.nine_cid
        map["source_type[0]"] = "1"
        map["source_type[1]"] = "4"
        return getService(GoodsApi::class.java).getGoodsList(map)
    }

    /**
     * 猜你喜欢
     */
    fun getLikeGoodsList(queryParam: GoodsQueryParam, type_1: String, type_2: String): Observable<ResponseDataArray<GoodsEntity?>> {
        val map = HashMap<String, String>()
        map["page"] = queryParam.page.toString()
        map["pageSize"] = "10"
        map["tid"] = "1"
        map["item_source"] = queryParam.item_source;
//        map["nine_cid"] = goodsParamBean.nine_cid
        map["source_type[0]"] = type_1
        map["source_type[1]"] = type_2
        return getService(GoodsApi::class.java).getGoodsList(map)
    }

    /**
     * 是否已收藏-已登录才请求
     *
     * @return
     */
    fun doGoodsFav(id: String, item_source: String?): Observable<ResponseDataObject<GoodsFavEntity?>> = getService<GoodsApi>(GoodsApi::class.java).goodsFav(id, item_source)

    /**
     * 解析商品url 支持京东普通商品地址
     */
//    fun decodeGoodsByUrl(url: String, need_get_info: Int): Observable<ResponseDataObject<GoodsEntity>> = getService<GoodsApi>(GoodsApi::class.java).goodsDecodeByUrl(url, need_get_info)

    /**
     * 淘口令解析
     */
    fun decodeGoodsByCode(code: String, need_get_info: Int, is_serch: String): Observable<ResponseDataObject<GoodsEntity>> = getService<GoodsApi>(GoodsApi::class.java).goodsDecodeByCode(code, need_get_info, is_serch)

    /**
     * 获取商品评论
     */
    fun doGoodsRates(id: String, type: Int?, row: Int, page: Int, item_source: String?): Observable<ResponseDataObject<List<RateBase>>> = getService<GoodsApi>(GoodsApi::class.java).goodsRates(id, type, row, page, item_source)

    /**
     * 获取商品评论数量
     */
    fun doGoodsRatesCount(id: String, item_source: String?): Observable<ResponseDataObject<RateBaseCountEntity>> = getService<GoodsApi>(GoodsApi::class.java).goodsRatesCount(id, item_source)

    /**
     * 获取商品详情页推荐商品
     */
    fun doGoodsDetailRecommend(id: String, row: Int, page: Int, item_source: String?): Observable<ResponseDataObject<List<GoodsEntity>>> = getService<GoodsApi>(GoodsApi::class.java).goodsDetailRecommend(id, row, page, item_source)

    /**
     * 淘宝/天猫授权
     */
    fun authTaoBao(url: String): Observable<ResponseDataObject<Any>> = getService<GoodsApi>(GoodsApi::class.java).authTaobao(url)

    /**
     * 添加商品浏览记录
     */
    fun doAddGoodsRecode(id: String?, item_source: String?): Observable<ResponseDataObject<Any>> = getService<GoodsApi>(GoodsApi::class.java).addGoodsRecode(id, item_source)

    /**
     * 转跳商品链接
     */
    @JvmStatic
    fun doPromotionLink(promotionLinkBodyEntity: PromotionLinkBodyEntity): Observable<ResponseDataObject<PromotionLinkEntity>> {

//        val promotionLinkBodyEntity = PromotionLinkBodyEntity(arrayOf("2","3"),"39997","","P","1")
        return getService<GoodsApi>(GoodsApi::
        class.java).promotionLink(promotionLinkBodyEntity)
    }


//........VIP商品.........
    /**
     * 获取VIP商品详情
     *
     * @return
     */
    fun doVipGoodsDetail(id: String): Observable<ResponseDataObject<VipGoodsEntity?>> = getService<GoodsApi>(GoodsApi::class.java).vipGoodsDetail(id)

    /**
     * 获取收货地址
     *
     * @return
     */
    fun doVipGoodsAddress(): Observable<ResponseDataObject<VipGoodsAddressEntity?>> = getService<GoodsApi>(GoodsApi::class.java).vipGoodsAddress()

    /**
     * 保存/修改收货地址
     *
     * @return
     */
    fun doVipGoodsAddressChange(vipGoodsAddressEntity: VipGoodsAddressEntity): Observable<ResponseDataObject<Any>> = getService<GoodsApi>(GoodsApi::class.java).vipGoodsAddressChange(vipGoodsAddressEntity)

    /**
     * VIP商品列表
     *
     * @return
     */
    fun doVipGoodsList(page: Int, row: Int): Observable<ResponseDataObject<List<VipOrderEntity>>> = getService<GoodsApi>(GoodsApi::class.java).vipGoodsList(page, row)

    /**
     * 下VIP订单
     *
     * @return
     */
    fun doVipGoodsBuy(pay_way: String, goods_id: String, trade_type: String): Observable<ResponseDataObject<OrderPayResponse>> = getService<GoodsApi>(GoodsApi::class.java).vipGoodsBuy(pay_way, goods_id, trade_type)

    /**
     * 判断商品是否可以推荐
     */
    fun communityGoodsRecm(goods_id: String, item_source: String): Observable<ResponseDataObject<GoodRecmEntity>> {
        return getService<GoodsApi>(GoodsApi::class.java).communityGoodsRecm(goods_id, item_source)
    }
}