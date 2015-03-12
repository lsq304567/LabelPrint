package com.inspur.common.app;

import android.app.Application;

import com.inspur.common.UserInfo;

/**
 * Created by Administrator on 2014/12/2 0002.
 */
public class LabelPrintApplication extends Application {

    public static final String TAG=LabelPrintApplication.class.getSimpleName();

    private UserInfo userInfo=new UserInfo();

    //外网地址
    private String serviceUrl;

    //可修改的资源模型
    private String modifyRes = "1";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getModifyRes() {
        return modifyRes;
    }

    public void setModifyRes(String modifyRes) {
        this.modifyRes = modifyRes;
    }

}
