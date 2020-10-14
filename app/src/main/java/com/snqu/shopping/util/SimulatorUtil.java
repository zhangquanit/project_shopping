package com.snqu.shopping.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.android.util.LContext;
import com.android.util.log.LogUtil;
import com.lahm.library.EasyProtectorLib;
import com.lahm.library.EmulatorCheckCallback;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.ui.main.view.TipDialogView;
import com.snqu.shopping.util.statistics.AnalysisUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import common.widget.dialog.EffectDialogBuilder;

/**
 * @author 张全
 */
public class SimulatorUtil {
    public static boolean isSimulator(Context context) {
        LogUtil.e("simulator", "packageName=" + LContext.getContext().getPackageName());
        LogUtil.e("simulator", "deviceId=" + AnalysisUtil.getUniqueId());
        try {
            boolean notHasBlueTooth = notHasBlueTooth();
            boolean features = isFeatures();
            boolean isNotRealPhone = checkIsNotRealPhone();
            boolean checkPipes = checkPipes();
            boolean isSimulatorHardware = checkFeaturesByHardware();

            String userInfo = "";
            UserEntity user = UserClient.getUser();
            if (null != user) {
                userInfo = "username=" + user.username + ",uid=" + user._id;
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("userInfo", userInfo);
            map.put("deviceId", AnalysisUtil.getUniqueId());
            map.put("notHasBlueTooth", notHasBlueTooth);
            map.put("features", features);
            map.put("isNotRealPhone", isNotRealPhone);
            map.put("checkPipes", checkPipes);
            map.put("isSimulatorHardware", isSimulatorHardware);
            MobclickAgent.onEventObject(LContext.getContext(), "simulator", map);
            LogUtil.e("simulator", map.toString());

            if (notHasBlueTooth
                    || features
                    || isNotRealPhone
                    || checkPipes
                    || isSimulatorHardware
            ) {
                showAlertDialog(context, "提示", "请勿使用模拟器打开【星乐桃】,对此带来的不便，敬请谅解!");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 用途:判断蓝牙是否有效来判断是否为模拟器
     * 返回:true 为模拟器
     */
    private static boolean notHasBlueTooth() {
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        if (ba == null) {
            return true;
        } else {
            // 如果有蓝牙不一定是有效的。获取蓝牙名称，若为null 则默认为模拟器
            String name = ba.getName();
            if (TextUtils.isEmpty(name)) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 用途:根据部分特征参数设备信息来判断是否为模拟器
     * 返回:true 为模拟器
     */
    private static boolean isFeatures() {
        boolean isSimulator = Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MODEL.contains("MuMu") || Build.MODEL.contains("virtual")
                || Build.SERIAL.equalsIgnoreCase("android")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT)
                || ((TelephonyManager) LContext.getContext().getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName().toLowerCase().equals("android");

        return isSimulator;
    }

    /**
     * 用途:根据CPU是否为电脑来判断是否为模拟器
     * 返回:true 为模拟器
     */
    private static boolean checkIsNotRealPhone() {
        String cpuInfo = readCpuInfo();
        if ((cpuInfo.contains("intel") || cpuInfo.contains("amd"))) {
            return true;
        }
        return false;
    }

    /**
     * 用途:根据CPU是否为电脑来判断是否为模拟器(子方法)
     * 返回:String
     */
    private static String readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder cmd = new ProcessBuilder(args);

            java.lang.Process process = cmd.start();
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }
            responseReader.close();
            result = sb.toString().toLowerCase();
        } catch (IOException ex) {
        }
        return result;
    }

    /**
     * 用途:检测模拟器的特有文件
     * 返回:true 为模拟器
     */
    private static String[] known_pipes = {"/dev/socket/qemud", "/dev/qemu_pipe"};

    private static boolean checkPipes() {
        for (int i = 0; i < known_pipes.length; i++) {
            String pipes = known_pipes[i];
            File qemu_socket = new File(pipes);
            if (qemu_socket.exists()) {
                return true;
            }
        }
        return false;
    }

    private static String getProperty(String propName) {
        String value = null;
        Object roSecureObj;
        try {
            roSecureObj = Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class)
                    .invoke(null, propName);
            if (roSecureObj != null) value = (String) roSecureObj;
        } catch (Exception e) {
            value = null;
        }
        return value;

    }

    /**
     * 特征参数-硬件名称
     *
     * @return 0表示可能是模拟器，1表示模拟器，2表示可能是真机
     */
    private static boolean checkFeaturesByHardware() {
        String hardware = getProperty("ro.hardware");
        if (TextUtils.isEmpty(hardware)) return false;
        switch (hardware.toLowerCase()) {
            case "ttvm"://天天模拟器
            case "nox"://夜神模拟器
            case "cancro"://网易MUMU模拟器
            case "intel"://逍遥模拟器
            case "vbox":
            case "vbox86"://腾讯手游助手
            case "android_x86"://雷电模拟器
                return true;
        }
        return false;
    }

    public static void check(Context context) {
        if (LContext.isDebug && TextUtils.equals(LContext.channel, "prod_test")) {
            return;
        }
        if (!TextUtils.equals(LContext.pkgName, "com.snqu.xlt") || !TextUtils.equals(LContext.appName, "星乐桃")) {
            showAlertDialog(context, "提示", "检测到你的应用非官方正版，请从正规渠道下载！");
            return;
        }
        //模拟器检测
        boolean successful = EasyProtectorLib.checkIsRunningInEmulator(context, new EmulatorCheckCallback() {

            @Override
            public void findEmulator(String emulatorInfo) {
            }
        });

        if (successful) {
            showAlertDialog(context, "提示", "请勿使用模拟器打开【星乐桃】,对此带来的不便，敬请谅解!");
        }

//        isSimulator(context);


//        //应用分身检测
//        try {
//            VirtualApkCheckUtil.getSingleInstance().checkByPrivateFilePath(LContext.getContext(), new VirtualCheckCallback() {
//                @Override
//                public void findSuspect() {
//                    ToastUtil.show("checkByPrivateFilePath findSuspect()");
//                    showAlertDialog(context, "提示", "【星乐桃】不允许使用分身软件，请将分身卸载，保留原始app打开，对此带来的不便，敬请谅解!");
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            VirtualApkCheckUtil.getSingleInstance().checkByOriginApkPackageName(LContext.getContext(), new VirtualCheckCallback() {
//                @Override
//                public void findSuspect() {
//                    ToastUtil.show("checkByOriginApkPackageName findSuspect()");
//                    showAlertDialog(context, "提示", "【星乐桃】不允许使用分身软件，请将分身卸载，保留原始app打开，对此带来的不便，敬请谅解!");
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            VirtualApkCheckUtil.getSingleInstance().checkByMultiApkPackageName(new VirtualCheckCallback() {
//                @Override
//                public void findSuspect() {
//                    ToastUtil.show("checkByMultiApkPackageName findSuspect()");
//                    showAlertDialog(context, "提示", "【星乐桃】不允许使用分身软件，请将分身卸载，保留原始app打开，对此带来的不便，敬请谅解!");
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            VirtualApkCheckUtil.getSingleInstance().checkByPortListening("port", new VirtualCheckCallback() {
//                @Override
//                public void findSuspect() {
//                    ToastUtil.show("checkByPortListening findSuspect()");
//                    showAlertDialog(context, "提示", "【星乐桃】不允许使用分身软件，请将分身卸载，保留原始app打开，对此带来的不便，敬请谅解!");
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private static void showAlertDialog(Context context, String title, String content) {
        TipDialogView tipDialogView = new TipDialogView(context, title, content);
        tipDialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Process.killProcess(Process.myPid());
            }
        });
        new EffectDialogBuilder(context)
                .setContentView(tipDialogView)
                .setCancelable(false)
                .setCancelableOnTouchOutside(false)
                .show();
    }
}
