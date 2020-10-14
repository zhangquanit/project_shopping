package com.snqu.shopping.data.base;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.android.util.LContext;
import com.android.util.encode.MD5;
import com.android.util.log.LogUtil;
import com.android.util.os.DeviceUtil;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.data.DataConfig;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.util.location.LocationEntity;
import com.snqu.shopping.util.location.LocationUtil;
import com.snqu.shopping.util.log.LogClient;
import com.snqu.shopping.util.statistics.AnalysisUtil;
import com.ta.utdid2.device.UTDevice;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import mtopsdk.common.util.StringUtils;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * OkHttp请求
 *
 * @author 张全
 */
public final class RestClient {
    private static final String TAG = "RestClient";
    public static final int TIMEOUT_CONNECTION = 20; //连接超时
    public static final int TIMEOUT_READ = 20; //读取超时A
    public static final int TIMEOUT_WRITE = 20; //写入超时

    private static final Charset UTF8 = Charset.forName("UTF-8");
    public static final String REST_API_URL = DataConfig.API_HOST;
    private static Retrofit s_retrofit;
    private static OkHttpClient fileDownloadClient;
    private static OkHttpClient imgDownloadClient;
    public static final String APPID = "200201";
    private static final String APPKEY = "HAA@OwBhZ!aZV!wmUKsG2FZjYEU!gO&&";
    public static final String APP_SOURCE = "2";


    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .proxy(Proxy.NO_PROXY)
                .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS)
                .addInterceptor(new HeaderIntercepter());

        if (DataConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
            //            //用于模拟弱网的拦截器
            //            builder.addNetworkInterceptor(new DoraemonWeakNetworkInterceptor())
            //                    //网络请求监控的拦截器
            //                    .addInterceptor(new DoraemonInterceptor());
        }
        getUnsafeOkHttpClient(builder);
        OkHttpClient client = builder.build();

        s_retrofit = new Retrofit.Builder()
                .baseUrl(REST_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        //        changeUrl();
    }

    public static OkHttpClient getDownloadClient() {
        if (null == fileDownloadClient) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT_WRITE, TimeUnit.SECONDS);
            getUnsafeOkHttpClient(builder);
            fileDownloadClient = builder.build();
        }
        return fileDownloadClient;
    }

    public static OkHttpClient getImgDownloadClient() {
        if (null == imgDownloadClient) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
                    .readTimeout(50, TimeUnit.SECONDS)
                    .writeTimeout(50, TimeUnit.SECONDS);
            getUnsafeOkHttpClient(builder);
            imgDownloadClient = builder.build();
        }
        return imgDownloadClient;
    }

    private static void changeUrl() {
        try {
            Class<Retrofit> retrofitClass = Retrofit.class;
            Field baseUrl = retrofitClass.getDeclaredField("baseUrl");
            baseUrl.setAccessible(true);
            baseUrl.set(s_retrofit, HttpUrl.get("https://api-t.xin1.cn/"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public static void getUnsafeOkHttpClient(OkHttpClient.Builder builder) {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    static class HeaderIntercepter implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();

            String token = UserClient.getToken();
            long timestamp = System.currentTimeMillis() / 1000;
            String sign = signRequest(request, timestamp);


            Request.Builder builder = request.newBuilder();
            builder.addHeader("x-app-source", APP_SOURCE); // 1 星口袋 2 星乐桃
            builder.addHeader("x-sign", sign); //签名
            builder.addHeader("x-timestamp", "" + timestamp);
            builder.addHeader("x-appid", APPID);
            builder.addHeader("x-m", AnalysisUtil.getUniqueId());//设备id


            if (!TextUtils.isEmpty(token)) {
                builder.addHeader("Authorization", token);
            }

            builder.addHeader("User-Agent", AnalysisUtil.getUA());
            builder.addHeader("Cache-Control", CacheControl.FORCE_NETWORK.toString());
            builder.addHeader("sgn", DeviceUtil.getSignature()); //签名校验
            builder.addHeader("pkg", DeviceUtil.getPkgName()); //包名校验
            builder.addHeader("appVersion", LContext.versionName); //版本号
            builder.addHeader("client-v", LContext.versionName); //版本号
            builder.addHeader("channel", LContext.channel);//渠道
            builder.addHeader("check-enable", "1");
            builder.addHeader("dev-type", "1");// 1 安卓  ,  2 IOS ,  3 其他
            builder.addHeader("client-type", "2");//1,微信小程序 , 2  APP

            LocationEntity location = LocationUtil.getLocation();
            if (null != location) {
                builder.addHeader("x-lng", location.longitude);
                builder.addHeader("x-lat", location.latitude);
                builder.addHeader("x-city-code", location.cityCode);
            }
            String utdid = UTDevice.getUtdid(LContext.getContext());
            if (!TextUtils.isEmpty(utdid)) {
                builder.addHeader("x-utdid", utdid);
            }

            request = builder.build();
            Response response = chain.proceed(request);
            checkResponse(request.url().toString(), response);
            return response;
        }
    }

    private static void checkResponse(String url, Response response) {
        try {
            ResponseBody body = response.body();
            BufferedSource source = body.source();
            Headers headers = response.headers();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
                GzipSource gzippedResponseBody = null;
                try {
                    gzippedResponseBody = new GzipSource(buffer.clone());
                    buffer = new Buffer();
                    buffer.writeAll(gzippedResponseBody);
                } finally {
                    if (gzippedResponseBody != null) {
                        gzippedResponseBody.close();
                    }
                }
            }
            Charset charset = UTF8;
            MediaType contentType = body.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            String responseStr = buffer.clone().readString(charset);
            LogClient.logResponse(url, responseStr);
            int code = new JSONObject(responseStr).optInt("code");
            if (code == 201) { //登录失效
                UserClient.loginOut();
                EventBus.getDefault().post(new PushEvent(Constant.Event.LOGIN_OUT));
                LContext.getContext().sendBroadcast(new Intent(LContext.pkgName + ".action.login"));
            }
            //            else if (code == -1000) { //apk被攥改
            //                LContext.getContext().sendBroadcast(new Intent(LContext.pkgName + ".action.apchange"));
            //            }
        } catch (Exception e) {
        }
    }

    private static String signRequest(Request request, long timestamp) {
        String sign = null;
        if ("POST".equals(request.method())) {
            sign = signPostRequest(request, timestamp);
        } else if ("GET".equals(request.method())) {
            sign = signGetRequest(request, timestamp);
        }
        return sign;
    }

    /**
     * 对post请求添加统一参数
     */
    private static String signPostRequest(Request request, long timestamp) {
        LogUtil.e(TAG, "rebuildPostRequest");
        String url = request.url().toString();
        if (url.contains("https://report")) {
            return "";
        }
        Map<String, String> params = new HashMap<>();
        RequestBody originalRequestBody = request.body();
        try {
            if (originalRequestBody instanceof FormBody) { // 传统表单
                FormBody requestBody = (FormBody) request.body();
                int fieldSize = requestBody == null ? 0 : requestBody.size();
                for (int i = 0; i < fieldSize; i++) {
                    params.put(requestBody.name(i), requestBody.value(i));
                }
                LogUtil.e(TAG, "FormBody params=" + params);
            } else if (originalRequestBody instanceof MultipartBody) {
                List<MultipartBody.Part> parts = ((MultipartBody) originalRequestBody).parts();
                if (parts != null && parts.size() >= 2) {
                    MultipartBody.Part part = parts.get(0);
                    params.put("save_type", getParamContent(part.body()));
                }
            } else {
                if (originalRequestBody.contentLength() > 0) {
                    String paramContent = getParamContent(originalRequestBody);
                    if (paramContent.startsWith("[")) { //JsonArray
                        return "";
                    }
                    JSONObject jsonObject = new JSONObject(paramContent);
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        String value = jsonObject.optString(key);
                        params.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return signParam(request.url().toString(), params, timestamp);
    }

    /**
     * 获取常规post请求参数
     */
    private static String getParamContent(RequestBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        return buffer.readUtf8();
    }

    /**
     * 对get请求做统一参数处理
     */
    private static String signGetRequest(Request request, long timestamp) {
        String url = request.url().toString();
        HashMap<String, String> paramValues = new HashMap<>();
        if (url.contains("?")) { //包含参数
            Uri uri = Uri.parse(url);
            Set<String> queryParameterNames = uri.getQueryParameterNames();
            for (String parameter : queryParameterNames) {
                paramValues.put(parameter, uri.getQueryParameter(parameter));
            }

            if (url.contains("source_type[0]")) {
                String[] list = url.split("&");
                String source_type_1 = "";
                String source_type_2 = "";
                for (String str : list) {
                    if (str.contains("=")) {
                        String[] data = str.split("=");
                        if (data != null && data.length == 2) {
                            if (data[0].contains("source_type[0]")) {
                                source_type_1 = data[1];
                            }
                            if (data[0].contains("source_type[1]")) {
                                source_type_2 = data[1];
                            }
                        }
                    }
                }
                paramValues.remove("source_type[1]");
                paramValues.remove("source_type[0]");
                String type = "[\"" + source_type_1 + "\",\"" + source_type_2 + "\"]";
                paramValues.put("source_type", type);
            }
        }

        return signParam(url, paramValues, timestamp);
    }


    public static String signParam(String url, Map<String, String> param, long timestamp) {
        LogClient.logRequest(url, param);
        String $a = "";
        String $b = "";
        String $c = "";
        String $path = url;
        if (url.startsWith("http")) {
            $path = Uri.parse(url).getPath();
            $path = $path.substring(1, $path.length()); //去掉第一个/
        } else if (url.startsWith("/")) {
            $path = $path.substring(1, $path.length()); //去掉第一个/
        }
        Map<String, String> map = param;
        if (null != param && !param.isEmpty()) {
            try {
                List<Map.Entry<String, String>> itmes = new ArrayList<Map.Entry<String, String>>(map.entrySet());
                Collections.sort(itmes, new Comparator<Map.Entry<String, String>>() {
                    @Override
                    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                        return (o1.getKey().toString().compareTo(o2.getKey()));
                    }
                });
                StringBuffer sb = new StringBuffer();
                for (Map.Entry<String, String> item : itmes) {
                    if (StringUtils.isNotBlank(item.getKey())) {
                        String key = item.getKey();
                        String val = item.getValue();
                        sb.append(key + "=" + val);
                    }
                }
                // 对参数进行统一处理，去除掉字节数>=4的特殊符号
//                String[] contents = sb.toString().split("");
//                StringBuilder contentbuffer = new StringBuilder();
//                for (String s : contents) {
//                    if (s.getBytes().length < 4) {
//                        contentbuffer.append(s);
//                    }
//                }
                $a = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        LogUtils.e("$a="+$a);
        $a = MD5.MD5Encode($a);
//        LogUtils.e("$a1="+$a);
        if ($a.length() > 5) {
            $b = $a.substring(0, 5); //前5位
            $c = $a.substring($a.length() - 5, $a.length()); //后5位
        } else { //不足5位
            $b = $c = $a;
        }

        String $signText = $b + APPID + $path + $a + timestamp + $c + APPKEY;
        String sign = MD5.MD5Encode($signText);
        LogUtil.e(TAG, "$signText=" + $signText);
        LogUtil.e(TAG, "$signText=" + $signText + ",sign=" + sign + ",url=" + url);
        return sign;
    }

    public static <T> T getService(Class<T> serviceClass) {
        return s_retrofit.create(serviceClass);
    }

    public static OkHttpClient getHttpClient() {
        Call.Factory factory = s_retrofit.callFactory();
        return (OkHttpClient) factory;
    }


}
