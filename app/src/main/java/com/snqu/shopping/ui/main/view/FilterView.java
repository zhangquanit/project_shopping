package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.goods.entity.GoodsQueryParam;
import com.snqu.shopping.util.statistics.SndoData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张全
 */
public class FilterView extends LinearLayout {
    private int d5;
    private int normolColor, selColor;
    List<TextView> typeViewList = new ArrayList<>();
    OnItemClickListener onItemClickListener;
    public GoodsQueryParam.Sort sort = GoodsQueryParam.Sort.NONE;
    private String priceText = "券后价";
    private String itemSource = "";

    public FilterView(Context context) {
        super(context);
        init(context);
    }

    public FilterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private int lastSelPos;
    private int selIndex;

    private void init(Context ctx) {

        d5 = DeviceUtil.dip2px(ctx, 5);
        normolColor = Color.parseColor("#000000");
        selColor = Color.parseColor("#FF8202");
        LayoutInflater.from(ctx).inflate(R.layout.filter_banner, this);
        TextView tv_zh = findViewById(R.id.tv_zh);
        TextView tv_quan = findViewById(R.id.tv_quan);
        TextView tv_soldcount = findViewById(R.id.tv_soldcount);
        TextView tv_commsion = findViewById(R.id.tv_commsion);
        TextView tv_filter = findViewById(R.id.tv_filter);

        tv_zh.setSelected(true);
        tv_zh.getPaint().setFakeBoldText(true);

        typeViewList.add(tv_zh);
        typeViewList.add(tv_quan);
        typeViewList.add(tv_soldcount);
        typeViewList.add(tv_commsion);
        typeViewList.add(tv_filter);

        List<String> titleList = new ArrayList();
        titleList.add("综合");
        titleList.add(priceText);
        titleList.add("销量");
        titleList.add("返利金");
        titleList.add("筛选");

        for (int i = 0; i < typeViewList.size(); i++) {
            final int pos = i;
            TextView itemView = typeViewList.get(i);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SndoData.event(SndoData.XLT_EVENT_FILTER,
                            SndoData.XLT_ITEM_LEVEL, "null",
                            SndoData.XLT_ITEM_CLASSIFY_NAME, titleList.get(pos),
                            "filter_dimension", "null"
                    );
                    int drawRes = 0;
                    switch (v.getId()) {
                        case R.id.tv_zh: //综合
                            if (lastSelPos == pos) {
                                return;
                            }
                            sort = GoodsQueryParam.Sort.NONE;
                            selIndex = 1;
                            tv_zh.setSelected(true);
                            tv_quan.setText(getText(priceText, normolColor, R.drawable.filter_icon_normal));
                            tv_soldcount.setText(getText("销量", normolColor, R.drawable.filter_icon_down_gray));
                            tv_commsion.setText(getText("返利金", normolColor, R.drawable.filter_icon_down_gray));

                            break;
                        case R.id.tv_quan: //卷后价
                            drawRes = getSelDrawRes(pos);
                            tv_zh.setSelected(false);
                            tv_quan.setText(getText(priceText, selColor, drawRes));
                            tv_soldcount.setText(getText("销量", normolColor, R.drawable.filter_icon_down_gray));
                            tv_commsion.setText(getText("返利金", normolColor, R.drawable.filter_icon_down_gray));
                            break;
                        case R.id.tv_soldcount: //销量
                            if (lastSelPos == pos) {
                                return;
                            }
                            tv_zh.setSelected(false);
                            tv_quan.setText(getText(priceText, normolColor, R.drawable.filter_icon_normal));
                            tv_soldcount.setText(getText("销量", selColor, R.drawable.filter_icon_down));
                            tv_commsion.setText(getText("返利金", normolColor, R.drawable.filter_icon_down_gray));

                            break;
                        case R.id.tv_commsion: //佣金
                            drawRes = getSelDrawRes(pos);

                            tv_zh.setSelected(false);
                            tv_quan.setText(getText(priceText, normolColor, R.drawable.filter_icon_normal));
                            tv_soldcount.setText(getText("销量", normolColor, R.drawable.filter_icon_down_gray));
                            tv_commsion.setText(getText("返利金", selColor, drawRes));

                            break;
                        case R.id.tv_filter: //筛选
                            onItemClickListener.filtrate();
                            break;
                    }
                    //选中文字加粗
                    setTextBold(pos);
                    lastSelPos = pos;
                    if (v.getId() != R.id.tv_filter) {
                        onItemClickListener.onFilter(getSort());
                    }
                }
            });
        }
        tv_quan.setText(getText(priceText, normolColor, R.drawable.filter_icon_normal));
        tv_soldcount.setText(getText("销量", normolColor, R.drawable.filter_icon_down_gray));
        tv_commsion.setText(getText("返利金", normolColor, R.drawable.filter_icon_normal));
        tv_filter.setText(getText("筛选", normolColor, R.drawable.filter_icon_screen));
    }

    public void hideFilterItem() {
        findViewById(R.id.tv_filter).setVisibility(View.GONE);
    }

    /**
     * 搜索页面-淘宝、京东、唯品会
     */
    public void setTbSearchStyle() {
        itemSource = Constant.BusinessType.TB;
        priceText = "价格";
        typeViewList.get(1).setText(getText(priceText, normolColor, R.drawable.filter_icon_normal));
        typeViewList.get(2).setVisibility(View.GONE);
        typeViewList.get(3).setVisibility(View.GONE);
    }

    /**
     * 搜索页面-拼多多
     */
    public void setPddSearchStyle() {
        itemSource = Constant.BusinessType.PDD;
        priceText = "券后价";
        typeViewList.get(1).setText(getText(priceText, normolColor, R.drawable.filter_icon_normal));
        typeViewList.get(2).setVisibility(View.VISIBLE);
        typeViewList.get(3).setVisibility(View.VISIBLE);
    }

    /**
     * 搜索页面苏宁
     */
    public void setSuningSearchStyle() {
        itemSource = Constant.BusinessType.S;
        priceText = "价格";
        typeViewList.get(1).setText(getText(priceText, normolColor, R.drawable.filter_icon_normal));
        typeViewList.get(3).setText(getText("返利金", normolColor, R.drawable.filter_icon_down_gray));
        typeViewList.get(2).setVisibility(View.GONE);
        typeViewList.get(3).setVisibility(View.VISIBLE);
    }

    public void resetUI() {
        sort = GoodsQueryParam.Sort.NONE;
        selIndex = 1;
        lastSelPos = 0;
        typeViewList.get(0).setSelected(true);
        typeViewList.get(0).getPaint().setFakeBoldText(true);

        typeViewList.get(1).setText(getText("券后价", normolColor, R.drawable.filter_icon_up_gray));
        typeViewList.get(1).getPaint().setFakeBoldText(false);

        typeViewList.get(2).setText(getText("销量", normolColor, R.drawable.filter_icon_down_gray));
        typeViewList.get(2).getPaint().setFakeBoldText(false);

        typeViewList.get(3).setText(getText("返利金", normolColor, R.drawable.filter_icon_normal));
        typeViewList.get(3).getPaint().setFakeBoldText(false);
    }

    public GoodsQueryParam.Sort getSort() {
        switch (lastSelPos) {
            case 0: //综合
                sort = GoodsQueryParam.Sort.NONE;
                break;
            case 1: //券后价
                sort = selIndex == 1 ? GoodsQueryParam.Sort.PRICE_UP : GoodsQueryParam.Sort.PRICE_DOWN;
                break;
            case 2://销量
                sort = GoodsQueryParam.Sort.SELL_COUNT_DOWN;
                break;
            case 3: //返利金
                if (TextUtils.equals(itemSource, Constant.BusinessType.S)) {
                    sort = GoodsQueryParam.Sort.AMOUNT_DOWN;
                } else {
                    sort = selIndex == 1 ? GoodsQueryParam.Sort.AMOUNT_DOWN : GoodsQueryParam.Sort.AMOUNT_UP;
                }
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

        if (pos == 1) { //券后价
            return selIndex == 1 ? R.drawable.filter_icon_up : R.drawable.filter_icon_down;
        } else if (pos == 2) { // 销量
            return R.drawable.filter_icon_down;
        } else { //返利金
            if (TextUtils.equals(itemSource, Constant.BusinessType.S)) {
                return R.drawable.filter_icon_down;
            }
            return selIndex == 1 ? R.drawable.filter_icon_down : R.drawable.filter_icon_up;
        }
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

        void onFilter(GoodsQueryParam.Sort sort);

    }
}
