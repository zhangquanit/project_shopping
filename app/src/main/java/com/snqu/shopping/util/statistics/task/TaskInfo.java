package com.snqu.shopping.util.statistics.task;

import java.io.Serializable;

/**
 * @author 张全
 */
public class TaskInfo implements Serializable {
    public String id;
    public long countDown;
    public String reward;
    public String type; //1浏览商品 2分享
}
