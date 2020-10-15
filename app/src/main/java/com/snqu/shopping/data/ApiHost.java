package com.snqu.shopping.data;

/**
 * 服务器接口
 *
 * @author 张全
 */
public final class ApiHost {
    //------------------首页
    /**
     * 首页布局
     */
    public static final String LAYOUT_INDEX = "/v3/home-page/index";
    /**
     * 获取子板块
     */
    public static final String PLATE_LIST = "plate/list";

    /**
     * 首页模块点击次数[进入次数]
     */
    public static final String LAYOUT_INDEX_CLICK = "/v3/home-page/click";
    /**
     * 获取板块筛选条件
     */
    public static final String PLATE_OPTIONS = "plate/get-options";
    /**
     * 首页获取推荐商品
     */
    public static final String HOME_RECOMMEND_GOODS = "goods/index";
    /**
     * 首页广告
     */
    public static final String HOME_AD = "/v3/ad/index";

    /**
     * 获取淘宝授权地址
     */
    public static final String TB_AUTH = "taobao/get-auth-url";

    /**
     * 分类
     */
    public static final String CATEGORY_LIST = "category/list";
    /**
     * 商品列表
     */
    public static final String GOODS_LIST = "goods/list";

    /**
     * 热门搜索
     */
    public static final String SEARCH_HOT = "hot-search";
    /**
     * 点击热门搜索
     */
    public static final String SEARCH_HOT_CLICK = "hot-search/click";
    /**
     * 搜索商品
     */
    public static final String SEARCH_GOODS = "v2/goods/search";

    /**
     * 判断商品是否可以推荐
     */
    public static final String COMMUNITY_GOODS_RECM = "v2/community-goods-recm/can-share";

    /**
     * 点击商品推荐
     */
    public static final String COMMUNITY_GOODS_RECM_CLICK = "click_community-goods-recm";

    /**
     * 搜索店铺
     */
    public static final String SEARCH_SHOP = "seller/search";

    /**
     * 搜索联想词
     */
    public static final String SEARCH_SLUG = "search/slug";

    /**
     * 推荐店铺
     */
    public static final String SEARCH_SHOP_RECOMMEND = "seller/recm";

    //----------------------红人街
    /**
     * 爆款分类
     */
    public static final String RED_CATEGORY = "hot-street/red-cate";
    /**
     * 红人街商品列表
     */
    public static final String RED_GOODS = "hot-street/red-good";
    /**
     * 网红店 店铺列表
     */
    public static final String RED_SHOPS = "hot-street/seller";

    /**
     * 图标配置
     */
    public static final String ICON_CONFIG = "client/ico";

    /**
     * 店铺详情
     */
    public static final String RED_SHOP_DETAIL = "seller/info";

    /**
     * 好物说
     */
    public static final String RED_HAOWU = "hot-street/good-item";
    /**
     * 大V推荐
     */
    public static final String RED_BIGV = "hot-street/v-recommend";
    /**
     * 大V主页
     */
    public static final String RED_BIGV_DETAIL = "hot-street/v-detail";

    /**
     * 大V推荐商品
     */
    public static final String RED_BIGV_GOODS = "hot-street/v-goods";

    //--------------发圈
    /**
     * 发圈板块
     */
    public static final String COMMONUNITY_PLATE = "v3/community/get-plate";
    /**
     * 发圈社区列表
     */
    public static final String COMMONUNITY_LIST = "community/list";
    /**
     * 转发点击
     */
    public static final String COMMUNITY_CLICK = "community/click";
    /**
     * 好物推荐列表
     */
    public static final String COMMUNITY_RECOMMEND_LIST = "v2/community-goods-recm/list";

    /**
     * 邀请好友-海报列表
     */
    public static final String INVITE_IMGS = "v2/invite-code-images/list";

    /**
     * 海报点击
     */
    public static final String INVITE_IMG_CLICK = "invite-code-images/click";

    /**
     * 商学院——搜索文章
     */
    public static final String COMMUNITY_ARTICAL_SEARCH = "v2/article/search";
    /**
     * 商学院——文章列表
     */
    public static final String COMMUNITY_ARTICAL_LIST = "v2/article/list";
    /**
     * 商学院——热门分类
     */
    public static final String COMMUNITY_ARTICAL_HOT_CAT = "v2/article/hot-cate";


    //--------------广告
    /**
     * 广告列表
     */
    public static final String AD_LIST = "v3/ad/list";

    /**
     * 广告点击
     */
    public static final String AD_CLICK = "v3/ad/click";
    /**
     * 获取淘宝京东活动链接
     */
//    public static final String AD_CONVERT_URL = "ad-activity/convert";
    /**
     * 每日推荐
     */
    public static final String RECOMMEND_DAY = "goods/day-recommend";

    /**
     * 解析商品url
     */
//    public static final String GOODS_DEURL = "v2/goods/de-url";

    /**
     * ]解析淘口令 还可以解析淘宝推广Url
     */
    public static final String GOODS_DECODE = "v3/de/code";

    /**
     * app升级
     */
    public static final String APP_UPDATE = "version/get-last";
    /**
     * 登陆发送验证码
     */
    public static final String LOGIN_CODE = "login/code";

    /**
     * 登录--校验验证码=》》》变更
     */
    public static final String VERIFY_CODE = "/v2/login/verify-code-chang";

    /**
     * 新版登陆发送验证码
     */
    public static final String SEND_BIND_CODE = "v2/login/send-bind-code";

    /**
     * 微信登陆
     */
    public static final String LOGIN_WX = "v2/login/wx-app-login";


    /**
     * 新版验证码登陆
     */
    public static final String NEW_CODE_LOGIN = "v2/login/code-login";

    /**
     * 微信登陆绑定手机号
     */
    public static final String WX_LOGIN_BIND_PHONE = "v2/login/bind-phone";
    /**
     * 邀请绑定
     */
    public static final String INVITE_CODE = "v2/user/bind-invite";

    /**
     * 绑定邀请码
     */
    public static final String BIND_INVITE = "/v2/login/bind";

    /**
     * 绑定微信
     */
    public static final String BIND_WX = "v2/user/bind-wechat-app";

    /**
     * 获取邀请人信息
     */
    public static final String INVITER_INFO = "v2/user/find-inviter";

    /**
     * 获取邀请人信息（无AUTH校验）
     */
    public static final String INVITER_INFO_NO_AUTH = "/v2/login/find-inviter";

    /**
     * 登录--校验验证码
     */
    public static final String FIND_INVITER_CODE = "/v2/login/verify-code";

    /**
     * 查询被分享者 电话
     */
    public static final String INVITE_CODE_PHONE = "share-log/sharer-phone";
    /**
     * 获取登录用户信息
     */
    public static final String USER_INFO = "v2/user";
    /**
     * 登出登陆
     */
    public static final String LOGIN_OUT = "user/logout";

    /**
     * 原手机号发送验证码
     */
    public static final String CHANGE_PHONE_CODE = "user/code";

    /**
     * 原手机号验证码验证
     */
    public static final String CHANGE_PHONE_VERIFY_CODE = "user/verify-code";

    /**
     * 绑定支付宝
     */
    public static final String BIND_ALIPAY = "user/bind-alipay";
    /**
     * 获取支付宝信息
     */
    public static final String ALIPAY_INFO = "user/alipay";

    /**
     * 设置给下级显示的微信ID
     */
    public static final String SET_WECHAT_ID = "v2/user/set-wechat-id";

    /**
     * 我的余额 余额信息
     */
    public static final String BALANCE_INFO = "v2/account";

    /**
     * 获取用户时间区间收益报表
     */
    public static final String ACCOUNT_INFO = "/v2/account/info";

    /**
     * 任务中心动图
     */
    public static final String AD_TASK_PAGE = "ad_task_page";

    /**
     * 更换手机号并重新登录
     */
    public static final String CHANGE_PHONE_BIND_VERIFY_CODE = "user/new-phone";

    /**
     * 个人中心 - 我的余额 - 获取可提现全部余额
     */
    public static final String ALL_BALANCE = "user-account/amount-avail";


    /**
     * 绑定支付宝前发送验证短信
     */
    public static final String ALIPAY_CODE = "user/alipay-code";

    /**
     * 推送绑定设备id
     */
    public static final String BIND_DEVICE = "user/bind-device";

    /**
     * 我的余额 - 账户明细
     */
    public static final String BALANCE_RECODE = "v2/account/changes";
    /**
     * 我的余额页面-提现
     */
    public static final String BALANCE_WITHDRAW = "v2/account/withdraw";
    /**
     * 我的足迹
     */
    public static final String SELF_FOOT_GOODS = "user-foot-print";

    /**
     * 添加收藏
     */
    public static final String ADD_COLLECTION_GOODS = "fav/add";

    /**
     * 删除收藏
     */
    public static final String DELETE_COLLECTION_GOODS = "fav/del";

    /**
     * 删除收藏更具商品id
     */
    public static final String DELETE_COLLECTION_GOODS_ITEM = "fav/del-item";
    /**
     * 清除失效收藏
     */
    public static final String CLEAR_FAIL_COLLECTION_GOODS = "fav/del-fail";

    /**
     * 获取收藏列表
     */
    public static final String FAV_LIST = "/v2/fav/list";

//    /**
//     * 获取收藏列表
//     */
//    public static final String COLLECTION_GOODS_LIST = "fav/list";
    /**
     * 获取商品基础详情数据
     */
    public static final String GOODS_DETAIL = "v2/goods/detail";

    /**
     * 更新|获取推荐语
     */
    public static final String GOODS_REC_TEXT = "v2/goods/rec-text";

    /**
     * 商品详情页
     */
    public static final String GOODS_DETAIL_DESC = "v2/goods/desc";

    /**
     * 商品列表获取
     */
    public static final String GET_GOODS_LIST = "v3/goods-list/get";

    /**
     * .是否已收藏-已登录才请求
     */
    public static final String GOODS_FAV = "v2/goods/fav";

    /**
     * 获取订单列表 单个订单 加_id参数即可
     */
    public static final String ORDER_LIST = "v2/xlt-order/index";
    /**
     * 获取订单列表 单个订单 加_id参数即可团队
     */
    public static final String ORDER_LIST_GROUP = "v2/xlt-order/team";
    /**
     * 添加分享记录
     */
    public static final String ORDER_SHARE_CODE = "share-log/add-log";

    /**
     * 抖音带货-获取列表
     */
    public static final String DYDH_LIST = "v2/activity/ac202004dydh/main/list";

    /**
     * 抖音带货-获取分类
     */
    public static final String DYDH_CATEGORY = "v2/activity/ac202004dydh/main/category";

    /**
     * 领取返利金
     */
    public static final String GET_REBATE = "order/rebate";
    /**
     * 订单找回
     */
    public static final String ORDER_RETRIEVE = "v2/xlt-order/retrieve";

    /**
     * 获取店铺推荐商品
     */
    public static final String SHOP_RECOMMEND = "seller/recommend";

    /**
     * 转链商品链接
     */
    public static final String PROMOTION_LINK = "v3/copo/cover";

    /**
     * 获取平台来源分享模板
     */
    public static final String SHARE_TEMPLATE = "v2/item-source/share-template";

    /**
     * 获取商品评论
     */
    public static final String GOODS_RATES = "goods-rate/list";

    /**
     * 获取商品评论数量
     */
    public static final String GOODS_RATES_COUNT = "goods-rate/get-count";

    /**
     * 获取商品详情页推荐商品
     */
    public static final String GOODS_DETAIL_RECOMMEND = "goods/recommend";

    /**
     * 汇报日志
     */
    public static final String LOG_REPORT = "api/report/log";

    /**
     * 添加商品浏览记录
     */
    public static final String GOODS_FOOT_RECODE = "user-foot-print/add";
    /**
     * 猜你喜欢
     */
    public static final String LIKE_GOODS = "goods/like";

    /**
     * 我的-专属推荐
     */
    public static final String RECOMMEND_USER = "goods/recommend-user";

    /**
     * 我的团队
     */
    public static final String USER_FANS = "v2/user/fans";
    /**
     * 我的团队用户列表
     */
    public static final String USER_FANS_LIST = "v2/user/fans-list";
    /**
     * 团队收益
     */
    public static final String EARNING_TEAM = "v2/account/group-info";

    /**
     * 自购收益
     */
    public static final String EARNING_SELF = "v2/account/mine-info";

    /**
     * Vip商品列表
     */
    public static final String VIP_GOODS = "v2/vip-goods/list";
    /**
     * 获取VIP商品详情
     */
    public static final String VIP_GOODS_DETAIL = "v2/vip-goods";
    /**
     * 获取收货地址
     */
    public static final String VIP_GOODS_ADDRESS = "v2/vip-order/get-addr";

    /**
     * 保存/修改收货地址
     */
    public static final String VIP_GOODS_ADDRESS_CHANGE = "v2/vip-order/put-addr";

    /**
     * VIP商品列表
     */
    public static final String VIP_GOODS_LIST = "v2/vip-order/list";

    /**
     * 下VIP订单
     */
    public static final String VIP_GOODS_BUY = "v2/vip-order/buy";

    /**
     * VIP任务
     */
    public static final String VIP_TASKS = "v2/user-task/list";

    /**
     * 乐桃收益榜
     */
    public static final String XLT_INCOME = "v2/xlt-income-rank/index";

    /**
     * 成员贡献榜——总预估佣金
     */
    public static final String TEAM_INCOME_TOTAL = "v2/rank/commission";
    /**
     * 成员贡献榜——本月预估佣金
     */
    public static final String TEAM_INCOME_MONTH = "v2/rank/month";
    /**
     * 成员贡献榜——7日拉新
     */
    public static final String TEAM_INCOME_WEEK = "v2/rank/week-invite";

    /**
     * 搜索商品-剪切板跳转过来的 非普通搜索接口
     */
    public static final String SEARCH_CLIPBOARD = "v2/goods/search-clipboard";
    /**
     * 获取商品可用来源
     */
    public static final String ITEM_SOURCE_LIST = "v3/item-source/list";
    /**
     * 会员权益
     */
    public static final String VIP_RIGHTS = "v2/user/rights";

    /**
     * 友盟点击上报
     */
    public static final String UMENG_CLICK_REPORT = "v2/umeng/report";

    /**
     * 友盟数据上报
     */
    public static final String UMENG_DATA_REPORT = "v2/umeng/add";

    /**
     * 自有消息推送列表
     */
    public static final String UMENG_ACTION_LIST = "v2/umeng/list";

    /**
     * 关闭某个消息推送
     */
    public static final String UMENG_ACTION_BAN = "v2/umeng/ban";

    /**
     * 获取邀请码信息
     */
    public static final String GET_INVITE_CODE = "v2/user/get-invite-code";

    /**
     * 获取推荐人邀请码
     */
    public static final String GET_RECOMMEND_CODE = "/v3/recommend-superior/index";

    /**
     * 设置用户邀请码
     */
    public static final String SET_INVITE_CODE = "v2/user/set-invite-code";

    /**
     * 检测邀请码是否被使用
     */
    public static final String CHECK_INVITE_CODE = "v2/user/check-invite-code";

    /**
     * 获取推荐未使用的邀请码
     */
    public static final String GET_RAND_CODE = "v2/user/get-rand-code";

    /**
     * 新手任务汇报
     */
    public static final String NEW_TASK = "v2/task/task/new-task";
    /**
     * 日常任务
     */
    public static final String DAY_TASK = "v2/task/everyday/everyday-task";

    /**
     * 根据任务编码获取星币和当前用户状态
     */
    public static final String TASK_X_NUMBER = "v2/task/task/x-number";

    /**
     * 活动详情
     */
    public static final String ACT_DETAIL = "v2/activity-pages/details";

    /**
     * 上传日志
     */
    public static final String LOG_UPLOAD = "v2/feedback/upfile";

    /**
     * 获取问题反馈详情
     */
    public static final String GET_FEEDBACK_DETAIL = "v2/feedback/detail";

    /**
     * 获取问题反馈列表
     */
    public static final String GET_FEEDBACK_LIST = "v2/feedback/list";

    /**
     * 提交问题反馈列表
     */
    public static final String POST_SEND_FEEDBACK = "v2/feedback/send";

    /**
     * 上传问题反馈相关文件
     */
    public static final String POST_UPFILE_FEEDBACK = "v2/feedback/upfile";

    /**
     * 获取我推荐的商品列表
     */
    public static final String GET_MY_GOOD_RECM_LIST = "v2/community-goods-recm/m-list";

    /**
     * 删除我的推荐
     */
    public static final String DEL_GOOD_RECM = "v2/community-goods-recm/del";

    /**
     * 获取我推荐的奖励信息
     */
    public static final String GET_MY_GOOD_RECM_INFO = "v2/community-goods-recm/info";

    /**
     * 提交商品推荐
     */
    public static final String POST_SHARE_GOOD_RECM = "v2/community-goods-recm/share";

    /**
     * 上传好物推荐图片
     */
    public static final String UPFILE_COMMUNITY_GOODS_RECM = "v2/community-goods-recm/upfile";

    /**
     * 星乐桃在线客服
     */
    public static final String GET_CONFIG_KEFU = "/config/kefu";

    /**
     * 检查提现金额是否需要签署协议
     */
    public static final String GET_CHECK_CONTRACT = "/v2/account/pig-contract-check";

    /**
     * 获取签合同的url
     */
    public static final String GET_REQUEST_CONTRACT = "/v2/account/request-contract";

    /**
     * 获取提醒
     */
    public static final String GET_ACCOUNT_TIPS = "/v2/account/tips";

    /**
     * 获取用户水印信息
     */
    public static final String GET_USER_WATERMARK = "/v2/xlt-user-watermark/index";

    /**
     * 保存用户水印信息
     */
    public static final String SAVE_USER_WATERMARK = "/v2/xlt-user-watermark/save";

    /**
     * 我的推荐-奖励记录
     */
    public static final String COMMUNITY_REWARD_LIST = "v2/community-goods-recm/reward-list";

    /**
     * 获取安全域名列表
     */
    public static final String SAFE_DOMAIN = "config/safe-domain";

    /**
     * 注销详情
     */
    public static final String ACCOUNT_LOGOUT_DETAILS = "/v2/xlt-account-logout/logout-details";

    /**
     * 注销账户
     */
    public static final String ACCOUNT_LOGOUT = "/v2/xlt-account-logout/logout";

    /**
     * 发送验证码
     */
    public static final String LOGOUT_SENDCODE = "/v2/xlt-account-logout/send-code";

    /**
     * 撤销账号注销申请
     */
    public static final String LOGOUT_REVOCATION = "/v2/xlt-account-logout/revocation-logout";

    //-----------------------------------------------自供
    /**
     * 自供banner
     */
    public static final String MALL_BANNER = "selfsupport/home/banner-list";
    /**
     * banner pv上报
     */
    public static final String MALL_BANNER_REPORT = "selfsupport/home/pv-banner";
    /**
     * 分类列表
     */
    public static final String MALL_CATEGORY = "selfsupport/home/category-list";
    /**
     * 推荐
     */
    public static final String MALL_RECOMMEND = "selfsupport/home/recommend";

    /**
     * 商品详情
     */
    public static final String MALL_GOOD_DETAIL = "selfsupport/home/goods-details";
    /**
     * 根据商品分类获取
     */
    public static final String MALL_CATEGORY_REFER = "selfsupport/home/category-refer";
    /**
     * 关键词搜索
     */
    public static final String MALL_SEARCH_WORDS = "selfsupport/home/keyword";

    //###########地址模块
    /**
     * 添加地址
     */
    public static final String ADDRESS_ADD = "selfsupport/user-address/add";
    /**
     * 修改地址
     */
    public static final String ADDRESS_UPDATE = "selfsupport/user-address/edit";
    /**
     * 删除地址
     */
    public static final String ADDRESS_DEL = "selfsupport/user-address/del";
    /**
     * 地址列表
     */
    public static final String ADDRESS_LIST = "selfsupport/user-address/list";
    /**
     * 查询地址
     */
    public static final String ADDRESS_QUERY = "selfsupport/user-address/query";

    //###########自营-订单
    /**
     * 去支付
     */
    public static final String MALL_ORDER_GOPAY = "selfsupport/shop-order/go-pay";
    /**
     * 取消订单
     */
    public static final String MALL_ORDER_CANCEL = "selfsupport/shop-order/off-order";
    /**
     * 快递信息查询
     */
    public static final String MALL_ORDER_EXPRESS = "selfsupport/shop-order/express";
    /**
     * 立即购买
     */
    public static final String MALL_ORDER_BUYNOW = "selfsupport/shop-order/buy-now";
    /**
     * 订单列表
     */
    public static final String MALL_ORDER_LIST = "selfsupport/shop-order/order-list";
    /**
     * 订单唤起支付
     */
    public static final String MALL_ORDER_RE_PAY = "selfsupport/shop-order/order-go-pay";

    /**
     * 分享
     */
    public static final String MALL_HOME_SHARE = "selfsupport/home/share";

    /**
     * 订单详情
     */
    public static final String MALL_ORDER_DETAIL = "selfsupport/shop-order/order-details";

    /**
     * 订单评论
     */
    public static final String MALL_ORDER_COMMENT = "selfsupport/shop-order/comment";
    /**
     * 确认收货
     */
    public static final String MALL_ORDER_AFFIRM = "selfsupport/shop-order/affirm-order";

    /**
     * 导师分享-上传文件
     */
    public static final String TUTOR_SHARE_UPLOAD = "v2/tutor-share/upload";

    /**
     * 创建导师分享文章
     */
    public static final String TUTOR_SHARE_CREATE = "v2/tutor-share/create";

    /**
     * 复制导师分享文章
     */
    public static final String TUTOR_SHARE_COPY = "v2/tutor-share/copy";

    /**
     * 移动导师分享文章排序
     */
    public static final String TUTOR_SHARE_MOVE = "v2/tutor-share/move";

    /**
     * 获取我的导师分享文章
     */
    public static final String TUTOR_SHARE_LIST = "v2/tutor-share/list";

    /**
     * 获取我自己发布的导师分享文章
     */
    public static final String TUTOR_SHARE_ME_LIST = "v2/tutor-share/m-list";

    /**
     * 获取文章详情
     */
    public static final String TUTOR_SHARE_DETAIL = "v2/tutor-share/detail";

    /**
     * 设置导师分享文章是否展示
     */
    public static final String TUTOR_SHARE_SET_STATUS = "v2/tutor-share/set-status";

    /**
     * 设置导师分享文章是否置顶
     */
    public static final String TUTOR_SHARE_TOP = "v2/tutor-share/top";

}
