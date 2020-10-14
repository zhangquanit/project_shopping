package com.snqu.shopping.data.user.entity;

import java.net.URLEncoder;

/**
 * @author 张全
 */
public class IncomeQueryParam {
    public int page=1;
    public int row = 10;
    public Sort sort = Sort.NONE;
    public String relation; //0:直接一级 1:二级 缺省为全部

    public enum Sort {
        NONE(""),
        TOTAL(""),
        MONTH(""),
        WEEK_UP("fans_all"),
        WEEK_DOWN("-fans_all");
        public String value;

        private Sort(String value) {
            try {
                this.value = URLEncoder.encode(value, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
                this.value = value;
            }
        }

    }
}
