package com.snqu.shopping.data.home.entity;

/**
 * @author 张全
 */
public class CommunityRewardEntity {
    /**
     * _id : 5eec58c84a71d0622037e1c5
     * user_id : 5e4e26ef3b77501a58622983
     * goods_id : 072a5b70e066ab6e6b283f951302efff
     * item_image : https://a.vpimg2.com/upload/merchandise/pdcvis/2020/04/08/95/57b9273b-6be9-4297-a47a-ced5d8d1bcab.jpg
     * item_source : V
     * item_title : 女款休闲简约百搭舒适圆领套头基础小猪绣花短袖T恤
     * itime : 1592547528
     * order_count : 0
     * rank : 9999999
     * recm_itime : 1592547528
     * reward_amount : 0
     * set_time : 1595469600
     * status : 4
     * utime : 1592547528
     */

    public String _id;
    public String user_id;
    public String goods_id;
    public String item_image;
    public String item_source;
    public String item_title;
    public long itime;
    public long order_count;
    public String rank;
    public long recm_itime;
    public long reward_amount;
    public long set_time;
    public int status; //1 等待结算 2 已结算 3已失效 4 预估结算中
    public long utime;
    public String settle_type; // 1 正常结算  2 异步结算



}
