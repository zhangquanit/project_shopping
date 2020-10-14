package com.snqu.shopping.data.home;

import android.text.TextUtils;

import com.android.util.ext.SPUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.DataConfig;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.base.RestClient;
import com.snqu.shopping.data.bringgoods.BringGoodsBean;
import com.snqu.shopping.data.bringgoods.BringGoodsItemBean;
import com.snqu.shopping.data.goods.bean.GoodsParamBean;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Query;

/**
 * @author 张全
 */
public class HomeClient {

    private static HomeApi getApi() {
        return RestClient.getService(HomeApi.class);
    }

    /**
     * 首页布局
     *
     * @return
     */
    public static Observable<ResponseDataObject<HomeLayoutEntity>> getLayoutIndex() {
        return getApi().layoutIndex();
    }

    /**
     * 首页模块点击次数[进入次数]
     * @param id
     * @return
     */
    public static Observable<ResponseDataObject<Object>> getLayoutIndexClick(@Query("_id") String id){
        return getApi().layoutIndexClick(id);
    }

    /**
     * 首页广告
     *
     * @return
     */
    public static Observable<ResponseDataObject<HomeAdEntity>> getHomeAd() {
        return getApi().homeAd();
    }

    /**
     * 获取淘宝授权地址
     *
     * @return
     */
    public static Observable<ResponseDataObject<String>> getAuthUrl() {
        return getApi().getTBAuthUrl();
    }

    /**
     * 获取子版块
     *
     * @param pid
     * @return
     */
    public static Observable<ResponseDataArray<CategoryEntity>> getPlateList(String pid) {
        return getApi().plateList(pid);
    }

    /**
     * 获取板块筛选条件
     *
     * @param id
     * @return
     */
    public static Observable<ResponseDataObject<PlateOptions>> getPlateOptions(String id) {
        return getApi().plateOptions(id);
    }

    /**
     * 首页-推荐商品
     *
     * @return
     */
    public static Observable<ResponseDataArray<GoodsEntity>> getRecommendList(GoodsQueryParam queryParam) {
        GoodsParamBean goodsParamBean = new GoodsParamBean();
        goodsParamBean.page = String.valueOf(queryParam.page);
        goodsParamBean.pageSize = "10";
        goodsParamBean.tid = "layouts_index";
        goodsParamBean.item_source = queryParam.item_source == null ? "" : queryParam.item_source;
        Map<String, String> map = new HashMap();
        map.put("page", goodsParamBean.page);
        map.put("pageSize", goodsParamBean.pageSize);
        map.put("tid", goodsParamBean.tid);
        map.put("item_source", goodsParamBean.item_source);
        map.put("source_type[0]", "1");
        map.put("source_type[1]", "4");
        if (null != queryParam.sort && queryParam.sort != GoodsQueryParam.Sort.NONE) {
            map.put("sort", queryParam.sort.value);
        }
        if (queryParam.has_coupon > 0) {
            map.put("has_coupon", "1");
        }
        if (queryParam.postage > 0) {
            map.put("postage", "1");
        }
        return getApi().getHomeGoodsList(map);
    }

    /**
     * 首页-猜你喜欢
     *
     * @return
     */
    public static Observable<ResponseDataArray<GoodsEntity>> likeGoods(int page, int row) {
        return getApi().likeGoods(page, row);
    }

    /**
     * 获取广告列表
     *
     * @param pos 1 开屏广告
     *            2 首页
     *            3 首页Banner
     *            4 首页中心广告
     *            6 用户
     *            7 用户页面中心广告
     *            8 红人街
     * @return
     */
    public static Observable<ResponseDataArray<AdvertistEntity>> getAdList(String... pos) {
        StringBuilder adPositions = new StringBuilder();
        for (String item : pos) {
            adPositions.append(item).append(",");
        }
        adPositions = adPositions.deleteCharAt(adPositions.length() - 1);
        return getApi().adList(adPositions.toString());
    }


    /**
     * 广告点击
     *
     * @param id
     * @return
     */
    public static Observable<ResponseDataObject<Object>> adClick(String id) {
        return getApi().adClick(id);
    }

//    /**
//     * 获取淘宝京东活动链接
//     *
//     * @param url
//     * @param platform
//     * @return
//     */
//    public static Observable<ResponseDataObject<AdConvertEntity>> adConvertUrl(String url, String platform) {
//        return getApi().adConvertUrl(url, platform, 1);
//    }

    /**
     * 每日推荐
     *
     * @return
     */
    public static Observable<ResponseDataArray<RecommendDayEntity>> getDayRecommend() {
        return getApi().recommendDay();
    }

    /**
     * 分类
     *
     * @return
     */
    public static Observable<ResponseDataArray<CategoryEntity>> getAllCategorys() {
        return getApi().getAllCategoryList(1);
    }

    /**
     * 获取子分类
     *
     * @param pid
     * @return
     */
    public static Observable<ResponseDataArray<CategoryEntity>> getCategotyEntitysById(String pid, int level) {
        return getApi().getCategoryListById(pid, 1, level);
    }

    /**
     * 热门搜索
     *
     * @return
     */
    public static Observable<ResponseDataArray<HotSearchWord>> getHotwords() {
        return getApi().getHotWords();
    }

    /**
     * 点击热门搜索
     *
     * @param id
     * @return
     */
    public static Observable<ResponseDataObject<Object>> hotwordClick(String id) {
        return getApi().hotwordClick(id);
    }

    /**
     * 获取商品列表
     *
     * @param queryParam
     * @return
     */
    public static Observable<ResponseDataArray<GoodsEntity>> getGoods(GoodsQueryParam queryParam) {
//        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.GOODS_LIST).append("?");
//        sb.append("page=" + queryParam.page);
//        sb.append("&row=" + queryParam.row);
//        if (!TextUtils.isEmpty(queryParam.plate)) {
//            sb.append("&plate=").append(queryParam.plate);
//        }

//        if (null != queryParam.sort && queryParam.sort != GoodsQueryParam.Sort.NONE) {
//            sb.append("&sort=").append(queryParam.sort.value);
//        }
//        if (!TextUtils.isEmpty(queryParam.item_source)) {
//            sb.append("&item_source=").append(queryParam.item_source);
//        }
//        if (queryParam.has_coupon > 0) {
//            sb.append("&has_coupon=1");
//        }
//        if (queryParam.postage > 0) {
//            sb.append("&postage=1");
//        }
//        if (!TextUtils.isEmpty(queryParam.search)) {
//            sb.append("&search=").append(queryParam.search);
//        }

//        if (!TextUtils.isEmpty(queryParam.goods_id)) {
//            sb.append("&goods_id=").append(queryParam.goods_id);
//        }

//        if (!TextUtils.isEmpty(queryParam.seller_shop_id)) {
//            sb.append("&seller_shop_id=").append(queryParam.seller_shop_id);
//        }
//
//        if (!TextUtils.isEmpty(queryParam.start_price)) {
//            sb.append("&start_price=").append(queryParam.start_price);
//        }
//
//        if (!TextUtils.isEmpty(queryParam.end_price)) {
//            sb.append("&end_price=").append(queryParam.end_price);
//        }

        GoodsParamBean goodsParamBean = new GoodsParamBean();
        goodsParamBean.page = String.valueOf(queryParam.page);
        goodsParamBean.pageSize = "10";
//        goodsParamBean.tid = queryParam.category;
        goodsParamBean.item_source = queryParam.item_source == null ? "" : queryParam.item_source;
        Map<String, String> map = new HashMap();
        map.put("page", goodsParamBean.page);
        map.put("pageSize", goodsParamBean.pageSize);

        if (!TextUtils.isEmpty(queryParam.category)) {
            map.put("tid", queryParam.category);
            map.put("source_type[0]", "1");
            map.put("source_type[1]", "2");
        }
        if (!TextUtils.isEmpty(queryParam.plate)) {
            map.put("tid", queryParam.plate);
            map.put("source_type[0]", "1");
            map.put("source_type[1]", "3");
        }

        if (queryParam.isShop) {
            map.put("tid", queryParam.seller_shop_id);
            map.put("source_type[0]", "1");
            map.put("source_type[1]", "6");
        }


        map.put("item_source", goodsParamBean.item_source);
        if (!TextUtils.isEmpty(queryParam.plate)) {
            map.put("plate", queryParam.plate);
        }
        if (null != queryParam.sort && queryParam.sort != GoodsQueryParam.Sort.NONE) {
            map.put("sort", queryParam.sort.value);
        }
        if (queryParam.has_coupon > 0) {
            map.put("has_coupon", "1");
        }
        if (queryParam.postage > 0) {
            map.put("postage", "1");
        }
        if (!TextUtils.isEmpty(queryParam.search)) {
            map.put("search", queryParam.search);
        }

        if (!TextUtils.isEmpty(queryParam.goods_id)) {
            map.put("goods_id", queryParam.goods_id);
        }

        if (!TextUtils.isEmpty(queryParam.seller_shop_id)) {
            map.put("seller_shop_id", queryParam.seller_shop_id);
        }

        if (!TextUtils.isEmpty(queryParam.start_price)) {
            map.put("start_price", queryParam.start_price);
        }

        if (!TextUtils.isEmpty(queryParam.end_price)) {
            map.put("end_price", queryParam.end_price);
        }
        if (!TextUtils.isEmpty(queryParam.category)) {
            map.put("category", queryParam.category);
        }

        return getApi().getHomeGoodsList(map);
    }



    /**
     * 搜索商品列表
     *
     * @param queryParam
     * @return
     */
    public static Observable<ResponseDataArray<GoodsEntity>> searchGoods(GoodsQueryParam queryParam) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.SEARCH_GOODS).append("?");
        sb.append("page=" + queryParam.page);
        sb.append("&row=" + queryParam.row);

        if (null != queryParam.sort && queryParam.sort != GoodsQueryParam.Sort.NONE) {
            sb.append("&sort=").append(queryParam.sort.value);
        }
        if (!TextUtils.isEmpty(queryParam.item_source)) {
            sb.append("&item_source=").append(queryParam.item_source);
        }
        if (queryParam.has_coupon > 0) {
            sb.append("&has_coupon=1");
        }
        if (queryParam.postage > 0) {
            sb.append("&postage=1");
        }
        if (!TextUtils.isEmpty(queryParam.search)) {
            try {
                sb.append("&search=").append(URLEncoder.encode(queryParam.search, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append("&search=").append(queryParam.search);
            }
        }
        if (!TextUtils.isEmpty(queryParam.goods_id)) {
            sb.append("&goods_id=").append(queryParam.goods_id);
        }

        if (!TextUtils.isEmpty(queryParam.start_price)) {
            sb.append("&start_price=").append(queryParam.start_price);
        }

        if (!TextUtils.isEmpty(queryParam.end_price)) {
            sb.append("&end_price=").append(queryParam.end_price);
        }

        return getApi().getGoodList(sb.toString());
    }

    /**
     * 搜索店铺
     *
     * @param queryParam
     * @return
     */
    public static Observable<ResponseDataArray<ShopItemEntity>> searchShop(GoodsQueryParam queryParam) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.SEARCH_SHOP).append("?");
        sb.append("page=" + queryParam.page);
        sb.append("&row=" + queryParam.row);
        if (!TextUtils.isEmpty(queryParam.search)) {
            try {
                sb.append("&search=").append(URLEncoder.encode(queryParam.search, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append("&search=").append(queryParam.search);
            }
        }
        if (!TextUtils.isEmpty(queryParam.item_source)) {
            sb.append("&seller_type=").append(queryParam.item_source);
        }

        return getApi().searchShop(sb.toString());
    }

    /**
     * 搜索联想词
     *
     * @param search
     * @return
     */
    public static Observable<ResponseDataArray<SearchSlugEntity>> searchSlugList(String search) {
        return getApi().searchSlug(search);
    }

    /**
     * 推荐商铺
     *
     * @return
     */
    public static Observable<ResponseDataArray<ShopItemEntity>> getRecommendShop(GoodsQueryParam queryParam) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.SEARCH_SHOP_RECOMMEND).append("?");
        sb.append("page=" + queryParam.page);
        sb.append("&row=" + queryParam.row);

        if (!TextUtils.isEmpty(queryParam.item_source)) {
            sb.append("&seller_type=").append(queryParam.item_source);
        }
        return getApi().getRecommendShop(sb.toString());
    }

    /**
     * 图标配置
     *
     * @return
     */
    public static Observable<ResponseDataObject<IconEntity>> getIconConfig() {
        return getApi().getIconConfig();
    }

    /**
     * 社区板块
     *
     * @return
     */
    public static Observable<ResponseDataArray<PlateEntity>> getCommunityPlate() {
        return getApi().getCommunityPlate();
    }

    /**
     * 社区列表
     *
     * @param queryParam
     * @return
     */
    public static Observable<ResponseDataArray<CommunityEntity>> getCommunityList(GoodsQueryParam queryParam) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.COMMONUNITY_LIST).append("?");
        sb.append("page=" + queryParam.page);
        sb.append("&row=" + queryParam.row);

        if (!TextUtils.isEmpty(queryParam.plate)) {
            sb.append("&plate=").append(queryParam.plate);
        }
        return getApi().getCommunityList(sb.toString());
    }

    public static Observable<ResponseDataArray<CommunityEntity>> getCommunityRecommendList(GoodsQueryParam queryParam, String dayType) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.COMMUNITY_RECOMMEND_LIST).append("?");
        sb.append("page=" + queryParam.page);
        sb.append("&row=" + queryParam.row);

        if (!TextUtils.isEmpty(dayType)) {
            sb.append("&day-type=").append(dayType);
        }
        return getApi().getCommunityList(sb.toString());
    }

    /**
     * 获取我推荐的奖励记录
     *
     * @return
     */
    public static Observable<ResponseDataArray<CommunityRewardEntity>> getCommunityRewardList(int page) {
        return getApi().getCommunityRewardList(page, 10);
    }

    /**
     * 转发点击
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> clickCommunity(String id) {
        return getApi().clickCommunity(id);
    }

    /**
     * 商学院-分类
     *
     * @return
     */
    public static Observable<ResponseDataArray<ArticalCategoryEntity>> getArticalCategorys(String category_id) {
        return getApi().getArticalCategorys(category_id);
    }

    /**
     * 商学院-搜索文章
     *
     * @return
     */
    public static Observable<ResponseDataArray<ArticalEntity>> searchArticals(String search, int page) {
        return getApi().searchArticals(search, page, 10);
    }

    /**
     * 商学院-文章列表
     *
     * @return
     */
    public static Observable<ResponseDataArray<ArticalEntity>> getArticalList(String category_id, int page) {
        return getApi().getArticalList(category_id, page, 10);
    }

    /**
     * 邀请分享-海报
     */
    public static Observable<ResponseDataArray<SharePosterEntity>> getInvitePosterImgs(String code) {
        return getApi().getInvitePosterImgs(code);
    }

    /**
     * 点击次数
     *
     * @param id
     * @return
     */
    public static Observable<ResponseDataObject<Object>> clickPosterImgs(String id) {
        return getApi().clickPosterImgs(id);
    }

    /**
     * vip商品
     *
     * @return
     */
    public static Observable<ResponseDataArray<VipGoodEntity>> getVipGoods(int page) {
        return getApi().getVipGoods(page, 10);
    }

    /**
     * 友盟点击上报
     *
     * @param id
     * @return
     */
    public static Observable<ResponseDataObject<Object>> umengClickReport(String id) {
        return getApi().umengClickReport(id);
    }

    /**
     * 友盟数据上报
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> umengDataReport(String id, String userId, String content) {
        return getApi().umengDataReport(id, userId, content);
    }

    /**
     * VIP任务
     *
     * @return
     */
    public static Observable<ResponseDataObject<VipTaskEntity>> getVipTasks() {
        return getApi().getVipTasks();
    }

    /**
     * VIP任务
     *
     * @return
     */
    public static Observable<ResponseDataObject<VipTaskEntity>> getVipTasks(String uid) {
        return getApi().getVipTasks(uid);
    }

    /**
     * 剪贴板搜索
     *
     * @param queryParam
     * @return
     */
    public static Observable<ResponseDataArray<GoodsEntity>> searchClipboard(GoodsQueryParam queryParam) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.SEARCH_CLIPBOARD).append("?");
        sb.append("page=" + queryParam.page);
        sb.append("&row=" + queryParam.row);
        if (null != queryParam.sort && queryParam.sort != GoodsQueryParam.Sort.NONE) {
            sb.append("&sort=").append(queryParam.sort.value);
        }
        if (!TextUtils.isEmpty(queryParam.item_source)) {
            sb.append("&item_source=").append(queryParam.item_source);
        }
        if (queryParam.has_coupon > 0) {
            sb.append("&has_coupon=1");
        }
        if (queryParam.postage > 0) {
            sb.append("&postage=1");
        }
        if (!TextUtils.isEmpty(queryParam.search)) {
            try {
                sb.append("&search=").append(URLEncoder.encode(queryParam.search, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append("&search=").append(queryParam.search);
            }
        }
        if (!TextUtils.isEmpty(queryParam.goods_id)) {
            sb.append("&goods_id=").append(queryParam.goods_id);
        }

        if (!TextUtils.isEmpty(queryParam.start_price)) {
            sb.append("&start_price=").append(queryParam.start_price);
        }

        if (!TextUtils.isEmpty(queryParam.end_price)) {
            sb.append("&end_price=").append(queryParam.end_price);
        }

        return getApi().searchClipboard(sb.toString());
    }

    /**
     * 商品来源列表
     *
     * @return
     */
    public static Observable<ResponseDataObject<ItemSourceResponseEntity>> itemSouceList() {
        return getApi().itemSouceList();
    }

    /**
     * @return
     */

    public static Observable<ResponseDataObject<VipRightEntity>> getVipRights() {
        return getApi().getVipRights();
    }

    /**
     * 获取平台来源分享模板
     *
     * @return
     */
    public static Observable<ResponseDataObject<ShareEntity>> getShareTemp() {
        return getApi().getShareTemplate();
    }

    /**
     * 新手任务
     *
     * @param type
     * @return
     */
    public static Observable<ResponseDataObject<NewTaskReportEntity>> newTaskReport(String type) {
        return getApi().newTask(type);
    }

    /**
     * 日产任务汇报
     *
     * @param type
     * @return
     */
    public static Observable<ResponseDataObject<NewTaskReportEntity>> dayTaskReport(String type) {
        return getApi().dayTask(type);
    }


    /**
     * 根据任务编码获取星币和当前用户状态
     *
     * @return
     */
    public static Observable<ResponseDataObject<NewTaskReportEntity>> getTaskXNumber(String code) {
        return getApi().getTaskXNumber(code);
    }

    /**
     * 活动详情
     *
     * @param code
     * @return
     */
    public static Observable<ResponseDataObject<ActivityDetailEntity>> getActivityDetail(String code) {
        return getApi().getActivityDetail(code);
    }

    /**
     * 抖音带货-分类
     *
     * @return
     */
    public static Observable<ResponseDataArray<BringGoodsBean>> getDydhCatergory() {
        return getApi().getDydhCatergory();
    }

    /**
     * 抖音带货-分类列表
     *
     * @param cid
     * @return
     */
    public static Observable<ResponseDataArray<BringGoodsItemBean>> getDydhList(String cid, int page) {
        return getApi().getDydhList(cid, page, 10);
    }

    /**
     * 获取安全域名
     *
     * @return
     */
    public static Observable<ResponseDataArray<String>> doGetSafeDomain() {
        return getApi().getSafeDomain();
    }

    //-----------------------------本地缓存--------------
    private static final String SEARCH_HISTORY = "SEARCH_HISTORY";
    private static final String SEARCH_HOT = "SEARCH_HOT";
    private static final String ICON_CONFIG = "ICON_CONFIG";
    private static final String ITEM_SOURCES = "ITEM_SOURCES";
    private static final String VIP_RIGHTS = "VIP_RIGHTS";
    private static final String SHARE_TEMP = "SHARE_TEMP";
    private static final String SAFE_DOMAIN = "SAFE_DOMAIN";

    /**
     * 获取历史搜索
     *
     * @return
     */
    public static List<String> getSearchHistory() {
        List<String> searchList = new ArrayList<>();
        String flightHistory = SPUtil.getString(SEARCH_HISTORY, null);
        if (TextUtils.isEmpty(flightHistory)) {
            return searchList;
        }
        String[] items = flightHistory.split(",");
        searchList = Arrays.asList(items);

        //倒序
        Collections.reverse(searchList);
        //去重
        List listTemp = new ArrayList();
        for (int i = 0; i < searchList.size(); i++) {
            if (!listTemp.contains(searchList.get(i))) {
                listTemp.add(searchList.get(i));
            }
        }
        //最多10个
        if (listTemp.size() > 10) {
            listTemp = listTemp.subList(0, 10);

            //只保留10个
            List<String> savedList = new ArrayList<>(listTemp);
            Collections.reverse(savedList);
            StringBuffer stringBuffer = new StringBuffer();
            for (String item : savedList) {
                stringBuffer.append(item).append(",");
            }
            stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            SPUtil.setString(SEARCH_HISTORY, stringBuffer.toString());
        }

        return listTemp;
    }

    /**
     * 保存搜索历史
     */
    public static void addSearchHistory(String keyword) {
        String searchHistory = SPUtil.getString(SEARCH_HISTORY, null);
        if (!TextUtils.isEmpty(searchHistory)) {
            searchHistory += "," + keyword;
        } else {
            searchHistory = keyword;
        }
        SPUtil.setString(SEARCH_HISTORY, searchHistory);
    }

    /**
     * 清除搜索历史
     */
    public static void clearSearchHistory() {
        SPUtil.setString(SEARCH_HISTORY, null);
    }

    public static void saveHotWords(List<HotSearchWord> hotSearchWords) {
        if (null == hotSearchWords || hotSearchWords.isEmpty()) {
            SPUtil.setString(SEARCH_HOT, null);
            return;
        }
        String json = new Gson().toJson(hotSearchWords, new TypeToken<List<HotSearchWord>>() {
        }.getType());
        SPUtil.setString(SEARCH_HOT, json);
    }

    public static List<HotSearchWord> getLocalHotwords() {
        String string = SPUtil.getString(SEARCH_HOT);
        if (!TextUtils.isEmpty(string)) {
            return new Gson().fromJson(string, new TypeToken<List<HotSearchWord>>() {
            }.getType());
        }
        return new ArrayList<>();
    }

    public static int getIcon() {
        return SPUtil.getInt(ICON_CONFIG, 0);
    }

    public static void setIcon(int icon) {
        SPUtil.setInt(ICON_CONFIG, icon);
    }

    public static ShareEntity getShareConfig() {
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.B = new ShareEntity.ShareBean("復製这条淘口令\n" +
                "\n" +
                "进入【Tao宝】即可抢购", "復製这条淘口令\n" +
                "\n" +
                "进入【Tao宝】即可抢购");
        shareEntity.C = shareEntity.B;
        shareEntity.P = new ShareEntity.ShareBean("点击这里抢购地址\n" +
                "\n" +
                "进入【拼多多】即可抢购", "点击这里抢购地址\n" +
                "\n" +
                "进入【拼多多】即可抢购");
        shareEntity.D = new ShareEntity.ShareBean("点击这里抢购地址\n" +
                "\n" +
                "进入【京东】即可抢购", "点击这里抢购地址\n" +
                "\n" +
                "进入【京东】即可抢购");
        String shareTemp = SPUtil.getString(SHARE_TEMP);
        if (!TextUtils.isEmpty(shareTemp)) {
            shareEntity = new Gson().fromJson(shareTemp, new TypeToken<ShareEntity>() {
            }.getType());
        }
        return shareEntity;
    }

    public static void saveVipRights(VipRightEntity entities) {
        if (null == entities) {
            return;
        }
        try {
            Gson gson = new Gson();
            String data = gson.toJson(entities);
            SPUtil.setString(VIP_RIGHTS, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static VipRightEntity getVipRight() {
        String str = SPUtil.getString(VIP_RIGHTS);
        VipRightEntity entity = null;
        if (!TextUtils.isEmpty(str)) {
            entity = new Gson().fromJson(str, VipRightEntity.class);
        }
        return entity;
    }

    public static String getLevelIcon(int level) {
        try {
            VipRightEntity vipRight = getVipRight();
            if (null != vipRight) {
                return vipRight.getLevelRight(level).icon;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveSafeDomain(List<String> domains) {
        if (null == domains || domains.isEmpty()) {
            SPUtil.setString(SAFE_DOMAIN, null);
            return;
        }
        String json = new Gson().toJson(domains, new TypeToken<List<HotSearchWord>>() {
        }.getType());
        SPUtil.setString(SAFE_DOMAIN, json);
    }

    public static List<String> getSafeDomain() {
        String string = SPUtil.getString(SAFE_DOMAIN);
        if (!TextUtils.isEmpty(string)) {
            return new Gson().fromJson(string, new TypeToken<List<String>>() {
            }.getType());
        }
        return new ArrayList<>();
    }

}
