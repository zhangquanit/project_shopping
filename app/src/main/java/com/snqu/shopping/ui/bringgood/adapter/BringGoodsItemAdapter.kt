package com.snqu.shopping.ui.bringgood.adapter

import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.makeramen.roundedimageview.RoundedImageView
import com.snqu.shopping.R
import com.snqu.shopping.data.bringgoods.BringGoodsItemBean
import com.snqu.shopping.data.goods.entity.GoodsEntity
import com.snqu.shopping.util.GlideUtil


class BringGoodsItemAdapter : BaseQuickAdapter<BringGoodsItemBean, BaseViewHolder>(R.layout.item_list_bring_goods) {
    override fun convert(helper: BaseViewHolder, item: BringGoodsItemBean?) {

        // 动画播放
        val itemPlay = helper.getView<ImageView>(R.id.item_play)
        val playAnim = itemPlay.background as AnimationDrawable
        if (playAnim != null && !playAnim.isRunning) {
            playAnim.start()
        }

        val itemImg = helper.getView<RoundedImageView>(R.id.item_img)
        val dp = ConvertUtils.dp2px(5F).toFloat()

        if (helper.adapterPosition == 0) {
            helper.setGone(R.id.item_content_layout, false)
            itemImg.setCornerRadius(dp, dp, dp, dp)
        } else {
            helper.setGone(R.id.item_content_layout, true)
            itemImg.setCornerRadius(dp, dp, 0F, 0F)
        }

        //商品图片
        GlideUtil.loadPic(itemImg, item?.first_frame, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic)

        //xx人已买
        val saleCount = item?.item_sale
        if (saleCount != null) {
            helper.setGone(R.id.item_sale_count, true)
            helper.setText(R.id.item_sale_count, saleCount.toString() + "人已买")
        } else {
            helper.setGone(R.id.item_sale_count, false)
        }

        // 商品价格
        val price = item?.goods_info?.item_price
        if (price != null) {
            helper.setGone(R.id.item_price, true)
            helper.setText(R.id.item_price, getPrice(item.goods_info))
        } else {
            helper.setGone(R.id.item_price, false)
        }

        //商品名称
        val goodsName = item?.goods_info?.item_title
        if (!TextUtils.isEmpty(goodsName)) {
            helper.setGone(R.id.item_goods_name, true)
            helper.setText(R.id.item_goods_name, goodsName)
        } else {
            helper.setGone(R.id.item_goods_name, false)
        }

        //优惠券
        if (!TextUtils.isEmpty(item?.goods_info?.getCouponPrice())) {
            helper.setGone(R.id.item_coupon, true)
            helper.setText(R.id.item_coupon, item?.goods_info?.getCouponPrice() + "元券")
        } else {
            helper.setGone(R.id.item_coupon, false)
        }

        //分享赚
        val rebatePrice: String? = item?.goods_info?.getRebatePrice()
        if (!TextUtils.isEmpty(rebatePrice)) {
            helper.setVisible(R.id.item_earn, true)
            helper.setText(R.id.item_earn, rebatePrice?.let { getRebatePriceText(it) })
        } else {
            helper.setVisible(R.id.item_earn, false)
        }

    }

    private fun getRebatePriceText(price: String): SpannableStringBuilder? {
        return SpanUtils().append("分享赚").setFontSize(10, true)
                .append("¥").setFontSize(8, true)
                .append(price).setFontSize(10, true)
                .setBold()
                .create()
    }

    fun getPrice(item: GoodsEntity): SpannableStringBuilder? {
        val price = item.getNow_price()
        val oldPrice = item.getOld_price()
        val spanUtils = SpanUtils()
        spanUtils.append("¥").setFontSize(12, true).setForegroundColor(Color.parseColor("#FFFFBB02"))
        if (!TextUtils.equals(price, oldPrice)) {
            //有返利金显示原件
            spanUtils.append("$price ").setFontSize(16, true).setForegroundColor(Color.parseColor("#FFFFBB02"))
            spanUtils.append(oldPrice).setForegroundColor(Color.parseColor("#848487")).setFontSize(11, true).setStrikethrough()
        } else {
            spanUtils.append(price).setFontSize(16, true).setForegroundColor(Color.parseColor("#FFFFBB02"))
            spanUtils.setBold()
        }
        return spanUtils.create()
    }


}