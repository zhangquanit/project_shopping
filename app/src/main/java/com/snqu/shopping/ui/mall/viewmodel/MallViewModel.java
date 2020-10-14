package com.snqu.shopping.ui.mall.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.snqu.shopping.common.viewmodel.BaseAndroidViewModel;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.BaseResponseObserver;
import com.snqu.shopping.data.base.HttpResponseException;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.home.entity.SearchSlugEntity;
import com.snqu.shopping.data.mall.MallClient;
import com.snqu.shopping.data.mall.entity.CommentEntity;
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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

/**
 * 自供ViewModel
 */
public class MallViewModel extends BaseAndroidViewModel {
    public static final String TAG_BANNER = "TAG_BANNER";
    public static final String TAG_RECOMMEND = "TAG_RECOMMEND";
    public static final String TAG_CATEGORY = "TAG_CATEGORY";
    public static final String TAG_CATEGORY_GOODS = "TAG_CATEGORY_GOODS";
    public static final String TAG_KEYWORDS = "TAG_KEYWORDS";
    public static final String TAG_ORDER_LIST = "TAG_ORDER_LIST";
    public static final String TAG_ORDER_CANCEL = "TAG_ORDER_CANCEL";
    public static final String TAG_ORDER_DETAIL = "TAG_ORDER_DETAIL";
    public static final String TAG_ORDER_COMMENT = "TAG_ORDER_COMMENT";
    public static final String TAG_ORDER_RECEIPT = "TAG_ORDER_RECEIPT";

    public MutableLiveData<NetReqResult> mNetReqResultLiveData = new MutableLiveData<>();

    public MallViewModel(@NonNull Application application) {
        super(application);
    }

    public void getBanner() {
        executeNoMapHttp(MallClient.getBanner(), new BaseResponseObserver<ResponseDataObject<MallBannerEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<MallBannerEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_BANNER, null, true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_BANNER, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void bannerReport(String _id) {
        executeNoMapHttp(MallClient.bannerReport(_id), new BaseResponseObserver<ResponseDataObject<Object>>() {
            @Override
            public void onSuccess(ResponseDataObject<Object> value) {
            }

            @Override
            public void onError(HttpResponseException e) {
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void getCategory() {
        executeNoMapHttp(MallClient.getCategory(), new BaseResponseObserver<ResponseDataArray<MallCategoryEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<MallCategoryEntity> value) {
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

    public void getRecommend(int page, MutableLiveData liveData) {
        executeNoMapHttp(MallClient.getRecommend(page), new BaseResponseObserver<ResponseDataArray<MallRecommendEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<MallRecommendEntity> value) {
                liveData.setValue(new NetReqResult(TAG_RECOMMEND, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                liveData.setValue(new NetReqResult(TAG_RECOMMEND, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void getCategeoryGoods(String id, String search, int page) {
        getCategeoryGoods(id, search, page, mNetReqResultLiveData);
    }

    public void getCategeoryGoods(String id, String search, int page, MutableLiveData liveData) {
        executeNoMapHttp(MallClient.getCategeoryGoods(id, search, page), new BaseResponseObserver<ResponseDataArray<ShopGoodsEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<ShopGoodsEntity> value) {
                if (null != liveData) {
                    liveData.setValue(new NetReqResult(TAG_CATEGORY_GOODS, null, true, value));
                } else {
                    mNetReqResultLiveData.setValue(new NetReqResult(TAG_CATEGORY_GOODS, null, true, value));
                }
            }

            @Override
            public void onError(HttpResponseException e) {
                if (null != liveData) {
                    liveData.setValue(new NetReqResult(TAG_CATEGORY_GOODS, e.alert, false, e));
                } else {
                    mNetReqResultLiveData.setValue(new NetReqResult(TAG_CATEGORY_GOODS, e.alert, false, e));
                }

            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 关键词搜索
     */
    public void searchSlugList(String search) {
        executeNoMapHttp(MallClient.searchSlugList(search), new BaseResponseObserver<ResponseDataArray<SearchSlugEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<SearchSlugEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_KEYWORDS, null, true, value.getDataList()));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_KEYWORDS, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }


    /**
     * 立即购买
     *
     * @param _id
     * @param standard_name
     * @param number
     */
    public void orderBuy(String _id, String standard_name, int number, MutableLiveData liveData) {
        BaseResponseObserver<Object> baseResponseObserver = Observable
                .zip(
                        MallClient.doGetAddress(),
                        MallClient.orderBuy(_id, standard_name, number),
                        new BiFunction<ResponseDataArray<AddressEntity>, ResponseDataObject<ShopGoodsEntity>, Object>() {
                            @Override
                            public ShopGoodsEntity apply(ResponseDataArray<AddressEntity> addressEntityResponseDataArray, ResponseDataObject<ShopGoodsEntity> shopGoodsEntityResponseDataObject) throws Exception {
                                ShopGoodsEntity shopGoodsEntity = null;
                                if (shopGoodsEntityResponseDataObject != null && shopGoodsEntityResponseDataObject.data != null) {
                                    shopGoodsEntity = shopGoodsEntityResponseDataObject.data;
                                    if (addressEntityResponseDataArray != null && addressEntityResponseDataArray.data != null) {
                                        if (addressEntityResponseDataArray.data.size() > 0) {
                                            //获得默认第一个地址
                                            shopGoodsEntity.addressEntity = addressEntityResponseDataArray.data.get(0);
                                        }
                                    }
                                }
                                return shopGoodsEntity;
                            }
                        }
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new BaseResponseObserver<Object>() {
                    @Override
                    public void onSuccess(Object value) {
                        if (liveData != null) {
                            liveData.setValue(new NetReqResult(ApiHost.MALL_ORDER_BUYNOW, null, true, value));
                        } else {
                            mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.MALL_ORDER_BUYNOW, null, true, value));
                        }
                    }

                    @Override
                    public void onError(HttpResponseException e) {
                        if (liveData != null) {
                            liveData.setValue(new NetReqResult(ApiHost.MALL_ORDER_BUYNOW, e.getMsg(), false));
                        } else {
                            mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.MALL_ORDER_BUYNOW, e.getMsg(), false));
                        }
                    }

                    @Override
                    public void onEnd() {
                        dispose();
                    }
                });


//        executeNoMapHttp(MallClient.orderBuy(_id, standard_name, number), new BaseResponseObserver<ResponseDataObject<ShopGoodsEntity>>() {
//            @Override
//            public void onSuccess(ResponseDataObject<ShopGoodsEntity> value) {
//                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.MALL_ORDER_BUYNOW, null, true, value.data));
//            }
//
//            @Override
//            public void onError(HttpResponseException e) {
//                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.MALL_ORDER_BUYNOW, e.getMsg(), false));
//            }
//
//            @Override
//            public void onEnd() {
//
//            }
//        });
    }

    /**
     * 获取分享商品信息
     *
     * @param _id
     */
    public void getShareInfo(String _id) {
        executeNoMapHttp(MallClient.getShareInfo(_id), new BaseResponseObserver<ResponseDataObject<MallGoodShareInfoEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<MallGoodShareInfoEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.MALL_HOME_SHARE, null, true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.MALL_HOME_SHARE, e.getMsg(), false));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void goRePay(String _id, String addressId) {
        goRePay(_id, addressId, null);
    }

    public void goRePay(String _id, String addressId, MutableLiveData liveData) {
        executeNoMapHttp(MallClient.goRePay(_id, addressId), new BaseResponseObserver<ResponseDataObject<PayResultDataEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<PayResultDataEntity> value) {
                if (null != liveData) {
                    liveData.setValue(new NetReqResult(ApiHost.MALL_ORDER_RE_PAY, null, true, value.data));
                } else {
                    mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.MALL_ORDER_RE_PAY, null, true, value.data));
                }
            }

            @Override
            public void onError(HttpResponseException e) {
                if (null != liveData) {
                    liveData.setValue(new NetReqResult(ApiHost.MALL_ORDER_RE_PAY, e.getMsg(), false));
                } else {
                    mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.MALL_ORDER_RE_PAY, e.getMsg(), false));
                }
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void goRePay(String _id) {
        goRePay(_id, null);
    }

    public void goPay(PayDataEntity payDataEntity, MutableLiveData liveData) {
        executeNoMapHttp(MallClient.goPay(payDataEntity), new BaseResponseObserver<ResponseDataObject<PayResultDataEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<PayResultDataEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.MALL_ORDER_GOPAY, null, true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.MALL_ORDER_GOPAY, e.getMsg(), false));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void getGoodDetail(String id) {
        executeNoMapHttp(MallClient.getGoodDetail(id), new BaseResponseObserver<ResponseDataObject<ShopGoodsEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<ShopGoodsEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.MALL_GOOD_DETAIL, null, true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.MALL_GOOD_DETAIL, e.getMsg(), false));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 订单列表
     *
     * @param status
     * @param page
     * @param liveData
     */
    public void getOrderList(int status, int page, MutableLiveData liveData) {
        executeNoMapHttp(MallClient.orderList(status, page), new BaseResponseObserver<ResponseDataArray<MallOrderEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<MallOrderEntity> value) {
                liveData.setValue(new NetReqResult(TAG_ORDER_LIST, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                liveData.setValue(new NetReqResult(TAG_ORDER_LIST, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 取消订单
     *
     * @param id
     */
    public void orderCancel(String id) {
        orderCancel(id, null);
    }

    public void orderCancel(String id, MutableLiveData liveData) {
        executeNoMapHttp(MallClient.orderCancel(id), new BaseResponseObserver<ResponseDataObject<Object>>() {
            @Override
            public void onSuccess(ResponseDataObject<Object> value) {
                if (null != liveData) {
                    liveData.setValue(new NetReqResult(TAG_ORDER_CANCEL, null, true, value));
                } else {
                    mNetReqResultLiveData.setValue(new NetReqResult(TAG_ORDER_CANCEL, null, true, value));
                }
            }

            @Override
            public void onError(HttpResponseException e) {
                if (null != liveData) {
                    liveData.setValue(new NetReqResult(TAG_ORDER_CANCEL, e.alert, false, e));
                } else {
                    mNetReqResultLiveData.setValue(new NetReqResult(TAG_ORDER_CANCEL, e.alert, false, e));
                }
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 订单详情
     *
     * @param _id
     */
    public void orderDetail(String _id) {
        executeNoMapHttp(MallClient.orderDetail(_id), new BaseResponseObserver<ResponseDataObject<MallOrderDetailEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<MallOrderDetailEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ORDER_DETAIL, null, true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ORDER_DETAIL, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 订单评价
     *
     * @param commentEntity
     */
    public void orderComment(CommentEntity commentEntity) {
        executeNoMapHttp(MallClient.orderComment(commentEntity), new BaseResponseObserver<ResponseDataObject<Object>>() {
            @Override
            public void onSuccess(ResponseDataObject<Object> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ORDER_COMMENT, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ORDER_COMMENT, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 订单-确认收货
     */
    public void orderReceipt(String id) {
        executeNoMapHttp(MallClient.orderReceipt(id), new BaseResponseObserver<ResponseDataObject<Object>>() {
            @Override
            public void onSuccess(ResponseDataObject<Object> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ORDER_RECEIPT, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ORDER_RECEIPT, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }
}
