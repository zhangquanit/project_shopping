package com.snqu.shopping.data.home.entity;

import android.text.TextUtils;

import com.android.util.db.Key;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 板块
 *
 * @author 张全
 */
public class CategoryEntity implements Serializable {
    @Key
    public String _id;
    public String pid;
    public String name;
    public String icon;
    public int sort; //排序
    public int level; //层级
    public String title;//子类板块中使用
    public int status;//状态
    public int show_index; //显示在首页


    public List<CategoryEntity> childList = new ArrayList<>();

    public String getName() {
        if (!TextUtils.isEmpty(name)) {
            return name;
        }
        return title;
    }

}
