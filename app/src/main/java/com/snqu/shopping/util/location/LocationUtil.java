package com.snqu.shopping.util.location;

import android.text.TextUtils;

import com.android.util.ext.SPUtil;
import com.google.gson.Gson;

/**
 * @author 张全
 */
public class LocationUtil {
    private static final String LOCATION = "location";
    private static LocationEntity locationEntity;

    ///获取纬度
    public static LocationEntity getLocation() {
        if (null == locationEntity) {
            String value = SPUtil.getString(LOCATION);
            if (!TextUtils.isEmpty(value)) {
                locationEntity = new Gson().fromJson(value, LocationEntity.class);
            }
        }
        return locationEntity;
    }

    public static void setLocation(LocationEntity location) {
        if (null != location) {
            locationEntity = location;
            SPUtil.setString(LOCATION, new Gson().toJson(location));
        }
    }

}
