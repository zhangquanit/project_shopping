package com.snqu.shopping.data.red;

import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.data.home.entity.ShopItemEntity;
import com.snqu.shopping.data.red.entity.BigVEntity;
import com.snqu.shopping.data.red.entity.RedGoodeEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author 张全
 */
public interface RedApi {

    /**
     * 爆款分类商品列表
     *
     * @param url
     * @return
     */
    @GET
    Observable<ResponseDataArray<GoodsEntity>> getGoodList(@Url String url);

    /**
     * 爆款分类
     *
     * @return
     */
    @GET(ApiHost.RED_CATEGORY)
    Observable<ResponseDataArray<CategoryEntity>> getCategoryList();

    /**
     * 好物说
     *
     * @return
     */
    @GET(ApiHost.RED_HAOWU)
    Observable<ResponseDataArray<RedGoodeEntity>> getRedGoods(@Query("page") int page, @Query("row") int row);


    /**
     * 网红店列表
     *
     * @return
     */
    @GET(ApiHost.RED_SHOPS)
    Observable<ResponseDataArray<ShopItemEntity>> getRedShops(@Query("page") int page, @Query("row") int row);

    /**
     * 店铺详情
     *
     * @param seller_shop_id
     * @return
     */
    @GET(ApiHost.RED_SHOP_DETAIL)
    Observable<ResponseDataObject<ShopItemEntity>> getShopDetail(@Query("seller_shop_id") String seller_shop_id, @Query("item_source") String item_source);

    /**
     * 大V列表
     *
     * @return
     */
    @GET(ApiHost.RED_BIGV)
    Observable<ResponseDataArray<BigVEntity>> getBigVList(@Query("page") int page, @Query("row") int row);


    /**
     * 大V推荐商品
     *
     * @return
     */
    @GET
    Observable<ResponseDataArray<GoodsEntity>> getBigVGoodList(@Url String url);


}
