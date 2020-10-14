package com.snqu.shopping.data.user.entity;

public class TutorShareContract {


    /**
     * _id : 5f718ec64a71d029a32caa72
     * user_id : 5e4e26ef3b77501a58622983
     * logo : http://backend.xin1.cn/img/logo.7505308e.png
     * title : 我查看我的导师分享
     * content : 查看我的导师分享
     * status : 1
     * is_top : 0
     * view_user_count : 0
     * view_count : 0
     * from_share : null
     * sort : 500
     * itime : 1601277638
     * utime : 1601277638
     */

    public String _id;
    public String user_id;
    public String logo;
    public String title;
    public String content;
    public int status;
    public int is_top;
    public int view_user_count;
    public int view_count;
//    public Object from_share;
    public int sort;
    public int itime;
    public int utime;
    public String url; //详情
    public boolean isFirst ; //是否是除了置顶外的第一个
}
