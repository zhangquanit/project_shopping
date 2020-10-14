package com.snqu.shopping.data.home.entity;

import java.util.List;

/**
 * 会员权益
 */
public class VipRightEntity {
    public List<VipRightItem> list;
    public int level;

    public static class VipRightItem {
        public int level;
        public String icon;
        public List<VipRight> rights;
        public List<VipMoreImg> moreimg;

    }

    public static class VipRight {
        public String title;
        public String icon;
        public String subtitle;

    }

    public static class VipMoreImg {
        public String icon; //图标
        public String url; //跳转地址
    }


    public VipRightItem getLevelRight(int level) {
        for (VipRightItem item : list) {
            if (item.level == level) {
                return item;
            }
        }
        return null;
    }
}
