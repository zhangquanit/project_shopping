package com.snqu.shopping.data.mall.entity.address;

import java.util.List;

/**
 * 省份城市
 *
 * @author 张全
 */
public class ProvinceCityEntity {

    public String _id;
    public int level;
    public String name;
    public List<CityEntity> citys;


    public static class CityEntity {
        public String _id;
        public int level;
        public String name;
    }
}
