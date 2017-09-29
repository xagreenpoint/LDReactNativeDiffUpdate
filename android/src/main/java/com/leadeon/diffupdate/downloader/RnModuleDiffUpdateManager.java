package com.leadeon.diffupdate.downloader;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.leadeon.diffupdate.LeadeonDiff;
import com.leadeon.diffupdate.bean.RnCheckRes;
import com.leadeon.diffupdate.utils.LogUtils;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lynn on 2017/8/17.
 */

public class RnModuleDiffUpdateManager {

    private LeadeonDiff.CopyCompletedCallback copyCompletedCallback=null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:   //表示首次的资源复制已经完成
                    LogUtils.writeLog("首次的资源复制已经完成");
                    copyBundleUnzipRes();
                    break;
                case 2:  //表示下载后的资源已经解压和复制   完成
                    setComplete();
                    LogUtils.writeLog("表示下载后的资源已经解压和复制   完成");
                    checkVersion();
                    break;
                case 3:    //此处表示zip已下载，jsbundle也已经合并
                    System.out.println("停止service");
                    break;
            }
        }
    };

    private Context mContext=null;



    public RnModuleDiffUpdateManager(Context context,LeadeonDiff.CopyCompletedCallback copyCompletedCallback){
        if(context==null){
            throw new NullPointerException("context is null ,please check your agent");
        }
        this.copyCompletedCallback=copyCompletedCallback;
        this.mContext=context.getApplicationContext();
        FileDownloader.setup(this.mContext);
    }

    public void start(){
        new CopyModule(this.mContext).start(this.handler);
    }





    private void setComplete() {
        if(this.copyCompletedCallback!=null){
            this.copyCompletedCallback.onCompleted();
        }
    }


    /**
     * 复制上次合并后的jsbundle文件
     * 开始解压上次下载的zip中的资源
     */
    private void copyBundleUnzipRes() {
        new CopyAndUnzipRes(this.mContext).start(this.handler);
    }

    /**
     * 开始版本检测
     */
    private void checkVersion() {

        RnVersionManager rnVersionManager = new RnVersionManager(this.mContext);
        HashMap<String, String> versions = rnVersionManager.getRnVersion();

        System.out.println("数据为   " + JSON.toJSONString(versions));


        //返回了结果之后
        List<RnCheckRes> ls = new ArrayList<>();
        LogUtils.writeLog("版本检测数据结果" + JSON.toJSONString(versions));

        if (versions.get("ganio") != null && versions.get("ganio").equals("1.0.0")) {
            RnCheckRes rnCheckRes = new RnCheckRes();
            rnCheckRes.setLoadType("ReactNative");
            rnCheckRes.setModuleName("ganio");
            rnCheckRes.setVersion("1.0.1");
            rnCheckRes.setJsbundleHash("cb8e882abfadd5eb57621c3da2373e66");
            rnCheckRes.setZipHash("b2256bc43ab9b220f4bf9955550dce57");
            rnCheckRes.setZipPath("http://192.168.2.3:8080/test/ganio.zip");
            rnCheckRes.setDownloadNow("0");
            rnCheckRes.setLoadNow("false");
            rnCheckRes.setNeedGoBack("false");
            ls.add(rnCheckRes);

        }
        new RnModuleDownloader(this.mContext).startDown(ls, this.handler);
    }




}
