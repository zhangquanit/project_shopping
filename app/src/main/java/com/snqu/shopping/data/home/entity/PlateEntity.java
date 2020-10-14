package com.snqu.shopping.data.home.entity;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 板块
 *
 * @author 张全
 */
public class PlateEntity implements Serializable {

    /**
     * _id : 5d65eeac1d40260f4c005e02
     * title : 花花
     * sub_title : 12
     * icon : /static/images/20190828/accfb3f3544b75436d353baaed849f8d.jpg
     * dev_code : 2
     * is_dev : 0
     */

    public String _id;
    public String title;
    public String name;
    public String sub_title;
    public String icon;
    public String dev_code; //板块代码
    public int is_dev; //是否专属板块


    public String code;
    @SerializedName("children")
    public List<PlateEntity> categories_list;


    public String getSubTitle() {
        if (!TextUtils.isEmpty(title)) {
            return title;
        }
        return name;
    }
}
