package com.snqu.shopping.ui.main.frag.classification.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.ui.StatusBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.ui.main.frag.classification.adapter.ClassficationListAdapter;
import com.snqu.shopping.ui.main.frag.classification.adapter.ClassficationTypeAdapter;
import com.snqu.shopping.ui.main.frag.search.SearchFrag;
import com.snqu.shopping.ui.main.scan.ScanActivity;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.statistics.DataCache;
import com.snqu.shopping.util.statistics.SndoData;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类
 *
 * @author 张全
 */
public class ClassificationFrag extends SimpleFrag {
    private SmartRefreshLayout refreshLayout;
    private HomeViewModel mHomeViewModel;
    private ClassficationTypeAdapter classficationTypeAdapter;
    private ClassficationListAdapter classficationListAdapter;
    private RecyclerView listView;
    private RecyclerView classficationTypeView;

    private int selPos;
    private CategoryEntity selEntity;

    @Override
    protected int getLayoutId() {
        return R.layout.classification_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
//        addAction(Constant.Event.CLASSFICATION_ITEM);
        initView();
        initData();
    }

    private void initView() {


        refreshLayout = findViewById(R.id.refreshlayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mHomeViewModel.getAllCategorys();
            }
        });
        findViewById(R.id.search_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFrag.start(mContext);
            }
        });
        ImageView iv_scan = findViewById(R.id.iv_scan);
        iv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanActivity.Companion.start(mContext);
            }
        });

        classficationTypeView = findViewById(R.id.classfication1);
        classficationTypeView.setLayoutManager(new LinearLayoutManager(mContext));

        classficationTypeAdapter = new ClassficationTypeAdapter();
        classficationTypeView.setAdapter(classficationTypeAdapter);
        classficationTypeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CategoryEntity categoryEntity = (CategoryEntity) adapter.getData().get(position);
                switchChildList(categoryEntity, position);

                DataCache.classificationFirstCategory = categoryEntity;
                SndoData.event(SndoData.XLT_EVENT_CATEGORY,
                        SndoData.XLT_ITEM_CLASSIFY_NAME, categoryEntity.getName(),
                        SndoData.XLT_ITEM_LEVEL, categoryEntity.level+""
                );
            }
        });

        listView = findViewById(R.id.classfication_list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        classficationListAdapter = new ClassficationListAdapter();
        listView.setAdapter(classficationListAdapter);


        if (getArguments() != null && getArguments().containsKey(Constant.Event.CLASSFICATION_ITEM)) {
            if (getTitleBar() != null) {
                StatusBar.setStatusBar(getActivity(), true, getTitleBar());
//                getTitleBar().setBackgroundColor(Color.WHITE);
            }
            findViewById(R.id.rl_search).setVisibility(View.GONE);
            findViewById(R.id.v_divider).setVisibility(View.GONE);
            selEntity = (CategoryEntity) getArguments().getSerializable(Constant.Event.CLASSFICATION_ITEM);
        }
    }

    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_CATEGORY_ALL:
                        refreshLayout.finishRefresh(netReqResult.successful);
                        if (netReqResult.successful) {
                            ResponseDataArray<CategoryEntity> responseDataArray = (ResponseDataArray<CategoryEntity>) netReqResult.data;
                            List<CategoryEntity> categoryEntities = responseDataArray.getDataList();
                            List<CategoryEntity> typeCategorys = new ArrayList<>();
                            for (CategoryEntity item : categoryEntities) {
                                if (item.level == 1) {
                                    typeCategorys.add(item);
                                    constructList(item._id, item, new ArrayList<>(categoryEntities));
                                }
                            }
                            if (typeCategorys.isEmpty()) {
                                classficationTypeAdapter.setNewData(null);
                                classficationListAdapter.setNewData(null);
                            } else {
                                DataCache.classificationFirstCategory = typeCategorys.get(selPos);
                                classficationTypeAdapter.setNewData(typeCategorys);
                                classficationListAdapter.setNewData(typeCategorys.get(selPos).childList);
                            }
                            goToTarget(selEntity);
                            selEntity = null;
                        } else {
                            ToastUtil.show(netReqResult.message);
                        }
                        break;
                }
            }
        });

        mHomeViewModel.getAllCategorys();
    }

    public void constructList(String pid, CategoryEntity categoryEntity, List<CategoryEntity> categoryEntities) {
        for (CategoryEntity item : categoryEntities) {
            if (TextUtils.equals(item.pid, pid)) {
                categoryEntity.childList.add(item);
                if (categoryEntity.level == 1 && item.level == 2) { //查询三级分类
                    constructList(item._id, item, new ArrayList<>(categoryEntities));
                }
            }
        }
    }

    private void switchChildList(CategoryEntity categoryEntity, int position) {
        selPos = position;
        classficationTypeAdapter.setSelId(categoryEntity);
        //刷新右侧列表
        classficationListAdapter.setNewData(categoryEntity.childList);
        listView.scrollToPosition(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        if (TextUtils.equals(event.getAction(), Constant.Event.CLASSFICATION_ITEM)) {
            //分类-查看更多
            CategoryEntity selEntity = (CategoryEntity) event.getData();
            goToTarget(selEntity);
        }
    }

    private void goToTarget(CategoryEntity selEntity) {
        List<CategoryEntity> dataList = classficationTypeAdapter.getData();
        if (null == dataList || null == selEntity) {
            return;
        }

        String selId = selEntity._id;
        if (selEntity.level == 2) {
            selId = selEntity.pid;
        }
        for (int i = 0; i < dataList.size(); i++) {
            CategoryEntity categoryEntity = dataList.get(i);
            if (TextUtils.equals(categoryEntity._id, selId)) {
                switchChildList(categoryEntity, i);
                classficationTypeView.scrollToPosition(i);
                return;
            }
        }
    }


    @Override
    public void restorePage() {
        StatusBar.setStatusBar(mContext, true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBar.setStatusBar(mContext, true);
//            if (classficationTypeAdapter.getData().isEmpty()) mHomeViewModel.getAllCategorys();
        }

    }
}
