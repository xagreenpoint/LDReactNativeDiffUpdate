package com.yyh.lib.bsdiff.downloader;

import android.content.Context;

import com.yyh.lib.bsdiff.utils.RNFilePathUtils;

import java.io.File;

/**
 * Created by Lynn on 2017/8/17.
 */

public class ModuleManager {

    public static String getMoudleJsBundleFile( Context mcontext,String moudleName){
        File indexFile=new File(RNFilePathUtils.getBundleFile(mcontext,moudleName));
        if(indexFile.exists()){
            return RNFilePathUtils.getBundleFile(mcontext,moudleName);
        }else {
            return RNFilePathUtils.getBaseBundleFile(mcontext,moudleName);
        }
    }


}
