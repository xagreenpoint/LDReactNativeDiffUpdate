package com.leadeon.diffupdate.downloader;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import com.alibaba.fastjson.JSON;
import com.leadeon.diffupdate.bean.RnCheckRes;
import com.leadeon.diffupdate.utils.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lynn on 2017/8/17.
 */

public class RnModuleDiffUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:   //表示首次的资源复制已经完成
                    LogUtils.writeLog("首次的资源复制已经完成");
                    UnzipFile();
                    break;
                case 2:  //表示下载后的资源已经解压和复制
                    LogUtils.writeLog("表示下载后的资源已经解压和复制");
                    checkVersion();
                    break;

            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        new CopyModule(this).start(this.handler);

    }


    /**
     * 开始解压上次下载的patch文件
     */
    private void UnzipFile() {
        new MergeLastDownedFile(this).start(this.handler);
    }

    /**
     * 开始版本检测
     */
    private void checkVersion() {
        //返回了结果之后
        List<RnCheckRes> ls = new ArrayList<>();

        RnVersionManager rnVersionManager = new RnVersionManager(this);
        HashMap<String, String> versions = rnVersionManager.getRnVersion();

        LogUtils.writeLog("版本检测数据结果" + JSON.toJSONString(versions));
        if (versions.get("updataDemo").equals("1.0.0")) {
            RnCheckRes rnCheckRes = new RnCheckRes();
            rnCheckRes.setModuleName("updataDemo");
            rnCheckRes.setVersion("1.0.1");
            rnCheckRes.setJsbundleHash("c2c36ef32378c2fd61f38e9d36e1e87b");
            rnCheckRes.setZipHash("de66af96ca496c4380ce174cba0bff9c");
            rnCheckRes.setZipPath("http://192.168.10.221:8080/test/updataDemo.zip");
            ls.add(rnCheckRes);
        }
        new RnModuleDownloader(this).startDown(ls);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.writeLog("service  onDestory");
    }



}
