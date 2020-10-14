package com.snqu.shopping;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.multidex.MultiDex;

import com.ali.auth.third.core.MemberSDK;
import com.ali.auth.third.core.callback.InitResultCallback;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeInitCallback;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.alibaba.baichuan.trade.common.AlibcMiniTradeCommon;
import com.android.util.LContext;
import com.android.util.ext.SPUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.kd.charge.entrance.KdCharge;
import com.kd.charge.entrance.config.ChargeConfiguration;
import com.kepler.jd.Listener.AsyncInitListener;
import com.kepler.jd.login.KeplerApiManager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.sndodata.analytics.android.sdk.PropertyBuilder;
import com.sndodata.analytics.android.sdk.SDConfigOptions;
import com.sndodata.analytics.android.sdk.SndoDataAPI;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.DataConfig;
import com.snqu.shopping.data.base.AppVersionChecker;
import com.snqu.shopping.ui.mall.address.helper.AreaData;
import com.snqu.shopping.util.GlideUtil;
import com.snqu.shopping.util.PushUtil;
import com.snqu.shopping.util.location.LocationService;
import com.snqu.shopping.util.log.LogClient;
import com.snqu.shopping.util.statistics.SndoData;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.library.Umeng;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.umcrash.UMCrash;
import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumConfig;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.AlbumLoader;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import cn.jiguang.jmlinksdk.api.JMLinkAPI;
import component.update.AppDownloadClient;
import component.update.AppVersionConfiguration;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import me.yokeyword.fragmentation.Fragmentation;

/**
 * desc:
 * time: 2019/1/5
 *
 */
public class App extends Application {

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            ClassicsHeader classicsHeader = new ClassicsHeader(context);
            classicsHeader.setBackgroundColor(Color.TRANSPARENT);
            return classicsHeader;
        });
    }

    private final String TAG = App.class.getSimpleName();
    public static String UMENG_CHANNEL;
    public static boolean devEnv = false;
    public static App mApp;
    public IWXAPI iwxapi;
    public String umengDeviceToken;
    public static boolean coldStart;


    @Override
    public void onCreate() {
        super.onCreate();

        //友盟推送需要在主进程和子进程进行注册，因此放在最前面
        initUmengPush();

        String mMainProcess = getPackageName();// 主进程
        String curProcessName = getCurProcessName();
        LogUtil.d("curProcessName=" + curProcessName + ",pid="
                + android.os.Process.myPid());
        //只有主进程才进行相关配置初始化，其他进程不管
        if (!mMainProcess.equals(curProcessName)) {
            return;
        }

        init();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void init() {
        coldStart = true;
        mApp = this;

        // 初始化配置
        initConfigs();
        // 内存优化
        heapUtilization();
        // 开启服务、任务
        initBackgroundTasks();
        initOthers();
        //图片选择
        initAlbum();
        // 检测版本
        AppVersionConfiguration configuration = new AppVersionConfiguration.Builder(this)
                .appIcon(R.mipmap.ic_launcher)
                .isDebug(BuildConfig.DEBUG)
                .setVersionChecker(new AppVersionChecker())
                .build();
        AppDownloadClient.getInstance().init(configuration);

        //极光魔链
        initMagicLink();
        //京东
        initJD();
        //阿里百川
        initAlibcSDK();
        //城市数据
        AreaData.parseData(this);
        //拦截异常
        Thread.UncaughtExceptionHandler exceptionHandler = Thread.currentThread().getUncaughtExceptionHandler();
        Thread.currentThread().setUncaughtExceptionHandler(new AppCrashHandler(exceptionHandler));
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (throwable instanceof CompositeException) { //打印异常
                    CompositeException exception = (CompositeException) throwable;
                    List<Throwable> exceptions = exception.getExceptions();
                    for (Throwable item : exceptions) {
                        item.printStackTrace();
                    }
                } else {
                    throwable.printStackTrace();
                }
            }
        });

        // 友盟统计
        initAnalytics();
        //腾讯bugly
//        initBugly();
        //深度数据采集
        initSndoDataAPI();
        //快电
        initKD();
        //LeakCanary
        //initLeakCanary();
    }

    private void initLeakCanary() {
//        if (App.devEnv) {
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                return;
//            }
//            LeakCanary.install(this);
//        }
    }

    private void initJD() {
        KeplerApiManager.asyncInitSdk(this, getString(R.string.jd_app_key), getString(R.string.jd_app_secret),
                new AsyncInitListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("Kepler", "Kepler asyncInitSdk onSuccess ");
                    }

                    @Override
                    public void onFailure() {

                        Log.e("Kepler",
                                "Kepler asyncInitSdk 授权失败，请检查lib 工程资源引用；包名,签名证书是否和注册一致=");

                    }
                });
    }

    private void initAlibcSDK() {
        AlibcTradeSDK.asyncInit(this, new AlibcTradeInitCallback() {
            @Override
            public void onSuccess() {
                LogUtil.d("AlibcUtil", "阿里百川初始化成功");
            }

            @Override
            public void onFailure(int code, String msg) {
                LogUtil.d("AlibcUtil", "阿里百川初始化失败 code=" + code + ",msg=" + msg);
            }
        });
        //淘客
        AlibcTaokeParams taokeParams = new AlibcTaokeParams("", "", "");
        taokeParams.setPid(LContext.getString(R.string.ali_pid));
        AlibcTradeSDK.setTaokeParams(taokeParams);


        if (TextUtils.equals(LContext.channel, Constant.PROD_TEST)) {
            MemberSDK.turnOnDebug();
        }
        MemberSDK.init(this, new InitResultCallback() {

            @Override
            public void onFailure(int code, String msg) {
                LogUtil.d("AlibcUtil", "MemberSDK初始化失败 code=" + code + ",msg=" + msg);
            }

            @Override
            public void onSuccess() {
                LogUtil.d("AlibcUtil", "MemberSDK初始化成功");
            }
        });

        if (TextUtils.equals(LContext.channel, Constant.PROD_TEST)) {
            AlibcMiniTradeCommon.turnOnDebug();
        }
    }

    /**
     * 初始化 Sensors Analytics SDK
     */
    private void initSndoDataAPI() {
        String serverUrl;
        if (TextUtils.equals(LContext.channel, Constant.PROD_TEST)) {
            serverUrl = "https://sd.sndo.com/p?project=zjxabyor";
        } else {
            serverUrl = "http://sd.sndo.com/p?service=sndo&project=ov3k17dq";
        }
        SDConfigOptions sdConfigOptions = new SDConfigOptions(serverUrl);
//        sdConfigOptions.enableTrackAppCrash();
        sdConfigOptions.enableJavaScriptBridge(true);//允许H5内的事件通过app发送
        sdConfigOptions.setChannel(LContext.channel);
//        sdConfigOptions.enableHeatMap(true); //开启点击图
//        sdConfigOptions.enableHeatMapConfirmDialog(true); //设置点击图提示对话框是否可用
        SndoDataAPI.startWithConfigOptions(this, sdConfigOptions);

        //信任服务端证书
//        try{
//            final TrustManager[] trustAllCerts = new TrustManager[]{
//                    new X509TrustManager() {
//                        @Override
//                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//                        }
//
//                        @Override
//                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//                        }
//
//                        @Override
//                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                            return new java.security.cert.X509Certificate[]{};
//                        }
//                    }
//            };
//
//            final SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//            SndoDataAPI.sharedInstance(this).setSSLSocketFactory(sslSocketFactory);
//        }catch(Exception e){
//            e.printStackTrace();
//        }

        // 打开自动采集, 并指定追踪哪些 AutoTrack 事件
        List<SndoDataAPI.AutoTrackEventType> eventTypeList = new ArrayList<>();
        // $AppStart
        eventTypeList.add(SndoDataAPI.AutoTrackEventType.APP_START);
        // $AppEnd
        eventTypeList.add(SndoDataAPI.AutoTrackEventType.APP_END);

        if (!TextUtils.equals(LContext.channel, Constant.PROD_TEST)) {
            // $AppClick
            eventTypeList.add(SndoDataAPI.AutoTrackEventType.APP_CLICK);
            // $AppViewScreen
            eventTypeList.add(SndoDataAPI.AutoTrackEventType.APP_VIEW_SCREEN);
        }
        SndoDataAPI.sharedInstance(this).enableLog(TextUtils.equals(LContext.channel, Constant.PROD_TEST));
        SndoDataAPI.sharedInstance(this).enableAutoTrack(eventTypeList);


        //初始化公共属性
        JSONObject jsonObject = PropertyBuilder.newInstance().append("client", "星乐桃APP").append("channel", LContext.channel).toJSONObject();
        SndoDataAPI.sharedInstance(LContext.getContext()).registerSuperProperties(jsonObject);
        SndoData.login();
    }

    /**
     * 快电初始化
     */
    private void initKD() {
        ChargeConfiguration configuration = new
                ChargeConfiguration.Builder()
                .context(this)
                .isDebug(false)//⾃主切换测试/⽣产环境
                .build();
        KdCharge.getInstance().init(configuration);
    }


    /**
     * 初始化图片选择框架
     */
    private void initAlbum() {
        Album.initialize(AlbumConfig.newBuilder(this)
                .setLocale(Locale.CHINA)
                .setAlbumLoader(new AlbumLoader() {
                    @Override
                    public void load(ImageView imageView, AlbumFile albumFile) {
                        GlideUtil.loadLocalPic(imageView, albumFile.getPath());
                    }

                    @Override
                    public void load(ImageView imageView, String url) {
                        GlideUtil.loadPic(imageView, url);
                    }
                }).build());

    }

    private void initMagicLink() {
        JMLinkAPI.getInstance().init(getApplicationContext());
        JMLinkAPI.getInstance().setDebugMode(BuildConfig.DEBUG);
    }

    /**
     * 初始化友盟统计
     */
    private void initAnalytics() {
        Umeng.initAnalytics(this, BuildConfig.DEBUG);
        iwxapi = Umeng.iwxapi;
    }

    private void initBugly() {
        CrashReport.initCrashReport(getApplicationContext(), "280e459010", BuildConfig.DEBUG);
    }

    private void initConfigs() {
        PackageManager pm = getPackageManager();
        int versionCode = 0;
        String versionName = null;
        String appName = null;


        try {
            ApplicationInfo info = pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            PackageInfo packInfo = pm.getPackageInfo(getPackageName(), 0);
            appName = pm.getApplicationLabel(info).toString();
            if (packInfo != null) {
                versionName = packInfo.versionName;
                versionCode = packInfo.versionCode;
            }
            if (null != info.metaData) {
                UMENG_CHANNEL = info.metaData.getString("UMENG_CHANNEL");
                devEnv = info.metaData.getBoolean("env", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LContext.init(this, BuildConfig.DEBUG);
        LContext.appIcon = R.mipmap.ic_launcher;
        LContext.appName = appName;
        LContext.pkgName = getPackageName();
        LContext.versionCode = versionCode;
        LContext.versionName = versionName;
        LContext.channel = UMENG_CHANNEL;

        DataConfig.DEBUG = BuildConfig.DEBUG;
        if (devEnv) {
            int dev = getDev();
            if (dev == 0) { //线上
                DataConfig.API_HOST = "https://api.xinletao.vip/";
                DataConfig.H5_HOST = "https://m.xinletao.vip/";
                DataConfig.LOG_HOST = "https://report.xin1.cn/";
                DataConfig.H5_ACT_HOST = "https://ac.xinletao.vip/";
            } else if (dev == 1) { //测试
                DataConfig.API_HOST = "https://api-t.xin1.cn/";
                DataConfig.H5_HOST = "https://m-xlt-t.xin1.cn/";
                DataConfig.LOG_HOST = "https://report-t.xin1.cn/";
                DataConfig.H5_ACT_HOST = "https://ac-t.xin1.cn/";
            } else if (dev == 2) { //开发
                DataConfig.API_HOST = "https://api-dev.xin1.cn/";
                DataConfig.H5_HOST = "https://m-dev.xin1.cn/";
                DataConfig.LOG_HOST = "https://report-t.xin1.cn/";
                DataConfig.H5_ACT_HOST = "https://ac-t2.xin1.cn/";
            } else if (dev == 3) { //预发
                DataConfig.API_HOST = "https://api-t2.xin1.cn/";
                DataConfig.H5_HOST = "https://m-xlt-t2.xin1.cn/";
                DataConfig.LOG_HOST = "https://report-t2.xin1.cn/";
                DataConfig.H5_ACT_HOST = "https://ac-t2.xin1.cn/";
            }
        } else {
            DataConfig.API_HOST = BuildConfig.API_HOST;
            DataConfig.H5_HOST = BuildConfig.H5_HOST;
            DataConfig.LOG_HOST = BuildConfig.LOG_HOST;
            DataConfig.H5_ACT_HOST = BuildConfig.H5_ACT_HOST;
        }

        //        DataConfig.API_HOST = "https://api-t2.xin1.cn/";
        //        DataConfig.H5_HOST = "https://m-xlt-t2.xin1.cn/";
        //        DataConfig.LOG_HOST = "https://report-t2.xin1.cn/";
    }

    private void initOthers() {
        //ButterKnife
        ButterKnife.setDebug(BuildConfig.DEBUG);
        //Fragmentation
        Fragmentation.builder()
                // 显示悬浮球 ; 其他Mode:SHAKE: 摇一摇唤出   NONE：隐藏
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG)
                .install();
    }

    private void initUmengPush() {
        UMConfigure.setLogEnabled(true);
        // 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
// 参数一：当前上下文context；
// 参数二：应用申请的Appkey（需替换）；
// 参数三：渠道名称；
// 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
// 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）
        UMConfigure.init(this, "5dd34d390cafb2c61e0000ef", "umeng", UMConfigure.DEVICE_TYPE_PHONE,
                "2ccc3788214dc6bc9962cbdda04f0c69");

        PushAgent pushAgent = PushAgent.getInstance(this);
        pushAgent.setResourcePackageName("com.snqu.shopping");
//注册推送服务，每次调用register方法都会回调该接口
        pushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                umengDeviceToken = deviceToken;
                Log.i(TAG, "注册成功1：deviceToken：-------->  " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e(TAG, "注册失败1：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });

//        String manufacturer = DeviceUtils.getManufacturer();
//        if (AgooConstants.MESSAGE_SYSTEM_SOURCE_VIVO.equalsIgnoreCase(manufacturer)) {
        VivoRegister.register(this);
//        } else if (AgooConstants.MESSAGE_SYSTEM_SOURCE_OPPO.equalsIgnoreCase(manufacturer)) {
        OppoRegister.register(this, "dc6fd2b52fd64f2f8d00c7f7f32b9016", "ade971ffa52a4f769df51855651d9c92");
//        } else if (AgooConstants.MESSAGE_SYSTEM_SOURCE_XIAOMI.equalsIgnoreCase(manufacturer)) {
        MiPushRegistar.register(this, "2882303761518269843", "5371826996843");
//        } else if (AgooConstants.MESSAGE_SYSTEM_SOURCE_HUAWEI.equalsIgnoreCase(manufacturer)) {
        HuaWeiRegister.register(this);
//        }
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithNotificationMessage(Context context, UMessage uMessage) {
                if (devEnv) {
                    LogUtil.d("UMLog", "dealWithNotificationMessage uMessage=" + uMessage.getRaw().toString());
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (devEnv) {
                                ToastUtil.show(uMessage.getRaw().toString());
                            }
                        }
                    });
                }
                super.dealWithNotificationMessage(context, uMessage);
            }

            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                if (devEnv) {
                    LogUtil.d("UMLog", "dealWithCustomMessage uMessage=" + msg.getRaw().toString());
                }
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // 对于自定义消息，PushSDK默认只统计送达。若开发者需要统计点击和忽略，则需手动调用统计方法。
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        PushUtil.parseMessage(msg.custom);
                        if (devEnv) {
                            ToastUtil.showLong(msg.custom);
                        }
                    }
                });
            }
        };
        PushAgent.getInstance(this).setMessageHandler(messageHandler);
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                if (devEnv) {
                    ToastUtil.showLong(msg.text);
                }
            }
        };
        PushAgent.getInstance(this).setNotificationClickHandler(notificationClickHandler);
    }

    /**
     * 使用 dalvik.system.VMRuntime类提供的setTargetHeapUtilization方法可以增强程序堆内存的处理效率
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void heapUtilization() {
        try {
            Class localClass = Class.forName("dalvik.system.VMRuntime");
            Method localMethod1 = localClass.getDeclaredMethod("getRuntime",
                    new Class[0]);
            Class[] arrayOfClass = new Class[1];
            arrayOfClass[0] = Float.TYPE;
            Method localMethod2 = localClass.getDeclaredMethod(
                    "setTargetHeapUtilization", arrayOfClass);
            Object localObject = localMethod1.invoke(localClass, new Object[0]);
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = Float.valueOf(0.75F);
            localMethod2.invoke(localObject, arrayOfObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前进程名称
     *
     * @return
     */
    private String getCurProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 初始化后台定时任务
     */
    private void initBackgroundTasks() {
        //        ScheduleTaskManager taskManager = ScheduleTaskManager.getInstance();
        //        // 更新任务
        //        taskManager.addTask(new UpdateTask());
        //
        //        // 开启后台服务
        //        try {
        //            startService(new Intent(this, CoreService.class));
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

        LocationService.start(this);
    }

    private class AppCrashHandler implements Thread.UncaughtExceptionHandler {
        private Thread.UncaughtExceptionHandler exceptionHandler;

        public AppCrashHandler(Thread.UncaughtExceptionHandler exceptionHandler) {
            this.exceptionHandler = exceptionHandler;
        }

        @Override
        public void uncaughtException(Thread t, Throwable ex) {

            StringBuffer sb = new StringBuffer();
            sb.append("\r\n");
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            ex.printStackTrace(printWriter);
            printWriter.close();
            String result = writer.toString();
            sb.append(result);
            LogClient.log("crashInfo", sb.toString());

            LogUtil.e(sb.toString());

            //友盟统计
            UMCrash.generateCustomLog(ex, "UmengException");

            exceptionHandler.uncaughtException(t, ex);
        }
    }

    public static int getDev() {
        return SPUtil.getInt("dev_env", BuildConfig.DEBUG ? 1 : 0);
    }

    public static void setDev(int dev) {
        SPUtil.setInt("dev_env", dev);
    }

}
