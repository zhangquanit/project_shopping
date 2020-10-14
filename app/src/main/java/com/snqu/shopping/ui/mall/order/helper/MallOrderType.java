package com.snqu.shopping.ui.mall.order.helper;

import com.snqu.shopping.R;

/**
 * 订单状态
 */
public enum MallOrderType {
//    public static final int STATUS_ALL = -1;
//    public static final int STATUS_PAY = 10;
//    public static final int STATUS_FH = 20;
//    public static final int STATUS_SH = 30;
//    public static final int STATUS_COMPLETE = 40;

    ALL("全部", -1, -1),
    CANCEL("已取消", 0, R.drawable.mall_status_cancel),
    PAY("待支付", 10, R.drawable.mall_status_pay),
    FH("待发货", 20, R.drawable.mall_status_sh),
    SH("待收货", 30, R.drawable.mall_status_sh),
    COMPLETE("已完成", 40, R.drawable.mall_status_complete);

    public String name;
    public int status;
    public int res;

    private MallOrderType(String name, int status, int res) {
        this.name = name;
        this.status = status;
        this.res = res;
    }

    public static String getMallOrderType(int status) {
        MallOrderType[] values = MallOrderType.values();
        for (MallOrderType orderType : values) {
            if (orderType.status == status) {
                return orderType.name;
            }
        }
        return null;
    }

    public static MallOrderType getMallOrder(int status) {
        MallOrderType[] values = MallOrderType.values();
        for (MallOrderType orderType : values) {
            if (orderType.status == status) {
                return orderType;
            }
        }
        return MallOrderType.ALL;
    }
}
