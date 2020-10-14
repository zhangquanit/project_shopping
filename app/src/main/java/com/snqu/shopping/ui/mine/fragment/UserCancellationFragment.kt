package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.ui.mine.adapter.UserCancelAdapter
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.user_cancel_fragment.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 账号注销
 *
 */
class UserCancellationFragment : SimpleFrag() {


    private lateinit var adapter: UserCancelAdapter

    private var checkList: Map<String, Boolean>? = null

    override fun getLayoutId() = R.layout.user_cancel_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)

        addAction(Constant.Event.USER_CANCEL_CHECK)

        val dataList = arrayListOf<String>()
        dataList.add("安全隐私顾虑")
        dataList.add("多余账号")
        dataList.add("体验不好")
        dataList.add("返利比较低")
        dataList.add("商品找不到")
        dataList.add("其他")

        adapter = UserCancelAdapter(dataList)
        recy_list.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        recy_list.adapter = adapter

        edit_user_cancel.setOnTouchListener { v, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                //通知父控件不要干扰
                //                KeyboardUtils.showSoftInput(et_share_content)
                v.parent.requestDisallowInterceptTouchEvent(true)
            }
            if (motionEvent.action == MotionEvent.ACTION_MOVE) {
                //通知父控件不要干扰
                v.parent.requestDisallowInterceptTouchEvent(true)
            }
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        // 动态修改底部bottom的位置，避免被软键盘遮挡。
        KeyboardUtils.registerSoftInputChangedListener(activity) { height ->
            if (height > 0) {
//                recy_list.visibility = View.GONE
            } else {
//                recy_list.visibility = View.VISIBLE
            }
//            tv_set_invitecode.layoutParams = lp
        }

        edit_user_cancel.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s.toString())) {
                    val str = s.toString()
                    tv_tip.text = "${str.length}/200"
                } else {
                    tv_tip.text = "0/200"
                }
                checkList?.let {
                    if (it.containsKey("其他")) {
                        tv_next.isEnabled = !TextUtils.isEmpty(s.toString())
                    }
                }
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: PushEvent) {
        if (TextUtils.equals(event.action, Constant.Event.USER_CANCEL_CHECK)) {
            adapter.let {
                checkList = adapter.dataList.filter {
                    it.value
                }
                if (checkList == null || checkList?.size == 0) {
                    tv_next.isEnabled = false
                    edit_layout.visibility = View.INVISIBLE
                } else {
                    checkList?.let {
                        if (!it.containsKey("其他")) {
                            edit_layout.visibility = View.INVISIBLE
                            tv_next.isEnabled = true
                        } else {
                            edit_layout.visibility = View.VISIBLE
                            if (edit_user_cancel.length() > 0) {
                                tv_next.isEnabled = true
                            }
                        }
                    }
                }
            }
        }

        tv_next.onClick {
            val checkList = ArrayList(checkList?.keys)
            var other = ""
            if (edit_layout.visibility == View.VISIBLE) {
                other = edit_user_cancel.text.toString()
            }
            UserCancelDetailFrag.start(mContext, checkList, other)
        }
    }

    companion object {
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("账号注销",
                    UserCancellationFragment::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }

}