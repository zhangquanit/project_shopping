package com.snqu.shopping.ui.main.frag.community;

import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.google.android.material.tabs.TabLayout;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.ui.goods.fragment.GoodRecmMySelfFrag;
import com.snqu.shopping.ui.main.adapter.CommunityRecommendListAdapter;
import com.snqu.shopping.ui.main.view.CommunityLoadingMoreView;

/**
 * 全民推荐
 *
 * @author 张全
 */
public class CommunityRecommendFrag extends CommunityListFrag {
    private String dayType;
    private CommunityRecommendListAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.commuity_recommend_frag;
    }

    @Override
    protected void initTab() {
        TabLayout tabLayout = findViewById(R.id.tabs);
        addTab(tabLayout, "实时推荐榜");
        addTab(tabLayout, "昨日推荐榜");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
                    dayType = "pre";
                    if (null != adapter) adapter.showOrderCount(false);
                } else {
                    if (null != adapter) adapter.showOrderCount(true);
                    dayType = null;
                }
                loadData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(0).select();

        findViewById(R.id.rl_recommend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodRecmMySelfFrag.start(mContext);
            }
        });

        if (UserClient.getUser() != null) {
            if (UserClient.getUser().level >= 3) {
                findViewById(R.id.rl_recommend).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.rl_recommend).setVisibility(View.GONE);
            }
        }
    }

    private void addTab(TabLayout tabLayout, String text) {
        TabLayout.Tab tab = tabLayout.newTab().setText(text);
        LinearLayout tabView = tab.view;
        if (tabView != null) {
            tabView.setBackgroundColor(getColor(R.color.transparent));
        }
        tabLayout.addTab(tab);
    }

    @Override
    public LoadMoreView getLoadMoreView() {
        return new CommunityLoadingMoreView();
    }

    @Override
    public CommunityRecommendListAdapter getAdapter() {
        adapter = new CommunityRecommendListAdapter(this);
        return adapter;
    }

    @Override
    public void loadData() {
        queryParam.page = 1;
        mHomeViewModel.getCommunityRecommendList(queryParam, dayType, liveData);
    }

    @Override
    public void loadMore() {
        mHomeViewModel.getCommunityRecommendList(queryParam, dayType, liveData);
    }
}
