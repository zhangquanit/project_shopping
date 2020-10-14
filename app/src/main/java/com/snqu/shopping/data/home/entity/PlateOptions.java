package com.snqu.shopping.data.home.entity;

import java.util.List;

/**
 * @author 张全
 */
public class PlateOptions {
    public String _id;
    public int is_dev; //是否为人工板块
    public String dev_code; //板块代码
    public List<String> item_source; //为空则筛选全部
    public List<String> item_tag;

}
