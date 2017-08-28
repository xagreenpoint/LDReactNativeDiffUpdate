package com.updatademo;

import android.app.Application;

import com.liulishuo.filedownloader.FileDownloader;

/**
 * Created by Lynn on 2017/8/17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloader.setupOnApplicationOnCreate(this);
    }
}
