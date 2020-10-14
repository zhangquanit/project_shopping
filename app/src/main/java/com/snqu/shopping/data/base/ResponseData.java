package com.snqu.shopping.data.base;

/**
 * @author 张全
 */
public class ResponseData {
    public int code;
    public String message;

    public boolean isSuccessful() {
        return code == 0;
    }
}
