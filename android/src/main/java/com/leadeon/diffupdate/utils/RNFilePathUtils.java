package com.leadeon.diffupdate.utils;

import android.content.Context;

import java.io.File;

/**
 * Created by Lynn on 2017/8/8.
 */

public class RNFilePathUtils {

    public static String INDEX_BUNDLE_NAME="main.jsbundle";
    public static String BASE_BUNDLE_NAME="main.jsbundle";
    public static String BASE_BUNDLE_ZIP="base.zip";


    public static String MERGE_BUNDLE_NAME="merge.jsbundle";
    public static String BUNDLE_PATCH_NAME="patch.jsbundle";
    public static String PATCH_ZIP_NAME="patch.zip";



    public static String getBundleRootPath(Context context){
        return context.getApplicationContext().getCacheDir().getParent()+File.separator+"rn_bundle";
    }


    public static String getBundlePath(Context context,String moduleName){
        return getBundleRootPath(context)+File.separator+moduleName+File.separator+"index";
    }
    public static String getBundleFile(Context context,String moduleName){
        return getBundleRootPath(context)+File.separator+moduleName+File.separator+"index"+File.separator+INDEX_BUNDLE_NAME;
    }

    /**
     * 获取patch的路径
     * @param context
     * @return
     */
    public static String getBundlePatchPath(Context context,String moduleName){
        return getBundleRootPath(context)+File.separator+moduleName+File.separator+"patch";
    }

    public static String getBundlePatchFile(Context context,String moduleName){
        return getBundleRootPath(context)+File.separator+moduleName+File.separator+"patch"+File.separator+BUNDLE_PATCH_NAME;
    }
    public static String getBundleZipFile(Context context,String moduleName){
        return getBundleRootPath(context)+File.separator+moduleName+File.separator+"patch"+File.separator+PATCH_ZIP_NAME;
    }

    /**
     * 获取用于存放合并文件的路径
     * @param context
     * @return
     */
    public static String getBundleMergePath(Context context,String moduleName){
        return getBundleRootPath(context)+File.separator+moduleName+File.separator+"merge";
    }
    public static String getBundleMergeFile(Context context,String moduleName){
        return getBundleRootPath(context)+File.separator+moduleName+File.separator+"merge"+File.separator+MERGE_BUNDLE_NAME;
    }
    /**
     * 获取存储基线包的路径
     * @param context
     * @return
     */
    public static String getBaseBundlePath(Context context,String moduleName){
        return getBundleRootPath(context)+File.separator+moduleName+File.separator+"base";
    }

    public static String getBaseBundleFile(Context context,String moduleName){
        return getBundleRootPath(context)+File.separator+moduleName+File.separator+"base"+File.separator+BASE_BUNDLE_NAME;
    }

    public static String getBaseZipFile(Context context,String moduleName){
        return getBundleRootPath(context)+File.separator+moduleName+File.separator+"base"+File.separator+BASE_BUNDLE_ZIP;
    }
}
