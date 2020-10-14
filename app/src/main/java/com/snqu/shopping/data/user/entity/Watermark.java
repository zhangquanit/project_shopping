package com.snqu.shopping.data.user.entity;

import com.android.util.db.Key;

import java.io.Serializable;

public class Watermark implements Serializable {
    @Key
    public String _id;

    public String watermark;                //类型：String  必有字段  备注：水印名称

    public String user_id;                 //类型：String  必有字段  备注：用户_id

    public String status;              //类型：String  必有字段  备注：水印状态 status 开启:1、已禁用:0

    public int enabled;                //类型：String  必有字段  备注：用户使用状态 enabled 开启:1、关闭:0

    public String reason;                //类型：String  必有字段  备注：禁用原因

    @Override
    public String toString() {
        return "Watermark{" +
                "_id='" + _id + '\'' +
                ", watermark='" + watermark + '\'' +
                ", user_id='" + user_id + '\'' +
                ", status='" + status + '\'' +
                ", enabled=" + enabled +
                ", reason='" + reason + '\'' +
                '}';
    }
}
