package com.snqu.shopping.ui.mine.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputFilter
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.ToastUtils
import com.jakewharton.rxbinding2.widget.RxTextView
import com.snqu.shopping.App
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.KefuEntity
import com.snqu.shopping.data.user.entity.AlipayInfoEntity
import com.snqu.shopping.ui.goods.util.JumpUtil
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.util.ext.clickWithTrigger
import com.snqu.shopping.util.ext.onClick
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.bind_alipay_fragment.*
import org.greenrobot.eventbus.EventBus
import java.util.regex.Pattern


/**
 * desc:
 * time: 2019/8/20
 * @author 银进
 */
class BindAlipayFragment : SimpleFrag() {

    private var mLoadingDialog: LoadingDialog? = null

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    private val countDownTimer by lazy {
        object : CountDownTimer(60000, 1000) {
            override fun onFinish() {
                tv_send_code.isEnabled = true
                tv_send_code.text = "获取验证码"
            }

            override fun onTick(millisUntilFinished: Long) {
                tv_send_code.isEnabled = false
                tv_send_code.text = "重新发送(${millisUntilFinished / 1000}s)"
            }

        }
    }
    private val alipayInfoEntity by lazy {
        arguments?.getParcelable<AlipayInfoEntity>(EXTRA_INFO)
    }
    private val isToFinish by lazy {
        arguments?.getBoolean(EXTRA_TO_FINISH) ?: false
    }

    override fun getLayoutId() = R.layout.bind_alipay_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        userViewModel.dataResult.observe(this, Observer {
            if (it?.tag == ApiHost.BIND_ALIPAY) {
                closeLoadDialog()
                showToastShort(it.message)
                if (it.successful) {
                    //绑定支付宝更新信息
                    val userEntity = UserClient.getUser()
                    userEntity.bind_alipay = "1"
                    UserClient.saveLoginUser(userEntity)
                    EventBus.getDefault().post(PushEvent(Constant.Event.BIND_ALIPAY_SUCCESS))
                    if (!isToFinish) {
                        BindAlipaySuccessFragment.start(activity)
                    }
                    finish()
                }
            } else if (it?.tag == ApiHost.ALIPAY_CODE) {
                closeLoadDialog()
                showToastShort(it.message)
                if (it.successful) {
                    countDownTimer.start()
                }

            } else if (it?.tag == ApiHost.GET_CONFIG_KEFU) {
                if (it.successful && it.data != null) {
                    val kefuEntity = it.data as KefuEntity
                    if (kefuEntity.wx != null) {
                        kefu_tip.text = "若有疑问，请联系客服微信：" + kefuEntity?.wx
                        kefu_layout.visibility = View.VISIBLE
                        item_copy.onClick {
                            try {
                                val clipboardManager = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clipData = ClipData.newPlainText(null, kefuEntity.wx)
                                clipboardManager.primaryClip = clipData
                                ToastUtil.show("已复制客服微信号：" + kefuEntity.wx)
                                if (App.mApp.iwxapi.isWXAppInstalled) {
                                    activity?.let { it1 -> JumpUtil.openWechat(it1) }
                                } else {
                                    ToastUtils.showShort("您的设备未安装微信客户端")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                ToastUtil.show("复制失败")
                            }
                        }
                    }

                }
            }
        })
        userViewModel.getConfigKefu()
        initView()
    }


    /**
     * 初始化View
     */
    @SuppressLint("CheckResult")
    private fun initView() {
        et_user_name.filters = arrayOf(
                InputFilter { source, _, _, _, _, _ ->
                    val speChat = "^[A-Za-z\\u4e00-\\u9fa5]+\$"
                    val pattern = Pattern.compile(speChat)
                    val matcher = pattern.matcher(source.toString())
                    if (matcher.matches()) {
                        null
                    } else
                        ""
                },
                InputFilter { source, _, _, _, _, _ ->
                    if (source == " ")
                        ""
                    else
                        null
                }
                ,
                InputFilter.LengthFilter(20)
        )
        RxTextView.textChanges(et_alipay_account)
                //                .debounce(200, TimeUnit.MILLISECONDS)
                .subscribe {
                    judge()
                }
        RxTextView.textChanges(et_user_name)
                .subscribe {
                    judge()
                }
        RxTextView.textChanges(et_msg_code)
                .subscribe {
                    judge()
                }
        tv_sure.clickWithTrigger(1000) {
            showLoadingDialog("加载中")
            userViewModel.doBindAlipay(et_user_name.text.toString(), et_alipay_account.text.toString(), et_msg_code.text.toString())
        }
        tv_send_code.clickWithTrigger(1000) {
            showLoadingDialog("加载中")
            userViewModel.doAlipayCode()
        }
        tv_phone.text = UserClient.getUser().phone_hide
        if (!isToFinish && null != alipayInfoEntity) {
            et_user_name.setText(alipayInfoEntity?.realname ?: "")
            et_alipay_account.setText(alipayInfoEntity?.account ?: "")
        }
    }

    private fun judge() {
        tv_sure.isEnabled = !(et_alipay_account.text.isNullOrBlank() || et_user_name.text.isNullOrBlank() || et_msg_code.text.length != 6)
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_INFO = "EXTRA_INFO"
        const val EXTRA_TO_FINISH = "EXTRA_TO_FINISH"

        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("绑定支付宝",
                    BindAlipayFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }

        fun start(context: Context?, isFromBalance: Boolean) {
            val fragParam = SimpleFragAct.SimpleFragParam("绑定支付宝",
                    BindAlipayFragment::class.java, Bundle().apply {
                putBoolean(EXTRA_TO_FINISH, isFromBalance)

            })
            SimpleFragAct.start(context, fragParam)
        }

        fun start(context: Context?, alipayInfoEntity: AlipayInfoEntity?, isToFinish: Boolean) {
            val fragParam = SimpleFragAct.SimpleFragParam("绑定支付宝",
                    BindAlipayFragment::class.java, Bundle().apply {
                putParcelable(EXTRA_INFO, alipayInfoEntity)
                putBoolean(EXTRA_TO_FINISH, isToFinish)

            })
            SimpleFragAct.start(context, fragParam)
        }
    }

    fun showLoadingDialog(content: String?) {
        mLoadingDialog = LoadingDialog.showBackCancelableDialog(activity, content)
    }


    fun closeLoadDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog?.dismiss()
        }
    }
}