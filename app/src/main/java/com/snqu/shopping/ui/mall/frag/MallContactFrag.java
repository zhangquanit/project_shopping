package com.snqu.shopping.ui.mall.frag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.R;
import com.snqu.shopping.util.CommonUtil;

/**
 * 联系客服
 */
public class MallContactFrag extends SimpleFrag {
    private final String WECHAT = "xingletao2020";
    private final int QRCODE = R.drawable.icon_gzh; //二维码图片

    public static void start(Context ctx) {
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("联系客服", MallContactFrag.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.mall_contact_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        StatusBar.setStatusBar(mContext, true, getTitleBar());
        getTitleBar().setBackgroundColor(Color.WHITE);


        ImageView iv_qrcode = findViewById(R.id.iv_qrcode);
        iv_qrcode.setImageResource(QRCODE);
        findViewById(R.id.tv_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToGallery();
            }
        });

        TextView tv_wechat = findViewById(R.id.tv_wechat);
        SpannableStringBuilder stringBuilder = new SpanUtils()
                .append("客服微信号：")
                .append(WECHAT).setBold()
                .create();
        tv_wechat.setText(stringBuilder);

        findViewById(R.id.tv_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.addToClipboard(WECHAT);
                ToastUtil.show("复制成功");
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("weixin://"));
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.show("打开失败，检查是否安装微信App");
                }
            }
        });

    }

    private void saveToGallery() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), QRCODE);
        try {
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                    bitmap, "xlt_contact_qrcode", "星乐桃客服微信");
            ToastUtil.show("保存相册成功");
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show("保存相册失败");
        }
    }
}
