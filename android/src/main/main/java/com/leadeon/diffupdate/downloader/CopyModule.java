package com.leadeon.diffupdate.downloader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.alibaba.fastjson.JSON;
import com.leadeon.diffupdate.bean.checkversion.RnCheckRes;
import com.leadeon.diffupdate.utils.FileUtil;
import com.leadeon.diffupdate.utils.LogUtils;
import com.leadeon.diffupdate.utils.RNFilePathUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 此模块主要用于从 assets目录下复制
 * 文件
 * Created by Lynn on 2017/8/20.
 */

public class CopyModule {


    private Context mContext=null;
    private RnVersionManager rnVersionManager=null;

    public CopyModule(Context context){
        this.mContext=context;
        this.rnVersionManager=new RnVersionManager(context);
    }


    /**
     * 用于从assets文件夹下复制   rn的zip包
     * @param handler
     */
    public void start(final Handler handler){
        /**
         * 判断客户端是否进行了覆盖安装
         */
        boolean mergedAssets =this.rnVersionManager.getSharedPreference().getString("lastVersion","").equals(getVersion(mContext));
        if (!mergedAssets) {
           new Thread(new Runnable() {
               @Override
               public void run() {
                   rnVersionManager.deleteRnDescription();    //清空在磁盘中保存的rn模块的版本信息
                   merge(handler);
               }
           }).start();
        }else {
            handler.sendEmptyMessage(1);  //通知主线程不需要进行复制
        }
    }

    /**
     * 从assets目录下复制基础包
     * 基础包的个数和业务的个数一样
     * 有多少种业务包就有多少个基础包
     * @param handler
     */
    private void merge(Handler handler){
        long time1=System.currentTimeMillis();

        try {       //从配置文件中读取数据
           String rnConfig= FileUtil.readFile(this.mContext.getAssets().open("rn_base_model.json"));
            List<RnCheckRes> rnCheckResList= JSON.parseArray(rnConfig,RnCheckRes.class);
            for (RnCheckRes rnCheckRes:rnCheckResList){
                firstMergeAssets(rnCheckRes);
            }
            this.rnVersionManager.getSharedPreference().edit().putString("lastVersion",getVersion(mContext)).commit();   //保存客户端版本号
        } catch (IOException e) {
            e.printStackTrace();
        }

        handler.sendEmptyMessage(1);  //通知主线程首次复制已完成
        long time2=System.currentTimeMillis();
        LogUtils.writeLog("首次的复制总耗时为   "+(time2-time1));
    }


    /**
     * 用于从assets文件夹下复制预埋的rn代码和资源的zip包
     *
     * @param rnCheckRes
     */
    private void firstMergeAssets(RnCheckRes rnCheckRes) {
        String moduleName=rnCheckRes.getModuleName();
        try {
            LogUtils.writeLog("开始复制   ");
            FileUtil.deleteAll(RNFilePathUtils.getBundleRootPath(this.mContext));   //删除rn_bundle目录下所有文件
            InputStream assestInput = this.mContext.getAssets().open(moduleName+".zip");
            String dest = RNFilePathUtils.getBaseZipFile(this.mContext, moduleName);
            if (FileUtil.copyFile(assestInput, dest)) {   //copy插件包到宿主的安装目录下
                FileUtil.unZipAll(      //将对应的module的基础包解压到对应的module文件夹下的base文件夹下
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


    /**
     * 获取客户端的版本号
     * @param context
     * @return
     */
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
