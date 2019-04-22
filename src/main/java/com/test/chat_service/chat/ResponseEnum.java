package com.test.chat_service.chat;

public enum ResponseEnum {

    OK(100,"OK"),

    LOGIN_FAIL(101,"账号或密码错误"),

    INVALID_REQUEST(201,"无效的请求格式"),

    INVALID_SERVICE(202,"无效的业务请求"),

    INVALID_PARAMS(203,"无效的参数格式"),

    NO_SUCH_RECEIVER(204,"无效的消息接收方")
    ;

    private final int code;
    private final String msg;

    ResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
