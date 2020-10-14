package com.snqu.shopping.ui.order.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Environment
import android.text.TextUtils
import android.view.View
import com.android.util.encode.MD5
import com.snqu.shopping.App
import com.snqu.shopping.common.Constant
import com.snqu.shopping.ui.goods.fragment.ShareFragment.Companion.setWaterMark
import com.snqu.shopping.ui.mine.fragment.ExclusiveWatermarkFrag
import com.snqu.shopping.util.CommonUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * desc:
 * time: 2019/8/19
 * @author 银进
 */
object ImgUtils {
    //保存文件到指定路径
    fun saveImageToGallery(context: Context, bmp: Bitmap) {
        val fileName = "share_wx_img.jpg"
        saveImageToGalleryCheckExist(context, bmp, fileName)
    }

    /**
     * 保存图片到相册
     * 如果文件存在则不重新创建
     */
    fun saveImageRestoreToGallery(context: Context, bmp: Bitmap, name: String): File {
        val fileName = "xlt_${MD5.MD5Encode(name)}_.png"
        val appDir = File(Environment.getExternalStorageDirectory(), Constant.SD_DIR)
        val dir = File(appDir, "images_circle")
        dir.mkdirs()
        var file: File?
        file = File(dir, fileName)
        if (null != file && file.length() > 0) {
            file.delete()
        }
        try {
            val fos = FileOutputStream(file)
            //通过io流的方式来压缩保存图片
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()

            //其次把文件插入到系统图库
            CommonUtil.notifyFileToGallery(context, file)
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            file.deleteOnExit()
            file = null
        }
        return file
    }


    /**
     * 仅仅用于保存图片不能删除（防止重复，相同的名字先删除以前的在保存）
     */
    @JvmStatic
    fun saveImageToGalleryCheckExist(context: Context, bmp: Bitmap, name: String): File? {
        // 首先保存图片
        var file: File? = null

        val fileName = "xlt_${MD5.MD5Encode(name)}_.png"
        val appDir = File(Environment.getExternalStorageDirectory(), Constant.SD_DIR)
        val dir = File(appDir, Constant.SAVE_FILE)
        if (dir.mkdirs() || dir.isDirectory) {
            file = File(dir, fileName)
        }
        if (null != file && file.length() > 0) {
            //已经存在说明不用在存了
            return file
        }
        try {
            val fos = FileOutputStream(file)
            //通过io流的方式来压缩保存图片
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()

            //其次把文件插入到系统图库
            CommonUtil.notifyFileToGallery(context, file)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return file
    }

    /**
     * 仅仅用于保存图片不能删除（防止重复，相同的名字先删除以前的在保存）
     */
    fun saveImageToGalleryCheckExisttoWaterMark(context: Context, bmp: Bitmap, name: String): File? {
        // 首先保存图片
        var file: File? = null

        val fileName = "xlt_${MD5.MD5Encode(name)}_.png"
        val appDir = File(Environment.getExternalStorageDirectory(), Constant.SD_DIR)
        val dir = File(appDir, Constant.SAVE_FILE)
        if (dir.mkdirs() || dir.isDirectory) {
            file = File(dir, fileName)
        }
        if (null != file && file.length() > 0) {
            //已经存在说明不用在存了
            return file
        }
        val name = Constant.water_name
        var markBitmap = bmp
        if (!TextUtils.isEmpty(name)) {
            val newBitmap = setWaterMark(App.mApp.resources, bmp, name)
            if (newBitmap != null) {
                markBitmap = newBitmap
            }
        }
        try {
            val fos = FileOutputStream(file)
            //通过io流的方式来压缩保存图片
            markBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()

            //其次把文件插入到系统图库
            CommonUtil.notifyFileToGallery(context, file)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return file
    }

    fun viewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return bitmap
    }
}