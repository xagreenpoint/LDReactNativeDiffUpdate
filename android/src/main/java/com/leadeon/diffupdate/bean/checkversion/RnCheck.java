package com.leadeon.diffupdate.bean.checkversion;

import java.io.Serializable;

/**
 * Created by Lynn on 2017/12/5.
 */

public class RnCheck implements Serializable {

  private VersionReq reqBody=null;

  public VersionReq getReqBody() {
    return reqBody;
  }

  public void setReqBody(VersionReq reqBody) {
    this.reqBody = reqBody;
  }
}
