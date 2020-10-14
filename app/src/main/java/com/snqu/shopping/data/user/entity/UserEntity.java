package com.snqu.shopping.data.user.entity;

import com.android.util.db.Key;

/**
 * @author 张全
 */
public class UserEntity {
    public String token;
    @Key
    public String _id;
    public String avatar;
    public int level = 1;//用户等级 1=>用户 2=>会员 3=>超级会员 4=>最高级(名称未定)
    public String invite_link = "0";//是否绑定邀请 0不是 1 是
    public String wechat_info = "0";//是否绑定微信 0不是 1 是
    public String invite_link_code;//邀请码
    public String inviter;//邀请人
    public String invited;//
    public String inviter_link_code;//上级邀请码
    public String inviter_avatar;//邀请人头像
    public String tutor_inviter_avatar; //导师头像
    public String tutor_inviter_username;//导师昵称
    public String phone;
    public String phone_hide;//隐藏了中间四位
    public String username;
    public String is_new;//是否为新用户 0不是 1 是
    public String bind_alipay;//1 绑定  0 未绑定
    public String sid;//微信中转的token
    public int canSkipInvited;//为1可以跳过
    public long itime;//注册时间
    public ConfigInfo config;//配制
    public int has_bind_tb = -1;//淘宝授权  1 授权 0未授权
    public long svip_expire;// 超级会员到期时间
    public String is_logout; //0正常，1代表冻结中，2代表已注销
    public String wechat_show_uid; //用户设置的微信
    public String tutor_wechat_show_uid;//上级导师微信
    public int document_id;//提现金额是否需要签署协议,document_id=1，说明已经签署，其余情况未签署
    public String isTutor; //是否是导师。1是导师，0 不是

    public class ConfigInfo {
        public String xlt_withdraw_date;//提现到账天
        public Long xlt_min_withdraw_amount;//提现最小金额
        public String xlt_rebate_time;//结算时间 天
        public String helpvideo_url; //视频教程
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "token='" + token + '\'' +
                ", _id='" + _id + '\'' +
                ", avatar='" + avatar + '\'' +
                ", level=" + level +
                ", invite_link='" + invite_link + '\'' +
                ", wechat_info='" + wechat_info + '\'' +
                ", invite_link_code='" + invite_link_code + '\'' +
                ", inviter='" + inviter + '\'' +
                ", invited='" + invited + '\'' +
                ", inviter_avatar='" + inviter_avatar + '\'' +
                ", phone='" + phone + '\'' +
                ", phone_hide='" + phone_hide + '\'' +
                ", username='" + username + '\'' +
                ", is_new='" + is_new + '\'' +
                ", bind_alipay='" + bind_alipay + '\'' +
                ", sid='" + sid + '\'' +
                ", config=" + config +
                ", has_bind_tb=" + has_bind_tb +
                ", svip_expire=" + svip_expire +
                '}';
    }
}
