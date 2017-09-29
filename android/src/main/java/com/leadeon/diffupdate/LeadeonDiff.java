package com.leadeon.diffupdate;

import android.content.Context;

import com.leadeon.diffupdate.downloader.RnModuleDiffUpdateManager;
import com.leadeon.diffupdate.utils.RNFilePathUtils;

import java.io.File;

/**
 * Created by Lynn on 2017/9/1.
 */

public class LeadeonDiff {


    /**
     * 当首次复制assets中的模块完成时会  回调
     * CopyCompletedCallback 接口，
     * @param context
     * @param copyCompletedCallback
     */
    public static void init(Context context,CopyCompletedCallback copyCompletedCallback)  {
        if(context!=null){
           new RnModuleDiffUpdateManager(context,copyCompletedCallback).start();
        }
    }



    public static String getMoudleJsBundleFile( Context mcontext,String moudleName){
        File indexFile=new File(RNFilePathUtils.getBundleFile(mcontext,moudleName));
        if(indexFile.exists()){
            return RNFilePathUtils.getBundleFile(mcontext,moudleName);
        }else {
            return RNFilePathUtils.getBaseBundleFile(mcontext,moudleName);
        }
    }


    public interface CopyCompletedCallback{
        void onCompleted();
    }


}
