package com.jh.androidautoupdate.download;

public interface DownloadListener {

    /**
     * 准备下载
     */
    void onDownloadPrepare();

    /**
     * 开始下载
     */
    void onDownloadStart(long totalProgress);

    /**
     * 下载中
     *
     * @param currentProgress       当前进度： 下载程度
     * @param percentage            百分比：   当前下载的进度占比总大小   50% = 50/100*100
     */
    void onDownloadIng(long currentProgress,int percentage);

    /**
     * 下载完成
     */
    void onDownloadComplete(String apkFilePath);

    /**
     * 下载错误
     */
    void onDownloadFail(String message);

}
