package com.sndo.czbwebview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class MapUtil {

    public static final String PN_GAODE_MAP = "com.autonavi.minimap";// 高德地图包名
    public static final String PN_BAIDU_MAP = "com.baidu.BaiduMap"; // 百度地图包名
    public static final String PN_TENCENT_MAP = "com.tencent.map"; // 腾讯地图包名

    /**
     * 检查地图应用是否安装
     *
     * @return
     */
    public static boolean isGdMapInstalled() {
        return isInstallPackage(PN_GAODE_MAP);
    }

    public static boolean isBaiduMapInstalled() {
        return isInstallPackage(PN_BAIDU_MAP);
    }

    public static boolean isTencentMapInstalled() {
        return isInstallPackage(PN_TENCENT_MAP);
    }

    private static boolean isInstallPackage(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }


    public static boolean openMap(Context context, String slat, String slon, String sname, String dlat, String dlon, String dname) {
        //高德地图
        try {
            if (isGdMapInstalled()) {
                openGaoDeNavi(context, slat, slon, sname, dlat, dlon, dname);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //百度地图
        try {
            if (isBaiduMapInstalled()) {
                openBaiDuNavi(context, slat, slon, sname, dlat, dlon, dname);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //腾讯地图
        try {
            if (isTencentMapInstalled()) {
                openTencentMap(context, slat, slon, sname, dlat, dlon, dname);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 打开高德地图导航功能
     *
     * @param context
     * @param slat    起点纬度
     * @param slon    起点经度
     * @param sname   起点名称 可不填（0,0，null）
     * @param dlat    终点纬度
     * @param dlon    终点经度
     * @param dname   终点名称 必填
     */
    public static void openGaoDeNavi(Context context, String slat, String slon, String sname, String dlat, String dlon, String dname) {
        String uriString = null;
        StringBuilder builder = new StringBuilder("amapuri://route/plan?sourceApplication=maxuslife");

        builder.append("&sname=").append(sname)
                .append("&slat=").append(slat)
                .append("&slon=").append(slon);

        builder.append("&dlat=").append(dlat)
                .append("&dlon=").append(dlon)
                .append("&dname=").append(dname)
                .append("&dev=0")
                .append("&t=0");
        uriString = builder.toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(PN_GAODE_MAP);
        intent.setData(Uri.parse(uriString));
        context.startActivity(intent);
    }

    /**
     * 打开腾讯地图
     * params 参考http://lbs.qq.com/uri_v1/guide-route.html
     *
     * @param context
     * @param slat    起点纬度
     * @param slon    起点经度
     * @param sname   起点名称 可不填（0,0，null）
     * @param dlat    终点纬度
     * @param dlon    终点经度
     * @param dname   终点名称 必填
     *                驾车：type=drive，policy有以下取值
     *                0：较快捷
     *                1：无高速
     *                2：距离
     *                policy的取值缺省为0
     *                &from=" + dqAddress + "&fromcoord=" + dqLatitude + "," + dqLongitude + "
     */
    public static void openTencentMap(Context context, String slat, String slon, String sname, String dlat, String dlon, String dname) {
        String uriString = null;
        StringBuilder builder = new StringBuilder("qqmap://map/routeplan?type=drive&policy=0&referer=zhongshuo");

        builder.append("&from=").append(sname)
                .append("&fromcoord=").append(slat)
                .append(",")
                .append(slon);

        builder.append("&to=").append(dname)
                .append("&tocoord=").append(dlat)
                .append(",")
                .append(dlon);
        uriString = builder.toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(PN_TENCENT_MAP);
        intent.setData(Uri.parse(uriString));
        context.startActivity(intent);
    }

    /**
     * 打开百度地图导航功能(默认坐标点是高德地图，需要转换)
     *
     * @param context
     * @param slat    起点纬度
     * @param slon    起点经度
     * @param sname   起点名称 可不填（0,0，null）
     * @param dlat    终点纬度
     * @param dlon    终点经度
     * @param dname   终点名称 必填
     */
    public static void openBaiDuNavi(Context context, String slat, String slon, String sname, String dlat, String dlon, String dname) {
        String uriString = null;
        StringBuilder builder = new StringBuilder("baidumap://map/direction?mode=driving&");


        builder.append("origin=latlng:")
                .append(slat)
                .append(",")
                .append(slon)
                .append("|name:")
                .append(sname);

        builder.append("&destination=latlng:")
                .append(dlat)
                .append(",")
                .append(dlon)
                .append("|name:")
                .append(dname);
        uriString = builder.toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(PN_BAIDU_MAP);
        intent.setData(Uri.parse(uriString));
        context.startActivity(intent);
    }
}