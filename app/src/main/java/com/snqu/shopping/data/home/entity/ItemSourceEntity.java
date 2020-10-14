package com.snqu.shopping.data.home.entity;

import java.util.List;

public class ItemSourceEntity {
    public String _id;
    public String name;
    public String code;
    public String icon;
    public List<Integer> show_position;

    public ItemSourceEntity(){}

    public ItemSourceEntity(String name, String code) {
        this.name = name;
        this.code = code;
    }

    @Override
    public String toString() {
        return "ItemSourceEntity{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", icon='" + icon + '\'' +
                ", show_position=" + show_position +
                '}';
    }
}
