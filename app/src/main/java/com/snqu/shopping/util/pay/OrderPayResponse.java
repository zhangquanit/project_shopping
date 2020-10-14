package com.snqu.shopping.util.pay;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 支付信息
 *
 * @author 张全
 */
public class OrderPayResponse implements Serializable {
    public String appid;
    public String partnerid;
    public String prepayid;
    @SerializedName("package")
    public String packageStr;
    public String timestamp;
    public String noncestr;
    public String sign;

    public String orderInfo; //支付宝支付信息.

    public String orderId;//订单id
    public String price; //支付价格
}
