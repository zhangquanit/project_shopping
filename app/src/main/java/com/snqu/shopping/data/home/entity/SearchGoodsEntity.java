package com.snqu.shopping.data.home.entity;

import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.GoodsEntity;

import java.util.List;

public class SearchGoodsEntity {
    public ResponseDataArray<GoodsEntity> goodsEntities; //搜索列表
    public ResponseDataArray<GoodsEntity> recommendEntities;//推荐列表

    public List<GoodsEntity> getDataList() {
        if (!goodsEntities.getDataList().isEmpty()) {
            return goodsEntities.getDataList();
        }
        return recommendEntities.getDataList();
    }
}
