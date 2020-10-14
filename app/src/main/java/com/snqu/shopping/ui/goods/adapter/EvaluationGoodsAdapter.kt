package com.snqu.shopping.ui.goods.adapter

import android.view.View
import com.android.util.date.DateFormatUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.data.goods.entity.RateBase
import com.snqu.shopping.util.GlideUtil
import kotlinx.android.synthetic.main.evaluation_goods_item.view.*
import kotlinx.android.synthetic.main.include_evaluation_item_media_type_one.view.*
import java.util.*
import kotlinx.android.synthetic.main.evaluation_goods_item.view.include_evaluation_item_media_type_one as type_one

/**
 * desc:
 * time: 2019/8/23
 * @author 银进
 */
class EvaluationGoodsAdapter : BaseQuickAdapter<RateBase, BaseViewHolder>(R.layout.evaluation_goods_item) {
    override fun convert(helper: BaseViewHolder, item: RateBase?) {
        helper?.itemView?.apply {
            GlideUtil.loadPic(img_evaluation_item_head, item?.head_pic, R.drawable.icon_default_head, R.drawable.icon_default_head)
            tv_evaluation_item_name.text = item?.user_nick ?: ""
            //购买类型
            val buyType = java.lang.StringBuilder()
            if ((item?.date_time ?: 0) != 0) {
                buyType.append(DateFormatUtil.yyyy_MM_dd().format(Date((item?.date_time ?: 0)*1000L)))
                buyType.append(" ")
            }
            if (item?.sku_map != null) {
                item.sku_map.entries.forEach { entry ->
                    buyType.append(entry.key + "：" + entry.value + " ")
                }
            }
            tv_evaluation_item_detail.text = buyType.toString().trim()
            tv_evaluation_item_content.text = item?.feed_content ?: ""
            val imgList = item?.images ?: arrayListOf()
            type_one.apply {
                when (imgList.size) {
                    0 -> {
                        type_one.visibility = View.GONE
                    }
                    1 -> {
                        type_one.visibility = View.VISIBLE
                        img_include_evaluation_item_media_type_one_one.visibility = View.VISIBLE
                        img_include_evaluation_item_media_type_one_two.visibility = View.INVISIBLE
                        img_include_evaluation_item_media_type_one_three.visibility = View.INVISIBLE
                        GlideUtil.loadPic(img_include_evaluation_item_media_type_one_one, imgList[0], R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
                    }
                    2 -> {
                        type_one.visibility = View.VISIBLE
                        img_include_evaluation_item_media_type_one_one.visibility = View.VISIBLE
                        img_include_evaluation_item_media_type_one_two.visibility = View.VISIBLE
                        img_include_evaluation_item_media_type_one_three.visibility = View.INVISIBLE
                        GlideUtil.loadPic(img_include_evaluation_item_media_type_one_one, imgList[0], R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
                        GlideUtil.loadPic(img_include_evaluation_item_media_type_one_two, imgList[1], R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
                    }
                    else -> {
                        type_one.visibility = View.VISIBLE
                        img_include_evaluation_item_media_type_one_one.visibility = View.VISIBLE
                        img_include_evaluation_item_media_type_one_two.visibility = View.VISIBLE
                        img_include_evaluation_item_media_type_one_three.visibility = View.VISIBLE
                        GlideUtil.loadPic(img_include_evaluation_item_media_type_one_one, imgList[0], R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
                        GlideUtil.loadPic(img_include_evaluation_item_media_type_one_two, imgList[1], R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
                        GlideUtil.loadPic(img_include_evaluation_item_media_type_one_three, imgList[2], R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic)
                    }
                }
            }
            if (item?.append_feed.isNullOrEmpty()) {
                recycler_view_evaluation_item_add.visibility = View.GONE
            } else {
                recycler_view_evaluation_item_add.visibility = View.VISIBLE
                recycler_view_evaluation_item_add.apply {
                    layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
                    adapter=AppendFeedAdapter().apply {
                        setNewData(item?.append_feed)
                    }
                }

            }
            if ((item?.interact_info?.readCount ?: "0") == "0") {
                tv_evaluation_item_scan_time.visibility = View.GONE
            } else {
                tv_evaluation_item_scan_time.visibility = View.VISIBLE
                tv_evaluation_item_scan_time.text = "浏览${(item?.interact_info?.readCount ?: "0")}次"
            }

        }
        helper?.addOnClickListener(R.id.include_evaluation_item_media_type_one)
    }
}