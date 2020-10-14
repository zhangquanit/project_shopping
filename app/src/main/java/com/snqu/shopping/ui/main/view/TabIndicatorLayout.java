package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.snqu.shopping.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张全
 */
public class TabIndicatorLayout extends LinearLayout {
    private List<View> titleViewList = new ArrayList<>();
    private List<View> indicatorList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public TabIndicatorLayout(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public TabIndicatorLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public TabIndicatorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    private int lastPos;

    public void setData(List<String> titleList, int selPos) {
        setWeightSum(titleList.size());
        lastPos = selPos;
        LayoutParams layoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        for (int i = 0; i < titleList.size(); i++) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_indicator_layout, null);
            addView(view, layoutParams);
            TextView item_title = view.findViewById(R.id.item_title);
            View indicator = view.findViewById(R.id.indictor);
            item_title.setText(titleList.get(i));
            titleViewList.add(item_title);
            indicatorList.add(indicator);

            if (selPos == i) {
                indicator.setVisibility(View.VISIBLE);
                item_title.setSelected(true);
            }
            view.setTag(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    if (lastPos == pos) {
                        return;
                    }

                    titleViewList.get(pos).setSelected(true);
                    indicatorList.get(pos).setVisibility(View.VISIBLE);

                    titleViewList.get(lastPos).setSelected(false);
                    indicatorList.get(lastPos).setVisibility(View.INVISIBLE);

                    if (null != onItemClickListener) {
                        onItemClickListener.onClick(pos, v);
                    }
                    lastPos = pos;
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onClick(int pos, View v);
    }

}
