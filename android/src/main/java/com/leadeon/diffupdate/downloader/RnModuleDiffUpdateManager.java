package com.leadeon.diffupdate.downloader;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.leadeon.diffupdate.LeadeonDiff;
import com.leadeon.diffupdate.bean.checkversion.RnCheck;
import com.leadeon.diffupdate.bean.checkversion.VersionRes;
import com.leadeon.diffupdate.bean.checkversion.RnCheckRes;
import com.leadeon.diffupdate.bean.checkversion.VersionReq;
import com.leadeon.diffupdate.utils.AppUtils;
import com.leadeon.diffupdate.utils.LogUtils;
import com.leadeon.diffupdate.utils.okutil.OkHttpManager;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Lynn on 2017/8/17.
 */

public class RnModuleDiffUpdateManager {

  private LeadeonDiff.CopyCompletedCallback copyCompletedCallback = null;

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

  private Context mContext = null;
  private String AppKey=null;
  private String RnVersion=null;
  private String url=null;


  public RnModuleDiffUpdateManager(Context context,String appKey,String rnVersion,String url, LeadeonDiff.CopyCompletedCallback copyCompletedCallback) {
    if (context == null) {
      throw new NullPointerException("context is null ,please check your agent");
    }
    this.copyCompletedCallback = copyCompletedCallback;
    this.mContext = context.getApplicationContext();
    this.AppKey=appKey;
    this.RnVersion=rnVersion;
    this.url=url;
    FileDownloader.setup(this.mContext);
  }

  public void start() {
    new CopyModule(this.mContext).start(this.handler);
  }


  private void setComplete() {
    if (this.copyCompletedCallback != null) {
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

    RnCheck rnCheck = new RnCheck();
    VersionReq versionReq = new VersionReq();
    versionReq.setAppKey(AppKey);
    versionReq.setRnVersion(RnVersion);
    versionReq.setAppVersion(AppUtils.getAppVersionName(mContext));
    versionReq.setPlatForm("android");
    versionReq.setReqParam(versions);
    rnCheck.setReqBody(versionReq);

    OkHttpManager.getInstance().post(this.url, rnCheck, new OkHttpManager.DataCallBack() {
      @Override
      public void requestSuccess(String result) {
        VersionRes versionRes = JSON.parseObject(result, VersionRes.class);
        if (versionRes != null && versionRes.getRetCode().equals("000000")) {
          //表示业务成功
          List<RnCheckRes> list = versionRes.getRspBody();
          if (list != null && list.size() > 0) {
            next(list);
          }
        }
      }

      @Override
      public void requestError(String retCode) {
        LogUtils.writeLog("版本检测接口失败   可能是网络问题   http错误码为" + retCode);
      }

    });
  }


  /**
   * 当有新版本时执行下边代码进行
   * 增量更新包的下载，以及合并工作
   * 负责  则不用执行此方法
   *
   * @param ls
   */
  private void next(List<RnCheckRes> ls) {
    new RnModuleDownloader(this.mContext).startDown(ls, this.handler);
  }

}
