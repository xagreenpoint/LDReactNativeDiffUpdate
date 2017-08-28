package com.yyh.lib.bsdiff.downloader;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.yyh.lib.bsdiff.PatchUtils;
import com.yyh.lib.bsdiff.bean.RnCheckRes;
import com.yyh.lib.bsdiff.utils.FileMd5Utils;
import com.yyh.lib.bsdiff.utils.FileUtil;
import com.yyh.lib.bsdiff.utils.LogUtils;
import com.yyh.lib.bsdiff.utils.RNFilePathUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lynn on 2017/8/17.
 */

public class RnModuleDownloader {


    private Context context = null;

    public RnModuleDownloader(Context context) {
        this.context = context.getApplicationContext();
    }


    /**
     * 开始下载rn模块代码
     * 此方法应该在rn的版本检测成功后调用
     *
     * @param resList
     */
    public void startDown(List<RnCheckRes> resList) {
        if(resList==null||resList.size()==0){
            LogUtils.writeLog("没有可下载的文件    ");
            return;
        }

        LogUtils.writeLog("开始执行下载模块的代码");
        FileDownloadQueueSet queueSet = new FileDownloadQueueSet(new FileDownloadSampleListener() {
            @Override
            protected void completed(BaseDownloadTask task) {
                super.completed(task);
                LogUtils.writeLog("completed 下载完成   thread: "+Thread.currentThread().getName());
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                super.blockComplete(task);
                String path = task.getPath();
                LogUtils.writeLog("blockComplete 下载完成    本地存储路径为" + path +"   thread: "+Thread.currentThread().getName());
                RnCheckRes rnCheckRes = ((RnCheckRes) task.getTag());
                if (FileMd5Utils.MD5File(path).equals(rnCheckRes.getZipHash())) {   //比对zipmd5
                    unZipPatch(rnCheckRes);
                } else {        //删除patch文件夹下所有文件
                    LogUtils.writeLog("下载完文件后  进行MD5校验未通过   执行删除操作");
                    FileUtil.deleteAll(RNFilePathUtils.getBundlePatchPath(context, rnCheckRes.getModuleName()));
                }
            }
        });

        List<BaseDownloadTask> tasks = new ArrayList<>();
        for (RnCheckRes rnCheckRes : resList) {
            if (!TextUtils.isEmpty(rnCheckRes.getZipPath())) {
                String zipFilePath = RNFilePathUtils.getBundleZipFile(this.context, rnCheckRes.getModuleName());
                tasks.add(FileDownloader.getImpl().create(rnCheckRes.getZipPath()).setPath(zipFilePath).setTag(rnCheckRes));
            }
        }
        queueSet.disableCallbackProgressTimes(); // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
        queueSet.setAutoRetryTimes(1);               // 所有任务在下载失败的时候都自动重试一次
        queueSet.downloadTogether(tasks);              // 并行执行该任务队列
        queueSet.start();
    }


    /**
     * 开始解压patch，并且合并patch文件
     *
     * @param rnCheckRes
     */
    private  void unZipPatch(RnCheckRes rnCheckRes) {
        String moduleName = rnCheckRes.getModuleName();
        String jsbundleMd5 = rnCheckRes.getJsbundleHash();
        FileUtil.unZipPatch(
                RNFilePathUtils.getBundleZipFile(this.context, moduleName),
                RNFilePathUtils.getBundlePatchPath(this.context, moduleName));    //解压到patch文件夹下
        LogUtils.writeLog("解压完成   开始合并");
        PatchUtils.getInstance().mergePatch(               //合并jsbundle
                RNFilePathUtils.getBaseBundleFile(this.context, moduleName),
                RNFilePathUtils.getBundleMergeFile(this.context, moduleName),
                RNFilePathUtils.getBundlePatchFile(this.context, moduleName)
        );
        if (!FileMd5Utils.MD5File(RNFilePathUtils.getBundleMergeFile(this.context, moduleName)).equals(jsbundleMd5)) {
            FileUtil.deleteAll(RNFilePathUtils.getBundlePath(this.context, moduleName));   //删除下载的文件
            LogUtils.writeLog("合并完文件后  进行MD5校验未通过   执行删除操作");
        } else {
            LogUtils.writeLog("合并完文件  并保存版本信息" + JSON.toJSONString(rnCheckRes));
                     // 保存版本号
            new RnVersionManager(this.context).saveRnDescription(rnCheckRes);      //保存版本信息
        }
    }

}
