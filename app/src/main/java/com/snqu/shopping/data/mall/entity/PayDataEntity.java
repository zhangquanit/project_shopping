package com.snqu.shopping.data.mall.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户购买信息
 */
public class PayDataEntity {
    public String address_id; //类型：String  必有字段  备注：收货地址
    public List<UserType> user_type_in = new ArrayList<>();//用户录入信息
    public String pay_type = ""; //支付方式 1:微信 2:支付宝
    public String _id ="";   //类型：String  必有字段  备注：商品id
    public String standard_name ="";// 备注：规格名
    public int number ;//备注：购买数量

    public static class UserType{
        public String name;
        public String value;

        public UserType(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}

