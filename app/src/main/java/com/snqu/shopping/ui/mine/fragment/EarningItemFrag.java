package com.snqu.shopping.ui.mine.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.util.ext.ToastUtil;
import com.anroid.base.SimpleFrag;
import com.blankj.utilcode.util.SpanUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.user.entity.EarningEnity;
import com.snqu.shopping.data.user.entity.SelfEarningEntity;
import com.snqu.shopping.ui.login.vm.UserViewModel;
import com.snqu.shopping.ui.main.view.TipDialogView;
import com.snqu.shopping.util.NumberUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import common.widget.dialog.EffectDialogBuilder;

/**
 * 团队收益
 *
 * @author 张全
 */
public class EarningItemFrag extends SimpleFrag {
    @BindView(R.id.tv_total)
    TextView tv_total;
    @BindView(R.id.tv_value1)
    TextView tv_value1;
    @BindView(R.id.tv_value2)
    TextView tv_value2;
    @BindView(R.id.tv_value3)
    TextView tv_value3;
    @BindView(R.id.tv_value4)
    TextView tv_value4;
    @BindView(R.id.tv_value5)
    TextView tv_value5;
    @BindView(R.id.tv_value6)
    TextView tv_value6;
    @BindView(R.id.tv_value7)
    TextView tv_value7;
    @BindView(R.id.tv_value8)
    TextView tv_value8;
    @BindView(R.id.tv_today_value1)
    TextView tv_today_value1;
    @BindView(R.id.tv_today_value2)
    TextView tv_today_value2;
    @BindView(R.id.tv_yesterday_value1)
    TextView tv_yesterday_value1;
    @BindView(R.id.tv_yesterday_value2)
    TextView tv_yesterday_value2;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout smartRefreshLayout;

    private static final String PARAM = "TYPE";
    private int type;
    private UserViewModel userViewModel;

    /**
     * @param value 0 团队 1自购
     */
    public static Bundle getParam(int value) {
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM, value);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.earning_team;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        type = getArguments().getInt(PARAM);
        ButterKnife.bind(this, mView);
        initView();
        initData();
    }

    private void initView() {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/withdrawal_font.ttf");
        tv_total.setTypeface(typeface);
        tv_value1.setTypeface(typeface);
        tv_value2.setTypeface(typeface);
        tv_value3.setTypeface(typeface);
        tv_value4.setTypeface(typeface);
        tv_value5.setTypeface(typeface);
        tv_value6.setTypeface(typeface);
        tv_value7.setTypeface(typeface);
        tv_value8.setTypeface(typeface);
        tv_today_value1.setTypeface(typeface);
        tv_today_value2.setTypeface(typeface);
        tv_yesterday_value1.setTypeface(typeface);
        tv_yesterday_value2.setTypeface(typeface);

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadData();
            }
        });
    }

    private void initData() {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getDataResult().observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                smartRefreshLayout.finishRefresh(netReqResult.successful);
                if (TextUtils.equals(netReqResult.tag, ApiHost.EARNING_TEAM)) { //团队
                    if (netReqResult.successful) {
                        EarningEnity earningEnity = (EarningEnity) netReqResult.data;
                        setData(earningEnity);
                    } else {
                        ToastUtil.show(netReqResult.message);
                    }
                } else if (TextUtils.equals(netReqResult.tag, ApiHost.EARNING_SELF)) { //自购
                    if (netReqResult.successful) {
                        SelfEarningEntity earningEnity = (SelfEarningEntity) netReqResult.data;
                        setSelfData(earningEnity);
                    } else {
                        ToastUtil.show(netReqResult.message);
                    }
                }
            }
        });
        loadData();
    }

    private void loadData() {
        if (type == 0) { //团购
            userViewModel.getTeamEaring();
        } else { //自购
            userViewModel.getSelfEaring();
        }
    }

    /**
     * 团队收益
     * @param earningEnity
     */
    private void setData(EarningEnity earningEnity) {
        tv_total.setText(NumberUtil.saveTwoPoint(earningEnity.amount_total));
        tv_value1.setText(NumberUtil.saveTwoPoint(earningEnity.nowmonth_commission));
        tv_value2.setText(NumberUtil.saveTwoPoint(earningEnity.lastmonth_commission));
        tv_value3.setText(NumberUtil.saveTwoPoint(earningEnity.nowmonth_estimate_commission));
        tv_value4.setText(NumberUtil.saveTwoPoint(earningEnity.lastmonth_estimate_commission));
        tv_value5.setText(NumberUtil.saveTwoPoint(earningEnity.nowmonth_reward));
        tv_value6.setText(NumberUtil.saveTwoPoint(earningEnity.lastmonth_reward));
        tv_value7.setText(getSpanText(earningEnity.nowmonth_team_valid_order , earningEnity.nowmonth_team_invalid_order));
        tv_value8.setText(getSpanText(earningEnity.lastmonth_team_valid_order, earningEnity.lastmonth_team_invalid_order));
        tv_today_value1.setText(earningEnity.today_team_valid_order + "");
        tv_today_value2.setText(NumberUtil.saveTwoPoint(earningEnity.today_estimate_commission));
        tv_yesterday_value1.setText(earningEnity.yesterday_team_valid_order + "");
        tv_yesterday_value2.setText(NumberUtil.saveTwoPoint(earningEnity.yesterday_estimate_commission));
    }

    /**
     * 自购收益
     * @param earningEnity
     */
    private void setSelfData(SelfEarningEntity earningEnity) {
        tv_total.setText(NumberUtil.saveTwoPoint(earningEnity.amount_total));
        tv_value1.setText(NumberUtil.saveTwoPoint(earningEnity.nowmonth_total));
        tv_value2.setText(NumberUtil.saveTwoPoint(earningEnity.lastmonth_total));
        tv_value3.setText(NumberUtil.saveTwoPoint(earningEnity.nowmonth_estimate));
        tv_value4.setText(NumberUtil.saveTwoPoint(earningEnity.lastmonth_estimate));
        tv_value5.setText(NumberUtil.saveTwoPoint(earningEnity.nowmonth_reward));
        tv_value6.setText(NumberUtil.saveTwoPoint(earningEnity.lastmonth_reward));
        tv_value7.setText(getSpanText(earningEnity.nowmonth_valid_order_count , earningEnity.nowmonth_invalid_order_count));
        tv_value8.setText(getSpanText(earningEnity.lastmonth_valid_order_count ,earningEnity.lastmonth_invalid_order_count));
        tv_today_value1.setText(earningEnity.today_valid_order_count + "");
        tv_today_value2.setText(NumberUtil.saveTwoPoint(earningEnity.today_estimate));
        tv_yesterday_value1.setText(earningEnity.yesterday_valid_order_count + "");
        tv_yesterday_value2.setText(NumberUtil.saveTwoPoint(earningEnity.yesterday_estimate));
    }

    private SpannableStringBuilder getSpanText(long validOrder, long invalidOrder){
        return new SpanUtils()
                .append(validOrder+" / ").setForegroundColor(Color.parseColor("#25282D"))
                .append(invalidOrder+"").setForegroundColor(Color.parseColor("#C9C9C9"))
                .create();
    }

    @OnClick({R.id.tv_estimate, R.id.tv_estimate2, R.id.tv_estimate3})
    public void onClick(View view) {
        String title = "";
        StringBuffer sb = new StringBuffer();
        TipDialogView tipDialogView = null;
        switch (view.getId()) {
            case R.id.tv_estimate:
                title = "预估收入说明";
                sb.append("本月结算预估：").append("\n").append("本月内已到账的订单预估收益").append("\n")
                        .append("上月结算预估：").append("\n").append("上个月内已到账的订单预估收益").append("\n")
                        .append("本月付款预估：").append("\n").append("本月内已付款的订单预估收益").append("\n")
                        .append("上月付款预估：").append("\n").append("上个月内已付款的订单预估收益").append("\n")
                        .append("本月奖励金额:").append("\n").append("本月获得的所有奖励金额总额").append("\n")
                        .append("上月奖励金额:").append("\n").append("上个月获得的所有奖励金额总额").append("\n");
                tipDialogView = new TipDialogView(mContext, title, sb.toString());
                tipDialogView.setEmite();
                break;
            case R.id.tv_estimate2:  //今日预估
                title = "今日数据说明";
                sb.append("付款笔数：").append("\n")
                        .append("今日所有付款的订单数量，只包含有效订单。").append("\n")
                        .append("预计收入：").append("\n")
                        .append("今日内创建的有效订单预估返利。");
                tipDialogView = new TipDialogView(mContext, title, sb.toString());
                break;
            case R.id.tv_estimate3: //昨日预估
                title = "昨日数据说明";
                sb.append("付款笔数：").append("\n")
                        .append("昨日所有付款的订单数量，只包含有效订单。").append("\n")
                        .append("预计收入：").append("\n")
                        .append("昨日内创建的有效订单预估返利。");
                tipDialogView = new TipDialogView(mContext, title, sb.toString());
                break;
        }


        new EffectDialogBuilder(mContext)
                .setContentView(tipDialogView)
                .show();
    }
}
