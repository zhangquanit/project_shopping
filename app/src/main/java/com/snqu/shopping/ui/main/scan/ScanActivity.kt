package com.snqu.shopping.ui.main.scan

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.android.util.ext.ToastUtil
import com.anroid.base.BaseActivity
import com.anroid.base.ui.StatusBar
import com.snqu.shopping.R
import com.snqu.shopping.data.base.ResponseDataObject
import com.snqu.shopping.data.goods.entity.GoodsEntity
import com.snqu.shopping.ui.goods.GoodsDetailActivity
import com.snqu.shopping.ui.main.frag.WebViewFrag
import com.snqu.shopping.ui.main.frag.search.SearchFrag
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel
import com.snqu.shopping.util.CommonUtil
import com.snqu.shopping.util.ext.onClick
import com.tbruyelle.rxpermissions2.RxPermissions
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.api.widget.Widget
import common.widget.dialog.EffectDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_scan.*


class ScanActivity : BaseActivity(), QRCodeView.Delegate {
    private var rxPermissions: RxPermissions? = null
    private var isOpenFlash = false
    private var isScanAlbum = false
    override fun getLayoutId(): Int = R.layout.activity_scan


    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(this, false)
        rxPermissions = RxPermissions(this)
        grantPermissionDialog()
        zxing_view.setDelegate(this)
        iv_back.onClick {
            finish()
        }
        tv_open_flash.onClick {
            if (isOpenFlash) {
                tv_open_flash.isSelected = false
                //关闭闪光灯
                zxing_view.closeFlashlight()
                isOpenFlash = false
                tv_open_flash.text = "打开闪光灯"
            } else {
                tv_open_flash.isSelected = true
                zxing_view.openFlashlight()
                isOpenFlash = true
                tv_open_flash.text = "关闭闪光灯"
            }

        }
        tv_open_album.onClick {
            Album.image(this) // Image selection.
                    .singleChoice()
                    .camera(false)
                    .columnCount(4)
                    .onResult {
                        isScanAlbum = true
                        zxing_view.decodeQRCode(it[0].path)
                    }
                    .onCancel { }
                    .widget(Widget.newDarkBuilder(this).title("选择图片") // Title.
                            .statusBarColor(Color.parseColor("#1B1B1B")) // StatusBar color.
                            .toolBarColor(Color.parseColor("#1B1B1B")) // Toolbar color.
                            .navigationBarColor(Color.parseColor("#D9B560")) // Virtual NavigationBar color of Android5.0+.
                            .mediaItemCheckSelector(Color.parseColor("#1B1B1B"), Color.parseColor("#D9B560")) // Image or video selection box.
                            .bucketItemCheckSelector(Color.parseColor("#1B1B1B"), Color.parseColor("#D9B560")) // Select the folder selection box.
                            .buttonStyle( // Used to configure the style of button when the image/video is not found.
                                    Widget.ButtonStyle.newLightBuilder(this) // With Widget's Builder model.
                                            .setButtonSelector(Color.parseColor("#1B1B1B"), Color.parseColor("#D9B560")) // Button selector.
                                            .build()
                            )
                            .build())
                    .start()
        }
        initData()
    }

    //--------------------------handle reuslt----------
    private val homViewModel by lazy {
        ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    fun initData() {
        homViewModel.mNetReqResultLiveData.observe(this, Observer {
            when (it?.tag) {
                HomeViewModel.TAG_GOODS_DECODE_CODE, HomeViewModel.TAG_GOODS_DECODE_URL -> {
                    closeLoadDialog()
                    if (null != it.data) { //接口调用成功
                        val goodsDecodeEntity = it!!.data as ResponseDataObject<GoodsEntity>
                        handleNext(goodsDecodeEntity)
                    } else { //接口调用失败
                        ToastUtil.show("解析失败")
                        startScan()
                    }
                }
            }
        })
    }

    var text: String? = null
    private fun handleResult(result: String?) {
        if (TextUtils.isEmpty(result)) {
            ToastUtil.show("无法识别")
            return
        }
        text = result

        showLoadingDialog("请稍候")
        //解析字符串
        homViewModel.decodeGoodByCode(text, 1, "0")
    }

    private fun handleNext(data: ResponseDataObject<GoodsEntity>) {
        if (data.code == 502) { //
            if (text!!.startsWith("http", true)) {
                handleUrl(text)
            } else {
                showUrlDialog(data.message)
            }
            return
        }
        val goodsEntity = data.data
        if (null != goodsEntity && !TextUtils.isEmpty(goodsEntity.goods_id)) {
            //商品详情
            GoodsDetailActivity.start(ScanActivity@ this, goodsEntity!!.goods_id, goodsEntity!!.item_source, goodsEntity)
            return
        }

        if (null != goodsEntity && TextUtils.equals(goodsEntity.need_search, "1")) { //进入搜索页
            SearchFrag.startFromSearch(mContext, text, goodsEntity.item_source)
            return
        }


        var result = text
        if (null != goodsEntity && !TextUtils.isEmpty(goodsEntity.real_url)) {
            result = goodsEntity.real_url
        }

        //url
        if (result!!.startsWith("http", true)) {
            handleUrl(result)
            return
        }

        //字符串
        showScanTextDialog(result)
    }

    fun handleUrl(result: String?) {
        if (CommonUtil.isInnerUrl(result)) { //内部链接
            var webViewParam = WebViewFrag.WebViewParam()
            webViewParam.url = result
            WebViewFrag.start(this, webViewParam)
        } else {   //外部链接
            val urlOpenDialogView = UrlOpenDialogView(this, result)
            urlOpenDialogView.setDismissListener {
                startScan()
            }
            EffectDialogBuilder(this)
                    .setContentView(urlOpenDialogView)
                    .setCancelable(false)
                    .setCancelableOnTouchOutside(false)
                    .show()
        }

    }

    private fun showScanTextDialog(str: String) {
        val textDialogView = ScanTextDialogView(this, str)
        textDialogView.setDismissListener {
            startScan()
        }
        EffectDialogBuilder(this)
                .setContentView(textDialogView)
                .setCancelable(false)
                .setCancelableOnTouchOutside(false)
                .show()
    }

    private fun showUrlDialog(str: String) {
        val textDialogView = ParseTextDialogView(this, str)
        textDialogView.setDismissListener {
            startScan()
        }
        EffectDialogBuilder(this)
                .setContentView(textDialogView)
                .setCancelable(false)
                .setCancelableOnTouchOutside(false)
                .show()
    }

    //--------------------------handle reuslt-----------

    @SuppressLint("AutoDispose", "CheckResult")
    private fun grantPermissionDialog() {
        if (!rxPermissions!!.isGranted(Manifest.permission.CAMERA)) {
            rxPermissions!!.request(Manifest.permission.CAMERA)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe {
                        if (it) {
                            startScan()
                        } else {
                            grantPermissionDialog()
                        }
                    }

        } else {
            startScan()
        }
    }

    override fun onScanQRCodeSuccess(result: String?) {
        if (isScanAlbum) {
            startScan()
            isScanAlbum = false
        } else {
//            zxing_view.stopSpot() // 开始识别
        }
        handleResult(result)
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        var tipText = zxing_view.scanBoxView.tipText
        val ambientBrightnessTip = "\n环境过暗，请打开闪光灯"
        if (isDark) {
            //环境过暗的话显示打开闪光灯按钮
            if (!tipText.contains(ambientBrightnessTip)) {
                zxing_view.scanBoxView.tipText = tipText + ambientBrightnessTip
            }
        } else {
            //环境不暗的话显示关闭闪光灯按钮
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip))
                zxing_view.scanBoxView.tipText = tipText
            }
        }
    }

    override fun onScanQRCodeOpenCameraError() {
        Log.e("ScanActivity", "打开相机出错")
    }


    override fun onStart() {
        super.onStart()
        zxing_view.showScanRect()
    }

    /**
     * 开始扫描
     */
    private fun startScan() {
        zxing_view.stopCamera()
        zxing_view.startSpotAndShowRect() // 显示扫描框，并开始识别
    }

    override fun onResume() {
        startScan()
        super.onResume()
    }

    override fun onStop() {
        zxing_view.stopCamera() // 关闭摄像头预览，并且隐藏扫描框
        super.onStop()

    }

    override fun onDestroy() {
        //关闭闪光灯
        zxing_view.closeFlashlight()
        zxing_view.onDestroy()// 销毁二维码扫描控件
        super.onDestroy()

    }

    /**
     * 震动
     */
    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }

    companion object {
        //启动Activity
        fun start(context: Context?) {
            context?.startActivity(Intent(context, ScanActivity::class.java))

        }
    }
}
