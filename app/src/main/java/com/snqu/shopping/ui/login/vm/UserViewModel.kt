package com.snqu.shopping.ui.login.vm

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.common.viewmodel.BaseAndroidViewModel
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.base.*
import com.snqu.shopping.data.goods.entity.GoodsEntity
import com.snqu.shopping.data.home.HomeClient
import com.snqu.shopping.data.home.entity.AdvertistEntity
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.*
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import retrofit2.http.Field
import java.io.File
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * desc:
 * time: 2019/8/26
 * @author 银进
 */
class UserViewModel : BaseAndroidViewModel {
    val dataResult = MutableLiveData<NetReqResult>()
    var inviterInfoDisposable: Disposable? = null

    constructor(application: Application) : super(application)

    /**
     * 登录--校验验证码=》》》变更
     */
    fun doVerifyCode(phone: String, code: String) {
        executeNoMapHttp(UserClient.verifyCode(phone, code), object : BaseResponseObserver<ResponseDataObject<UserEntity>>() {
            override fun onSuccess(value: ResponseDataObject<UserEntity>) {
                dataResult.value = NetReqResult(ApiHost.VERIFY_CODE, "", true, value.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.VERIFY_CODE, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 发送验证码
     */
    fun doLoginCode(phone: String) {
        executeNoMapHttp(UserClient.doLoginCode(phone), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.LOGIN_CODE, "验证码已发送，请注意查收", true)
            }

            override fun onError(e: HttpResponseException?) {
                if (e?.resultCode == 425) {
                    dataResult.value = NetReqResult(ApiHost.LOGIN_CODE, "验证码已发送，请注意查收", true)
                } else {
                    dataResult.value = NetReqResult(ApiHost.LOGIN_CODE, e?.alert, false)
                }
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 新版发送验证码
     */
    fun doNewLoginCode(phone: String) {
        executeNoMapHttp(UserClient.doNewLoginCode(phone), object : BaseResponseObserver<ResponseDataObject<InvitedEntity>>() {
            override fun onSuccess(value: ResponseDataObject<InvitedEntity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.SEND_BIND_CODE, "验证码已发送，请注意查收", true, value.data)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.SEND_BIND_CODE, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 微信登录
     */
    fun doLoginWX(code: String) {
        executeNoMapHttp(UserClient.doLoginWX(code), object : BaseResponseObserver<ResponseDataObject<UserEntity>>() {
            override fun onSuccess(value: ResponseDataObject<UserEntity>?) {
                if (value?.isSuccessful == true) {
                    if (value.data?.token != null && value.data?.invited == "1") {
                        UserClient.saveLoginUser(value.data)
                        dataResult.value = NetReqResult(ApiHost.LOGIN_WX, "登陆成功", true, value.data)
                    } else {
                        dataResult.value = NetReqResult(ApiHost.LOGIN_WX, "需要绑定邀请码", true, value.data)
                    }
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.LOGIN_WX, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 绑定登录
     */
    fun doBindWX(code: String, token: String) {
        executeNoMapHttp(UserClient.doBindWX(code, token), object : BaseResponseObserver<ResponseDataObject<UserEntity>>() {
            override fun onSuccess(value: ResponseDataObject<UserEntity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.BIND_WX, "", true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.BIND_WX, value?.message, true, null)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.BIND_WX, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 验证码登陆
     */
    fun doNewCodeLogin(phone: String, code: String, invite_code: String = "") {
        executeNoMapHttp(UserClient.doNewCodeLogin(phone, code, invite_code), object : BaseResponseObserver<ResponseDataObject<UserEntity>>() {
            override fun onSuccess(value: ResponseDataObject<UserEntity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.NEW_CODE_LOGIN, "登陆成功", true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.NEW_CODE_LOGIN, value?.message, false)
                }
            }

            override fun onError(e: HttpResponseException?) {
                // 验证码超时
                if (e?.resultCode == 400001) {
                    dataResult.value = NetReqResult(ApiHost.NEW_CODE_LOGIN, e.alert, false, e.resultCode)
                } else {
                    dataResult.value = NetReqResult(ApiHost.NEW_CODE_LOGIN, e?.alert, false)
                }
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 微信登陆绑定手机号
     */
    fun doWXCodeLogin(phone: String, code: String, sid: String, invite_code: String = "") {
        executeNoMapHttp(UserClient.doWXCodeLogin(phone, code, sid, invite_code), object : BaseResponseObserver<ResponseDataObject<UserEntity>>() {
            override fun onSuccess(value: ResponseDataObject<UserEntity>?) {
                if (value?.data != null) {
//                    UserClient.saveLoginUser(value.data)
                    dataResult.value = NetReqResult(ApiHost.WX_LOGIN_BIND_PHONE, "登陆成功", true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.WX_LOGIN_BIND_PHONE, value?.message, false)
                }

            }

            override fun onError(e: HttpResponseException?) {
                // 验证码超时
                if (e?.resultCode == 400001) {
                    dataResult.value = NetReqResult(ApiHost.WX_LOGIN_BIND_PHONE, e.alert, false, e.resultCode)
                } else {
                    dataResult.value = NetReqResult(ApiHost.WX_LOGIN_BIND_PHONE, e?.alert, false)
                }
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 邀请绑定
     */
    fun doInviteCode(invite_code: String, recommed: Int) {
        executeNoMapHttp(UserClient.doInviteCode(invite_code, recommed), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.INVITE_CODE, "", true, value)
            }

            override fun onError(e: HttpResponseException) {
                dataResult.value = NetReqResult(ApiHost.INVITE_CODE, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    fun bindInvite(invite_code: String, uid: String) {
        executeNoMapHttp(UserClient.bindInvite(invite_code, uid), object : BaseResponseObserver<ResponseDataObject<UserEntity>>() {
            override fun onSuccess(value: ResponseDataObject<UserEntity>?) {
                dataResult.value = NetReqResult(ApiHost.BIND_INVITE, "", true, value?.data)
            }

            override fun onError(e: HttpResponseException) {
                dataResult.value = NetReqResult(ApiHost.BIND_INVITE, e.msg, false)
            }

            override fun onEnd() {
            }
        })
    }


    /**
     * 获取邀请人信息
     */
    @SuppressLint("AutoDispose")
    fun doInviterInfo(invite_code: String) {
        if (inviterInfoDisposable != null && inviterInfoDisposable?.isDisposed != true) {
            inviterInfoDisposable?.dispose()
        }
        UserClient.doInviterInfoNoAuth(invite_code)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<ResponseDataObject<InviterInfo>> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {
                        inviterInfoDisposable = d
                    }

                    override fun onNext(value: ResponseDataObject<InviterInfo>) {
                        if (value.isSuccessful && value.data != null) {
                            dataResult.value = NetReqResult(ApiHost.INVITER_INFO_NO_AUTH, "", true, value.data)
                        } else {
                            dataResult.value = NetReqResult(ApiHost.INVITER_INFO_NO_AUTH, value.message
                                    ?: "", false)
                        }
                    }

                    override fun onError(e: Throwable) {
                        val errorCause = if (e is UnknownHostException) {
                            HttpResponseException("无网络连接, 请重试", e)
                        } else if (e is SocketTimeoutException || e is ConnectException) {
                            HttpResponseException("网络连接超时, 请重试", e)
                        } else if (e is HttpResponseException) { //服务器错误
                            e
                        } else {
                            HttpResponseException("网络异常", e)
                        }
                        dataResult.value = NetReqResult(ApiHost.INVITER_INFO_NO_AUTH, errorCause.alert, false)
                    }
                })
    }


    /**
     * 验证码登陆
     */
    fun doUserInfo() {
        executeNoMapHttp(UserClient.doUserInfo(), object : BaseResponseObserver<ResponseDataObject<UserEntity>>() {
            override fun onSuccess(value: ResponseDataObject<UserEntity>?) {
                if (value?.isSuccessful == true && value.data != null) {
                    val userEntity = value.data
                    userEntity.token = UserClient.getToken()
                    UserClient.updateUser(userEntity)
                    dataResult.value = NetReqResult(ApiHost.USER_INFO, "", true, value.data)
                }
            }

            override fun onError(e: HttpResponseException?) {
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 登录--校验验证码
     */
    fun doInviterCode(phone: String, code: String, sid: String) {
        executeNoMapHttp(UserClient.findInviterCode(phone, code, sid), object : BaseResponseObserver<ResponseDataObject<UserEntity>>() {
            override fun onSuccess(value: ResponseDataObject<UserEntity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.FIND_INVITER_CODE, "", true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.FIND_INVITER_CODE, value?.message, true, null)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.FIND_INVITER_CODE, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 登出登陆
     */
    fun doLoginOut() {
        executeNoMapHttp(UserClient.doLoginOut(), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.LOGIN_OUT, "", true)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.LOGIN_OUT, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 原手机号发送验证码
     */
    fun doChangePhoneCode(phone: String) {
        executeNoMapHttp(UserClient.doChangePhoneCode(phone), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.CHANGE_PHONE_CODE, "验证码已发送，请注意查收", true)
            }

            override fun onError(e: HttpResponseException?) {
                if (e?.resultCode == 425) {
                    dataResult.value = NetReqResult(ApiHost.CHANGE_PHONE_CODE, "验证码已发送，请注意查收", true)
                } else {
                    dataResult.value = NetReqResult(ApiHost.CHANGE_PHONE_CODE, e?.alert, false)
                }
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 原手机号验证码验证
     */
    fun doChangePhoneVerifyCode(phone: String, code: String) {
        executeNoMapHttp(UserClient.doChangePhoneVerifyCode(phone, code), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.CHANGE_PHONE_VERIFY_CODE, "", true)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.CHANGE_PHONE_VERIFY_CODE, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 绑定支付宝
     */
    fun doBindAlipay(realName: String, alipay: String, code: String) {
        executeNoMapHttp(UserClient.doBindAlipay(realName, alipay, code), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.BIND_ALIPAY, "绑定成功", true)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.BIND_ALIPAY, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 获取支付宝信息
     */
    fun doAlipayInfo() {
        executeNoMapHttp(UserClient.doAlipayInfo(), object : BaseResponseObserver<ResponseDataObject<AlipayInfoEntity>>() {
            override fun onSuccess(value: ResponseDataObject<AlipayInfoEntity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.ALIPAY_INFO, "", true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.ALIPAY_INFO, value?.message, false)
                }

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.ALIPAY_INFO, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 注销账户
     */
    fun accountLogou(phone: String,
                     code: String,
                     reason: String) {
        executeNoMapHttp(UserClient.accountLogou(phone, code, reason), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                val tip = value?.data ?: ""
                dataResult.value = NetReqResult(ApiHost.ACCOUNT_LOGOUT, tip.toString(), true)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.ACCOUNT_LOGOUT, e?.msg, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 获取详情
     */
    fun getLogoutDetails() {
        executeNoMapHttp(UserClient.getLogoutDetails(), object : BaseResponseObserver<ResponseDataObject<AccountCancelEntity>>() {
            override fun onSuccess(value: ResponseDataObject<AccountCancelEntity>?) {
                dataResult.value = NetReqResult(ApiHost.ACCOUNT_LOGOUT_DETAILS, value?.message, true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.ACCOUNT_LOGOUT_DETAILS, e?.msg, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 我的余额 余额信息
     */
    fun doBalanceInfo() {
        executeNoMapHttp(UserClient.doBalanceInfo(), object : BaseResponseObserver<ResponseDataObject<BalanceInfoEntity>>() {
            override fun onSuccess(value: ResponseDataObject<BalanceInfoEntity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.BALANCE_INFO, "", true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.BALANCE_INFO, value?.message, false)
                }

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.BALANCE_INFO, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 获取用户时间区间收益报表
     */
    fun doAccountInfo(user_id: String) {
        executeNoMapHttp(UserClient.doAccountInfo(user_id), object : BaseResponseObserver<ResponseDataObject<AccountInfoEntity>>() {
            override fun onSuccess(value: ResponseDataObject<AccountInfoEntity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.ACCOUNT_INFO, "", true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.ACCOUNT_INFO, value?.message, false)
                }

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.ACCOUNT_INFO, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 更换手机号并重新登录
     */
    fun doChangePhoneBindVerifyCode(phone: String, code: String) {
        executeNoMapHttp(UserClient.doChangePhoneBindVerifyCode(phone, code), object : BaseResponseObserver<ResponseDataObject<UserEntity>>() {
            override fun onSuccess(value: ResponseDataObject<UserEntity>?) {
                if (value?.data != null) {
                    UserClient.deleteUser()
                    UserClient.saveLoginUser(value.data)
                    dataResult.value = NetReqResult(ApiHost.CHANGE_PHONE_BIND_VERIFY_CODE, "修改成功", true)
                } else {
                    dataResult.value = NetReqResult(ApiHost.CHANGE_PHONE_BIND_VERIFY_CODE, value?.message, false)
                }

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.CHANGE_PHONE_BIND_VERIFY_CODE, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }


    /**
     * 绑定支付宝前发送验证短信
     */
    fun doAlipayCode() {
        executeNoMapHttp(UserClient.doAlipayCode(), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.ALIPAY_CODE, "验证码已发送，请注意查收", true)
            }

            override fun onError(e: HttpResponseException?) {
                if (e?.resultCode == 425) {
                    dataResult.value = NetReqResult(ApiHost.ALIPAY_CODE, "验证码已发送，请注意查收", true)
                } else {
                    dataResult.value = NetReqResult(ApiHost.ALIPAY_CODE, e?.alert, false)
                }
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 我的余额 - 账户明细
     */
    fun doBalanceRecode(yearMonth: String, page: Int, pageSize: Int) {
        executeNoMapHttp(UserClient.doBalanceRecode(yearMonth, page, pageSize), object : BaseResponseObserver<ResponseDataArray<BalanceRecodeEntity>>() {
            override fun onSuccess(value: ResponseDataArray<BalanceRecodeEntity>?) {
                dataResult.value = NetReqResult(ApiHost.BALANCE_RECODE, "", true, value?.dataList
                        ?: arrayListOf<BalanceRecodeEntity>()).apply {
                    extra = "$yearMonth$$page"

                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.BALANCE_RECODE, e?.alert, false).apply {
                    extra = "$yearMonth$$page"
                }
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 我的余额页面-提现
     */
    fun doWithdraw(amount: Int, amount_useable: Long) {
        executeNoMapHttp(UserClient.doWithdraw(amount, amount_useable), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.BALANCE_WITHDRAW, "", true, value?.data)
                EventBus.getDefault().post(PushEvent(Constant.Event.WITHDRAWAL_SUCCESS))
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.BALANCE_WITHDRAW, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 我的足迹
     */
    fun doSelfFootGoods(page: Int, row: Int) {
        executeNoMapHttp(UserClient.doSelfFootGoods(page, row), object : BaseResponseObserver<ResponseDataObject<List<GoodsEntity>>>() {
            override fun onSuccess(value: ResponseDataObject<List<GoodsEntity>>?) {
                dataResult.value = NetReqResult(ApiHost.SELF_FOOT_GOODS, "", true, value
                        ?.data ?: arrayListOf<BalanceRecodeEntity>())
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.SELF_FOOT_GOODS, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 绑定设备
     */
    fun bindDevice(deviceToken: String) {
        executeNoMapHttp(UserClient.bindDevice(deviceToken), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
            }

            override fun onError(e: HttpResponseException?) {
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 个人中心广告
     */
    fun doAdList() {
        executeNoMapHttp(HomeClient.getAdList("7"), object : BaseResponseObserver<ResponseDataArray<AdvertistEntity>>() {
            override fun onSuccess(value: ResponseDataArray<AdvertistEntity>?) {
                dataResult.value = NetReqResult(ApiHost.AD_LIST, "", true, value?.dataList)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.AD_LIST, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 广告点击
     */
    fun adClick(id: String) {
        executeNoMapHttp(HomeClient.adClick(id), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>) {}

            override fun onError(e: HttpResponseException) {}

            override fun onEnd() {

            }
        })
    }

    /**
     * 个人中心专属推荐商品
     *
     * @param item_source
     */
    fun doRecommendGoods(page: Int, row: Int) {
        executeNoMapHttp(UserClient.doRecommendGoods(page, row), object : BaseResponseObserver<ResponseDataArray<GoodsEntity>>() {
            override fun onSuccess(value: ResponseDataArray<GoodsEntity>) {
                dataResult.value = NetReqResult(ApiHost.RECOMMEND_USER, null, true, value.dataList)
            }

            override fun onError(e: HttpResponseException) {
                dataResult.value = NetReqResult(ApiHost.RECOMMEND_USER, null, false, e)
            }

            override fun onEnd() {

            }
        })
    }

    /**
     * 我的团队
     */
    fun getUserFans() {
        executeNoMapHttp(UserClient.getUserFans(), object : BaseResponseObserver<ResponseDataObject<UserFansEntity>>() {
            override fun onSuccess(value: ResponseDataObject<UserFansEntity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.USER_FANS, null, true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.USER_FANS, value?.message, false, null)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.USER_FANS, e?.alert, false, e)
            }

            override fun onEnd() {
            }

        })
    }

    /**
     * 粉丝列表
     */
    fun getFansList(queryParam: FansQueryParam) {
        executeNoMapHttp(UserClient.getFansList(queryParam), object : BaseResponseObserver<ResponseDataArray<FansEntity>>() {
            override fun onSuccess(value: ResponseDataArray<FansEntity>?) {
                dataResult.value = NetReqResult(ApiHost.USER_FANS_LIST, null, true, value?.dataList)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.USER_FANS_LIST, e?.alert, false, e)
            }

            override fun onEnd() {

            }
        })

    }

    /**
     * 团队收益
     */
    fun getTeamEaring() {
        executeNoMapHttp(UserClient.getTeamEarning(), object : BaseResponseObserver<ResponseDataObject<EarningEnity>>() {
            override fun onSuccess(value: ResponseDataObject<EarningEnity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.EARNING_TEAM, null, true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.EARNING_TEAM, value?.message, false, null)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.EARNING_TEAM, e?.alert, false, e)
            }

            override fun onEnd() {

            }
        })
    }

    /**
     * 自购收益
     */
    fun getSelfEaring() {
        executeNoMapHttp(UserClient.getSelfEarning(), object : BaseResponseObserver<ResponseDataObject<SelfEarningEntity>>() {
            override fun onSuccess(value: ResponseDataObject<SelfEarningEntity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.EARNING_SELF, null, true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.EARNING_SELF, value?.message, false, null)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.EARNING_SELF, e?.alert, false, e)
            }

            override fun onEnd() {

            }
        })
    }


    /**
     * 乐桃收入榜
     */
    fun getXltIncome(type: String) {
        executeNoMapHttp(UserClient.getXltIncome(type), object : BaseResponseObserver<ResponseDataArray<XltIncomeEntity>>() {
            override fun onSuccess(value: ResponseDataArray<XltIncomeEntity>?) {
                var netReqResult = NetReqResult(ApiHost.XLT_INCOME, null, true, value?.dataList)
                netReqResult.extra = type
                dataResult.value = netReqResult
            }

            override fun onError(e: HttpResponseException?) {
                var netReqResult = NetReqResult(ApiHost.XLT_INCOME, e?.alert, false, e)
                netReqResult.extra = type
                dataResult.value = netReqResult
            }

            override fun onEnd() {

            }
        })
    }

    /**
     * 成员贡献榜——总预估佣金
     */
    fun getTeamIncomeTotal(queryParam: IncomeQueryParam) {
        executeNoMapHttp(UserClient.getTeamIncomeTotal(queryParam), object : BaseResponseObserver<ResponseDataArray<TeamIncomeEntity>>() {
            override fun onSuccess(value: ResponseDataArray<TeamIncomeEntity>?) {
                var netReqResult = NetReqResult(ApiHost.TEAM_INCOME_TOTAL, null, true, value)
                dataResult.value = netReqResult
            }

            override fun onError(e: HttpResponseException?) {
                var netReqResult = NetReqResult(ApiHost.TEAM_INCOME_TOTAL, e?.alert, false, e)
                dataResult.value = netReqResult
            }

            override fun onEnd() {

            }
        })
    }

    /**
     * 成员贡献榜——本月预估佣金
     */
    fun getTeamIncomeMonth(queryParam: IncomeQueryParam) {
        executeNoMapHttp(UserClient.getTeamIncomeMonth(queryParam), object : BaseResponseObserver<ResponseDataArray<TeamIncomeEntity>>() {
            override fun onSuccess(value: ResponseDataArray<TeamIncomeEntity>?) {
                var netReqResult = NetReqResult(ApiHost.TEAM_INCOME_MONTH, null, true, value)
                dataResult.value = netReqResult
            }

            override fun onError(e: HttpResponseException?) {
                var netReqResult = NetReqResult(ApiHost.TEAM_INCOME_MONTH, e?.alert, false, e)
                dataResult.value = netReqResult
            }

            override fun onEnd() {

            }
        })
    }

    /**
     * 成员贡献榜——7日拉新
     */
    fun getTeamIncomeWeek(queryParam: IncomeQueryParam) {
        executeNoMapHttp(UserClient.getTeamIncomeWeek(queryParam), object : BaseResponseObserver<ResponseDataArray<TeamIncomeEntity>>() {
            override fun onSuccess(value: ResponseDataArray<TeamIncomeEntity>?) {
                var netReqResult = NetReqResult(ApiHost.TEAM_INCOME_WEEK, null, true, value)
                dataResult.value = netReqResult
            }

            override fun onError(e: HttpResponseException?) {
                var netReqResult = NetReqResult(ApiHost.TEAM_INCOME_WEEK, e?.alert, false, e)
                dataResult.value = netReqResult
            }

            override fun onEnd() {

            }
        })
    }


    /**
     * 自有消息推送列表
     *
     * @param userId
     * @return
     */
    fun getActionList(userId: String) {
        executeNoMapHttp(UserClient.getActionList(userId), object : BaseResponseObserver<ResponseDataArray<PushMessageEntity>>() {
            override fun onSuccess(value: ResponseDataArray<PushMessageEntity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.UMENG_ACTION_LIST, null, true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.UMENG_ACTION_LIST, null, true, null)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.UMENG_ACTION_LIST, e?.alert, false, null)
            }

            override fun onEnd() {

            }
        })
    }

    /**
     * 关闭某个消息推送
     *
     * @param userId
     * @return
     */
    fun banPushMessage(id: String, userId: String, enable: String) {
        executeNoMapHttp(UserClient.banPushMessage(id, userId, enable), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
            }

            override fun onError(e: HttpResponseException?) {
            }

            override fun onEnd() {

            }
        })
    }

    fun getRecommendCode() {
        executeNoMapHttp(UserClient.getRecommendCode(), object : BaseResponseObserver<ResponseDataObject<RecommendSuperior>>() {
            override fun onSuccess(value: ResponseDataObject<RecommendSuperior>?) {
                dataResult.value = NetReqResult(ApiHost.GET_RECOMMEND_CODE, null, true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GET_RECOMMEND_CODE, e?.alert, false, null)
            }

            override fun onEnd() {
            }

        })
    }

    /**
     * 获取邀请码信息
     */
    fun getInviteCode() {
        executeNoMapHttp(UserClient.getInviteCode(), object : BaseResponseObserver<ResponseDataObject<InviteCodeEntity>>() {
            override fun onSuccess(value: ResponseDataObject<InviteCodeEntity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.GET_INVITE_CODE, null, true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.GET_INVITE_CODE, null, false, null)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GET_INVITE_CODE, e?.alert, false, null)
            }

            override fun onEnd() {

            }
        })
    }

    /**
     * 设置邀请码
     */
    fun setInviteCode(invite_code: String) {
        executeNoMapHttp(UserClient.setInviteCode(invite_code), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                if (value?.isSuccessful == true) {
                    dataResult.value = NetReqResult(ApiHost.SET_INVITE_CODE, value.data.toString(), true, null)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.SET_INVITE_CODE, e?.alert
                        ?: "当前暂时未拥有修改邀请码次数!", false, e?.resultCode)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     *  检测邀请码是否被使用
     */
    fun checkInviteCode(invite_code: String) {
        executeNoMapHttp(UserClient.checkInviteCode(invite_code), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                if (value?.isSuccessful == true) {
                    dataResult.value = NetReqResult(ApiHost.CHECK_INVITE_CODE, value.data?.toString(), true, null)
                }
            }

            override fun onError(e: HttpResponseException?) {
                if (e?.resultCode == 400) {
                    dataResult.value = NetReqResult(ApiHost.CHECK_INVITE_CODE, e?.alert, false, e.resultCode)
                } else {
                    dataResult.value = NetReqResult(ApiHost.CHECK_INVITE_CODE, e?.alert, false, null)
                }
            }

            override fun onEnd() {
            }
        })
    }

    /**
     *获取推荐未使用的邀请码
     */
    fun getRandCode() {
        executeNoMapHttp(UserClient.getRandCode(), object : BaseResponseObserver<ResponseDataObject<List<String>>>() {
            override fun onSuccess(value: ResponseDataObject<List<String>>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.GET_RAND_CODE, "", true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.GET_RAND_CODE, "获取邀请码失败，请重试", false, null)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GET_RAND_CODE, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 设置给下级显示的微信id
     */
    fun setWechatId(id: String) {
        executeNoMapHttp(UserClient.setWechatId(id), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.SET_WECHAT_ID, value.data.toString(), true, null)
                } else {
                    dataResult.value = NetReqResult(ApiHost.SET_WECHAT_ID, value?.message, false, null)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.SET_WECHAT_ID, e?.alert, false, null)
            }

            override fun onEnd() {

            }
        })
    }

    fun getFeedbackList(page: Int) {
        executeNoMapHttp(UserClient.getFeedbackList(page), object : BaseResponseObserver<ResponseDataArray<FeedbackEntity>>() {
            override fun onSuccess(value: ResponseDataArray<FeedbackEntity>?) {
                dataResult.value = NetReqResult(ApiHost.GET_FEEDBACK_LIST, null, true, value)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GET_FEEDBACK_LIST, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    fun getAccountTips() {
        executeNoMapHttp(UserClient.getAccountTips(), object : BaseResponseObserver<ResponseDataObject<AccountTipsEntity>>() {
            override fun onSuccess(value: ResponseDataObject<AccountTipsEntity>?) {
                dataResult.value = NetReqResult(ApiHost.GET_ACCOUNT_TIPS, null, true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GET_ACCOUNT_TIPS, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    @SuppressLint("AutoDispose")
    fun getFeedbackDetail(id: String) {
        executeNoMapHttp(UserClient.getFeedbackDetail(id), object : BaseResponseObserver<ResponseDataObject<FeedbackEntity>>() {
            override fun onSuccess(value: ResponseDataObject<FeedbackEntity>?) {
                dataResult.value = NetReqResult(ApiHost.GET_FEEDBACK_DETAIL, null, true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GET_FEEDBACK_DETAIL, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    @SuppressLint("AutoDispose")
    fun sendFeedback(phone: String, content: String, logPath: String, pathList: List<String>) {
        executeNoMapHttp(UserClient.sendFeedBack(phone, logPath, content, pathList.toTypedArray()), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.POST_SEND_FEEDBACK, null, true, null)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.POST_SEND_FEEDBACK, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    @SuppressLint("AutoDispose")
    fun getConfigKefu() {
        executeNoMapHttp(UserClient.getConfigKefu(), object : BaseResponseObserver<ResponseDataObject<KefuEntity>>() {
            override fun onSuccess(value: ResponseDataObject<KefuEntity>?) {
                dataResult.value = NetReqResult(ApiHost.GET_CONFIG_KEFU, null, true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GET_CONFIG_KEFU, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 保存用户水印信息
     */
    fun saveUserWatermark(watermark: Watermark) {
        executeNoMapHttp(UserClient.saveUserWatermark(watermark), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                if (value?.code == 0) {
                    dataResult.value = NetReqResult(ApiHost.SAVE_USER_WATERMARK, value.message, true, null)
                }
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.SAVE_USER_WATERMARK, e?.alert, false, null)
            }

            override fun onEnd() {
            }

        })
    }


    /**
     * 获取用户水印信息
     */
    fun getUserWatermark() {
        Constant.water_name = ""
        if (UserClient.isLogin()) {
            executeNoMapHttp(UserClient.getUserWatermark(), object : BaseResponseObserver<ResponseDataObject<Watermark>>() {
                override fun onSuccess(value: ResponseDataObject<Watermark>?) {
                    dataResult.value = NetReqResult(ApiHost.GET_USER_WATERMARK, null, true, value?.data)
                }

                override fun onError(e: HttpResponseException?) {
                    dataResult.value = NetReqResult(ApiHost.GET_USER_WATERMARK, e?.alert, false, null)
                }

                override fun onEnd() {
                }
            })
        }
    }

    fun uploadFiles(file: File, type: String) {
        executeNoMapHttp(UserClient.uploadFiles(file, type), object : BaseResponseObserver<ResponseDataArray<FeedUploadEntity>>() {
            override fun onSuccess(value: ResponseDataArray<FeedUploadEntity>?) {
                dataResult.value = NetReqResult(type, null, true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(type, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    fun checkContract(amount: Float) {
        executeNoMapHttp(UserClient.checkContract(amount), object : BaseResponseObserver<ResponseDataObject<PigContract>>() {
            override fun onSuccess(value: ResponseDataObject<PigContract>?) {
                dataResult.value = NetReqResult(ApiHost.GET_CHECK_CONTRACT, null, true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GET_CHECK_CONTRACT, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    fun checkContract() {
        executeNoMapHttp(UserClient.checkContract(0F), object : BaseResponseObserver<ResponseDataObject<PigContract>>() {
            override fun onSuccess(value: ResponseDataObject<PigContract>?) {
                dataResult.value = NetReqResult("Tag_Tip", null, true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult("Tag_Tip", e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 发送注销验证码
     */
    fun logoutSendCode(phone: String) {
        executeNoMapHttp(UserClient.logoutSendCode(phone), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.LOGOUT_SENDCODE, value?.message, true, value)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.LOGOUT_SENDCODE, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 撤销账号注销申请
     */
    fun logoutRevocation() {
        executeNoMapHttp(UserClient.logoutRevocation(), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.LOGOUT_REVOCATION, "撤销注销成功", true, value)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.LOGOUT_REVOCATION, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    fun getTutorShareList(page: Int) {
        executeNoMapHttp(UserClient.getTutorShareList(page), object : BaseResponseObserver<ResponseDataArray<TutorShareContract>>() {
            override fun onSuccess(value: ResponseDataArray<TutorShareContract>?) {
                dataResult.value = NetReqResult(ApiHost.TUTOR_SHARE_LIST, null, true, value)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.TUTOR_SHARE_LIST, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 获取我自己发布的导师分享文章
     */
    fun getMeTutorShareList(status: String, page: Int) {
        executeNoMapHttp(UserClient.getMeTutorShareList(status, page), object : BaseResponseObserver<ResponseDataArray<TutorShareContract>>() {
            override fun onSuccess(value: ResponseDataArray<TutorShareContract>?) {
                dataResult.value = NetReqResult(ApiHost.TUTOR_SHARE_ME_LIST, null, true, value)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.TUTOR_SHARE_ME_LIST, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 创建导师分享文章
     */
    fun createTutorShare(logo: String, title: String, content: String) {
        executeNoMapHttp(UserClient.createTutorShare(logo, title, content), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
//                dataResult.value = NetReqResult(ApiHost.TUTOR_SHARE_LIST, null, true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
//                dataResult.value = NetReqResult(ApiHost.TUTOR_SHARE_LIST, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 移动导师分享文章排序
     */
    fun moveTutorShare(share_id: String, type: String) {
        executeNoMapHttp(UserClient.moveTutorShare(share_id, type), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.TUTOR_SHARE_MOVE, null, true, null)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.TUTOR_SHARE_MOVE, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 设置导师分享文章状态
     */
    fun changeTutorShareStatus(share_id: String, status: String) {
        executeNoMapHttp(UserClient.changeTutorShareStatus(share_id, status), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.TUTOR_SHARE_SET_STATUS, status, true, null)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.TUTOR_SHARE_SET_STATUS, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 设置导师分享文章是否置顶
     */
    fun changeTutorShareTop(share_id: String, type: String) {
        executeNoMapHttp(UserClient.changeTutorShareTop(share_id, type), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.TUTOR_SHARE_TOP, type, true, null)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.TUTOR_SHARE_TOP, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

}