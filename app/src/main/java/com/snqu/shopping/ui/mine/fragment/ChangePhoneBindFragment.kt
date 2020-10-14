package com.snqu.shopping.ui.mine.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.os.KeyboardUtils
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.change_phone_fragment.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * desc:
 * time: 2019/8/22
 * @author 银进
 */
class ChangePhoneBindFragment : SimpleFrag() {
    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    override fun getLayoutId() = R.layout.change_phone_bind_fragment

    @SuppressLint("CheckResult", "SetTextI18n")
    override fun init(savedInstanceState: Bundle?) {
        KeyboardUtils.showSoftInput(activity)
        titleBar.visibility = View.GONE
        StatusBar.setStatusBar(activity, true)
        userViewModel.dataResult.observe(this, Observer {
            if (it?.tag == ApiHost.LOGIN_CODE) {
                if (it.successful) {
                    ChangePhoneBindMsgCodeFragment.start(activity, et_phone.text.toString())
                }
                showToastShort(it.message)
            }
        })
        addAction(Constant.Event.CHANGE_PHONE_SUCCESS)
        initView()

    }

    /**
     * 初始化View
     */
    private fun initView() {
        et_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                if (charSequence == null || charSequence.isEmpty()) {
                    img_clear.visibility = View.GONE
                    return
                } else {
                    img_clear.visibility = View.VISIBLE
                }

                val stringBuilder = StringBuilder()
                for (i in 0 until charSequence.length) {
                    if (i != 3 && i != 8 && charSequence[i] == ' ') {
                        continue
                    } else {
                        stringBuilder.append(charSequence[i])
                        if ((stringBuilder.length == 4 || stringBuilder.length == 9) && stringBuilder[stringBuilder.length - 1] != ' ') {
                            stringBuilder.insert(stringBuilder.length - 1, ' ')
                        }
                    }
                }
                if (stringBuilder.toString() != charSequence.toString()) {
                    var index = start + 1
                    if (stringBuilder[start] == ' ') {
                        if (before == 0) {
                            index++
                        } else {
                            index--
                        }
                    } else {
                        if (before == 1) {
                            index--
                        }
                    }
                    et_phone.setText(stringBuilder.toString())
                    et_phone.setSelection(index)
                }
                tv_send_msg_code.isEnabled = stringBuilder.length == 13
            }
        })
        img_clear.onClick {
            et_phone.setText("")
            tv_send_msg_code.isEnabled = false
        }
        img_back.onClick {
            finish()
        }

        tv_send_msg_code.onClick {
            val phone = et_phone.text.toString().replace(" ", "")
            if (phone.matches(Regex(Constant.Match.PHONE_MATCH))) {
                userViewModel.doLoginCode(phone)
            } else {
                showToastShort("手机号格式不正确")
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventLogin(event: PushEvent?) {
        if (event?.action == Constant.Event.CHANGE_PHONE_SUCCESS) {
            finish()
        }
    }

    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("",
                    ChangePhoneBindFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }
}