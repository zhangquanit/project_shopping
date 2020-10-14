package com.snqu.shopping.ui.main.frag.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.LazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.home.entity.PlateEntity;
import com.snqu.shopping.data.home.entity.artical.ArticalCategoryEntity;
import com.snqu.shopping.data.home.entity.artical.ArticalEntity;
import com.snqu.shopping.ui.main.adapter.ArticalCategoryAdapter;
import com.snqu.shopping.ui.main.adapter.ArticalListAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.RecycleViewScrollToTop;

import java.util.List;

/**
 * 商学院
 */
public class CommunitySchoolFrag extends LazyFragment {
    private static final String PARAM = "PLATE";
    private HomeViewModel mHomeViewModel;
    private ArticalListAdapter articalListAdapter;
    private LoadingStatusView loadingStatusView;
    private SmartRefreshLayout smartRefreshLayout;
    private ArticalCategoryAdapter articalCategoryAdapter;
    private int page = 1;
    private PlateEntity plateEntity;


    public static Bundle getParam(PlateEntity plateEntity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM, plateEntity);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.community_school_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        plateEntity = (PlateEntity) getArguments().getSerializable(PARAM);
        initView();
        initData();
    }

    private void initView() {
        smartRefreshLayout = findViewById(R.id.refreshlayout);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                loadCategoryData();
                loadData();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        //header
        View header = LayoutInflater.from(mContext).inflate(R.layout.community_school_head, null);

        header.findViewById(R.id.search_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArticalSearchFrag.start(mContext, plateEntity.title);
            }
        });

        RecyclerView rv_category = header.findViewById(R.id.rv_category);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
        rv_category.setLayoutManager(gridLayoutManager);

        articalCategoryAdapter = new ArticalCategoryAdapter();
        rv_category.setAdapter(articalCategoryAdapter);
        articalCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticalListFrag.start(mContext, articalCategoryAdapter.getItem(position));
            }
        });

        articalListAdapter = new ArticalListAdapter(mContext);
        articalListAdapter.addHeaderView(header);
        recyclerView.setAdapter(articalListAdapter);


        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        articalListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArticalEntity articalEntity = articalListAdapter.getData().get(position);
                CommonUtil.jumpToArticalDetial(getActivity(), articalEntity);
            }
        });
        articalListAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                ArticalEntity articalEntity = articalListAdapter.getItem(position);
                if (view.getId() == R.id.item_copy) {
                    CommonUtil.shareArtical(getActivity(), articalEntity);
                }
            }
        });

        articalListAdapter.setLoadMoreView(new CommonLoadingMoreView());
        articalListAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, recyclerView);

        loadingStatusView = new LoadingStatusView(mContext);
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        articalListAdapter.setEmptyView(loadingStatusView);
    }

    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                smartRefreshLayout.finishRefresh(netReqResult.successful);
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_ARTICAL_LIST: //列表
                        if (netReqResult.successful) {
                            ResponseDataArray<ArticalEntity> goodsData = (ResponseDataArray<ArticalEntity>) netReqResult.data;
                            if (page == 1) {
                                articalListAdapter.setNewData(goodsData.getDataList());
                            } else if (!goodsData.getDataList().isEmpty()) {
                                articalListAdapter.addData(goodsData.getDataList());
                            }

                            if (goodsData.hasMore()) {
                                page++;
                                articalListAdapter.loadMoreComplete(); //刷新成功
                            } else {
                                articalListAdapter.loadMoreEnd(page == 1);//无下一页
                            }

                            if (page == 1 && goodsData.getDataList().isEmpty()) { //第一页 无数据
                                LoadingStatusView.Status status = LoadingStatusView.Status.EMPTY;
                                loadingStatusView.setStatus(status);
                                loadingStatusView.setText("暂无数据");
                            }

                        } else {
                            if (page > 1) { //加载下一页数据失败
                                articalListAdapter.loadMoreFail();
                            } else if (articalListAdapter.getData().isEmpty()) { //第一页  无数据
                                LoadingStatusView.Status status = LoadingStatusView.Status.FAIL;
                                loadingStatusView.setStatus(status);
                            } else { //下拉刷新失败
                                ToastUtil.show(netReqResult.message);
                            }
                        }
                        break;
                    case HomeViewModel.TAG_ARTICAL_CATEGORY: //热门分类
                        if (netReqResult.successful) {
                            List<ArticalCategoryEntity> dataList = (List<ArticalCategoryEntity>) netReqResult.data;
                            articalCategoryAdapter.setNewData(dataList);
                        }
                        break;
                }
            }
        });

    }

    private void loadData() {
        mHomeViewModel.getArticalList(plateEntity._id, page);
    }

    private void loadCategoryData() {
        mHomeViewModel.getArticalCategorys(plateEntity._id);
    }

    public void setData(PlateEntity plateEntity) {
        this.plateEntity = plateEntity;
    }

    @Override
    public void onFirstInit() {
        page = 1;
        loadData();
        loadCategoryData();
    }

    @Override
    public void onLazyResume() {

    }

    @Override
    public void onLazyPause() {

    }


}
