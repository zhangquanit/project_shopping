package com.snqu.shopping.ui.main.frag.search;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.blankj.utilcode.util.SpanUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.entity.SearchSlugEntity;

/**
 * 搜索关键词
 */
public class SearchSlugListAdapter extends BaseQuickAdapter<SearchSlugEntity, BaseViewHolder> {

    public SearchSlugListAdapter() {
        super(R.layout.search_slug_item);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchSlugEntity item) {
        helper.setText(R.id.item_name, item.key);

    }

    private SpannableStringBuilder getText(SearchSlugEntity item) {
        SpanUtils spanUtils = new SpanUtils();
        if (!TextUtils.isEmpty(item.prefixText)) {
            spanUtils.append(item.prefixText).setForegroundColor(Color.parseColor("#131413")).setFontSize(14, true);
        }
        spanUtils.append(item.search).setForegroundColor(Color.parseColor("#FF8202")).setFontSize(14, true);
        if (!TextUtils.isEmpty(item.subfixText)) {
            spanUtils.append(item.subfixText).setForegroundColor(Color.parseColor("#131413")).setFontSize(14, true);
        }
        return spanUtils.create();
    }

}