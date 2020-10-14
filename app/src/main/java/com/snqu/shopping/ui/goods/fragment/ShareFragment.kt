package com.snqu.shopping.ui.goods.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.*
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jakewharton.rxbinding2.view.RxView
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.DataConfig
import com.snqu.shopping.data.goods.bean.SelectedPicBean
import com.snqu.shopping.data.goods.entity.GoodsEntity
import com.snqu.shopping.data.home.ItemSourceClient
import com.snqu.shopping.data.user.entity.Watermark
import com.snqu.shopping.ui.goods.adapter.SharePicAdapter
import com.snqu.shopping.ui.login.vm.UserViewModel
import com.snqu.shopping.ui.order.util.ImgUtils
import com.snqu.shopping.util.DispatchUtil
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ShareUtil
import com.snqu.shopping.util.ext.onClick
import com.snqu.shopping.util.statistics.task.DayTaskReport
import com.snqu.shopping.util.statistics.task.TaskInfo
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.include_bitmap.*
import kotlinx.android.synthetic.main.share_fragment.*
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * desc:商品详情页分享
 * time: 2019/11/21
 * @author 银进
 */
class ShareFragment : SimpleFrag() {
    private val picAdapter by lazy {
        SharePicAdapter().apply {
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
        arguments?.getParcelable<GoodsEntity>(EXTRA_GOODS)
    }
    private val password by lazy {
        arguments?.getString(EXTRA_PASSWORD)
    }

    private val imgs by lazy {
        arguments?.getStringArrayList(EXTRA_IMGS)
    }
    private val share_text by lazy {
        arguments?.getString(EXTRA_SHARE_TEXT)
    }
    private val share_code by lazy {
        arguments?.getString(EXTRA_SHARE_CODE)
    }
    private var enableClick = false
    private var isSelectedFirstPic = true
        get() {
            if (picAdapter.data.isNullOrEmpty()) return false
            return (picAdapter.data[0].selected)
        }


    private val userViewModel by lazy {
        ViewModelProviders.of(this).get(UserViewModel::class.java)
    }

    override fun getLayoutId() = R.layout.share_fragment

    override fun init(savedInstanceState: Bundle?) {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.setBackgroundColor(Color.WHITE)
        initView()
        taskInfo = DispatchUtil.taskInfo
        userViewModel.getUserWatermark()
        userViewModel.dataResult.observe(this, Observer {
            when (it?.tag) {
                ApiHost.GET_USER_WATERMARK -> {
                    if (it.successful) {
                        if (it.data != null) {
                            val watermark = it.data as Watermark
                            if (!TextUtils.isEmpty(watermark.watermark) && watermark.enabled == 1) {
                                Constant.water_name = watermark.watermark
                            }
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("CheckResult")
    private fun initView() {
        if (goodsEntity == null) {
            return
        }
        tv_share_rebate.text = SpanUtils()
                .append("分享赚:").setForegroundColor(Color.parseColor("#FFFFFF")).setFontSize(16, true)
                .append("￥").setForegroundColor(Color.parseColor("#FFFFFF")).setFontSize(18, true).setBold()
                .append(goodsEntity!!.getRebatePrice()).setForegroundColor(Color.parseColor("#FFFFFF")).setFontSize(25, true).setBold()
                .create()
        recycle_view_share_pic.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity, 0, false)
            adapter = picAdapter
        }
        //生产输入源
        if (null != imgs && imgs?.isNotEmpty()!!) {
            for (i in 0 until (imgs?.size ?: 0)) {
                if (i == 0) {
                    adapterData.add(SelectedPicBean(true, imgs?.get(i) ?: ""))
                } else {
                    adapterData.add(SelectedPicBean(false, imgs?.get(i) ?: ""))
                }
            }
        }

        picAdapter.setNewData(adapterData)

        changeSelectNum()
        tv_share_save_pic.onClick {
            for (i in 0 until picAdapter.data.size) {
                if (picAdapter.data[i].selected) {
                    if (i == 0) {
                        ImgUtils.saveImageToGalleryCheckExist(activity!!, ImgUtils.viewToBitmap(include_bitmap), picAdapter.data[i].url)
                    } else {
                        if (picAdapter.glideBitmap[i] != null) {
                            val name = Constant.water_name
                            var bitmap: Bitmap? = null
                            if (!TextUtils.isEmpty(name)) {
                                bitmap = setWaterMark(resources, picAdapter.glideBitmap[i]!!, name)
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
            }
            showToastShort("保存成功")
        }

        if (goodsEntity!!.item_source == Constant.BusinessType.TB || goodsEntity!!.item_source == Constant.BusinessType.TM) {
            tv_copy_password.visibility = View.VISIBLE
        } else {
            tv_copy_password.visibility = View.GONE
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

        tv_copy_password.onClick {
            val cmb = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val password = share_code
            cmb.primaryClip = ClipData.newPlainText(null, password)
//            CommonUtil.setClipboardText(password)
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

        //生成图片所需要的布局
        tv_title.setLineSpacing(0F, 1.2F)
        tv_title.setText(ItemSourceClient.getItemSourceName(goodsEntity?.item_source), goodsEntity?.item_title)
        //价格
        val price = SpanUtils()
        //优惠券
        if (goodsEntity!!.getCouponPrice().isEmpty()) {
            tv_coupon.visibility = View.GONE
        } else {
            tv_coupon.visibility = View.VISIBLE
            price.append("券后").setForegroundColor(Color.parseColor("#F34264")).setFontSize(13, true)
            tv_coupon.text = "${goodsEntity!!.getCouponPrice()}元券"
        }
        price.append("￥").setForegroundColor(Color.parseColor("#F34264")).setFontSize(14, true).setBold()
        try {
            val nowPrice = goodsEntity!!.getNow_price().split(".")
            if (nowPrice.size == 2) {
                price.append("${nowPrice[0]}").setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(23, true).setBold()
                price.append(".${nowPrice[1]} ").setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(14, true).setBold()
            } else {
                price.append("${goodsEntity!!.getNow_price()} ").setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(23, true).setBold()
            }
        } catch (e: Exception) {
            price.append("${goodsEntity!!.getNow_price()} ").setForegroundColor(Color.parseColor("#FFF73737")).setFontSize(23, true).setBold()
        }

        tv_new_price.text = price.create()
        tv_old_price.text = SpanUtils().append("原价:").setForegroundColor(Color.parseColor("#C3C4C7")).setFontSize(12, true)
                .append("￥${goodsEntity!!.getOld_price()}").setForegroundColor(Color.parseColor("#C3C4C7")).setFontSize(12, true).setStrikethrough()
                .create()
        GlideUtil.loadPic(img_pic, goodsEntity!!.item_image, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)

        if (TextUtils.equals(goodsEntity!!.item_source, Constant.BusinessType.TM) || TextUtils.equals(goodsEntity!!.item_source, Constant.BusinessType.TB)) {
            ll_tb_guide.visibility = View.VISIBLE
            tv_jd_guide.visibility = View.GONE
            img_jd_guide.visibility = View.GONE
            GlideUtil.loadBitmapPic(img_er_code, DataConfig.API_HOST + "qrcode?text=" + URLEncoder.encode(Constant.WebPage.SHARE_GOODS_URL + goodsEntity?.item_source + "-" + goodsEntity?._id + ".html?code=" + if (password?.isNotEmpty() == true) {
                URLEncoder.encode(password, "UTF-8")
            } else password, "UTF-8"), R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic, object : RequestListener<Bitmap> {
                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (resource != null) {
                        img_er_code.setImageBitmap(resource)
                        enableClick = true
                    } else {
                        img_er_code.setImageResource(R.drawable.icon_min_default_pic)
                        enableClick = false
                    }
                    return true
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    img_er_code.setImageResource(R.drawable.icon_min_default_pic)
                    enableClick = false
                    return true
                }
            })
        } else {
            img_jd_guide.visibility = View.VISIBLE
            tv_jd_guide.visibility = View.VISIBLE
            ll_tb_guide.visibility = View.GONE

            GlideUtil.loadBitmapPic(img_er_code, DataConfig.API_HOST + "qrcode?text=" + URLEncoder.encode(password, "UTF-8"), R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic, object : RequestListener<Bitmap> {
                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (resource != null) {
                        img_er_code.setImageBitmap(resource)
                        enableClick = true
                    } else {
                        img_er_code.setImageResource(R.drawable.icon_min_default_pic)
                        enableClick = false
                    }

                    return true
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    img_er_code.setImageResource(R.drawable.icon_min_default_pic)
                    enableClick = false
                    return true
                }
            })
        }
    }

    private fun shareToThird(media: SHARE_MEDIA) {
        if (isSelectedFirstPic) {
            if (!enableClick) {
                showToastShort("图片正在生成，请稍等")
                return
            }
        }
        val bitmapList = arrayListOf<Bitmap>()
        for (i in 0 until picAdapter.data.size) {
            if (picAdapter.data[i].selected) {
                if (i == 0) {
                    bitmapList.add(ImgUtils.viewToBitmap(include_bitmap))
                } else {
                    if (picAdapter.glideBitmap[i] != null) {
                        val name = Constant.water_name
                        if (!TextUtils.isEmpty(name)) {
                            bitmapList.add(setWaterMark(resources, picAdapter.glideBitmap[i]!!, name))
                        } else {
                            bitmapList.add(picAdapter.glideBitmap[i]!!)
                        }
                    } else {
                        showToastShort("图片正在生成，请稍等")
                        return
                    }
                }
            }
        }
        shareResult = ShareUtil.shareImgs(activity, bitmapList, media)
        copyContent()
    }

    //分享成功汇报
    var shareResult = false
    var taskInfo: TaskInfo? = null

    override fun onResume() {
        super.onResume()
        if (shareResult && null != taskInfo && TextUtils.equals(taskInfo?.type, "2") && TextUtils.equals(taskInfo?.id, "EverydayTaskThree")) { //分享成功  汇报
            DayTaskReport.shareReport(mContext, taskInfo)
        }
        shareResult = false
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
                .setVerticalAlign(SpanUtils.ALIGN_CENTER)
                .append("选择图片").setForegroundColor(Color.parseColor("#FF25282D")).setFontSize(16, true).setBold()
                .append("(已选").setForegroundColor(Color.parseColor("#FF25282D")).setFontSize(13, true)
                .append(num.toString()).setForegroundColor(Color.parseColor("#FFFF8202")).setFontSize(13, true)
                .append("张)").setForegroundColor(Color.parseColor("#FF25282D")).setFontSize(13, true)
                .create()
    }

    override fun onDestroy() {
        super.onDestroy()
        SimpleFragAct.lastClass = null
    }


    companion object {
        const val EXTRA_PASSWORD = "EXTRA_PASSWORD"
        const val EXTRA_GOODS = "EXTRA_GOODS"
        const val EXTRA_IMGS = "EXTRA_IMGS"
        const val EXTRA_SHARE_TEXT = "EXTRA_SHARE"
        const val EXTRA_SHARE_CODE = "EXTRA_SHARE_CODE"

        fun resizeBitmap(bitmap: Bitmap, w: Int, h: Int): Bitmap {
            val width = bitmap.width
            val height = bitmap.height
            val scaleWidth = w.toFloat() / width
            val scaleHeight = h.toFloat() / height
            val matrix = Matrix()
            matrix.postScale(scaleWidth, scaleHeight)
            Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true)
            return bitmap;
        }

        fun resizeBitmap(path: String?, width: Int, height: Int): Bitmap? {
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            options.outWidth = width
            options.outHeight = height
            var bmp = BitmapFactory.decodeFile(path, options)
            options.inSampleSize = options.outWidth / height
            options.inJustDecodeBounds = false
            bmp = BitmapFactory.decodeFile(path, options)
            return bmp
        }

        @JvmStatic
        fun start(context: Context?, password: String?, goods: GoodsEntity?, imgs: ArrayList<String?>?, share_text: String?, share_code: String?) {
            val fragParam = SimpleFragAct.SimpleFragParam("创建分享",
                    ShareFragment::class.java, Bundle().apply {
                putString(EXTRA_PASSWORD, password)
                putParcelable(EXTRA_GOODS, goods)
                putStringArrayList(EXTRA_IMGS, imgs)
                putString(EXTRA_SHARE_TEXT, share_text)
                putString(EXTRA_SHARE_CODE, share_code)
            })
            fragParam.mutliPage = true
            SimpleFragAct.start(context, fragParam)
        }

        @JvmStatic
        fun setWaterMark(resources: Resources, oldBitmap: Bitmap, content: String): Bitmap {
            var bitmap = oldBitmap
            val w = 690
            val h = 520
            if (bitmap.width < w || bitmap.height < h) {
                var scaleW = w / bitmap.width
                var scaleH = h / bitmap.height
                var scale = if (scaleW >= scaleH) scaleW else scaleH
                bitmap = ImageUtils.scale(bitmap, scale, scale)
            }
//            if (bitmap.width < w && bitmap.height < h) {
//                bitmap = BitmapUtil.big(bitmap, w.toFloat(), h.toFloat())
//            } else if (bitmap.width < w && bitmap.height > h) {
//                bitmap = BitmapUtil.big(bitmap, w.toFloat(), bitmap.height.toFloat())
//            } else if (bitmap.width > w && bitmap.height < h) {
//                bitmap = BitmapUtil.big(bitmap, bitmap.width.toFloat(), h.toFloat())
//            }
            val waterBitmap = BitmapFactory.decodeResource(resources, R.drawable.watermar_xlt)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.isAntiAlias = true
            val bitmapH = (bitmap.height / 2 + waterBitmap.height / 2).toFloat()
            canvas.drawBitmap(waterBitmap, (bitmap.width / 2 - waterBitmap.width / 2).toFloat(),
                    bitmapH, paint)
            paint.color = Color.parseColor("#59FFFFFF")
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = ConvertUtils.dp2px(22F).toFloat()
            val font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.typeface = font
            canvas.drawText(content, (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(), paint)

            paint.style = Paint.Style.STROKE
            paint.color = Color.parseColor("#26000000")
            paint.strokeWidth = 1f
            canvas.drawText(content, (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(), paint)
            return bitmap
        }
    }
}