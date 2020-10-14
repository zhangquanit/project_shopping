package com.snqu.shopping.data.home.entity.artical;

import com.google.gson.annotations.SerializedName;
import com.snqu.shopping.data.home.entity.ItemSourceEntity;

import java.util.ArrayList;
import java.util.List;

public class ItemSourceResponseEntity {

    /**
     * 首页
     */
    @SerializedName("1")
    public List<ItemSourceEntity> homeItemSource = new ArrayList<>();

    /**
     * 分平台搜索
     */
    @SerializedName("2")
    public List<ItemSourceEntity> searchItemSource = new ArrayList<>();

    /**
     * 订单中心
     */
    @SerializedName("3")
    public List<ItemSourceEntity> orderItemSource = new ArrayList<>();

    /**
     * 收益表报、我的收益
     */
    @SerializedName("4")
    public List<ItemSourceEntity> earnItemSource = new ArrayList<>();

}
