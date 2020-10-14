package com.snqu.shopping.ui.main.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.entity.AccountInfoEntity;
import com.snqu.shopping.data.user.entity.FansEntity;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.NumberUtil;

import java.text.SimpleDateFormat;

import common.widget.dialog.DialogView;

/**
 * 粉丝收益
 *
 * @author 张全
 */
public class FansDialogView extends DialogView {
    private AccountInfoEntity balanceEntity;
    private FansEntity fansEntity;

    public FansDialogView(Context ctx, FansEntity fansEntity, AccountInfoEntity accountInfoEntity) {
        super(ctx);
        this.fansEntity = fansEntity;
        this.balanceEntity = accountInfoEntity;
    }

    @Override
    protected void initView(View view) {

        GlideUtil.loadPic(findViewById(R.id.item_img), balanceEntity.getAvatar(), R.drawable.icon_default_head, R.drawable.icon_default_head);

        TextView label1 = findViewById(R.id.label1);
        TextView label2 = findViewById(R.id.label2);
        TextView label3 = findViewById(R.id.label3);
        TextView label4 = findViewById(R.id.label4);
        TextView label5 = findViewById(R.id.label5);
        TextView label6 = findViewById(R.id.label6);


        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/withdrawal_font.ttf");
        label1.setTypeface(typeface);
        label2.setTypeface(typeface);
        label3.setTypeface(typeface);
        label4.setTypeface(typeface);
        label5.setTypeface(typeface);
        label6.setTypeface(typeface);

        label1.setText(NumberUtil.saveTwoPoint(balanceEntity.getToday_estimate())); //今日预估
        label2.setText(NumberUtil.saveTwoPoint(balanceEntity.getYesterday_estimate()));//昨日预估
        label3.setText(NumberUtil.saveTwoPoint(balanceEntity.getLastmonth_estimate()));//上月预估收益
        label4.setText(NumberUtil.saveTwoPoint(balanceEntity.getUnsettled_amount()));//总收益

        label5.setText(getOrderSpan(balanceEntity.getValid_order_count_total(), balanceEntity.getInvalid_order_count_total()));
        label6.setText(String.valueOf(balanceEntity.getVaild_direct_vip()));


        TextView item_nickname = findViewById(R.id.item_nickname);
        item_nickname.setText(balanceEntity.getUsername());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd");
        TextView item_time = findViewById(R.id.item_time);

        if (null == balanceEntity.getItime() || balanceEntity.getItime() == 0) {
            item_time.setText("");
        } else {
            item_time.setText(getSpanText("注册时间：", dateFormat.format(balanceEntity.getItime() * 1000)));
        }

        TextView tv_phone = findViewById(R.id.item_phone);
        if (!TextUtils.isEmpty(fansEntity.phone)) {
            tv_phone.setText(getSpanText("注册手机：", fansEntity.phone));
        } else {
            tv_phone.setText("");
        }

        TextView tv_tip = findViewById(R.id.tv_tip);
        if (!TextUtils.isEmpty(fansEntity.copy_helptext)) {
            tv_tip.setVisibility(View.VISIBLE);
            int d7 = DeviceUtil.dip2px(getContext(), 7);
            SpannableStringBuilder stringBuilder = new SpanUtils()
                    .appendImage(R.drawable.team_fans_item_notice, SpanUtils.ALIGN_CENTER)
                    .appendSpace(d7).append(fansEntity.copy_helptext).setForegroundColor(Color.parseColor("#C6C6C6"))
                    .create();
            tv_tip.setText(stringBuilder);
        }


        TextView item_copy = findViewById(R.id.item_copy);
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


        findViewById(R.id.tv_know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private SpannableStringBuilder getSpanText(String text1, String text2) {
        return new SpanUtils().append(text1).setForegroundColor(Color.parseColor("#A5A5A6"))
                .append(text2).setForegroundColor(Color.parseColor("#25282D"))
                .create();
    }

    private SpannableStringBuilder getOrderSpan(long validOrder, long invalidOrder) {
        return new SpanUtils()
                .append(validOrder + "/").setForegroundColor(Color.parseColor("#F73737"))
                .append(invalidOrder + "").setForegroundColor(Color.parseColor("#C6C6C6"))
                .create();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.myteam_fans_dialog;
    }
}
