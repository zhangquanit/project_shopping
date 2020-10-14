package com.snqu.shopping.ui.mine.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.entity.SharePosterEntity;
import com.snqu.shopping.util.GlideUtil;

public class SharePosterListAdapter extends BaseQuickAdapter<SharePosterEntity, BaseViewHolder> {
    private String invitateCode;
    private int checkedPos;

    public SharePosterListAdapter(String invitateCode) {
        super(R.layout.share_posters_item);
        this.invitateCode = invitateCode;
    }

    public void setChecked(int pos) {
        checkedPos = pos;
    }

    public int getCheckedPos() {
        return checkedPos;
    }

    @Override
    protected void convert(BaseViewHolder helper, SharePosterEntity item) {
        ImageView imageView = helper.getView(R.id.item_img);
        GlideUtil.loadPic(imageView, item.gen_image, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
//        helper.setText(R.id.item_code, "邀请口令：" + invitateCode);
        helper.getView(R.id.item_check).setSelected(helper.getAdapterPosition() == checkedPos);
    }

}