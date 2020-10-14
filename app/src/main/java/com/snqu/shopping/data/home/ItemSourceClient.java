package com.snqu.shopping.data.home;

import android.text.TextUtils;

import com.android.util.ext.SPUtil;
import com.google.gson.Gson;
import com.snqu.shopping.data.home.entity.ItemSourceEntity;
import com.snqu.shopping.data.home.entity.artical.ItemSourceResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 张全
 */
public class ItemSourceClient {

    public static enum ItemSourceType {
        /**
         * 首页
         */
        HOME,
        /**
         * 订单中心
         */
        ORDER,
        /**
         * 分平台搜索
         */
        SEARCH,
        /**
         * 账单明细
         */
        EARN;
    }

    public static HashMap<String, String> itemSourceMap = new HashMap<>();

    public static void saveItemSource(ItemSourceResponseEntity responseEntity) {
        ItemSourceClient.responseEntity = responseEntity;
        try {
            Gson gson = new Gson();
            String data = gson.toJson(responseEntity);
            SPUtil.setString(ITEM_SOURCES, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ItemSourceResponseEntity getResponseEntity() {
        if (null != responseEntity) {
            return responseEntity;
        }

        String itemSources = SPUtil.getString(ITEM_SOURCES);
        if (!TextUtils.isEmpty(itemSources)) {
            Gson gson = new Gson();
            responseEntity = gson.fromJson(itemSources, ItemSourceResponseEntity.class);
        }
        return responseEntity;
    }

    public static List<ItemSourceEntity> getHomeItemSource() {
        return getItemSources(ItemSourceType.HOME);
    }

    public static String getItemSourceName(String itemSource) {
        if (itemSource == null) {
            return "";
        }
        if (itemSourceMap.size() == 0) {
            if (null != responseEntity) {
                if (responseEntity.homeItemSource != null) {
                    for (ItemSourceEntity itemSourceEntity : responseEntity.homeItemSource) {
                        itemSourceMap.put(itemSourceEntity.code, itemSourceEntity.name);
                    }
                }
                if (responseEntity.orderItemSource != null) {
                    for (ItemSourceEntity itemSourceEntity : responseEntity.orderItemSource) {
                        itemSourceMap.put(itemSourceEntity.code, itemSourceEntity.name);
                    }
                }
                if (responseEntity.earnItemSource != null) {
                    for (ItemSourceEntity itemSourceEntity : responseEntity.earnItemSource) {
                        itemSourceMap.put(itemSourceEntity.code, itemSourceEntity.name);
                    }
                }
                if (responseEntity.searchItemSource != null) {
                    for (ItemSourceEntity itemSourceEntity : responseEntity.searchItemSource) {
                        itemSourceMap.put(itemSourceEntity.code, itemSourceEntity.name);
                    }
                }
            }
            itemSourceMap.put("B","天猫");
        }
        return itemSourceMap.get(itemSource);
    }

    public static List<ItemSourceEntity> getSearchItemSource() {
        List<ItemSourceEntity> dataList = getItemSources(ItemSourceType.SEARCH);
        if (dataList.isEmpty()) {
            dataList.add(new ItemSourceEntity("淘宝", "C"));
            dataList.add(new ItemSourceEntity("京东", "D"));
            dataList.add(new ItemSourceEntity("拼多多", "P"));
            dataList.add(new ItemSourceEntity("唯品会", "V"));
            dataList.add(new ItemSourceEntity("苏宁","S"));
        }
        return dataList;
    }

    public static List<ItemSourceEntity> getOrderItemSource() {
        List<ItemSourceEntity> list = getItemSources(ItemSourceType.ORDER);
        if (list.isEmpty()) {
            list = new ArrayList<>();
            ItemSourceEntity itemSourceEntity = new ItemSourceEntity();
            itemSourceEntity.code = "all";
            itemSourceEntity.name = "全部";
            list.add(0, itemSourceEntity);
        }

        if (!TextUtils.equals("全部", list.get(0).name)) {
            ItemSourceEntity itemSourceEntity = new ItemSourceEntity();
            itemSourceEntity.code = "all";
            itemSourceEntity.name = "全部";
            list.add(0, itemSourceEntity);
        }
        return list;
    }

    public static List<ItemSourceEntity> getEarnItemSource() {
        return getItemSources(ItemSourceType.EARN);
    }

    public static List<ItemSourceEntity> getItemSources(ItemSourceType sourceType) {
        ItemSourceResponseEntity responseEntity = getResponseEntity();
        List<ItemSourceEntity> list = new ArrayList<>();
        if (null != responseEntity) {
            switch (sourceType) {
                case HOME:
                    list = responseEntity.homeItemSource;
                    break;
                case ORDER:
                    list = responseEntity.orderItemSource;
                    break;
                case SEARCH:
                    list = responseEntity.searchItemSource;
                    break;
                case EARN:
                    list = responseEntity.earnItemSource;
                    break;
            }
        }
        if (null == list) {
            list = new ArrayList<>();
        }

        return new ArrayList<ItemSourceEntity>(list);
    }


    public static ItemSourceEntity getItemSourceEntity(ItemSourceType sourceType, String code) {
        List<ItemSourceEntity> itemSources = getItemSources(sourceType);
        for (ItemSourceEntity entity : itemSources) {
            if (TextUtils.equals(entity.code, code)) {
                return entity;
            }
        }
        return null;
    }

    private static final String ITEM_SOURCES = "ITEM_SOURCES_V3";
    private static ItemSourceResponseEntity responseEntity;
}
