package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.R;

/**
 * @author 张全
 */
public class ItemNameView extends FrameLayout {
    public TextView item_shop_label;
    public TextView item_title;
    int marginLeft;

    public ItemNameView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ItemNameView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemNameView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.icon_name_layout, this);
        item_shop_label = findViewById(R.id.item_shop_label);
        item_title = findViewById(R.id.title_label);
        marginLeft = DeviceUtil.dip2px(LContext.getContext(), 3);
    }

    /**
     * 用了这个方法后一定要用setText
     */
    private void hideIcon() {
        item_shop_label.setVisibility(GONE);
        marginLeft = 0;
    }
    /**
     * 用了这个方法后一定要用setText
     *
     * 原生    private float mSpacingMult = 1.0f;
     *     private float mSpacingAdd = 0.0f;
     */
    public void setLineSpacing(float add, float mult) {
        item_title.setLineSpacing(add,mult);
    }

    public void setText(String shopName, String title) {
        if (TextUtils.isEmpty(shopName)) { //只有标题
            item_shop_label.setVisibility(View.GONE);
            item_title.setText(title);
            return;
        }
        item_shop_label.setVisibility(View.VISIBLE);
        item_shop_label.setText(shopName);
        if (TextUtils.isEmpty(title)) {
            item_title.setText(title);
            return;
        }
        float textWidth = item_shop_label.getPaint().measureText(shopName);
        int paddingValue = item_shop_label.getPaddingLeft() + item_shop_label.getPaddingRight();
        int space = (int) (textWidth + paddingValue + marginLeft);
        SpannableStringBuilder sb = new SpanUtils().appendSpace(space)
                .append(title)
                .setFontSize(14,true)
                .setBold()
                .create();
        item_title.setText(sb);
    }

    public void setText(String title) {
        hideIcon();
        item_title.setText(title);
    }
}
