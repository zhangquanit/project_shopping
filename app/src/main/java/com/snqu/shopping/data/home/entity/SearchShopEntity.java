package com.snqu.shopping.data.home.entity;


import com.snqu.shopping.data.base.ResponseDataArray;

import java.util.List;

public class SearchShopEntity {
    public ResponseDataArray<ShopItemEntity> shopEntities; //商铺列表
    public ResponseDataArray<ShopItemEntity> recommendEntities;//推荐列表

    public List<ShopItemEntity> getDataList() {
        if (!shopEntities.getDataList().isEmpty()) {
            return shopEntities.getDataList();
        }
        return recommendEntities.getDataList();
    }
}
