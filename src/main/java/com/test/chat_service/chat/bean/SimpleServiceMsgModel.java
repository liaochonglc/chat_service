package com.test.chat_service.chat.bean;

import com.alibaba.fastjson.JSONObject;

public class SimpleServiceMsgModel {

    private String serviceName;
    private JSONObject params;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }
}
