package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.entity.FansQueryParam;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张全
 */
public class TeamFilterView extends LinearLayout {
    private int d5;
    private int normolColor, selColor;
    List<TextView> typeViewList = new ArrayList<>();
    OnItemClickListener onItemClickListener;
    public FansQueryParam.QuerySort sort = FansQueryParam.QuerySort.NONE;

    public TeamFilterView(Context context) {
        super(context);
        init(context);
    }

    public TeamFilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TeamFilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private int lastSelPos;
    private int selIndex;

    private void init(Context ctx) {
        d5 = DeviceUtil.dip2px(ctx, 5);
        normolColor = Color.parseColor("#000000");
        selColor = Color.parseColor("#FF8202");
        LayoutInflater.from(ctx).inflate(R.layout.myteam_filter_banner, this);
        TextView tv_time = findViewById(R.id.tv_time);
        TextView tv_fans = findViewById(R.id.tv_fans);
        TextView tv_filter = findViewById(R.id.tv_filter);


        typeViewList.add(tv_time);
        typeViewList.add(tv_fans);
        typeViewList.add(tv_filter);

        for (int i = 0; i < typeViewList.size(); i++) {
            final int pos = i;
            TextView itemView = typeViewList.get(i);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int drawRes = 0;
                    switch (v.getId()) {
                        case R.id.tv_time: //直属注册时间
                            drawRes = getSelDrawRes(pos);
                            tv_time.setText(getText("累计预估收益", selColor, drawRes));
                            tv_fans.setText(getText("粉丝人数", normolColor, R.drawable.filter_icon_normal));
                            break;
                        case R.id.tv_fans: //成员人数
                            drawRes = getSelDrawRes(pos);

                            tv_time.setText(getText("累计预估收益", normolColor, R.drawable.filter_icon_normal));
                            tv_fans.setText(getText("粉丝人数", selColor, drawRes));

                            break;
                        case R.id.tv_filter: //筛选
                            onItemClickListener.filtrate();
                            break;
                    }
                    //选中文字加粗
//                    setTextBold(pos);
                    lastSelPos = pos;
                    onItemClickListener.onFilter(getSort());
                }
            });
        }
        tv_time.setText(getText("累计预估收益", normolColor, R.drawable.filter_icon_normal));
        tv_fans.setText(getText("粉丝人数", normolColor, R.drawable.filter_icon_normal));
        tv_filter.setText(getText("筛选", normolColor, R.drawable.filter_icon_screen));
    }

    public FansQueryParam.QuerySort getSort() {
        switch (lastSelPos) {
            case 0: //累计预估收益
                sort = selIndex == 1 ? FansQueryParam.QuerySort.INCOME_DOWN : FansQueryParam.QuerySort.INCOME_UP;
                break;
            case 1: //成员人数
                sort = selIndex == 1 ? FansQueryParam.QuerySort.FANS_DOWN : FansQueryParam.QuerySort.FANS_UP;
                break;
        }
        return sort;
    }

    private void setTextBold(int pos) {
        if (lastSelPos != pos) {
            typeViewList.get(lastSelPos).getPaint().setFakeBoldText(false);
            typeViewList.get(pos).getPaint().setFakeBoldText(true);
        }
    }

    private int getSelDrawRes(int pos) {
        if (lastSelPos != pos) {
            selIndex = 1;
        } else {
            selIndex++;
            if (selIndex > 2) {
                selIndex = 1;
            }
        }
//        if (pos == 0) { //直属注册时间
//            return selIndex == 1 ? R.drawable.filter_icon_up : R.drawable.filter_icon_down;
//        }
        return selIndex == 1 ? R.drawable.filter_icon_down : R.drawable.filter_icon_up;
    }

    private SpannableStringBuilder getText(String text, int textColor, int drawabeRes) {
        SpanUtils spanUtils = new SpanUtils()
                .append(text).setForegroundColor(textColor);
        if (drawabeRes != -1) {
            spanUtils.appendSpace(d5).appendImage(drawabeRes, SpanUtils.ALIGN_CENTER);
        }
        return spanUtils.create();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * 累计预估收益
         */
        void filtrate();

        void onFilter(FansQueryParam.QuerySort sort);

    }
}
