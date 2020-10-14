package com.snqu.shopping.data.user.entity;

/**
 * @author 张全
 */
public class TeamIncomeEntity {
    /**
     * {                //类型：Object  必有字段  备注：无
     * "_id":"tcw111112021",                //类型：String  必有字段  备注：无
     * "total_amount":25,                //类型：Number  必有字段  备注：金额
     * "username":"小田",                //类型：String  必有字段  备注：昵称
     * "avatar":"mixed",                //类型：Mixed  必有字段  备注：头像
     * "level":4,                //类型：Number  必有字段  备注：等级 [1用户 2会员 3超级会员 4运营总监]
     * "level_text":"运营总监"                //类型：String  必有字段  备注：等级说明
     * }
     */
    public String _id;
    public String avatar;
    public String username;
    public long total_amount;
    public int level;
    public String level_text;
    public int fans_all; //拉新人数

}
