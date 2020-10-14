package com.snqu.shopping.data.user.entity;

public class AccountCancelEntity {
    public long logout_time;
    public long itime;
    public String status;
    public String uid;

    @Override
    public String toString() {
        return "AccountCancelEntity{" +
                "logout_time=" + logout_time +
                ", status='" + status + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}
