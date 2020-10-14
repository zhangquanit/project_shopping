package com.snqu.shopping.ui.mine.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.AccountInfoEntity;
import com.snqu.shopping.data.user.entity.FansEntity;
import com.snqu.shopping.data.user.entity.FansQueryParam;
import com.snqu.shopping.ui.login.vm.UserViewModel;
import com.snqu.shopping.ui.main.view.CommonLoadingMoreView;
import com.snqu.shopping.ui.main.view.FansStatusView;
import com.snqu.shopping.ui.main.view.MyTeamFilterView;
import com.snqu.shopping.ui.mine.adapter.FansListAdapter;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 邀请明细
 *
 * @author 张全
 */
public class FansListFrag extends SimpleFrag {
    private static final String PARAM = "fansEntity";
    private static final String PARAM_PAGE = "nextPage";
    private SmartRefreshLayout refreshLayout;
    private UserViewModel userViewModel;
    private FansQueryParam queryParam = new FansQueryParam();
    private FansListAdapter adapter;
    private FansStatusView loadingStatusView;
    private MyTeamFilterView filterView;
    private FansEntity fansEntity;
    private View view;
    private boolean nextPage;
    public static int pageIndex = 0;
    private TextView label1, label2, label3, label4, label5, label6, label7;
    private static final String ACTION = "FANS_CLOSE_PAGE";
    private int level;

    public static void start(Context ctx, FansEntity fansEntity, boolean nextPage) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM, fansEntity);
        bundle.putBoolean(PARAM_PAGE, nextPage);

        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("邀请明细", FansListFrag.class, bundle);
        param.mutliPage = true;
        SimpleFragAct.start(ctx, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fans_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        pageIndex++;
        level = UserClient.getUser().level;
        nextPage = getArguments().getBoolean(PARAM_PAGE, false);
        fansEntity = (FansEntity) getArguments().getSerializable(PARAM);
        queryParam.uid = fansEntity._id;

        addAction(ACTION);
        StatusBar.setStatusBar(getActivity(), true, getTitleBar());
        getTitleBar().setBackgroundColor(Color.WHITE);
        initView();
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        if (TextUtils.equals(event.getAction(), ACTION)) {
            //分类-查看更多
            close();
        }
    }

    private void initView() {
        getTitleBar().setRightBtnDrawable(R.drawable.close_b);
        getTitleBar().setOnRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PushEvent(ACTION));
            }
        });
        refreshLayout = findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                queryParam.page = 1;
                userViewModel.doAccountInfo(queryParam.uid);
                loadData();
            }
        });

        view = LayoutInflater.from(mContext).inflate(R.layout.fans_list_header, null);

        filterView = view.findViewById(R.id.filterview);
        filterView.setOnItemClickListener(new MyTeamFilterView.OnItemClickListener() {
            @Override
            public void filtrate() {
            }

            @Override
            public void onFilter(FansQueryParam.QuerySort sort) {
                queryParam.page = 1;
                queryParam.sort = sort;
                loadData();
            }
        });

        if (!TextUtils.isEmpty(fansEntity.status) && TextUtils.equals(fansEntity.status, "-1")) {
            view.findViewById(R.id.icon_user_cancel).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.icon_user_cancel).setVisibility(View.GONE);
        }


        label1 = view.findViewById(R.id.label1);
        label2 = view.findViewById(R.id.label2);
        label3 = view.findViewById(R.id.label3);
        label4 = view.findViewById(R.id.label4);
        label5 = view.findViewById(R.id.label5);
        label6 = view.findViewById(R.id.label6);
        label7 = view.findViewById(R.id.label7);


//        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/withdrawal_font.ttf");
////        label1.setTypeface(typeface);
//        label2.setTypeface(typeface);
//        label3.setTypeface(typeface);
//        label4.setTypeface(typeface);
//        label5.setTypeface(typeface);
//        label6.setTypeface(typeface);
//        label7.setTypeface(typeface);


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new FansListAdapter();

        adapter.addHeaderView(view);
        recyclerView.setAdapter(adapter);
        if (nextPage) {
            adapter.showArrow();
        } else {
            adapter.hideArrow();
        }
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                if (nextPage) {
//                    boolean next = true;
//                    if (level == 3 && pageIndex == 2) { //超级会员只能看3页
//                        next = false;
//                    }
//                    fansEntity = (FansEntity) adapter.getData().get(position);
//                    FansListFrag.start(mContext, fansEntity, next);
//                } else {
//                    ToastUtil.show("升级后可查看详细数据");
//                }
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
        loadingStatusView.showInvite = false;
        loadingStatusView.tv_invite.setVisibility(View.GONE);
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

    }

    private void initData() {
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getDataResult().observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                refreshLayout.finishRefresh(true);
                if (TextUtils.equals(netReqResult.tag, ApiHost.USER_FANS_LIST)) {
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
                } else if (TextUtils.equals(netReqResult.tag, ApiHost.ACCOUNT_INFO)) { //当前粉丝信息
                    if (netReqResult.successful) {
                        AccountInfoEntity accountInfoEntity = (AccountInfoEntity) netReqResult.data;
                        setData(accountInfoEntity);
                    } else {
//                        ToastUtil.show(netReqResult.message);
                    }
                }
            }
        });
        userViewModel.doAccountInfo(queryParam.uid);
        loadData();
    }

    private void loadData() {
        userViewModel.getFansList(queryParam);
    }

    private void setData(AccountInfoEntity balanceEntity) {

        GlideUtil.loadPic(view.findViewById(R.id.item_img), balanceEntity.getAvatar(), R.drawable.icon_default_head, R.drawable.icon_default_head);

        label1.setText(NumberUtil.INSTANCE.saveTwoPoint(balanceEntity.getToday_estimate())); //今日预估
        label2.setText(NumberUtil.INSTANCE.saveTwoPoint(balanceEntity.getYesterday_estimate()));//昨日预估
        label3.setText(NumberUtil.INSTANCE.saveTwoPoint(balanceEntity.getLastmonth_estimate()));//上月预估收益
        label4.setText(NumberUtil.INSTANCE.saveTwoPoint(balanceEntity.getUnsettled_amount()));//总收益
        label7.setText(NumberUtil.saveTwoPoint(balanceEntity.getLastmonth_total())); //上月结算预估

        label5.setText(getOrderSpan(balanceEntity.getValid_order_count_total(), balanceEntity.getInvalid_order_count_total()));
        label6.setText(getOrderSpan(balanceEntity.getVaild_direct_vip(), balanceEntity.getInvaild_direct_vip()));

        ImageView imageView = view.findViewById(R.id.iv_vip);
        FansListAdapter.setVipText(fansEntity.level, imageView);
        TextView tv_phone = view.findViewById(R.id.item_phone);
        if (!TextUtils.isEmpty(fansEntity.phone)) {
            tv_phone.setText(getSpanText("注册手机 ", fansEntity.phone));
        } else {
            tv_phone.setText("注册手机 ");
        }

        TextView tv_tip = view.findViewById(R.id.tv_tip);
        if (!TextUtils.isEmpty(fansEntity.copy_helptext)) {
            tv_tip.setVisibility(View.VISIBLE);
            int d7 = DeviceUtil.dip2px(getContext(), 5);
            SpannableStringBuilder stringBuilder = new SpanUtils()
                    .appendImage(R.drawable.team_fans_item_notice, SpanUtils.ALIGN_CENTER)
                    .appendSpace(d7)
                    .append(fansEntity.copy_helptext).setForegroundColor(Color.parseColor("#C6C6C6"))
                    .create();
            tv_tip.setText(stringBuilder);
        }


        View tv_recent = view.findViewById(R.id.tv_recent);
        tv_recent.setVisibility(TextUtils.equals(fansEntity.recent, "1") ? View.VISIBLE : View.GONE);

        TextView item_copy = view.findViewById(R.id.item_copy);
        if (TextUtils.equals(fansEntity.can_copy, "1")) {
            item_copy.setVisibility(View.VISIBLE);
            item_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText(null, fansEntity.phone);
                        clipboardManager.setPrimaryClip(clipData);
                        ToastUtil.show("复制成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.show("复制失败");
                    }
                }
            });
        } else {
            item_copy.setVisibility(View.GONE);
        }

        TextView item_nickname = view.findViewById(R.id.item_nickname);
        item_nickname.setText(balanceEntity.getUsername());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
        TextView item_time = view.findViewById(R.id.item_time);

        if (null == balanceEntity.getItime() || balanceEntity.getItime() == 0) {
            item_time.setText("");
        } else {
            item_time.setText(getSpanText("注册时间 ", dateFormat.format(balanceEntity.getItime() * 1000)));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pageIndex--;
    }

    private SpannableStringBuilder getSpanText(String text1, String text2) {
        return new SpanUtils().append(text1).setForegroundColor(Color.parseColor("#A5A5A6"))
                .append(text2).setForegroundColor(Color.parseColor("#25282D"))
                .create();
    }

    private SpannableStringBuilder getOrderSpan(long validOrder, long invalidOrder) {
        return new SpanUtils()
                .append(validOrder + "/").setForegroundColor(Color.parseColor("#25282D"))
                .append(invalidOrder + "").setForegroundColor(Color.parseColor("#D5D5D5"))
                .create();
    }


}
