package com.jh.androidupdate;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.jh.androidautoupdate.AndroidAutoUpdate;
import com.jh.androidautoupdate.AndroidAutoUpdateConfig;
import com.jh.androidautoupdate.download.DownloadListener;

import java.io.File;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private MyUploadDialog dialog = null;
    private AndroidAutoUpdateConfig androidAutoUpdateConfig = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new MyUploadDialog(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidAutoUpdate.getInstance().download(androidAutoUpdateConfig);
            }
        });
        androidAutoUpdateConfig = new AndroidAutoUpdateConfig(this);
        androidAutoUpdateConfig.setApkUrl("your APK url");
        androidAutoUpdateConfig.setApkPath(Objects.requireNonNull(getExternalFilesDir("")).getPath());
        androidAutoUpdateConfig.setApkName("***.apk");
        androidAutoUpdateConfig.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadPrepare() {

            }

            @Override
            public void onDownloadStart(long totalProgress) {
                if (dialog!=null){
                    dialog.setTotalProcess((totalProgress/1024/1024)+"");
                }
            }

            @Override
            public void onDownloadIng(long currentProgress, int percentage) {
                if (dialog!=null){
                    dialog.setPercentage(percentage);
                    dialog.setCurrentProcess((currentProgress/1024/1024)+"");
                }
            }

            @Override
            public void onDownloadComplete(String apkFilePath) {
                AndroidAutoUpdate.getInstance().install(MainActivity.this,apkFilePath);
            }

            @Override
            public void onDownloadFail(String message) {

            }
        });
    }

    public void showUpdateDialog(View view) {
        if (dialog != null) {
            dialog.show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        AndroidAutoUpdate.getInstance().destroyDownLoad();
    }
}
