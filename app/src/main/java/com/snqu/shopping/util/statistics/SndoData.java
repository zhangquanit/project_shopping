package com.snqu.shopping.util.statistics;

import com.android.util.LContext;
import com.sndodata.analytics.android.sdk.PropertyBuilder;
import com.sndodata.analytics.android.sdk.SndoDataAPI;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.data.home.entity.RecommendDayEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.util.GoodsMathUtil;

import org.json.JSONObject;

import java.util.Map;

/**
 * 深度数据汇报
 *
 * @author 张全
 */
public class SndoData {

    /**
     * 1.首页banner
     */
    public static final String XLT_EVENT_HOME_BANNER = "xlt_event_home_banner";

    /**
     * 2.首页视频教程
     */
    public static final String XLT_EVENT_HOME_COURSE = "xlt_event_course";

    /**
     * 3.首页金刚区点击
     */
    public static final String XLT_EVENT_JGQ = "xlt_event_jgq";

    /**
     * 4.首页板块点击
     */
    public static final String XLT_EVENT_PLATE = "xlt_event_plate";

    /**
     * 5.首页每日推荐
     */
    public static final String XLT_EVENT_HOME_RECOMMEDN_DAY = "xlt_event_home_recommend_day";

    /**
     * 6.首页为你推荐等分类点击
     */
    public static final String XLT_EVENT_HOME_RECOMMEDN = "xlt_event_home_recommend";

    /**
     * 7.首页tab点击
     */
    public static final String XLT_EVENT_HOME_TAB = "xlt_event_home_tab";

    /**
     * 8.推荐位点击
     */
    public static final String XLT_EVENT_RECOMMEN_POSITION_CLICK = "recommend_position_click";

    /**
     * 9.推荐位展示
     */
    public static final String XLT_EVENT_RECOMMEN_POSITION_SHOW = "recommend_position_show";

    /**
     * 10.个人中心点击专属推荐和我的足迹事件
     */
    public static final String XLT_EVENT_USER_CATEGORY = "xlt_event_user_category";

    /**
     * 11.复制链接搜索
     */
    public static final String XLT_EVENT_POPUP = "xlt_event_popup";


    /**
     * 首页扫一扫
     */
    public static final String XLT_EVENT_HOME_SCAN = "xlt_event_home_scan";

    /**
     * 首页搜索
     */
    public static final String XLT_EVENT_HOME_SEARCH = "xlt_event_home_search";


    /**
     * 搜索框搜索
     * xlt_item_search_keyword
     */
    public static final String XLT_EVENT_SEARCH = "xlt_event_search";

    /**
     * 搜索结果点击
     */
    public static final String XLT_EVENT_SEARCHRESULTCLICK = "SearchResultClick";


    /**
     * 首页模块点击
     * *  xlt_item_id:_id
     * *  xlt_item_title:title
     */
    public static final String XLT_EVENT_HOME_MODULCLICK = "xlt_event_home_modulclick";

    /**
     * 商品详情领取优惠券事件
     * 商品信息
     * xlt_item_id:_id
     * xlt_item_title:item_title
     * xlt_item_source:item_source
     * 优惠券信息
     * xlt_item_coupon_amount:amount
     * xlt_item_coupon_id:coupon_id
     */
    public static final String XLT_EVENT_GOODDETAIL_MANAGE = "xlt_event_gooddetail_coupon";

    /**
     * 商品进入店铺事件
     * 商品信息
     * xlt_item_id:_id
     * xlt_item_title:item_title
     * xlt_item_source:item_source
     * 店铺信息
     * xlt_item_id:seller_shop_id
     * xlt_item_title:seller_shop_name
     * xlt_item_source:seller_type
     */
    public static final String XLT_EVENT_GOODDETAIL_SHOP = "xlt_event_gooddetail_shop";
    /**
     * 商品详情点击保障事件
     * 商品信息
     * xlt_item_id:_id
     * xlt_item_title:item_title
     * xlt_item_source:item_source
     */
    public static final String XLT_EVENT_GOODDETAIL_GUARANTEE = "xlt_event_gooddetail_guarantee";
    /**
     * 商品详情点击参数事件
     * 商品信息
     * xlt_item_id:_id
     * xlt_item_title:item_title
     * xlt_item_source:item_source
     */
    public static final String XLT_EVENT_GOODDETAIL_PARAMETER = "xlt_event_gooddetail_parameter";//商品详情点击参数事件
    /**
     * 商品详情点击首页事件
     * 商品信息
     * xlt_item_id:_id
     * xlt_item_title:item_title
     * xlt_item_source:item_source
     */
    public static final String XLT_EVENT_GOODDETAIL_HOME = "xlt_event_gooddetail_home";
    /**
     * 商品详情点击收藏事件
     * xlt_item_id:_id
     * xlt_item_title:item_title
     * xlt_item_source:item_source
     * xlt_item_collected:1 收藏 0 取消收藏
     */
    public static final String XLT_EVENT_GOODDETAIL_FAVORITE = "xlt_event_gooddetail_favorite";
    /**
     * 商品详情点击分享事件
     * xlt_item_id:_id
     * xlt_item_title:item_title
     * xlt_item_source:item_source
     * 分享赚金额
     * xlt_item_rebate_amount:xlt_amount
     */
    public static final String XLT_EVENT_GOODDETAIL_SHARE = "xlt_event_gooddetail_share";
    /**
     * 商品详情点击下单事件
     * xlt_item_id:_id
     * xlt_item_title:item_title
     * xlt_item_source:item_source
     * 下单返利金额
     * xlt_item_rebate_amount:xlt_amount
     */
    public static final String XLT_EVENT_GOODDETAIL_BUY = "xlt_event_gooddetail_buy";

    /**
     * 商品详情点击复制口令事件
     * xlt_item_id:_id
     * xlt_item_title:item_title
     * xlt_item_source:item_source
     */
    public static final String XLT_EVENT_GOODDETAIL_COPYLINK = "xlt_event_gooddetail_copylink";
    /**
     * 商品详情查看全部事件
     * xlt_item_id:_id
     * xlt_item_title:item_title
     * xlt_item_source:item_source
     */
    public static final String XLT_EVENT_GOODDETAIL_LOOKALL = "xlt_event_gooddetail_lookall";

    /**
     * 个人中心点击设置事件
     */
    public static final String XLT_EVENT_USER_SETTING = "xlt_event_user_setting";
    /**
     * 个人中心点击订单事件
     * xlt_item_title:对应订单title（全部、即将到账、已到账、已失效）
     * xlt_item_source_title: 对应平台title (全部、淘宝、天猫、京东、拼多多)
     */
    public static final String XLT_EVENT_USER_ORDER = "xlt_event_user_order";

    /**
     * 团队订单
     * xlt_item_title:对应订单title（全部、即将到账、已到账、已失效）
     * xlt_item_source_title: 对应平台title (全部、淘宝、天猫、京东、拼多多)
     */
    public static final String XLT_EVENT_GROUP_ORDER = "xlt_event_group_order";
    /**
     * 任务点击事件
     */
    public static final String XLT_EVENT_USER_TASK = "xlt_event_user_task";
    /**
     * 联系导师
     */
    public static final String XLT_EVENT_USER_MENTOR = "xlt_event_user_mentor";

    /**
     * 个人中心点击找回订单
     */
    public static final String XLT_EVENT_GETBACK_ORDER = "xlt_event_getback_order";
    /**
     * 个人中心点击常见问题事件
     */
    public static final String XLT_EVENT_USER_PROBLEM = "xlt_event_user_problem";

    /**
     * 个人中心点击地推
     */
    public static final String XLT_EVENT_USER_GroundPush = "xlt_event_user_groundpush";

    /**
     * 个人中心点击转链
     */
    public static final String XLT_EVENT_USER_SPINCHAIN = "xlt_event_user_spinchain";

    /**
     * 个人中心点击出单榜
     */
    public static final String XLT_EVENT_USER_STANDLIST = "xlt_event_user_standlist";


    /**
     * 仅显示有优惠券按钮点击
     */
    public static final String XLT_EVENT_FILTER_COUPON = "xlt_event_filter_coupon";

    /**
     * 广告位点击
     * xlt_item_id:_id
     * xlt_item_title:title
     * xlt_item_ad_platform:platform
     * xlt_item_ad_position:show_position
     */
    public static final String XLT_EVENT_AD = "xlt_event_ad";

    /**
     * 分类点击(分类tab页面)
     * xlt_item_id:_id
     * xlt_item_title:title
     * xlt_item_level:level
     */
    public static final String XLT_EVENT_CATEGORY = "xlt_event_category";

    /**
     * 首页分类点击
     * xlt_item_id:_id
     * xlt_item_title:name
     * xlt_item_level:level
     */
    public static final String XLT_EVENT_HOME_CATEGORY = "xlt_event_home_category";

    /**
     * 筛选事件
     * xlt_item_title:对应点击title
     */
    public static final String XLT_EVENT_FILTER = "xlt_event_filter";

    /**
     * 商品点击
     * xlt_item_id:_id
     * xlt_item_title:item_title
     * xlt_item_source:item_source
     * xlt_item_good_id:item_id
     * xlt_item_place: 所在位置 参照下面Place定义
     * xlt_item_good_position:商品位于列表中的位置index
     * +所属分类或版块
     */
    public static final String XLT_EVENT_GOODS = "xlt_event_goods";

    /**
     * 商品展示
     * xlt_item_goodslist_count:count(列表所加载的商品数量)
     * +所属分类或版块
     */
    public static final String XLT_EVENT_GOODSLIST_COUNT = "xlt_event_goodslist_count";

    /**
     * 自购收益
     */
    public static final String XLT_EVENT_SELF_EARNINGS = "xlt_event_self_earnings";

    /**
     * 团购收益
     */
    public static final String XLT_EVENT_GROUP_EARNINGS = "xlt_event_group_earnings";

    /**
     * 邀请页-分享海报
     */
    public static final String XLT_EVENT_APP_POSTER_SHARE = "xlt_event_app_poster_share";

    /**
     * 邀请页-保存海报到相册
     */
    public static final String XLT_EVENT_APP_POSTER_SAVE_ALBUM = "xlt_event_app_poster_save_album";

    /**
     * 邀请页-复制分享链接
     */
    public static final String XLT_EVENT_APP_LINK_COPY = "xlt_event_app_link_copy";

    /**
     * 个人中心-点击收藏事件
     */
    public static final String XLT_EVENT_USER_COLLECTION = "xlt_event_user_collection";


    //---------------------------------属性定义--------------------------------------------------
    //成员元素
    public static final String XLT_ITEM_ID = "xlt_item_id";
    public static final String XLT_ITEM_PID = "xlt_item_pid";
    public static final String XLT_ITEM_TITLE = "xlt_item_title";


    //所属分类
    public static final String XLT_ITEM_FIRSTCate_ID = "xlt_item_firstcate_id"; //一级分类id
    public static final String XLT_ITEM_FIRSTCate_TITLE = "xlt_item_firstcate_title"; //一级分类名称
    public static final String XLT_ITEM_SECONDCate_ID = "xlt_item_secondcate_id";  //二级分类id
    public static final String XLT_ITEM_SECONDCate_TITLE = "xlt_item_secondcate_title"; //二级分类名称
    public static final String XLT_ITEM_THIRDCate_ID = "xlt_item_thirdcate_id";  //三级分类id
    public static final String XLT_ITEM_THIRDCate_TITLE = "xlt_item_thirdcate_title"; //三级分类名称
    public static final String XLT_ITEM_LEVEL = "xlt_item_level";//分类级别
    public static final String XLT_ITEM_CLASSIFY_NAME = "classify_name";//分类名称

    //所属版块
    public static final String XLT_ITEM_FIRSTPLATE_ID = "xlt_item_firstplatle_id"; //一级板块id
    public static final String XLT_ITEM_FIRSTPLATE_TITLE = "xlt_item_firstplatle_title"; //一级板块名称
    public static final String XLT_ITEM_SECONDPLATE_ID = "xlt_item_secondplatle_id";  //二级板块id
    public static final String XLT_ITEM_SECONDPLATE_TITLE = "xlt_item_secondplatle_title"; //二级板块名称

//    public static final String XLT_ITEM_GOOD_POSITION = "xlt_item_good_position";//商品所在列表的位置
    public static final String XLT_ITEM_GOODSLIST_COUNT = "xlt_item_goodslist_count"; //加载商品数量

    //搜索关键词
    public static final String XLT_ITEM_SEARCH_KEYWORD = "xlt_item_search_keyword"; //搜索关键词

    //广告
    public static final String XLT_ITEM_AD_POSITION = "xlt_item_ad_position"; //广告位置
    public static final String XLT_ITEM_AD_PLATFORM = "xlt_item_ad_platform"; //广告来源


    public static final String XLT_ITEM_GOOD_ID = "xlt_item_good_id"; //商品item_id
    public static final String XLT_ITEM_SOURCE = "xlt_item_source"; //商品来源，C 淘宝 B 天猫 D京东
    public static final String XLT_ITEM_SOURCE_TITLE = "xlt_item_source_title"; //来源名称
    public static final String XLT_ACTIVITY_TIME = "activity_time";//活动时间范围
    public static final String XLT_ITEM_COUPON_ID = "xlt_item_coupon_id";  //优惠券id
    public static final String XLT_ITEM_COUPON_AMOUNT = "xlt_item_coupon_amount"; //优惠券金额
    public static final String XLT_ITEM_REBATE_AMOUNT = "xlt_item_rebate_amount";//返利金额

    public static final String XLT_ITEM_COLLECTED = "xlt_item_collected"; // 1、收藏  0 取消收藏
    /*
     * 店铺元素
     */
    public static final String XLT_ITEM_SHOP_ID = "xlt_item_shop_id";         //店铺ID
    public static final String XLT_ITEM_SHOP_TITLE = "xlt_item_shop_title";   //店铺Title
    public static final String XLT_ITEM_SHOP_TYPE = "xlt_item_shop_type";     //店铺Type

//    public static final String XLT_ITEM_FIRSTCATE_TITLE = "xlt_item_firstcate_title";
//    public static final String XLT_ITEM_THIRDCATE_TITLE = "xlt_item_thirdcate_title";
//    public static final String XLT_ITEM_SECONDCATE_TITLE = "xlt_item_secondcate_title";

    public static final String XLT_GOOD_NAME = "good_name";
    public static final String XLT_GOOD_ID = "good_id";
    /**
     * 首页-为你推荐：homepage_unique_recommend
     * 首页-猜你喜欢  homepage_unique_guess_like
     * 首页-推荐天猫：homepage_unique_tm_recommend
     * 首页-推荐淘宝：homepage_unique_tb_recommend
     * 首页-推荐京东：homepage_unique_jd_recommend
     * 收藏夹-猜你喜欢：collection_guessyoulike
     * 详情页-店铺推荐：item_details_recommend
     * 详情页-大家都在买：item_details_popular
     * 我的-专属推荐：personal_center_exclusive_recommend
     * 我的-我的足迹：personal_center_browsing_history
     */
    public static final String XLT_ITEM_PLACE = "xlt_item_place";// 商品点击所属位置

    public static enum PLACE {
        homepage_unique_recommend,
        homepage_unique_guess_like,
        homepage_unique_tm_recommend,
        homepage_unique_tb_recommend,
        homepage_unique_jd_recommend,
        homepage_unique_pdd_recommend,
        collection_guessyoulike,
        item_details_recommend,
        item_details_popular,
        personal_center_exclusive_recommend,
        personal_center_browsing_history,

    }

    /**
     * 事件汇报
     *
     * @param eventName
     * @param propertys
     */
    public static void event(String eventName, Object... propertys) {
        try {
            if (null != propertys && propertys.length > 0) {
                PropertyBuilder propertyBuilder = PropertyBuilder.newInstance();
                for (int i = 0; i < propertys.length; i += 2) {
                    propertyBuilder.append(propertys[i], propertys[i + 1] == null ? "null" : propertys[i + 1]);
                }
                SndoDataAPI.sharedInstance(LContext.getContext()).track(eventName, propertyBuilder.toJSONObject());
                return;
            }
            SndoDataAPI.sharedInstance(LContext.getContext()).track(eventName);
        } catch (Exception e) {
            if (null != propertys && propertys.length > 0) {
                PropertyBuilder propertyBuilder = PropertyBuilder.newInstance();
                for (int i = 0; i < propertys.length; i += 2) {
                    propertyBuilder.append(propertys[i], propertys[i + 1]);
                }
                SndoDataAPI.sharedInstance(LContext.getContext()).track(eventName, propertyBuilder.toJSONObject());
                return;
            }
            SndoDataAPI.sharedInstance(LContext.getContext()).track(eventName);
        }
    }

    /**
     * 事件汇报
     *
     * @param eventName
     * @param property
     */
    public static void event(String eventName, Map<String, Object> property) {
        JSONObject jsonObject = null;
        if (null != property && !property.isEmpty()) {
            jsonObject = PropertyBuilder.newInstance().append(property).toJSONObject();
        }
        SndoDataAPI.sharedInstance(LContext.getContext()).track(eventName, jsonObject);
    }

    /**
     * 登录
     */
    public static void login() {
        UserEntity user = UserClient.getUser();
        JSONObject jsonObject = PropertyBuilder.newInstance().append("is_login", null != user ? true : false).toJSONObject();
        SndoDataAPI.sharedInstance(LContext.getContext()).registerSuperProperties(jsonObject);
    }

    /**
     * 退出登录
     */
    public static void loginOut() {
        JSONObject jsonObject = PropertyBuilder.newInstance().append("is_login", false).toJSONObject();
        SndoDataAPI.sharedInstance(LContext.getContext()).registerSuperProperties(jsonObject);
    }

    /**
     * 商品位置点击汇报
     *
     * @param goodsEntity
     * @param pos
     * @param place
     */
    public static void reportGoods(GoodsEntity goodsEntity, int pos, String place) {

        event(XLT_EVENT_GOODS,
                SndoData.XLT_ITEM_COUPON_AMOUNT, GoodsMathUtil.calcCoupon_amount(goodsEntity),
                SndoData.XLT_ITEM_REBATE_AMOUNT,GoodsMathUtil.calcRebate_amount(goodsEntity),
                SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
                SndoData.XLT_ITEM_FIRSTPLATE_ID, "null",
                SndoData.XLT_ITEM_FIRSTPLATE_TITLE, "null",
                SndoData.XLT_GOOD_NAME, goodsEntity.getItem_title(),
                SndoData.XLT_GOOD_ID, goodsEntity.getGoods_id(),
                SndoData.XLT_ITEM_SOURCE, goodsEntity.getItem_source(),
                SndoData.XLT_ITEM_PLACE,String.valueOf(pos),
                SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
                SndoData.XLT_ITEM_SECONDPLATE_ID, "null",
                SndoData.XLT_ITEM_FIRSTPLATE_TITLE, "null",
                SndoData.XLT_ITEM_THIRDCate_TITLE, "null"
        );
    }

    /**
     * 板块商品点击汇报
     *
     * @param goodsEntity
     * @param pos
     * @param place
     * @param first_id
     * @param first_name
     * @param second_id
     * @param second_name
     */
    public static void reportGoodsByPlate(GoodsEntity goodsEntity, int pos, String place, String first_id, String first_name, String second_id, String second_name) {
        event(XLT_EVENT_GOODS,
                SndoData.XLT_ITEM_COUPON_AMOUNT, GoodsMathUtil.calcCoupon_amount(goodsEntity),
                SndoData.XLT_ITEM_REBATE_AMOUNT, GoodsMathUtil.calcRebate_amount(goodsEntity),
                SndoData.XLT_ITEM_FIRSTCate_TITLE, "null",
                SndoData.XLT_GOOD_NAME, goodsEntity.getItem_title(),
                SndoData.XLT_GOOD_ID, goodsEntity.getGoods_id(),
                SndoData.XLT_ITEM_SOURCE, goodsEntity.getItem_source(),
                SndoData.XLT_ITEM_PLACE,String.valueOf(pos),
                SndoData.XLT_ITEM_SECONDCate_TITLE, "null",
                SndoData.XLT_ITEM_THIRDCate_TITLE, "null",
                XLT_ITEM_FIRSTPLATE_ID, first_id,
                XLT_ITEM_FIRSTPLATE_TITLE, first_name,
                XLT_ITEM_SECONDPLATE_ID, second_id,
                XLT_ITEM_SECONDPLATE_TITLE, second_name
        );

//        event(XLT_EVENT_GOODS,
//                XLT_ITEM_ID, goodsEntity.get_id(),
//                XLT_ITEM_TITLE, goodsEntity.getItem_title(),
//                XLT_ITEM_SOURCE, goodsEntity.getItem_source(),
//                XLT_ITEM_GOOD_ID, goodsEntity.getItem_id(),
//                XLT_ITEM_GOOD_POSITION, pos,
//                XLT_ITEM_PLACE, place,
//                XLT_ITEM_FIRSTPLATE_ID, first_id,
//                XLT_ITEM_FIRSTPLATE_TITLE, first_name,
//                XLT_ITEM_SECONDPLATE_ID, second_id,
//                XLT_ITEM_SECONDPLATE_TITLE, second_name
//        );
    }

    /**
     * 分类商品点击汇报
     *
     * @param goodsEntity
     * @param pos
     * @param place
     * @param firstCategory
     * @param thirdCategory
     */
    public static void reportGoodsByCategory(GoodsEntity goodsEntity, int pos, String place, CategoryEntity firstCategory, CategoryEntity thirdCategory) {
        event(XLT_EVENT_GOODS,
                SndoData.XLT_ITEM_COUPON_AMOUNT, GoodsMathUtil.calcCoupon_amount(goodsEntity),
                SndoData.XLT_ITEM_REBATE_AMOUNT, GoodsMathUtil.calcRebate_amount(goodsEntity),
                SndoData.XLT_GOOD_NAME, goodsEntity.getItem_title(),
                SndoData.XLT_GOOD_ID, goodsEntity.getGoods_id(),
                SndoData.XLT_ITEM_SOURCE, goodsEntity.getItem_source(),
                SndoData.XLT_ITEM_PLACE,String.valueOf(pos),
                XLT_ITEM_FIRSTCate_ID, null != firstCategory ? firstCategory._id : "null",
                XLT_ITEM_FIRSTCate_TITLE, null != firstCategory ? firstCategory.getName() : "null",
                XLT_ITEM_SECONDCate_ID, "",
                XLT_ITEM_SECONDCate_TITLE, "",
                XLT_ITEM_THIRDCate_ID, null != thirdCategory ? thirdCategory._id : "null",
                XLT_ITEM_THIRDCate_TITLE, null != thirdCategory ? thirdCategory.getName() : "null"
        );
    }

    /**
     * 广告点击汇报
     *
     * @param advertistEntity
     */
    public static void reportAd(AdvertistEntity advertistEntity) {
        event(XLT_EVENT_AD,
                XLT_ITEM_ID, advertistEntity._id,
                XLT_ITEM_TITLE, advertistEntity.name,
                XLT_ITEM_AD_PLATFORM, advertistEntity.platform,
                XLT_ITEM_AD_POSITION, advertistEntity.show_position
        );
    }

    /**
     * 每日推荐点击汇报
     *
     * @param recommendDayEntity
     * @param place
     */
    public static void reportDayRecommend(RecommendDayEntity recommendDayEntity, String place) {
        SndoData.event(SndoData.XLT_EVENT_HOME_RECOMMEDN_DAY,
                SndoData.XLT_GOOD_ID, recommendDayEntity._id,
                "xlt_item_firstcate_title", "null",
                "xlt_item_thirdcate_title", "null",
                "xlt_item_secondcate_title", "null",
                "good_name", recommendDayEntity.item_title,
                "xlt_item_source", recommendDayEntity.item_source

        );
    }

    /**
     * 分类商品数量加载汇报
     *
     * @param count
     */
    public static void reportGoodsCountByCategory(int count, CategoryEntity firstCategory, CategoryEntity thirdCategory) {
//        event(XLT_EVENT_GOODSLIST_COUNT,
//                XLT_ITEM_GOODSLIST_COUNT, count,
//                XLT_ITEM_PLACE, "",
//                XLT_ITEM_FIRSTCate_ID, null != firstCategory ? firstCategory._id : "",
//                XLT_ITEM_FIRSTCate_TITLE, null != firstCategory ? firstCategory.getName() : "",
//                XLT_ITEM_SECONDCate_ID, "",
//                XLT_ITEM_SECONDCate_TITLE, "",
//                XLT_ITEM_THIRDCate_ID, null != thirdCategory ? thirdCategory._id : "",
//                XLT_ITEM_THIRDCate_TITLE, null != thirdCategory ? thirdCategory.getName() : ""
//        );
    }

    /**
     * 板块商品加载数量汇报
     *
     * @param count
     * @param first_id
     * @param first_name
     * @param second_id
     * @param second_name
     */
    public static void reportGoodsCountByPlate(int count, String first_id, String first_name, String second_id, String second_name) {

//        event(XLT_EVENT_GOODSLIST_COUNT,
//                XLT_ITEM_GOODSLIST_COUNT, count,
//                XLT_ITEM_PLACE, "",
//                XLT_ITEM_FIRSTPLATE_ID, first_id,
//                XLT_ITEM_FIRSTPLATE_TITLE, first_name,
//                XLT_ITEM_SECONDPLATE_ID, second_id,
//                XLT_ITEM_SECONDPLATE_TITLE, second_name
//        );
    }

    /**
     * 点击猜你喜欢item
     *
     * @param goodsEntity
     */
    public static void reportGuessLikeItem(GoodsEntity goodsEntity, int pos) {
        event(SndoData.XLT_EVENT_RECOMMEN_POSITION_CLICK,
                "is_power", true,
                "power_position", "猜你喜欢",
                "power_model", "null",
                "activity_time", "null",
                SndoData.XLT_GOOD_ID, goodsEntity.getGoods_id(),
                "xlt_item_firstcate_title", "null",
                "xlt_item_thirdcate_title", "null",
                "xlt_item_secondcate_title", "null",
                "good_name", goodsEntity.getItem_title(),
                SndoData.XLT_ITEM_PLACE, String.valueOf(pos),
                SndoData.XLT_ITEM_SOURCE, goodsEntity.getItem_source()
        );
    }

//    /**
//     * 推荐位展示
//     * @param goodsEntities
//     */
//    public static void reportGuessGoodList(List<GoodsEntity>goodsEntities){
//        event(SndoData.XLT_EVENT_RECOMMEN_POSITION_SHOW,);
//    }


}
