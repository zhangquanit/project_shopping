package com.snqu.shopping.data.home.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author 张全
 */
public class HomeAdEntity {
//    @SerializedName("3")
//    public List<AdvertistEntity> bannerEntitys;
    @SerializedName("4")
    public List<AdvertistEntity> cetnerAdEntity;
    @SerializedName("10")
    public List<AdvertistEntity> tipAdEntity; //首页顶部引导
    @SerializedName("11")
    public List<AdvertistEntity> alertAdEntity; //首页弹框广告

    @SerializedName("10003")
    public List<AdvertistEntity> freeAdEntity; //新人0元购

    @SerializedName("20001")
    public List<AdvertistEntity> searchAdEntity; //搜索页引导广告

    @SerializedName("19")
    public List<AdvertistEntity> bottomEntity; //底部通知栏
}
