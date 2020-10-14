package com.snqu.shopping.data.mall.entity.address;

/**
 * @author 张全
 */
public class PoiEntity {
    public String province;//四川省
    public String city; //成都市
    public String name; //天府新谷10
    public String address; //成汉南路与锦晖西二街交叉口东北150米
    public String district; //武侯区
    public double lon;
    public double lat;

    @Override
    public String toString() {
        return "PoiEntity{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", district='" + district + '\'' +
                ", lon=" + lon +
                ", lat=" + lat +
                '}';
    }
}
