package com.snqu.shopping.data.user.entity;

import androidx.annotation.Keep;

@Keep
public class InviteCodeEntity {
    //当前用户邀请码
    public String invite_link_code;
    //是否可以设置邀请码 此字段也可以作为可修改次数
    public Integer can_set;
    //显示内容
    public String show_content;
    //当前用户等级
    public String level;

    @Override
    public String toString() {
        return "InviteCodeEntity{" +
                "invite_link_code='" + invite_link_code + '\'' +
                ", can_set='" + can_set + '\'' +
                ", show_content='" + show_content + '\'' +
                ", level='" + level + '\'' +
                '}';
    }
}
