package com.snqu.shopping.data.user.entity;

import com.android.util.db.Key;

import java.io.Serializable;

public class PushEntity implements Serializable {

    @Key
    public String _id;

    public String id;

    public String title;

    public String content;

    public String page;

    public PushParam param;

    @Override
    public String toString() {
        return "PushEntity{" +
                "_id='" + _id + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", page='" + page + '\'' +
                ", param=" + param +
                '}';
    }
}
