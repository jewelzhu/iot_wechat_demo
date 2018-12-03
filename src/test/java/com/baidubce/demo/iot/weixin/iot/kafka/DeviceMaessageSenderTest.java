package com.baidubce.demo.iot.weixin.iot.kafka;

import com.baidubce.demo.iot.weixin.WxMpDemoApplication;
import com.baidubce.demo.iot.weixin.iot.dm.DeviceMessageSender;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = WxMpDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Slf4j
public class DeviceMaessageSenderTest {
    @Autowired
    DeviceMessageSender deviceMessageSender;

    @Test
    public void testSendUrl() {
        deviceMessageSender.sendAudioUrlToDevice("a_sample_device", "hello");
    }
}
