package com.leadeon.diffupdate.bean.checkversion;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lynn on 2017/10/24.
 */

public class VersionRes implements Serializable {

  private String retCode = null;
  private String retDesc = null;
  private List<RnCheckRes> rspBody = null;

  public String getRetCode() {
    return retCode;
  }

  public void setRetCode(String retCode) {
    this.retCode = retCode;
  }

  public String getRetDesc() {
    return retDesc;
  }

  public void setRetDesc(String retDesc) {
    this.retDesc = retDesc;
  }

  public List<RnCheckRes> getRspBody() {
    return rspBody;
  }

  public void setRspBody(List<RnCheckRes> rspBody) {
    this.rspBody = rspBody;
  }
}
