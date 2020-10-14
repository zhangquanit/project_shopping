package com.snqu.shopping.data.goods.entity;

public class GoodRecmEntity {
    public boolean can_show; // //类型：Boolean  必有字段  备注：是否可以显示
    public String share_type;//1 可推荐2今日推荐上限 3已分享此商品 4没有权限 5没有找到商品 6 商品价格过低
    public String share_advance; //判断是否有特权 0 无 1 有
}
