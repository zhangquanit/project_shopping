package com.snqu.shopping.data.user.entity;

import java.net.URLEncoder;

/**
 * @author 张全
 */
public class FansQueryParam {
    public int row = 10; //每页请求条数
    public int page = 1; //第几页

    public QuerySort sort = QuerySort.NONE;
    public String fans = "0"; //0:直接一级 1:二级 缺省为全部

    public String search;//搜索手机号或者昵称

    public String uid;


    public void reset() {
        page = 1;
        sort = QuerySort.NONE;
        search = null;
        fans = null;
        search = null;
    }

    public enum QuerySort {
        NONE(""), //综合
        TIME_UP("itime"),
        TIME_DOWN("-itime"),

        //下级数
        FANS_UP("fans_all"),
        FANS_DOWN("-fans_all"),

        //收入
        INCOME_UP("estimate_total"),
        INCOME_DOWN("-estimate_total");

        public String value;

        private QuerySort(String value) {
            try {
                this.value = URLEncoder.encode(value, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
                this.value = value;
            }
        }

    }
}
