package com.snqu.shopping.ui.mine.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.android.util.text.StringUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.home.entity.VipTaskEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.AccountInfoEntity;
import com.snqu.shopping.data.user.entity.FansEntity;
import com.snqu.shopping.data.user.entity.FansQueryParam;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.data.user.entity.UserFansEntity;
import com.snqu.shopping.ui.goods.util.JumpUtil;
import com.snqu.shopping.ui.login.vm.UserViewModel;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.view.FansDialogView;
import com.snqu.shopping.ui.main.view.FansStatusView;
import com.snqu.shopping.ui.main.view.TeamFilterView;
import com.snqu.shopping.ui.main.view.TipDialogView;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.ui.mine.adapter.MyTeamDialogAdapter;
import com.snqu.shopping.ui.mine.adapter.MyTeamFansListAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.widget.dialog.EffectDialogBuilder;
import common.widget.dialog.loading.LoadingDialog;

/**
 * 我的团队
 *
 * @author 张全
 */
public class MyTeamFragment extends SimpleFrag {
    TextView tv_fans_onetwo;
    TextView tv_fans_one;
    TextView tv_fans_two;
    TextView tv_today;
    TextView tv_yesterday;
    TextView tv_month;
    TextView tv_lastmonth;
    TeamFilterView filterView;
    EditText et_input;
    RecyclerView recyclerView;
    TextView tv_invite;
    View iv_clear;
    SmartRefreshLayout refreshLayout;
    TextView tv_vaild_direct_vip, tv_vaild_indirect_vip;

    private UserViewModel userViewModel;
    private HomeViewModel homeViewModel;
    private FansQueryParam queryParam = new FansQueryParam();
    private List<String> pickerList = new ArrayList<>();
    private MyTeamFansListAdapter adapter;

    private FansStatusView loadingStatusView;
    private Map<String, AccountInfoEntity> accountMap = new HashMap<>();
    private FansEntity fansEntity;
    private boolean hide = true;

    public static void start(Context ctx) {
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("我的粉丝", MyTeamFragment.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.my_team_fragment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        UserClient.verifyInviter(getActivity());
        StatusBar.setStatusBar(getActivity(), true, getTitleBar());
        getTitleBar().setBackgroundColor(Color.WHITE);
        addAction(Constant.Event.MY_TEAM_CLICK);
        queryParam.fans = null;
        queryParam.sort = FansQueryParam.QuerySort.TIME_DOWN;
        initView();
        initData();
    }


    private void initView() {
        pickerList.add("全部");
        pickerList.add("专属粉丝");
        pickerList.add("其他粉丝");

        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                userViewModel.getUserFans();
                queryParam.page = 1;
                loadData();
            }
        });

        findViewById(R.id.tv_team_orders).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamIncomeFrag.start(mContext);
            }
        });
//        findViewById(R.id.tv_xlt_orders).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                XltIncomeFrag.start(mContext);
//            }
//        });


        View view = LayoutInflater.from(mContext).inflate(R.layout.myteam_header, null);


        tv_fans_onetwo = view.findViewById(R.id.tv_fans_onetwo);
        tv_fans_one = view.findViewById(R.id.tv_fans_one);
        tv_fans_two = view.findViewById(R.id.tv_fans_two);
        tv_today = view.findViewById(R.id.tv_today);
        tv_yesterday = view.findViewById(R.id.tv_yesterday);
        tv_month = view.findViewById(R.id.tv_month);
        tv_lastmonth = view.findViewById(R.id.tv_lastmonth);


        view.findViewById(R.id.rl_fans_one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TipDialogView tipDialogView = new TipDialogView(mContext, "专属粉丝", "通过你直接邀请的用户");
                new EffectDialogBuilder(mContext)
                        .setContentView(tipDialogView)
                        .show();
            }
        });

        view.findViewById(R.id.rl_fans_two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TipDialogView tipDialogView = new TipDialogView(mContext, "其他粉丝", "除专属粉丝以外的其他粉丝");
                new EffectDialogBuilder(mContext)
                        .setContentView(tipDialogView)
                        .show();
            }
        });

        View rl_vip_member = view.findViewById(R.id.rl_vip_member);
        tv_vaild_direct_vip = view.findViewById(R.id.tv_vaild_direct_vip);
        tv_vaild_indirect_vip = view.findViewById(R.id.tv_vaild_indirect_vip);
        TextView tv_collepsed = view.findViewById(R.id.tv_collepsed);
        int d7 = DeviceUtil.dip2px(mContext, 7);
        SpannableStringBuilder stringBuilder = new SpanUtils().append("查看更多数据").setForegroundColor(Color.parseColor("#FF8228"))
                .appendSpace(d7).appendImage(R.drawable.myteam_arrow_down, SpanUtils.ALIGN_CENTER)
                .create();
        tv_collepsed.setText(stringBuilder);
        tv_collepsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hide) {
                    rl_vip_member.setVisibility(View.VISIBLE);
                    SpannableStringBuilder stringBuilder = new SpanUtils().append("收起数据").setForegroundColor(Color.parseColor("#848487"))
                            .appendSpace(d7).appendImage(R.drawable.myteam_arrow_up, SpanUtils.ALIGN_CENTER)
                            .create();
                    tv_collepsed.setText(stringBuilder);

                } else {
                    rl_vip_member.setVisibility(View.GONE);
                    SpannableStringBuilder stringBuilder = new SpanUtils().append("查看更多数据").setForegroundColor(Color.parseColor("#FF8228"))
                            .appendSpace(d7).appendImage(R.drawable.myteam_arrow_down, SpanUtils.ALIGN_CENTER)
                            .create();
                    tv_collepsed.setText(stringBuilder);
                }
                hide = !hide;
            }
        });


        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/withdrawal_font.ttf");
        tv_fans_onetwo.setTypeface(typeface);
        tv_fans_one.setTypeface(typeface);
        tv_fans_two.setTypeface(typeface);
        tv_today.setTypeface(typeface);
        tv_yesterday.setTypeface(typeface);
        tv_month.setTypeface(typeface);
        tv_lastmonth.setTypeface(typeface);
        tv_vaild_direct_vip.setTypeface(typeface);
        tv_vaild_indirect_vip.setTypeface(typeface);

        iv_clear = view.findViewById(R.id.iv_clear_input);
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_input.setText(null);
                queryParam.search = null;
                queryParam.page = 1;
                loadData();
            }
        });
        et_input = view.findViewById(R.id.input);
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    iv_clear.setVisibility(View.INVISIBLE);
                } else {
                    iv_clear.setVisibility(View.VISIBLE);
                }
            }
        });
        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int arg1, KeyEvent arg2) {
                if (arg1 == EditorInfo.IME_ACTION_SEARCH || arg1 == EditorInfo.IME_ACTION_DONE || arg1 == EditorInfo.IME_ACTION_GO) {
                    if (TextUtils.isEmpty(StringUtil.trim(textView))) {
                        ToastUtil.show("请输入搜索关键字");
                    } else {
                        String keyword = textView.getText().toString().trim();
                        queryParam.search = keyword;
                        queryParam.page = 1;
                        userViewModel.getFansList(queryParam);
                        hideSoftInput();
                    }
                    return true;
                }
                return false;
            }
        });

        filterView = view.findViewById(R.id.filterview);
        filterView.setOnItemClickListener(new TeamFilterView.OnItemClickListener() {
            @Override
            public void filtrate() {
                showPicker();
            }

            @Override
            public void onFilter(FansQueryParam.QuerySort sort) {
                queryParam.page = 1;
                queryParam.sort = sort;
                loadData();
            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new MyTeamFansListAdapter();
        adapter.addHeaderView(view);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                fansEntity = (FansEntity) adapter.getData().get(position);
//                if (accountMap.containsKey(fansEntity.username) && accountMap.get(fansEntity.username) != null) { //存在缓存
//                    showFansBalance(accountMap.get(fansEntity.username));
//                } else {
//                    showLoading();
//                    userViewModel.doAccountInfo(fansEntity._id);
//                }
                FansListFrag.pageIndex = 0;
                FansListFrag.start(mContext, fansEntity, UserClient.getUser().level >= 3);
            }
        });

        adapter.setLoadMoreView(new CommonLoadingMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, recyclerView);

        loadingStatusView = view.findViewById(R.id.statusView);
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        loadingStatusView.setInviteClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvitateFrag.start(mContext);
            }
        });


    }

    private void loadData() {
        userViewModel.getFansList(queryParam);
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
                if (options1 == 0) {
                    queryParam.fans = "";
                } else if (options1 == 1) {
                    queryParam.fans = "0";
                } else if (options1 == 2) {
                    queryParam.fans = "1";
                }
                queryParam.page = 1;
                loadData();
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

    private void initData() {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        userViewModel.getDataResult().observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                refreshLayout.finishRefresh(true);
                if (TextUtils.equals(netReqResult.tag, ApiHost.USER_FANS)) {
                    if (netReqResult.successful) {
                        UserFansEntity fansEntity = (UserFansEntity) netReqResult.data;
                        setData(fansEntity);
                    } else {
                    }

                } else if (TextUtils.equals(netReqResult.tag, ApiHost.USER_FANS_LIST)) {
                    if (netReqResult.successful) {
                        List<FansEntity> dataList = (List<FansEntity>) netReqResult.data;
                        if (queryParam.page == 1) {
                            adapter.setNewData(dataList);
                        } else if (!dataList.isEmpty()) {
                            adapter.addData(dataList);
                        }

                        if (dataList.size() >= queryParam.row) {
                            queryParam.page++;
                            adapter.loadMoreComplete(); //刷新成功
                        } else {
                            adapter.loadMoreEnd(queryParam.page == 1);//无下一页
                        }

                        if (queryParam.page == 1 && dataList.isEmpty()) { //第一页 无数据
                            FansStatusView.FansLoadingStatus status = null;
//                            if (TextUtils.isEmpty(queryParam.fans) && queryParam.sort == FansQueryParam.QuerySort.NONE && TextUtils.isEmpty(queryParam.search)) {
//                                status = FansStatusView.FansLoadingStatus.EMPTY_INVITE;
//                            } else {
                            status = FansStatusView.FansLoadingStatus.EMPTY;
//                            }
                            loadingStatusView.setStatus(status);
                        } else {
                            loadingStatusView.setVisibility(View.GONE);
                        }
                    } else {
                        if (queryParam.page > 1) { //加载下一页数据失败
                            adapter.loadMoreFail();
                        } else if (adapter.getData().isEmpty()) { //第一页  无数据
                            FansStatusView.FansLoadingStatus status = FansStatusView.FansLoadingStatus.FAIL;
                            loadingStatusView.setStatus(status);
                        } else { //下拉刷新失败
                            ToastUtil.show(netReqResult.message);
                        }
                    }
                } else if (TextUtils.equals(netReqResult.tag, ApiHost.ACCOUNT_INFO)) {
                    cancelLoading();
                    if (netReqResult.successful) {
                        AccountInfoEntity accountInfoEntity = (AccountInfoEntity) netReqResult.data;
                        accountMap.put(accountInfoEntity.getUsername(), accountInfoEntity);
                        showFansBalance(accountInfoEntity);
                    } else {
                        ToastUtil.show(netReqResult.message);
                    }
                }
            }
        });

        homeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(ApiHost.VIP_TASKS, netReqResult.tag)) {
                    cancelLoading();
                    if (netReqResult.successful) {
                        VipTaskEntity vipTaskEntity = (VipTaskEntity) netReqResult.data;
                        if (vipTaskEntity == null) {
                            return;
                        }
                        View dialogView = View.inflate(mContext, R.layout.myteam_fans_item_dialog, null);
                        RecyclerView itemList = dialogView.findViewById(R.id.item_list);
                        itemList.setLayoutManager(new LinearLayoutManager(getActivity()));
                        TextView item_title = dialogView.findViewById(R.id.item_title);
                        TextView item_desc = dialogView.findViewById(R.id.item_desc);
                        item_title.setText(vipTaskEntity.process_explain);

                        item_desc.setText(vipTaskEntity.explain);

                        if (vipTaskEntity.event_rules != null && vipTaskEntity.event_rules.size() > 0) {
                            itemList.setAdapter(new MyTeamDialogAdapter(vipTaskEntity.event_rules));
                        }
                        final Dialog dialog = new EffectDialogBuilder(mContext)
                                .setContentView(dialogView)
                                .show();
                        dialogView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            }
                        });
                    } else {
                        ToastUtil.show(netReqResult.message);
                    }
                }
            }
        });

        userViewModel.getUserFans();
        loadData();
    }

    private void setData(UserFansEntity fansEntity) {
        tv_fans_onetwo.setText(fansEntity.fans_all);
        tv_fans_one.setText(fansEntity.fans_one);
        tv_fans_two.setText(fansEntity.fans_other);
        tv_today.setText(fansEntity.today);
        tv_yesterday.setText(fansEntity.yesterday);
        tv_month.setText(fansEntity.month);
        tv_lastmonth.setText(fansEntity.lastmonth);
        tv_vaild_direct_vip.setText(String.valueOf(fansEntity.vaild_direct_vip));
        tv_vaild_indirect_vip.setText(String.valueOf(fansEntity.vaild_indirect_vip));
    }

    private void showFansBalance(AccountInfoEntity accountInfoEntity) {
        FansDialogView fansDialogView = new FansDialogView(getContext(), fansEntity, accountInfoEntity);
        new EffectDialogBuilder(getContext())
                .setContentView(fansDialogView)
                .show();
    }

    private LoadingDialog loadingDialog;

    private void showLoading() {
        loadingDialog = LoadingDialog.showDialog(mContext, "请稍候");
    }

    private void cancelLoading() {
        if (null != loadingDialog) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        accountMap.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && !getActivity().isFinishing()) {
            // 超级会员以上，且微信号为空才提示显示设置微信
            UserEntity userEntity = UserClient.getUser();
            View rlWcharLayout = findViewById(R.id.rl_wechat);
            if (userEntity.level >= 3 && TextUtils.isEmpty(userEntity.wechat_show_uid)) {
                rlWcharLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.wechat_btn).setOnClickListener(v -> MeWechatFragment.start(getActivity()));
            } else {
                rlWcharLayout.setVisibility(View.GONE);
            }
            if (rlWcharLayout.getVisibility() == View.GONE) {
                if (userEntity.level != 4) {
                    if (!TextUtils.isEmpty(userEntity.tutor_wechat_show_uid) && !TextUtils.equals(userEntity.tutor_wechat_show_uid, "null")) {
                        findViewById(R.id.rl_wechat_teacher).setVisibility(View.VISIBLE);
                        findViewById(R.id.rl_wechat_teacher).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText(null, userEntity.tutor_wechat_show_uid);
                                    clipboardManager.setPrimaryClip(clipData);
                                    ToastUtil.show("微信号复制成功");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ToastUtil.show("复制失败");
                                }
                                try {
                                    JumpUtil.openWechat(getActivity());
                                }catch(Exception e){
                                    e.printStackTrace();
                                    ToastUtil.show("打开微信失败，请确认是否安装微信客户端");
                                }
                            }
                        });
                    }
                }
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent pushEvent) {
        if (TextUtils.equals(pushEvent.getAction(), Constant.Event.MY_TEAM_CLICK)) {
            if (getActivity() != null && !getActivity().isFinishing()) {
                FansEntity item = (FansEntity) pushEvent.getData();
                showLoading();
                if (!TextUtils.isEmpty(item._id)) {
                    homeViewModel.getVipTasks(item._id);
                } else {
                    ToastUtil.show("用户数据错误");
                }
            }
        } else if (TextUtils.equals(pushEvent.getAction(), Constant.Event.MY_TEAM_ITEM_CLICK)) {
            if (getActivity() != null && !getActivity().isFinishing()) {
                int position = (int) pushEvent.getData();
                fansEntity = adapter.getData().get(position);
                FansListFrag.pageIndex = 0;
                FansListFrag.start(mContext, fansEntity, UserClient.getUser().level >= 3);
            }
        }
    }
}
