package com.leadeon.diffupdate;

import android.content.Context;
import android.content.Intent;

import com.leadeon.diffupdate.downloader.RnModuleDiffUpdateService;

/**
 * Created by Lynn on 2017/9/1.
 */

public class LeadeonDiff {
    public static void startDiff(Context context)  {
        if(context!=null){
            Intent intent=new Intent(context, RnModuleDiffUpdateService.class);
            context.getApplicationContext().startService(intent);
        }
    }


    public static void stopDiff(Context context){
        if(context!=null){
            Intent intent=new Intent(context, RnModuleDiffUpdateService.class);
            context.getApplicationContext().stopService(intent);
        }
    }
}
