package com.snqu.shopping.ui.main.frag.channel.reds;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.snqu.shopping.common.viewmodel.BaseAndroidViewModel;
import com.snqu.shopping.data.base.BaseResponseObserver;
import com.snqu.shopping.data.base.HttpResponseException;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.data.home.entity.RecommendDayEntity;
import com.snqu.shopping.data.home.entity.ShopItemEntity;
import com.snqu.shopping.data.red.RedClient;
import com.snqu.shopping.data.red.entity.BigVEntity;
import com.snqu.shopping.data.red.entity.RedGoodeEntity;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * 红人街
 *
 * @author 张全
 */
public class RedViewModel extends BaseAndroidViewModel {
    public static final String TAG_CATEGORY = "TAG_CATEGORY";
    public static final String TAG_GOODS = "TAG_GOODS";
    public static final String TAG_SHOP = "TAG_SHOP";
    public static final String TAG_SHOP_DETAIL = "TAG_SHOP_DETAIL";
    public static final String TAG_BIGV = "TAG_BIGV";
    public static final String TAG_BIGV_GOODS = "TAG_BIGV_GOODS";
    public static final String TAG_HAOWU = "TAG_HAOWU";

    public MutableLiveData<NetReqResult> mNetReqResultLiveData = new MutableLiveData<>();


    public RedViewModel(@NonNull Application application) {
        super(application);
    }


    @SuppressLint("AutoDispose")
    public void refreshAll(GoodsQueryParam queryParam, MutableLiveData<NetReqResult> mRefreshData) {
        compositeDisposable.add(Observable.zip(
                HomeClient.getAdList("8"),
                HomeClient.getDayRecommend(),
                RedClient.getGoodList(queryParam),
                new Function3<ResponseDataArray<AdvertistEntity>, ResponseDataArray<RecommendDayEntity>, ResponseDataArray<GoodsEntity>, RedHomeData>() {
                    @Override
                    public RedHomeData apply(ResponseDataArray<AdvertistEntity> advertistEntities, ResponseDataArray<RecommendDayEntity> recommendDayEntities, ResponseDataArray<GoodsEntity> goods) throws Exception {
                        RedHomeData redHomeData = new RedHomeData();
                        redHomeData.advertistEntities = advertistEntities.getDataList();
                        redHomeData.recommendDayEntities = recommendDayEntities.getDataList();
                        redHomeData.goodList = goods;
                        return redHomeData;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new BaseResponseObserver<RedHomeData>() {
                    @Override
                    public void onSuccess(RedHomeData value) {
                        mRefreshData.setValue(new NetReqResult(null, null, true, value));
                    }

                    @Override
                    public void onError(HttpResponseException e) {
                        mRefreshData.setValue(new NetReqResult(null, e.alert, false, e));
                    }

                    @Override
                    public void onEnd() {

                    }
                }));


    }


    /**
     * 分类
     */
    public void getCategorys() {
        executeNoMapHttp(RedClient.getCategoryList(), new BaseResponseObserver<ResponseDataArray<CategoryEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<CategoryEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_CATEGORY, null, true, value.getDataList()));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_CATEGORY, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 网红爆款商品
     *
     * @param queryParam
     * @param liveData
     */
    public void getGoods(GoodsQueryParam queryParam, MutableLiveData<NetReqResult> liveData) {
        executeNoMapHttp(RedClient.getGoodList(queryParam), new BaseResponseObserver<ResponseDataArray<GoodsEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<GoodsEntity> value) {
                liveData.setValue(new NetReqResult(TAG_GOODS, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                liveData.setValue(new NetReqResult(TAG_GOODS, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 网红店
     *
     * @param page
     */
    public void getRedShops(int page) {
        executeNoMapHttp(RedClient.getRedShops(page), new BaseResponseObserver<ResponseDataArray<ShopItemEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<ShopItemEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_SHOP, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_SHOP, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 店铺详情
     *
     * @param id
     */
    public void getShopDetail(String id,String item_source) {
        executeNoMapHttp(RedClient.getShopDetail(id,item_source), new BaseResponseObserver<ResponseDataObject<ShopItemEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<ShopItemEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_SHOP_DETAIL, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_SHOP_DETAIL, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }


    /**
     * 大V列表
     */
    public void getBigVList(GoodsQueryParam queryParam) {
        executeNoMapHttp(RedClient.getBigVList(queryParam.page), new BaseResponseObserver<ResponseDataArray<BigVEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<BigVEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_BIGV, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_BIGV, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 大V 商品列表
     *
     * @param queryParam
     */
    public void getBigVGoods(GoodsQueryParam queryParam) {
        executeNoMapHttp(RedClient.getBigVGoodList(queryParam), new BaseResponseObserver<ResponseDataArray<GoodsEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<GoodsEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_BIGV_GOODS, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_BIGV_GOODS, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 好物说
     *
     * @param page
     */
    public void getHaoWuList(int page) {
        executeNoMapHttp(RedClient.getRedGoods(page), new BaseResponseObserver<ResponseDataArray<RedGoodeEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<RedGoodeEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_HAOWU, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_HAOWU, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public static class RedHomeData {
        public List<AdvertistEntity> advertistEntities;
        public List<RecommendDayEntity> recommendDayEntities;
        public ResponseDataArray<GoodsEntity> goodList;
    }
}
