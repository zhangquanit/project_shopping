package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.util.os.DeviceUtil;
import com.snqu.shopping.R;

/**
 * @author 张全
 */
public class FansStatusView extends FrameLayout {
    public ImageView iv_icon;
    public TextView tv_text;
    public RelativeLayout rl_content_view;
    public RelativeLayout rl_loading_view;
    public TextView btn;
    public FansLoadingStatus status;
    public TextView tv_invite;
    public boolean showInvite = true;

    public FansStatusView(@NonNull Context context) {
        super(context);
        init();
    }

    public FansStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FansStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.fans_empty_layout, this);
        rl_content_view = findViewById(R.id.rl_content_view);
        rl_loading_view = findViewById(R.id.rl_loading_view);
        iv_icon = findViewById(R.id.empty_icon);
        tv_text = findViewById(R.id.empty_text);
        btn = findViewById(R.id.empty_btn);

        tv_invite = findViewById(R.id.tv_invite);

        setStatus(FansLoadingStatus.LOADING);
    }

    public void setStatus(FansLoadingStatus status) {
        this.status = status;
        setVisibility(View.VISIBLE);
        if (status == FansLoadingStatus.LOADING) {
            rl_loading_view.setVisibility(View.VISIBLE);
            rl_content_view.setVisibility(View.GONE);
        } else {
            rl_loading_view.setVisibility(View.GONE);
            rl_content_view.setVisibility(View.VISIBLE);
            iv_icon.setImageResource(status.iconRes);
            tv_text.setText(status.text);
            btn.setText(status.btnText);
            btn.setVisibility(TextUtils.isEmpty(status.btnText) ? View.GONE : View.VISIBLE);
            if (status == FansLoadingStatus.EMPTY && showInvite) {
                tv_invite.setVisibility(View.VISIBLE);
            } else {
                tv_invite.setVisibility(View.GONE);
            }
        }
    }

//    public void setStatus(@DrawableRes int iconRes, String text, String btnText) {
//        iv_icon.setImageResource(iconRes);
//        tv_text.setText(text);
//        btn.setText(btnText);
//        btn.setVisibility(TextUtils.isEmpty(btnText) ? View.GONE : View.VISIBLE);
//    }

    public boolean canLoading() {
        return status == FansLoadingStatus.NO_CONNECTION || status == FansLoadingStatus.FAIL;
    }

    public void setIcon(@DrawableRes int iconRes) {
        iv_icon.setImageResource(iconRes);
    }

    public void setText(String text) {
        tv_text.setText(text);
    }

    public void setBtnText(String btnText) {
        btn.setText(btnText);
        btn.setVisibility(TextUtils.isEmpty(btnText) ? View.GONE : View.VISIBLE);
    }

    public void setOnBtnClickListener(OnClickListener listener) {
        btn.setOnClickListener(listener);
    }

    public void setInviteClickListener(OnClickListener listener) {
        tv_invite.setOnClickListener(listener);
    }

    public void setContentViewTop(int top) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_content_view.getLayoutParams();
        params.removeRule(RelativeLayout.CENTER_VERTICAL);
        rl_content_view.setPadding(0, DeviceUtil.dip2px(getContext(), top), 0, 0);
        rl_content_view.setLayoutParams(params);
    }

    public void setContentViewTopAndBottom(int top, int bottom) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_content_view.getLayoutParams();
        params.removeRule(RelativeLayout.CENTER_VERTICAL);
        rl_content_view.setPadding(0, DeviceUtil.dip2px(getContext(), top), 0, DeviceUtil.dip2px(getContext(), bottom));
        rl_content_view.setLayoutParams(params);
    }

    public static enum FansLoadingStatus {
        LOADING(-1, null, null),
        EMPTY(R.drawable.empty_icon, "您还没有记录哦～", ""),
        //        EMPTY_INVITE(R.drawable.empty_icon, "您还没有记录哦～", ""),
        FAIL(R.drawable.icon_fail, "加载失败", "重新加载"),
        NO_CONNECTION(R.drawable.icon_fail, "无网络连接", "重新加载");

        public int iconRes;
        public String text;
        public String btnText;

        private FansLoadingStatus(@DrawableRes int iconRes, String text, String btnText) {
            this.iconRes = iconRes;
            this.text = text;
            this.btnText = btnText;
        }
    }
}
