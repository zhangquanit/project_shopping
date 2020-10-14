package com.snqu.shopping.data.user.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张全
 */
public class FansEntity implements Serializable {


    /**
     * _id : 5ec63c473b77501ff078e545
     * username : 149****0520
     * level : 2
     * avatar :
     * phone : 149****0520
     * itime : 1590049863
     * user_task_list : [{"_id":"5ec63c483b77501ff078e54d","task_id":"5dede63b0a6c8e22dc0012df","task_code":"5dede63b0a6c8e22dc0012df","user_id":"5ec63c473b77501ff078e545","type":2,"label":1,"reward_fetch":0,"reward_fetched":null,"event_rules":[{"event_type":null,"attr_type":"ATTR_VAILD_DIRECT_VIP","max_value":1,"desc":"直接有效会员人数","has_value":0},{"event_type":null,"attr_type":"ATTR_VAILD_OT_VIP","max_value":2,"desc":"直邀+间接有效会员人数","has_value":0},{"event_type":null,"attr_type":"ATTR_RECENT_30DAY_SETTLEMENT","max_value":2000,"desc":"近30天结算金额","has_value":0}],"score":0,"user_level":[2],"reward_id":["5e8e87c43b77502f24694e54"],"start_time":1575691646,"end_time":1648656000,"status":1,"itime":1590049864,"utime":1590049864}]
     * estimate_total : 0
     * inviter_username : 云。白白
     * fans_all : 0
     * fans_one : 0
     * fans_other : 0
     * recent : 0
     * copy_helptext : 自注册起3天可见
     * can_copy : 0
     * process_explain : 升级超级会员进度
     * process : 0
     */

    public String _id;
    public String username;
    public int level;
    public String avatar;
    public String phone;
    public long itime;
    public long estimate_total;
    public String inviter_username;
    public String status;
    public int fans_all;
    public int fans_one;
    public int fans_other;
    public String recent;
    public String copy_helptext;
    public String recommed;//1 APP推荐用户 ,0 非APP推荐用户
    public String can_copy;
    public String process_explain;
    public float process;
    public List<UserTaskListBean> user_task_list;
    public String register_from;

    public static class UserTaskListBean implements Serializable {
        /**
         * _id : 5ec63c483b77501ff078e54d
         * task_id : 5dede63b0a6c8e22dc0012df
         * task_code : 5dede63b0a6c8e22dc0012df
         * user_id : 5ec63c473b77501ff078e545
         * type : 2
         * label : 1
         * reward_fetch : 0
         * reward_fetched : null
         * event_rules : [{"event_type":null,"attr_type":"ATTR_VAILD_DIRECT_VIP","max_value":1,"desc":"直接有效会员人数","has_value":0},{"event_type":null,"attr_type":"ATTR_VAILD_OT_VIP","max_value":2,"desc":"直邀+间接有效会员人数","has_value":0},{"event_type":null,"attr_type":"ATTR_RECENT_30DAY_SETTLEMENT","max_value":2000,"desc":"近30天结算金额","has_value":0}]
         * score : 0
         * user_level : [2]
         * reward_id : ["5e8e87c43b77502f24694e54"]
         * start_time : 1575691646
         * end_time : 1648656000
         * status : 1
         * itime : 1590049864
         * utime : 1590049864
         */

        public String _id;
        public String task_id;
        public String task_code;
        public String user_id;
        public int type;
        public int label;
        public int reward_fetch;
        public Object reward_fetched;
        public int score;
        public int start_time;
        public int end_time;
        public int status;
        public int itime;
        public int utime;
        public List<EventRulesBean> event_rules;
        public List<Integer> user_level;
        public List<String> reward_id;

        public static class EventRulesBean implements Serializable {
            /**
             * event_type : null
             * attr_type : ATTR_VAILD_DIRECT_VIP
             * max_value : 1
             * desc : 直接有效会员人数
             * has_value : 0
             */

            public Object event_type;
            public String attr_type;
            public int max_value;
            public String desc;
            public int has_value;
        }
    }
}
