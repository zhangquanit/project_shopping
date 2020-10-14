package com.snqu.shopping.ui.main.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.util.os.DeviceUtil;
import com.android.util.os.KeyboardUtils;
import com.snqu.shopping.R;
import com.snqu.shopping.data.home.ItemSourceClient;
import com.snqu.shopping.data.home.entity.ItemSourceEntity;
import com.snqu.shopping.data.home.entity.PlateOptions;

import java.util.ArrayList;
import java.util.List;

import common.widget.dialog.loading.LoadingDialog;

/**
 * @author 张全
 */
public class FlitingCoverBar extends RelativeLayout implements View.OnClickListener, Animation.AnimationListener {
    private long duration = 300;
    private boolean show;
    private View floatingBg, floatingBar;
    private RelativeLayout floatingContent;
    private Animation rightInAnim, rightOutAnim;
    private Animation fadeOut, fadeIn;
    private boolean isAnim;
    private DissmissListener dissmissListener;

    public FlitingCoverBar(Context context) {
        super(context);
        init(context);
    }

    public FlitingCoverBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlitingCoverBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View.inflate(context, R.layout.filter_cover_bar, this);

        floatingBar = findViewById(R.id.floatingBar);
        floatingBg = findViewById(R.id.floatingBar_bg);
        floatingBg.setOnClickListener(this);
        floatingContent = findViewById(R.id.floatingBar_content);

        rightInAnim = AnimationUtils.loadAnimation(context, R.anim.anim_right_in);
        rightOutAnim = AnimationUtils.loadAnimation(context, R.anim.anim_right_out);
        fadeOut = AnimationUtils.loadAnimation(context,
                R.anim.anim_fade_out);
        fadeIn = AnimationUtils.loadAnimation(context,
                R.anim.anim_fade_in);

        rightInAnim.setDuration(duration);
        rightInAnim.setAnimationListener(this);

        rightOutAnim.setDuration(duration);
        rightOutAnim.setAnimationListener(this);

        fadeOut.setDuration(duration);
        fadeOut.setAnimationListener(this);

        fadeIn.setDuration(duration);
        fadeIn.setAnimationListener(this);

        //
        initContentView();
    }

    public void addContentView(View view) {
        floatingContent.addView(view);
    }

    public void addContentView(View view, LinearLayout.LayoutParams params) {
        floatingContent.addView(view, params);
    }

    public void addContentView(int contentResId) {
        View.inflate(getContext(), contentResId, floatingContent);
    }

    public boolean isShowing() {
        return floatingBar.getVisibility() == View.VISIBLE;
    }

    public void setOnDissmissListener(DissmissListener dissmissListener) {
        this.dissmissListener = dissmissListener;
    }

    public void show() {
        if (isAnim) {
            return;
        }
        setVisibility(View.VISIBLE);
        show = true;
        floatingBg.startAnimation(fadeOut);
        floatingContent.startAnimation(rightInAnim);
    }

    public void searchShow() {
        if (isAnim) {
            return;
        }
        setVisibility(View.VISIBLE);
        findViewById(R.id.tv_platforms).setVisibility(View.GONE);
        findViewById(R.id.ll_platforms_container).setVisibility(View.GONE);
        show = true;
        floatingBg.startAnimation(fadeOut);
        floatingContent.startAnimation(rightInAnim);
    }


    public void hide() {
        if (isAnim) {
            return;
        }
        show = false;
        floatingBg.startAnimation(fadeIn);
        floatingContent.startAnimation(rightOutAnim);
        KeyboardUtils.hideSoftInput(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        isAnim = true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        isAnim = false;
        if (show) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.floatingBar_bg) {
            hide();
            if (null != dissmissListener) {
                dissmissListener.dismiss();
            }
        }
    }

    /**
     * 是否处理返回键
     *
     * @return
     */
    public boolean handleBackPressed() {
        if (isShowing()) {
            hide();
            return true;
        }
        return false;
    }

    public interface DissmissListener {
        void dismiss();
    }


    //---------------------------------------------------------------------
    List<TextView> platformViews = new ArrayList<>();
    TextView tv_email;
    EditText et_minprice, et_maxprice;


    private void initContentView() {
        List<ItemSourceEntity> itemSources = ItemSourceClient.getSearchItemSource();
        setItemSourceView(itemSources);
        //包邮
        tv_email = findViewById(R.id.tv_email);
        tv_email.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_email.setSelected(!tv_email.isSelected());
            }
        });
        //重置
        findViewById(R.id.btn_reset).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPackaged = tv_email.isSelected(); //包邮
                boolean isPlatformSelected = false;
                for (TextView item : platformViews) {
                    if (item.isSelected()) {
                        isPlatformSelected = true;
                        break;
                    }
                }

                resetUI();
                if (platformViews.size() == 1) {
                    resultCall();
                    return;
                }

                if (isPackaged || isPlatformSelected) {
                    resultCall();
                }
            }
        });
        //确定
        findViewById(R.id.btn_sure).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resultCall();
                hide();
            }
        });

        et_minprice = findViewById(R.id.et_minprice);
        et_maxprice = findViewById(R.id.et_maxprice);
        et_minprice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    String str = s.toString();
                    if (str.startsWith("0")
                            && str.trim().length() > 1) {
                        et_minprice.setText("0");
                        et_minprice.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (!TextUtils.isEmpty(s.toString()) && Integer.valueOf(s.toString()) < 0) {
//                    ToastUtil.show("最小价格不能为负数");
//                }
            }
        });
        et_maxprice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString())) {
                    String str = s.toString();
                    if (str.startsWith("0")
                            && str.trim().length() > 1) {
                        et_maxprice.setText("0");
                        et_maxprice.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (!TextUtils.isEmpty(s.toString())) {
//                    int maxPrice = Integer.valueOf(s.toString());
//                    if (maxPrice < 0) {
//                        ToastUtil.show("最高价不能为负数");
//                    }
//                    else {
//                        String minPrice = et_minprice.getText().toString();
//                        if (!TextUtils.isEmpty(minPrice) && maxPrice <= Integer.valueOf(minPrice)) {
//                            ToastUtil.show("最高价不能低于最低价");
//                            et_maxprice.setText("");
//                        }
//                    }
//                }
            }
        });
    }

    public void resetUI() {
        tv_email.setSelected(false);

        et_minprice.setText(null);
        et_maxprice.setText(null);

        if (platformViews.size() > 1) {
            for (TextView item : platformViews) {
                item.setSelected(false);
            }
        }
    }

    public void setItemSources(PlateOptions plateOptionsList) {
        if (null == plateOptionsList) {
            return;
        }
        List<String> item_source_list = plateOptionsList.item_source;
        List<ItemSourceEntity> dataList = new ArrayList();
        if (null != item_source_list) {
            for (String code : item_source_list) {
                ItemSourceEntity itemSourceEntity = ItemSourceClient.getItemSourceEntity(ItemSourceClient.ItemSourceType.SEARCH, code);
                if (itemSourceEntity != null) {
                    dataList.add(itemSourceEntity);
                }
            }
        }
        if (dataList.isEmpty()) {
            return;
        }
        setItemSourceView(dataList);
    }

    private void setItemSourceView(List<ItemSourceEntity> dataList) {
        LinearLayout ll_platforms_container = findViewById(R.id.ll_platforms_container);
        View tv_platform = findViewById(R.id.tv_platforms);
        ll_platforms_container.removeAllViews();

        if (null == dataList || dataList.isEmpty()) {
            tv_platform.setVisibility(View.INVISIBLE);
            return;
        }
        this.platformViews.clear();
        tv_platform.setVisibility(View.VISIBLE);

        int d100 = DeviceUtil.dip2px(getContext(), 100);
        int d10 = DeviceUtil.dip2px(getContext(), 10);
        int index = 0;
        while (dataList.size() >= 2) {
            ItemSourceEntity data1 = dataList.get(0);
            ItemSourceEntity data2 = dataList.get(1);
            dataList = dataList.subList(2, dataList.size());
            RelativeLayout relativeLayout = new RelativeLayout(getContext());

            TextView textView1 = (TextView) View.inflate(getContext(), R.layout.filter_cover_item, null);
            textView1.setText(data1.name);
            textView1.setTag(data1);
            textView1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());
                }
            });
            TextView textView2 = (TextView) View.inflate(getContext(), R.layout.filter_cover_item, null);
            textView2.setText(data2.name);
            textView2.setTag(data2);
            textView2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());
                }
            });

            LayoutParams layoutParams = new LayoutParams(d100, ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeLayout.addView(textView1, layoutParams);

            layoutParams = new LayoutParams(d100, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            relativeLayout.addView(textView2, layoutParams);


            this.platformViews.add(textView1);
            this.platformViews.add(textView2);

            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (index > 0) {
                layoutParams1.topMargin = d10;
            }
            ll_platforms_container.addView(relativeLayout, layoutParams1);
            index++;
        }

        if (dataList.size() == 1) {
            TextView textView1 = (TextView) View.inflate(getContext(), R.layout.filter_cover_item, null);
            textView1.setText(dataList.get(0).name);
            textView1.setTag(dataList.get(0));
            textView1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setSelected(!v.isSelected());
                }
            });

            this.platformViews.add(textView1);

            RelativeLayout relativeLayout = new RelativeLayout(getContext());
            LayoutParams layoutParams = new LayoutParams(d100, ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeLayout.addView(textView1, layoutParams);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (index > 0) {
                layoutParams1.topMargin = d10;
            }
            ll_platforms_container.addView(relativeLayout, layoutParams1);
        }

        if (this.platformViews.size() == 1) {
            this.platformViews.get(0).setSelected(true);
            this.platformViews.get(0).setOnClickListener(null);
        }
    }

    public void setSelectedItems(List<ItemSourceEntity> dataList) {
        for (TextView item : platformViews) {
            item.setSelected(false);
        }
        if (null == dataList || dataList.isEmpty()) {
            return;
        }
        for (TextView item : platformViews) {
            ItemSourceEntity itemSource = (ItemSourceEntity) item.getTag();  //平台
            if (dataList.contains(itemSource)) {
                item.setSelected(true);
            }
        }
    }

    private void resultCall() {
        boolean isPackaged = tv_email.isSelected(); //包邮
        int postage = isPackaged ? 1 : 0;

        String minPriceStr = null;
        String maxPriceStr = null;
        try {
            String priceStr1 = et_minprice.getText().toString();
            String priceStr2 = et_maxprice.getText().toString();

            if (!TextUtils.isEmpty(priceStr2)) {
                priceStr1 = TextUtils.isEmpty(priceStr1) ? "0" : priceStr1;
                priceStr2 = TextUtils.isEmpty(priceStr2) ? "0" : priceStr2;

                long price1 = Long.valueOf(priceStr1);
                long price2 = Long.valueOf(priceStr2);

                long minPrice = price1 <= price2 ? price1 : price2;
                long maxPrice = price1 >= price2 ? price1 : price2;


                if (!TextUtils.isEmpty(priceStr1)) {
                    et_minprice.setText(String.valueOf(minPrice));
                }

                et_maxprice.setText(String.valueOf(maxPrice));

                minPrice *= 100;
                maxPrice *= 100;


                if (maxPrice > 0) {
                    minPriceStr = String.valueOf(minPrice);
                    if (maxPrice > 0) {
                        maxPriceStr = String.valueOf(maxPrice);
                    }
                }
            } else if (!TextUtils.isEmpty(priceStr1)) {
                minPriceStr = String.valueOf(Long.valueOf(priceStr1) * 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        StringBuffer item_source = new StringBuffer();
        for (TextView item : platformViews) {
            if (item.isSelected()) {
                ItemSourceEntity itemSource = (ItemSourceEntity) item.getTag();  //平台
                item_source.append(itemSource.code).append(",");
            }
        }
        if (item_source.length() > 0) {
            item_source = item_source.deleteCharAt(item_source.length() - 1);
        }
        for (CoverBarListener listener : coverBarListeners) {
            listener.sure(item_source.toString(), postage, minPriceStr, maxPriceStr);
        }
    }

    public void showLoading() {
        loadingDialog = LoadingDialog.showDialog(getContext(), "请稍候").show();
    }

    public void dissmissDialog() {
        if (null != loadingDialog) {
            loadingDialog.dismiss();
        }
    }


    private List<CoverBarListener> coverBarListeners = new ArrayList<>();
    private LoadingDialog loadingDialog;


    public void setCoverBarListener(CoverBarListener coverBarListener) {
        coverBarListeners.add(coverBarListener);
    }

    public void removeCoverBarListener(CoverBarListener coverBarListener) {
        coverBarListeners.remove(coverBarListener);
    }

    public static interface CoverBarListener {
        void sure(String item_source, int postage, String minPrice, String maxPrice);
    }
}
