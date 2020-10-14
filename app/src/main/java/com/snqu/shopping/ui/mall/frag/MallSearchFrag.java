package com.snqu.shopping.ui.mall.frag;

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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import com.android.util.ext.ToastUtil;
import com.android.util.os.NetworkUtil;
import com.android.util.text.StringUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.snqu.shopping.R;
import com.snqu.shopping.data.mall.MallClient;
import com.snqu.shopping.ui.main.frag.search.SoftKeyBoardListener;
import com.snqu.shopping.ui.mall.viewmodel.MallViewModel;

/**
 * 搜索
 */
public class MallSearchFrag extends SimpleFrag {
    private MallSearchPreFrag searchPreFrag;
    private MallSearchResultFrag searchResultFrag;

    private EditText et_input;
    private ImageView iv_clear;
    private MallViewModel mallViewModel;
    private View tv_search;
    private String decodeKeyword;

    public static void start(Context ctx) {
        SimpleFragAct.SimpleFragParam fragParam = new SimpleFragAct.SimpleFragParam("直供商品搜索", MallSearchFrag.class);
        SimpleFragAct.start(ctx, fragParam);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mall_search_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, true, getTitleBar());
        initView();
        initData();
    }

    private void initView() {
        getTitleBar().setBackgroundColor(Color.WHITE);

        tv_search = findViewById(R.id.tv_search);
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


        searchPreFrag = findChildFragment(MallSearchPreFrag.class);
        searchResultFrag = findChildFragment(MallSearchResultFrag.class);
        if (null == searchPreFrag) {
            searchPreFrag = new MallSearchPreFrag();
            searchResultFrag = new MallSearchResultFrag();
            loadMultipleRootFragment(R.id.search_container_layout, 0,
                    searchPreFrag, searchResultFrag
            );
        }
    }

    private void initData() {
        mallViewModel = ViewModelProviders.of(this).get(MallViewModel.class);
    }

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
            MallClient.addSearchHistory(keyword);
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
        decodeKeyword = keyword;
        showResultFrag(keyword);
    }

    private void showResultFrag(String keyword) {

        setKeyword = true;

        showHideFragment(searchResultFrag, searchPreFrag);
        et_input.setText(keyword);
        et_input.setSelection(keyword.length());
        iv_clear.setVisibility(View.INVISIBLE);

        et_input.clearFocus();
        hideSoftInput();

        searchResultFrag.startSearch(keyword);
    }

}
