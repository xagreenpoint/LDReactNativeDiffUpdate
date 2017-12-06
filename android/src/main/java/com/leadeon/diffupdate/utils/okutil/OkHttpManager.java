package com.leadeon.diffupdate.utils.okutil;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.leadeon.diffupdate.utils.LogUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Lynn on 2017/9/28.
 */

public class OkHttpManager {

    /**
     * 静态实例
     */
    private static OkHttpManager sOkHttpManager;



    /**
     * 因为我们请求数据一般都是子线程中请求，在这里我们使用了handler
     */
    private Handler mHandler;

    /**
     * 单例模式  获取OkHttpManager实例
     *
     * @return
     */
    public static OkHttpManager getInstance() {
        if (sOkHttpManager == null) {
            sOkHttpManager = new OkHttpManager();
        }
        return sOkHttpManager;
    }


    /**
     * 构造方法
     */
    private OkHttpManager() {
        /**
         * 初始化handler
         */
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 数据回调接口
     */
    public interface DataCallBack {
        void requestSuccess(String result);
        void requestError(String retCode);
    }


    public void post(final String urls, final Object object, final DataCallBack dataCallBack){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    postJson(urls,object,dataCallBack);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void postJson(String urls, Object object, final DataCallBack dataCallBack) throws IOException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urls);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
        String jsonString = JSON.toJSONString(object);

        LogUtils.writeLog("请求 为    "+jsonString);

        wr.writeBytes(jsonString);
        wr.flush();
        wr.close();
        // try to get response
        final int statusCode = urlConnection.getResponseCode();
        if (statusCode == 200) {
            final StringBuffer sb = new StringBuffer();
            String str = null;
            InputStream inputStream1=urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream1));
            try {
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                inputStream1.close();
            }
            final String resout=sb.toString();
           mHandler.post(new Runnable() {
               @Override
               public void run() {
                dataCallBack.requestSuccess(resout);
               }
           });
            LogUtils.writeLog("相应结果为："+resout);
        }else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    dataCallBack.requestError(""+statusCode);
                }
            });
        }

    }

}
