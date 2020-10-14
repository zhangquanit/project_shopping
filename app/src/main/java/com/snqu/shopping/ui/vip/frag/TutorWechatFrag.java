package com.snqu.shopping.ui.vip.frag;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.ui.order.util.ImgUtils;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.statistics.SndoData;

/**
 * 联系导师
 */
public class TutorWechatFrag extends SimpleFrag {

    public static void start(Context ctx) {
        SndoData.event(SndoData.XLT_EVENT_USER_MENTOR);
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("联系导师", TutorWechatFrag.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.tutor_wechat_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        StatusBar.setStatusBar(getActivity(), true, getTitleBar());
        getTitleBar().setBackgroundColor(Color.WHITE);


        TextView tv_wechat = findViewById(R.id.tv_wechat);
        UserEntity user = UserClient.getUser();
        final String tutor_wechat_show_uid = user.tutor_wechat_show_uid;

        tv_wechat.setText("导师微信：" + tutor_wechat_show_uid);

        RoundedImageView roundedImageView = findViewById(R.id.teacher_icon);
        if (TextUtils.isEmpty(user.tutor_inviter_avatar)) {
            roundedImageView.setVisibility(View.GONE);
        } else {
            roundedImageView.setVisibility(View.VISIBLE);
            GlideUtil.loadPic(roundedImageView, user.tutor_inviter_avatar, R.drawable.icon_default_head, R.drawable.icon_default_head);
        }
        findViewById(R.id.tv_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText(null, tutor_wechat_show_uid);
                    clipboardManager.setPrimaryClip(clipData);
                    ToastUtil.show("复制成功");
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse("weixin://"));
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.show("未安装微信客户端");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.show("复制失败");
                }
            }
        });


        findViewById(R.id.qrcode).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                String[] items = {"保存到相册"};
                alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tutor_code);
                            if (bitmap != null) {
                                ImgUtils.saveImageToGalleryCheckExist(getActivity(), bitmap, "tutor_code");
                                showToastShort("图片已保存到相册");
                            }
                        }
                    }
                });
                alertDialog.show();
                return true;
            }
        });


        findViewById(R.id.save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tutor_code);
                if (bitmap != null) {
                    ImgUtils.saveImageToGalleryCheckExist(getActivity(), bitmap, "tutor_code");
                    showToastShort("图片已保存到相册");
                }
            }
        });
    }

}
