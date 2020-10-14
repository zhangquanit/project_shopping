package com.snqu.shopping.data.mall.entity.address;

import java.io.Serializable;

/**
 * 前置仓过滤位置信息
 *
 * @author 张全
 */
public class LocationEntity implements Serializable {
    public String mAddressId; //用户从地址管理中选择的
    public String mAddress;
    public String mLatitude;
    public String mLongitude;
}
