package com.snqu.shopping.ui.vip.frag;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.blankj.utilcode.util.ConvertUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.data.home.entity.VipRightEntity;
import com.snqu.shopping.data.home.entity.VipTaskEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.ui.login.vm.UserViewModel;
import com.snqu.shopping.ui.main.frag.WebViewFrag;
import com.snqu.shopping.ui.main.viewmodel.HomeViewModel;
import com.snqu.shopping.ui.mine.fragment.InvitateFrag;
import com.snqu.shopping.ui.vip.adapter.VipGoodListAdapter;
import com.snqu.shopping.ui.vip.adapter.VipImgAdapter;
import com.snqu.shopping.ui.vip.view.VipTaskItemView;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.RecycleViewScrollToTop;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 会员
 *
 * @author 张全
 */
public class VipFrag extends SimpleFrag {
    private SmartRefreshLayout smartRefreshLayout;
    private VipGoodListAdapter vipGoodListAdapter;
    private LoadingStatusView loadingStatusView;
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private View view;
    private int page = 1;
    private VipTaskEntity vipTaskEntity;
    private UserViewModel userViewModel;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
    private boolean isDetailJump = false;
    private VipRightEntity vipRight;
    private VipImgAdapter vipImgAdapter;
    private View mRecommend;

    private static final String EXTRA_IS_DETAIL_JUMP = "EXTRA_DETAIL_JUMP";

    public static void startFromSearch(Context ctx, boolean isDetailsJump) {
        SimpleFragAct.SimpleFragParam fragParam = new SimpleFragAct.SimpleFragParam("会员", VipFrag.class);
        fragParam.hideTitleBar(true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_IS_DETAIL_JUMP, isDetailsJump);
        fragParam.paramBundle = bundle;
        SimpleFragAct.start(ctx, fragParam);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.vip_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        addAction(Constant.Event.ORDER_BUY_SUCCESS);
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        if (TextUtils.equals(event.getAction(), Constant.Event.ORDER_BUY_SUCCESS)) {
            userViewModel.doUserInfo();
            homeViewModel.getVipTasks();
        }
    }

    private void initView() {

        findViewById(R.id.tv_recommend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvitateFrag.start(mContext);
            }
        });


        smartRefreshLayout = findViewById(R.id.refresh_layout);
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                loadData();
            }
        });

        view = LayoutInflater.from(mContext).inflate(R.layout.vip_header, null);

        view.findViewById(R.id.vip_back).setOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.listview);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int h = recyclerView.computeVerticalScrollOffset();
                RelativeLayout relativeLayout = findViewById(R.id.toolbar);
                float tvAlpha = h / 255F;
                if (tvAlpha > 1) {
                    tvAlpha = 1;
                }
                relativeLayout.setAlpha(tvAlpha);
            }
        });
        RecycleViewScrollToTop.addScroolToTop(recyclerView, findViewById(R.id.scroll_to_top));

        vipGoodListAdapter = new VipGoodListAdapter();


        vipImgAdapter = new VipImgAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.rv_list_divider_10));
        recyclerView.addItemDecoration(dividerItemDecoration);
        vipImgAdapter.addHeaderView(view);
        recyclerView.setAdapter(vipImgAdapter);

        vipImgAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VipRightEntity.VipMoreImg vipMoreImg = vipImgAdapter.getData().get(position);

                if (!TextUtils.isEmpty(vipMoreImg.url)) {
                    WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                    webViewParam.url = vipMoreImg.url;
                    WebViewFrag.start(mContext, webViewParam);
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    mRecommend.setVisibility(View.VISIBLE);
                    mRecommend.startAnimation(moveToViewLocation());
                } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    if (mRecommend.getVisibility() == View.VISIBLE) {
                        TranslateAnimation translateAnimation = moveToViewBottom();
                        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mRecommend.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        mRecommend.startAnimation(moveToViewBottom());
                    }
                    mRecommend.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    private void initData() {

        mRecommend = findViewById(R.id.tv_recommend);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getDataResult().observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, ApiHost.USER_INFO)) {
                    homeViewModel.getVipRights();
                }
            }
        });

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                if(smartRefreshLayout!=null) {
                    smartRefreshLayout.finishRefresh(netReqResult.successful);
                    if (TextUtils.equals(netReqResult.tag, HomeViewModel.TAG_VIP_TASKS)) {
                        if (netReqResult.successful) {
                            vipTaskEntity = (VipTaskEntity) netReqResult.data;
                            setViewData();
                        } else {
                            ToastUtil.show("Vip任务请求失败，请刷新重试");
                        }
                    } else if (TextUtils.equals(netReqResult.tag, HomeViewModel.TAG_VIP_RIGHTS)) {
                        setViewData();
                    }
                }
            }
        });

        if (getArguments() != null && getArguments().containsKey(EXTRA_IS_DETAIL_JUMP)) {
            isDetailJump = getArguments().getBoolean(EXTRA_IS_DETAIL_JUMP, false);
            onHiddenChanged(false);
        }
    }

    private void loadData() {
        UserEntity user = UserClient.getUser();
        if (user.level < 4) {
            homeViewModel.getVipTasks();
        }
        userViewModel.doUserInfo();
    }

    private int selPos = -1;


    private void setViewData() {

        UserEntity user = UserClient.getUser();
        if (user == null) {
            return;
        }

        vipRight = HomeClient.getVipRight();


        //顶部背景
        ImageView imageView = view.findViewById(R.id.top_bg);
        if (user.level == 4) {
            imageView.setBackgroundResource(R.drawable.vip_top_bg_level4);
        } else {
            imageView.setBackgroundResource(R.drawable.vip_top_bg);
        }

        GlideUtil.loadPic(view.findViewById(R.id.iv_avater), user.avatar, R.drawable.icon_default_head, R.drawable.icon_default_head);
        TextView tv_phone = view.findViewById(R.id.tv_phone);
        tv_phone.setText(user.username);

        ImageView iv_level = view.findViewById(R.id.iv_level);

        ViewGroup rl_task = view.findViewById(R.id.rl_task);
//        ImageView rl_task_bg = view.findViewById(R.id.rl_task_bg);

        TextView tv_task_tip = view.findViewById(R.id.tv_task_tip);
        TextView tv_task_title = view.findViewById(R.id.tv_task_title);


        View iv_cover_task = view.findViewById(R.id.iv_cover_task);
//        View iv_cover_user = view.findViewById(R.id.iv_cover_user);
        TextView svip_date = view.findViewById(R.id.svip_date);
        svip_date.setText(null);

        TextView tv_tutor = view.findViewById(R.id.tv_tutor);
        if (!TextUtils.isEmpty(user.tutor_wechat_show_uid) && !TextUtils.equals(user.tutor_wechat_show_uid, "null")) {
            tv_tutor.setVisibility(View.VISIBLE);
            tv_tutor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TutorWechatFrag.start(mContext);
                }
            });
        } else {
            tv_tutor.setVisibility(View.INVISIBLE);
        }

        int levelDrawableRes = -1;
        switch (user.level) {
            case 1:
                iv_level.setImageDrawable(null);
                rl_task.setBackgroundResource(R.drawable.vip_top_level1);
//                tv_task_title.setText("升级会员进度");
                break;
            case 2:
                levelDrawableRes = R.drawable.vip_level2;
                rl_task.setBackgroundResource(R.drawable.vip_top_level2);
//                tv_task_title.setText("升级超级会员进度");
                break;
            case 3:
                levelDrawableRes = R.drawable.vip_level3;
                rl_task.setBackgroundResource(R.drawable.vip_top_level3);
//                tv_task_title.setText("升级运营总监进度");
                if (user.svip_expire > 0) {
                    String date = simpleDateFormat.format(new Date(user.svip_expire * 1000));
                    svip_date.setVisibility(View.VISIBLE);
                    svip_date.setText(date + "到期");
                } else {
                    //代表是永久会员
                    if (user.svip_expire == -1) {
                        svip_date.setVisibility(View.VISIBLE);
                        svip_date.setText("永久");
                    }
                }
                break;
            case 4:
                levelDrawableRes = R.drawable.vip_level4;
                break;
        }

        //会员等级图标
        if (levelDrawableRes != -1) {
            if (null != vipRight) {
                VipRightEntity.VipRightItem levelRight = vipRight.getLevelRight(user.level);
                if (null != levelRight) {
                    GlideUtil.loadPic(iv_level, levelRight.icon, levelDrawableRes, levelDrawableRes);
                }
            }
        }

        //任务进度
        if (user.level == 4) {
            rl_task.setVisibility(View.GONE);
//            rl_task_bg.setVisibility(View.GONE);
            iv_cover_task.setVisibility(View.GONE);
//            iv_cover_user.setVisibility(View.VISIBLE);
        } else {
            if (null == vipTaskEntity || null == vipTaskEntity.event_rules || vipTaskEntity.event_rules.isEmpty()) {
                rl_task.setVisibility(View.GONE);
//                rl_task_bg.setVisibility(View.GONE);
                iv_cover_task.setVisibility(View.GONE);
//                iv_cover_user.setVisibility(View.VISIBLE);

            } else {
                rl_task.setVisibility(View.VISIBLE);
//                rl_task_bg.setVisibility(View.VISIBLE);
                iv_cover_task.setVisibility(View.VISIBLE);
//                iv_cover_user.setVisibility(View.GONE);
                LinearLayout ll_tasks = view.findViewById(R.id.ll_tasks);
                ll_tasks.removeAllViews();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                if (null != vipTaskEntity) {
                    tv_task_tip.setText(vipTaskEntity.explain);//升级描述
                    tv_task_title.setText(vipTaskEntity.process_explain);
                    List<VipTaskEntity.Rule> event_rules = vipTaskEntity.event_rules;
//                    event_rules.addAll(event_rules);
//                    event_rules.addAll(event_rules);
                    int d13 = DeviceUtil.dip2px(mContext, 13);
                    if (null != event_rules && !event_rules.isEmpty()) {
                        for (int i = 0; i < event_rules.size(); i++) {
                            VipTaskItemView vipTaskItemView = new VipTaskItemView(mContext);
                            vipTaskItemView.setData(event_rules.get(i));
                            if (i == 0) {
                                ll_tasks.addView(vipTaskItemView);
                            } else {
                                layoutParams.topMargin = d13;
                                ll_tasks.addView(vipTaskItemView, layoutParams);
                            }
                        }
                    }
                }
            }
        }

        //等级权益

        LinearLayout rl_vip_rights = view.findViewById(R.id.rl_vip_rights);
        List<View> rlViewList = new ArrayList<>();
        rlViewList.add(view.findViewById(R.id.rl_level1));
        if (user.level != 4) {
            view.findViewById(R.id.rl_level2).setVisibility(View.VISIBLE);
            rlViewList.add(view.findViewById(R.id.rl_level2));
        } else {
            view.findViewById(R.id.rl_level2).setVisibility(View.GONE);
        }

        List<View> lineList = new ArrayList<>();
        lineList.add(view.findViewById(R.id.indictor1));
        if (user.level != 4) {
            lineList.add(view.findViewById(R.id.indictor2));
        }

        List<TextView> textList = new ArrayList<>();
        TextView nowLevelText = view.findViewById(R.id.level_text1);
        nowLevelText.setText("当前权益");
        textList.add(nowLevelText);
        TextView nextLevelText = view.findViewById(R.id.level_text2);
        if (user.level != 4) {
            textList.add(nextLevelText);
            nextLevelText.setText(CommonUtil.getVipText(user.level + 1));
        }
        for (int i = 0; i < rlViewList.size(); i++) {
            final int pos = i;
            rlViewList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selPos = pos;
                    for (View view : textList) {
                        view.setSelected(false);
                    }
                    for (View view : lineList) {
                        view.setVisibility(View.INVISIBLE);
                    }

                    textList.get(selPos).setSelected(true);

                    rlViewList.get(selPos).setSelected(true);
                    lineList.get(selPos).setVisibility(View.VISIBLE);

                    addLevel(selPos, rl_vip_rights);
                }
            });
        }

        //当前等级权益
        for (View view : textList) {
            view.setSelected(false);
        }
        for (View view : lineList) {
            view.setVisibility(View.INVISIBLE);
        }
        if (selPos == -1) {
            selPos = 0;
        }
        addLevel(selPos, rl_vip_rights);
        lineList.get(selPos).setVisibility(View.VISIBLE);
        textList.get(selPos).setSelected(true);

        if (isDetailJump) {
            view.findViewById(R.id.vip_back).setVisibility(View.VISIBLE);
        }
    }

    private void addLevel(int selPos, LinearLayout rl_vip_rights) {
        rl_vip_rights.removeAllViews();

        int level = UserClient.getUser().level;
        level = selPos == 0 ? level : level + 1;


        if (null != vipRight) {
            VipRightEntity.VipRightItem levelRight = vipRight.getLevelRight(level);
            if (null == levelRight) {
                vipImgAdapter.setNewData(null);
                return;
            }

            //权益列表
            List<VipRightEntity.VipRight> rights = levelRight.rights;
            if (null != rights && !rights.isEmpty()) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                int index = 0;

                List<VipRightEntity.VipRight> vipRights = new ArrayList<>(rights);
                while (vipRights.size() >= 2) {
                    VipRightEntity.VipRight data1 = vipRights.get(0);
                    VipRightEntity.VipRight data2 = vipRights.get(1);
                    vipRights = vipRights.subList(2, vipRights.size());

                    View view = inflater.inflate(R.layout.vip_right_item, null);
                    ImageView imageView = view.findViewById(R.id.item_img1);
                    TextView tv_item1 = view.findViewById(R.id.tv_item1);
                    TextView tv_item2 = view.findViewById(R.id.tv_item2);

                    tv_item1.setVisibility(TextUtils.isEmpty(data1.title) ? View.GONE : View.VISIBLE);
                    tv_item1.setText(data1.title);
                    tv_item2.setText(data1.subtitle);
                    GlideUtil.loadPic(imageView, data1.icon, R.drawable.vip_right_default, R.drawable.vip_right_default);


                    ImageView imageView2 = view.findViewById(R.id.item_img2);
                    TextView tv_item3 = view.findViewById(R.id.tv_item3);
                    TextView tv_item4 = view.findViewById(R.id.tv_item4);
                    tv_item3.setVisibility(TextUtils.isEmpty(data2.title) ? View.GONE : View.VISIBLE);
                    tv_item3.setText(data2.title);
                    tv_item4.setText(data2.subtitle);
                    GlideUtil.loadPic(imageView2, data2.icon, R.drawable.vip_right_default, R.drawable.vip_right_default);


                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (vipRights.size() == 0) {

                        view.setBackgroundResource(R.drawable.bg_vip_bottom_white);
//                        layoutParams1.topMargin = d10;
                        view.setPadding(ConvertUtils.dp2px(10), ConvertUtils.dp2px(10), ConvertUtils.dp2px(10), ConvertUtils.dp2px(10));
                    }
                    rl_vip_rights.addView(view, layoutParams1);
                    index++;
                }

                if (vipRights.size() == 1) {
                    VipRightEntity.VipRight data1 = vipRights.get(0);
                    View view = inflater.inflate(R.layout.vip_right_item, null);


                    ImageView imageView = view.findViewById(R.id.item_img1);
                    TextView tv_item1 = view.findViewById(R.id.tv_item1);
                    TextView tv_item2 = view.findViewById(R.id.tv_item2);

                    tv_item1.setVisibility(TextUtils.isEmpty(data1.title) ? View.GONE : View.VISIBLE);
                    tv_item1.setText(data1.title);
                    tv_item2.setText(data1.subtitle);
                    GlideUtil.loadPic(imageView, data1.icon, R.drawable.vip_right_default, R.drawable.vip_right_default);
                    view.findViewById(R.id.item_container2).setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (index > 0) {
                        view.setBackgroundResource(R.drawable.bg_vip_bottom_white);
                        view.setPadding(ConvertUtils.dp2px(10), ConvertUtils.dp2px(10), ConvertUtils.dp2px(10), ConvertUtils.dp2px(10));
                    }
                    rl_vip_rights.addView(view, layoutParams1);
                }
            }

            vipImgAdapter.setNewData(levelRight.moreimg);

        }

    }

    @Override
    public void restorePage() {
        StatusBar.setStatusBar(mContext, true);
        initViewAndData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            UserClient.verifyInviter(getActivity());
            StatusBar.setStatusBar(mContext, false);
            initViewAndData();
        }
    }

    private void initViewAndData() {
        if (null == UserClient.getUser()) return;
        if (null == view) {
            initView();
        }
        setViewData();
        loadData();
    }

    /**
     * 从控件所在位置移动到控件的底部
     *
     * @return
     */
    public static TranslateAnimation moveToViewBottom() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1f);
        mHiddenAction.setDuration(200);
        return mHiddenAction;
    }

    /**
     * 从控件的底部移动到控件所在位置
     *
     * @return
     */
    public static TranslateAnimation moveToViewLocation() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(200);
        return mHiddenAction;
    }


}
