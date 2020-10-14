package com.snqu.shopping.data.home.entity;

import com.android.util.db.Key;

/**
 * 首页 banner
 *
 * @author 张全
 */
public class BannerEntity {

    @Key
    public String _id;

    public String name;
    public String url;
    public String pic_url;

    @Override
    public String toString() {
        return "BannerEntity{" +
                "_id='" + _id + '\'' +
                ", username='" + name + '\'' +
                ", url='" + url + '\'' +
                ", pic_url='" + pic_url + '\'' +
                '}';
    }
}
