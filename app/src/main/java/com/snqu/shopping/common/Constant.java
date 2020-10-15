package com.snqu.shopping.common;


import com.snqu.shopping.data.DataConfig;
import com.snqu.shopping.data.home.entity.AdvertistEntity;

import java.util.List;


/**
 * desc:
 * time: 2019/1/7
 *
 * @author yinYin
 */
public class Constant {
    /**
     * SD卡 共享根目录
     */
    public static final String SD_DIR = "xlt";
    //保存的子文件夹(不能删除)
    public static final String SAVE_FILE = "save_image";
    //零时存储用
    public static final String[] CAN_DELETE_FILE = {"share_detail", "images_circle"};
    //测试渠道
    public static final String PROD_TEST = "prod_test";
    //发圈-商学院code
    public static final String CODE_SXY = "202002sxy";
    //发圈-推荐榜
    public static final String CODE_TJB = "2020recommed";
    //水印名称
    public static String water_name = "";

    public static List<AdvertistEntity> searchAdEntity; //搜索页引导广告

    public interface Bundle {
        String USER_CANCEL_CHECK_LIST = "USER_CANCEL_CHECK_LIST"; //注销原因
        String USER_CANCEL_OTHER = "OTHER"; //注销-其他原因
    }

    public interface PREF {
        String IS_NEW = "IS_NEW";   //控制是否弹出新用户导师对话框
        String IS_NEW_DATA = "is_new_count";
        String IS_FREE = "IS_FREE"; //控制是否弹出免单
        String LIKE_MODE = "LIKE_MODE"; //猜你喜欢，1大数据模式，2淘宝模式
    }

    public interface Event {
        //刷新导师分享
        String TUTOR_SHARE_REFRESH = "TUTOR_SHARE_REFRESH";
        //导师分享显示下方添加文档
        String TUTOR_SHARE_SHOW = "TUTOR_SHARE_SHOW";
        //导师分享没有数据
        String TUTOR_SHARE_NO_DATA = "TUTOR_SHARE_NO_DATA";
        //用户注销原因选择
        String USER_CANCEL_CHECK = "USER_CANCEL_CHECK";
        //暂停banner滑动
        String PAUSE_RECOMMEND = "PAUSE_RECOMMEND";
        //定位
        String LOCATION = "ACTION_LOCATION";
        //首页布局
        String HOME_LAYOUT_INDEX = "home_layout_index";
        //退出登录
        String LOGIN_OUT = "LOGIN_OUT";
        //登录成功
        String LOGIN_SUCCESS = "LOGIN_SUCCESS";
        //收藏
        String COLLECTION_CHANGE = "COLLECTION_CHANGE";
        //修改手机号
        String CHANGE_PHONE_SUCCESS = "CHANGE_PHONE_SUCCESS";
        //wx授权Code
        String WX_CODE = "WX_CODE";
//        String WX_CODE_PAGE_INVITE = "WX_"

        //支付
        String ORDER_BUY_SUCCESS = "ORDER_BUY_SUCCESS";
        String ORDER_BUY_FAIL = "ORDER_BUY_FAIL";
        String ORDER_BUY_CANCEL = "ORDER_BUY_CANCEL";

        //绑定支付宝成功
        String BIND_ALIPAY_SUCCESS = "BIND_ALIPAY_SUCCESS";
        //授权成功
        String AUTH_SUCCESS = "AUTH_SUCCESS";
        //體現成功
        String WITHDRAWAL_SUCCESS = "WITHDRAWAL_SUCCESS";
        //找回订单成功
        String FIND_ORDER_SUCCESS = "FIND_ORDER_SUCCESS";

        //绑定联系人
        String BIND_INVITE_SUCCESS = "BIND_INVITE_SUCCESS";
        //绑定微信成功
        String BIND_WX_SUCCESS = "BIND_WX_SUCCESS";
        //改变地址成功
        String CHANGE_ADDRESS_SUCCESS = "CHANGE_ADDRESS_SUCCESS";
        //查看更多分类
        String CLASSFICATION_ITEM = "CLASSFICATION_ITEM";
        //订单搜索
        String ORDER_SEARCH = "order_search";
        //任务汇报
        String TASK_REPORT = "TASK_REPORT";
        //问题反馈提交成功
        String FEED_SUCCESS = "FEED_SUCCESS";
        // 点击查看
        String MY_TEAM_CLICK = "my_team_click";
        // item点击查看
        String MY_TEAM_ITEM_CLICK = "my_team_item_click";
        // 点击首页回到顶部
        String HOME_TAP_TOP = "HOME_TAP_TOP";
        // 点击我的回到顶部
        String PERSON_TAP_TOP = "PERSON_TAP_TOP";
        // 刷新推荐接口
        String REFRESH_RECM = "REFRESH_RECM";

        //直供首页刷新
        String MALL_RECOM_REFRESH = "MALL_RECOM_REFRESH";

        //更新地址
        String ADDRESS_UPDATE = "ADDRESS_UPDATE";
        //地址管理页面 选择地址
        String ADDRESS_MANAGER_ITEM = "ADDRESS_MANAGER_ITEM";
        //选择POI地址
        String ADDRESS_CHOOSE_POI = "ADDRESS_CHOOSE_POI";

        //地址保存成功
        String SAVE_ADDRESS = "SAVE_ADDRESS";
    }

    public interface Match {
        String PHONE_MATCH = "1[3456789]\\d{9}";
    }

    public interface WebPage {

        //----------------H5_HOST
        String NORMAL_QUESTION = DataConfig.H5_HOST + "help.html"; //个人中心--常见问题
        String CUSTOMER_SERVICE = DataConfig.H5_HOST + "service.html"; //个人中心-在线客服
        String SHARE_INVITE_URL = DataConfig.H5_HOST + "activity/ushare.html?code=";
        String SHARE_GOODS_URL = DataConfig.H5_HOST + "item/";
        String USER_AGREEMENT = DataConfig.H5_HOST + "article-service.html"; //用户协议
        String PRIVACY_PROTOCAL = DataConfig.H5_HOST + "article-privacy.html"; //隐私政策
        String PROMOTE = DataConfig.H5_HOST + "popula-rule.html"; //推广规则

        //----------------H5_ACT_HOST
        String EARNING_PAGE = DataConfig.H5_ACT_HOST + "h5s/ac202010earnings?hideTitlebar=1";//我的收益
        String GROUND_PUSH = DataConfig.H5_ACT_HOST + "activity/ac202004dtwl/index.html"; //个人中心-地推
        String BUSINESS_COOPERATION = DataConfig.H5_ACT_HOST + "h5s/ac202004business/index.html";//个人中心商家合作
        String CHUDAN = DataConfig.H5_ACT_HOST + "h5s/ac202005orderrank/index.html?hideTitlebar=1&lightModel=1"; //出单榜
        String TASK_PAGE = DataConfig.H5_ACT_HOST + "h5s/ac202002missionactivity/index.html?hideTitlebar=1&lightModel=0&needLogin=1";//任务中心
        String TRANSFORM_URL = DataConfig.H5_ACT_HOST + "h5s/ac202004turnlink/index.html?parseClipboard=0";//批量转链
        String ORDER_ASSISTANT = DataConfig.H5_ACT_HOST + "h5s/ac202005yfd/index.html?hideTitlebar=1&lightModel=1"; //发单助手
        String REWARD_RULE = DataConfig.H5_ACT_HOST + "h5s/feed-recommend";//奖励规则
        String PROMOTION_STANDARD = DataConfig.H5_ACT_HOST + "h5s/ac202007promotionnorm/index.html"; //推广规范
        String CONTACT_US = DataConfig.H5_ACT_HOST + "h5s/ac202007contactus/index.html";//联系我们
        String GUIDE = DataConfig.H5_ACT_HOST + "h5s/ac202007guide/index.html";//新手教程

        String PRIVILEGE = DataConfig.H5_ACT_HOST + "h5s/ac202008privilege/index.html";//特权页面
        String TUTORSHARE = DataConfig.H5_ACT_HOST + "h5s/ac202009tutor/index.html?hideTitlebar=1&lightModel=1";//新建文档页
        String TUTOR_SHARE_EDIT = DataConfig.H5_ACT_HOST + "h5s/ac202009tutor/index.html?hideTitlebar=1&lightModel=1&share_id="; //编辑文档
        String ORDER_TIP = DataConfig.H5_ACT_HOST + "h5s/ac202010ordertip"; //我的订单提示
    }

    public interface BusinessType {
        public static final String TB = "C"; //淘宝
        public static final String TM = "B"; //天猫
        public static final String PDD = "P"; // 拼多多
        public static final String JD = "D"; //京东
        public static final String V = "V"; //唯品会
        public static final String CZB = "CZB"; //加油
        public static final String MT = "MT"; //美团
        public static final String S = "S"; //苏宁
    }

}
