package com.snqu.shopping.ui.mine.adapter

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.snqu.shopping.R


class InviteCodeAdapter() : BaseQuickAdapter<String, BaseViewHolder>(R.layout.changeinvicode_item) {


    private var sW = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        sW = (ScreenUtils.getScreenWidth() - (ConvertUtils.dp2px(76F)) - (ConvertUtils.dp2px(65F) * 4)) / 3
        return super.onCreateViewHolder(parent, viewType)
    }


    override fun convert(helper: BaseViewHolder, item: String?) {
        if (sW > 0 && helper.adapterPosition != 3) {
            val lp = helper.getView<LinearLayout>(R.id.item_container).layoutParams as androidx.recyclerview.widget.RecyclerView.LayoutParams
            lp.setMargins(0, 0, sW, 0);
        }
        helper.getView<TextView>(R.id.item_code).text = item
    }
}