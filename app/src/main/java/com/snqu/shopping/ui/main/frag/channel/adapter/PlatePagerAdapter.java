package com.snqu.shopping.ui.main.frag.channel.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.ui.main.view.GifChannelCircleItemView;

import java.util.List;

public class PlatePagerAdapter extends BaseQuickAdapter<AdvertistEntity, BaseViewHolder> {

    private List<AdvertistEntity> mBottomDataList;
    private List<AdvertistEntity> mCenterDataList;

    public PlatePagerAdapter(@Nullable List<AdvertistEntity> topDataList, List<AdvertistEntity> centerDataList, List<AdvertistEntity> bottomDataList) {
        super(R.layout.home_item_plate, topDataList);
        this.mCenterDataList = centerDataList;
        this.mBottomDataList = bottomDataList;
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, AdvertistEntity item) {
        // 保持一个屏幕5个
        helper.getView(R.id.item_container).setLayoutParams(new LinearLayout.LayoutParams(ScreenUtils.getScreenWidth() / 5, ViewGroup.LayoutParams.WRAP_CONTENT));
        // topView
        GifChannelCircleItemView topItemView = helper.getView(R.id.topView);
        topItemView.setData(item);


        GifChannelCircleItemView bottomItemView = helper.getView(R.id.bottomView);
        if (mBottomDataList.size() == 0) {
            bottomItemView.setVisibility(View.GONE);
        } else {
            bottomItemView.setVisibility(View.INVISIBLE);
            // bottomView
            if (mBottomDataList != null && helper.getAdapterPosition() < mBottomDataList.size()) {
                AdvertistEntity plateInfo = mBottomDataList.get(helper.getAdapterPosition());
                bottomItemView.setVisibility(View.VISIBLE);
                bottomItemView.setData(plateInfo);
            }
        }

        GifChannelCircleItemView centerView = helper.getView(R.id.centerView);
        centerView.setVisibility(View.INVISIBLE);
        if (mCenterDataList != null && helper.getAdapterPosition() < mCenterDataList.size()) {
            AdvertistEntity plateInfo = mCenterDataList.get(helper.getAdapterPosition());
            centerView.setVisibility(View.VISIBLE);
            centerView.setData(plateInfo);
        }


        helper.addOnClickListener(R.id.topView);
        helper.addOnClickListener(R.id.bottomView);
        helper.addOnClickListener(R.id.centerView);

    }

}