package com.snqu.shopping.data.user.entity;

/**
 * @author 张全
 */
public class UserFansEntity {
    /**
     * "fans_onetwo":0,                //类型：Number  必有字段  备注：团队总人数
     * "fans_one":0,                //类型：Number  必有字段  备注：直接下级
     * "fans_two":1,                //类型：Number  必有字段  备注：二级下代?
     * "today":0,                //类型：Number  必有字段  备注：今日新增
     * "yesterday":0,                //类型：Number  必有字段  备注：昨日新增
     * "month":2,                //类型：Number  必有字段  备注：本月新增
     * "lastmonth":0                //类型：Number  必有字段  备注：上月新增
     */

    public String fans_all; //团队总人数
    public String fans_one; //直接下级
    public String fans_other; //二级下代
    public String today; //今日新增
    public String yesterday; //昨日新增
    public String month; //本月新增
    public String lastmonth; //上月新增
    public long vaild_direct_vip; //有效直接用户(会员)数
    public long vaild_indirect_vip; //有效其它用户(会员)数
}
