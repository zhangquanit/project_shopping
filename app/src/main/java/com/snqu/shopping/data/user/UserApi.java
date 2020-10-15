package com.snqu.shopping.data.user;

import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.VersionEntity;
import com.snqu.shopping.data.user.entity.FeedUploadEntity;
import com.snqu.shopping.data.user.entity.FeedbackBody;
import com.snqu.shopping.data.user.entity.FeedbackEntity;
import com.snqu.shopping.data.user.entity.InviteCodeEntity;
import com.snqu.shopping.data.user.entity.KefuEntity;
import com.snqu.shopping.data.user.entity.RecommendSuperior;
import com.snqu.shopping.data.user.entity.AccountCancelEntity;
import com.snqu.shopping.data.user.entity.AccountInfoEntity;
import com.snqu.shopping.data.user.entity.AccountTipsEntity;
import com.snqu.shopping.data.user.entity.AlipayInfoEntity;
import com.snqu.shopping.data.user.entity.BalanceInfoEntity;
import com.snqu.shopping.data.user.entity.BalanceRecodeEntity;
import com.snqu.shopping.data.user.entity.EarningEnity;
import com.snqu.shopping.data.user.entity.FansEntity;
import com.snqu.shopping.data.user.entity.InvitedEntity;
import com.snqu.shopping.data.user.entity.InviterInfo;
import com.snqu.shopping.data.user.entity.PigContract;
import com.snqu.shopping.data.user.entity.PushMessageEntity;
import com.snqu.shopping.data.user.entity.SelfEarningEntity;
import com.snqu.shopping.data.user.entity.TeamIncomeEntity;
import com.snqu.shopping.data.user.entity.TutorShareContract;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.data.user.entity.UserFansEntity;
import com.snqu.shopping.data.user.entity.Watermark;
import com.snqu.shopping.data.user.entity.XltIncomeEntity;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author 张全
 */
public interface UserApi {
    /**
     * app升级
     *
     * @param url
     * @return
     */
    @GET
    Observable<ResponseDataObject<VersionEntity>> appUpdate(@Url String url);

    /**
     * 登陆发送验证码
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.LOGIN_CODE)
    Observable<ResponseDataObject<Object>> loginCode(@Field("phone") String phone);

    /**
     * 登录--校验验证码=》》》变更
     *
     * @param phone
     * @param code
     * @return
     */
    @POST(ApiHost.VERIFY_CODE)
    @FormUrlEncoded
    Observable<ResponseDataObject<UserEntity>> verifyCode(@Field("phone") String phone, @Field("code") String code);

    /**
     * 新版登陆发送验证码
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.SEND_BIND_CODE)
    Observable<ResponseDataObject<InvitedEntity>> newLoginCode(@Field("phone") String phone);

    /**
     * 微信登陆
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.LOGIN_WX)
    Observable<ResponseDataObject<UserEntity>> loginWX(@Field("code") String code);


    /**
     * 微信绑定
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.BIND_WX)
    Observable<ResponseDataObject<UserEntity>> bindWX(@Field("code") String code, @Header("Authorization") String token);

    /**
     * 新版验证码登陆
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.NEW_CODE_LOGIN)
    Observable<ResponseDataObject<UserEntity>> newCodeLogin(@Field("phone") String phone, @Field("code") String code, @Field("invite_code") String invite_code);

    /**
     * 微信登陆绑定手机号
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.WX_LOGIN_BIND_PHONE)
    Observable<ResponseDataObject<UserEntity>> wxCodeLogin(@Field("phone") String phone, @Field("code") String code, @Field("sid") String sid, @Field("invite_code") String invite_code);

    /**
     * 邀请绑定
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.INVITE_CODE)
    Observable<ResponseDataObject<Object>> inviteCode(@Field("invite_code") String invite_code, @Field("recommed") int recommed);

    /**
     * 绑定邀请码
     *
     * @param invite_code
     * @param uid
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.BIND_INVITE)
    Observable<ResponseDataObject<UserEntity>> bindInvite(@Field("invite_code") String invite_code, @Field("uid") String uid);

    /**
     * 获取邀请人信息
     *
     * @return
     */
    @GET(ApiHost.INVITER_INFO)
    Observable<ResponseDataObject<InviterInfo>> inviterInfo(@Query("code") String invite_code);

    /**
     * 获取邀请人信息(无auth校验)
     *
     * @return
     */
    @GET(ApiHost.INVITER_INFO_NO_AUTH)
    Observable<ResponseDataObject<InviterInfo>> inviterInfoNoAuth(@Query("code") String invite_code);

    //    /**
//     * 登录--校验验证码
//     *
//     * @param code
//     * @param phone
//     * @return
//     */
    @FormUrlEncoded
    @POST(ApiHost.FIND_INVITER_CODE)
    Observable<ResponseDataObject<UserEntity>> findInviterCode(@Field("phone") String phone, @Field("code") String code,
                                                               @Field("sid") String sid);

    /**
     * 获取登录用户信息
     *
     * @return
     */
    @GET(ApiHost.USER_INFO)
    Observable<ResponseDataObject<UserEntity>> userInfo();

    /**
     * 登出登陆
     *
     * @return
     */
    @GET(ApiHost.LOGIN_OUT)
    Observable<ResponseDataObject<Object>> loginOut();


    /**
     * 原手机号发送验证码
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.CHANGE_PHONE_CODE)
    Observable<ResponseDataObject<Object>> changePhoneCode(@Field("phone") String phone);

    /**
     * 原手机号验证码验证
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.CHANGE_PHONE_VERIFY_CODE)
    Observable<ResponseDataObject<Object>> changePhoneVerifyCode(@Field("phone") String phone, @Field("code") String code);

    /**
     * 绑定支付宝
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.BIND_ALIPAY)
    Observable<ResponseDataObject<Object>> bindAlipay(@Field("realname") String realName, @Field("alipay") String alipay, @Field("code") String code);

    /**
     * 获取支付宝信息
     *
     * @return
     */
    @GET(ApiHost.ALIPAY_INFO)
    Observable<ResponseDataObject<AlipayInfoEntity>> alipayInfo();

    /**
     * 我的余额 余额信息
     *
     * @return
     */
    @GET(ApiHost.BALANCE_INFO)
    Observable<ResponseDataObject<BalanceInfoEntity>> balanceInfo();

    /**
     * 注销详情
     *
     * @return
     */
    @POST(ApiHost.ACCOUNT_LOGOUT)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> accountLogout(
            @Field("phone") String phone,
            @Field("code") String code,
            @Field("reason") String reason
    );

    /**
     * 注销详情
     */
    @GET(ApiHost.ACCOUNT_LOGOUT_DETAILS)
    Observable<ResponseDataObject<AccountCancelEntity>> getLogoutDetails();

    /**
     * 获取用户时间区间收益报表
     *
     * @return
     */
    @GET(ApiHost.ACCOUNT_INFO)
    Observable<ResponseDataObject<AccountInfoEntity>> accountInfo(@Query("_id") String user_id);

    /**
     * 更换手机号并重新登录
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.CHANGE_PHONE_BIND_VERIFY_CODE)
    Observable<ResponseDataObject<UserEntity>> changePhoneBindVerifyCode(@Field("phone") String phone, @Field("code") String code);


    /**
     * 绑定支付宝前发送验证短信
     *
     * @return
     */
    @GET(ApiHost.ALIPAY_CODE)
    Observable<ResponseDataObject<Object>> alipayCode();

    /**
     * 我的余额 - 账户明细
     *
     * @return
     */
    @GET(ApiHost.BALANCE_RECODE)
    Observable<ResponseDataArray<BalanceRecodeEntity>> balanceRecode(@Query("month") String month, @Query("page") int page, @Query("limit") int limit,@Query("type")String type);

    /**
     * 我的余额页面-提现
     *
     * @return
     */
    @FormUrlEncoded
    @POST(ApiHost.BALANCE_WITHDRAW)
    Observable<ResponseDataObject<Object>> withdraw(@Field("amount") int amount, @Field("amount_useable") Long amount_useable);

    /**
     * 我的足迹
     *
     * @return
     */
    @GET(ApiHost.SELF_FOOT_GOODS)
    Observable<ResponseDataObject<List<GoodsEntity>>> selfFootGoods(@Query("page") int page, @Query("row") int row);

    /**
     * 绑定设备
     *
     * @param device_token
     * @return
     */
    @POST(ApiHost.BIND_DEVICE)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> bindDevice(@Field("device_token") String device_token);


    /**
     * 专属推荐
     *
     * @return
     */
    @GET(ApiHost.RECOMMEND_USER)
    Observable<ResponseDataArray<GoodsEntity>> doRecommendGoods(@Query("page") int page, @Query("row") int row);

    /**
     * 我的团队
     */
    @GET(ApiHost.USER_FANS)
    Observable<ResponseDataObject<UserFansEntity>> getUserFans();

    /**
     * 我的团队用户列表
     *
     * @return
     */
    @GET
    Observable<ResponseDataArray<FansEntity>> getFansList(@Url String url);

    /**
     * 团队收益
     *
     * @return
     */
    @GET(ApiHost.EARNING_TEAM)
    Observable<ResponseDataObject<EarningEnity>> getTeamEarning();

    /**
     * 自购收益
     *
     * @return
     */
    @GET(ApiHost.EARNING_SELF)
    Observable<ResponseDataObject<SelfEarningEntity>> getSlefEarning();

    /**
     * 乐淘收入榜
     *
     * @param type
     * @return
     */
    @GET(ApiHost.XLT_INCOME)
    Observable<ResponseDataArray<XltIncomeEntity>> getXltIncomes(@Query("type") String type);

    /**
     * 成员贡献榜——总预估佣金
     *
     * @param url
     * @return
     */
    @GET
    Observable<ResponseDataArray<TeamIncomeEntity>> getTeamIncomeTotal(@Url String url);

    /**
     * 成员贡献榜——本月预估佣金
     *
     * @param url
     * @return
     */
    @GET
    Observable<ResponseDataArray<TeamIncomeEntity>> getTeamIncomeMonth(@Url String url);

    /**
     * 成员贡献榜——7日拉新
     *
     * @param url
     * @return
     */
    @GET
    Observable<ResponseDataArray<TeamIncomeEntity>> getTeamIncomeInvite(@Url String url);

    /**
     * 自有消息推送列表
     *
     * @param userId
     * @return
     */
    @GET(ApiHost.UMENG_ACTION_LIST)
    Observable<ResponseDataArray<PushMessageEntity>> getActionList(@Query("userId") String userId);

    /**
     * 关闭某个消息推送
     *
     * @param userId
     * @return
     */
    @POST(ApiHost.UMENG_ACTION_BAN)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> banPushMessage(@Field("id") String id, @Field("userId") String userId, @Field("enable") String enable);

    /**
     * 设置给下级显示的微信ID
     */
    @POST(ApiHost.SET_WECHAT_ID)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> setWechatId(@Field("wechat_show_uid") String id);

    /**
     * 获取邀请码信息
     *
     * @return
     */
    @GET(ApiHost.GET_INVITE_CODE)
    Observable<ResponseDataObject<InviteCodeEntity>> getInviteCode();

    /**
     * 获取推荐人邀请码
     *
     * @return
     */
    @GET(ApiHost.GET_RECOMMEND_CODE)
    Observable<ResponseDataObject<RecommendSuperior>> getRecommendCode();

    /**
     * 设置邀请码
     *
     * @param invite_code
     * @return
     */
    @POST(ApiHost.SET_INVITE_CODE)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> setInviteCode(@Field("invite_code") String invite_code);

    /**
     * 检测邀请码是否被使用
     *
     * @param invite_code
     * @return
     */
    @POST(ApiHost.CHECK_INVITE_CODE)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> checkInviteCode(@Field("invite_code") String invite_code);

    /**
     * 获取推荐未使用的邀请码
     *
     * @return
     */
    @GET(ApiHost.GET_RAND_CODE)
    Observable<ResponseDataObject<List<String>>> getRandCode();

    /**
     * 获取问题反馈详情
     *
     * @return
     */
    @GET(ApiHost.GET_FEEDBACK_DETAIL)
    Observable<ResponseDataObject<FeedbackEntity>> getFeedbackDetail(@Query("id") String id);

    /**
     * 获取问题反馈列表
     *
     * @return
     */
    @GET(ApiHost.GET_FEEDBACK_LIST)
    Observable<ResponseDataArray<FeedbackEntity>> getFeedbackList(@Query("page") int page, @Query("pageSize") int pageSize, @Query("sort") String time);

    /**
     * 提交问题反馈列表
     *
     * @return
     */
    @POST(ApiHost.POST_SEND_FEEDBACK)
    Observable<ResponseDataObject<Object>> sendFeedBack(@Body FeedbackBody feedbackBody);

    /**
     * 客服
     *
     * @return
     */
    @GET(ApiHost.GET_CONFIG_KEFU)
    Observable<ResponseDataObject<KefuEntity>> getConfigKefu();

    /**
     * 文件上传
     *
     * @return
     */
    //上传文件
    @Multipart
    @POST(ApiHost.POST_UPFILE_FEEDBACK)
    Observable<ResponseDataArray<FeedUploadEntity>> uploadMultipleFiles(@Part MultipartBody.Part file, @Part MultipartBody.Part file2);


    /**
     * 检查提现金额是否需要签署协议
     *
     * @return
     */
    @GET(ApiHost.GET_CHECK_CONTRACT)
    Observable<ResponseDataObject<PigContract>> checkContract(@Query("amount") float amount);

    /**
     * 获取提醒
     *
     * @return
     */
    @GET(ApiHost.GET_ACCOUNT_TIPS)
    Observable<ResponseDataObject<AccountTipsEntity>> getAccountTips();

    /**
     * 保存用户水印信息
     *
     * @return
     */
    @POST(ApiHost.SAVE_USER_WATERMARK)
    Observable<ResponseDataObject<Object>> saveUserWatermark(@Body Watermark watermark);

    /**
     * 获取用户水印信息
     *
     * @return
     */
    @GET(ApiHost.GET_USER_WATERMARK)
    Observable<ResponseDataObject<Watermark>> getUserWatermark();

    /**
     * 账号注销-发送验证码
     *
     * @param phone
     * @return
     */
    @POST(ApiHost.LOGOUT_SENDCODE)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> logoutSendCode(@Field("phone") String phone);

    /**
     * 撤销账号注销申请
     *
     * @return
     */
    @POST(ApiHost.LOGOUT_REVOCATION)
    Observable<ResponseDataObject<Object>> logoutRevocation();

    /**
     * 获取我的导师分享文章
     *
     * @return
     */
    @GET(ApiHost.TUTOR_SHARE_LIST)
    Observable<ResponseDataArray<TutorShareContract>> getTutorShareList(@Query("page") int page, @Query("pageSize") int pageSize);

    /**
     * 获取我自己发布的导师分享文章
     *
     * @return
     */
    @GET(ApiHost.TUTOR_SHARE_ME_LIST)
    Observable<ResponseDataArray<TutorShareContract>> getMeTutorShareList(@Query("page") int page, @Query("pageSize") int pageSize, @Query("status") String status);

    /**
     * 创建导师分享文章
     */
    @POST(ApiHost.TUTOR_SHARE_CREATE)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> createTutorShare(@Field("logo") String logo, @Field("title") String title, @Field("content") String content);

    /**
     * 移动导师分享文章排序
     */
    @POST(ApiHost.TUTOR_SHARE_MOVE)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> moveTutorShare(@Field("share_id") String share_id, @Field("type") String type);

    /**
     * 设置导师分享文章状态
     */
    @POST(ApiHost.TUTOR_SHARE_SET_STATUS)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> changeTutorShareStatus(@Field("share_id") String share_id, @Field("status") String status);

    /**
     * 设置导师分享文章是否置顶
     */
    @POST(ApiHost.TUTOR_SHARE_TOP)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> changeTutorShareTop(@Field("share_id") String share_id, @Field("type") String type);

}
