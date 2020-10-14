package com.snqu.shopping.data.home;

import androidx.annotation.Keep;

import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.bringgoods.BringGoodsBean;
import com.snqu.shopping.data.bringgoods.BringGoodsItemBean;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
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
import com.snqu.shopping.util.statistics.task.NewTaskReportEntity;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * @author 张全
 */
@Keep
public interface HomeApi {
    /**
     * 首页布局
     *
     * @return
     * +"?_id=5f03e40d0a6c8e5bfd006c43"
     */
    @GET(ApiHost.LAYOUT_INDEX)
    Observable<ResponseDataObject<HomeLayoutEntity>> layoutIndex();

    /**
     * 首页模块点击次数[进入次数]
     * @return
     */
    @GET(ApiHost.LAYOUT_INDEX_CLICK)
    Observable<ResponseDataObject<Object>> layoutIndexClick(@Query("_id") String id);

    /**
     * 首页广告
     *
     * @return
     */
    @GET(ApiHost.HOME_AD)
    Observable<ResponseDataObject<HomeAdEntity>> homeAd();

    /**
     * 获取淘宝京东活动链接
     *
     * @return
     */
//    @GET(ApiHost.AD_CONVERT_URL)
//    Observable<ResponseDataObject<AdConvertEntity>> adConvertUrl(@Query("url") String url, @Query("platform") String platform, @Query("need_tkl") int need_tkl);

    /**
     * 获取淘宝授权地址
     *
     * @return
     */
    @GET(ApiHost.TB_AUTH)
    Observable<ResponseDataObject<String>> getTBAuthUrl();

    /**
     * 获取子板块
     */
    @GET(ApiHost.PLATE_LIST)
    Observable<ResponseDataArray<CategoryEntity>> plateList(@Query("pid") String pid);

    /**
     * 获取板块筛选条件
     */
    @GET(ApiHost.PLATE_OPTIONS)
    Observable<ResponseDataObject<PlateOptions>> plateOptions(@Query("id") String id);

    /**
     * 首页推荐商品
     *
     * @return
     */
    @GET
    Observable<ResponseDataArray<GoodsEntity>> homeRecommendGoods(@Url String url);

    /**
     * 猜你喜欢
     *
     * @return
     */
    @GET(ApiHost.LIKE_GOODS)
    Observable<ResponseDataArray<GoodsEntity>> likeGoods(@Query("page") int page, @Query("row") int row);

    /**
     * 获取广告列表
     *
     * @param position 多个位置使用逗号分割
     * @return
     */
    @GET(ApiHost.AD_LIST)
    Observable<ResponseDataArray<AdvertistEntity>> adList(@Query("position") String position);

    /**
     * 广告点击
     *
     * @param id
     * @return
     */
    @GET(ApiHost.AD_CLICK)
    Observable<ResponseDataObject<Object>> adClick(@Query("id") String id);

    /**
     * 每日推荐
     *
     * @return
     */
    @GET(ApiHost.RECOMMEND_DAY)
    Observable<ResponseDataArray<RecommendDayEntity>> recommendDay();

    /**
     * 分类
     *
     * @return
     */
    @GET(ApiHost.CATEGORY_LIST)
    Observable<ResponseDataArray<CategoryEntity>> getCategoryList(@Query("pid") String pid, @Query("all") int all, @Query("need_three") int need_three);

    @GET(ApiHost.CATEGORY_LIST)
    Observable<ResponseDataArray<CategoryEntity>> getAllCategoryList(@Query("all") int all);

    @GET(ApiHost.CATEGORY_LIST)
    Observable<ResponseDataArray<CategoryEntity>> getCategoryListById(@Query("pid") String pid, @Query("need_three") int need_three, @Query("level") int level);

    /**
     * 商品列表
     */
    @GET
    Observable<ResponseDataArray<GoodsEntity>> getGoodList(@Url String url);

    /**
     * 搜索商品列表
     */
    @GET
    Observable<ResponseDataArray<GoodsEntity>> searchGoodList(@Url String url);


    /**
     * 搜索商品列表
     */
    @GET(ApiHost.SEARCH_SLUG)
    Observable<ResponseDataArray<SearchSlugEntity>> searchSlug(@Query("search") String search);

    /**
     * 热门搜索
     *
     * @return
     */
    @GET(ApiHost.SEARCH_HOT)
    Observable<ResponseDataArray<HotSearchWord>> getHotWords();

    /**
     * 点击热门搜索
     *
     * @param id
     * @return
     */
    @POST(ApiHost.SEARCH_HOT_CLICK)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> hotwordClick(@Field("id") String id);


    /**
     * 搜索店铺
     *
     * @return
     */
    @GET
    Observable<ResponseDataArray<ShopItemEntity>> searchShop(@Url String url);

    /**
     * 推荐店铺
     *
     * @return
     */
    @GET
    Observable<ResponseDataArray<ShopItemEntity>> getRecommendShop(@Url String url);

    /**
     * 图标配置
     *
     * @return
     */
    @GET(ApiHost.ICON_CONFIG)
    Observable<ResponseDataObject<IconEntity>> getIconConfig();

    /**
     * 社区列表
     *
     * @return
     */
    @GET
    Observable<ResponseDataArray<CommunityEntity>> getCommunityList(@Url String url);

    /**
     * 社区板块
     *
     * @return
     */
    @GET(ApiHost.COMMONUNITY_PLATE)
    Observable<ResponseDataArray<PlateEntity>> getCommunityPlate();

    /**
     * 转发点击
     *
     * @return
     */
    @GET(ApiHost.COMMUNITY_CLICK)
    Observable<ResponseDataObject<Object>> clickCommunity(@Query("id") String id);

    /**
     * 获取我推荐的奖励记录
     *
     * @return
     */
    @GET(ApiHost.COMMUNITY_REWARD_LIST)
    Observable<ResponseDataArray<CommunityRewardEntity>> getCommunityRewardList(@Query("page") int page, @Query("row") int row);

    /**
     * 商学院-分类
     */
    @GET(ApiHost.COMMUNITY_ARTICAL_HOT_CAT)
    Observable<ResponseDataArray<ArticalCategoryEntity>> getArticalCategorys(@Query("category_id") String category_id);

    /**
     * 商学院-搜索文章
     */
    @GET(ApiHost.COMMUNITY_ARTICAL_SEARCH)
    Observable<ResponseDataArray<ArticalEntity>> searchArticals(@Query("search") String search, @Query("page") int page, @Query("pageSize") int pageSize);


    /**
     * 商学院-文章列表
     */
    @GET(ApiHost.COMMUNITY_ARTICAL_LIST)
    Observable<ResponseDataArray<ArticalEntity>> getArticalList(@Query("category_id") String category_id, @Query("page") int page, @Query("pageSize") int pageSize);


    /**
     * 邀请-海报
     *
     * @return
     */
    @GET(ApiHost.INVITE_IMGS)
    Observable<ResponseDataArray<SharePosterEntity>> getInvitePosterImgs(@Query("code") String code);

    /**
     * 点击邀请图片
     *
     * @return
     */
    @POST(ApiHost.INVITE_IMG_CLICK)
    Observable<ResponseDataObject<Object>> clickPosterImgs(@Field("id") String id);

    /**
     * VIP商品列表
     */
    @GET(ApiHost.VIP_GOODS)
    Observable<ResponseDataArray<VipGoodEntity>> getVipGoods(@Query("page") int page, @Query("row") int row);

    /**
     * 友盟点击上报
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.UMENG_CLICK_REPORT)
    Observable<ResponseDataObject<Object>> umengClickReport(@Field("id") String id);

    /**
     * 友盟消息上报
     *
     * @param id
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.UMENG_DATA_REPORT)
    Observable<ResponseDataObject<Object>> umengDataReport(@Field("id") String id, @Field("userId") String userId, @Field("content") String content);

    /**
     * VIP任务
     *
     * @return
     */
    @GET(ApiHost.VIP_TASKS)
    Observable<ResponseDataObject<VipTaskEntity>> getVipTasks();

    /**
     * VIP任务
     *
     * @return
     */
    @GET(ApiHost.VIP_TASKS)
    Observable<ResponseDataObject<VipTaskEntity>> getVipTasks(@Query("uid") String uid);

    /**
     * 剪贴板搜索
     *
     * @param url
     * @return
     */
    @GET()
    Observable<ResponseDataArray<GoodsEntity>> searchClipboard(@Url String url);

    /**
     * 商品来源列表
     */
    @GET(ApiHost.ITEM_SOURCE_LIST)
    Observable<ResponseDataObject<ItemSourceResponseEntity>> itemSouceList();


    /**
     * 会员权益
     *
     * @return
     */
    @GET(ApiHost.VIP_RIGHTS)
    Observable<ResponseDataObject<VipRightEntity>> getVipRights();


    /**
     * 获取平台来源分享模板
     */
    @GET(ApiHost.SHARE_TEMPLATE)
    Observable<ResponseDataObject<ShareEntity>> getShareTemplate();

    /**
     * 新手任务
     *
     * @param type
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.NEW_TASK)
    Observable<ResponseDataObject<NewTaskReportEntity>> newTask(@Field("type") String type);

    /**
     * 日常任务
     *
     * @param type
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.DAY_TASK)
    Observable<ResponseDataObject<NewTaskReportEntity>> dayTask(@Field("type") String type);


    /**
     * 根据任务编码获取星币和当前用户状态
     *
     * @return
     */
    @GET(ApiHost.TASK_X_NUMBER)
    Observable<ResponseDataObject<NewTaskReportEntity>> getTaskXNumber(@Query("code") String code);

    /**
     * 活动详情
     * U
     *
     * @param code
     * @return
     */
    @GET(ApiHost.ACT_DETAIL)
    Observable<ResponseDataObject<ActivityDetailEntity>> getActivityDetail(@Query("code") String code);

    /**
     * 抖音带货-分类
     *
     * @return
     */
    @GET(ApiHost.DYDH_CATEGORY)
    Observable<ResponseDataArray<BringGoodsBean>> getDydhCatergory();

    /**
     * 抖音带货-某个分类下的细分列表
     *
     * @param cid
     * @return
     */
    @GET(ApiHost.DYDH_LIST)
    Observable<ResponseDataArray<BringGoodsItemBean>> getDydhList(@Query("cid") String cid, @Query("page") int page, @Query("pageSize") int pageSize);


    /**
     * 商品列表
     * @param options
     * @return
     */
    @GET(ApiHost.GET_GOODS_LIST)
    Observable<ResponseDataArray<GoodsEntity>> getHomeGoodsList(@QueryMap(encoded = true) Map<String, String> options);


    /**
     * 获取安全域名
     *
     * @return
     */
    @GET(ApiHost.SAFE_DOMAIN)
    Observable<ResponseDataArray<String>> getSafeDomain();

}
