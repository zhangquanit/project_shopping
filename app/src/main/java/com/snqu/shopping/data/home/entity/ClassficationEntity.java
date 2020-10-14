package com.snqu.shopping.data.home.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张全
 */
public class ClassficationEntity {
    public String id;
    public String name;

    public List<ClassficationItemEntity> itemList = new ArrayList<>();
}
