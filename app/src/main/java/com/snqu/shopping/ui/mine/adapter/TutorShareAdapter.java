package com.snqu.shopping.ui.mine.adapter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.entity.TutorShareContract;
import com.snqu.shopping.util.DateUtil;
import com.snqu.shopping.util.GlideUtil;

/**
 * @author liuming
 */
public class TutorShareAdapter extends BaseQuickAdapter<TutorShareContract, BaseViewHolder> {

    public TutorShareAdapter() {
        super(R.layout.item_tutor_share);
    }

//    public TutorShareAdapter(@Nullable List<TutorShareContract> data) {
//        super(R.layout.item_tutor_share,data);
//    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TutorShareContract item) {
        if (!TextUtils.isEmpty(item.logo)) {
            GlideUtil.loadPic(helper.getView(R.id.item_icon), item.logo, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        }

        if (!TextUtils.isEmpty(item.title)) {
            helper.setText(R.id.item_title, item.title);
        }

        if (item.itime > 0) {
            helper.setText(R.id.item_time, "创建于 " + DateUtil.getFriendlyTimeStr2(item.itime * 1000L));
        }


    }

}
