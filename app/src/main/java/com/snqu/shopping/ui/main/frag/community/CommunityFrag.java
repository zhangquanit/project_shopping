package com.snqu.shopping.ui.main.frag.community;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;

import com.android.util.os.DeviceUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.ui.StatusBar;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.home.entity.PlateEntity;
import com.snqu.shopping.data.user.entity.Watermark;
import com.snqu.shopping.ui.login.vm.UserViewModel;
import com.snqu.shopping.ui.main.MainActivity;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.util.RefreshFragmentAdapter;
import com.snqu.shopping.util.statistics.task.TaskInfo;
import com.snqu.shopping.util.statistics.ui.TaskProgressView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import common.widget.viewpager.ViewPager;
import common.widget.viewpager.indicator.TabPageIndicator;
import common.widget.viewpager.indicator.TitleIndicator;

/**
 * 社区
 *
 * @author 张全
 */
public class CommunityFrag extends SimpleFrag {
    @BindView(R.id.tabs)
    TabPageIndicator tabPageIndicator;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.loadingview)
    LoadingStatusView loadingStatusView;
    @BindView(R.id.taskProgressView)
    TaskProgressView taskProgressView;

    private HomeViewModel mHomeViewModel;
    private List<PlateEntity> plateList;
    //    private List<View> lines = new ArrayList<>();
    private List<TextView> textViews = new ArrayList<>();
    private UserViewModel userViewModel;
    private RefreshFragmentAdapter adapter;
    private boolean reset;

    @Override
    protected int getLayoutId() {
        return R.layout.community_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this, mView);
        addAction(Constant.Event.LOGIN_SUCCESS);
        initView();
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        if (TextUtils.equals(event.getAction(), Constant.Event.LOGIN_SUCCESS)) {
            //登录或退出登录 刷新
            reset = true;
            if (null != viewPager && null != viewPager.getAdapter()) {
                plateList = null;
                tabPageIndicator.setCurrentItem(0);
                viewPager.setAdapter(null);
            }
        }
    }

    private void initView() {
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingStatusView.setStatus(LoadingStatusView.Status.LOADING);
                loadData();
            }
        });
    }

    private void loadData() {
        mHomeViewModel.getCommunityPlate();
    }

    private void initData() {
        mHomeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getDataResult().observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, ApiHost.GET_USER_WATERMARK)) {
                    if (netReqResult.successful) {
                        if (netReqResult.data != null) {
                            Watermark watermark = (Watermark) netReqResult.data;
                            if (!TextUtils.isEmpty(watermark.watermark) && watermark.enabled == 1) {
                                Constant.water_name = watermark.watermark;
                            }
                        }
                    }
                }
            }
        });
        mHomeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(HomeViewModel.TAG_COMMUNITY_PLATE, netReqResult.tag)) {
                    if (netReqResult.successful) {
                        loadingStatusView.setVisibility(View.GONE);
                        List<PlateEntity> dataList = (List<PlateEntity>) netReqResult.data;
                        initCategoryList(dataList);
                    } else {
                        loadingStatusView.setStatus(LoadingStatusView.Status.FAIL);
                    }
                }
            }
        });

    }

    private void initCategoryList(List<PlateEntity> dataList) {

        plateList = dataList;
        List<String> titleList = getTitleList();
        List<Fragment> frags = getFrags();

        if (null != adapter) {
            adapter.setFragments(titleList, frags);
//            tabPageIndicator.setCurrentItem(0);
            viewPager.setAdapter(adapter);
            return;
        } else {
            adapter = new RefreshFragmentAdapter(getChildFragmentManager(), titleList, frags);
            viewPager.setAdapter(adapter);
        }


        viewPager.setOffscreenPageLimit(dataList.size());

        LayoutInflater inflater = LayoutInflater.from(mContext);

        int d20 = DeviceUtil.dip2px(mContext, 25);

        tabPageIndicator.setInterval(d20);
        tabPageIndicator.setTitleInidcator(new TitleIndicator() {
            @Override
            public View addTab(int index, CharSequence title) {
                View view = inflater.inflate(R.layout.community_tab_title_layout, null);
                TextView textView = view.findViewById(R.id.tv_title);
                textView.setText(title);
                View line = view.findViewById(R.id.line);
                line.setVisibility(View.INVISIBLE);

                textViews.add(textView);
                if (index == 0) {
                    textView.setSelected(true);
                    textView.setTextSize(16);
                    textView.getPaint().setFakeBoldText(true);

                }
                return view;
            }
        });


        tabPageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int pos, float v, int i1) {

            }

            @Override
            public void onPageSelected(int pos) {

                for (TextView view : textViews) {
                    view.setSelected(false);
                    view.setTextSize(14);
                    view.getPaint().setFakeBoldText(false);
                }


                TextView textView = textViews.get(pos);
                textView.setSelected(true);
                textView.setTextSize(16);
                textView.getPaint().setFakeBoldText(true);
            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });

        tabPageIndicator.setViewPager(viewPager);
        viewPager.setCurrentItem(0);
    }


    private List<String> getTitleList() {
        List<String> titleList = new ArrayList<>();
        for (PlateEntity plateEntity : plateList) {
            titleList.add(plateEntity.title);
        }
        return titleList;
    }

    private List<Fragment> getFrags() {
        List<Fragment> frags = new ArrayList<>();
        for (PlateEntity plateEntity : plateList) {
            if (TextUtils.equals(plateEntity.code, Constant.CODE_SXY)) { //商学院
                CommunitySchoolFrag communitySchoolFrag = new CommunitySchoolFrag();
                communitySchoolFrag.setArguments(CommunitySchoolFrag.getParam(plateEntity));
                communitySchoolFrag.setData(plateEntity);
                frags.add(communitySchoolFrag);
            } else if (TextUtils.equals(plateEntity.code, Constant.CODE_TJB)) { //推荐
                CommunityListFrag categoryFrag = new CommunityRecommendFrag();
                categoryFrag.setArguments(CommunityListFrag.getParam(plateEntity));
                categoryFrag.setData(plateEntity);
                frags.add(categoryFrag);
            } else {
                CommunityListFrag categoryFrag = new CommunityListFrag();
                categoryFrag.setArguments(CommunityListFrag.getParam(plateEntity));
                categoryFrag.setData(plateEntity);
                frags.add(categoryFrag);
            }
        }
        return frags;
    }

    private class CommonunityPagerAdapter extends FragmentPagerAdapter {
        public CommonunityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            PlateEntity plateEntity = plateList.get(position);
            if (TextUtils.equals(plateEntity.code, Constant.CODE_SXY)) { //商学院
                CommunitySchoolFrag communitySchoolFrag = new CommunitySchoolFrag();
                communitySchoolFrag.setArguments(CommunitySchoolFrag.getParam(plateEntity));
                communitySchoolFrag.setData(plateEntity);
                return communitySchoolFrag;
            } else if (TextUtils.equals(plateEntity.code, Constant.CODE_TJB)) { //推荐
                CommunityListFrag categoryFrag = new CommunityRecommendFrag();
                categoryFrag.setArguments(CommunityListFrag.getParam(plateEntity));
                categoryFrag.setData(plateEntity);
                return categoryFrag;
            } else {
                CommunityListFrag categoryFrag = new CommunityListFrag();
                categoryFrag.setArguments(CommunityListFrag.getParam(plateEntity));
                categoryFrag.setData(plateEntity);
                return categoryFrag;
            }

        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return plateList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return plateList.get(position).title;
        }
    }

    @Override
    public void restorePage() {
        StatusBar.setStatusBar(mContext, true);
        loadData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {

            userViewModel.getUserWatermark();

            StatusBar.setStatusBar(mContext, true);
            if (reset || null == plateList || null == viewPager.getAdapter()) {
                reset = false;
                loadData();
            }

            TaskInfo taskInfo = MainActivity.taskInfo;
            MainActivity.taskInfo = null;
            if (null != taskInfo) {
                taskProgressView.setTaskInfo(taskInfo);
            }
        } else {
            MainActivity.taskInfo = null;
            if (null != taskProgressView) taskProgressView.setVisibility(View.GONE);
        }

    }
}
