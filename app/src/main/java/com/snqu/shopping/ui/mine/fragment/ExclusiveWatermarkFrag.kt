package com.snqu.shopping.ui.mine.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ToastUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.ui.LoadingStatusView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.user.entity.Watermark
import com.snqu.shopping.ui.goods.fragment.ShareFragment
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.util.ext.onClick
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.exclusive_watermark_fragment.*
import java.io.File


class ExclusiveWatermarkFrag : SimpleFrag() {

    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    private var mLoadingDialog: LoadingDialog? = null

    private var watermark: Watermark? = null

    override fun getLayoutId(): Int = R.layout.exclusive_watermark_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)

        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.SAVE_USER_WATERMARK -> {
                    closeLoadDialog()
                    if (it.successful) {
                        if (cb.isChecked) {
                            ToastUtils.showShort("设置成功，快去分享商品图片吧")
//                            SPUtils.getInstance().put(PREF_NAME, edit_watermark.text.toString())
                        }
                    } else {
                        ToastUtils.showShort(it.message)
                    }
                }
                ApiHost.GET_USER_WATERMARK -> {
                    if (it.successful) {
                        if (it.data != null) {
                            rl_content.visibility = View.VISIBLE
                            statusView.visibility = View.GONE
                            watermark = it.data as Watermark
                            if (watermark != null) {
                                // 表示第一次进入
                                if (TextUtils.isEmpty(watermark?._id) && TextUtils.isEmpty(watermark?.watermark)) {
                                    tv_save.visibility = View.VISIBLE
                                    ll_img.visibility = View.VISIBLE
                                    cb.isChecked = true
                                    setWaterMark("")
                                } else {
                                    if (watermark?.enabled == 1) {
//                                        if (!TextUtils.isEmpty(watermark?.watermark)) {
//                                            SPUtils.getInstance().put(PREF_NAME, watermark?.watermark)
//                                        }
                                        tv_save.visibility = View.VISIBLE
                                        ll_img.visibility = View.VISIBLE
                                        ll_img.post {
                                            if (TextUtils.isEmpty(watermark?.watermark)) {
                                                setWaterMark("")
                                            } else {
                                                edit_watermark.setText(watermark?.watermark)
                                            }
                                            cb.isChecked = watermark?.enabled == 1
                                        }
                                    } else {
                                        tv_save.visibility = View.GONE
                                        ll_img.visibility = View.GONE
                                    }
                                }
                            }
                        } else {
                            statusView.setStatus(LoadingStatusView.Status.EMPTY)
                        }
                    } else {
                        statusView?.apply {
                            setStatus(LoadingStatusView.Status.FAIL)
                            setOnBtnClickListener {
                                statusView.setStatus(LoadingStatusView.Status.LOADING)
                                userViewModel.getUserWatermark()
                            }
                        }
                    }
                }
            }
        })


        statusView.setStatus(LoadingStatusView.Status.LOADING)
        userViewModel.getUserWatermark()

        cb.onClick {
            cb.isChecked = !cb.isChecked
            if (cb.isChecked) {
                ll_img.visibility = View.VISIBLE
                tv_save.visibility = View.VISIBLE
                if (!TextUtils.isEmpty(watermark?.watermark)) {
                    edit_watermark.setText(watermark?.watermark!!)
                } else {
                    setWaterMark("")
                }
            } else {
                ll_img.visibility = View.GONE
                tv_save.visibility = View.GONE
                val watermark = Watermark()
                watermark.watermark = edit_watermark.text.toString()
                watermark.enabled = 0
                userViewModel.saveUserWatermark(watermark)
            }

        }

        tv_save.onClick {
            val watermark = Watermark()
            watermark.watermark = edit_watermark.text.toString()
            if (cb.isChecked) {
                watermark.enabled = 1
            } else {
                watermark.enabled = 0
            }
            userViewModel.saveUserWatermark(watermark)
            showLoadingDialog("正在保存中...")
        }

        edit_watermark.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    setWaterMark(s.toString())
                    tv_save.isEnabled = s.isNotEmpty()
                }
            }
        })

        watermark_img.onClick {
            watermark_img.post {
                val bitmap = ImageUtils.view2Bitmap(watermark_img)
                val fileDir = File(Environment.getExternalStorageDirectory(), Constant.SD_DIR)
                if (!fileDir.exists()) {
                    fileDir.mkdirs()
                }
                val file = File(fileDir, "warermark_img")
                val save = ImageUtils.save(bitmap, file, Bitmap.CompressFormat.PNG)
                if (save) {
                    ImageWatermarkFragment.start(activity, file.path)
                }
            }
        }

    }

    fun setWaterMark(content: String) {
        watermark_img.setImageResource(R.drawable.watermark_bg)
        watermark_img.post {
            val bitmap = ImageUtils.view2Bitmap(watermark_img)
            val newBitmap = ShareFragment.setWaterMark(resources, bitmap, content)
            if (newBitmap != null) {
                watermark_img.setImageBitmap(newBitmap)
            } else {
                watermark_img.setImageBitmap(bitmap)
            }
        }
    }

    fun showLoadingDialog(content: String?) {
        mLoadingDialog = LoadingDialog.showBackCancelableDialog(activity, content)
    }


    fun closeLoadDialog() {
        mLoadingDialog?.dismiss()
    }


    companion object {
        @JvmStatic
        fun start(context: Context?) {
            val fragParam = SimpleFragAct.SimpleFragParam("设置专属水印",
                    ExclusiveWatermarkFrag::class.java)
            SimpleFragAct.start(context, fragParam)
        }
    }

}