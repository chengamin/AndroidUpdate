package com.jh.androidautoupdate;

import android.content.Context;

import com.jh.androidautoupdate.download.DownloadListener;
import com.jh.androidautoupdate.install.InstallListener;

public class AndroidAutoUpdateConfig {

    private Context context = null;
    private String apkName = null;
    private int apkCode = 0;
    private String apkUrl = null;
    private String apkPath = null;
    private DownloadListener downloadListener = null;
    private InstallListener installListener = null;
    // 更新速率
    private float rate = .0f;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public AndroidAutoUpdateConfig(Context context) {
        this.context = context;
    }

    public String getApkName() {
        return apkName;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    public int getApkCode() {
        return apkCode;
    }

    public void setApkCode(int apkCode) {
        this.apkCode = apkCode;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    public InstallListener getInstallListener() {
        return installListener;
    }

    public void setInstallListener(InstallListener installListener) {
        this.installListener = installListener;
    }

    public  float getRate() {
        return rate;
    }

    public  void setRate(float rate) {
        this.rate = rate;
    }
}
