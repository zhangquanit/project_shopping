package com.snqu.shopping.data.red;

import android.text.TextUtils;

import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.DataConfig;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.base.RestClient;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.data.home.entity.ShopItemEntity;
import com.snqu.shopping.data.red.entity.BigVEntity;
import com.snqu.shopping.data.red.entity.RedGoodeEntity;

import io.reactivex.Observable;

/**
 * 红人街
 *
 * @author 张全
 */
public class RedClient {

    private static RedApi getApi() {
        return RestClient.getService(RedApi.class);
    }


    /**
     * 爆款分类
     *
     * @return
     */
    public static Observable<ResponseDataArray<CategoryEntity>> getCategoryList() {
        return getApi().getCategoryList();
    }

    /**
     * 网红爆款商品
     *
     * @return
     */
    public static Observable<ResponseDataArray<GoodsEntity>> getGoodList(GoodsQueryParam goodsQueryParam) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.RED_GOODS).append("?");
        sb.append("page=" + goodsQueryParam.page);
        sb.append("&row=" + goodsQueryParam.row);

        if (!TextUtils.isEmpty(goodsQueryParam.id)) {
            sb.append("&category_id=" + goodsQueryParam.id);
        }

        if (!TextUtils.isEmpty(goodsQueryParam.item_source)) {
            sb.append("&item_source=" + goodsQueryParam.item_source);
        }

        if (goodsQueryParam.postage == 1) {
            sb.append("&postage=1");
        }
        return getApi().getGoodList(sb.toString());
    }

    /**
     * 网红店铺
     *
     * @param page
     * @return
     */
    public static Observable<ResponseDataArray<ShopItemEntity>> getRedShops(int page) {
        return getApi().getRedShops(page, 10);
    }

    /**
     * 店铺详情
     *
     * @param id
     * @return
     */
    public static Observable<ResponseDataObject<ShopItemEntity>> getShopDetail(String id,String item_source) {
        return getApi().getShopDetail(id,item_source);
    }

    /**
     * 大V列表
     *
     * @param page
     * @return
     */
    public static Observable<ResponseDataArray<BigVEntity>> getBigVList(int page) {
        return getApi().getBigVList(page, 10);
    }

    /**
     * 好物说
     *
     * @return
     */
    public static Observable<ResponseDataArray<RedGoodeEntity>> getRedGoods(int page) {
        return getApi().getRedGoods(page, 10);
    }

    /**
     * 大V推荐商品
     *
     * @param queryParam
     * @return
     */
    public static Observable<ResponseDataArray<GoodsEntity>> getBigVGoodList(GoodsQueryParam queryParam) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.RED_BIGV_GOODS).append("?");
        sb.append("page=" + queryParam.page);
        sb.append("&row=" + queryParam.row);

        if (!TextUtils.isEmpty(queryParam.id)) {
            sb.append("&certified_id=" + queryParam.id);
        }

        if (null != queryParam.sort && queryParam.sort != GoodsQueryParam.Sort.NONE) {
            sb.append("&sort=").append(queryParam.sort.value);
        }
        if (!TextUtils.isEmpty(queryParam.item_source)) {
            sb.append("&item_source=").append(queryParam.item_source);
        }
        if (queryParam.postage > 0) {
            sb.append("&postage=1");
        }

        return getApi().getBigVGoodList(sb.toString());

    }


}
