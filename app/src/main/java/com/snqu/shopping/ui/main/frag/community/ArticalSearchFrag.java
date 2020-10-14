package com.snqu.shopping.ui.main.frag.community;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.android.util.text.StringUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.home.entity.artical.ArticalEntity;
import com.snqu.shopping.ui.main.adapter.ArticalListAdapter;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.RecycleViewScrollToTop;

public class ArticalSearchFrag extends SimpleFrag {
    private EditText et_search;

    private HomeViewModel mHomeViewModel;
    private ArticalListAdapter articalListAdapter;
    private LoadingStatusView loadingStatusView;
    private View holder;
    private int page = 1;
    private String keyword;

    public static void start(Context ctx, String name) {
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam(name, ArticalSearchFrag.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.artical_search_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, true, getTitleBar());

        getTitleBar().setBackgroundColor(Color.WHITE);


        holder = findViewById(R.id.holder);
        et_search = findViewById(R.id.input);
        View iv_clear_input = findViewById(R.id.iv_clear_input);
        iv_clear_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText(null);
                page = 1;
                articalListAdapter.setNewData(null);
                loadingStatusView.setStatus(LoadingStatusView.Status.EMPTY);
                iv_clear_input.setVisibility(View.INVISIBLE);
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_SEARCH || arg1 == EditorInfo.IME_ACTION_DONE || arg1 == EditorInfo.IME_ACTION_GO) {
                    if (TextUtils.isEmpty(StringUtil.trim(textView))) {
                        ToastUtil.show("请输入搜索关键字");
                    } else {
                        hideSoftInput();
                        keyword = textView.getText().toString().trim();
                        page = 1;
                        loadData();
                    }
                    return true;
                }
                return false;
            }
        });
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    articalListAdapter.setNewData(null);
                    loadingStatusView.setStatus(LoadingStatusView.Status.EMPTY);
                    iv_clear_input.setVisibility(View.INVISIBLE);
                } else {
                    iv_clear_input.setVisibility(View.VISIBLE);
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        articalListAdapter = new ArticalListAdapter(mContext);
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


        initData();
    }

    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_ARTICAL_SEARCH: //热门搜索
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

                        if (null == articalListAdapter.getData() || articalListAdapter.getData().isEmpty()) {
                            holder.setVisibility(View.INVISIBLE);
                        } else {
                            holder.setVisibility(View.VISIBLE);
                        }

                        break;
                }
            }
        });

    }

    private void loadData() {
        if (null == articalListAdapter.getEmptyView()) {
            articalListAdapter.setEmptyView(loadingStatusView);
        }
        if (page == 1) {
            loadingStatusView.setStatus(LoadingStatusView.Status.LOADING);
        }
        mHomeViewModel.searchArticals(keyword, page);
    }
}
