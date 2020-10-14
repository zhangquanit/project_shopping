package com.snqu.shopping.data;

import com.instacart.library.truetime.TrueTimeRx;

/**
 * @author 张全
 */
public class TimeClient {

    public static long getNowTime() {
        long now = System.currentTimeMillis();
        try {
            now = TrueTimeRx.now().getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

}
