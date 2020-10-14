package com.snqu.shopping.ui.mall.goods.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SpanUtils
import com.jakewharton.rxbinding2.view.RxView
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.data.goods.bean.SelectedPicBean
import com.snqu.shopping.data.mall.entity.MallGoodShareInfoEntity
import com.snqu.shopping.ui.goods.fragment.ShareFragment
import com.snqu.shopping.ui.mall.adapter.MallSharePicAdapter
import com.snqu.shopping.ui.order.util.ImgUtils
import com.snqu.shopping.util.ShareUtil
import com.snqu.shopping.util.ext.onClick
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.share_fragment.et_share_content
import kotlinx.android.synthetic.main.share_fragment.ll_qq
import kotlinx.android.synthetic.main.share_fragment.ll_wb
import kotlinx.android.synthetic.main.share_fragment.ll_wx
import kotlinx.android.synthetic.main.share_fragment.ll_wx_circle
import kotlinx.android.synthetic.main.share_fragment.nest_scroll_view
import kotlinx.android.synthetic.main.share_fragment.recycle_view_share_pic
import kotlinx.android.synthetic.main.share_fragment.tv_copy_content
import kotlinx.android.synthetic.main.share_fragment.tv_share_num
import kotlinx.android.synthetic.main.share_fragment.tv_share_save_pic
import kotlinx.android.synthetic.main.shop_share_fragment.*
import java.util.concurrent.TimeUnit

/**
 * 直供-分享
 */
class ShopShareFrag : SimpleFrag() {

    companion object {
        const val EXTRA_DATA = "EXTRA_DATA"

        @JvmStatic
        fun start(context: Context?, mallGoodShareInfoEntity: MallGoodShareInfoEntity?) {
            val fragParam = SimpleFragAct.SimpleFragParam("分享商品",
                    ShopShareFrag::class.java, Bundle().apply {
                putSerializable(EXTRA_DATA, mallGoodShareInfoEntity)
            })
            fragParam.mutliPage = true
            SimpleFragAct.start(context, fragParam)
        }
    }

    override fun getLayoutId(): Int = R.layout.shop_share_fragment

    override fun onResume() {
        super.onResume()
        shareResult = false
    }

    //分享成功汇报
    var shareResult = false

    private val picAdapter by lazy {
        MallSharePicAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                var selectNum = 0
                var index = -1
                for (i in 0 until data.size) {
                    if (data[i].selected) {
                        selectNum++
                        index = i
                    }
                }

                if (selectNum > 1) {
                    data[position].selected = !data[position].selected
                    view.findViewById<View>(R.id.img_select).isSelected = data[position].selected
                    view.findViewById<View>(R.id.view_blur).visibility = if (data[position].selected) View.VISIBLE else View.GONE
                    changeSelectNum()
                } else {
                    if (position == index) {
                        showToastShort("至少选一张")
                    } else {
                        data[position].selected = !data[position].selected
                        view.findViewById<View>(R.id.img_select).isSelected = data[position].selected
                        view.findViewById<View>(R.id.view_blur).visibility = if (data[position].selected) View.VISIBLE else View.GONE
                        changeSelectNum()
                    }
                }

            }
        }
    }

    val adapterData = arrayListOf<SelectedPicBean>()

    private val goodsEntity by lazy {
        arguments?.getSerializable(EXTRA_DATA) as MallGoodShareInfoEntity
    }

    private var imgs = ArrayList<String>()
    private var share_text = ""
    private var share_code = ""
    private var enableClick = false
//    private var isSelectedFirstPic = true
//        get() {
//            if (picAdapter.data.isNullOrEmpty()) return false
//            return (picAdapter.data[0].selected)
//        }

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        initView()
    }

    @SuppressLint("CheckResult")
    private fun initView() {
        if(goodsEntity.banner_img_txt==null||goodsEntity.name==null){
            finish()
        }
        imgs = goodsEntity.banner_img_txt as ArrayList<String>
        share_code = goodsEntity.url

        val shareText = SpanUtils()
        if (!goodsEntity.describe.isNullOrEmpty()) {
            shareText.append(goodsEntity.describe)
            shareText.append("\n")
            shareText.append("-------------")
            shareText.append("\n")
            shareText.append("打开地址:" + goodsEntity.url)
        } else {
            shareText.append("打开地址:" + goodsEntity.url)
        }
        share_text = shareText.create().toString()

        recycle_view_share_pic.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = picAdapter
        }
        //生产输入源
        imgs.forEachIndexed { index, s ->
            if (index == 0) {
                adapterData.add(SelectedPicBean(true, s))
            } else {
                adapterData.add(SelectedPicBean(false, s))
            }
        }
        picAdapter.setNewData(adapterData)

        changeSelectNum()
        tv_share_save_pic.onClick {
            for (i in 0 until picAdapter.data.size) {
                if (picAdapter.data[i].selected) {
                    if (picAdapter.glideBitmap[i] != null) {
                        val name = Constant.water_name
                        var bitmap: Bitmap? = null
                        if (!TextUtils.isEmpty(name)) {
                            bitmap = ShareFragment.setWaterMark(resources, picAdapter.glideBitmap[i]!!, name)
                        } else {
                            bitmap = picAdapter.glideBitmap[i]!!
                        }
                        ImgUtils.saveImageToGalleryCheckExist(activity!!, bitmap!!, picAdapter.data[i].url)
                    } else {
                        showToastShort("图片下载中,请稍后...")
                        return@onClick
                    }

                }
            }
            showToastShort("保存成功")
        }



        if (!TextUtils.isEmpty(share_text)) {
            et_share_content.setText(share_text)
        }


        et_share_content.setOnTouchListener { v, motionEvent ->
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
            if (nest_scroll_view != null) {
                nest_scroll_view.post {
                    val lp = nest_scroll_view.layoutParams as FrameLayout.LayoutParams
                    nest_scroll_view.fullScroll(NestedScrollView.MEASURED_HEIGHT_STATE_SHIFT)
                    if (height > 0) {
                        lp.bottomMargin = height + ConvertUtils.dp2px(10F)
                    } else {
                        lp.bottomMargin = ConvertUtils.dp2px(230F)
                    }
                    nest_scroll_view.layoutParams = lp
                }
            }
        }

        tv_copy_url.onClick {
            val cmb = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val password = share_code
            cmb.primaryClip = ClipData.newPlainText(null, password)
            showToastShort("复制成功")
        }
        RxView.clicks(ll_wx).throttleFirst(1, TimeUnit.SECONDS).subscribe {
            shareToThird(SHARE_MEDIA.WEIXIN)
        }
        RxView.clicks(ll_wx_circle).throttleFirst(1, TimeUnit.SECONDS).subscribe {
            shareToThird(SHARE_MEDIA.WEIXIN_CIRCLE)
        }
        RxView.clicks(ll_qq).throttleFirst(1, TimeUnit.SECONDS).subscribe {
            shareToThird(SHARE_MEDIA.QQ)
        }
        RxView.clicks(ll_wb).throttleFirst(1, TimeUnit.SECONDS).subscribe {
            shareToThird(SHARE_MEDIA.SINA)
        }
        tv_copy_content.onClick {
            copyContent()
            showToastShort("复制成功")
        }
    }

    private fun shareToThird(media: SHARE_MEDIA) {
        val bitmapList = arrayListOf<Bitmap>()
        for (i in 0 until picAdapter.data.size) {
            if (picAdapter.data[i].selected) {
                if (picAdapter.glideBitmap[i] != null) {
                    val name = Constant.water_name
                    if (!TextUtils.isEmpty(name)) {
                        bitmapList.add(ShareFragment.setWaterMark(resources, picAdapter.glideBitmap[i]!!, name))
                    } else {
                        bitmapList.add(picAdapter.glideBitmap[i]!!)
                    }
                } else {
                    showToastShort("图片正在生成，请稍等")
                    return
                }
            }
        }
        shareResult = ShareUtil.shareImgs(activity, bitmapList, media)
        copyContent()
    }

    /**
     * 复制内容
     */
    private fun copyContent() {
        val cmb = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cmb.primaryClip = ClipData.newPlainText(null, et_share_content.text.toString())
    }

    private fun changeSelectNum() {
        var num = 0
        picAdapter.data.forEach {
            if (it.selected) {
                num++
            }
        }
        tv_share_num.text = SpanUtils()
                .append("选择图片").setForegroundColor(Color.parseColor("#26282E")).setFontSize(16, true).setBold()
                .append("(已选 ").setForegroundColor(Color.parseColor("#26282E")).setFontSize(13, true)
                .append(num.toString()).setForegroundColor(Color.parseColor("#FF8202")).setFontSize(13, true).setBold()
                .append("/" + picAdapter.data.size.toString()).setForegroundColor(Color.parseColor("#26282E")).setFontSize(13, true)
                .append("张)").setForegroundColor(Color.parseColor("#26282E")).setFontSize(13, true)
                .create()
    }

    override fun onDestroy() {
        super.onDestroy()
        SimpleFragAct.lastClass = null
    }


}