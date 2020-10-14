package com.snqu.shopping.data.mall.entity;

import java.util.List;

public class MallBannerEntity {
    public List<Banner> banner_list;

    public static class Banner {
        public String _id;
        public String images;
        public String place; //1-活动 2-商品详情页
        public String activity_id; //活动
        public String goods_details;
        public String images_url;
        public String url;//活动地址
    }
}
