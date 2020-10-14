package com.snqu.shopping.ui.main.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anroid.base.SimpleFrag;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.main.adapter.GoodListAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.statistics.SndoData;

/**
 * @author 张全
 */
public class RecommendGoodFrag extends SimpleFrag {
    private RecyclerView mFloorListView;
    private GoodListAdapter goodListAdapter;
    private GoodsQueryParam queryParam;
    private HomeViewModel mHomeViewModel;
    private static final String ITEM_SOURCE = "ITEM_SOURCEG";
    public MutableLiveData<NetReqResult> mGoodLiveData;

    public static Bundle getParam(String itemSource) {
        Bundle bundle = new Bundle();
        bundle.putString(ITEM_SOURCE, itemSource);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.recommend_good_item;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mGoodLiveData = new MutableLiveData<>();//
        queryParam = new GoodsQueryParam();
        queryParam.item_source = getArguments().getString(ITEM_SOURCE);
        initView();
        initData();
    }

    private void initView() {
        mFloorListView = findViewById(R.id.listview);

        goodListAdapter = new GoodListAdapter();
        mFloorListView.setAdapter(goodListAdapter);
        mFloorListView.setLayoutManager(new LinearLayoutManager(mContext));

        goodListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goodsEntity = goodListAdapter.getData().get(position);
                GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), goodsEntity);

                String place = SndoData.PLACE.homepage_unique_recommend.name();
                if (TextUtils.equals(queryParam.item_source, Constant.BusinessType.TB)) {
                    place = SndoData.PLACE.homepage_unique_tb_recommend.name();
                } else if (TextUtils.equals(queryParam.item_source, Constant.BusinessType.TM)) {
                    place = SndoData.PLACE.homepage_unique_tm_recommend.name();
                } else if (TextUtils.equals(queryParam.item_source, Constant.BusinessType.JD)) {
                    place = SndoData.PLACE.homepage_unique_jd_recommend.name();
                } else if (TextUtils.equals(queryParam.item_source, Constant.BusinessType.PDD)) {
                    place = SndoData.PLACE.homepage_unique_pdd_recommend.name();
                }
                SndoData.reportGoods(goodsEntity, position, place);

                //统计
                SndoData.event(SndoData.XLT_EVENT_HOME_RECOMMEDN,
                        SndoData.XLT_GOOD_ID, goodsEntity.get_id(),
                        "xlt_item_firstcate_title", "null",
                        "xlt_item_thirdcate_title", "null",
                        "xlt_item_secondcate_title", "null",
                        "good_name", goodsEntity.getItem_title(),
                        SndoData.XLT_ITEM_PLACE, String.valueOf(position + 1),
                        "xlt_item_source", goodsEntity.getItem_source()

                );
            }
        });

        goodListAdapter.setLoadMoreView(new CommonLoadingMoreView());
        goodListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadGoods();
            }
        }, mFloorListView);
    }

    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        //推荐商品
        mGoodLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_RECOMMEND_GOODS: //推荐商品
                        goodListAdapter.setEnableLoadMore(true);
                        if (netReqResult.successful) {
                            ResponseDataArray<GoodsEntity> goodsData = (ResponseDataArray<GoodsEntity>) netReqResult.data;

                            if (queryParam.page == 1) {
                                goodListAdapter.setNewData(goodsData.getDataList());
                            } else if (!goodsData.getDataList().isEmpty()) {
                                goodListAdapter.addData(goodsData.getDataList());
                            }

                            if (goodsData.hasMore()) {
                                queryParam.page++;
                                goodListAdapter.loadMoreComplete();
                            } else {
                                goodListAdapter.loadMoreEnd();
                            }
                        } else {
                            if (queryParam.page > 1) {
                                goodListAdapter.loadMoreFail();
                            }
                        }
                        break;
                }
            }
        });
        loadGoods();
    }

    public void scrollToTop() {
        mFloorListView.scrollToPosition(0);
    }

    private void loadGoods() {
        mHomeViewModel.getRecommendGoods(queryParam, mGoodLiveData);
    }

    public void refresh() {
        goodListAdapter.setEnableLoadMore(false);
        queryParam.page = 1;
        mHomeViewModel.getRecommendGoods(queryParam, mGoodLiveData);
    }
}
