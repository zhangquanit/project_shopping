package com.snqu.shopping.data.user.entity;

public class PigContract {
    public int requireCheck ; //是否需要签协议 [1:需要 0:不需要]
    public String url;//跳转地址 为良好兼容性请用系统浏览器跳转
    public String thismonth ;
    public String tips;//提示

    @Override
    public String toString() {
        return "PigContract{" +
                "requireCheck='" + requireCheck + '\'' +
                ", url='" + url + '\'' +
                ", thismonth='" + thismonth + '\'' +
                ", tips='" + tips + '\'' +
                '}';
    }
}
