package com.snqu.shopping.data.goods.entity;

import android.text.TextUtils;

import com.snqu.shopping.util.NumberUtil;

/**
 * 商品url 解析
 */
public class GoodsDecodeEntity {


    /**
     * coupon : {"amount":0}
     * item_min_price : 1190
     * item_price : 1190
     * rebate : {"xkd_amount":18}
     * title : 【良品铺子-山药薄片脆片70gx2袋】脆薯片好吃的膨化休闲零食小吃
     * pict_url : https://img.alicdn.com/bao/uploaded/i1/619123122/O1CN019hWnYd1Yvv5FmGvqq_!!0-item_pic.jpg
     * num_iid : 553465619093
     * user_type : 1
     * item_url : https://detail.tmall.com/item.htm?id=553465619093
     * goods_id : 5dbfc5274ff7384a86061b35f3486779
     * item_source : B
     * item_id : 553465619093
     */

    public CouponBean coupon;
    public long item_min_price;
    public long item_price;
    public RebateBean rebate;
    public String title;
    public String item_title;
    public String item_image;
    public String pict_url;
    public String num_iid;
    public String user_type;
    public String item_url;
    public String goods_id;
    public String item_source;
    public String item_id;
    public String real_url;

    public static class CouponBean {
        /**
         * amount : 0
         */

        public int amount;

        public String getPrice() {
            if (amount == 0) {
                return "";
            }
            return NumberUtil.INSTANCE.couponPrice(amount);//优惠券金额
        }
    }

    public static class RebateBean {
        /**
         * xkd_amount : 18
         */

        public long xkd_amount;

        public String getPrice() {
            if (xkd_amount == 0L) {
                return "";
            }
            return NumberUtil.INSTANCE.saveTwoPoint(xkd_amount);//返利
        }
    }

    public String getOld_price() {
        return NumberUtil.INSTANCE.saveTwoPoint(item_min_price);//原价
    }

    public String getNow_price() {
        return NumberUtil.INSTANCE.saveTwoPoint(item_price);//券后价
    }


    public String getCouponPrice() {
        if (coupon == null) {
            return "";
        }
        return coupon.getPrice();
    }

    public String getRebatePrice() {
        if (rebate == null) {
            return "";
        }
        return rebate.getPrice();
    }

    public String getTitleStr() {
        if (!TextUtils.isEmpty(title)) {
            return title;
        }
        return item_title;
    }

    public String getImageUrl() {
        if (!TextUtils.isEmpty(pict_url)) {
            return pict_url;
        }
        return item_image;
    }
}
