package com.snqu.shopping.ui.main.view;


import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.snqu.shopping.R;

/**
 * @author 张全
 */
public class CommunityLoadingMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.community_load_more;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
