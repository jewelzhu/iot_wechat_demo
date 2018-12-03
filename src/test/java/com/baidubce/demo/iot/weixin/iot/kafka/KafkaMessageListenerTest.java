package com.baidubce.demo.iot.weixin.iot.kafka;

import com.baidubce.demo.iot.weixin.WxMpDemoApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = WxMpDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Slf4j
public class KafkaMessageListenerTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.device.voice.topic}")
    private String topic;

    @Test
    public void testSendMsg() throws Exception {
        log.info("send hi to kafka topic {} start", topic);
        kafkaTemplate.send(topic, "hi");
        log.info("send hi to kafka topic {} end", topic);
        Thread.sleep(2000);
    }
}
