package com.baidubce.demo.iot.weixin.utils;

public class IotWechatException extends RuntimeException {
    private IotWechatExceptionCode code;

    public IotWechatException(String message, IotWechatExceptionCode code) {
        super(message);
        this.code = code;
    }

    public IotWechatException(IotWechatExceptionCode code) {
        super(code.getDefaultMessage());
        this.code = code;
    }

    public IotWechatExceptionCode getCode() {
        return this.code;
    }
}
