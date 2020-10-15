package com.snqu.shopping.data.goods.bean;

import com.snqu.shopping.data.goods.entity.CollectionGoodsEntity;
import com.snqu.shopping.util.NumberUtil;

import java.util.List;

public class CollectionEntity {

    public Expired expired; //已失效商品列表
    public ThreeMonthsAgo threeMonthsAgo;//三个月前
    public NoCoupon noCoupon;//无优惠券
    public List<CollectionGoodsEntity> list;//正常的收藏商品列表
    public long frugal; //  省了多少钱

    public class NoCoupon {
        public List<CollectionGoodsEntity> list;
        public int count;
    }

    public class Expired {
        public List<CollectionGoodsEntity> list;
        public int count;
    }

    public class ThreeMonthsAgo {
        public List<CollectionGoodsEntity> list;
        public int count;
    }

    /**
     * 省了多少钱
     * @return
     */
    public String getFrugal() {
        return NumberUtil.saveTwoPoint(frugal);
    }



    @Override
    public String toString() {
        return "CollectionEntity{" +
                "expired=" + expired +
                ", threeMonthsAgo=" + threeMonthsAgo +
                ", noCoupon=" + noCoupon +
                ", list=" + list +
                ", frugal=" + frugal +
                '}';
    }
}
