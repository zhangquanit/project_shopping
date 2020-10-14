package com.snqu.shopping.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * desc:
 * time: 2019/9/9
 *
 * @author 银进
 */
public class BitmapUtil {

    public static Bitmap big(Bitmap b,float x,float y)
    {
        int w=b.getWidth();
        int h=b.getHeight();
        float sx=(float)x/w;//要强制转换，不转换我的在这总是死掉。
        float sy=(float)y/h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w,
                h, matrix, true);
        return resizeBmp;
    }

    public static Bitmap getGreyImage(Bitmap old) {
        int width, height;
        height = old.getHeight();
        width = old.getWidth();
        Bitmap newBitmap= Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(old);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.5f);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(newBitmap, 0, 0, paint);
        return newBitmap;
    }
}
