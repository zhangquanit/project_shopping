package com.snqu.shopping.ui.mall.order;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.ui.BottomInDialog;
import com.snqu.shopping.common.ui.LoadingStatusView;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.mall.entity.CommentEntity;
import com.snqu.shopping.data.mall.entity.ConvertEntity;
import com.snqu.shopping.data.mall.entity.MallOrderDetailEntity;
import com.snqu.shopping.data.mall.entity.PayResultDataEntity;
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity;
import com.snqu.shopping.data.mall.entity.address.AddressEntity;
import com.snqu.shopping.data.mall.entity.flow.FlowDetailEntity;
import com.snqu.shopping.data.mall.entity.flow.FlowEntity;
import com.snqu.shopping.ui.mall.address.AddressManagerFrag;
import com.snqu.shopping.ui.mall.frag.MallContactFrag;
import com.snqu.shopping.ui.mall.order.helper.MallCancelDialogView;
import com.snqu.shopping.ui.mall.order.helper.MallOrderType;
import com.snqu.shopping.ui.mall.viewmodel.AddressViewModel;
import com.snqu.shopping.ui.mall.viewmodel.MallViewModel;
import com.snqu.shopping.util.CommonUtil;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;
import com.snqu.shopping.util.pay.OrderPay;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.List;

import common.widget.dialog.EffectDialogBuilder;
import common.widget.dialog.loading.LoadingDialog;
import common.widget.ratingbar.CommonRationBar;

/**
 * 订单详情
 */
public class MallOrderDetailFrag extends SimpleFrag {
    private static final String PARAM_ORDER = "PARAM_ORDER";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private String orderId;
    private MallOrderDetailEntity orderEntity;
    private LoadingStatusView loadingStatusView;
    private TextView tv_title, tv_title_desc;

    private CountDownTimer countDownTimer;
    private MallViewModel mallViewModel;
    private AddressViewModel addressViewModel;

    public static void start(Context ctx, String orderId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_ORDER, orderId);
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("", MallOrderDetailFrag.class, bundle).hideTitleBar(true));

    }

    @Override
    protected int getLayoutId() {
        return R.layout.mall_order_detail_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, false);
        orderId = getArguments().getString(PARAM_ORDER);

        addAction(Constant.Event.ADDRESS_MANAGER_ITEM);
        addAction(Constant.Event.ADDRESS_UPDATE);
        addAction(Constant.Event.ORDER_BUY_SUCCESS);
        addAction(Constant.Event.ORDER_BUY_FAIL);
        addAction(Constant.Event.ORDER_BUY_CANCEL);

        initView();
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        if (TextUtils.equals(event.getAction(), Constant.Event.ADDRESS_MANAGER_ITEM)) { //更换地址
            AddressEntity addressEntity = (AddressEntity) event.getData();
            orderEntity.address = addressEntity;
            orderEntity.address.phone = addressEntity.phone_txt;
            setAddress(orderEntity.address);
        } else if (TextUtils.equals(event.getAction(), Constant.Event.ADDRESS_UPDATE)) { //编辑地址
            AddressEntity addressEntity = (AddressEntity) event.getData();
            if (null != orderEntity.address && TextUtils.equals(orderEntity.address._id, addressEntity._id)) {
                orderEntity.address = addressEntity;
                orderEntity.address.phone = addressEntity.phone_txt;
                setAddress(orderEntity.address);
            }
            addressViewModel.getAddressList();
        } else if (TextUtils.equals(event.getAction(), Constant.Event.ORDER_BUY_SUCCESS)) { //支付成功
            ToastUtil.show("支付成功");
            loadData();
        } else if (TextUtils.equals(event.getAction(), Constant.Event.ORDER_BUY_FAIL)) { //支付失败
            ToastUtil.show("支付失败");
        } else if (TextUtils.equals(event.getAction(), Constant.Event.ORDER_BUY_CANCEL)) { //取消支付

        }
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        loadingStatusView = findViewById(R.id.loadingStatusView);
        loadingStatusView.setOnBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        tv_title = findViewById(R.id.tv_title);
        tv_title_desc = findViewById(R.id.tv_title_desc);
    }

    private void setViewData(MallOrderDetailEntity orderDetailEntity) {
        this.orderEntity = orderDetailEntity;
//        orderEntity.status=MallOrderType.SH.status;

        //标题
        MallOrderType mallOrderType = MallOrderType.getMallOrder(orderEntity.status);
        Drawable drawable = getResources().getDrawable(mallOrderType.res);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        tv_title.setCompoundDrawables(drawable, null, null, null);
        tv_title.setCompoundDrawablePadding(DeviceUtil.dip2px(getContext(), DeviceUtil.dip2px(mContext, 3)));
        tv_title.setGravity(Gravity.CENTER_VERTICAL);
        tv_title.setText(mallOrderType.name);

        //联系客服
        findViewById(R.id.rl_custom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MallContactFrag.start(mContext);
            }
        });

        //特殊状态处理
        setStatus();
    }

    private void setStatus() {
        MallOrderType mallOrderType = MallOrderType.getMallOrder(orderEntity.status);

        //副标题

        tv_title_desc.setVisibility(View.INVISIBLE);

        //物流
        View rl_flow = findViewById(R.id.rl_flow); //物流
        View fl_flow_divider = findViewById(R.id.fl_flow_divider); //物流下面的分割线
        TextView tv_flow = findViewById(R.id.tv_flow);
        FlowEntity flowEntity = orderEntity.flow;

        if (null == flowEntity || null == flowEntity.detail || flowEntity.detail.isEmpty()) {
//            if (orderEntity.status == MallOrderType.FH.status) { //待发货
//                rl_flow.setVisibility(View.VISIBLE);
//                fl_flow_divider.setVisibility(View.VISIBLE);
//                tv_flow.setText("暂未发货");
//            } else {
            rl_flow.setVisibility(View.GONE);
            fl_flow_divider.setVisibility(View.GONE);
//            }
        } else {
            rl_flow.setVisibility(View.VISIBLE);
            fl_flow_divider.setVisibility(View.VISIBLE);
            FlowDetailEntity detailEntity = flowEntity.detail.get(0);
            tv_flow.setText(detailEntity.context);
        }
        rl_flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != flowEntity) MallFlowDetailFrag.start(mContext, flowEntity);
            }
        });
        //收货地址
        setAddress(orderEntity.address);

        //兑换码
        View rl_dym = findViewById(R.id.rl_dym);
        List<ConvertEntity> dymList = orderEntity.admin_user_show_in;
        if (null != dymList && !dymList.isEmpty()
                && (orderEntity.status == MallOrderType.SH.status || orderEntity.status == MallOrderType.COMPLETE.status)) { //待收货 已完成
            rl_dym.setVisibility(View.VISIBLE);
            ConvertEntity convertEntity = dymList.get(0);
            if (convertEntity.value.startsWith("http")) { //图片兑换码
                ImageView iv_dhm = findViewById(R.id.iv_dhm);
                iv_dhm.setVisibility(View.VISIBLE);
                GlideUtil.loadPic(iv_dhm, convertEntity.value, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
            } else { //文字兑换码
                TextView tv_dhm = findViewById(R.id.tv_dhm);
                tv_dhm.setVisibility(View.VISIBLE);
                tv_dhm.setText(convertEntity.value);
            }
        } else {
            rl_dym.setVisibility(View.GONE);
        }

        //商品
        List<ShopGoodsEntity> goods_info = orderEntity.goods_info;
        View rl_goodbar = findViewById(R.id.rl_goodbar);
        if (null == goods_info || goods_info.isEmpty()) {
            rl_goodbar.setVisibility(View.GONE);
        } else {
            rl_goodbar.setVisibility(View.VISIBLE);
            ShopGoodsEntity goodsBean = goods_info.get(0);
            GlideUtil.loadPic(findViewById(R.id.item_img), goodsBean.getImage(), R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
            TextView item_name = findViewById(R.id.item_name);
            TextView item_standard_name = findViewById(R.id.item_standard_name);
            TextView item_price = findViewById(R.id.item_price);
            TextView item_number = findViewById(R.id.item_number);
            item_name.setText(goodsBean.name);
            item_standard_name.setText(goodsBean.standard_name);
            item_price.setText("￥" + NumberUtil.saveTwoPoint(goodsBean.selling_price));
            item_number.setText("x" + goodsBean.number);
        }
        //订单编号 下单时间 支付时间
        //订单编号
        TextView tv_orderNo = findViewById(R.id.tv_orderNo);
        tv_orderNo.setText(orderEntity._id);
        findViewById(R.id.tv_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.addToClipboard(orderId);
                ToastUtil.show("复制成功");
            }
        });
        //下单时间
        TextView tv_orderTime = findViewById(R.id.tv_orderTime);
        tv_orderTime.setText(dateFormat.format(orderEntity.order_time * 1000));
        //支付时间
        View rl_paytime = findViewById(R.id.rl_paytime);
        if (orderEntity.status == MallOrderType.FH.status
                || orderEntity.status == MallOrderType.SH.status
                || orderEntity.status == MallOrderType.COMPLETE.status) {
            rl_paytime.setVisibility(View.VISIBLE);
            TextView tv_payTime = findViewById(R.id.tv_payTime);
            tv_payTime.setText(dateFormat.format(orderEntity.pay_time * 1000));
        } else {
            rl_paytime.setVisibility(View.GONE);
        }

        //商品总额
        TextView tv_totalPrice = findViewById(R.id.tv_totalPrice);
        tv_totalPrice.setText("￥" + NumberUtil.saveTwoPoint(orderEntity.total_price));
        //支付金额
        View rl_payPrice = findViewById(R.id.rl_payPrice);
        if (orderEntity.status != MallOrderType.CANCEL.status    //已取消
                && orderEntity.status != MallOrderType.PAY.status //待支付
        ) { //取消支付
            rl_payPrice.setVisibility(View.VISIBLE);
            TextView tv_payPrice = findViewById(R.id.tv_payPrice);
            tv_payPrice.setText("￥" + NumberUtil.saveTwoPoint(orderEntity.pay_price));
        } else {
            rl_payPrice.setVisibility(View.GONE);
        }

        //备注
        View rl_note = findViewById(R.id.rl_note);
        rl_note.setVisibility(View.GONE);
        if (null != orderEntity.goods_info && !orderEntity.goods_info.isEmpty()) {
            ShopGoodsEntity goodsBean = orderEntity.goods_info.get(0);
            if (!TextUtils.isEmpty(goodsBean.getReasonText())) {
                rl_note.setVisibility(View.VISIBLE);
                TextView tv_note = findViewById(R.id.tv_note);
                tv_note.setText(goodsBean.serven_reason);
            }
        }


        //底部按钮状态
        View bottomBar = findViewById(R.id.rl_bottombar);
        bottomBar.setVisibility(View.VISIBLE);
        //收货/发货/评价
        TextView tv_operater = findViewById(R.id.tv_operater);
        tv_operater.setVisibility(View.VISIBLE);
        //支付
        View rl_paybar = findViewById(R.id.rl_paybar);
        rl_paybar.setVisibility(View.GONE);

        stopCountDownTime();
        switch (mallOrderType) {
            case CANCEL: //取消
                tv_title_desc.setVisibility(View.VISIBLE);
                tv_title_desc.setText(orderEntity.status_excuse);

                //无物流
                rl_flow.setVisibility(View.GONE);
                fl_flow_divider.setVisibility(View.GONE);

                bottomBar.setVisibility(View.GONE);

                break;
            case PAY: //待支付
                tv_title_desc.setVisibility(View.VISIBLE);
                countDownTime(); //倒计时

                rl_paybar.setVisibility(View.VISIBLE);
                tv_operater.setVisibility(View.GONE);
                findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //取消订单
                        cancelOrder();
                    }
                });
                //支付金额
                TextView tv_price = findViewById(R.id.tv_price);
                tv_price.setText("￥" + NumberUtil.saveTwoPoint(orderEntity.pay_price));
                findViewById(R.id.rl_pay).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //去支付
                        payOrder();
                    }
                });

                break;
            case FH: //待发货
                tv_operater.setText("确认收货");
                tv_operater.setOnClickListener(null);
                tv_operater.setClickable(true);
                tv_operater.setBackgroundColor(Color.parseColor("#C3C4C7"));

                break;
            case SH: //待收货
                tv_operater.setText("确认收货");
                tv_operater.setBackgroundResource(R.drawable.mall_order_bottom_btn);
                tv_operater.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmReceipt();
                    }
                });
                break;
            case COMPLETE: //已完成
                tv_operater.setBackgroundColor(Color.WHITE);
                tv_operater.setTextColor(Color.parseColor("#26282E"));
                if (null == orderEntity.comment) {
                    tv_operater.setText("评价");
                } else {
                    tv_operater.setText("已评价");
                }
                tv_operater.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentOrder();
                    }
                });
                break;
        }
    }

    private void setAddress(AddressEntity addressBean) {
        View rl_addressbar = findViewById(R.id.rl_addressbar);
        rl_addressbar.setVisibility(View.VISIBLE);
        TextView tv_contact = findViewById(R.id.tv_contact);
        TextView tv_address = findViewById(R.id.tv_address);
        if (null == addressBean) {
            if (orderEntity.status != MallOrderType.CANCEL.status) {
                tv_contact.setText(null);
                tv_address.setText(null);
            } else { //已取消
                rl_addressbar.setVisibility(View.GONE);
            }
        } else {
            tv_contact.setText(addressBean.name + " " + addressBean.phone);
            tv_address.setText("地址：" + addressBean.address_txt);
        }

        TextView tv_changeAddress = findViewById(R.id.tv_changeAddress);
        tv_changeAddress.setVisibility(View.GONE);
        if (orderEntity.status == MallOrderType.PAY.status) { //待支付 可以修改地址
            tv_changeAddress.setVisibility(View.VISIBLE);
            tv_changeAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AddressManagerFrag.startForOrder(mContext, orderEntity.address);
                }
            });

            if (null == addressBean) {
                tv_changeAddress.setText("选择地址");
            } else {
                tv_changeAddress.setText("修改地址");
            }
        }
    }

    private void initData() {
        addressViewModel = ViewModelProviders.of(this).get(AddressViewModel.class);
        addressViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, AddressViewModel.TAG_LIST)) {
                    if (netReqResult.successful) {
                        List<AddressEntity> addressList = (List<AddressEntity>) netReqResult.data;
                        if (addressList.isEmpty()) { //空列表
                            orderEntity.address = null;
                            setAddress(null);
                        } else if (null != orderEntity.address) {
                            boolean isDelete = true;
                            for (AddressEntity addressEntity : addressList) {
                                if (TextUtils.equals(orderEntity.address._id, addressEntity._id)) { //更新地址信息
                                    isDelete = false;
                                    orderEntity.address = addressEntity;
                                    orderEntity.address.phone = addressEntity.phone_txt;
                                    setAddress(orderEntity.address);
                                    break;
                                }
                            }
                            addressViewModel.updateUserAddress(addressList);
                            if (isDelete) { //当前地址被删除
                                orderEntity.address = null;
                                setAddress(null);
                            }
                        }
                    }
                }
            }
        });

        mallViewModel = ViewModelProviders.of(this).get(MallViewModel.class);
        mallViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(NetReqResult netReqResult) {
                if (TextUtils.equals(netReqResult.tag, MallViewModel.TAG_ORDER_DETAIL)) { //订单详情
                    cancelLoading();
                    if (netReqResult.successful) {
                        loadingStatusView.setVisibility(View.GONE);
                        MallOrderDetailEntity orderDetailEntity = (MallOrderDetailEntity) netReqResult.data;
                        setViewData(orderDetailEntity);
                    } else {
                        LoadingStatusView.Status status = LoadingStatusView.Status.FAIL;
                        loadingStatusView.setStatus(status);
                    }
                } else if (TextUtils.equals(netReqResult.tag, MallViewModel.TAG_ORDER_CANCEL)) {//取消订单
                    cancelLoading();
                    if (netReqResult.successful) {
                        ToastUtil.show("订单已取消");
                        loadData();
                    } else {
                        ToastUtil.show(netReqResult.message);
                    }
                } else if (TextUtils.equals(netReqResult.tag, MallViewModel.TAG_ORDER_COMMENT)) { //订单评价
                    cancelLoading();
                    if (netReqResult.successful) {
                        if (null != bottomInDialog) bottomInDialog.dismiss();
                        loadData();
                    } else {
                        ToastUtil.show(netReqResult.message);
                    }
                } else if (TextUtils.equals(netReqResult.tag, MallViewModel.TAG_ORDER_RECEIPT)) { //确认收货
                    cancelLoading();
                    if (netReqResult.successful) {
                        loadData();
                        commentOrder();
                    } else {
                        ToastUtil.show(netReqResult.message);
                    }
                } else if (TextUtils.equals(netReqResult.tag, ApiHost.MALL_ORDER_RE_PAY)) { //订单支付
                    cancelLoading();
                    if (netReqResult.successful) {
                        PayResultDataEntity dataEntity = (PayResultDataEntity) netReqResult.data;
                        new OrderPay().alipay(mContext, dataEntity.sign);
                    } else {
                        ToastUtil.show(netReqResult.message);
                    }
                }
            }
        });

        loadData();
    }

    private void loadData() {
        cancelLoading();
        LoadingStatusView.Status status = LoadingStatusView.Status.LOADING;
        loadingStatusView.setStatus(status);
        mallViewModel.orderDetail(orderId);
    }

    private void countDownTime() {
        stopCountDownTime();
        countDownTimer = new CountDownTimer((orderEntity.timeout * 1000 - System.currentTimeMillis()), 1 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    long seconds = millisUntilFinished / 1000;
                    long minute = seconds / 60;
                    long second = seconds % 60;
                    String format = String.format("请在 %02d:%02d 内支付，超时订单将自动关闭", minute, second);
                    tv_title_desc.setText(format);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                cancelOrder();
            }
        };
        countDownTimer.start();
    }

    private void stopCountDownTime() {
        if (null != countDownTimer) countDownTimer.cancel();
    }

    private void cancelOrder() {
        MallCancelDialogView dialogView = new MallCancelDialogView(mContext)
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoading();
                        mallViewModel.orderCancel(orderId);
                    }
                });

        new EffectDialogBuilder(mContext)
                .setCancelable(false)
                .setCancelableOnTouchOutside(false)
                .setContentView(dialogView).show();
    }

    /**
     * 去支付
     */
    private void payOrder() {
        if (null == orderEntity.address) {
            ToastUtil.show("请填写地址");
            return;
        }

        if (!CommonUtil.checkAliPayInstalled(mContext)) { //未安装支付宝
            ToastUtil.show(R.string.alipay_not_support);
            return;
        }

        showLoading();
        String addressId = orderEntity.address._id;
        mallViewModel.goRePay(orderEntity._id, addressId);
    }

    /**
     * 确认收货
     */
    private void confirmReceipt() {
        MallCancelDialogView mallCancelDialogView = new MallCancelDialogView(mContext)
                .setContent("已收到商品")
                .setLeftClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoading();
                        mallViewModel.orderReceipt(orderEntity._id);
                    }
                });

        new EffectDialogBuilder(mContext)
                .setContentView(mallCancelDialogView)
                .setCancelable(false)
                .setCancelableOnTouchOutside(false)
                .show();

    }

    /**
     * 订单评价
     */
    BottomInDialog bottomInDialog;

    private void commentOrder() {

        bottomInDialog = new BottomInDialog(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.mall_comment_layout, null);

        //商品
        CommonRationBar rb_goods = view.findViewById(R.id.rb_goods);
        TextView tv_good_result = view.findViewById(R.id.tv_good_result);
        rb_goods.setOnRatingBarChangeListener(new CommonRationBar.RatingBarChangeListener() {
            @Override
            public void onRatingChanged(CommonRationBar ratingBar, float rating) {
                parseRation(tv_good_result, rating);
            }
        });

        //物流
        CommonRationBar rb_flow = view.findViewById(R.id.rb_flow);
        TextView tv_flow_result = view.findViewById(R.id.tv_flow_result);
        rb_flow.setOnRatingBarChangeListener(new CommonRationBar.RatingBarChangeListener() {
            @Override
            public void onRatingChanged(CommonRationBar ratingBar, float rating) {
                parseRation(tv_flow_result, rating);
            }
        });

        //客服
        CommonRationBar rb_service = view.findViewById(R.id.rb_service);
        TextView tv_service_result = view.findViewById(R.id.tv_service_result);
        rb_service.setOnRatingBarChangeListener(new CommonRationBar.RatingBarChangeListener() {
            @Override
            public void onRatingChanged(CommonRationBar ratingBar, float rating) {
                parseRation(tv_service_result, rating);
            }
        });

        //评论
        EditText et_comment = view.findViewById(R.id.et_comment);

        View tv_cancel = view.findViewById(R.id.tv_cancel);
        View tv_sure = view.findViewById(R.id.tv_sure);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomInDialog.dismiss();
            }
        });
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float goodsRating = rb_goods.getRating();
                if (goodsRating == 0) {
                    ToastUtil.show("请给 [商品] 评分");
                    return;
                }

                float flowRating = rb_flow.getRating();
                if (flowRating == 0) {
                    ToastUtil.show("请给 [物流] 评分");
                    return;
                }

                float serviceRating = rb_service.getRating();
                if (serviceRating == 0) {
                    ToastUtil.show("请给 [客服] 评分");
                    return;
                }

                String comment = et_comment.getText().toString();

                CommentEntity commentEntity = new CommentEntity();
                commentEntity.id = orderEntity._id;
                commentEntity.goods = goodsRating;
                commentEntity.flow = flowRating;
                commentEntity.service = serviceRating;
                commentEntity.content = comment;

                showLoading();
                mallViewModel.orderComment(commentEntity);

            }
        });

        view.findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomInDialog.dismiss();
            }
        });

        CommentEntity comment = orderEntity.comment;
        if (null != comment) {
            rb_goods.setRating(comment.goods);
            rb_flow.setRating(comment.flow);
            rb_service.setRating(comment.service);
            et_comment.setText(comment.content);
            et_comment.setEnabled(false);
            et_comment.setFocusable(false);
            tv_sure.setVisibility(View.GONE);
        }

        bottomInDialog.setContentView(view);
        bottomInDialog.setCanceledOnTouchOutside(true);
        bottomInDialog.setCancelable(true);
        bottomInDialog.show();

    }

    private void parseRation(TextView textView, float ration) {
        System.out.println("ration=" + ration);
        String result = "一般";
        if (ration <= 1) {
            result = "非常差";
        } else if (ration <= 2) {
            result = "差";
        } else if (ration <= 3) {
            result = "一般";
        } else if (ration <= 4) {
            result = "满意";
        } else if (ration <= 5) {
            result = "非常满意";
        }
        textView.setText(result);
    }

    private void showLoading() {
        loadingDialog = LoadingDialog.showBackCancelableDialog(mContext, "请稍候");
    }

    private void cancelLoading() {
        if (null != loadingDialog) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCountDownTime();
    }

    private LoadingDialog loadingDialog;
}
