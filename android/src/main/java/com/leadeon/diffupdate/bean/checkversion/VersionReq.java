package com.leadeon.diffupdate.bean.checkversion;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Lynn on 2017/10/24.
 */

public class VersionReq implements Serializable {


    private String appKey=null;
    private String appVersion=null;
    private String platForm=null;
    private String rnVersion=null;
    private HashMap<String ,String> reqParam=null;


    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppVersion() {
        return appVersion;
    }


  public String getPlatForm() {
    return platForm;
  }

  public void setPlatForm(String platForm) {
    this.platForm = platForm;
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

  public HashMap<String, String> getReqParam() {
    return reqParam;
  }

  public void setReqParam(HashMap<String, String> reqParam) {
    this.reqParam = reqParam;
  }
}
