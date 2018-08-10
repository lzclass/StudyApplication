package com.arms.perfect.common;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

/**
 * Created by liuzhao on 2018/3/21 20:39.
 * description:手机的信息
 */

public class Apn {
    /**
     * 当前版本
     */
    public static String version;
    /**
     * 当前版本号
     */
    public static int versionCode;
    /**
     * 设备唯一标识
     */
    public static String deviceId;
    /**
     * 手机系统版本
     */
    public static String osVersion;
    /**
     * 手机型号
     */
    public static String model;

    public static void init(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager
                    .getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = telephonyManager.getDeviceId();
            if (deviceId == null) {
                deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
            model = android.os.Build.MODEL;
            osVersion = android.os.Build.VERSION.RELEASE;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
