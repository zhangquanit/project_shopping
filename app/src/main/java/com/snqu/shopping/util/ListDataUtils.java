package com.snqu.shopping.util;

import java.util.ArrayList;
import java.util.List;

public class ListDataUtils {

//    public static List<List> averageAssign(List source, int n) {
//        List<List> result = new ArrayList<List>();
//        int remainder = source.size() % n; //(先计算出余数)
//        int number = source.size() / n; //然后是商
//        int offset = 0;//偏移量
//        for (int i = 0; i < n; i++) {
//            List value = null;
//            if (remainder > 0) {
//                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
//                remainder--;
//                offset++;
//            } else {
//                value = source.subList(i * number + offset, (i + 1) * number + offset);
//            }
//            result.add(value);
//        }
//        return result;
//    }

    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remainder = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remainder > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remainder--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }


}
