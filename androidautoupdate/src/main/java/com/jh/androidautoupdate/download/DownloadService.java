package com.jh.androidautoupdate.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jh.androidautoupdate.AndroidAutoUpdateConfig;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadService extends Service {

    private AndroidAutoUpdateConfig androidAutoUpdateConfig = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DownloadBinder();
    }


    public class DownloadBinder extends Binder {
        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    private static final int DOWNLOAD_PREPARE = 0;        // 准备下载
    private static final int DOWNLOAD_BEGIN = 1;        // 开始下载
    private static final int DOWNLOAD_ING = 2;          // 下载中
    private static final int DOWNLOAD_COMPLETE = 3;     // 结束下载
    private static final int DOWNLOAD_FAIL = 4;         // 下载失败


    private Handler downloadHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (androidAutoUpdateConfig != null) {
                DownloadListener downloadListener = androidAutoUpdateConfig.getDownloadListener();
                if (downloadListener != null) {
                    int what = msg.what;
                    switch (what) {
                        case DOWNLOAD_PREPARE:
                            downloadListener.onDownloadPrepare();
                            break;
                        case DOWNLOAD_BEGIN:
                            downloadListener.onDownloadStart((Long) msg.obj);
                            break;
                        case DOWNLOAD_ING:
                            Bundle bundle = msg.getData();
                            long currentProgress = bundle.getLong("currentProgress");
                            int percentage = bundle.getInt("percentage");
                            downloadListener.onDownloadIng(currentProgress, percentage);
                            break;
                        case DOWNLOAD_COMPLETE:
                            downloadListener.onDownloadComplete((String) msg.obj);
                            break;
                        case DOWNLOAD_FAIL:
                            downloadListener.onDownloadFail((String) msg.obj);
                            break;
                        default:

                            break;
                    }
                    return true;
                }
            }
            return false;
        }
    });


    public void downloadApk(final AndroidAutoUpdateConfig androidAutoUpdateConfig) {
        this.androidAutoUpdateConfig = androidAutoUpdateConfig;
        final File file = createFile(androidAutoUpdateConfig.getApkPath(), androidAutoUpdateConfig.getApkName());
        sendDownloadPrepare();

        Request request = new Request.Builder()
                .url(androidAutoUpdateConfig.getApkUrl())
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                sendDownloadFail(e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    long totalProgress = responseBody.contentLength();
                    // 开始发送
                    sendDownloadBegin(totalProgress);
                    // 发送中
                    sendDownloadIng(response, totalProgress, androidAutoUpdateConfig,file);
                    // 发送完成
                    sendDownloadComplete(file);
                } else {
                    sendDownloadFail("检查后台APP版本");
                }
            }
        });
    }


    private File createFile(String filePath, String apkName) {
        File file = new File(filePath, apkName);
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 准备发送
     */
    private void sendDownloadPrepare() {
        Message message = downloadHandler.obtainMessage();
        message.what = DOWNLOAD_PREPARE;
        downloadHandler.sendMessage(message);
    }

    /**
     * 开始发送
     *
     * @param totalProgress 总长度   kb  转换 mb = totalProgress/1024/1024
     */
    private void sendDownloadBegin(long totalProgress) {
        Message message = downloadHandler.obtainMessage();
        message.what = DOWNLOAD_BEGIN;
        message.obj = totalProgress;
        downloadHandler.sendMessage(message);
    }


    /**
     * 发送中
     *
     * @param response                请求响应
     * @param totalProgress           总进度
     * @param androidAutoUpdateConfig 配置文件
     */
    private void sendDownloadIng(@NotNull Response response, long totalProgress, AndroidAutoUpdateConfig androidAutoUpdateConfig,File apkFile) {
        InputStream inputStream = null;
        byte[] buff = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            inputStream = response.body().byteStream();
            fos = new FileOutputStream(apkFile);
            long sum = 0;
            while ((len = inputStream.read(buff)) != -1) {
                fos.write(buff, 0, len);
                sum += len;
                int percentage = (int) (sum * 1.0f / totalProgress * 100);
                Message message = downloadHandler.obtainMessage();
                message.what = DOWNLOAD_ING;
                Bundle bundle = new Bundle();
                bundle.putLong("currentProgress", sum);
                bundle.putInt("percentage", percentage);
                message.setData(bundle);
                downloadHandler.sendMessage(message);
               /* if (androidAutoUpdateConfig.getRate() != percentage) {
                    Message message = downloadHandler.obtainMessage();
                    message.what = DOWNLOAD_ING;
                    Bundle bundle = new Bundle();
                    bundle.putLong("currentProgress", sum);
                    bundle.putInt("percentage", percentage);
                    downloadHandler.sendMessage(message);
                    androidAutoUpdateConfig.setRate(percentage);
                }*/
            }
            fos.flush();
            // 关流
            if (inputStream != null) {
                inputStream.close();
            }
            // 关流
            if (fos != null) {
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            sendDownloadFail(e.toString());
        }
    }

    /**
     * 发送完成
     */
    private void sendDownloadComplete(File file) {
        Message message = downloadHandler.obtainMessage();
        message.what = DOWNLOAD_COMPLETE;
        message.obj = file.getPath().toString();
        downloadHandler.sendMessage(message);
    }


    /**
     * 发送错误
     *
     * @param s
     */
    private void sendDownloadFail(String s) {
        Message message = downloadHandler.obtainMessage();
        message.what = DOWNLOAD_FAIL;
        message.obj = s;
        downloadHandler.sendMessage(message);
    }

    /**
     *  移除Handler，防止内存泄露
     */
    public void destroyHandler(){
        downloadHandler.removeCallbacks(null);
        downloadHandler = null;
    }

}
