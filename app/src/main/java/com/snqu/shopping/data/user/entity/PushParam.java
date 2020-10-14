package com.snqu.shopping.data.user.entity;

import java.io.Serializable;

public class PushParam implements Serializable {
    public String id;

    public String item_source;

    public String url;

    public String code;

    @Override
    public String toString() {
        return "PushParam{" +
                "id='" + id + '\'' +
                ", item_source='" + item_source + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
