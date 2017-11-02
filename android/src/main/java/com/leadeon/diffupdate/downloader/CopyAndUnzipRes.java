package com.leadeon.diffupdate.downloader;

import android.content.Context;
import android.os.Handler;

import com.leadeon.diffupdate.utils.FileUtil;
import com.leadeon.diffupdate.utils.LogUtils;
import com.leadeon.diffupdate.utils.RNFilePathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 此类主要用于
 * 复制上次合并的jsbundle文件
 * 解压上次下载的
 * zip文件
 * Created by Lynn on 2017/8/17.
 */

public class CopyAndUnzipRes {

    private Context context = null;


    public CopyAndUnzipRes(Context context){
        if (this.context == null) {
            this.context = context.getApplicationContext();
        }
    }



    public void start(Handler handler) {
        File file = new File(RNFilePathUtils.getBundleRootPath(this.context));
        if (file.exists()) {
            if (file.isDirectory()) {
                List<String> listMoudleName = new ArrayList<>();
                File[] childFiles = file.listFiles();
                for (int i = 0; i < childFiles.length; i++) {
                    listMoudleName.add(childFiles[i].getName());
                }
                if (listMoudleName.size() == 0) {
                    handler.sendEmptyMessage(2);  //表示完成了解压和复制
                    LogUtils.writeLog("解压和复制bundle总耗时为：  没有可解压和复制的文件");
                } else {
                    //开启一个线程来进行文件操作
                    startnewThread(listMoudleName,handler);
                }
            }
        }else {
            handler.sendEmptyMessage(2);  //表示完成了解压和复制
        }
    }


    /**
     * 开启一个线程来
     * @param listMoudleName
     * @param handler
     */
    private void startnewThread(final List<String> listMoudleName, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long time1=System.currentTimeMillis();
                for (String moduleName : listMoudleName) {
                    copyAndUnzip(moduleName);
                }
                long time2=System.currentTimeMillis();
                LogUtils.writeLog("解压和复制bundle总耗时为："+(time2-time1));
                handler.sendEmptyMessage(2);  //表示完成了解压和复制
            }
        }).start();
    }


    /**
     * 将patch文件夹下的资源文件解压到index文件夹下
     * 将merge文件夹下的jsbundle复制到index文件夹下
     * @param moduleName
     */
    private void copyAndUnzip(String moduleName) {
        File file = new File(RNFilePathUtils.getBundleMergeFile(this.context, moduleName));
        if (file.exists()) {
            LogUtils.writeLog("存在可解压的文件以及需要复制的jsbundle");
            FileUtil.unZipRes(
                    RNFilePathUtils.getBundleZipFile(this.context, moduleName),
                    RNFilePathUtils.getBundlePath(this.context, moduleName)
            );         //资源覆盖解压

            FileUtil.copyFile(
                    RNFilePathUtils.getBundleMergeFile(this.context, moduleName),
                    RNFilePathUtils.getBundleFile(this.context, moduleName)
            ); //复制合并后的jsbundle
            //此处应该读取版本信息存储在share文件中

            FileUtil.deleteAll(RNFilePathUtils.getBundleMergePath(this.context, moduleName)); //删除merge文件夹下的东西
            FileUtil.deleteAll(RNFilePathUtils.getBundlePatchPath(this.context, moduleName)); //删除patch文件夹下的东西

        }
    }

}
