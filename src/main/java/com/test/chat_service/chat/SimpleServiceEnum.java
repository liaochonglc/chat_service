package com.test.chat_service.chat;

public enum SimpleServiceEnum {

    USER_SEND_MSG_TO_OTHER_BY_ID("userSendMsgToOtherById");


    private final String serviceName;

    private SimpleServiceEnum(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName(){
        return serviceName;
    }

}
