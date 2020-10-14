package com.snqu.shopping.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.anroid.base.BaseActivity;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.ui.main.MainActivity;

import java.util.ArrayList;

/**
 * 引导页面
 */
public class GuideActivity extends BaseActivity {

    private static final int[] BMP = new int[]{R.drawable.guide01, R.drawable.guide02, R.drawable.guide03};
    private ViewPager viewPager;

    @Override
    public int getLayoutId() {
        return R.layout.guide_layout;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        parseClipboard = false;
        ArrayList<View> viewList = new ArrayList<>();
        for (int i = 0; i < BMP.length; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_guide, null);
            ImageView imageView = view.findViewById(R.id.guide_img);
            imageView.setImageResource(BMP[i]);
            viewList.add(view);
            if (i == BMP.length - 1) {
                view.findViewById(R.id.tv_go_login).setVisibility(View.VISIBLE);
                view.findViewById(R.id.tv_go_video).setVisibility(View.VISIBLE);
                view.findViewById(R.id.tv_go_login).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserClient.showGuide();
                        MainActivity.start(GuideActivity.this);
                        finish();
                    }
                });
                view.findViewById(R.id.tv_go_video).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GuideVideoActivity.start(mContext);
                    }
                });
            }
        }
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                container.addView(viewList.get(position), ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                return viewList.get(position);
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
    }

    public static void start(Context ctx) {
        Intent intent = new Intent(ctx, GuideActivity.class);
        ctx.startActivity(intent);
    }
}
