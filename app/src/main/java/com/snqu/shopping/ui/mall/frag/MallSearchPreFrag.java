package com.snqu.shopping.ui.mall.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anroid.base.SimpleFrag;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.snqu.shopping.R;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.home.entity.SearchSlugEntity;
import com.snqu.shopping.data.mall.MallClient;
import com.snqu.shopping.ui.main.frag.search.SearchFrag;
import com.snqu.shopping.ui.main.frag.search.SearchSlugListAdapter;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MallSearchPreFrag extends SimpleFrag {
    View searchingBar;
    View search_content;
    TagFlowLayout fl_history;
    SearchSlugListAdapter searchSlugListAdapter;
    ImageView iv_clear;

    @Override
    protected int getLayoutId() {
        return R.layout.mall_search_pre_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        iv_clear = findViewById(R.id.iv_clear);
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MallClient.clearSearchHistory();
                refreshSearchHistory();
            }
        });
        searchingBar = findViewById(R.id.searching_bar);
        RecyclerView recyclerView = findViewById(R.id.search_recommend_words);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInput();
                return false;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        searchSlugListAdapter = new SearchSlugListAdapter();
        recyclerView.setAdapter(searchSlugListAdapter);
        searchSlugListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SearchFrag searchFrag = (SearchFrag) getParentFragment();
                searchFrag.search(searchSlugListAdapter.getData().get(position).key);
            }
        });


        search_content = findViewById(R.id.search_content);
        fl_history = findViewById(R.id.fl_history);
        refreshSearchHistory();
    }

    public void refreshSearchHistory() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        List<String> searchHistory = MallClient.getSearchHistory();
        iv_clear.setVisibility(searchHistory.isEmpty() ? View.GONE : View.VISIBLE);
        fl_history = findViewById(R.id.fl_history);
        TagAdapter tagAdapter = new TagAdapter(searchHistory) {
            @Override
            public View getView(FlowLayout parent, int position, Object o) {
                TextView view = (TextView) inflater.inflate(R.layout.search_label_item, null);
                String keyword = searchHistory.get(position);
                if (keyword.length() > 20) {
                    keyword = keyword.substring(0, 20) + "...";
                }
                view.setText(keyword);
                return view;
            }
        };
        fl_history.setAdapter(tagAdapter);
        fl_history.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                MallClient.addSearchHistory(searchHistory.get(position));
                showResultFrag(searchHistory.get(position));
                refreshSearchHistory();
                return true;
            }
        });
    }


    Disposable disposable;

    private void searchSlug(String search) {
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = MallClient.searchSlugList(search)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseDataArray<SearchSlugEntity>>() {
                    @Override
                    public void accept(ResponseDataArray<SearchSlugEntity> response) throws Exception {
                        List<SearchSlugEntity> dataList = response.getDataList();
                        searchSlugListAdapter.setNewData(dataList);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

    }

    private void showResultFrag(String label) {
        MallSearchFrag searchFrag = (MallSearchFrag) getParentFragment();
        searchFrag.search(label);
    }

    /**
     * 正在搜索
     */

    public void showSearchingBar(String search) {
        searchingBar.setVisibility(View.VISIBLE);
        search_content.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(search)) {
            searchSlug(search);
        }
    }


    public void showSearchContentBar() {
        searchingBar.setVisibility(View.GONE);
        search_content.setVisibility(View.VISIBLE);
    }


}
