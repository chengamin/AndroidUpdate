package com.jh.androidautoupdate.install;

public interface InstallListener {

    void onInstallStart();

    void onInstallIng();

    void onInstallComplete();

    void onInstallFail();

}
