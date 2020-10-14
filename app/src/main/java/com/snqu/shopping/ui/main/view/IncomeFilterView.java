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
import com.snqu.shopping.data.user.entity.IncomeQueryParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 成员贡献过滤
 *
 * @author 张全
 */
public class IncomeFilterView extends LinearLayout {
    private int d5;
    private int normolColor, selColor;
    List<TextView> typeViewList = new ArrayList<>();
    OnItemClickListener onItemClickListener;
    public IncomeQueryParam.Sort sort = IncomeQueryParam.Sort.NONE;

    public IncomeFilterView(Context context) {
        super(context);
        init(context);
    }

    public IncomeFilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IncomeFilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private int lastSelPos;
    private int selIndex;

    private void init(Context ctx) {
        d5 = DeviceUtil.dip2px(ctx, 5);
        normolColor = Color.parseColor("#000000");
        selColor = Color.parseColor("#FF8202");
        LayoutInflater.from(ctx).inflate(R.layout.team_income_filter_banner, this);
        TextView tv_total = findViewById(R.id.tv_total);
        TextView tv_month = findViewById(R.id.tv_month);
        TextView tv_week = findViewById(R.id.tv_week);
        TextView tv_filter = findViewById(R.id.tv_filter);

        typeViewList.add(tv_total);
        typeViewList.add(tv_month);
        typeViewList.add(tv_week);
        typeViewList.add(tv_filter);

        for (int i = 0; i < typeViewList.size(); i++) {
            final int pos = i;
            TextView itemView = typeViewList.get(i);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int drawRes = 0;
                    switch (v.getId()) {
                        case R.id.tv_total: //总预估
                            if (lastSelPos == pos) {
                                return;
                            }
                            tv_total.setSelected(true);
                            tv_month.setSelected(false);
                            tv_week.setText(getText("七日拉新", normolColor, R.drawable.filter_icon_normal));
                            break;
                        case R.id.tv_month: //本月预估
                            if (lastSelPos == pos) {
                                return;
                            }
                            tv_total.setSelected(false);
                            tv_month.setSelected(true);
                            tv_week.setText(getText("七日拉新", normolColor, R.drawable.filter_icon_normal));
                            break;
                        case R.id.tv_week: //七日拉新
                            drawRes = getSelDrawRes(pos);

                            tv_total.setSelected(false);
                            tv_month.setSelected(false);
                            tv_week.setText(getText("七日拉新", selColor, drawRes));
                            break;
                        case R.id.tv_filter: //筛选
                            onItemClickListener.filtrate();
                            break;
                    }
                    //选中文字加粗
//                    setTextBold(pos);
                    lastSelPos = pos;
                    if (v.getId() != R.id.tv_filter) {
                        onItemClickListener.onFilter(lastSelPos,getSort());
                    }
                }
            });
        }
        tv_total.setSelected(true);
        tv_week.setText(getText("七日拉新", normolColor, R.drawable.filter_icon_normal));
        tv_filter.setText(getText("筛选", normolColor, R.drawable.filter_icon_screen));
    }

    public IncomeQueryParam.Sort getSort() {
        switch (lastSelPos) {
            case 0: //总预估
            case 1: //本月预估
                sort = IncomeQueryParam.Sort.NONE;
                break;
            case 2:
                //七日拉新
                sort = selIndex == 1 ? IncomeQueryParam.Sort.WEEK_DOWN : IncomeQueryParam.Sort.WEEK_UP;
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
         * 筛选
         */
        void filtrate();

        void onFilter(int page, IncomeQueryParam.Sort sort);

    }
}
