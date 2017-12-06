package com.leadeon.diffupdate.downloader;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.leadeon.diffupdate.bean.checkversion.RnCheckRes;
import com.leadeon.diffupdate.utils.FileMd5Utils;
import com.leadeon.diffupdate.utils.FileUtil;
import com.leadeon.diffupdate.utils.LogUtils;
import com.leadeon.diffupdate.utils.RNFilePathUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.yyh.lib.bsdiff.PatchUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lynn on 2017/8/17.
 */

public class RnModuleDownloader {


  private Context context = null;
  private HashMap<String, Boolean> rnModules = null;

  public RnModuleDownloader(Context context) {
    this.context = context.getApplicationContext();
  }


  /**
   * 开始下载rn模块代码
   * 此方法应该在rn的版本检测成功后调用
   *
   * @param resList
   */
  public void startDown(List<RnCheckRes> resList, final Handler handler) {
    if (resList == null || resList.size() == 0) {
      LogUtils.writeLog("没有可下载的文件    ");
      handler.sendEmptyMessage(3);
      return;
    }
    this.rnModules = new HashMap<>();
    for (RnCheckRes rnCheckRes : resList) {
      this.rnModules.put(rnCheckRes.getModuleName(), false);
    }


    LogUtils.writeLog("开始执行下载模块的代码");
    FileDownloadQueueSet queueSet = new FileDownloadQueueSet(new FileDownloadSampleListener() {
      @Override
      protected void completed(BaseDownloadTask task) {
        super.completed(task);

        String path = task.getPath();
        LogUtils.writeLog("blockComplete 下载完成    本地存储路径为" + path + "   thread: " + Thread.currentThread().getName());
        RnCheckRes rnCheckRes = ((RnCheckRes) task.getTag());
        if (FileMd5Utils.MD5File(path).equals(rnCheckRes.getZipHash())) {   //比对zipmd5
          unZipPatchAndMerge(rnCheckRes);
        } else {        //删除patch文件夹下所有文件
          LogUtils.writeLog("下载完文件后  进行MD5校验未通过   执行删除操作");
          FileUtil.deleteAll(RNFilePathUtils.getBundlePatchPath(context, rnCheckRes.getModuleName()));
        }
        sendComplete(rnCheckRes);
      }

      @Override
      protected void blockComplete(BaseDownloadTask task) {
        super.blockComplete(task);
      }

      private void sendComplete(RnCheckRes rnCheckRes) {
        if (rnModules != null) {
          rnModules.remove(rnCheckRes.getModuleName());
          if (rnModules.size() == 0) {
            handler.sendEmptyMessage(3);
          }
        }
      }

      @Override
      protected void error(BaseDownloadTask task, Throwable e) {
        super.error(task, e);
        if (rnModules != null) {
          RnCheckRes rnCheckRes = ((RnCheckRes) task.getTag());
          sendComplete(rnCheckRes);
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
    queueSet.disableCallbackProgressTimes();     // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
    queueSet.setAutoRetryTimes(1);               // 所有任务在下载失败的时候都不进行重试
    queueSet.downloadTogether(tasks);            // 并行执行该任务队列
    queueSet.start();
  }


  /**
   * 开始解压patch，并且合并patch文件
   *
   * @param rnCheckRes
   */
  private void unZipPatchAndMerge(RnCheckRes rnCheckRes) {
    String moduleName = rnCheckRes.getModuleName();
    String jsbundleMd5 = rnCheckRes.getJsbundleHash();
    FileUtil.unZipPatch(
      RNFilePathUtils.getBundleZipFile(this.context, moduleName),
      RNFilePathUtils.getBundlePatchPath(this.context, moduleName));    //解压到patch文件夹下
    LogUtils.writeLog("解压完成   开始合并");


    LogUtils.writeLog("old path  " + RNFilePathUtils.getBaseBundleFile(this.context, moduleName));
    LogUtils.writeLog("new path  " + RNFilePathUtils.getBundleMergeFile(this.context, moduleName));
    LogUtils.writeLog("patch path  " + RNFilePathUtils.getBundlePatchFile(this.context, moduleName));

    LogUtils.writeLog("patch path  开始创建文件");
    File oldFile = new File(RNFilePathUtils.getBaseBundleFile(this.context, moduleName));
    File patchFile=new File(RNFilePathUtils.getBundlePatchFile(this.context, moduleName));
    if (!oldFile.exists()||!patchFile.exists()) {
      FileUtil.deleteAll(patchFile.getPath());
      return;
    }
    int res = PatchUtils.getInstance().mergePatch(               //合并jsbundle
      RNFilePathUtils.getBaseBundleFile(this.context, moduleName),
      RNFilePathUtils.getBundleMergeFile(this.context, moduleName),
      RNFilePathUtils.getBundlePatchFile(this.context, moduleName)
    );
    LogUtils.writeLog("合并结果为     " + res);
    LogUtils.writeLog("服务器端的MD5  " + jsbundleMd5);
    String md5 = FileMd5Utils.MD5File(RNFilePathUtils.getBundleMergeFile(this.context, moduleName));
    LogUtils.writeLog("客户端MD5 " + md5);

    if (!md5.equals(jsbundleMd5)) {
      FileUtil.deleteAll(RNFilePathUtils.getBundleMergeFile(this.context, moduleName));   //删除下载的文件
      LogUtils.writeLog("合并完文件后  进行MD5校验未通过   执行删除操作");
    } else {
      LogUtils.writeLog("合并完文件  并保存版本信息" + JSON.toJSONString(rnCheckRes));
      // 保存版本号
      new RnVersionManager(this.context).saveRnDescription(rnCheckRes);      //保存版本信息
    }
  }

}
