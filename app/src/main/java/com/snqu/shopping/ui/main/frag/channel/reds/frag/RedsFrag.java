package com.snqu.shopping.ui.main.frag.channel.reds.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.anroid.base.BaseFragment;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.snqu.shopping.R;
import com.snqu.shopping.common.ui.BottomBar;
import com.snqu.shopping.common.ui.BottomBarTab;
import com.snqu.shopping.data.home.entity.PlateCode;
import com.snqu.shopping.ui.main.frag.search.SearchFrag;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.statistics.StatisticInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 红人街
 */
public class RedsFrag extends SimpleFrag {
    private BottomBar mNavigationView;
    private List<BaseFragment> mFragmentList = new ArrayList<>();
    private List<String> codeList;

    public static void start(Context ctx) {
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("", RedsFrag.class).hideTitleBar(true).showBg());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.reds_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, false);
        initView();
        mFragmentList.clear();
        codeList = new ArrayList<>();
        codeList.add(PlateCode.RED_ONLINE);
        codeList.add(PlateCode.RED_BIGV);
//        codeList.add(PlateCode.RED_SHOP);
        codeList.add(PlateCode.RED_GOODS);

        if (null == findChildFragment(OnlineCelebrityFrag.class)) {
            BaseFragment onlineCelebrityFrag = new OnlineCelebrityFrag();
            mFragmentList.add(onlineCelebrityFrag);

            BaseFragment bigVFrag = new BigVFrag();
            mFragmentList.add(bigVFrag);

//            BaseFragment redsShopFrag = new RedsShopFrag();
//            mFragmentList.add(redsShopFrag);

            BaseFragment redsGoodsFrag = new RedsGoodsFrag();
            mFragmentList.add(redsGoodsFrag);

            loadMultipleRootFragment(R.id.container, 0,
                    onlineCelebrityFrag, bigVFrag, redsGoodsFrag
            );
        } else {
            mFragmentList.add(findChildFragment(OnlineCelebrityFrag.class));
            mFragmentList.add(findChildFragment(BigVFrag.class));
//            mFragmentList.add(findChildFragment(RedsShopFrag.class));
            mFragmentList.add(findChildFragment(RedsGoodsFrag.class));
        }
        CommonUtil.PLATE = PlateCode.RED;
        CommonUtil.PLATE_CHILD = codeList.get(0);
        new StatisticInfo().viewRedModelPage(PlateCode.RED, codeList.get(0));
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFrag.start(mContext);
            }
        });
        initBottomBar();
    }

    public void initBottomBar() {
        mNavigationView = findViewById(R.id.bottom_bar);
        mNavigationView
                .addItem(new BottomBarTab(mContext, R.drawable.red_tab_online, "网红爆款"))
                .addItem(new BottomBarTab(mContext, R.drawable.red_tab_bigv, "大V推荐"))
//                .addItem(new BottomBarTab(mContext, R.drawable.red_tab_shop, "网红店"))
                .addItem(new BottomBarTab(mContext, R.drawable.red_tab_goods, "好物说"));
        mNavigationView.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {

            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragmentList.get(position), mFragmentList.get(prePosition));
                new StatisticInfo().viewRedModelPage(PlateCode.RED, codeList.get(position));
                CommonUtil.PLATE_CHILD = codeList.get(position);
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });
    }
}
