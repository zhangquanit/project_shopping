package com.snqu.shopping.data.goods.entity;

import java.net.URLEncoder;

/**
 * @author 张全
 */
public class GoodsQueryParam {
    public String plate; //板块id 如果为0 则 直接获取最新的商品
    public String category; //分类信息
    public boolean isShop = false;//是否是店铺
    public Sort sort = Sort.NONE;// 排序 item_sell_count-销量 rebate.amount-返利金 item_price-卷后价 +从小到大 -从大到小
    public String item_source;//商品来源 C:淘宝 B:天猫 D:京东    多个平台以逗号分隔
    public int postage; //是否包邮
    public int has_coupon;//是否有优惠劵
    public String seller_shop_id;//店铺id
    public String search;//关键词
    public String goods_id;//按商品id去搜索

    public String start_price, end_price; //价格区间

    public int row = 10; //每页请求条数
    public int page = 1; //第几页

    public String id; //按id去请求
    public String goodsRecmSort;
    public String goodsStatus; //筛选类型 1 成功 2 审核失败
    public String goodsDate;

    public void reset() {
        sort = Sort.NONE;
        item_source = null;
        postage = 0;
        has_coupon = 0;
        seller_shop_id = null;
        search = null;
    }

    public enum Sort {
        NONE(""), //综合
        SELL_COUNT_UP("+item_sell_count"),
        SELL_COUNT_DOWN("-item_sell_count"),
        AMOUNT_UP("+rebate.xkd_amount"),
        AMOUNT_DOWN("-rebate.xkd_amount"),
        PRICE_UP("+item_price"),
        PRICE_DOWN("-item_price"),
        CUSTOM_SORT("-coupon.amount"),
        ORDER_UP("order_count"),
        ORDER_DOWN("-order_count"),
        REWARD_UP("reward_amount"),
        REWARD_DOWN("-reward_amount"),
        TIME_DOWN("-time");

        public String value;

        private Sort(String value) {
            try {
                this.value = URLEncoder.encode(value, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
                this.value = value;
            }
        }

    }

    @Override
    public String toString() {
        return "GoodsQueryParam{" +
                "plate='" + plate + '\'' +
                ", category='" + category + '\'' +
                ", sort=" + sort +
                ", item_source='" + item_source + '\'' +
                ", postage=" + postage +
                ", has_coupon=" + has_coupon +
                ", seller_shop_id='" + seller_shop_id + '\'' +
                ", search='" + search + '\'' +
                ", goods_id='" + goods_id + '\'' +
                ", start_price='" + start_price + '\'' +
                ", end_price='" + end_price + '\'' +
                ", row=" + row +
                ", page=" + page +
                ", id='" + id + '\'' +
                '}';
    }
}
