package com.snqu.shopping.data.home.entity;

import java.util.List;

/**
 * @author 张全
 */
public class VipTaskEntity {
    public String _id;
    public List<Rule> event_rules;

    public String explain;
    public String process_explain;

    public static class Rule {
        public String has_value;
        public String max_value;
        public String desc;
    }
}
