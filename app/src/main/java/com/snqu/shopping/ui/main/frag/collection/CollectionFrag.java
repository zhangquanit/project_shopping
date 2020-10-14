package com.snqu.shopping.ui.main.frag.collection;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.goods.entity.CollectionGoodsEntity;
import com.snqu.shopping.data.goods.entity.CollectionListGoodsEntity;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.goods.vm.GoodsViewModel;
import com.snqu.shopping.ui.main.MainActivity;
import com.snqu.shopping.ui.main.adapter.CollectionRecommendListAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.ui.mine.adapter.PersonGoodsItemAdapter;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.statistics.SndoData;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏
 *
 * @author 张全
 */
public class CollectionFrag extends SimpleFrag {
    private View emptyBar;
    //全选按钮
    private View tvSel;
    private TextView tv_price;
    private TextView tv_collection_num;
    private SmartRefreshLayout smartRefreshLayout;
    private TextView tv_del;
    private TextView tv_count;
    private View collection_header;
    private View invalidateListHeader;
    private RecyclerView collectionListView;
    private RecyclerView invalidateListView;
    private RecyclerView recommendListView;
    private CollectionRecommendListAdapter collectionListAdapter;
    private CollectionRecommendListAdapter invalidateListAdapter;
    private PersonGoodsItemAdapter recommendListAdapter;
    private View bottomBar;
    private GoodsViewModel goodsViewModel;
    private HomeViewModel homeViewModel;
    private TextView tv_operator;
    //删除的数据源用来做删除用
    ArrayList<CollectionGoodsEntity> deleteData = new ArrayList<>();
    private View recommendHeadView;
    private boolean initData;
    private int page = 1;
    private int row = 20;

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam fragParam = new SimpleFragAct.SimpleFragParam("我的收藏",
                CollectionFrag.class);
        SimpleFragAct.start(context, fragParam);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.collection_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, false, getTitleBar());
        getTitleBar().setVisibility(View.GONE);
        initView();
        initData();
        refreshData();
    }

    private void refreshData() {
        recommendListAdapter.setEnableLoadMore(false);
        goodsViewModel.doCollectionGoodsList();
        page = 1;
        homeViewModel.likeGoods(page, row);
    }


    private void loadMoreData() {
        homeViewModel.likeGoods(page, row);
    }

    private void setPrice(String price) {
        SpannableStringBuilder priceBuilder = new SpanUtils()
                .append(price).setForegroundColor(Color.parseColor("#FF8202")).setFontSize(20, true)
                .append("元").setForegroundColor(Color.parseColor("#ff848487")).setFontSize(14, true)
                .create();
        tv_price.setText(priceBuilder);
    }

    private void initData() {
        goodsViewModel = ViewModelProviders.of(this).get(GoodsViewModel.class);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        goodsViewModel.getDataResult().observe(this, netReqResult -> {
            switch (netReqResult.tag) {
                case ApiHost.COLLECTION_GOODS_LIST:
                    if (netReqResult.successful) {
                        smartRefreshLayout.finishRefresh(true);
                        CollectionListGoodsEntity collectionListGoodsEntity = (CollectionListGoodsEntity) netReqResult.data;
                        //未失效
                        if (collectionListGoodsEntity.getFail_list().isEmpty() && collectionListGoodsEntity.getList().isEmpty()) {
                            //请求成功但是没有数据展示空页面
                            showEmptyStatus();
                            tv_collection_num.setText("我的收藏");
                        } else {
                            tv_collection_num.setText("我的收藏(" + (collectionListGoodsEntity.getList().size() + collectionListGoodsEntity.getFail_list().size()) + ")");
                            //是否有失效商品
                            showNormalStatus();
                            if (collectionListGoodsEntity.getFail_list().isEmpty()) {
                                invalidateListView.setVisibility(View.GONE);
                                invalidateListAdapter.setNewData(null);
                            } else {
                                invalidateListView.setVisibility(View.VISIBLE);
                                //失效宝贝
                                invalidateListAdapter.setNewData(collectionListGoodsEntity.getFail_list());
                                setOInvalidateCount(collectionListGoodsEntity.getFail_list().size() + "");
                                tv_del.setEnabled(false);
                                tvSel.setSelected(false);
                            }
                            //是否有未失效商品
                            if (collectionListGoodsEntity.getList().isEmpty()) {
                                collectionListView.setVisibility(View.GONE);
                                collectionListAdapter.setNewData(null);
                            } else {
                                collectionListView.setVisibility(View.VISIBLE);
                                collectionListAdapter.setNewData(collectionListGoodsEntity.getList());
                                tv_del.setEnabled(false);
                                tvSel.setSelected(false);
                            }
                            setPrice(collectionListGoodsEntity.getFrugalMoney());
                        }
                    } else {
                        smartRefreshLayout.finishRefresh(false);
                        //请求失败
                        showEmptyStatus();
                    }
                    break;
                case ApiHost.CLEAR_FAIL_COLLECTION_GOODS:
                    if (netReqResult.successful) {
                        invalidateListView.setVisibility(View.GONE);
                        invalidateListAdapter.setNewData(null);
                    }
                    showToastShort(netReqResult.message);
                    break;
                case ApiHost.DELETE_COLLECTION_GOODS:
                    if (netReqResult.successful) {
                        changeButtonSelectStatus();
                        goodsViewModel.doCollectionGoodsList();
                    }
                    showToastShort(netReqResult.message);
                    break;
            }

        });
        homeViewModel.mNetReqResultLiveData.observe(this, netReqResult -> {
            if (netReqResult.tag.equals(ApiHost.LIKE_GOODS)) {
                recommendListAdapter.setEnableLoadMore(true);
                if (netReqResult.successful) {
                    ResponseDataArray<GoodsEntity> goodsData = (ResponseDataArray<GoodsEntity>) netReqResult.data;
                    List<GoodsEntity> data = goodsData.getDataList();
                    if (!data.isEmpty()) {
                        if (page == 1) {
                            recommendListAdapter.setNewData(data);
                        } else {
                            recommendListAdapter.addData(data); //添加下一页的数据
                        }

                        if (goodsData.hasMore()) { //还有更多数据
                            page++;
                            recommendListAdapter.loadMoreComplete();
                        } else {
                            recommendListAdapter.loadMoreEnd(page == 1);//无下一页
                        }
                    } else {
                        if (page == 1) {
                            showEmptyView();
                        }
                    }
                } else {
                    if (page == 1) {
                        showErrorView();
                    } else {
                        recommendListAdapter.loadMoreFail();
                    }
                }
            }
        });
    }

    private void initView() {
        addAction(Constant.Event.LOGIN_SUCCESS);
        addAction(Constant.Event.COLLECTION_CHANGE);
        recommendHeadView = getLayoutInflater().inflate(R.layout.recommend_head_item, null);
        tv_collection_num = findViewById(R.id.tv_collection_num);
        findViewById(R.id.img_back).setOnClickListener(v -> finish());
        tv_operator = findViewById(R.id.tv_operator);
        tv_operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean editable = collectionListAdapter.setEditable();
                invalidateListAdapter.setEditable();
                tv_operator.setText(editable ? "完成" : "管理");
                bottomBar.setVisibility(editable ? View.VISIBLE : View.GONE);
                if (editable) {
                    recommendListView.scrollToPosition(0);
                }
                //这里不做记录
                unSelectedAll();
            }
        });
        smartRefreshLayout = findViewById(R.id.refresh_layout);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            page = 1;
            refreshData();
        });
        CommonUtil.setRefreshHeaderWhiteText(smartRefreshLayout);
        bottomBar = findViewById(R.id.collection_bottombar);
        tv_del = findViewById(R.id.tv_del);
        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<CollectionGoodsEntity> collectionListAdapterData = (ArrayList<CollectionGoodsEntity>) collectionListAdapter.getData();
                ArrayList<CollectionGoodsEntity> invalidateListAdapterData = (ArrayList<CollectionGoodsEntity>) invalidateListAdapter.getData();
                for (int i = 0; i < collectionListAdapterData.size(); i++) {
                    if (collectionListAdapterData.get(i).isSelected()) {
                        deleteData.add(collectionListAdapterData.get(i));
                    }
                }

                for (int i = 0; i < invalidateListAdapterData.size(); i++) {
                    if (invalidateListAdapterData.get(i).isSelected()) {
                        deleteData.add(invalidateListAdapterData.get(i));
                    }
                }

                StringBuilder ids = new StringBuilder();
                for (int i = 0; i < deleteData.size(); i++) {
                    if (i == deleteData.size() - 1) {
                        ids.append(deleteData.get(i).get_id());
                    } else {
                        ids.append(deleteData.get(i).get_id());
                        ids.append(",");
                    }
                }
                goodsViewModel.doDeleteCollectionGoods(ids.toString());
            }
        });
        //是否全部选中
        tvSel = findViewById(R.id.tv_sel);
        tvSel.setOnClickListener(v -> {
            //删除
            if (tvSel.isSelected()) {
                //选中状态
                //那我就让他全部不选中
                unSelectedAll();

            } else {
                //未选中状态
                //那我就让他全部选中
                selectedAll();
            }
        });


        emptyBar = recommendHeadView.findViewById(R.id.emtpy_bar);
        recommendHeadView.findViewById(R.id.tv_tohome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回到首页
                MainActivity.start(mContext);
            }
        });

        //收藏列表
        collectionListView = recommendHeadView.findViewById(R.id.collection_list);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        collection_header = layoutInflater.inflate(R.layout.collection_list_header, null);
        tv_price = collection_header.findViewById(R.id.tv_price);

        collectionListAdapter = new CollectionRecommendListAdapter(false);
        collectionListAdapter.addHeaderView(collection_header);
        collectionListView.setAdapter(collectionListAdapter);
        collectionListView.setLayoutManager(new LinearLayoutManager(mContext));
        collectionListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goods = collectionListAdapter.getData().get(position).getGoods();
                GoodsDetailActivity.Companion.start(mContext, goods.get_id(), goods.getItem_source(), goods);
            }
        });
        collectionListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_check:
                        collectionListAdapter.getData().get(position).setSelected(!collectionListAdapter.getData().get(position).isSelected());
                        changeButtonSelectStatus();
                        //因为有header所以加1
                        view.setSelected(collectionListAdapter.getData().get(position).isSelected());
                        break;
                }
            }
        });

        //失效宝贝
        invalidateListHeader = layoutInflater.inflate(R.layout.collection_invalidate_list_header, null);
        tv_count = invalidateListHeader.findViewById(R.id.tv_count);
        invalidateListHeader.findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除失效宝贝
                goodsViewModel.doClearFailCollectionGoods();
            }
        });
        invalidateListView = recommendHeadView.findViewById(R.id.collection_invalidate_list);
        invalidateListView.setLayoutManager(new LinearLayoutManager(mContext));
        invalidateListAdapter = new CollectionRecommendListAdapter(true);
        invalidateListAdapter.addHeaderView(invalidateListHeader);
        invalidateListView.setAdapter(invalidateListAdapter);
        invalidateListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goods = invalidateListAdapter.getData().get(position).getGoods();
                GoodsDetailActivity.Companion.start(mContext, goods.get_id(), goods.getItem_source(), goods);
            }
        });
        invalidateListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_check:
                        invalidateListAdapter.getData().get(position).setSelected(!invalidateListAdapter.getData().get(position).isSelected());
                        changeButtonSelectStatus();
                        view.setSelected(invalidateListAdapter.getData().get(position).isSelected());
                        break;
                }
            }
        });

        //猜你喜欢
        recommendListView = findViewById(R.id.collection_recommend_list);
        View recommendHeader = layoutInflater.inflate(R.layout.collection_recommend_header, null);
        recommendListAdapter = new PersonGoodsItemAdapter(true);

        recommendListAdapter.addHeaderView(recommendHeadView);
        recommendListAdapter.addHeaderView(recommendHeader);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false);
        recommendListView.setLayoutManager(gridLayoutManager);
        recommendListView.setAdapter(recommendListAdapter);

        recommendListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsEntity goodsEntity = recommendListAdapter.getData().get(position);
                GoodsDetailActivity.Companion.start(mContext, goodsEntity.get_id(), goodsEntity.getItem_source(), goodsEntity);
                SndoData.reportGoods(goodsEntity, position, SndoData.PLACE.collection_guessyoulike.name());
            }
        });
        recommendListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMoreData();
            }
        }, recommendListView);
        recommendListAdapter.setLoadMoreView(new CommonLoadingMoreView());
    }

    /**
     * 展示正常的数据
     */
    private void showNormalStatus() {
        emptyBar.setVisibility(View.GONE);
        tv_operator.setVisibility(View.VISIBLE);
        collectionListView.setVisibility(View.VISIBLE);
        invalidateListView.setVisibility(View.VISIBLE);
    }

    /**
     * 删除全部商品后的状态
     */
    private void showEmptyStatus() {
        emptyBar.setVisibility(View.VISIBLE);
        bottomBar.setVisibility(View.GONE);
        tv_operator.setVisibility(View.GONE);
        collectionListView.setVisibility(View.GONE);
        invalidateListView.setVisibility(View.GONE);
        collectionListAdapter.setEditable(false);
        invalidateListAdapter.setEditable(false);
        tv_operator.setText("管理");
    }

    /**
     * 全部不选中
     */
    private void unSelectedAll() {
        tv_del.setEnabled(false);
        List<CollectionGoodsEntity> collectionListAdapterData = collectionListAdapter.getData();
        for (int i = 0; i < collectionListAdapterData.size(); i++) {
            collectionListAdapterData.get(i).setSelected(false);
        }
        collectionListAdapter.notifyDataSetChanged();
        List<CollectionGoodsEntity> invalidateListAdapterData = invalidateListAdapter.getData();
        for (int i = 0; i < invalidateListAdapterData.size(); i++) {
            invalidateListAdapterData.get(i).setSelected(false);
        }
        tvSel.setSelected(false);
        collectionListAdapter.notifyDataSetChanged();
        invalidateListAdapter.notifyDataSetChanged();
    }

    /**
     * 全部选中
     */
    private void selectedAll() {
        tv_del.setEnabled(true);
        List<CollectionGoodsEntity> collectionListAdapterData = collectionListAdapter.getData();
        for (int i = 0; i < collectionListAdapterData.size(); i++) {
            collectionListAdapterData.get(i).setSelected(true);
        }
        collectionListAdapter.notifyDataSetChanged();
        List<CollectionGoodsEntity> invalidateListAdapterData = invalidateListAdapter.getData();
        for (int i = 0; i < invalidateListAdapterData.size(); i++) {
            invalidateListAdapterData.get(i).setSelected(true);
        }
        collectionListAdapter.notifyDataSetChanged();
        invalidateListAdapter.notifyDataSetChanged();
        tvSel.setSelected(true);
    }

    /**
     * 选中状态改变
     */
    private void changeButtonSelectStatus() {
        boolean isSelectedAll = false;
        boolean isEnableDelete = tv_del.isEnabled();

        ArrayList<CollectionGoodsEntity> data = new ArrayList<>();
        data.addAll(collectionListAdapter.getData());
        data.addAll(invalidateListAdapter.getData());
        //是否全部已经选中了
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSelected()) {
                isSelectedAll = true;
            } else {
                isSelectedAll = false;
                break;
            }
        }
        //是否全部已经选中了
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSelected()) {
                isEnableDelete = true;
                break;
            } else {
                isEnableDelete = false;

            }
        }
        tv_del.setEnabled(isEnableDelete);
        tvSel.setSelected(isSelectedAll);

    }

    /**
     * 失效宝贝数量
     *
     * @param num 失效宝贝数量
     */
    private void setOInvalidateCount(String num) {
        tv_count.setText("失效宝贝(" + num + ")");
    }


    /**
     * 展示正常返回的数据数据
     */
    private void showNormalView(List<GoodsEntity> data) {
        recommendListAdapter.setNewData(data);
    }

    /**
     * 展示正常返回的数据数据
     */
    private void addData(List<GoodsEntity> data) {
        recommendListAdapter.addData(data);
    }

    /**
     * 展示null数据
     */
    private void showEmptyView() {
        recommendListAdapter.setNewData(null);

    }

    /**
     * 展示网络或者请求错误数据
     */
    private void showErrorView() {
        recommendListAdapter.setNewData(null);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLogin(PushEvent event) {
        switch (event.getAction()) {
            case Constant.Event.LOGIN_SUCCESS:
            case Constant.Event.COLLECTION_CHANGE:
                if (!initData) { //第一次获取数据
                    initData = true;
                    refreshData();
                } else { //刷新收藏
                    goodsViewModel.doCollectionGoodsList();
                }
                break;

        }
    }


}
