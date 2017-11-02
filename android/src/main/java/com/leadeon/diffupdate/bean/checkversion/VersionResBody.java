package com.leadeon.diffupdate.bean.checkversion;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lynn on 2017/10/24.
 */

public class VersionResBody implements Serializable {
    private List<RnCheckRes> patchs=null;


    public List<RnCheckRes> getPatchs() {
        return patchs;
    }

    public void setPatchs(List<RnCheckRes> patchs) {
        this.patchs = patchs;
    }
}
