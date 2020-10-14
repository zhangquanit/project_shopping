package com.snqu.shopping.ui.main.frag.community;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.snqu.shopping.R;
import com.snqu.shopping.util.GlideUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * @author 张全
 */
public class ZoomPicFrag extends SimpleFrag {
    private static final String PARAM_INDEX = "INDEX";
    private static final String PARAM_IMGS = "IMGS";
    private List<View> viewList;
    private List<String> imgList;

    public static void start(Context ctx, int index, ArrayList<String> imgs) {
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_INDEX, index);
        bundle.putStringArrayList(PARAM_IMGS, imgs);

        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("", ZoomPicFrag.class, bundle).hideTitleBar(true));

    }

    @Override
    protected int getLayoutId() {
        return R.layout.zoom_img_layout;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(mContext, false);
        findViewById(R.id.img_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        int index = getArguments().getInt(PARAM_INDEX, 0);
        imgList = getArguments().getStringArrayList(PARAM_IMGS);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        viewList = new ArrayList<>();
        for (int i = 0; i < imgList.size(); i++) {
            View view = inflater.inflate(R.layout.zoom_img_item, null);
            viewList.add(view);
        }
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = viewList.get(position);
                ImageView imageView = view.findViewById(R.id.photo_view);
                String imgUrl = imgList.get(position);
                if (imgUrl.contains("good://")) {
                    imgUrl = imgUrl.replace("good://", "");
                    GlideUtil.loadLocalPic(imageView, imgUrl, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
                } else {
                    GlideUtil.loadPic(imageView, imgUrl, R.drawable.icon_max_default_pic, R.drawable.icon_max_default_pic);
                }
                container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView(viewList.get(position));
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        });
        viewPager.setCurrentItem(index);
    }
}
