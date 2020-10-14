package com.snqu.shopping.ui.goods.fragment

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.android.util.ext.ToastUtil
import com.anroid.base.SimpleFrag
import com.anroid.base.SimpleFragAct
import com.anroid.base.ui.StatusBar
import com.blankj.utilcode.util.LogUtils
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.common.event.PushEvent
import com.snqu.shopping.common.ui.AlertDialogView
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.goods.entity.GoodsEntity
import com.snqu.shopping.data.goods.entity.GoodsRecmEntity
import com.snqu.shopping.data.home.ItemSourceClient
import com.snqu.shopping.ui.goods.adapter.GoodPicAdapter
import com.snqu.shopping.ui.goods.vm.GoodsViewModel
import com.snqu.shopping.ui.main.frag.community.ZoomPicFrag
import com.snqu.shopping.util.CommonUtil
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ext.clickWithTrigger
import com.snqu.shopping.util.ext.onClick
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumFile
import com.yanzhenjie.album.api.widget.Widget
import common.widget.dialog.DialogView
import common.widget.dialog.EffectDialogBuilder
import common.widget.dialog.loading.LoadingDialog
import kotlinx.android.synthetic.main.recommend_good_frag.*
import org.greenrobot.eventbus.EventBus
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.concurrent.Executors

/**
 * 推荐商品
 */
class GoodRecommendFrag : SimpleFrag() {

    private var goodsEntity: GoodsEntity? = null
    private var mLoadingDialog: LoadingDialog? = null
    private lateinit var mAdapter: GoodPicAdapter
    private val uploadList = ArrayList<String>()
    private var uploadSize = 0
    private var mData: ArrayList<AlbumFile> = ArrayList() //数据源
    private var executors = Executors.newCachedThreadPool()
    private var tomorrow = 0 //是否明日发布

    private val goodsViewModel by lazy {
        ViewModelProviders.of(this).get(GoodsViewModel::class.java)
    }

    private val images_url by lazy {
        arguments?.getStringArrayList(EXTRA_COMMUNITY_DATA_SHOWIMAGES)
    }

    private val images by lazy {
        arguments?.getStringArrayList(EXTRA_COMMUNITY_DATA_IMAGES)
    }

    private val share_content by lazy {
        arguments?.getString(EXTRA_COMMUNITY_DATA_SHOW_CONTENT) ?: ""
    }

    private val share_advance by lazy {
        arguments?.getString(EXTRA_ADVANCE)
    }

    companion object {

        const val TYPE_NETWORK = "network"
        private const val EXTRA_DATA = "GOOD_DATA"
        private const val EXTRA_COMMUNITY_DATA_IMAGES = "COMMUNITY_DATA_IMAGES"
        private const val EXTRA_COMMUNITY_DATA_SHOWIMAGES = "COMMUNITY_DATA_SHOWIMAGES"
        private const val EXTRA_COMMUNITY_DATA_SHOW_CONTENT = "COMMUNITY_DATA_SHOW_CONTENT"
        private const val EXTRA_ADVANCE = "advance" //是否有特权

        @JvmStatic
        fun start(context: Context, goodsEntity: GoodsEntity?, advance: String?) {
            val fragParam = SimpleFragAct.SimpleFragParam("推荐商品",
                    GoodRecommendFrag::class.java, Bundle().apply {
                if (goodsEntity != null) {
                    putParcelable(EXTRA_DATA, goodsEntity)
                }
                putString(EXTRA_ADVANCE, advance)
            })
            SimpleFragAct.start(context, fragParam)
        }

        @JvmStatic
        fun start(context: Context, goodsEntity: GoodsEntity, images: List<String>?, showImages: List<String>?, show_content: String?, advance: String?) {
            val fragParam = SimpleFragAct.SimpleFragParam("推荐商品",
                    GoodRecommendFrag::class.java, Bundle().apply {
                putParcelable(EXTRA_DATA, goodsEntity)
                putStringArrayList(EXTRA_COMMUNITY_DATA_IMAGES, ArrayList(images))
                putStringArrayList(EXTRA_COMMUNITY_DATA_SHOWIMAGES, ArrayList(showImages))
                putString(EXTRA_COMMUNITY_DATA_SHOW_CONTENT, show_content)
                putString(EXTRA_ADVANCE, advance)
            })
            SimpleFragAct.start(context, fragParam)
        }

    }

    override fun getLayoutId(): Int = R.layout.recommend_good_frag


    override fun init(savedInstanceState: Bundle?) {
        arguments?.let {
            if (it.containsKey(EXTRA_DATA)) {
                goodsEntity = it.getParcelable(EXTRA_DATA)
            }
            setTheme()
            setGoodValue()
            setOnClickListener()
        }
    }

    private fun setTheme() {
        StatusBar.setStatusBar(activity, true, titleBar)
        titleBar.visibility = View.GONE
    }

    private fun setGoodValue() {

        //是否显示优先发布权限,1代表有特权
        if(!share_advance.isNullOrEmpty()&&TextUtils.equals(share_advance,"1")){
            good_ac_layout.visibility = View.VISIBLE
        }else{
            good_ac_layout.visibility = View.GONE
        }

        GlideUtil.loadPic(item_img, goodsEntity?.item_image, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)

        //优惠券
        if (!TextUtils.isEmpty(goodsEntity?.getCouponPrice())) {
            item_coupon.visibility = View.VISIBLE
            item_coupon.text = goodsEntity?.getCouponPrice() + "元券"
        } else {
            item_coupon.visibility = View.GONE
        }

        //返利金
        if (!TextUtils.isEmpty(goodsEntity?.getRebatePrice())) {
            item_fan.visibility = View.VISIBLE
            item_fan.text = "返" + goodsEntity?.getRebatePrice()
        } else {
            item_fan.visibility = View.GONE
        }

        //平台名称
        tv_icon.text = ItemSourceClient.getItemSourceName(goodsEntity?.item_source)

        //商品名称
        tv_name.text = goodsEntity?.item_title ?: ""

        //券后价
        item_price.text = CommonUtil.getPrice(goodsEntity)

        //设置推荐理由字数
        good_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!TextUtils.isEmpty(s.toString())) {
                    val str = s.toString()
                    good_input_tip.text = "${str.length}/500"
                } else {
                    good_input_tip.text = "0/500"
                }
                showSubmitBtn()
            }
        })

        good_input.setOnTouchListener { v, motionEvent ->
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

        //设置图片
        mAdapter = GoodPicAdapter()
        if (images_url != null && images != null && images_url?.size == images?.size && images?.size!! > 0) {
            images_url!!.forEachIndexed { _, s ->
                val albumFile = AlbumFile()
                albumFile.path = s
                albumFile.bucketName = TYPE_NETWORK
                mData.add(albumFile)
            }
            if (mData.size < 9) {
                mData.add(AlbumFile())
            }
            mAdapter.setNewData(mData)
            showSubmitBtn()
        } else {
            mData.add(AlbumFile())
            mAdapter.setNewData(mData)
        }

        goods_pic_listview.apply {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 3)
            adapter = mAdapter
        }

        mAdapter.setOnItemClickListener { _, view, position ->
            val iconPicEmpty = view.findViewById<View>(R.id.icon_pic_empty)
            if (iconPicEmpty.visibility == View.VISIBLE) {
                openFileChooser(position)
            } else {
                val list = ArrayList<String>()
                mData.filter {
                    !TextUtils.isEmpty(it.path)
                }.forEach {
                    val albumFile = it
                    var url = ""
                    if (TextUtils.equals(albumFile.bucketName, TYPE_NETWORK)) {
                        url = albumFile.path
                    } else {
                        url = "good://" + albumFile.path
                    }
                    list.add(url)
                }
                ZoomPicFrag.start(view.context, position, list)
            }
        }

        mAdapter.setOnItemChildClickListener { adapter, view, position ->
            if (view.id == R.id.icon_close) {
                mData.removeAt(position)
                if (mAdapter.data.size == 8) {
                    val count = mAdapter.data.filter {
                        !TextUtils.isEmpty(it.path)
                    }.size
                    if (count == 8) {
                        mData.add(AlbumFile())
                    }
                }
                mAdapter.setNewData(mData)
                showSubmitBtn()
            }
        }


        //提交
        good_btn.clickWithTrigger(1000) {
            if (mData.size >= 2) {
                showLoadingDialog("提交中")
                executors.execute {
                    uploadList.clear()
                    val list = mData.filter {
                        !TextUtils.isEmpty(it.path)
                    }
                    uploadSize = list.size
                    list.forEachIndexed { index, s ->
                        val albumFile = list[index]
                        if (TextUtils.equals(TYPE_NETWORK, albumFile.bucketName)) {
                            uploadList.add(albumFile.path)
                            uploadSize--
                            if (uploadSize == 0) {
                                goodsViewModel.sendGoodRecm(uploadList, good_input.text.toString(), goodsEntity?._id
                                        ?: "",
                                        goodsEntity?.item_source ?: "", goodsEntity?.item_id ?: "",tomorrow)
                            }
                        } else {
                            val file = File(list[index].path)
                            val bitmap = BitmapFactory.decodeFile(list[index].path)
                            if (bitmap != null && bitmap.width > 0) {
                                Luban.with(activity)
                                        .load(file)
                                        .ignoreBy(100)
                                        .setCompressListener(object : OnCompressListener {
                                            @Override
                                            override fun onStart() {
                                            }

                                            override fun onSuccess(file: File) {
                                                if (file != null) {
                                                    goodsViewModel.uploadGoodRecmFiles(file)
                                                } else {
                                                    uploadSize--
                                                    if (uploadSize == 0) {
                                                        goodsViewModel.sendGoodRecm(uploadList, good_input.text.toString(), goodsEntity?._id
                                                                ?: "",
                                                                goodsEntity?.item_source
                                                                        ?: "", goodsEntity?.item_id
                                                                ?: "",tomorrow)
                                                    }
                                                }
                                            }

                                            override fun onError(e: Throwable?) {
                                                uploadSize--
                                                if (uploadSize == 0) {
                                                    goodsViewModel.sendGoodRecm(uploadList, good_input.text.toString(), goodsEntity?._id
                                                            ?: "",
                                                            goodsEntity?.item_source
                                                                    ?: "", goodsEntity?.item_id
                                                            ?: "",tomorrow)
                                                }
                                            }
                                        }).launch()
                            } else {
                                uploadSize--
                                if (uploadSize == 0) {
                                    goodsViewModel.sendGoodRecm(uploadList, good_input.text.toString(), goodsEntity?._id
                                            ?: "",
                                            goodsEntity?.item_source
                                                    ?: "", goodsEntity?.item_id ?: "",tomorrow)
                                }
                            }
                        }
                    }
                }
            }
        }


        goodsViewModel.dataResult.observe(this, androidx.lifecycle.Observer {
            when (it?.tag) {
                ApiHost.POST_SHARE_GOOD_RECM -> {
                    closeLoadDialog()
                    if (it.successful) {
                        EffectDialogBuilder(activity)
                                .setContentView(object : DialogView(activity) {
                                    override fun getLayoutId(): Int {
                                        return R.layout.good_recm_success_dialog
                                    }

                                    override fun initView(view: View) {
                                        view.findViewById<View>(R.id.btn_left)?.onClick {
                                            EventBus.getDefault().post(PushEvent(Constant.Event.REFRESH_RECM))
                                            finish()
                                        }
                                        view.findViewById<View>(R.id.btn_right).onClick {
                                            EventBus.getDefault().post(PushEvent(Constant.Event.REFRESH_RECM))
                                            GoodRecmMySelfFrag.start(activity)
                                            finish()
                                        }
                                    }
                                })
                                .setCancelable(false)
                                .setCancelableOnTouchOutside(false)
                                .show()
                    } else {
                        ToastUtil.show(it.message)
                    }
                }
                ApiHost.UPFILE_COMMUNITY_GOODS_RECM -> {
                    uploadSize--
                    if (it.successful) {
                        val uploadEntityList = it.data as List<GoodsRecmEntity>
                        if (uploadEntityList.isNotEmpty()) {
                            val uploadEntity = uploadEntityList[0];
                            uploadList.add(uploadEntity.file)
                        }
                    }
                    if (uploadSize == 0) {
                        goodsViewModel.sendGoodRecm(uploadList, good_input.text.toString(), goodsEntity?._id
                                ?: "",
                                goodsEntity?.item_source ?: "", goodsEntity?.item_id ?: "",tomorrow)

                    }
                }
            }
        })

        if (!TextUtils.isEmpty(share_content)) {
            good_input.setText(share_content)
        }
    }

    private fun openFileChooser(positon: Int) {
        val widget = Widget.newLightBuilder(activity)
                .title("选择图片")
                .statusBarColor(Color.WHITE) // StatusBar color.
                .toolBarColor(Color.WHITE) // Toolbar color.
                .navigationBarColor(Color.WHITE) // Virtual NavigationBar color of Android5.0+.
//                .mediaItemCheckSelector(Color.BLUE, Color.GREEN) // Image or video selection box.
                .bucketItemCheckSelector(Color.RED, Color.YELLOW) // Select the folder selection box.
                .buttonStyle( // Used to configure the style of button when the image/video is not found.
                        Widget.ButtonStyle.newLightBuilder(activity) // With Widget's Builder model.
                                .setButtonSelector(Color.WHITE, Color.WHITE) // Button selector.
                                .build()
                )
                .build()
        Album.image(this)
                .multipleChoice()
                .widget(widget)
                .camera(true)
                .columnCount(3)
                .selectCount(9 - (mData.size - 1))
//                .checkedList(mData)
                .onResult { it ->
                    val newData = it.filter {
                        val bitmap = BitmapFactory.decodeFile(it.path)
                        bitmap != null && bitmap.width > 0
                    }

                    if (newData.size != it.size) {
                        val builder = EffectDialogBuilder(activity)
                        val dialogView: AlertDialogView = AlertDialogView(activity)
                                .setTitle("提示")
                                .setContent("选择图中有损坏的图片，已为您过滤去除，该损坏图片不能上传") //
                                .setSingleBtn("确定", View.OnClickListener {
                                    builder.dismiss()
                                })

                        builder
                                .setCancelable(false)
                                .setCancelableOnTouchOutside(false)
                                .setContentView(dialogView).show()
                    }
                    mData.removeAt(mData.size - 1)
                    mData.addAll(newData)
                    if (mData.size <= 8) {
                        mData.add(AlbumFile())
                    }
                    mAdapter.setNewData(mData)
                    showSubmitBtn()
                }
                .onCancel {

                }.start()
    }


    private fun setOnClickListener() {
        img_goods_back.onClick {
            finish()
        }
        good_recm_today.onClick {
            tomorrow = 0
            val drawable_p = resources.getDrawable(R.drawable.good_recm_p,null)
            drawable_p.setBounds(0,0,drawable_p.minimumWidth,drawable_p.minimumHeight)
            good_recm_today.setCompoundDrawables(drawable_p,null,null,null)
            val drawable_n = resources.getDrawable(R.drawable.good_recm_n,null)
            drawable_n.setBounds(0,0,drawable_n.minimumWidth,drawable_n.minimumHeight)
            good_recm_nextday.setCompoundDrawables(drawable_n,null,null,null)
        }
        good_recm_nextday.onClick {
            tomorrow = 1
            val drawable_p = resources.getDrawable(R.drawable.good_recm_n,null)
            drawable_p.setBounds(0,0,drawable_p.minimumWidth,drawable_p.minimumHeight)
            good_recm_today.setCompoundDrawables(drawable_p,null,null,null)
            val drawable_n = resources.getDrawable(R.drawable.good_recm_p,null)
            drawable_n.setBounds(0,0,drawable_n.minimumWidth,drawable_n.minimumHeight)
            good_recm_nextday.setCompoundDrawables(drawable_n,null,null,null)
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

    fun showSubmitBtn() {
        good_btn.isEnabled = good_input.length() >= 10 && mData.size >= 3
    }


}