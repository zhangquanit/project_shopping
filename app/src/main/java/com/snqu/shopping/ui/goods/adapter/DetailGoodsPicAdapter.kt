package com.snqu.shopping.ui.goods.adapter

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.bean.DetailImageContentBean
import com.snqu.shopping.data.home.ItemSourceClient
import com.snqu.shopping.ui.main.frag.community.ZoomPicFrag
import com.snqu.shopping.util.GlideUtil
import com.snqu.shopping.util.ext.onClick
import kotlinx.android.synthetic.main.person_goods_item.view.*

/**
 * desc:详情Fragment图文的适配器
 * time: 2019/1/30
 * @author 银进
 */
class DetailGoodsPicAdapter(data: List<DetailImageContentBean>) : BaseMultiItemQuickAdapter<DetailImageContentBean, BaseViewHolder>(data) {

    init {
        addItemType(DetailImageContentBean.DESC, R.layout.goods_detail_header)
        addItemType(DetailImageContentBean.DESC_IMG, R.layout.detail_goods_pic_item)
        addItemType(DetailImageContentBean.DESC_TEXT, R.layout.detail_goods_pic_item_text)
        addItemType(DetailImageContentBean.BOTTOM, R.layout.person_goods_item)
        addItemType(DetailImageContentBean.BOTTOM_RECOMMEND_TITLE, R.layout.goods_detail_recommend_header)
        addItemType(DetailImageContentBean.END, R.layout.goods_detail_end)
    }

    private fun View.convertBottomData(item: DetailImageContentBean, item_source: String) {
        val goodsEntity = item.goodsEntity
        GlideUtil.loadPic(img_person_item_pic, goodsEntity.item_image, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
        tv_person_item_title.text = goodsEntity.item_title ?: ""
        val price = SpanUtils()
        if (goodsEntity.getCouponPrice().isNullOrBlank()) {
            tv_person_coupon.visibility = View.GONE
        } else {
            tv_person_coupon.visibility = View.VISIBLE
            tv_person_coupon.text = goodsEntity.getCouponPrice() + "元券"
            price.append("券后 ").setForegroundColor(Color.parseColor("#25282D")).setFontSize(12, true)
        }
        tv_person_item_money.text = price.append("${goodsEntity.getNow_price()} ").setForegroundColor(Color.parseColor("#F34264")).setFontSize(16, true).setBold()
                .append("${goodsEntity.getOld_price()}").setForegroundColor(Color.parseColor("#848487")).setFontSize(11, true).setStrikethrough()
                .create()
        //返利
        if (goodsEntity.getRebatePrice().isNullOrEmpty()) {
            tv_person_rebate_money.visibility = View.GONE
        } else {
            tv_person_rebate_money.visibility = View.VISIBLE
            tv_person_rebate_money.text = "返${goodsEntity.getRebatePrice()}"
        }

        tv_person_pay_person_num.text = goodsEntity.getSell_count() + "人付款"
        //店铺
        if (goodsEntity.seller_shop_name.isNullOrBlank()) {
            tv_person_goods_shop_name.visibility = View.INVISIBLE
        } else {
            tv_person_goods_shop_name.visibility = View.VISIBLE
            tv_person_goods_shop_name.text = goodsEntity.seller_shop_name ?: ""
        }
        //店铺类型BCD
        if (goodsEntity.item_source.isNullOrBlank()) {
            tv_person_goods_from.visibility = View.INVISIBLE
        } else {
            tv_person_goods_from.visibility = View.VISIBLE
            tv_person_goods_from.text = ItemSourceClient.getItemSourceName(goodsEntity.item_source)
        }
    }

    override fun convert(helper: BaseViewHolder, item: DetailImageContentBean?) {
        helper.itemView.apply {
            val goodsEntity = item?.goodsEntity
            when (item?.type) {
                DetailImageContentBean.DESC_IMG -> {
                    val imageView: ImageView = findViewById(R.id.img_item)
                    GlideUtil.loadDetailPic(imageView, item.goodsEntity.desc_content, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)
                    imageView.onClick {
                        val list = ArrayList<String>()
                        list.add(item?.goodsEntity?.desc_content ?: "")
                        ZoomPicFrag.start(mContext, 0, list)
                    }
                }
                DetailImageContentBean.DESC_TEXT -> {
                    (this.findViewById(R.id.tv_item) as TextView).text = item?.goodsEntity?.desc_content
                            ?: ""
                }
                DetailImageContentBean.BOTTOM -> {
                    convertBottomData(item, goodsEntity!!.item_source!!)
                }
            }
        }
    }

}