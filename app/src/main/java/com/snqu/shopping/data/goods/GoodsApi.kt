package com.snqu.shopping.data.goods

import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.base.ResponseDataArray
import com.snqu.shopping.data.base.ResponseDataObject
import com.snqu.shopping.data.goods.entity.*
import com.snqu.shopping.data.home.entity.CommunityEntity
import com.snqu.shopping.util.pay.OrderPayResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * desc:
 * time: 2019/8/28
 * @author 银进
 */
interface GoodsApi {

    /**
     *     上传好物推荐图片
     */
    @Multipart
    @POST(ApiHost.UPFILE_COMMUNITY_GOODS_RECM)
    fun uploadGoodsRecm(@Part file: MultipartBody.Part?, @Part file2: MultipartBody.Part?): Observable<ResponseDataArray<GoodsRecmEntity>>

    /**
     * 提交商品推荐
     * @return
     */
    @POST(ApiHost.POST_SHARE_GOOD_RECM)
    fun submitGoodRecm(@Body goodRecmBody: GoodRecmBody): Observable<ResponseDataObject<Any>>

    /**
     * 获取我推荐的商品列表
     */
    /**
     * @Query("page") page: Int, @Query("pageSize") pageSize: Int,
    @Query("sort") time: String?
    ,@Query("status") status: String,
    @Query("date") date: String
     */
    @GET
    fun getMyGoodsReamList(@Url url: String): Observable<ResponseDataArray<CommunityEntity>>

    /**
     * 获取我推荐的奖励信息
     */
    @GET(ApiHost.GET_MY_GOOD_RECM_INFO)
    fun getMyGoodRecmInfo(): Observable<ResponseDataObject<GoodRecmInfoEntity>>

    /**
     * 添加收藏
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.ADD_COLLECTION_GOODS)
    fun addCollectionGoods(@Field("id") id: String, @Field("item_source") item_source: String?): Observable<ResponseDataObject<Any>>

    /**
     * 删除我的推荐
     */
    @FormUrlEncoded
    @POST(ApiHost.DEL_GOOD_RECM)
    fun delGoodRecm(@Field("recm_id") recm_uid: String): Observable<ResponseDataObject<Any>>

    /**
     * 删除收藏
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.DELETE_COLLECTION_GOODS)
    fun deleteCollectionGoods(@Field("ids") ids: String): Observable<ResponseDataObject<Any>>


    /**
     * 删除收藏更具商品id
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.DELETE_COLLECTION_GOODS_ITEM)
    fun deleteCollectionGoodsItem(@Field("id") ids: String, @Field("item_source") item_source: String?): Observable<ResponseDataObject<Any>>

    /**
     * 清除失效收藏
     *
     * @return
     */
    @POST(ApiHost.CLEAR_FAIL_COLLECTION_GOODS)
    fun clearFailCollectionGoods(): Observable<ResponseDataObject<Any>>

    /**
     * 获取收藏列表
     *
     * @return
     */
    @GET(ApiHost.COLLECTION_GOODS_LIST)
    fun collectionGoodsList(): Observable<ResponseDataObject<CollectionListGoodsEntity>>

    /**
     * 获取商品基础详情数据
     *
     * @return
     */
    @GET(ApiHost.GOODS_DETAIL)
    fun goodsDetail(@Query("id") id: String, @Query("user_id") user_id: String?, @Query("item_source") item_source: String?, @Query("item_id") item_id: String?): Observable<ResponseDataObject<GoodsEntity?>>

    /**
     * 更新|获取推荐语
     */
    @GET(ApiHost.GOODS_REC_TEXT)
    fun getGoodsRecText(@Query("goods_id") goods_id: String, @Query("item_source") item_source: String?): Observable<ResponseDataObject<GoodsRecmText>>

    @GET(ApiHost.GOODS_DETAIL)
    fun goodsDetailByItemId(@Query("item_id") id: String, @Query("user_id") user_id: String?, @Query("item_source") item_source: String?): Observable<ResponseDataObject<GoodsEntity?>>

    /**
     * 获取商品详情数据
     *
     * @return
     */
    @GET(ApiHost.GOODS_DETAIL_DESC)
    fun goodsDetailDesc(@Query("id") id: String, @Query("item_source") item_source: String?): Observable<ResponseDataObject<GoodsEntity?>>

    /**
     * 商品列表获取
     */
//    @GET(ApiHost.GET_GOODS_LIST+"?source_type[0]={type1}")
//    fun getGoosList(@Path("page") page: Int,
//                    @Path("pageSize") pageSize: Int,
//                    @Path("type1") source: String = "1",
//                    @Path("source_type[1]") source1: String = "4",
//                    @Path("tid") tid: String,
//                    @Path("item_source") item_source: String,
//                    @Path("category_id") category_id: String,
//                    @Path("item_category_id") item_category_id: String,
//                    @Path("nine_cid") nine_cid: String): Observable<ResponseDataArray<GoodsEntity?>>

    @GET(ApiHost.GET_GOODS_LIST)
    fun getGoodsList(@QueryMap(encoded = true) options: Map<String, String>): Observable<ResponseDataArray<GoodsEntity?>>


//    @GET(ApiHost.GET_GOODS_LIST)
//    fun getGoodsList(@Body goodsParamBean: GoodsParamBean): Observable<ResponseDataArray<GoodsEntity?>>

//    @POST(ApiHost.GET_GOODS_LIST)
//    fun getGoodsList(@Body goodsParamBean: GoodsParamBean): Observable<ResponseDataArray<GoodsEntity?>>

//    @GET(ApiHost.RED_SHOPS)
//    fun getRedShops(@Query("page") page: Int, @Query("row") row: Int): Observable<ResponseDataArray<ShopItemEntity?>?>?

    /**
     * 3.是否已收藏-已登录才请求
     *
     * @return
     */
    @GET(ApiHost.GOODS_FAV)
    fun goodsFav(@Query("id") id: String, @Query("item_source") item_source: String?): Observable<ResponseDataObject<GoodsFavEntity?>>


    /**
     * 解析商品url 支持京东普通商品地址
     */
//    @FormUrlEncoded
//    @POST(ApiHost.GOODS_DEURL)
//    fun goodsDecodeByUrl(@Field("url") url: String, @Field("need_get_info") need_get_info: Int): Observable<ResponseDataObject<GoodsEntity>>

    /**
     * 解析淘口令
     */
    @FormUrlEncoded
    @POST(ApiHost.GOODS_DECODE)
    fun goodsDecodeByCode(@Field("code") code: String, @Field("need_get_info") need_get_info: Int, @Field("is_serch") is_serch: String = "0"): Observable<ResponseDataObject<GoodsEntity>>

    /**
     * 获取商品评论
     */
    @GET(ApiHost.GOODS_RATES)
    fun goodsRates(@Query("id") id: String, @Query("type") type: Int?, @Query("row") row: Int, @Query("page") page: Int, @Query("item_source") item_source: String?): Observable<ResponseDataObject<List<RateBase>>>

    /**
     * 获取商品评论数量
     */
    @GET(ApiHost.GOODS_RATES_COUNT)
    fun goodsRatesCount(@Query("id") id: String, @Query("item_source") item_source: String?): Observable<ResponseDataObject<RateBaseCountEntity>>

    /**
     * 获取商品详情页推荐商品
     */
    @GET(ApiHost.GOODS_DETAIL_RECOMMEND)
    fun goodsDetailRecommend(@Query("id") id: String, @Query("row") row: Int, @Query("page") page: Int, @Query("item_source") item_source: String?): Observable<ResponseDataObject<List<GoodsEntity>>>

    /**
     * 转商品链接
     */
    @POST(ApiHost.PROMOTION_LINK)
    fun promotionLink(@Body promotionLinkEntity: PromotionLinkBodyEntity): Observable<ResponseDataObject<PromotionLinkEntity>>
//    fun promotionLink(@Body  @Field("link_type") link_type: String, @Field("tid") tid: String, @Field("link_url") link_url: String
//                      , @Field("item_source") item_source: String, @Field("need_code") need_code: String): Observable<ResponseDataObject<PromotionLinkEntity>>

    /**
     * 淘宝/天猫授权
     */
    @GET
    fun authTaobao(@Url url: String): Observable<ResponseDataObject<Any>>

    /**
     * 添加商品浏览记录
     */
    @FormUrlEncoded
    @POST(ApiHost.GOODS_FOOT_RECODE)
    fun addGoodsRecode(@Field("_id") id: String?, @Field("item_source") item_source: String?): Observable<ResponseDataObject<Any>>

//    ..............VIP商品...................
    /**
     * 获取VIP商品详情
     *
     * @return
     */
    @GET(ApiHost.VIP_GOODS_DETAIL)
    fun vipGoodsDetail(@Query("_id") id: String): Observable<ResponseDataObject<VipGoodsEntity?>>

    /**
     * 获取收货地址
     *
     * @return
     */
    @GET(ApiHost.VIP_GOODS_ADDRESS)
    fun vipGoodsAddress(): Observable<ResponseDataObject<VipGoodsAddressEntity?>>

    /**
     * 保存/修改收货地址
     *
     * @return
     */
    @POST(ApiHost.VIP_GOODS_ADDRESS_CHANGE)
    fun vipGoodsAddressChange(@Body vipGoodsAddressEntity: VipGoodsAddressEntity): Observable<ResponseDataObject<Any>>


    /**
     *
     *VIP商品列表
     * @return
     */
    @GET(ApiHost.VIP_GOODS_LIST)
    fun vipGoodsList(@Query("page") page: Int, @Query("row") row: Int): Observable<ResponseDataObject<List<VipOrderEntity>>>

    /**
     * 下VIP订单
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.VIP_GOODS_BUY)
    fun vipGoodsBuy(@Field("pay_way") pay_way: String, @Field("goods_id") goods_id: String, @Field("trade_type") trade_type: String): Observable<ResponseDataObject<OrderPayResponse>>

    /**
     * 判断商品是否可以推荐
     */
    @GET(ApiHost.COMMUNITY_GOODS_RECM)
    fun communityGoodsRecm(@Query("goods_id") goods_id: String, @Query("item_source") item_source: String): Observable<ResponseDataObject<GoodRecmEntity>>

}