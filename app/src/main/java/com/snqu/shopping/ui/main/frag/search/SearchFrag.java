package com.snqu.shopping.ui.main.frag.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.util.ext.ToastUtil;
import com.android.util.os.NetworkUtil;
import com.android.util.text.StringUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.google.android.material.tabs.TabLayout;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.data.home.entity.ItemSourceEntity;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.statistics.SndoData;
import com.snqu.shopping.util.statistics.StatisticInfo;

import java.util.ArrayList;
import java.util.List;

import common.widget.dialog.loading.LoadingDialog;

/**
 * 搜索
 *
 * @author 张全
 */
public class SearchFrag extends SimpleFrag {
    SearchPreFrag searchPreFrag;
    SearchResultFrag searchResultFrag;
    EditText et_input;
    ImageView iv_clear;
    HomeViewModel mHomeViewModel;
    TabLayout searchTabs;
    View tv_search;
    private String decodeKeyword;
    LoadingDialog loadingDialog;
    private final static String SEARCH_TEXT = "SEARCH_TEXT";
    private final static String ITEM_SOURCE = "ITEM_SOURCE";
    public static String searchText;
    private String item_source;
    private List<ItemSourceEntity> dataList = new ArrayList();
    private String decodedStr;
    private GoodsEntity decodeGoodEntity;

    public static void start(Context ctx) {
        SimpleFragAct.SimpleFragParam fragParam = new SimpleFragAct.SimpleFragParam("", SearchFrag.class);
        fragParam.hideTitleBar(true);
        SimpleFragAct.start(ctx, fragParam);
    }

    public static void startFromSearch(Context ctx, String searchText, String item_source) {
        SimpleFragAct.SimpleFragParam fragParam = new SimpleFragAct.SimpleFragParam("", SearchFrag.class);
        fragParam.hideTitleBar(true);
        fragParam.mutliPage = true;
        Bundle bundle = new Bundle();
        if (!TextUtils.isEmpty(searchText)) {
            bundle.putString(SEARCH_TEXT, searchText);
        }
        if (!TextUtils.isEmpty(item_source)) {
            bundle.putString(ITEM_SOURCE, item_source);
        }
        fragParam.paramBundle = bundle;
        SimpleFragAct.start(ctx, fragParam);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.search_container_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(getActivity(), true);
        searchText = getArguments().getString(SEARCH_TEXT);
        item_source = getArguments().getString(ITEM_SOURCE, "");
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
                    case HomeViewModel.TAG_GOODS_DECODE_URL: //URL解析
                    case HomeViewModel.TAG_GOODS_DECODE_CODE: //淘口令
                        cancelLoading();
                        if (netReqResult.successful) {
                            decodedStr = decodeKeyword;
                            ResponseDataObject<GoodsEntity> goodsDecodeEntity = (ResponseDataObject<GoodsEntity>) netReqResult.data;
                            GoodsEntity data = goodsDecodeEntity.data;
                            if (null == data || TextUtils.isEmpty(data.getGoods_id())) {
                                showResultFrag(decodeKeyword, null);
                                return;
                            }

                            // 特殊处理天猫平台
                            if (TextUtils.equals(data.getItem_source(), Constant.BusinessType.TM)) {
                                data.setItem_source(Constant.BusinessType.TB);
                            }

                            decodeGoodEntity = data;

                            if (!searchResultFrag.isVisible()) {
                                String itemName = "";
                                for (ItemSourceEntity itemSource : dataList) {
                                    if (data.getItem_source().equals(itemSource.code)) {
                                        itemName = itemSource.name;
                                    }
                                }

                                int count = searchTabs.getTabCount();
                                for (int i = 0; i < count; i++) {
                                    TabLayout.Tab tab = searchTabs.getTabAt(i);
                                    if (tab != null && tab.getText() != null) {
                                        if (tab.getText().equals(itemName)) {
                                            tab.select();
                                            break;
                                        }
                                    }
                                }
                            }
                            showResultFrag(decodeKeyword, data.getGoods_id());
                        } else {
                            showResultFrag(decodeKeyword, null); //普通字符串
                        }
                        break;
                }
            }
        });
    }


    private void showLoading() {
//        titleBar.setBackgroundColor(Color.WHITE)
        loadingDialog = LoadingDialog.showDialog(mContext, "请稍候");
    }

    private void cancelLoading() {
        loadingDialog.dismiss();
    }

    @SuppressLint("CheckResult")
    private void initView() {

        tv_search = findViewById(R.id.tv_search);

        findViewById(R.id.title_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        iv_clear = findViewById(R.id.iv_clear_input);
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_input.setText(null);
                showPreFrag(true);
            }
        });

        et_input = findViewById(R.id.input);

        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (setKeyword) {
                    return;
                }
                searchPreFrag.showSearchingBar(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (setKeyword) {
                    setKeyword = false;
                    return;
                }

                if (TextUtils.isEmpty(s.toString())) {
                    iv_clear.setVisibility(View.INVISIBLE);
                    searchPreFrag.showSearchContentBar();
                } else {
                    iv_clear.setVisibility(View.VISIBLE);
                }
            }
        });


        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });


        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_SEARCH || arg1 == EditorInfo.IME_ACTION_DONE || arg1 == EditorInfo.IME_ACTION_GO) {
                    startSearch();
                    return true;
                }
                return false;
            }
        });

        SoftKeyBoardListener.setListener(getActivity(), new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                if (TextUtils.equals(et_input.getText().toString(), decodeKeyword) && et_input.hasFocus()) {
                    iv_clear.setVisibility(View.VISIBLE);
                    showPreFrag(true);
                }
            }

            @Override
            public void keyBoardHide(int height) {
            }
        });
        searchPreFrag = findChildFragment(SearchPreFrag.class);
        searchResultFrag = findChildFragment(SearchResultFrag.class);
        if (null == searchPreFrag) {
            searchPreFrag = new SearchPreFrag();
            searchResultFrag = new SearchResultFrag();
            loadMultipleRootFragment(R.id.search_container_layout, 0,
                    searchPreFrag, searchResultFrag
            );
        }

        if (!TextUtils.isEmpty(searchText)) {
            post(new Runnable() {
                @Override
                public void run() {
                    HomeClient.addSearchHistory(searchText);
                    searchPreFrag.refreshSearchHistory();
                    showResultFrag(searchText, null);
                }
            });
        }


        searchTabs = findViewById(R.id.search_tabs);


        List<ItemSourceEntity> itemSourceList = ItemSourceClient.getSearchItemSource();
        dataList = itemSourceList;
        if (TextUtils.isEmpty(item_source)) {
            item_source = dataList.get(0).code;
        }
        setItemSourceView(itemSourceList);
    }

    private void setItemSourceView(List<ItemSourceEntity> dataList) {

        // 大于5个tab就变为可滑动的，否则tab自动填充满。
        if (dataList.size() >= 5) {
            searchTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        } else {
            searchTabs.setTabMode(TabLayout.MODE_FIXED);
        }


        for (ItemSourceEntity it : dataList) {
            TabLayout.Tab tab = searchTabs.newTab().setText(it.name);
            // 去掉点击背景
            LinearLayout tabView = tab.view;
            if (tabView != null) {
                tabView.setBackgroundColor(getColor(R.color.transparent));
            }
            searchTabs.addTab(tab);

            if (TextUtils.equals(item_source, it.code)) {
//                if (searchResultFrag != null && searchResultFrag.isVisible()) {
                tab.select();
//                }
            }
        }


        searchTabs.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == null || tab.getText() == null) {
                    return;
                }


                for (ItemSourceEntity itemSource : dataList) {
                    if (tab.getText().toString().equals(itemSource.name)) {
                        item_source = itemSource.code;
                        break;
                    }
                }
                if (searchResultFrag.isVisible()) {
                    if (!TextUtils.isEmpty(StringUtil.trim(et_input))) {
                        String goodsId = null != decodeGoodEntity ? decodeGoodEntity.getGoods_id() : null;
                        String keyword = et_input.getText().toString().trim();
                        showResultFrag(keyword, goodsId);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

//    @OnClick({R.id.title_left_button})
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.title_left_button:
//                start(new SearchResultFrag());
//                break;
//        }
//    }

    public void showPreFrag(boolean contentbar) {
        showHideFragment(searchPreFrag, searchResultFrag);
        if (contentbar) {
            searchPreFrag.showSearchContentBar();
        } else {
            searchPreFrag.showSearchingBar(null);
        }
    }

    private void startSearch() {
        if (TextUtils.isEmpty(StringUtil.trim(et_input))) {
            ToastUtil.show("请输入搜索关键字");
        } else {
            String keyword = et_input.getText().toString().trim();
            HomeClient.addSearchHistory(keyword);
            searchPreFrag.refreshSearchHistory();
            search(keyword);
        }
    }

    private boolean setKeyword;

    public void search(String keyword) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }

        try {
            SndoData.event(SndoData.XLT_EVENT_SEARCH, SndoData.XLT_ITEM_SEARCH_KEYWORD, keyword
                    , SndoData.XLT_ITEM_SOURCE, "null"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        decodeKeyword = keyword;
        new StatisticInfo().search(keyword);

        if (searchPreFrag.getSearchType() == SearchPreFrag.SearchType.GOODS) {
            //重复搜索内容
            if (null != decodedStr && TextUtils.equals(decodeKeyword, decodedStr)) {
                //无商品
                if (null == decodeGoodEntity) {
                    showResultFrag(decodeKeyword, null);
                    return;
                }

                String itemName = "";
                for (ItemSourceEntity itemSource : dataList) {
                    if (decodeGoodEntity.getItem_source().equals(itemSource.code)) {
                        itemName = itemSource.name;
                    }
                }
                //有商品
                int count = searchTabs.getTabCount();
                for (int i = 0; i < count; i++) {
                    TabLayout.Tab tab = searchTabs.getTabAt(i);
                    if (tab != null && tab.getText() != null) {
                        if (tab.getText().equals(itemName)) {
                            tab.select();
                            break;
                        }
                    }
                }
                showResultFrag(decodeKeyword, decodeGoodEntity.getGoods_id());
                return;
            }

            decodeGoodEntity = null;
            showLoading();
//            if (keyword.startsWith("http")) {
//                mHomeViewModel.decodeGoodByUrl(keyword, 0);
//            } else {
            mHomeViewModel.decodeGoodByCode(keyword, 0, "1");
//            }

        } else {
            showResultFrag(keyword, null);
        }

    }

    private void showResultFrag(String keyword, String good_id) {

        if (!TextUtils.isEmpty(keyword)) {
            if (getArguments() != null) {
                getArguments().putString(SEARCH_TEXT, keyword);
            }
        }

        setKeyword = true;

        showHideFragment(searchResultFrag, searchPreFrag);
        et_input.setText(keyword);
        et_input.setSelection(keyword.length());
        iv_clear.setVisibility(View.INVISIBLE);

        et_input.clearFocus();
        hideSoftInput();

        if (searchPreFrag.getSearchType() == SearchPreFrag.SearchType.GOODS) {
            searchResultFrag.showGoodsResult(keyword, good_id, item_source);
        } else {
            searchResultFrag.showShopResult(keyword, item_source);
        }
    }
}
