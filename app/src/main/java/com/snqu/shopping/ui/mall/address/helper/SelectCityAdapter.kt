package com.snqu.shopping.ui.mall.address.helper

import android.graphics.Color
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R
import com.snqu.shopping.data.mall.entity.address.ProvinceEntity
import kotlinx.android.synthetic.main.select_address_item.view.*

/**
 * desc:城市选址适配器
 * time: 2019/12/2
 * @author 银进
 */
class SelectCityAdapter : BaseQuickAdapter<ProvinceEntity, BaseViewHolder>(R.layout.select_address_item) {
    var selectedId:String?=null
    override fun convert(helper: BaseViewHolder, item: ProvinceEntity?) {
        helper?.itemView?.apply {
            tv_title.text=item?.name
            if (selectedId != null&&selectedId==item?.id) {
                tv_title.setTextColor(Color.parseColor("#FFFF8202"))
                img_check.visibility= View.VISIBLE
            } else {
                img_check.visibility= View.GONE
                tv_title.setTextColor(Color.parseColor("#FF131413"))
            }
        }
    }
}