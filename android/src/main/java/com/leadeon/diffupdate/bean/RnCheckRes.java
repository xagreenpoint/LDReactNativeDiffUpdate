package com.leadeon.diffupdate.bean;

import java.io.Serializable;

/**
 * Created by Lynn on 2017/8/17.
 */

public class RnCheckRes implements Serializable {
    private String loadType=null;           //业务类型：ReactNative、HybridApp
    private String zipPath = null;          //下载路径
    private String version = null;          //业务版本号
    private String moduleName = null;       //jsBundle名称
    private String zipHash = null;          //zip文件md5值
    private String jsbundleHash = null;     //差异合并后js文件md5值
    private String downloadNow=null;        //0：总是下载, 1:wifi下载，2: 4g和wifi下载
    private String loadNow=null;            //true:即刻更新，false:下次启动更新
    private String needGoBack=null;         //是否需要回退版本


    public String getLoadType() {
        return loadType;
    }

    public void setLoadType(String loadType) {
        this.loadType = loadType;
    }

    public String getZipPath() {
        return zipPath;
    }

    public void setZipPath(String zipPath) {
        this.zipPath = zipPath;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getZipHash() {
        return zipHash;
    }

    public void setZipHash(String zipHash) {
        this.zipHash = zipHash;
    }

    public String getJsbundleHash() {
        return jsbundleHash;
    }

    public void setJsbundleHash(String jsbundleHash) {
        this.jsbundleHash = jsbundleHash;
    }

    public String getDownloadNow() {
        return downloadNow;
    }

    public void setDownloadNow(String downloadNow) {
        this.downloadNow = downloadNow;
    }

    public String getLoadNow() {
        return loadNow;
    }

    public void setLoadNow(String loadNow) {
        this.loadNow = loadNow;
    }

    public String getNeedGoBack() {
        return needGoBack;
    }

    public void setNeedGoBack(String needGoBack) {
        this.needGoBack = needGoBack;
    }
}
