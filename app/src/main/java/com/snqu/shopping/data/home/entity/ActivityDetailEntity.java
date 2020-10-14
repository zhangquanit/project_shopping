package com.snqu.shopping.data.home.entity;

import java.util.List;

/**
 * 活动详情
 *
 * @author 张全
 */
public class ActivityDetailEntity {


    /**
     * _id : 5ea179e30a6c8e06c800555d
     * name : 帅哥哥
     * code : ac2020204c0x
     * platform : D
     * link : mmmm
     * content : 13131
     * style : {"bgcolor":"red 背景颜色","bgImage":"..../xx.png 活动图片","image_url":"http://resource-t.xin1.cn/..../xx.png 活动图片"}
     * button : [{"type":1,"name":"复制活动文案","color":"#000"},{"type":2,"name":"前往活动","color":"#000"}]
     * status : 1
     * itime : 1587640803
     * utime : 1587640803
     */

    public String _id;
    public String name;
    public String code;
    public String platform;
    public String link;
    public String content;
    public ActivityStyleBean style;
    public int status;
    public int itime;
    public int utime;
    public List<ActivityButtonBean> button;
    public String[] link_type;
    public String link_url;
    public String tid;
    public int open_third_app;
    public String direct;
    public String direct_protocal;

    public static class ActivityStyleBean {
        /**
         * bgcolor : red 背景颜色
         * bgImage : ..../xx.png 活动图片
         * image_url : http://resource-t.xin1.cn/..../xx.png 活动图片
         */

        public String bgcolor;
        public String bgImage;
        public String bgImage_url;
    }

    public static class ActivityButtonBean {
        /**
         * type : 1
         * name : 复制活动文案
         * color : #000
         */

        public int type;
        public String name;
        public String color;
        public String bgColor;
    }
}
