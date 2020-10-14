package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.CategoryEntity;
import com.snqu.shopping.ui.main.frag.GoodsListFrag;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.statistics.DataCache;
import com.snqu.shopping.util.statistics.SndoData;

import java.util.List;

/**
 * @author 张全
 */
public class ClassficationItemView extends LinearLayout {

    public ClassficationItemView(Context context) {
        super(context);
        init(context);
    }

    public ClassficationItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ClassficationItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.classfication_item, this);
    }

    public void setItems(List<CategoryEntity> items) {
        CategoryEntity item = items.get(0);
        ImageView imageView = findViewById(R.id.item_img1);
        GlideUtil.loadPic(imageView, item.icon, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
        TextView textView = findViewById(R.id.item_nam1);
        textView.setText(item.name);
        findViewById(R.id.item_banner1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GoodsListFrag.start(getContext(), items.get(0)._id, items.get(0).name);
                report(items.get(0));
            }
        });

        if (items.size() >= 2) {
            findViewById(R.id.item_banner2).setVisibility(View.VISIBLE);
            item = items.get(1);
            imageView = findViewById(R.id.item_img2);
            GlideUtil.loadPic(imageView, item.icon, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
            textView = findViewById(R.id.item_nam2);
            textView.setText(item.name);
            findViewById(R.id.item_banner2).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoodsListFrag.start(getContext(), items.get(1)._id, items.get(1).name);
                    report(items.get(1));
                }
            });
        }
        if (items.size() >= 3) {
            findViewById(R.id.item_banner3).setVisibility(View.VISIBLE);
            item = items.get(2);
            imageView = findViewById(R.id.item_img3);
            GlideUtil.loadPic(imageView, item.icon, R.drawable.icon_min_default_pic, R.drawable.icon_min_default_pic);
            textView = findViewById(R.id.item_nam3);
            textView.setText(item.name);
            findViewById(R.id.item_banner3).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoodsListFrag.start(getContext(), items.get(2)._id, items.get(2).name);
                    report(items.get(2));
                }
            });
        }
    }

    private void report(CategoryEntity categoryEntity) {
        DataCache.thirdCategory = categoryEntity;
        DataCache.firstCategory = DataCache.classificationFirstCategory;
        SndoData.event(SndoData.XLT_EVENT_CATEGORY,
                SndoData.XLT_ITEM_CLASSIFY_NAME, categoryEntity.getName(),
                SndoData.XLT_ITEM_LEVEL, categoryEntity.level+""
        );
    }

}
