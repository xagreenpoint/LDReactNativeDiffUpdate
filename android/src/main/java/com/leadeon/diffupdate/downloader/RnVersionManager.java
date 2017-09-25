package com.leadeon.diffupdate.downloader;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.leadeon.diffupdate.bean.RnCheckRes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Lynn on 2017/8/20.
 */

public class RnVersionManager {

    private String RN_VERSION_KEY = "rn_version_key";
    private String RN_VERSION_SP = "rn_version_sp";
    private Context context = null;

    public RnVersionManager(Context context) {
        this.context = context;
    }

    public synchronized void saveRnDescription(RnCheckRes rnCheckRes) {
        if (rnCheckRes == null) {
            return;
        }
        Hashtable<String, RnCheckRes> oldHashTable = readRn(RN_VERSION_KEY);
        if (oldHashTable == null) {
            oldHashTable = new Hashtable<>();
        }
        oldHashTable.put(rnCheckRes.getModuleName(), rnCheckRes);
        saveRn(RN_VERSION_KEY, oldHashTable);
    }

    /**
     * 用于清空rn模块的版本信息
     */
    public void deleteRnDescription(){
        saveRn(RN_VERSION_KEY, new Hashtable<String, RnCheckRes>());
    }

    public synchronized Hashtable<String, RnCheckRes> getRnDescription() {
        return readRn(RN_VERSION_KEY);
    }

    /**
     * 获取所有的Rn模块的版本信息
     * 返回一个hashmap，map的key是模块的名字
     * map的value是rn的版本号
     * @return
     */
    public synchronized HashMap<String, String> getRnVersion() {
        HashMap<String, String> hashMap = new HashMap<>();
        Hashtable<String, RnCheckRes> hashtable = readRn(RN_VERSION_KEY);
        if (hashtable != null) {
            for (Map.Entry<String, RnCheckRes> entry : hashtable.entrySet()) {
                hashMap.put(entry.getKey(), entry.getValue().getVersion());
            }
        }
        return hashMap;
    }


    public SharedPreferences getSharedPreference() {
        return this.context.getApplicationContext().getSharedPreferences(RN_VERSION_SP, Context.MODE_PRIVATE);
    }


    private synchronized boolean saveRn(String key, Hashtable<String, RnCheckRes> rns) {

        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(rns);
            objectOutputStream.flush();

            byte[] data = byteArrayOutputStream.toByteArray();
            String list = Base64.encodeToString(data, Base64.DEFAULT);

            getSharedPreference().edit().putString(key, list).commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private synchronized Hashtable<String, RnCheckRes> readRn(String key) {
        String list = getSharedPreference().getString(key, "");
        Serializable object = null;
        if (!TextUtils.isEmpty(list)) {
            ByteArrayInputStream byteArrayInputStream = null;
            ObjectInputStream objectInputStream = null;
            try {
                byteArrayInputStream = new ByteArrayInputStream(Base64.decode(list, Base64.DEFAULT));
                objectInputStream = new ObjectInputStream(byteArrayInputStream);
                object = (Serializable) objectInputStream.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (byteArrayInputStream != null) {
                    try {
                        byteArrayInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return (Hashtable<String, RnCheckRes>) object;
    }


}
