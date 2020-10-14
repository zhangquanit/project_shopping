package com.snqu.shopping.data.red.entity;

import com.snqu.shopping.data.goods.entity.GoodsEntity;

import java.util.List;

/**
 * 大V列表
 *
 * @author 张全
 */
public class BigVEntity extends BigVInfo {

    public List<GoodsEntity> good_info;

    public BigVInfo getBigvInfo() {
        BigVInfo bigVInfo = new BigVInfo();
        bigVInfo._id = _id;
        bigVInfo.avatar = avatar;
        bigVInfo.name = name;
        bigVInfo.source = source;
        bigVInfo.source_text = source_text;
        return bigVInfo;
    }
}
