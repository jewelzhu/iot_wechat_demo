package com.baidubce.demo.iot.weixin.controller;

import com.baidubce.demo.iot.weixin.WxMpDemoApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = WxMpDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Slf4j
public class WechatControllerTest {
    @Autowired
    private WechatController wechatController;

    @Test
    public void testScanInvalidCodeEvent() {
        String requestBody = "<xml><ToUserName><![CDATA[gh_b4cc5582f778]]></ToUserName>\n" +
                "<FromUserName><![CDATA[oNiyD1EJ_-NWVXq3H-OV5Kf5BgJ8]]></FromUserName>\n" +
                "<CreateTime>1519957274</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[scancode_waitmsg]]></Event>\n" +
                "<EventKey><![CDATA[scancode1]]></EventKey>\n" +
                "<ScanCodeInfo><ScanType><![CDATA[qrcode]]></ScanType>\n" +
                "<ScanResult><![CDATA[{\"deviceId\":\"123456\"}]]></ScanResult>\n" +
                "</ScanCodeInfo>\n" +
                "</xml>";
        String signature = "19f332985bac0d7efea25303e69495ca5375ef37";
        String timestamp = "1519957274";
        String nonce = "1966589949";
        String encType = null;
        String msgSignature = null;
        String response = wechatController.post(requestBody, signature, timestamp, nonce, encType, msgSignature);
        log.info("response={}", response);
    }

    @Test
    public void testScanValidCodeEvent() {
        String requestBody = "<xml><ToUserName><![CDATA[gh_b4cc5582f778]]></ToUserName>\n" +
                "<FromUserName><![CDATA[oNiyD1EJ_-NWVXq3H-OV5Kf5BgJ8]]></FromUserName>\n" +
                "<CreateTime>1520238419</CreateTime>\n" +
                "<MsgType><![CDATA[event]]></MsgType>\n" +
                "<Event><![CDATA[scancode_waitmsg]]></Event>\n" +
                "<EventKey><![CDATA[scancode1]]></EventKey>\n" +
                "<ScanCodeInfo><ScanType><![CDATA[qrcode]]></ScanType>\n" +
                "<ScanResult><![CDATA[{\n" +
                "        \"deviceId\": \"123456\"\n" +
                "}]]></ScanResult>\n" +
                "</ScanCodeInfo>\n" +
                "</xml>";
        String signature = "0b42c0fa2afe0f4aaef2c27952470beb015b4dfc";
        String timestamp = "1520238419";
        String nonce = "1263205797";
        String encType = null;
        String msgSignature = null;
        String response = wechatController.post(requestBody, signature, timestamp, nonce, encType, msgSignature);
        log.info("response={}", response);
    }

    @Test
    public void testVoiceEvent() {
        String requestBody = "<xml><ToUserName><![CDATA[gh_b4cc5582f778]]></ToUserName>\n" +
                "<FromUserName><![CDATA[oNiyD1EJ_-NWVXq3H-OV5Kf5BgJ8]]></FromUserName>\n" +
                "<CreateTime>1521023125</CreateTime>\n" +
                "<MsgType><![CDATA[voice]]></MsgType>\n" +
                "<MediaId><![CDATA[RUwFshEJBFf_JTqB-CosEuIuIK_RzoIYDx2wQRmYXZGkFbHmwzIC2RfhYx3g4Bxr]]></MediaId>\n" +
                "<Format><![CDATA[amr]]></Format>\n" +
                "<MsgId>6532744578767974183</MsgId>\n" +
                "<Recognition><![CDATA[]]></Recognition>\n" +
                "</xml>";
        String signature = "e63a1b046ae97741d779b79d4baadae414f0c341";
        String timestamp = "1520237790";
        String nonce = "726296868";
        String encType = null;
        String msgSignature = null;
        String response = wechatController.post(requestBody, signature, timestamp, nonce, encType, msgSignature);
        log.info("response={}", response);
    }
}
