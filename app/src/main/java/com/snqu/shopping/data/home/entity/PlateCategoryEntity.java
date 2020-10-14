package com.snqu.shopping.data.home.entity;

import java.io.Serializable;

/**
 * 首页推荐-分类
 *
 * @author 张全
 */
public class PlateCategoryEntity implements Serializable {
    public String plate_id;
//    public String id;
    public String name;
    public String icon;
    public String pid;
    public String dev_code;
    public int is_dev;
    public int ch_set_icon;//是否设置二级图标
//    public String title;
}
