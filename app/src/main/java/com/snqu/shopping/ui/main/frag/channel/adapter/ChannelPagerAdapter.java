package com.snqu.shopping.ui.main.frag.channel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.ui.main.frag.GoodsListFrag;
import com.snqu.shopping.ui.main.view.ChannelItemView;
import com.snqu.shopping.util.statistics.StatisticInfo;

import java.util.List;

public class ChannelPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<List<CategoryEntity>> mData;
    public static final int MARGIN_TOP = 15;
    public static final int ITEM_HEIGHT = 80;
    int marginLeft, marginTop;

    public ChannelPagerAdapter(Context context, List<List<CategoryEntity>> list) {
        mContext = context;
        mData = list;
        marginTop = DeviceUtil.dip2px(mContext, MARGIN_TOP);
        marginLeft = DeviceUtil.dip2px(mContext, 15);
    }

    public static int getLineHeight() {
        return DeviceUtil.dip2px(LContext.getContext(), ITEM_HEIGHT);
    }

    public static int getTwoLineHeight() {
        return DeviceUtil.dip2px(LContext.getContext(), ITEM_HEIGHT + MARGIN_TOP + ITEM_HEIGHT);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.channel_item_container, null);
        initChannel(mData.get(position), linearLayout);
        container.addView(linearLayout);
        return linearLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // super.destroyItem(container,position,object); 这一句要删除，否则报错
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    /**
     * 频道
     */
    private View initChannel(List<CategoryEntity> dataList, LinearLayout linearLayout) {

        int spanCount = 4;
        int count = dataList.size();
        int row = count <= 4 ? 1 : 2;
        int weightSum = 4;

        for (int i = 0; i < row; i++) {
            LinearLayout itemLayout = new LinearLayout(mContext);
            itemLayout.setWeightSum(weightSum);
            itemLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            for (int j = 0; j < spanCount; j++) {
                int index = i * spanCount + j;
                if (index >= count) {
                    break;
                }
                CategoryEntity channelEntity = dataList.get(index);
                ChannelItemView channelItemView = new ChannelItemView(mContext);
                channelItemView.setData(channelEntity);
                channelItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StatisticInfo statisticInfo = new StatisticInfo();
                        statisticInfo.viewPage(channelEntity.pid, channelEntity._id);
                        GoodsListFrag.startForPlate(mContext, channelEntity);
                    }
                });
                itemLayout.addView(channelItemView, layoutParams);
            }

            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                layoutParams.topMargin = marginTop;
            }
            linearLayout.addView(itemLayout, layoutParams);
        }
        return linearLayout;
    }

}