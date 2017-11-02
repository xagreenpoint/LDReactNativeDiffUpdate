package com.leadeon.diffupdate.bean.checkversion;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Lynn on 2017/10/24.
 */

public class VersionReq implements Serializable {


    private String appKey=null;
    private String appVersion=null;
    private String platform=null;
    private String rnVersion=null;
    private HashMap<String ,String> reqBody=null;


    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }


    public String getRnVersion() {
        return rnVersion;
    }

    public void setRnVersion(String rnVersion) {
        this.rnVersion = rnVersion;
    }

    public HashMap<String, String> getReqBody() {
        return reqBody;
    }

    public void setReqBody(HashMap<String, String> reqBody) {
        this.reqBody = reqBody;
    }
}
