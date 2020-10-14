package com.snqu.shopping.ui.mine.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.ToastUtil
import com.android.util.file.FileUtil
import com.android.util.os.DeviceUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.KeyboardUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.goods.bean.DetailImageBean
import com.snqu.shopping.data.goods.entity.VideoBean
import com.snqu.shopping.data.user.entity.FeedUploadEntity
import com.snqu.shopping.data.user.entity.FeedbackEntity
import com.snqu.shopping.ui.goods.player.VideoImageActivity
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.mine.adapter.MyFeedbackAdapter
import com.snqu.shopping.util.ext.clickWithTrigger
import com.snqu.shopping.util.log.LogClient
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.my_feedback_fragment.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.regex.Pattern


class MyFeedbackFrag : SimpleFrag() {

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("我要反馈",
                    MyFeedbackFrag::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }

    override fun getLayoutId(): Int = R.layout.my_feedback_fragment

    private var mLoadingDialog: LoadingDialog? = null
    private val pathList = ArrayList<String>()
    private val uriList = ArrayList<String>()
    private var feedSize = 0
    private val feedList = ArrayList<String>()
    private var logPath = ""

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    private lateinit var mAdapter: MyFeedbackAdapter

    override fun init(savedInstanceState: Bundle?) {

        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        titleBar.setLeftBtnDrawable(R.drawable.feedback_close)

        mAdapter = MyFeedbackAdapter().apply {
            this.setOnItemClickListener { adapter, view, position ->
                val icon_pic_empty = view.findViewById<View>(R.id.icon_pic_empty)
                if (icon_pic_empty.visibility == View.VISIBLE) {
                    openFileChooser(position)
                } else {
                    val list = ArrayList<DetailImageBean>()
                    val url = uriList[position]
                    if (pathList[position].contains("mp4")) {
                        val detailImageBean = DetailImageBean(true, url, VideoBean(url, url))
                        list.add(detailImageBean)
                    } else {
                        val detailImageBean = DetailImageBean(false, url, null)
                        list.add(detailImageBean)
                    }
                    VideoImageActivity.start(activity, list)
                }
            }
            this.setOnItemChildClickListener { adapter, view, position ->
                if (view.id == R.id.icon_close) {
                    if (mAdapter.data[position].bitmap != null) {
                        pathList.remove(FileUtil.getImageAbsolutePath(activity, mAdapter.data[position].bitmap))
                        uriList.remove(mAdapter.data[position].bitmap.toString())
                    }
                    mAdapter.data.remove(mAdapter.data[position])
                    mAdapter.notifyItemRemoved(position)
                    if (mAdapter.data.size == 5) {
                        val count = mAdapter.data.filter {
                            it.bitmap != null
                        }.size
                        if (count == 5) {
                            mAdapter.data.add(FeedbackEntity())
                            mAdapter.setNewData(mAdapter.data)
                        }
                    }
                    image_tip.text = "${pathList.size}/6"
                }
            }
        }

        feedback_listview.apply {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 5)
            adapter = mAdapter
        }

        val feedbackEntity = FeedbackEntity()
        mAdapter.addData(feedbackEntity)

        my_feedback_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s.toString())) {
                    val str = s.toString()
                    my_feedback_input_tip.text = "${str.length}/500"
                } else {
                    my_feedback_input_tip.text = "0/500"
                }
            }
        })

        setScrollViewEvent()

        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.POST_SEND_FEEDBACK -> {
                    closeLoadDialog()
                    if (it.successful) {
                        EventBus.getDefault().post(PushEvent(Constant.Event.FEED_SUCCESS))
                        ToastUtil.show("提交成功")
                        finish()
                    } else {
                        ToastUtil.show(it.message)
                    }
                }
                "zip" -> {
                    if (it.successful) {
                        //如果图片列表为空，则直接提交，否则就提交图片以后再提交
                        val uploadEntityList = it.data as List<FeedUploadEntity>
                        if (uploadEntityList.isNotEmpty()) {
                            val uploadEntity = uploadEntityList[0];
                            logPath = uploadEntity.file
                            if (pathList.size == 0) {
                                userViewModel.sendFeedback(input_phone.text.toString(), my_feedback_input.text.toString(), logPath, pathList)
                            } else {
                                pathList.forEach { it ->
                                    val file = File(it)
                                    if (file.exists()) {
                                        if (it.contains(".jpg") || it.contains(".png") || it.contains(".jpeg")) {
                                            feedSize++
                                            userViewModel.uploadFiles(file, "images")
                                        } else if (it.contains(".mp4")) {
                                            feedSize++
                                            userViewModel.uploadFiles(file, "video")
                                        }
                                    }

                                }
                            }
                        } else {
                            closeLoadDialog()
                            ToastUtil.show("提交失败，请重试")
                        }
                    } else {
                        closeLoadDialog()
                        ToastUtil.show("提交失败，请重试")
                    }
                }
                "images", "video" -> {
                    feedSize--
                    if (it.successful) {
                        val uploadEntityList = it.data as List<FeedUploadEntity>
                        if (uploadEntityList.isNotEmpty()) {
                            val uploadEntity = uploadEntityList[0];
                            feedList.add(uploadEntity.file)
                        }
                    }
                    if (feedSize == 0) {
                        userViewModel.sendFeedback(input_phone.text.toString(), my_feedback_input.text.toString(), logPath, feedList)
                    }
                }
            }
        })

        rl_feedback.clickWithTrigger(1000) {
            if (my_feedback_input.text.length <= 3) {
                ToastUtil.show("反馈内容需大于3个字")
            } else {
                val phone = input_phone.text?.toString() ?: ""
                if (phone.length > 0) {
                    if (!checkPhoneNum(phone)) {
                        ToastUtil.show("手机号格式有误")
                        return@clickWithTrigger
                    }
                }
                showLoadingDialog("正在提交，请稍后...")
                val logFile = LogClient.getZipFile();
                userViewModel.uploadFiles(logFile, "zip")

            }
        }

    }

    private fun setScrollViewEvent() {
        my_feedback_input.setOnTouchListener { v, motionEvent ->
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

        input_phone.setOnTouchListener { v, motionEvent ->
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


        KeyboardUtils.registerSoftInputChangedListener(activity) { height ->
            scrollview.post {
                val lp = scrollview.layoutParams as LinearLayout.LayoutParams
                if (height > 0) {
                    lp.bottomMargin = DeviceUtil.dip2px(activity, 180F)
                } else {
                    lp.bottomMargin = 0
                }
                scrollview.layoutParams = lp
            }
        }
    }

    fun checkPhoneNum(num: String): Boolean {
        val regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(14[5-9])|(166)|(19[8,9])|)\\d{8}$"
        val p = Pattern.compile(regExp)
        val m = p.matcher(num)
        return m.matches()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var positionCode = -1
        when (requestCode) {
            0, 1, 2, 3, 4, 5 -> {
                positionCode = requestCode;
                try {
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        if (resultCode == Activity.RESULT_OK && data != null) {
                            val dataString = data.dataString
                            if (!TextUtils.isEmpty(dataString)) {
                                val uri = Uri.parse(dataString)
                                if (uri != null) {
                                    val file = File(FileUtil.getImageAbsolutePath(activity, uri))
                                    if (pathList.contains(file.absolutePath)) {
                                        ToastUtil.show("该图片/视频已添加")
                                    } else {
                                        if (file.exists()) {
                                            val length = (file.length() / (1024 * 1024)).toInt()
                                            if (uri.toString().contains("video") || uri.toString().contains(".mp4")) {
                                                if (length > 100) {
                                                    ToastUtil.show("选择文件大于100M")
                                                } else {
                                                    val data = mAdapter.data[positionCode]
                                                    data.bitmap = uri
                                                    mAdapter.setData(positionCode, data)
                                                    if (pathList.size <= 5) {
                                                        pathList.add(FileUtil.getImageAbsolutePath(activity, uri))
                                                        uriList.add(uri.toString())
                                                        image_tip.text = "${pathList.size}/6"
                                                        if (pathList.size < 6) {
                                                            val feedbackEntity = FeedbackEntity()
                                                            mAdapter.addData(feedbackEntity)
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (length > 3) {
                                                    ToastUtil.show("选择图片大于3M")
                                                    return
                                                } else {
                                                    val data = mAdapter.data[positionCode]
                                                    data.bitmap = uri
                                                    mAdapter.setData(positionCode, data)
                                                    if (pathList.size <= 5) {
                                                        pathList.add(FileUtil.getImageAbsolutePath(activity, uri))
                                                        uriList.add(uri.toString())
                                                        image_tip.text = "${pathList.size}/6"
                                                        if (pathList.size < 6) {
                                                            val feedbackEntity = FeedbackEntity()
                                                            mAdapter.addData(feedbackEntity)
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            ToastUtil.show("选择文件失败")
                                        }
                                    }

                                }
                            }
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    ToastUtil.show("选择文件失败")
                }
            }
        }
    }

    private fun openFileChooser(positon: Int) {
        var type = "image/*;video/*"
        try {
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            i.type = type
            startActivityForResult(Intent.createChooser(i, "选择图片/视频"), positon)
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.show("无法选择文件")
        }
    }

    fun showLoadingDialog(content: String?) {
        mLoadingDialog = LoadingDialog.showDialog(activity, content)
        mLoadingDialog?.setCancelable(false)
        mLoadingDialog?.setCancelableOnTouchOutside(false)
    }


    fun closeLoadDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog?.dismiss()
        }
    }


}