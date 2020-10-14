package com.snqu.shopping.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.Target;
import com.snqu.shopping.R;

/**
 * desc:
 * time: 2019/1/14
 *
 * @author 银进
 */
public class GlideUtil {


    /**
     * 加载图片
     *
     * @param imageView ImageView
     * @param url       图片地址也可以是path
     */
    public static void loadPic(ImageView imageView, String url) {
        try {
            url = checkUrl(url);
            Glide.with(imageView.getContext())
                    .load(url)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 加载图片
     *
     * @param imageView ImageView
     * @param path      图片地址也可以是path
     */
    public static void loadLocalPic(ImageView imageView, String path) {
        try {
            Glide.with(imageView.getContext())
                    .load(path)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载图片
     *
     * @param imageView      ImageView
     * @param url            图片地址也可以是path
     * @param errorRes       errorRes
     * @param placeHolderRes placeHolderRes
     */
    public static void loadPic(ImageView imageView, String url, @DrawableRes int errorRes, @DrawableRes int placeHolderRes) {
        try {
            url = checkUrl(url);
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(new RequestOptions().error(errorRes).placeholder(placeHolderRes)
                    )
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载图片
     *
     * @param imageView      ImageView
     * @param url            图片地址也可以是path
     * @param errorRes       errorRes
     * @param placeHolderRes placeHolderRes
     */
    public static void loadLocalPic(ImageView imageView, String url, @DrawableRes int errorRes, @DrawableRes int placeHolderRes) {
        try {
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(new RequestOptions().error(errorRes).placeholder(placeHolderRes)
                    )
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadPic(ImageView imageView,Bitmap bitmap, @DrawableRes int errorRes, @DrawableRes int placeHolderRes) {
        try {
            Glide.with(imageView.getContext())
                    .load(bitmap)
                    .apply(new RequestOptions().error(errorRes).placeholder(placeHolderRes)
                    )
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadPic(ImageView imageView,int res) {
        try {
            Glide.with(imageView.getContext())
                    .load(res)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadPic(ImageView imageView,Bitmap bitmap) {
        try {
            Glide.with(imageView.getContext())
                    .load(bitmap)
                    .apply(new RequestOptions().error(R.drawable.icon_max_default_pic).placeholder(R.drawable.icon_max_default_pic)
                    )
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadDetailPic(ImageView imageView, String url, @DrawableRes int errorRes, @DrawableRes int placeHolderRes) {
        try {
            url = checkUrl(url);
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(new RequestOptions()
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    )
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 加载图片
     *
     * @param imageView      ImageView
     * @param url            图片地址也可以是path
     * @param errorRes       errorRes
     * @param placeHolderRes placeHolderRes
     */
    public static void loadPic(ImageView imageView, Uri url, @DrawableRes int errorRes, @DrawableRes int placeHolderRes) {
        try {
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(new RequestOptions().error(errorRes).placeholder(placeHolderRes))
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载图片
     *
     * @param imageView      ImageView
     * @param url            图片地址也可以是path
     * @param errorRes       errorRes
     * @param placeHolderRes placeHolderRes
     */
    public static void loadRoundPic(ImageView imageView, String url, @DrawableRes int errorRes, @DrawableRes int placeHolderRes) {
        try {
            url = checkUrl(url);
//            if (url.contains(".gif")) {
//                loadPic(imageView, url);
//            } else {
            //设置图片圆角角度
            RequestOptions roundOptions = new RequestOptions()
                    .error(errorRes)
                    .centerCrop()
                    .placeholder(placeHolderRes)
                    .transform(new GlideRoundTransform(imageView.getContext(), 20));
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(roundOptions)
                    .thumbnail(loadTransform(imageView.getContext(), errorRes, 20))
                    .thumbnail(loadTransform(imageView.getContext(), placeHolderRes, 20))
                    .into(imageView);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadRoundPic(ImageView imageView, String url, @DrawableRes int errorRes, @DrawableRes int placeHolderRes,int dp) {
        try {
            url = checkUrl(url);
//            if (url.contains(".gif")) {
//                loadPic(imageView, url);
//            } else {
            //设置图片圆角角度
            RequestOptions roundOptions = new RequestOptions()
                    .error(errorRes)
                    .centerCrop()
                    .placeholder(placeHolderRes)
                    .transform(new GlideRoundTransform(imageView.getContext(), dp));
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(roundOptions)
                    .thumbnail(loadTransform(imageView.getContext(), errorRes, dp))
                    .thumbnail(loadTransform(imageView.getContext(), placeHolderRes, dp))
                    .into(imageView);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static RequestBuilder<Drawable> loadTransform(Context context, @DrawableRes int placeholderId, int radius) {
        return Glide.with(context)
                .load(placeholderId)
                .apply(new RequestOptions().centerCrop()
                        .transform(new GlideRoundTransform(context, radius)));
    }


    /**
     * 加载图片
     *
     * @param imageView      ImageView
     * @param url            图片地址也可以是path
     * @param errorRes       errorRes
     * @param placeHolderRes placeHolderRes
     */
    public static void loadPicFit(ImageView imageView, String url, @DrawableRes int errorRes, @DrawableRes int placeHolderRes) {
        try {
            url = checkUrl(url);
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).error(errorRes).placeholder(placeHolderRes))
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 加载图片
     *
     * @param imageView       ImageView
     * @param url             图片地址也可以是path
     * @param errorRes        errorRes
     * @param placeHolderRes  placeHolderRes
     * @param requestListener 图片加载监听
     */
    public static void loadPic(ImageView imageView, String url, @DrawableRes int errorRes, @DrawableRes int placeHolderRes, @Nullable RequestListener<Drawable> requestListener) {
        try {
            url = checkUrl(url);
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(new RequestOptions().error(errorRes).placeholder(placeHolderRes))
                    .listener(requestListener)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载图片
     *
     * @param imageView       ImageView
     * @param url             图片地址也可以是path
     * @param errorRes        errorRes
     * @param placeHolderRes  placeHolderRes
     * @param requestListener 图片加载监听
     */
    public static void loadBitmapPic(ImageView imageView, String url, @DrawableRes int errorRes, @DrawableRes int placeHolderRes, @Nullable RequestListener<Bitmap> requestListener) {
        try {
            url = checkUrl(url);
            Glide.with(imageView.getContext())
                    .asBitmap()
                    .load(url)
                    .apply(new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).skipMemoryCache(true).error(errorRes).placeholder(placeHolderRes))
                    .listener(requestListener)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadBitmap(Context ctx, String url, BaseTarget<Bitmap> viewTarget) {
        try {
            url = checkUrl(url);
            Glide.with(ctx)
                    .asBitmap()
                    .load(url)
                    .into(viewTarget);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String checkUrl(String url) {
        if (!TextUtils.isEmpty(url) && !url.contains("http")) {
            if (url.startsWith("//")) {
                url = "https:" + url.subSequence(1, url.length());
            } else if (url.startsWith("/")) {
                url = "https:/" + url.subSequence(1, url.length());
            } else {
                url = "https://" + url;
            }
        }
        return url;
    }
}
