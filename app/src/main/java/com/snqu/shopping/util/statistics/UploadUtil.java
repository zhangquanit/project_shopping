package com.snqu.shopping.util.statistics;

import android.annotation.SuppressLint;
import android.util.Base64;

import com.android.util.log.LogUtil;
import com.google.gson.Gson;
import com.snqu.shopping.data.DataConfig;
import com.snqu.shopping.data.base.RestClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 日志上传
 *
 * @author 张全
 */
public class UploadUtil {
    private static String URL = DataConfig.LOG_HOST + "api/report/log";
    private static boolean GZIP = false;

    public static void log(StatisticInfo statisticInfo) {
        List<StatisticInfo> list = new ArrayList<>();
        list.add(statisticInfo);
        String str = new Gson().toJson(list);
        upload(str);
    }

    /**
     * 日志上传
     */
    @SuppressLint("CheckResult")
    private static void upload(String source) {
//        String path = URL;
//        if (GZIP) {
//            path = URL + "?f=1";//压缩
//        }
//        final String url = path;
//        Observable.create(new ObservableOnSubscribe<Object>() {
//            @Override
//            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
//                upload(url, source);
//                emitter.onNext("发送成功");
//                emitter.onComplete();
//            }
//        }).subscribeOn(Schedulers.io())
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(Object o) throws Exception {
//
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        throwable.printStackTrace();
//                        LogUtil.d("日志发送失败");
//                    }
//                });

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), source);
        Request request = new Request.Builder().url(URL)
                .post(requestBody)
                .build();
        Call call = RestClient.getHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                LogUtil.d("日志发送失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.d("日志发送成功");
            }
        });

    }

    private static void upload(String url, String source) throws Exception {
        byte[] gzip = null;
        if (GZIP) {
            gzip = gzip(source);
        } else {
            gzip = source.getBytes();
        }
        java.net.URL uri = new URL(url);
        HttpsURLConnection urlConnection = (HttpsURLConnection) uri.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        urlConnection.setRequestProperty("Content-Length", String.valueOf(gzip.length));
        urlConnection.setConnectTimeout(2000);
        urlConnection.setReadTimeout(5000);

        OutputStream outputStream = urlConnection.getOutputStream();
        outputStream.write(gzip);
        outputStream.flush();
        outputStream.close();

        int statusCode = urlConnection.getResponseCode();
        if (statusCode == 200) {
            LogUtil.d("日志发送成功");
        } else {
            LogUtil.d("日志发送失败 statusCode=" + statusCode);
        }
    }


    private static byte[] gzip(String data) throws Exception {
        //GZIP压缩
        byte[] bytes = data.getBytes();
        ByteArrayOutputStream os = new ByteArrayOutputStream(bytes.length);
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(bytes);
        gos.flush();
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }


    static String KEY = "sndo@koudaigou12"; //AED的加密密钥长度要求是16
    static String IV = "1234567890123456";    // 向量 使用密钥

    public static String encrypt(String source) throws Exception {

        byte[] raw = KEY.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(KEY.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

        byte[] encrypted = cipher.doFinal(source.getBytes());
        return new String(encrypted, "utf-8");
    }

    public static String decrypt(String data) throws Exception {
        try {

            byte[] raw = KEY.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(KEY.getBytes());

            byte[] encrypted1 = Base64.decode(data, Base64.DEFAULT);

            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(encrypted1);

            String originalString = new String(original);
            return originalString;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
