package com.baidubce.demo.iot.weixin.utils;

public enum IotWechatExceptionCode {
    QR_CODE_INVALID("二维码"),
    INVALID_VOICE_MESSAGE_FROM_DEVICE("来自端的消息格式不合法"),
    VOICE_CONVERSION_TIMEOUT("语音转码超时"),
    VOICE_CONVERSION_FAIL("语音转码失败");

    private String defaultMessage;

    IotWechatExceptionCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }
}
