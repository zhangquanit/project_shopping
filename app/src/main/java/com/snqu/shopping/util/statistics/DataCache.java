package com.snqu.shopping.util.statistics;

import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.CategoryEntity;

/**
 * @author 张全
 */
public class DataCache {
    public static CategoryEntity homeFirstCategory; //首页一级分类
    public static CategoryEntity classificationFirstCategory; //分类tab一级分类
    public static CategoryEntity firstCategory; //一级分类
    public static CategoryEntity thirdCategory; //三级分类

    public static String plate_first_id;//一级板块id
    public static String plate_first_name; //一级板块名字

    public static String plate_second_id;//二级板块id
    public static String plate_second_name; //二级板块名字

    public static void clearCategory() {
        firstCategory = null;
        thirdCategory = null;
    }

    public static void clearSubCategory() {
//        thirdCategory = null;
    }

    public static void clearPlate() {
        plate_first_id = null;
        plate_first_name = null;
        plate_second_id = null;
        plate_second_name = null;
        thirdCategory = null;
    }

    public static void clearSubPlate() {
//        plate_second_id = null;
//        plate_second_name = null;
    }

    public static void reportGoodsByCategory(GoodsEntity goodsEntity, int pos) {
        SndoData.reportGoodsByCategory(goodsEntity, pos, "", firstCategory, thirdCategory);
    }

    public static void reportGoodsByPlate(GoodsEntity goodsEntity, int pos) {
        SndoData.reportGoodsByPlate(goodsEntity, pos, "", plate_first_id, plate_first_name, plate_second_id, plate_second_name);
    }


    public static void reportGoodsCountByCategory(int count) {
        SndoData.reportGoodsCountByCategory(count, firstCategory, thirdCategory);
    }

    public static void reportGoodsCountByPlate(int count) {
        SndoData.reportGoodsCountByPlate(count, plate_first_id, plate_first_name, plate_second_id, plate_second_name);
    }
}
