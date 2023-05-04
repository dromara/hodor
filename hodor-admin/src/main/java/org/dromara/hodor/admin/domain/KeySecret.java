package org.dromara.hodor.admin.domain;

import java.io.Serializable;

public class KeySecret implements Serializable {

    private static final long serialVersionUID = 4533794352731465965L;
    private String appKey;
    private String appSecret;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    @Override
    public String toString() {
        return "KeySecret [appKey=" + appKey + ", appSecret=" + appSecret + "]";
    }

}
