package com.snqu.shopping.data.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;

import com.android.util.LContext;
import com.android.util.db.EasyDB;
import com.android.util.ext.SPUtil;
import com.blankj.utilcode.util.SPUtils;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.DataConfig;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.base.RestClient;
import com.snqu.shopping.data.base.RestRequest;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.VersionEntity;
import com.snqu.shopping.data.user.entity.AccountCancelEntity;
import com.snqu.shopping.data.user.entity.AccountInfoEntity;
import com.snqu.shopping.data.user.entity.AccountTipsEntity;
import com.snqu.shopping.data.user.entity.AlipayInfoEntity;
import com.snqu.shopping.data.user.entity.BalanceInfoEntity;
import com.snqu.shopping.data.user.entity.BalanceRecodeEntity;
import com.snqu.shopping.data.user.entity.EarningEnity;
import com.snqu.shopping.data.user.entity.FansEntity;
import com.snqu.shopping.data.user.entity.FansQueryParam;
import com.snqu.shopping.data.user.entity.FeedUploadEntity;
import com.snqu.shopping.data.user.entity.FeedbackBody;
import com.snqu.shopping.data.user.entity.FeedbackEntity;
import com.snqu.shopping.data.user.entity.IncomeQueryParam;
import com.snqu.shopping.data.user.entity.InviteCodeEntity;
import com.snqu.shopping.data.user.entity.InvitedEntity;
import com.snqu.shopping.data.user.entity.InviterInfo;
import com.snqu.shopping.data.user.entity.KefuEntity;
import com.snqu.shopping.data.user.entity.PigContract;
import com.snqu.shopping.data.user.entity.PushMessageEntity;
import com.snqu.shopping.data.user.entity.RecommendSuperior;
import com.snqu.shopping.data.user.entity.SelfEarningEntity;
import com.snqu.shopping.data.user.entity.TeamIncomeEntity;
import com.snqu.shopping.data.user.entity.TutorShareContract;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.data.user.entity.UserFansEntity;
import com.snqu.shopping.data.user.entity.Watermark;
import com.snqu.shopping.data.user.entity.XltIncomeEntity;
import com.snqu.shopping.ui.mine.fragment.InvitePersonFragment;
import com.snqu.shopping.util.CommonUtil;
import com.umeng.message.PushAgent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;


/**
 * @author 张全
 */
public class UserClient {
    private static UserEntity loginUser; //当前登录用户
    public static String IS_LOGIN = "IS_LOGIN";
    public static Long canWithdrawal = 0L;//可提现金额
    public static Long unsettled_amount = 0L;//未结算金额
    public static Long amount_useable = 0L;//可提现余额
    private static String WATCH_VIDEO = "WATCH_VIDEO";
    private static String SHOW_GUIDE = "SHOW_GUIDE";//引导页
    private static String NEW_USER_GUIDE = "NEW_USER_GUIDE";//

    static {

        //监听用户变化 ,只要对用户有操作，这里都有回调
        UserClient.getUserWithChange()
                .subscribe(new Consumer<List<UserEntity>>() {
                    @Override
                    public void accept(List<UserEntity> list) throws Exception {

                        if (!list.isEmpty()) {
                            loginUser = list.get(0);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private static <T> T getService(Class<T> api) {
        return RestClient.getService(api);
    }


    /**
     * app升级
     *
     * @return
     */
    public static Observable<ResponseDataObject<VersionEntity>> doUpdate(String channel) {
        RestRequest request = new RestRequest.Builder(ApiHost.APP_UPDATE).addParam("channel", channel).build();
        return getService(UserApi.class).appUpdate(request.getUrl());
    }


    /**
     * 微信登陆
     *
     * @return
     */
    public static Observable<ResponseDataObject<UserEntity>> doLoginWX(String code) {
        return getService(UserApi.class).loginWX(code);
    }

    /**
     * 微信绑定
     *
     * @return
     */
    public static Observable<ResponseDataObject<UserEntity>> doBindWX(String code, String token) {
        return getService(UserApi.class).bindWX(code, token);
    }

    /**
     * 登陆发送验证码
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> doLoginCode(String phone) {
        return getService(UserApi.class).loginCode(phone);
    }

    /**
     * 登录--校验验证码=》》》变更
     *
     * @param phone
     * @param code
     * @return
     */
    public static Observable<ResponseDataObject<UserEntity>> verifyCode(String phone, String code) {
        return getService(UserApi.class).verifyCode(phone, code);
    }

    /**
     * 新版登陆发送验证码
     *
     * @return
     */
    public static Observable<ResponseDataObject<InvitedEntity>> doNewLoginCode(String phone) {
        return getService(UserApi.class).newLoginCode(phone);
    }


    /**
     * 新版验证码登陆
     *
     * @return
     */
    public static Observable<ResponseDataObject<UserEntity>> doNewCodeLogin(String phone, String code, String invite_code) {
        return getService(UserApi.class).newCodeLogin(phone, code, invite_code);
    }

    /**
     * 微信登陆绑定手机号
     *
     * @return
     */
    public static Observable<ResponseDataObject<UserEntity>> doWXCodeLogin(String phone, String code, String sid, String invite_code) {
        return getService(UserApi.class).wxCodeLogin(phone, code, sid, invite_code);
    }

    /**
     * 邀请绑定
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> doInviteCode(String invite_code, int rec) {
        return getService(UserApi.class).inviteCode(invite_code, rec);
    }

    /**
     * 绑定邀请
     *
     * @param invite_code
     * @param uid
     * @return
     */
    public static Observable<ResponseDataObject<UserEntity>> bindInvite(String invite_code, String uid) {
        return getService(UserApi.class).bindInvite(invite_code, uid);
    }

//    /**
//     * 获取邀请人信息
//     *
//     * @return
//     */
//    public static Observable<ResponseDataObject<InviterInfo>> doInviterInfo(String invite_code) {
//        return getService(UserApi.class).
//    }

    /**
     * 获取邀请人信息（无AUTH校验））
     *
     * @param invite_code
     * @return
     */
    public static Observable<ResponseDataObject<InviterInfo>> doInviterInfoNoAuth(String invite_code) {
        return getService(UserApi.class).inviterInfoNoAuth(invite_code);
    }

    //    /**
//     * 登录--校验验证码
//     */
    public static Observable<ResponseDataObject<UserEntity>> findInviterCode(String phone, String code, String sid) {
        return getService(UserApi.class).findInviterCode(phone, code, sid);
    }

    /**
     * 获取登录用户信息
     *
     * @return
     */
    public static Observable<ResponseDataObject<UserEntity>> doUserInfo() {
        return getService(UserApi.class).userInfo();
    }

    /**
     * 登出登陆
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> doLoginOut() {
        return getService(UserApi.class).loginOut();
    }

    /**
     * 原手机号发送验证码
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> doChangePhoneCode(String phone) {
        return getService(UserApi.class).changePhoneCode(phone);
    }

    /**
     * 原手机号验证码验证
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> doChangePhoneVerifyCode(String phone, String code) {
        return getService(UserApi.class).changePhoneVerifyCode(phone, code);
    }

    /**
     * 绑定支付宝
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> doBindAlipay(String realName, String alipay, String code) {
        return getService(UserApi.class).bindAlipay(realName, alipay, code);
    }

    /**
     * 获取支付宝信息
     *
     * @return
     */
    public static Observable<ResponseDataObject<AlipayInfoEntity>> doAlipayInfo() {
        return getService(UserApi.class).alipayInfo();
    }

    /**
     * 设置给下级显示的微信id
     *
     * @param id
     * @return
     */
    public static Observable<ResponseDataObject<Object>> setWechatId(String id) {
        return getService(UserApi.class).setWechatId(id);
    }

    /**
     * 我的余额 余额信息
     *
     * @return
     */
    public static Observable<ResponseDataObject<BalanceInfoEntity>> doBalanceInfo() {
        return getService(UserApi.class).balanceInfo();
    }

    /**
     * 账户注销
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> accountLogou(String phone,
                                                                      String code,
                                                                      String reason) {
        return getService(UserApi.class).accountLogout(phone, code, reason);
    }

    /**
     * 注销详情
     *
     * @return
     */
    public static Observable<ResponseDataObject<AccountCancelEntity>> getLogoutDetails() {
        return getService(UserApi.class).getLogoutDetails();
    }

    /**
     * 获取用户时间区间收益报表
     *
     * @return
     */
    public static Observable<ResponseDataObject<AccountInfoEntity>> doAccountInfo(String user_id) {
        return getService(UserApi.class).accountInfo(user_id);
    }

    /**
     * 更换手机号并重新登录
     *
     * @return
     */
    public static Observable<ResponseDataObject<UserEntity>> doChangePhoneBindVerifyCode(String phone, String code) {
        return getService(UserApi.class).changePhoneBindVerifyCode(phone, code);
    }


    /**
     * 绑定支付宝前发送验证短信
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> doAlipayCode() {
        return getService(UserApi.class).alipayCode();
    }

    /**
     * 我的余额 - 账户明细
     *
     * @return
     */
    public static Observable<ResponseDataArray<BalanceRecodeEntity>> doBalanceRecode(String yearMonth, int page, int pageSize) {
        return getService(UserApi.class).balanceRecode(yearMonth, page, pageSize);
    }

    /**
     * 我的余额页面-提现
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> doWithdraw(int amount, Long amount_useable) {
        return getService(UserApi.class).withdraw(amount, amount_useable);
    }

    /**
     * 我的足迹
     *
     * @return
     */
    public static Observable<ResponseDataObject<List<GoodsEntity>>> doSelfFootGoods(int page, int row) {
        return getService(UserApi.class).selfFootGoods(page, row);
    }

    /**
     * 绑定设备
     *
     * @param deviceToken
     * @return
     */
    public static Observable<ResponseDataObject<Object>> bindDevice(String deviceToken) {
        return getService(UserApi.class).bindDevice(deviceToken);
    }

    /**
     * 专属推荐
     *
     * @param page
     * @param row
     * @return
     */
    public static Observable<ResponseDataArray<GoodsEntity>> doRecommendGoods(int page, int row) {
        return getService(UserApi.class).doRecommendGoods(page, row);
    }

    /**
     * 我的团队
     *
     * @return
     */
    public static Observable<ResponseDataObject<UserFansEntity>> getUserFans() {
        return getService(UserApi.class).getUserFans();
    }

    /**
     * 粉丝列表
     *
     * @param queryParam
     * @return
     */
    public static Observable<ResponseDataArray<FansEntity>> getFansList(FansQueryParam queryParam) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.USER_FANS_LIST).append("?");
        sb.append("page=" + queryParam.page);
        sb.append("&row=" + queryParam.row);
        if (null != queryParam.sort && queryParam.sort != FansQueryParam.QuerySort.NONE) {
            sb.append("&sort=").append(queryParam.sort.value);
        }
        if (!TextUtils.isEmpty(queryParam.fans)) {
            sb.append("&fans=" + queryParam.fans);
        }
        if (!TextUtils.isEmpty(queryParam.search)) {
            sb.append("&search=" + queryParam.search);
        }
        if (!TextUtils.isEmpty(queryParam.uid)) {
            sb.append("&uid=" + queryParam.uid);
        }
        return getService(UserApi.class).getFansList(sb.toString());
    }

    /**
     * 团购收益
     */
    public static Observable<ResponseDataObject<EarningEnity>> getTeamEarning() {
        return getService(UserApi.class).getTeamEarning();
    }

    /**
     * 自购收益
     */
    public static Observable<ResponseDataObject<SelfEarningEntity>> getSelfEarning() {
        return getService(UserApi.class).getSlefEarning();
    }

    /**
     * 乐淘收入榜
     *
     * @param type
     * @return
     */
    public static Observable<ResponseDataArray<XltIncomeEntity>> getXltIncome(String type) {
        return getService(UserApi.class).getXltIncomes(type);
    }

    /**
     * 成员贡献榜——总预估佣金
     *
     * @param queryParam
     * @return
     */
    public static Observable<ResponseDataArray<TeamIncomeEntity>> getTeamIncomeTotal(IncomeQueryParam queryParam) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.TEAM_INCOME_TOTAL).append("?");
        sb.append("page=" + queryParam.page);
        sb.append("&row=" + queryParam.row);
        if (!TextUtils.isEmpty(queryParam.relation)) {
            sb.append("&relation=").append(queryParam.relation);
        }
        return getService(UserApi.class).getTeamIncomeTotal(sb.toString());
    }

    /**
     * 成员贡献榜——本月预估佣金
     *
     * @param queryParam
     * @return
     */
    public static Observable<ResponseDataArray<TeamIncomeEntity>> getTeamIncomeMonth(IncomeQueryParam queryParam) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.TEAM_INCOME_MONTH).append("?");
        sb.append("page=" + queryParam.page);
        sb.append("&row=" + queryParam.row);
        if (!TextUtils.isEmpty(queryParam.relation)) {
            sb.append("&relation=").append(queryParam.relation);
        }
        return getService(UserApi.class).getTeamIncomeTotal(sb.toString());
    }

    /**
     * 成员贡献榜——7日拉新
     */
    public static Observable<ResponseDataArray<TeamIncomeEntity>> getTeamIncomeWeek(IncomeQueryParam queryParam) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.TEAM_INCOME_WEEK).append("?");
        sb.append("page=" + queryParam.page);
        sb.append("&row=" + queryParam.row);
        if (null != queryParam.sort && queryParam.sort != IncomeQueryParam.Sort.NONE) {
            sb.append("&sort=").append(queryParam.sort.value);
        }
        if (!TextUtils.isEmpty(queryParam.relation)) {
            sb.append("&relation=").append(queryParam.relation);
        }
        return getService(UserApi.class).getTeamIncomeTotal(sb.toString());
    }

    /**
     * 自有消息推送列表
     *
     * @param userId
     * @return
     */
    public static Observable<ResponseDataArray<PushMessageEntity>> getActionList(String userId) {
        return getService(UserApi.class).getActionList(userId);
    }

    /**
     * 关闭某个消息推送
     *
     * @param userId
     * @return
     */
    public static Observable<ResponseDataObject<Object>> banPushMessage(String id, String userId, String enable) {
        return getService(UserApi.class).banPushMessage(id, userId, enable);
    }

    /**
     * 获取邀请码信息
     *
     * @return
     */
    public static Observable<ResponseDataObject<InviteCodeEntity>> getInviteCode() {
        return getService(UserApi.class).getInviteCode();
    }

    /**
     * 获取推荐人邀请码
     *
     * @return
     */
    public static Observable<ResponseDataObject<RecommendSuperior>> getRecommendCode() {
        return getService(UserApi.class).getRecommendCode();
    }

    /**
     * 设置邀请码
     *
     * @param inviteCode
     * @return
     */
    public static Observable<ResponseDataObject<Object>> setInviteCode(String inviteCode) {
        return getService(UserApi.class).setInviteCode(inviteCode);
    }

    /**
     * 检测邀请码是否被使用
     *
     * @param inviteCode
     * @return
     */
    public static Observable<ResponseDataObject<Object>> checkInviteCode(String inviteCode) {
        return getService(UserApi.class).checkInviteCode(inviteCode);
    }

    /**
     * 获取推荐未使用的邀请码
     *
     * @return
     */
    public static Observable<ResponseDataObject<List<String>>> getRandCode() {
        return getService(UserApi.class).getRandCode();
    }

    /**
     * 获取问题反馈列表
     *
     * @return
     */
    public static Observable<ResponseDataArray<FeedbackEntity>> getFeedbackList(int page) {
        return getService(UserApi.class).getFeedbackList(page, 10, "-itime");
    }

    /**
     * 获取问题反馈详情
     *
     * @param id
     * @return
     */
    public static Observable<ResponseDataObject<FeedbackEntity>> getFeedbackDetail(String id) {
        return getService(UserApi.class).getFeedbackDetail(id);
    }

    /**
     * 提交问题反馈列表
     *
     * @param phone
     * @param log_url
     * @param content
     * @param enclosure
     * @return
     */
    public static Observable<ResponseDataObject<Object>> sendFeedBack(String phone, String log_url, String content, String[] enclosure) {
        FeedbackBody feedbackBody = new FeedbackBody(phone, log_url, content, enclosure);
        return getService(UserApi.class).sendFeedBack(feedbackBody);
    }

    /**
     * 客服
     */
    public static Observable<ResponseDataObject<KefuEntity>> getConfigKefu() {
        return getService(UserApi.class).getConfigKefu();
    }

    /**
     * 检查提现金额是否需要签署协议
     *
     * @param amount
     * @return
     */
    public static Observable<ResponseDataObject<PigContract>> checkContract(float amount) {
        return getService(UserApi.class).checkContract(amount);
    }

    /**
     * 获取提醒
     *
     * @return
     */
    public static Observable<ResponseDataObject<AccountTipsEntity>> getAccountTips() {
        return getService(UserApi.class).getAccountTips();
    }


    /**
     * 保存用户水印信息
     *
     * @param watermark
     * @return
     */
    public static Observable<ResponseDataObject<Object>> saveUserWatermark(Watermark watermark) {
        return getService(UserApi.class).saveUserWatermark(watermark);
    }

    /**
     * 获取用户水印信息
     *
     * @return
     */
    public static Observable<ResponseDataObject<Watermark>> getUserWatermark() {
        return getService(UserApi.class).getUserWatermark();
    }


    public static Observable<ResponseDataArray<FeedUploadEntity>> uploadFiles(File file, String type) {
        String media_type = "";
        if (type.equals("images")) {
            media_type = "image/*";
        } else if (type.equals("zip")) {
            media_type = "multipart/form-data";
        } else if (type.equals("video")) {
            media_type = "video*";
        }
        MultipartBody uploadBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("save_type", type)
                .addFormDataPart("files", file.getName(), RequestBody.create(MediaType.parse(media_type), file))
                .build();
        return getService(UserApi.class).uploadMultipleFiles(uploadBody.part(0), uploadBody.part(1));
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @return
     */
    public static Observable<ResponseDataObject<Object>> logoutSendCode(String phone) {
        return getService(UserApi.class).logoutSendCode(phone);
    }

    /**
     * 撤销账号注销申请
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> logoutRevocation() {
        return getService(UserApi.class).logoutRevocation();
    }

    /**
     * 获取我的导师分享文章
     *
     * @return
     */
    public static Observable<ResponseDataArray<TutorShareContract>> getTutorShareList(int page) {
        return getService(UserApi.class).getTutorShareList(page, 50);
    }

    /**
     * 获取我自己发布的导师分享文章
     *
     * @return
     */
    public static Observable<ResponseDataArray<TutorShareContract>> getMeTutorShareList(String status, int page) {
        return getService(UserApi.class).getMeTutorShareList(page, 50, status);
    }

    /**
     * 创建导师分享文章
     *
     * @param logo
     * @param title
     * @param content
     * @return
     */
    public static Observable<ResponseDataObject<Object>> createTutorShare(String logo, String title, String content) {
        return getService(UserApi.class).createTutorShare(logo, title, content);
    }

    /**
     * 移动导师分享文章排序
     *
     * @param share_id
     * @param type
     * @return
     */
    public static Observable<ResponseDataObject<Object>> moveTutorShare(String share_id, String type) {
        return getService(UserApi.class).moveTutorShare(share_id, type);
    }

    /**
     * 设置导师分享文章状态
     *
     * @param share_id
     * @param status
     * @return
     */
    public static Observable<ResponseDataObject<Object>> changeTutorShareStatus(@Field("share_id") String share_id, @Field("status") String status) {
        return getService(UserApi.class).changeTutorShareStatus(share_id, status);
    }

    /**
     * 设置导师分享文章是否置顶
     *
     * @param share_id
     * @param status
     * @return
     */
    public static Observable<ResponseDataObject<Object>> changeTutorShareTop(String share_id, String status) {
        return getService(UserApi.class).changeTutorShareTop(share_id, status);
    }

    /*
     ************************************** 用户模块缓存 ****************************************
     */

    public static void verifyInviter(Activity activity) {
        if (TextUtils.isEmpty(UserClient.getUser().inviter)) {
            InvitePersonFragment.start(activity);
            activity.finish();
        }
    }


    /**
     * 保存登录用户L
     * 用户登录完成后调用本方法，会先删除之前的登录账号
     *
     * @param user
     * @return
     */
    @SuppressLint("CheckResult")
    public static void saveLoginUser(final UserEntity user) {
        PushAgent.getInstance(LContext.getContext()).addAlias(user._id, "xlt", (b, s) -> {
        });
//        if (!TextUtils.isEmpty(App.mApp.umengDeviceToken)) {
//            bindDevice(App.mApp.umengDeviceToken)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeOn(Schedulers.io())
//                    .subscribeWith(new Observer<ResponseDataObject<Object>>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
//
//                        }
//
//                        @Override
//                        public void onNext(ResponseDataObject<Object> objectResponseDataObject) {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//                    })
//        }
        SPUtil.setBoolean(IS_LOGIN, true);
        updateUser(user);
    }

    /**
     * 更新当前登录用户
     *
     * @param user
     * @return
     */
    public static void updateUser(final UserEntity user) {
        EasyDB.with(UserEntity.class).insert(user);
    }

    /**
     * 更新当前登录用户
     *
     * @return
     */
    public static void deleteUser() {
        EasyDB.with(UserEntity.class).delete();
    }

    /**
     * 获取用户并监听用户变化
     *
     * @return
     */
    public static Observable<List<UserEntity>> getUserWithChange() {
        return EasyDB.with(UserEntity.class).queryWithObserverable();
    }


    /**
     * 获取当前登录用户
     *
     * @return
     */
    public static UserEntity getUser() {
        if (null == loginUser) {
            List<UserEntity> users = EasyDB.with(UserEntity.class).query();
            if (!users.isEmpty()) {
                loginUser = users.get(0);
            }
        }
        return loginUser;
    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    public static boolean isLogin() {
        if (null == loginUser) {
            return false;
        }
        return true;
    }

    /**
     * 是否vip
     *
     * @return
     */
    public static boolean isVip() {
        if (null == loginUser) {
            return false;
        }
        return loginUser.level > 1;
    }

    /**
     * vip等级
     *
     * @return
     */
    public static String vipLevel() {
        if (null == loginUser) {
            return "";
        }
        return CommonUtil.getVipText(loginUser.level);
    }

    /**
     * vip等级
     *
     * @return
     */
    public static String vipTagLevel() {
        if (null == loginUser) {
            return "";
        }
        return CommonUtil.getVipTagText(loginUser.level);
    }

    /**
     * 邀请码
     *
     * @return
     */
    public static String inviteCode() {
        if (null == loginUser) {
            return "";
        }
        return loginUser.invite_link_code == null ? "" : loginUser.invite_link_code;
    }

    /**
     * 获取用户登录token
     *
     * @return
     */
    public static String getToken() {
        UserEntity user = getUser();
        return (null != user && null != user.token) ? user.token : "";
    }

    /**
     * 退出登录
     */
    public static void loginOut() {
        SPUtils.getInstance().put(Constant.PREF.IS_NEW_DATA, "");
        SPUtils.getInstance().put(Constant.PREF.IS_NEW, "");
        SPUtils.getInstance().put(Constant.PREF.IS_FREE, "");
        EasyDB.with(UserEntity.class).delete();
        loginUser = null;
    }

    public static boolean hasWatchVideo() {
        return SPUtil.getBoolean(WATCH_VIDEO, false);
    }

    public static void watchVideo() {
        SPUtil.setBoolean(WATCH_VIDEO, true);
    }

    public static void showGuide() {
        SPUtil.setBoolean(SHOW_GUIDE, true);
    }

    public static boolean hasShowGuide() {
        return SPUtil.getBoolean(SHOW_GUIDE, false);
    }

    //新手任务汇报
    public static boolean hasNewTaskReport(String taskName) {
        return SPUtil.getBoolean(taskName, false);
    }

    public static void setNewTaskReport(String taskName) {
        SPUtil.setBoolean(taskName, true);
    }

    /**
     * 新手0元购
     *
     * @return
     */
    public static boolean hasShowNewUserGuide() {
        UserEntity user = UserClient.getUser();
        if (null == user) {
            return true;
        }

        String key = user._id + NEW_USER_GUIDE;
        String value = SPUtil.getString(key);
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        String day = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (TextUtils.equals(day, value)) {
            return true;
        }
        return false;
    }

    public static void setShowNewUserGuide() {
        UserEntity user = UserClient.getUser();
        if (null == user) {
            return;
        }
        String key = user._id + NEW_USER_GUIDE;
        String day = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SPUtil.setString(key, day);
    }

}
