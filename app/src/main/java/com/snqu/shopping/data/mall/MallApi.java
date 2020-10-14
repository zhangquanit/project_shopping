package com.snqu.shopping.data.mall;

import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.SearchSlugEntity;
import com.snqu.shopping.data.mall.entity.MallBannerEntity;
import com.snqu.shopping.data.mall.entity.MallCategoryEntity;
import com.snqu.shopping.data.mall.entity.MallGoodShareInfoEntity;
import com.snqu.shopping.data.mall.entity.MallOrderDetailEntity;
import com.snqu.shopping.data.mall.entity.MallOrderEntity;
import com.snqu.shopping.data.mall.entity.MallRecommendEntity;
import com.snqu.shopping.data.mall.entity.PayDataEntity;
import com.snqu.shopping.data.mall.entity.PayResultDataEntity;
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity;
import com.snqu.shopping.data.mall.entity.address.AddressEntity;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface MallApi {

    /**
     * 获取banner
     *
     * @return
     */
    @GET(ApiHost.MALL_BANNER)
    Observable<ResponseDataObject<MallBannerEntity>> getBanner();

    /**
     * banner点击汇报
     *
     * @return
     */
    @POST(ApiHost.MALL_BANNER_REPORT)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> bannerReport(@Field("_id") String _id);


    /**
     * 获取分类
     *
     * @return
     */
    @GET(ApiHost.MALL_CATEGORY)
    Observable<ResponseDataArray<MallCategoryEntity>> getCategory();

    /**
     * 获取推荐
     *
     * @return
     */
    @GET(ApiHost.MALL_RECOMMEND)
    Observable<ResponseDataArray<MallRecommendEntity>> getRecommend(@Query("page") int page, @Query("row") int row);


    @GET(ApiHost.MALL_CATEGORY_REFER)
    Observable<ResponseDataArray<ShopGoodsEntity>> getCategeoryGoods(@Query("_id") String _id, @Query("search") String search, @Query("page") int page, @Query("row") int row);

    /**
     * 商品详情
     */
    @GET(ApiHost.MALL_GOOD_DETAIL)
    Observable<ResponseDataObject<ShopGoodsEntity>> getGoodDetail(@Query("_id") String _id);


    /**
     * 订单去支付
     *
     * @param payDataEntity
     * @return
     */
    @POST(ApiHost.MALL_ORDER_GOPAY)
    Observable<ResponseDataObject<PayResultDataEntity>> goPay(@Body PayDataEntity payDataEntity);

    /**
     * 根据订单编号再次支付
     *
     * @return
     */
    @POST(ApiHost.MALL_ORDER_RE_PAY)
    @FormUrlEncoded
    Observable<ResponseDataObject<PayResultDataEntity>> goRePay(@Field("_id") String _id);

    /**
     * 商品分享
     *
     * @param _id
     * @return
     */
    @GET(ApiHost.MALL_HOME_SHARE)
    Observable<ResponseDataObject<MallGoodShareInfoEntity>> getShareInfo(@Query("_id") String _id);


    @POST(ApiHost.MALL_ORDER_RE_PAY)
    @FormUrlEncoded
    Observable<ResponseDataObject<PayResultDataEntity>> goRePay(@Field("_id") String _id, @Field("address_id") String address_id);

    /**
     * 立即购买
     *
     * @return
     */
    @POST(ApiHost.MALL_ORDER_BUYNOW)
    @FormUrlEncoded
    Observable<ResponseDataObject<ShopGoodsEntity>> orderBuy(@Field("_id") String _id, @Field("standard_name") String standard_name, @Field("number") int number);

    /**
     * 订单地址列表
     */
    @GET(ApiHost.ADDRESS_LIST)
    Observable<ResponseDataArray<AddressEntity>> getAddress();

    /**
     * 搜索关键词
     */
    @GET(ApiHost.MALL_SEARCH_WORDS)
    Observable<ResponseDataArray<SearchSlugEntity>> searchSlugList(@Query("search") String search);

    /**
     * 订单列表
     *
     * @return
     */
    @GET
    Observable<ResponseDataArray<MallOrderEntity>> orderList(@Url String url);

    /**
     * 取消订单
     *
     * @param _id
     * @return
     */
    @POST(ApiHost.MALL_ORDER_CANCEL)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> orderCancel(@Field("_id") String _id);

    /**
     * 订单详情
     *
     * @param _id
     * @return
     */
    @GET(ApiHost.MALL_ORDER_DETAIL)
    Observable<ResponseDataObject<MallOrderDetailEntity>> orderDetail(@Query("_id") String _id);


    /**
     * 订单评价
     *
     * @return
     */
    @POST(ApiHost.MALL_ORDER_COMMENT)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> orderComment(@Field("_id") String id, @Field("goods") float goods, @Field("flow") float flow, @Field("service") float service, @Field("content") String content);

    /**
     * 订单-确认收货
     */
    @POST(ApiHost.MALL_ORDER_AFFIRM)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> orderReceipt(@Field("_id") String _id);


    /**
     * 获取店铺推荐商品
     *
     * @return
     */
    @GET(ApiHost.SHOP_RECOMMEND)
    Observable<ResponseDataObject<List<GoodsEntity>>> shopRecommend(@Query("id") String id, @Query("goods_id") String goods_id, @Query("item_source") String item_source);


}
