package com.snqu.shopping.ui.main.frag.community

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.blankj.utilcode.util.SpanUtils
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.snqu.shopping.R
import com.snqu.shopping.common.Constant
import com.snqu.shopping.data.DataConfig
import com.snqu.shopping.data.goods.entity.GoodsEntity
import com.snqu.shopping.data.home.ItemSourceClient
import com.snqu.shopping.util.GlideUtil
import kotlinx.android.synthetic.main.include_bitmap.view.*
import java.net.URLEncoder

/**

@author 张全
 */
class CommunityShareView : RelativeLayout {
    var callBack: CommunityShareCallBack? = null
    private var loadItemImg = false
        set(value) {
            field = value
            Log.e("loadItemImg", value.toString())
        }
    private var loadErCodeImg = false
        set(value) {
            field = value
            Log.e("loadErCodeImg", value.toString())
        }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    fun init() {
        LayoutInflater.from(context).inflate(R.layout.include_bitmap, this)
    }

    //    var goodsEntity: GoodsEntity? = null
//    var password: String? = null
    fun setData(goodsEntity: GoodsEntity?, password: String?) {
        if (goodsEntity == null) return
        loadErCodeImg = false
        loadItemImg = false
        //生成图片所需要的布局
        tv_title.setLineSpacing(0F, 1.2F)
        tv_title.setText(ItemSourceClient.getItemSourceName(goodsEntity.item_source),goodsEntity!!.item_title ?: "")
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
        GlideUtil.loadBitmapPic(img_pic, goodsEntity!!.item_image, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic, object : RequestListener<Bitmap> {
            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                if (resource != null) {
                    img_pic.setImageBitmap(resource)
                    loadItemImg = true
                    if (loadItemImg && loadErCodeImg) {
                        callBack?.loadComplete()
                    }
                } else {
                    img_pic.setBackgroundResource(R.drawable.icon_min_default_pic)
                    callBack?.loadFail()
                }
                return true
            }

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                img_pic.setBackgroundResource(R.drawable.icon_min_default_pic)
                callBack?.loadFail()
                return true
            }
        })

        if (goodsEntity!!.item_source == Constant.BusinessType.JD||goodsEntity!!.item_source == Constant.BusinessType.PDD||goodsEntity!!.item_source == Constant.BusinessType.V||goodsEntity!!.item_source == Constant.BusinessType.S) {
            img_jd_guide.visibility = View.VISIBLE
            tv_jd_guide.visibility = View.VISIBLE
            ll_tb_guide.visibility = View.GONE

            GlideUtil.loadBitmapPic(img_er_code, DataConfig.API_HOST + "qrcode?text=" + URLEncoder.encode(password, "UTF-8"), R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic, object : RequestListener<Bitmap> {
                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (resource != null) {
                        img_er_code.setImageBitmap(resource)
                        loadErCodeImg = true
                        if (loadItemImg && loadErCodeImg) {
                            callBack?.loadComplete()
                        }
                    } else {
                        img_er_code.setBackgroundResource(R.drawable.icon_min_default_pic)
                        callBack?.loadFail()
                    }
                    return true
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    img_er_code.setBackgroundResource(R.drawable.icon_min_default_pic)
                    callBack?.loadFail()
                    return true
                }
            })
        } else {
            ll_tb_guide.visibility = View.VISIBLE
            tv_jd_guide.visibility = View.GONE
            img_jd_guide.visibility = View.GONE
            GlideUtil.loadBitmapPic(img_er_code, DataConfig.API_HOST + "qrcode?text=" + URLEncoder.encode(Constant.WebPage.SHARE_GOODS_URL + goodsEntity?.item_source + "-" + goodsEntity?._id + ".html?code=" + if (password?.isNotEmpty() == true) {
                URLEncoder.encode(password, "UTF-8")
            } else password, "UTF-8"), R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic, object : RequestListener<Bitmap> {
                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (resource != null) {
                        img_er_code.setImageBitmap(resource)
                        loadErCodeImg = true
                        if (loadItemImg && loadErCodeImg) {
                            callBack?.loadComplete()
                        }
                    } else {
                        img_er_code.setBackgroundResource(R.drawable.icon_min_default_pic)
                        callBack?.loadFail()
                    }
                    return true
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    img_er_code.setBackgroundResource(R.drawable.icon_min_default_pic)
                    callBack?.loadFail()
                    return true
                }
            })

        }
    }

}

interface CommunityShareCallBack {
    fun loadComplete()
    fun loadFail()
}