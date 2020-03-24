package com.jh.androidautoupdate.download;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.jh.androidautoupdate.AndroidAutoUpdateConfig;
import static android.content.Context.BIND_AUTO_CREATE;


public class DownloadWrapper {

    private DownloadService downloadService = null;
    private AndroidAutoUpdateConfig androidAutoUpdateConfig = null;

    /**
     * 下载APK
     *
     * @param androidAutoUpdateConfig
     */
    public void downloadApk(AndroidAutoUpdateConfig androidAutoUpdateConfig) {
        this.androidAutoUpdateConfig = androidAutoUpdateConfig;
        Intent intent = new Intent(androidAutoUpdateConfig.getContext(), DownloadService.class);
        androidAutoUpdateConfig.getContext().bindService(intent, downLoadServiceConnection, BIND_AUTO_CREATE);
    }

    public void unBindService(){
        androidAutoUpdateConfig.getContext().unbindService(downLoadServiceConnection);
    }

    /**
     * 移除Handler
     */
    public void destroyHandler() {
        downloadService.destroyHandler();
    }


    private ServiceConnection downLoadServiceConnection = new ServiceConnection() {

        // 系统调用这个方法来传送在service的onBind()中返回的IBinder．
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadService = (((DownloadService.DownloadBinder) service).getService());
            downloadService.downloadApk(androidAutoUpdateConfig);
        }

        // Android系统在同service的连接意外丢失时调用这个．比如当service崩溃了或被强杀了．当客户端解除绑定时，这个方法不会被调用．
        @Override
        public void onServiceDisconnected(ComponentName name) {
            destroyHandler();
        }
    };




}
