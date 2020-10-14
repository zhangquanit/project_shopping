package com.snqu.shopping.ui.main.frag.search;

import android.os.Bundle;

import com.anroid.base.SimpleFrag;
import com.snqu.shopping.R;

/**
 * 搜索结果
 *
 * @author 张全
 */
public class SearchResultFrag extends SimpleFrag {
    SearchGoodsResultFrag searchGoodsResultFrag;
    SearchShopResultFrag searchShopResultFrag;

    @Override
    protected int getLayoutId() {
        return R.layout.search_result_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        searchGoodsResultFrag = new SearchGoodsResultFrag();
        searchShopResultFrag = new SearchShopResultFrag();
        loadMultipleRootFragment(R.id.result_container, 0,
                searchGoodsResultFrag, searchShopResultFrag
        );
    }

    /**
     * 显示商品结果
     */
    public void showGoodsResult(String keyword, String good_id, String item_source) {
        showHideFragment(searchGoodsResultFrag, searchShopResultFrag);
        searchGoodsResultFrag.startSearch(keyword, good_id,item_source);
    }

    /**
     * 显示商铺结果
     */
    public void showShopResult(String keyword, String item_source) {
        showHideFragment(searchShopResultFrag, searchGoodsResultFrag);
        searchShopResultFrag.startSearch(keyword,item_source);
    }

}
