package com.leadeon.diffupdate.utils;

/**
 * Created by Lynn on 2017/8/21.
 */

public class LogUtils {

    public static boolean isDebug=true;
    public static String TAG="com.leadeon.diffipdate  日志   ";

    public static void writeLog(String msg){

        if(isDebug){
            System.out.println(TAG+msg);
        }
    }
}
