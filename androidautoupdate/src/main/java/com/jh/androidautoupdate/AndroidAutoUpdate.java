package com.jh.androidautoupdate;

import android.content.Context;

import com.jh.androidautoupdate.download.DownloadWrapper;
import com.jh.androidautoupdate.install.InstallWrapper;

public final class AndroidAutoUpdate {

    private DownloadWrapper downloadWrapper = null;
    private InstallWrapper installWrapper = null;

    private static class Inner {
        private static AndroidAutoUpdate instance = new AndroidAutoUpdate();
    }

    private AndroidAutoUpdate() {
        downloadWrapper = new DownloadWrapper();
        installWrapper = new InstallWrapper();
    }

    public static AndroidAutoUpdate getInstance() {
        return Inner.instance;
    }


    public void download(AndroidAutoUpdateConfig androidAutoUpdateConfig) {
        if (androidAutoUpdateConfig != null) {
            downloadWrapper.downloadApk(androidAutoUpdateConfig);
        } else {
            throw new NullPointerException("初始化配置文件");
        }
    }

    public void destroyDownLoad() {
        downloadWrapper.destroyHandler();
        downloadWrapper.unBindService();
    }


    public void install(Context context,String apkPath) {
        if (apkPath != null) {
            installWrapper.install(context,apkPath);
        } else {
            throw new NullPointerException("APK地址为空");
        }
    }

}
