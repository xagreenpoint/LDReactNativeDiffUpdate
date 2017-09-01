package com.leadeon.diffupdate.bean;

import java.io.Serializable;

/**
 * Created by Lynn on 2017/8/17.
 */

public class RnCheckRes implements Serializable {
    private String zipPath = null;
    private String version = null;
    private String moduleName = null;
    private String zipHash = null;
    private String jsbundleHash = null;

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
}
