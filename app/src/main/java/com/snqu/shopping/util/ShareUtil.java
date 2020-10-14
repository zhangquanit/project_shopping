package com.snqu.shopping.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.base.RestClient;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.media.UMWeb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;

/**
 * desc:分享工具类(可以参考https://developer.umeng.com/docs/66632/detail/66639)
 * time: 2019/1/7
 *
 * @author 银进
 */
public class ShareUtil {

    /**
     * 分享
     *
     * @param activity 上下文
     * @param url      分享的连接
     * @param title    分享的title
     * @param content  分享的内容
     * @param imgRes   bitmap
     *                 回调监听
     */
    public static void share(Activity activity, String url, String title, String content, String imgRes, SHARE_MEDIA shareMedia) {
        share(activity, url, title, content, imgRes, shareMedia, new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                ToastUtil.show("分享成功");
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                ToastUtil.show("分享失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                ToastUtil.show("取消分享");
            }
        });
    }

    @SuppressLint("CheckResult")
    public static void share(Activity activity, String url, String title, String content, String imgRes, SHARE_MEDIA shareMedia, UMShareListener umShareListener) {
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            activity.runOnUiThread(() -> {
                switch (shareMedia) {
                    case QQ:
                        if (!AppInstallUtil.INSTANCE.isAppInstalled(activity, "com.tencent.mobileqq")) {
                            ToastUtil.show("请先安装QQ客户端");
                            return;
                        }
                        break;
                    case WEIXIN:
                        if (!AppInstallUtil.INSTANCE.isAppInstalled(activity, "com.tencent.mm")) {
                            ToastUtil.show("请先安装微信客户端");
                            return;
                        }
                        break;
                    case SINA:
                        if (!AppInstallUtil.INSTANCE.isAppInstalled(activity, "com.sina.weibo")) {
                            ToastUtil.show("请先安装微博客户端");
                            return;
                        }
                        break;
                }
            });
            //主线程进行toast提示但是子线程还是要进行判断
            switch (shareMedia) {
                case QQ:
                    if (!AppInstallUtil.INSTANCE.isAppInstalled(activity, "com.tencent.mobileqq")) {
                        return;
                    }
                    break;
                case WEIXIN:
                    if (!AppInstallUtil.INSTANCE.isAppInstalled(activity, "com.tencent.mm")) {
                        return;
                    }
                    break;
                case SINA:
                    if (!AppInstallUtil.INSTANCE.isAppInstalled(activity, "com.sina.weibo")) {
                        return;
                    }
                    break;
            }
            UMImage image;
            if (!TextUtils.isEmpty(imgRes)) {
                // 小程序消息封面图片
                image = new UMImage(activity, getHtmlByteArray(imgRes));//网络图片
            } else {
                // 小程序消息封面图片
                image = new UMImage(activity, R.mipmap.ic_launcher);//网络图片
            }
            image.compressStyle = UMImage.CompressStyle.SCALE;
            image.compressFormat = Bitmap.CompressFormat.PNG;
            ShareAction shareAction = new ShareAction(activity);
            shareAction.setPlatform(shareMedia);
            if (TextUtils.isEmpty(url) && TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
                image.setThumb(image);
                shareAction.withMedia(image);
            } else {
                UMWeb web = new UMWeb(url);
                web.setTitle(title);//标题
                web.setThumb(image);  //缩略图
                web.setDescription(content);//描述
                shareAction.withMedia(web);
            }
            shareAction
                    .setCallback(umShareListener)
                    .share();
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Functions.emptyConsumer(), new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    /**
     * 分享
     *
     * @param activity 上下文
     * @param code     分享的code
     *                 回调监听
     */
    public static void share(Activity activity, String code, SHARE_MEDIA shareMedia) {
        String title = "购物达人必备省钱工具，跟我一起领高额购物补贴";
        String content = "领12亿商品内部优惠券，无门槛领券！";
        switch (shareMedia) {
            case QQ:
                if (!AppInstallUtil.INSTANCE.isAppInstalled(activity, "com.tencent.mobileqq")) {
                    ToastUtil.show("请先安装QQ客户端");
                    return;
                }
                break;
            case WEIXIN:
                if (!AppInstallUtil.INSTANCE.isAppInstalled(activity, "com.tencent.mm")) {
                    ToastUtil.show("请先安装微信客户端");
                    return;
                }
                break;
            case SINA:
                title = "星乐桃超值分享活动，免费领双份返利金，快来参与吧！";
                content = "帮助我助力，你可以免费获得双份返利金的特权，领取海量优惠券！查看详情>>";
                if (!AppInstallUtil.INSTANCE.isAppInstalled(activity, "com.sina.weibo")) {
                    ToastUtil.show("请先安装微博客户端");
                    return;
                }
                break;
        }
        String finalTitle = title;
        String finalContent = content;
        String url = Constant.WebPage.SHARE_INVITE_URL + code;
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            UMImage image;
            image = new UMImage(activity, R.drawable.icon_share_pic);//网络图片
            image.compressStyle = UMImage.CompressStyle.SCALE;
            image.compressFormat = Bitmap.CompressFormat.PNG;

            switch (shareMedia) {
                case WEIXIN:
//                    UMMin umMin = new UMMin(url);
//                    //兼容低版本的网页链接
//                    umMin.setThumb(image);
//                    // 小程序消息封面图片
//                    umMin.setTitle(finalTitle);
//                    // 小程序消息title
//                    umMin.setDescription(finalContent);
//                    // 小程序消息描述
//                    umMin.setPath(Constant.WebPage.SHARE_WX_MINI_PATH + code);
//                    //小程序页面路径
//                    umMin.setUserName(Constant.WebPage.SHARE_WX_MINI_ID);
//                    // 小程序原始id,在微信平台查询
//                    if (App.devEnv && App.getDev() == 1) {
//                        Config.setMiniPreView();
//                    }
//                    new ShareAction(activity)
//                            .withMedia(umMin)
//                            .setPlatform(shareMedia)
//                            .setCallback(new UMShareListener() {
//                                @Override
//                                public void onStart(SHARE_MEDIA share_media) {
//
//                                }
//
//                                @Override
//                                public void onResult(SHARE_MEDIA share_media) {
//                                    ToastUtil.show("分享成功");
//                                }
//
//                                @Override
//                                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//                                    ToastUtil.show("分享失败");
//                                }
//
//                                @Override
//                                public void onCancel(SHARE_MEDIA share_media) {
//                                    ToastUtil.show("取消分享");
//                                }
//                            }).share();
//                    break;
                case SINA:
                case QQ:
                    UMWeb web = new UMWeb(url);
                    web.setTitle(finalTitle);//标题
                    web.setThumb(image);  //缩略图
                    web.setDescription(finalContent);//描述
                    new ShareAction(activity).setPlatform(shareMedia)
                            .withMedia(web)
                            .setCallback(new UMShareListener() {
                                @Override
                                public void onStart(SHARE_MEDIA share_media) {

                                }

                                @Override
                                public void onResult(SHARE_MEDIA share_media) {
                                    ToastUtil.show("分享成功");
                                }

                                @Override
                                public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                    ToastUtil.show("分享失败");
                                }

                                @Override
                                public void onCancel(SHARE_MEDIA share_media) {
                                    ToastUtil.show("取消分享");
                                }
                            })
                            .share();
                    break;
            }


            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Functions.emptyConsumer(), new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }


    /**
     * 分享
     *
     * @param activity 上下文
     */
    public static boolean shareImgs(Activity activity, List<Bitmap> bitmapList, SHARE_MEDIA shareMedia) {
        switch (shareMedia) {
            case QQ:
                if (!AppInstallUtil.isAppInstalled(activity, "com.tencent.mobileqq")) {
                    ToastUtil.show("请先安装QQ客户端");
                    return false;
                }
                break;
            case WEIXIN_CIRCLE:
            case WEIXIN:
                if (!AppInstallUtil.INSTANCE.isAppInstalled(activity, "com.tencent.mm")) {
                    ToastUtil.show("请先安装微信客户端");
                    return false;
                }
                break;
            case SINA:
                if (!AppInstallUtil.isAppInstalled(activity, "com.sina.weibo")) {
                    ToastUtil.show("请先安装微博客户端");
                    return false;
                }
                break;
        }
        if (shareMedia == SHARE_MEDIA.WEIXIN_CIRCLE) {
            return ShareManagerUtil.shareSingleImage(activity, bitmapList.get(0), shareMedia);
        } else {
            return ShareManagerUtil.setShareImage(activity, bitmapList, shareMedia);
        }
    }


    /**
     * 分享 多个文件
     */
    public static void shareFiles(Activity activity, List<File> fileList, SHARE_MEDIA shareMedia) {
        switch (shareMedia) {
            case QQ:
                if (!AppInstallUtil.isAppInstalled(activity, "com.tencent.mobileqq")) {
                    ToastUtil.show("请先安装QQ客户端");
                    return;
                }
                break;
            case WEIXIN:
                if (!AppInstallUtil.INSTANCE.isAppInstalled(activity, "com.tencent.mm")) {
                    ToastUtil.show("请先安装微信客户端");
                    return;
                }
                break;
            case SINA:
                if (!AppInstallUtil.isAppInstalled(activity, "com.sina.weibo")) {
                    ToastUtil.show("请先安装微博客户端");
                    return;
                }
                break;
        }
        if (shareMedia == SHARE_MEDIA.WEIXIN_CIRCLE) {
            ShareManagerUtil.shareSingleFile(activity, fileList.get(0), shareMedia);
        } else {
            ShareManagerUtil.shareFiles(activity, fileList, shareMedia);
        }

    }

    public static void shareVideo(Activity activity, String video, SHARE_MEDIA shareMedia) {
        switch (shareMedia) {
            case QQ:
                if (!AppInstallUtil.isAppInstalled(activity, "com.tencent.mobileqq")) {
                    ToastUtil.show("请先安装QQ客户端");
                    return;
                }
                break;
            case WEIXIN:
            case WEIXIN_CIRCLE:
                if (!AppInstallUtil.INSTANCE.isAppInstalled(activity, "com.tencent.mm")) {
                    ToastUtil.show("请先安装微信客户端");
                    return;
                }
                break;
            case SINA:
                if (!AppInstallUtil.isAppInstalled(activity, "com.sina.weibo")) {
                    ToastUtil.show("请先安装微博客户端");
                    return;
                }
                break;
        }

        UMVideo umVideo = new UMVideo(video);
        new ShareAction(activity).setPlatform(shareMedia)
                .withMedia(umVideo)
                .share();

    }

    public static byte[] getHtmlByteArray(final String url) {
        InputStream inStream;
        byte[] data;
        try {
            Response response = RestClient.getHttpClient().newCall(new Request.Builder().url(url).build()).execute();
            inStream = response.body().byteStream();
            data = inputStreamToByte(inStream);
        } catch (Exception e) {
            data = bmpToByteArray(BitmapFactory.decodeResource(LContext.getContext().getResources(), R.mipmap.ic_launcher), true);
        }
        return data;
    }

    public static byte[] inputStreamToByte(InputStream is) {
        try {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            return imgdata;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}

