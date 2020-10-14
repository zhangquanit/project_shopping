package com.snqu.shopping.data.base;

import java.util.ArrayList;
import java.util.List;

/**
 * 马超专用data array
 * @author 张全
 */
public class MCResponseDataArray<T> extends ResponseData {
    ListData<T> data;

    public List<T> getList() {
        if (null == data || null == data.list) {
            return new ArrayList<>();
        }
        return data.list;
    }
}
