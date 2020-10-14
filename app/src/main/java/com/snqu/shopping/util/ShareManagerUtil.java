package com.snqu.shopping.util;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.snqu.shopping.common.Constant;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * desc:
 * time: 2019/11/23
 *
 * @author 银进
 */
public class ShareManagerUtil {
    public static File storageDir = new File(Environment.getExternalStorageDirectory(), Constant.SD_DIR + "/share_detail");
    public static File childFile;
    public static int i;

    public static boolean shareSingleFile(Context ctx, File file, SHARE_MEDIA shareMedia) {
        Intent intent = new Intent();
        ComponentName comp;
        switch (shareMedia) {
            case WEIXIN:
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                break;
            case WEIXIN_CIRCLE:
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                break;
            case SINA:
                comp = new ComponentName("com.sina.weibo", "com.sina.weibo.composerinde.ComposerDispatchActivity");
                break;
            default:
                comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
        }
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND);
//        intent.addCategory(Intent.ACTION_VIEW);
        Uri imageUri;
        if (file.getAbsolutePath().endsWith(".mp4")) {
            imageUri = getVideoContentUri(ctx, file);
            intent.setType("video/*");
        } else {
            imageUri = getImageContentUri(ctx, file);
            intent.setType("image/*");
        }
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        try {
            ctx.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean shareSingleImage(Context mContext, Bitmap bitmap, SHARE_MEDIA shareMedia) {
        deletePic(storageDir);
        childFile = new File(storageDir, System.currentTimeMillis() + "");
        if (!childFile.exists()) {
            childFile.mkdir();
        }
        i = 0;
        Intent intent = new Intent();
        ComponentName comp;
        switch (shareMedia) {
            case WEIXIN:
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                break;
            case WEIXIN_CIRCLE:
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                break;
            case SINA:
                comp = new ComponentName("com.sina.weibo", "com.sina.weibo.composerinde.ComposerDispatchActivity");
                break;
            default:
                comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
        }
        Uri file = saveImageToSdCard(mContext, bitmap);
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND);
        intent.addCategory(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, file);
        try {
            mContext.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void shareFiles(Context ctx, List<File> files, SHARE_MEDIA shareMedia) {
        Intent intent = new Intent();
        ComponentName comp;
        switch (shareMedia) {
            case WEIXIN:
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                break;
            case WEIXIN_CIRCLE:
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                break;
            case SINA:
                comp = new ComponentName("com.sina.weibo", "com.sina.weibo.composerinde.ComposerDispatchActivity");
                break;
            default:
                comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
        }
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);

        boolean shareImg = true;
        ArrayList<Uri> imageUris = new ArrayList<>();
        int i = 0;
        for (File file : files) {
            i++;
            if (i > 9) {
                break;
            }
            if (file.getAbsolutePath().endsWith(".mp4")) {
                shareImg = false;
                imageUris.add(getVideoContentUri(ctx, file));
            } else {
                imageUris.add(getImageContentUri(ctx, file));
            }

        }

        if (shareImg) {
            intent.setType("image/*");
        } else {
            intent.setType("video/*");
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        try {
            ctx.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean setShareImage(Context mContext, List<Bitmap> bitmapList, SHARE_MEDIA shareMedia) {
        deletePic(storageDir);
        childFile = new File(storageDir, System.currentTimeMillis() + "");
        if (!childFile.exists()) {
            childFile.mkdir();
        }
        i = 0;
        Intent intent = new Intent();
        ComponentName comp = null;
        switch (shareMedia) {
            case WEIXIN:
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
                break;
            case WEIXIN_CIRCLE:
                comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                break;
            case SINA:
                comp = new ComponentName("com.sina.weibo", "com.sina.weibo.composerinde.ComposerDispatchActivity");
                break;
            case QQ:
                comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
        }
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i = 0; i < ((bitmapList.size() >= 9) ? 9 : bitmapList.size()); i++) {
            Uri file = saveImageToSdCard(mContext, bitmapList.get(i));
            if (file != null) {
                uris.add(file);
            }
        }
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        try {
            mContext.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 将bitmap保存到本地
     *
     * @param bitmap bitmap
     * @return 文件
     */
    private static Uri saveImageToSdCard(Context mContext, Bitmap bitmap) {
        boolean success = false;
        File file = null;
        try {
            file = createStableImageFile();
            FileOutputStream outStream;
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                MediaScannerConnection.scanFile(mContext, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(uri);
                        mContext.sendBroadcast(intent);
                    }
                });
            } else {
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(storageDir.getAbsoluteFile())));
            }
        }
        if (success) {
//            Uri uri;
//            Log.e("Uri:", file.length() + "");
//            if (Build.VERSION.SDK_INT >= 24) {
//                uri = FileProvider.getUriForFile(mContext, LContext.getString(R.string.fileprovider_authority), file);
//            } else {
//                uri = Uri.fromFile(file);
//            }
            return getImageContentUri(mContext, file);
        } else {
            return null;
        }
    }

    /**
     * 创建本地保存路径
     *
     * @return 文件
     */
    private static File createStableImageFile() {
        i++;
        String imageFileName = System.currentTimeMillis() + ".png";
        if (childFile.exists()) {
            childFile.delete();
        }
        childFile.mkdirs();
        File image = new File(childFile, imageFileName);
        return image;
    }

    /**
     * 删除文件里面的内容
     *
     * @param storageDir
     */
    private static void deletePic(File storageDir) {
        if (storageDir.isDirectory()) {
            File[] files = storageDir.listFiles();

            for (int j = 0; j < files.length; j++) {
                File f = files[j];
                deletePic(f);
            }
        } else {
            storageDir.delete();
        }
    }

    private static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        Uri uri = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }

            cursor.close();
        }

        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        return uri;
    }

    private static Uri getVideoContentUri(Context context, File videoFile) {
        Uri uri = null;
        String filePath = videoFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media._ID}, MediaStore.Video.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/video/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }

            cursor.close();
        }

        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        }

        return uri;
    }
}
