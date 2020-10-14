package com.snqu.shopping.ui.main.frag.search;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.SimpleFrag;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.data.home.entity.AdvertistEntity;
import com.snqu.shopping.data.home.entity.HotSearchWord;
import com.snqu.shopping.data.home.entity.SearchSlugEntity;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.statistics.SndoData;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author 张全
 */
public class SearchPreFrag extends SimpleFrag {
    View searchingBar;
    View search_content;
    TextView tv_goods;
    TextView tv_shop;
    View tv_goods_line, tv_shop_line;
    HomeViewModel mHomeViewModel;
    TagFlowLayout fl_history;
    TagFlowLayout fl_hot;

    SearchSlugListAdapter searchSlugListAdapter;

    @BindView(R.id.iv_clear)
    ImageView iv_clear;
    @BindView(R.id.iv_guide)
    ImageView iv_guide;

    @Override
    protected int getLayoutId() {
        return R.layout.search_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this, mView);
        initView();
        initData();
    }

    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        mHomeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                switch (netReqResult.tag) {
                    case HomeViewModel.TAG_SEARCH_HOTWORD: //热门搜索
                        if (netReqResult.successful) {
                            ResponseDataArray<HotSearchWord> hotSearchWords = (ResponseDataArray<HotSearchWord>) netReqResult.data;
                            List<HotSearchWord> dataList = hotSearchWords.getDataList();
                            HomeClient.saveHotWords(dataList);
                            initHotWords(dataList);
                        } else {
                            ToastUtil.show(netReqResult.message);
                        }
                        break;
                    case HomeViewModel.TAG_AD:
                        if (netReqResult.successful) {
                            ResponseDataArray<AdvertistEntity> responseDataArray = (ResponseDataArray<AdvertistEntity>) netReqResult.data;
                            List<AdvertistEntity> adList = responseDataArray.getDataList();
                            Constant.searchAdEntity = adList;
                            setAd(adList);
                        }

                        break;
                }
            }
        });
        mHomeViewModel.getHotwords();

        if (null == Constant.searchAdEntity) {
            mHomeViewModel.getAdList("20001");
        } else {
            setAd(Constant.searchAdEntity);
        }

        List<HotSearchWord> localHotwords = HomeClient.getLocalHotwords();
        initHotWords(localHotwords);
    }

    Disposable disposable;

    private void searchSlug(String search) {
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
        }
        disposable = HomeClient.searchSlugList(search)
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

    private void initView() {
        tv_goods = findViewById(R.id.tv_goods);
        tv_shop = findViewById(R.id.tv_shop);
        tv_goods_line = findViewById(R.id.tv_goods_line);
        tv_shop_line = findViewById(R.id.tv_shop_line);
        //选中
        tv_goods.setSelected(true);
        tv_goods_line.setVisibility(View.VISIBLE);

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
        fl_hot = findViewById(R.id.fl_hot);
        fl_history = findViewById(R.id.fl_history);
        refreshSearchHistory();
    }

    public void refreshSearchHistory() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        List<String> searchHistory = HomeClient.getSearchHistory();
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
                HomeClient.addSearchHistory(searchHistory.get(position));
                showResultFrag(searchHistory.get(position));
                refreshSearchHistory();
                return true;
            }
        });
    }

    private void initHotWords(List<HotSearchWord> hotWords) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        fl_hot.setAdapter(new TagAdapter(hotWords) {
            @Override
            public View getView(FlowLayout parent, int position, Object o) {
                TextView view = (TextView) inflater.inflate(R.layout.search_label_item, null);
                view.setBackgroundResource(R.drawable.bg_hot_search);
                HotSearchWord hotSearchWord = hotWords.get(position);
//                hotSearchWord.back_color = "#121211";
//                hotSearchWord.font_color= "#ff8202";
//                view.setBackgroundResource(R.drawable.bg_hot_search);
//                view.setTextColor(Color.parseColor("#FFFF8202"));
                view.setText(hotWords.get(position).key);
                if (!TextUtils.isEmpty(hotSearchWord.font_color)) {
                    view.setTextColor(Color.parseColor(hotSearchWord.font_color));
                }
                if (!TextUtils.isEmpty(hotSearchWord.back_color)) {
                    GradientDrawable myGrad = (GradientDrawable) view.getBackground();
                    myGrad.setColor(Color.parseColor(hotSearchWord.back_color));
                }
                return view;
            }
        });
        fl_hot.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                HotSearchWord hotSearchWord = hotWords.get(position);
                showResultFrag(hotSearchWord.key);
                mHomeViewModel.hotwordClick(hotSearchWord._id); //点击汇报
                return true;
            }
        });
    }

    public void setAd(List<AdvertistEntity> tipAdEntities) {
        if (null == tipAdEntities || tipAdEntities.isEmpty()) {
            iv_guide.setVisibility(View.GONE);
            return;
        }
        AdvertistEntity advertistEntity = tipAdEntities.get(0);
        if (TextUtils.isEmpty(advertistEntity.image)) {
            return;
        }

        iv_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.startWebFrag(getContext(), advertistEntity);
                SndoData.event(SndoData.XLT_EVENT_HOME_COURSE);
            }
        });
        GlideUtil.loadBitmap(getContext(), advertistEntity.image, new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                try {
                    int vw = LContext.screenWidth - DeviceUtil.dip2px(LContext.getContext(), 20);
                    int vh = (int) (bitmap.getHeight() * vw * 1.0f / (bitmap.getWidth() * 1.0f));

                    ViewGroup.LayoutParams layoutParams = iv_guide.getLayoutParams();
                    if (layoutParams.height != vh) {
                        layoutParams.height = vh;
                        iv_guide.setLayoutParams(layoutParams);
                    }
                    iv_guide.setImageBitmap(bitmap);
                    iv_guide.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showResultFrag(String label) {
        SearchFrag searchFrag = (SearchFrag) getParentFragment();
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

    public SearchType getSearchType() {
        return SearchType.GOODS;
//        return tv_goods.isSelected() ? SearchType.GOODS : SearchType.SHOP;
    }

    @OnClick({R.id.iv_clear, R.id.tv_goods, R.id.tv_shop})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_clear: //清除历史搜索
                HomeClient.clearSearchHistory();
                refreshSearchHistory();
                break;
            case R.id.tv_goods:
                tv_goods.setSelected(true);
                tv_shop.setSelected(false);
                tv_goods_line.setVisibility(View.VISIBLE);
                tv_shop_line.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_shop:
                tv_goods.setSelected(false);
                tv_shop.setSelected(true);
                tv_goods_line.setVisibility(View.INVISIBLE);
                tv_shop_line.setVisibility(View.VISIBLE);
                break;
        }
    }

    public static enum SearchType {
        GOODS,
        SHOP;
    }
}
