package com.snqu.shopping.util.statistics;

import android.os.Build;
import android.text.TextUtils;

import com.android.util.LContext;
import com.android.util.log.LogUtil;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;

/**
 * @author 张全
 */
public class StatisticInfo {

    /**
     * udid : 设备唯一id
     * client_type : 1
     * client_version : 1.0
     * device : 小米3
     * os_version : android9.0
     * net_type : 1
     * uid : 用户id
     * action : 1
     * time : 11111111111
     * page : 访问页面
     * title : 页面名称
     * referer :
     * item_id : 商品id
     * model1 : 一级板块id
     * model2 : 二级板块id
     * platform : 平台 1 淘宝 2 天猫 3 京东
     * jump_link : 跳转链接
     * activity : 活动id
     * order_id : 订单id
     * keyword : 搜索关键词
     */

    public String udid;
    public final int client_type = 2;
    public String client_version; //客户端版本号
    public String device;
    public String os_version;
    public int net_type;
    public String uid;
    public String action;
    public long time; //秒
    //    public String page; //访问页面
//    public String title; //页面标题
//    public String referer;
    public String item_id; //商品id
    public String model1; //一级板块id
    public String model2; //二级板块id
    public int platform; //1淘宝，2天猫，3京东,4京东 5唯品会
    public String jump_link; //跳转地址
    public String activity; //活动id 订单分享赚佣金哪里的
    public String order_id; //订单id
    public String keyword; //搜索关键词

    public String jd_position_id; //京东推广位id
    public String tb_relation_id; //淘宝渠道id

    public int model_type; //0(或不传)：通用板块，model传板块id     1：人工编辑板块，model传板块code

    public int source = 2;
    public int user_level;

    public StatisticInfo() {
        udid = AnalysisUtil.getUniqueId();
        device = Build.BRAND + " " + Build.MODEL; //BRAND手机厂商   MODEL手机型号
        client_version = LContext.versionName;
        os_version = Build.VERSION.RELEASE;//系统版本
        String networkType = AnalysisUtil.networkType(LContext.getContext());
        if (TextUtils.equals(networkType, "NULL")) {
            net_type = 0;
        } else if (TextUtils.equals(networkType, "WIFI")) {
            net_type = 2;
        } else {  //移动网络
            net_type = 1;
        }

        UserEntity user = UserClient.getUser();
        if (null != user) {
            uid = user._id;
            user_level = user.level;
        }

        time = System.currentTimeMillis() / 1000;
    }

    /**
     * action:
     * 1访问页面
     * 一级板块(1-1,1-2)、二级板块
     * 2跳转商品
     * 在商品详情也点击下单，汇报item_id,platform,jump_link，一级板块和二级板块id
     * 6分享
     * 在下单列表点击分享，汇报活动id，order_id，item_id,platform
     * 7收藏
     * 商品相关信息：item_id、platform、一级板块和二级板块id
     * 8搜索
     * 汇报keyword
     */

    /**
     * 浏览页面
     *
     * @param plate
     * @param childPlate
     */
    public void viewPage(String plate, String childPlate) {
        this.action = "1";
        model1 = plate;
        model2 = childPlate;
        upload();
    }

    public void viewGoodDetailPage(String plate, String childPlate, String goodId, String item_source, int model_type) {
        this.action = "1";
        this.model1 = plate;
        this.model2 = childPlate;
        this.item_id = goodId;
        this.model_type = model_type;
        pareseItemSource(item_source);
        upload();
    }

    /**
     * 红人街
     *
     * @param parentCode
     * @param childCode
     */
    public void viewRedModelPage(String parentCode, String childCode) {
        this.action = "1";
        model_type = 1;
        model1 = parentCode;
        model2 = childCode;
        upload();
    }

    /**
     * 下单跳转
     *
     * @param plate
     * @param childPlate
     * @param item_id
     * @param item_source
     * @param jump_link
     */
    public void orderTB(String plate, String childPlate, String item_id, String item_source, String jump_link, String position_id, int model_type) {
        this.action = "2";
        this.model1 = plate;
        this.model2 = childPlate;
        this.item_id = item_id;
        this.jump_link = jump_link;
        this.tb_relation_id = position_id;
        this.model_type = model_type;
        pareseItemSource(item_source);
        upload();
    }

    public void orderJD(String plate, String childPlate, String item_id, String item_source, String jump_link, String position_id, int model_type) {
        this.action = "2";
        this.model1 = plate;
        this.model2 = childPlate;
        this.item_id = item_id;
        this.jump_link = jump_link;
        this.jd_position_id = position_id;
        this.model_type = model_type;
        pareseItemSource(item_source);
        upload();
    }

    public void orderPDD(String plate, String childPlate, String item_id, String item_source, String jump_link, String position_id, int model_type) {
        this.action = "2";
        this.model1 = plate;
        this.model2 = childPlate;
        this.item_id = item_id;
        this.jump_link = jump_link;
        this.jd_position_id = position_id;
        this.model_type = model_type;
        pareseItemSource(item_source);
        upload();
    }

    /**
     * 复制淘口令
     *
     * @param plate
     * @param childPlate
     * @param item_id
     * @param item_source
     */
    public void copyTKL(String plate, String childPlate, String item_id, String item_source, int model_type) {
        this.action = "5";
        this.model1 = plate;
        this.model2 = childPlate;
        this.item_id = item_id;
        this.model_type = model_type;
        pareseItemSource(item_source);
        upload();
    }

    /**
     * 分享
     *
     * @param item_id
     * @param order_id
     * @param activity
     * @param item_source
     */
    public void share(String item_id, String order_id, int activity, String item_source) {
        this.action = "6";
        this.item_id = item_id;
        this.order_id = order_id;
        this.activity = activity + "";
        pareseItemSource(item_source);
        upload();
    }

    /**
     * 收藏
     *
     * @param plate
     * @param childPlate
     * @param item_id
     * @param item_source
     */
    public void collect(String plate, String childPlate, String item_id, String item_source) {
        this.action = "7";
        this.model1 = plate;
        this.model2 = childPlate;
        this.item_id = item_id;
        pareseItemSource(item_source);
        upload();
    }

    /**
     * 取消 收藏
     *
     * @param plate
     * @param childPlate
     * @param item_id
     * @param item_source
     */
    public void cancelCollect(String plate, String childPlate, String item_id, String item_source) {
        this.action = "9";
        this.model1 = plate;
        this.model2 = childPlate;
        this.item_id = item_id;
        pareseItemSource(item_source);
        upload();
    }

    /**
     * 搜索
     *
     * @param keyword
     */
    public void search(String keyword) {
        this.action = "8";
        this.keyword = keyword;
        upload();
    }

    /**
     * 商品分享
     *
     * @param plate
     * @param childPlate
     * @param item_id
     * @param item_source
     */
    public void shareGoods(String plate, String childPlate, String item_id, String item_source) {
        this.action = "10";
        this.model1 = plate;
        this.model2 = childPlate;
        this.item_id = item_id;
        pareseItemSource(item_source);
        upload();
    }

    public void login(String uid, String is_new) {
        this.uid = uid;
        if (TextUtils.equals(is_new, "1")) { //用户注册
            this.action = "21";
        } else { //用户登录
            this.action = "22";
        }
        upload();
        SndoData.login();
    }


    private void pareseItemSource(String item_source) {
        if (TextUtils.equals(item_source, "C")) {
            platform = 1;
        } else if (TextUtils.equals(item_source, "B")) {
            platform = 2;
        } else if (TextUtils.equals(item_source, "D")) {
            platform = 3;
        } else if (TextUtils.equals(item_source, "P")) {
            platform = 4;
        } else if (TextUtils.equals(item_source, "V")) {
            platform = 5;
        }else if (TextUtils.equals(item_source, Constant.BusinessType.S)){
            platform = 6;
        }
    }

    private void upload() {
        LogUtil.d(getClass().getSimpleName(), toString());
        UploadUtil.log(this);
    }

    @Override
    public String toString() {

        return "StatisticInfo{" +
                "udid='" + udid + '\'' +
                ", client_type=" + client_type +
                ", client_version='" + client_version + '\'' +
                ", device='" + device + '\'' +
                ", os_version='" + os_version + '\'' +
                ", net_type=" + net_type +
                ", uid='" + uid + '\'' +
                ", action='" + action + '\'' +
                ", time=" + time +
                ", item_id='" + item_id + '\'' +
                ", model1='" + model1 + '\'' +
                ", model2='" + model2 + '\'' +
                ", platform=" + platform +
                ", jump_link='" + jump_link + '\'' +
                ", activity='" + activity + '\'' +
                ", order_id='" + order_id + '\'' +
                ", keyword='" + keyword + '\'' +
                ", jd_position_id='" + jd_position_id + '\'' +
                ", tb_relation_id='" + tb_relation_id + '\'' +
                ", model_type=" + model_type +
                '}';
    }
}
