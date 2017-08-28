package com.yyh.lib.bsdiff.downloader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.yyh.lib.bsdiff.bean.RnCheckRes;
import com.yyh.lib.bsdiff.utils.FileUtil;
import com.yyh.lib.bsdiff.utils.LogUtils;
import com.yyh.lib.bsdiff.utils.RNFilePathUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lynn on 2017/8/20.
 */

public class CopyModule {


    private Context mContext=null;
    private RnVersionManager rnVersionManager=null;

    public CopyModule(Context context){
        this.mContext=context;
        this.rnVersionManager=new RnVersionManager(context);
    }

    public void start(final Handler handler){
        boolean mergedAssets =this.rnVersionManager.getSharedPreference().getString("lastVersion","").equals(getVersion(mContext));
        if (!mergedAssets) {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   merge(handler);
               }
           }).start();
        }else {
            handler.sendEmptyMessage(1);  //通知主线程首次复制已完成
        }
    }

    private void merge(Handler handler){
        long time1=System.currentTimeMillis();
        List<RnCheckRes> rnCheckResList=new ArrayList<RnCheckRes>();
        RnCheckRes rnCheckRes1=new RnCheckRes();
        rnCheckRes1.setModuleName("updataDemo");
        rnCheckRes1.setVersion("1.0.0");
        rnCheckResList.add(rnCheckRes1);

        for (RnCheckRes rnCheckRes:rnCheckResList){
            firstMergeAssets(rnCheckRes);
        }
        this.rnVersionManager.getSharedPreference().edit().putString("lastVersion",getVersion(mContext)).commit();  //putBoolean("mergedassets",true).commit();
        handler.sendEmptyMessage(1);  //通知主线程首次复制已完成
        long time2=System.currentTimeMillis();
        LogUtils.writeLog("首次的复制总耗时为   "+(time2-time1));
    }

    private void firstMergeAssets(RnCheckRes rnCheckRes) {
        String moduleName=rnCheckRes.getModuleName();
        try {
            LogUtils.writeLog("开始复制   ");
            FileUtil.deleteAll(RNFilePathUtils.getBundleRootPath(this.mContext));   //删除rn_bundle目录下所有文件
            InputStream assestInput = this.mContext.getAssets().open(moduleName+".zip");
            String dest = RNFilePathUtils.getBaseZipFile(this.mContext, moduleName);
            if (FileUtil.copyFile(assestInput, dest)) {   //copy插件包到宿主的安装目录下
                FileUtil.unZipAll(
                        dest,
                        RNFilePathUtils.getBaseBundlePath(this.mContext, moduleName)
                );         //资源解压到base文件夹下

                //将bundle的描述信息以及版本信息写入sharedpreference
                this.rnVersionManager.saveRnDescription(rnCheckRes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            return "";
        }
    }






}
