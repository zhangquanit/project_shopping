package com.snqu.shopping.ui.main.viewmodel;

import android.annotation.SuppressLint;
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
import com.snqu.shopping.data.bringgoods.BringGoodsBean;
import com.snqu.shopping.data.bringgoods.BringGoodsItemBean;
import com.snqu.shopping.data.goods.GoodsClient;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.data.home.entity.ActivityDetailEntity;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.data.home.entity.CommunityEntity;
import com.snqu.shopping.data.home.entity.CommunityRewardEntity;
import com.snqu.shopping.data.home.entity.HomeAdEntity;
import com.snqu.shopping.data.home.entity.HomeLayoutEntity;
import com.snqu.shopping.data.home.entity.HotSearchWord;
import com.snqu.shopping.data.home.entity.IconEntity;
import com.snqu.shopping.data.home.entity.PlateEntity;
import com.snqu.shopping.data.home.entity.PlateOptions;
import com.snqu.shopping.data.home.entity.RecommendDayEntity;
import com.snqu.shopping.data.home.entity.SearchShopEntity;
import com.snqu.shopping.data.home.entity.SearchSlugEntity;
import com.snqu.shopping.data.home.entity.ShareEntity;
import com.snqu.shopping.data.home.entity.ShopItemEntity;
import com.snqu.shopping.data.home.entity.VipGoodEntity;
import com.snqu.shopping.data.home.entity.VipRightEntity;
import com.snqu.shopping.data.home.entity.VipTaskEntity;
import com.snqu.shopping.data.home.entity.artical.ArticalCategoryEntity;
import com.snqu.shopping.data.home.entity.artical.ArticalEntity;
import com.snqu.shopping.data.home.entity.artical.ItemSourceResponseEntity;
import com.snqu.shopping.data.user.entity.SharePosterEntity;
import com.snqu.shopping.util.log.LogClient;
import com.snqu.shopping.util.statistics.task.NewTaskReportEntity;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * @author 张全
 */
public class HomeViewModel extends BaseAndroidViewModel {
    public static final String TAG_LAYOUTINDEX = "TAG_LAYOUTINDEX"; //首页-布局
    public static final String TAG_RECOMMEND_GOODS = "TAG_RECOMMEND_GOODS"; //首页-推荐商品
    public static final String TAG_AD = "TAG_AD"; //广告
    public static final String TAG_HOME_AD = "TAG_HOME_AD";//首页广告
    public static final String TAG_DAY_RECOMMEND = "TAG_DAY_RECOMMEND";//每日推荐
    public static final String TAG_AUTH_URL = "TAG_AUTH_URL"; //淘宝授权地址
    public static final String TAG_CATEGORY_ALL = "TAG_CATEGORY_ALL"; //分类——全部
    public static final String TAG_CATEGORY = "TAG_CATEGORY";//获取某个分类
    public static final String TAG_PLATE_LIST = "TAG_PLATE_LIST";//获取子板块
    public static final String TAG_PLATE_OPTIONS = "TAG_PLATE_OPTIONS";//获取板块筛选条件

    public static final String TAG_SEARCH_HOTWORD = "TAG_SEARCH_HOTWORD";//获取热门搜索
    public static final String TAG_SEARCH_HOTWORD_CLICK = "TAG_SEARCH_HOTWORD_CLICK";//点击热门搜索

    public static final String TAG_GOO0D_LIST = "TAG_GOO0D_LIST";//商品列表
    public static final String TAG_SEARCH_GOODS_ALL = "TAG_SEARCH_GOODS_ALL";//搜索+推荐商品
    public static final String TAG_SEARCH_GOODS = "TAG_SEARCH_GOODS"; //搜索商品
    public static final String TAG_SEARCH_CLIPBOARD = "TAG_SEARCH_CLIPBOARD"; //搜索剪贴板
    public static final String TAG_SEARCH_SHOP = "TAG_SEARCH_SHOP";//搜索+推荐商铺
    public static final String TAG_SEARCH_SHOP_LIST = "TAG_SEARCH_SHOP_LIST";//搜索店铺列表
    public static final String TAG_SEARCH_SHOP_RECOMMEND_LIST = "TAG_SEARCH_SHOP_RECOMMEND_LIST";//店铺推荐列表
    public static final String TAG_SEARCH_SLUG_LIST = "TAG_SEARCH_SLUG_LIST";//搜索联想词

    public static final String TAG_GOODS_DECODE_URL = "TAG_GOODS_DECODE_URL";//解析商品url 支持京东普通商品地址
    public static final String TAG_GOODS_DECODE_CODE = "TAG_GOODS_DECODE_CODE";//淘口令解析
    public static final String TAG_GOODS_DETAIL = "TAG_GOODS_DETAIL";//商品详情
    public static final String TAG_ICON_CONFIG = "TAG_ICON_CONFIG";//切换icon

    public static final String TAG_COMMUNITY_PLATE = "TAG_COMMUNITY_PLATE";//社区板块
    public static final String TAG_COMMUNITY_LIST = "TAG_COMMUNITY_LIST";//社区列表
    public static final String TAG_INIVTE_POSTER_LIST = "TAG_INIVTE_POSTER_LIST";//邀请分享-海报
    public static final String TAG_COMMUNITY_REWARD_LIST = "TAG_COMMUNITY_REWARD_LIST";//我的推荐-奖励记录

    public static final String TAG_VIP_GOODS = "TAG_VIP_GOODS";//vip商品
    public static final String TAG_VIP_TASKS = "TAG_VIP_TASKS";//vip任务

    public static final String TAG_ITEM_SOURCE_LIST = "TAG_ITEM_SOURCE_LIST";//商品来源
    public static final String TAG_VIP_RIGHTS = "TAG_VIP_RIGHTS";//会员权益

    public static final String TAG_ARTICAL_CATEGORY = "TAG_ARTICAL_CATEGORY";//商学院-分类
    public static final String TAG_ARTICAL_LIST = "TAG_ARTICAL_LIST";//商学院——文章列表
    public static final String TAG_ARTICAL_SEARCH = "TAG_ARTICAL_SEARCH";//商学院——搜索文章

    public static final String TAG_NEW_TASK_REPORT = "TAG_NEW_TASK_REPORT";//新手任务汇报
    public static final String TAG_ACTIVITY_DETAIL = "TAG_ACTIVITY_DETAIL";//活动详情
    public static final String TAG_AD_CONVERT_URL = "TAG_AD_CONVERT_URL";//活动转链


    public MutableLiveData<NetReqResult> mNetReqResultLiveData = new MutableLiveData<>();
    private Disposable mGoodsDisposable = null;
    private Disposable mRecommendDisposable = null;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 首页布局点击
     * @param id
     */
    public void layoutIndexClick(String id){
        executeNoMapHttp(HomeClient.getLayoutIndexClick(id), new BaseResponseObserver<ResponseDataObject<Object>>() {
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

    @SuppressLint({"AutoDispose", "CheckResult"})
    public void refreshAll(MutableLiveData<NetReqResult> mRefreshLiveData) {
        Observable.zip(
                HomeClient.getLayoutIndex(),
                HomeClient.getDayRecommend(),
                HomeClient.getHomeAd(),
                new Function3<ResponseDataObject<HomeLayoutEntity>, ResponseDataArray<RecommendDayEntity>, ResponseDataObject<HomeAdEntity>, HomeData>() {
                    @Override
                    public HomeData apply(ResponseDataObject<HomeLayoutEntity> homeLayoutIndex, ResponseDataArray<RecommendDayEntity> recommendDayEntities, ResponseDataObject<HomeAdEntity> homeAdEntity) throws Exception {
                        HomeData homeData = new HomeData();
                        homeData.homeLayoutIndex = homeLayoutIndex.data;
                        homeData.homeAdEntity = homeAdEntity.data;
                        homeData.dayRecommendList = recommendDayEntities.getDataList();
                        return homeData;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new BaseResponseObserver<HomeData>() {
                    @Override
                    public void onSuccess(HomeData value) {
                        mRefreshLiveData.setValue(new NetReqResult(null, null, true, value));
                    }

                    @Override
                    public void onError(HttpResponseException e) {
                        mRefreshLiveData.setValue(new NetReqResult(null, e.alert, false, e));
                    }

                    @Override
                    public void onEnd() {
                        dispose();
                    }
                });
    }

    @SuppressLint({"CheckResult", "AutoDispose"})
    public void init() {
        Observable.zip(
                HomeClient.getLayoutIndex(),
                HomeClient.itemSouceList(),
                new BiFunction<ResponseDataObject<HomeLayoutEntity>, ResponseDataObject<ItemSourceResponseEntity>, ResponseDataObject<HomeLayoutEntity>>() {
                    @Override
                    public ResponseDataObject<HomeLayoutEntity> apply(ResponseDataObject<HomeLayoutEntity> t1, ResponseDataObject<ItemSourceResponseEntity> t2) throws Exception {
                        if (t2.isSuccessful()) {
                            ItemSourceClient.saveItemSource(t2.data);
                        }
                        return t1;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new BaseResponseObserver<ResponseDataObject<HomeLayoutEntity>>() {
                    @Override
                    public void onSuccess(ResponseDataObject<HomeLayoutEntity> value) {
                        mNetReqResultLiveData.setValue(new NetReqResult(TAG_LAYOUTINDEX, null, true, value.data));
                    }

                    @Override
                    public void onError(HttpResponseException e) {
                        mNetReqResultLiveData.setValue(new NetReqResult(TAG_LAYOUTINDEX, e.message, false, e));
                    }

                    @Override
                    public void onEnd() {

                    }
                });


    }

    //首页"任务中心"动态图
    public void doTaskList() {
        executeNoMapHttp(HomeClient.getAdList("17"), new BaseResponseObserver<ResponseDataArray<AdvertistEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<AdvertistEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.AD_TASK_PAGE, "", true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.AD_TASK_PAGE, e.alert, false, null));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 首页广告
     */
    public void getHomeAd(MutableLiveData<NetReqResult> netReqResult) {
        executeNoMapHttp(HomeClient.getHomeAd(), new BaseResponseObserver<ResponseDataObject<HomeAdEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<HomeAdEntity> value) {
                netReqResult.setValue(new NetReqResult(TAG_HOME_AD, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                netReqResult.setValue(new NetReqResult(TAG_HOME_AD, e.message, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 每日推荐
     */
    public void getDayRecommend(MutableLiveData<NetReqResult> netReqResult) {
        executeNoMapHttp(HomeClient.getDayRecommend(), new BaseResponseObserver<ResponseDataArray<RecommendDayEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<RecommendDayEntity> value) {
                netReqResult.setValue(new NetReqResult(TAG_DAY_RECOMMEND, null, true, value.getDataList()));
            }

            @Override
            public void onError(HttpResponseException e) {
                netReqResult.setValue(new NetReqResult(TAG_DAY_RECOMMEND, e.message, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void getTBAuthUrl() {
        executeNoMapHttp(HomeClient.getAuthUrl(), new BaseResponseObserver<ResponseDataObject<String>>() {
            @Override
            public void onSuccess(ResponseDataObject<String> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_AUTH_URL, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_AUTH_URL, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 首页推荐商品
     */
    @SuppressLint({"CheckResult", "AutoDispose"})
    public void getRecommendGoods(GoodsQueryParam queryParam, MutableLiveData<NetReqResult> liveData) {

        if (mRecommendDisposable != null && !mRecommendDisposable.isDisposed()) {
            mRecommendDisposable.dispose();
        }
        HomeClient.getRecommendList(queryParam)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseDataArray<GoodsEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mRecommendDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseDataArray<GoodsEntity> value) {
                        if (null != liveData) {
                            liveData.setValue(new NetReqResult(TAG_RECOMMEND_GOODS, null, true, value));
                        } else {
                            mNetReqResultLiveData.setValue(new NetReqResult(TAG_RECOMMEND_GOODS, null, true, value));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (null != liveData) {
                            liveData.setValue(new NetReqResult(TAG_RECOMMEND_GOODS, null, false, e));
                        } else {
                            mNetReqResultLiveData.setValue(new NetReqResult(TAG_RECOMMEND_GOODS, null, false, e));
                        }
                    }

                    @Override
                    public void onComplete() {
                        mRecommendDisposable = null;
                    }
                });
    }

    @SuppressLint({"CheckResult", "AutoDispose"})
    public void getRecommendGoods(GoodsQueryParam queryParam) {
        getRecommendGoods(queryParam, null);
    }

    /**
     * 猜你喜欢
     */
    public void likeGoods(int page, int row) {
        executeNoMapHttp(HomeClient.likeGoods(page, row), new BaseResponseObserver<ResponseDataArray<GoodsEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<GoodsEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.LIKE_GOODS, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.LIKE_GOODS, null, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 获取广告
     *
     * @param pos
     */
    public void getAdList(String... pos) {
        executeNoMapHttp(HomeClient.getAdList(pos), new BaseResponseObserver<ResponseDataArray<AdvertistEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<AdvertistEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_AD, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_AD, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 广告id
     *
     * @param id
     */
    public void adClick(String id) {
        executeNoMapHttp(HomeClient.adClick(id), new BaseResponseObserver<ResponseDataObject<Object>>() {
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

    /**
     * 分类tab
     */
    public void getAllCategorys() {
        executeNoMapHttp(HomeClient.getAllCategorys(), new BaseResponseObserver<ResponseDataArray<CategoryEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<CategoryEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_CATEGORY_ALL, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_CATEGORY_ALL, null, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 获取首页三级分类
     *
     * @param id
     */
    public void getCategoryById(String id, int level, MutableLiveData<NetReqResult> liveData) {
        executeNoMapHttp(HomeClient.getCategotyEntitysById(id, level), new BaseResponseObserver<ResponseDataArray<CategoryEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<CategoryEntity> value) {
                liveData.setValue(new NetReqResult(TAG_CATEGORY, null, true, value.getDataList()));
            }

            @Override
            public void onError(HttpResponseException e) {
                liveData.setValue(new NetReqResult(TAG_CATEGORY, null, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 获取子板块
     *
     * @param pid
     */
    public void getPlateList(String pid) {
        executeNoMapHttp(HomeClient.getPlateList(pid), new BaseResponseObserver<ResponseDataArray<CategoryEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<CategoryEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_PLATE_LIST, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_PLATE_LIST, null, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 获取板块筛选条件
     *
     * @param id
     */
    public void getPlateOptions(String id) {
        executeNoMapHttp(HomeClient.getPlateOptions(id), new BaseResponseObserver<ResponseDataObject<PlateOptions>>() {
            @Override
            public void onSuccess(ResponseDataObject<PlateOptions> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_PLATE_OPTIONS, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_PLATE_OPTIONS, null, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 获取热门搜索
     */
    public void getHotwords() {
        executeNoMapHttp(HomeClient.getHotwords(), new BaseResponseObserver<ResponseDataArray<HotSearchWord>>() {
            @Override
            public void onSuccess(ResponseDataArray<HotSearchWord> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_SEARCH_HOTWORD, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_SEARCH_HOTWORD, null, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void hotwordClick(String id) {
        executeNoMapHttp(HomeClient.hotwordClick(id), new BaseResponseObserver<ResponseDataObject<Object>>() {
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

    /**
     * 查询商品列表
     *
     * @param queryParam
     */
    public void getGoodList(GoodsQueryParam queryParam) {
        getGoodList(queryParam, null);
    }

    public void getGoodList(GoodsQueryParam queryParam, MutableLiveData<NetReqResult> liveData) {
        executeNoMapHttp(HomeClient.getGoods(queryParam), new BaseResponseObserver<ResponseDataArray<GoodsEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<GoodsEntity> value) {
                if (null != liveData) {
                    liveData.setValue(new NetReqResult(TAG_GOO0D_LIST, null, true, value));
                } else {
                    mNetReqResultLiveData.setValue(new NetReqResult(TAG_GOO0D_LIST, null, true, value));
                }
            }

            @Override
            public void onError(HttpResponseException e) {
                if (null != liveData) {
                    liveData.setValue(new NetReqResult(TAG_GOO0D_LIST, null, false, e));
                } else {
                    mNetReqResultLiveData.setValue(new NetReqResult(TAG_GOO0D_LIST, null, false, e));
                }
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 搜索商品
     *
     * @param queryParam
     */
    @SuppressLint({"CheckResult", "AutoDispose"})
    public void searchGoods(GoodsQueryParam queryParam) {

        if (mGoodsDisposable != null && !mGoodsDisposable.isDisposed()) {
            mGoodsDisposable.dispose();
        }

        HomeClient.searchGoods(queryParam)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ResponseDataArray<GoodsEntity>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mGoodsDisposable = d;
                    }

                    @Override
                    public void onNext(ResponseDataArray<GoodsEntity> value) {
                        if (value.isSuccessful()) {
                            mNetReqResultLiveData.setValue(new NetReqResult(TAG_SEARCH_GOODS, null, true, value));
                        } else {
                            mNetReqResultLiveData.setValue(new NetReqResult(TAG_SEARCH_GOODS, value.message, false, null));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mNetReqResultLiveData.setValue(new NetReqResult(TAG_SEARCH_GOODS, "请求失败", false, e));
                    }

                    @Override
                    public void onComplete() {
                        mGoodsDisposable = null;
                    }
                });

    }


    @SuppressLint("AutoDispose")
    public void searchShop(GoodsQueryParam queryParam) {
        compositeDisposable.add(Observable.zip(
                HomeClient.searchShop(queryParam),
                HomeClient.getRecommendShop(queryParam),
                new BiFunction<ResponseDataArray<ShopItemEntity>, ResponseDataArray<ShopItemEntity>, SearchShopEntity>() {
                    @Override
                    public SearchShopEntity apply(ResponseDataArray<ShopItemEntity> entities, ResponseDataArray<ShopItemEntity> recommendEntities) throws Exception {
                        SearchShopEntity searchShopEntity = new SearchShopEntity();
                        searchShopEntity.shopEntities = entities;
                        searchShopEntity.recommendEntities = recommendEntities;
                        return searchShopEntity;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new BaseResponseObserver<SearchShopEntity>() {
                    @Override
                    public void onSuccess(SearchShopEntity value) {
                        mNetReqResultLiveData.setValue(new NetReqResult(TAG_SEARCH_SHOP, null, true, value));
                    }

                    @Override
                    public void onError(HttpResponseException e) {
                        mNetReqResultLiveData.setValue(new NetReqResult(TAG_SEARCH_SHOP, e.alert, false, e));
                    }

                    @Override
                    public void onEnd() {

                    }
                }));
    }

    /**
     * 搜索店铺列表
     *
     * @param queryParam
     */
    public void getSearchShopList(GoodsQueryParam queryParam) {
        executeNoMapHttp(HomeClient.searchShop(queryParam), new BaseResponseObserver<ResponseDataArray<ShopItemEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<ShopItemEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_SEARCH_SHOP_LIST, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_SEARCH_SHOP_LIST, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 友盟上报
     *
     * @param id
     */
    public void umengClickReport(String id) {
        executeNoMapHttp(HomeClient.umengClickReport(id), new BaseResponseObserver<ResponseDataObject<Object>>() {
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

    /**
     * 友盟数据上报
     *
     * @param id
     */
    public void umengDataReport(String id, String userId, String content) {
        executeNoMapHttp(HomeClient.umengDataReport(id, userId, content), new BaseResponseObserver<ResponseDataObject<Object>>() {
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

    /**
     * 搜索联想词
     *
     * @param search
     */
    public void searchSlugList(String search) {
        executeNoMapHttp(HomeClient.searchSlugList(search), new BaseResponseObserver<ResponseDataArray<SearchSlugEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<SearchSlugEntity> value) {
                NetReqResult netReqResult = new NetReqResult(TAG_SEARCH_SLUG_LIST, null, true, value);
                netReqResult.extra = search;
                mNetReqResultLiveData.setValue(netReqResult);
            }

            @Override
            public void onError(HttpResponseException e) {
                NetReqResult netReqResult = new NetReqResult(TAG_SEARCH_SLUG_LIST, e.alert, false, e);
                netReqResult.extra = search;
                mNetReqResultLiveData.setValue(netReqResult);
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 推荐店铺列表
     *
     * @param queryParam
     */
    public void getRecommendShops(GoodsQueryParam queryParam) {
        executeNoMapHttp(HomeClient.getRecommendShop(queryParam), new BaseResponseObserver<ResponseDataArray<ShopItemEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<ShopItemEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_SEARCH_SHOP_RECOMMEND_LIST, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_SEARCH_SHOP_RECOMMEND_LIST, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void decodeAnd() {

    }

    /**
     * 解析商品url 支持京东普通商品地址
     *
     * @param url
     */
//    public void decodeGoodByUrl(String url, int need_get_info) {
//        decodeGoodByUrl(url, need_get_info, null);
//    }
//
//    @SuppressLint("AutoDispose")
//    public void decodeGoodByUrl(String url, int need_get_info, MutableLiveData<NetReqResult> liveData) {
//        executeNoMapHttp(GoodsClient.INSTANCE.decodeGoodsByUrl(url, need_get_info), new Consumer<ResponseDataObject<GoodsEntity>>() {
//            @Override
//            public void accept(ResponseDataObject<GoodsEntity> responseDataObject) throws Exception {
//                NetReqResult netReqResult = new NetReqResult(TAG_GOODS_DECODE_URL, null, true, responseDataObject);
//                netReqResult.extra = url;
//                if (null != liveData) {
//                    liveData.setValue(netReqResult);
//                } else {
//                    mNetReqResultLiveData.setValue(netReqResult);
//                }
//            }
//        }, new Consumer<Throwable>() {
//            @Override
//            public void accept(Throwable throwable) throws Exception {
//                LogClient.clipboardLog("decodeGoodByUrl失败 参数=" + url + "\n" + throwable.getMessage());
//                NetReqResult netReqResult = new NetReqResult(TAG_GOODS_DECODE_URL, null, false, null);
//                netReqResult.extra = url;
//                if (null != liveData) {
//                    liveData.setValue(netReqResult);
//                } else {
//                    mNetReqResultLiveData.setValue(netReqResult);
//                }
//            }
//        });
//    }

    /**
     * 淘口令解析商品
     *
     * @param code
     */
    public void decodeGoodByCode(String code, int need_get_info, String is_serch) {
        decodeGoodByCode(code, need_get_info, null, is_serch);
    }

    public void decodeGoodByCode(String code, int need_get_info, MutableLiveData<NetReqResult> liveData, String is_serch) {
        executeNoMapHttp(GoodsClient.INSTANCE.decodeGoodsByCode(code, need_get_info, is_serch), new Consumer<ResponseDataObject<GoodsEntity>>() {
            @Override
            public void accept(ResponseDataObject<GoodsEntity> responseDataObject) throws Exception {
                NetReqResult netReqResult = new NetReqResult(TAG_GOODS_DECODE_CODE, null, true, responseDataObject);
                netReqResult.extra = code;
                if (null != liveData) {
                    liveData.setValue(netReqResult);
                } else {
                    mNetReqResultLiveData.setValue(netReqResult);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogClient.clipboardLog("decodeGoodByCode失败 参数=" + code + "\n" + throwable.getMessage());
                NetReqResult netReqResult = new NetReqResult(TAG_GOODS_DECODE_CODE, null, false, null);
                netReqResult.extra = code;
                if (null != liveData) {
                    liveData.setValue(netReqResult);
                } else {
                    mNetReqResultLiveData.setValue(netReqResult);
                }
            }
        });
    }

    /**
     * 图标配置
     */
    public void getIconConfig() {
        executeNoMapHttp(HomeClient.getIconConfig(), new BaseResponseObserver<ResponseDataObject<IconEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<IconEntity> responseDataObject) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ICON_CONFIG, null, true, responseDataObject));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ICON_CONFIG, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 社区板块
     */
    public void getCommunityPlate() {

        executeNoMapHttp(HomeClient.getCommunityPlate(), new BaseResponseObserver<ResponseDataArray<PlateEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<PlateEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_COMMUNITY_PLATE, null, true, value.getDataList()));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_COMMUNITY_PLATE, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 社区列表
     *
     * @param queryParam
     */
    @SuppressLint({"CheckResult", "AutoDispose"})
    public void getCommunityList(GoodsQueryParam queryParam, MutableLiveData<NetReqResult> liveData) {

        executeNoMapHttp(HomeClient.getCommunityList(queryParam), new BaseResponseObserver<ResponseDataArray<CommunityEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<CommunityEntity> value) {
                liveData.setValue(new NetReqResult(TAG_COMMUNITY_LIST, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                liveData.setValue(new NetReqResult(TAG_COMMUNITY_LIST, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
//        Observable.zip(
//                HomeClient.getCommunityList(queryParam),
//                HomeClient.getShareTemp(),
//                (t1, t2) -> {
//                    if (t2.isSuccessful()) {
//                        HomeClient.saveShareTemp(t2.data);
//                    }
//                    return t1;
//                }).observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribeWith(new BaseResponseObserver<ResponseDataArray<CommunityEntity>>() {
//                    @Override
//                    public void onSuccess(ResponseDataArray<CommunityEntity> value) {
//                        liveData.setValue(new NetReqResult(TAG_COMMUNITY_LIST, null, true, value));
//                    }
//
//                    @Override
//                    public void onError(HttpResponseException e) {
//                        liveData.setValue(new NetReqResult(TAG_COMMUNITY_LIST, e.alert, false, e));
//                    }
//
//                    @Override
//                    public void onEnd() {
//
//                    }
//                });
    }

    @SuppressLint({"CheckResult", "AutoDispose"})
    public void getCommunityRecommendList(GoodsQueryParam queryParam, String dayType, MutableLiveData<NetReqResult> liveData) {
        executeNoMapHttp(HomeClient.getCommunityRecommendList(queryParam, dayType), new BaseResponseObserver<ResponseDataArray<CommunityEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<CommunityEntity> value) {
                liveData.setValue(new NetReqResult(TAG_COMMUNITY_LIST, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                liveData.setValue(new NetReqResult(TAG_COMMUNITY_LIST, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 获取我推荐的奖励记录
     */
    public void getCommunityRewardList(int page) {
        executeNoMapHttp(HomeClient.getCommunityRewardList(page), new BaseResponseObserver<ResponseDataArray<CommunityRewardEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<CommunityRewardEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_COMMUNITY_REWARD_LIST, null, true, value.getDataList()));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_COMMUNITY_REWARD_LIST, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 社区-分享点击
     *
     * @param id
     */
    public void clickCommunity(String id) {
        executeNoMapHttp(HomeClient.clickCommunity(id), new BaseResponseObserver<ResponseDataObject<Object>>() {
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

    /**
     * 邀请分享-海报
     */
    public void getInvitePosterImgs(String code) {
        executeNoMapHttp(HomeClient.getInvitePosterImgs(code), new BaseResponseObserver<ResponseDataArray<SharePosterEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<SharePosterEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_INIVTE_POSTER_LIST, null, true, value.getDataList()));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_INIVTE_POSTER_LIST, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 点击次数
     *
     * @param id
     */
    public void clickPosterImgs(String id) {
        executeNoMapHttp(HomeClient.clickPosterImgs(id), new BaseResponseObserver<ResponseDataObject<Object>>() {
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

    /**
     * vip商品列表
     */
    public void getVipGoods(int page) {
        executeNoMapHttp(HomeClient.getVipGoods(page), new BaseResponseObserver<ResponseDataArray<VipGoodEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<VipGoodEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_VIP_GOODS, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_VIP_GOODS, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * VIP升级任务
     */
    public void getVipTasks() {
        executeNoMapHttp(HomeClient.getVipTasks(), new BaseResponseObserver<ResponseDataObject<VipTaskEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<VipTaskEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_VIP_TASKS, null, true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_VIP_TASKS, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void getVipTasks(String uid) {
        executeNoMapHttp(HomeClient.getVipTasks(uid), new BaseResponseObserver<ResponseDataObject<VipTaskEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<VipTaskEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.VIP_TASKS, null, true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.VIP_TASKS, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void getTaskXnumber(String code) {
        executeNoMapHttp(HomeClient.getTaskXNumber(code), new BaseResponseObserver<ResponseDataObject<NewTaskReportEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<NewTaskReportEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.TASK_X_NUMBER, null, true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.TASK_X_NUMBER, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void getItemSourceList() {
        executeNoMapHttp(HomeClient.itemSouceList(), new BaseResponseObserver<ResponseDataObject<ItemSourceResponseEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<ItemSourceResponseEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ITEM_SOURCE_LIST, null, true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ITEM_SOURCE_LIST, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 会员权益
     */
    public void getVipRights() {
        executeNoMapHttp(HomeClient.getVipRights(), new BaseResponseObserver<ResponseDataObject<VipRightEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<VipRightEntity> value) {
                HomeClient.saveVipRights(value.data);
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_VIP_RIGHTS, null, true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_VIP_RIGHTS, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 获取平台来源分享模板
     */
    public void getShareTemp() {
        executeNoMapHttp(HomeClient.getShareTemp(), new BaseResponseObserver<ResponseDataObject<ShareEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<ShareEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.SHARE_TEMPLATE, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.SHARE_TEMPLATE, e.alert, false, null));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 商学院-文章列表
     *
     * @param id
     */
    public void getArticalList(String id, int page) {
        executeNoMapHttp(HomeClient.getArticalList(id, page), new BaseResponseObserver<ResponseDataArray<ArticalEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<ArticalEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ARTICAL_LIST, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ARTICAL_LIST, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 商学院-搜索
     *
     * @param search
     */
    public void searchArticals(String search, int page) {
        executeNoMapHttp(HomeClient.searchArticals(search, page), new BaseResponseObserver<ResponseDataArray<ArticalEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<ArticalEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ARTICAL_SEARCH, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ARTICAL_SEARCH, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 商学院-分类
     */
    public void getArticalCategorys(String category_id) {
        executeNoMapHttp(HomeClient.getArticalCategorys(category_id), new BaseResponseObserver<ResponseDataArray<ArticalCategoryEntity>>() {
            @Override
            public void onSuccess(ResponseDataArray<ArticalCategoryEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ARTICAL_CATEGORY, null, true, value.getDataList()));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ARTICAL_CATEGORY, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }


    /**
     * 活动详情
     *
     * @param code
     */
    public void getActivityDetail(String code) {
        executeNoMapHttp(HomeClient.getActivityDetail(code), new BaseResponseObserver<ResponseDataObject<ActivityDetailEntity>>() {
            @Override
            public void onSuccess(ResponseDataObject<ActivityDetailEntity> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ACTIVITY_DETAIL, null, true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ACTIVITY_DETAIL, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

//    /**
//     * 活动转链
//     *
//     * @param url
//     * @param platform
//     */
//    public void adConvertUrl(String url, String platform) {
//        executeNoMapHttp(HomeClient.adConvertUrl(url, platform), new BaseResponseObserver<ResponseDataObject<AdConvertEntity>>() {
//            @Override
//            public void onSuccess(ResponseDataObject<AdConvertEntity> value) {
//                mNetReqResultLiveData.setValue(new NetReqResult(TAG_AD_CONVERT_URL, null, true, value.data));
//            }
//
//            @Override
//            public void onError(HttpResponseException e) {
//                mNetReqResultLiveData.setValue(new NetReqResult(TAG_AD_CONVERT_URL, e.alert, false, e));
//            }
//
//            @Override
//            public void onEnd() {
//
//            }
//        });
//    }

    /**
     * 抖音带货-分类
     */
    public void getDydhCatergory() {
        executeNoMapHttp(HomeClient.getDydhCatergory(), new BaseResponseObserver<ResponseDataArray<BringGoodsBean>>() {
            @Override
            public void onSuccess(ResponseDataArray<BringGoodsBean> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.DYDH_CATEGORY, null, true, value.data));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.DYDH_CATEGORY, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 抖音带货-分类列表
     */
    public void getDydhList(String cid, int page) {
        executeNoMapHttp(HomeClient.getDydhList(cid, page), new BaseResponseObserver<ResponseDataArray<BringGoodsItemBean>>() {
            @Override
            public void onSuccess(ResponseDataArray<BringGoodsItemBean> value) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.DYDH_LIST, null, true, value));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(ApiHost.DYDH_LIST, e.alert, false, e));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 获取安全域名
     */
    public void getSafeDomain() {
        executeNoMapHttp(HomeClient.doGetSafeDomain(), new BaseResponseObserver<ResponseDataArray<String>>() {
            @Override
            public void onSuccess(ResponseDataArray<String> value) {
                HomeClient.saveSafeDomain(value.data);
            }

            @Override
            public void onError(HttpResponseException e) {

            }

            @Override
            public void onEnd() {

            }
        });
    }

//    /**
//     * 新手任务
//     *
//     * @param type
//     */
//    public void newTaskReport(int type) {
//        newTaskReport(type, mNetReqResultLiveData);
//    }
//
//    /**
//     * 新手任务
//     */
//    public void newTaskReport(int type, MutableLiveData<NetReqResult> liveData) {
//        executeNoMapHttp(HomeClient.newTaskReport(type), new BaseResponseObserver<ResponseDataObject<NewTaskReportEntity>>() {
//            @Override
//            public void onSuccess(ResponseDataObject<NewTaskReportEntity> value) {
//                //本地记录汇报
//                UserClient.setNewTaskReport(type);
//                liveData.setValue(new NetReqResult(TAG_NEW_TASK_REPORT, null, true, value));
//            }
//
//            @Override
//            public void onError(HttpResponseException e) {
//                liveData.setValue(new NetReqResult(TAG_NEW_TASK_REPORT, e.alert, false, e));
//            }
//
//            @Override
//            public void onEnd() {
//
//            }
//        });
//    }

    public static class HomeData {
        public HomeLayoutEntity homeLayoutIndex; //首页布局
        public HomeAdEntity homeAdEntity;
        public List<RecommendDayEntity> dayRecommendList; //每日推荐
    }


}
