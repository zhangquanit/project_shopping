package com.snqu.shopping.ui.mall.adapter;

import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.snqu.shopping.R;
import com.snqu.shopping.data.mall.entity.MallRecommendEntity;
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity;
import com.snqu.shopping.ui.mall.view.MallGoodItemView;
import com.snqu.shopping.util.GlideUtil;

import java.util.List;

/**
 * 推荐-活动
 */

public class MallRecommendActivityProvider extends BaseItemProvider<MallRecommendEntity, BaseViewHolder> {

    @Override
    public int viewType() {
        return MallRecommendAdapter.TYPE_ACT;
    }

    @Override
    public int layout() {
        return R.layout.mall_recommend_act;
    }

    @Override
    public void convert(BaseViewHolder helper, MallRecommendEntity data, int position) {
        GlideUtil.loadPic(helper.getView(R.id.imageview), data.images_url, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
        List<ShopGoodsEntity> goods = data.goods;
        helper.setGone(R.id.item_goods, null != goods && !goods.isEmpty());
        ViewGroup item_goods = helper.getView(R.id.item_goods);
        if (null != goods) {
            for (int i = 0; i < 3; i++) {
                MallGoodItemView itemView = (MallGoodItemView) item_goods.getChildAt(i);
                if (goods.size() > i) {
                    itemView.setVisibility(View.VISIBLE);
                    itemView.setData(goods.get(i));
                } else {
                    itemView.setVisibility(View.INVISIBLE);
                }

            }
        }
    }


    @Override
    public void onClick(BaseViewHolder helper, MallRecommendEntity data, int position) {
    }

    @Override
    public boolean onLongClick(BaseViewHolder helper, MallRecommendEntity data, int position) {
        return true;
    }
}
