package com.snqu.shopping.data.home.entity;

import com.android.util.db.Key;
import com.snqu.shopping.data.goods.entity.GoodsEntity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author 张全
 */
public class AdvertistEntity implements Serializable {

    @Key
    public String _id;
    public String bgImage; //背景图片
    public String bgColor; //背景颜色
    public String bgImageUrl;
    public String show_check_enable;
    public String show_end_time;
    public String show_start_time;
    public String type; //类型
    public String name;
    public String image;
    public Attribute attribute; //标题，副标题颜色
    public String[] authPlatform;
    public String[] link_type;
    public int needLogin;//需要登录
    public int needAuth;// 需要授权
    public int open_third_app = -1; //是否允许唤起app，0否，1是
    public String platform; // D:京东，C：淘宝，B：天猫，G:商品id，U：内部url，O:外部url
    public String direct;
    public String direct_protocal;
    public String item_source;
    public String link_url;
    public String tid;
    public List<GoodsEntity> goodsList;

    public String url;
    public int show_position;
    public int status;
    public int isAuth = -1;//
    public int per;//间隔时间 秒
    public int height;
    public int width;
    public String show_text;

    public class Attribute {

        /**
         * title : 我是标题
         * title_font_color : red
         * sub_title : 我是副标题
         * sub_title_font_color : red
         * label : 我是标签
         * label_font_color : #fff
         */

        public String title;
        public String title_font_color;
        public String sub_title;
        public String sub_title_font_color;
        public String label;
        public String label_font_color;

        @Override
        public String toString() {
            return "Attribute{" +
                    "title='" + title + '\'' +
                    ", title_font_color='" + title_font_color + '\'' +
                    ", sub_title='" + sub_title + '\'' +
                    ", sub_title_font_color='" + sub_title_font_color + '\'' +
                    ", label='" + label + '\'' +
                    ", label_font_color='" + label_font_color + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AdvertistEntity{" +
                "_id='" + _id + '\'' +
                ", bgImage='" + bgImage + '\'' +
                ", bgColor='" + bgColor + '\'' +
                ", bgImageUrl='" + bgImageUrl + '\'' +
                ", show_check_enable='" + show_check_enable + '\'' +
                ", show_end_time='" + show_end_time + '\'' +
                ", show_start_time='" + show_start_time + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", attribute=" + attribute +
                ", authPlatform=" + Arrays.toString(authPlatform) +
                ", link_type=" + Arrays.toString(link_type) +
                ", needLogin=" + needLogin +
                ", needAuth=" + needAuth +
                ", open_third_app=" + open_third_app +
                ", platform='" + platform + '\'' +
                ", direct='" + direct + '\'' +
                ", direct_protocal='" + direct_protocal + '\'' +
                ", item_source='" + item_source + '\'' +
                ", link_url='" + link_url + '\'' +
                ", tid='" + tid + '\'' +
                ", goodsList=" + goodsList +
                '}';
    }

    public boolean isAd() {
        if (!type.equals("good")) {
            return true;
        }
        return false;
    }
//
//    public PlateCategoryEntity toChannelEntity() {
//        PlateCategoryEntity plateCategoryEntity = new PlateCategoryEntity();
//        plateCategoryEntity.id = _id;
//        plateCategoryEntity.name = name;
//        plateCategoryEntity.icon = icon;
//        plateCategoryEntity.dev_code = dev_code;
//        return plateCategoryEntity;
//    }

}

