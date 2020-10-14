package com.snqu.shopping.ui.mine.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.entity.IncomeQueryParam;
import com.snqu.shopping.ui.main.view.IncomeFilterView;

import java.util.ArrayList;
import java.util.List;

import common.widget.viewpager.ViewPager;

/**
 * 成员贡献榜单
 *
 * @author 张全
 */
public class TeamIncomeFrag extends SimpleFrag {
    private ViewPager viewPager;
    private List<String> pickerList = new ArrayList<>();
    private List<TeamIncomePageFrag> fragList = new ArrayList<>();
    private int page;


    public static void start(Context ctx) {
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("粉丝贡献榜单", TeamIncomeFrag.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.team_income_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, true, getTitleBar());
        initView();
    }

    private void initView() {
        pickerList.add("全部");
        pickerList.add("专属粉丝");
        pickerList.add("其他粉丝");

        getTitleBar().setBackgroundColor(Color.WHITE);
        IncomeFilterView filterView = findViewById(R.id.filterview);
        filterView.setOnItemClickListener(new IncomeFilterView.OnItemClickListener() {
            @Override
            public void filtrate() {
                showPicker();
            }

            @Override
            public void onFilter(int pos, IncomeQueryParam.Sort sort) {
                page = pos;
                viewPager.setCurrentItem(pos);
                fragList.get(pos).refresh(page, sort);
            }
        });

        viewPager = findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new androidx.viewpager.widget.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int pos) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        viewPager.setAdapter(new IncomeTabPagerAdapter(getChildFragmentManager()));
    }


    private int selPos;

    private void showPicker() {
        OptionsPickerBuilder builder = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                if (selPos == options1) {
                    return;
                }
                selPos = options1;
                String relation = "";
                if (options1 == 0) {
                    relation = "";
                } else if (options1 == 1) {
                    relation = "1";
                } else if (options1 == 2) {
                    relation = "2";
                }
                fragList.get(page).refresh(relation);
            }
        });
        OptionsPickerView<String> build = builder.setCancelText("取消")//取消按钮文字
                .setSubmitText("确认")//确认按钮文字
                .setTitleSize(16)//标题文字大小
                .setTitleText("筛选")//标题文字
                .setContentTextSize(14)
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .setCyclic(false, false, false)//循环与否
                .setSubmitColor(Color.parseColor("#D22C2F"))//确定按钮文字颜色
                .setCancelColor(Color.parseColor("#333333"))//取消按钮文字颜色
                .setTitleColor(Color.parseColor("#333333"))
                .setBgColor(Color.WHITE)//滚轮背景颜色
                .setSelectOptions(selPos)
                .isCenterLabel(true) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .isDialog(false)//是否显示为对话框样式
                .build();


        build.setPicker(pickerList);
        build.show();
    }


    private class IncomeTabPagerAdapter extends FragmentStatePagerAdapter {

        public IncomeTabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TeamIncomePageFrag page = new TeamIncomePageFrag();
            page.setArguments(TeamIncomePageFrag.getParam(position));
            fragList.add(page);
            return page;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

}
