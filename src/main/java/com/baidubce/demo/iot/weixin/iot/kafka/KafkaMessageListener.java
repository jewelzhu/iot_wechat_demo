package com.baidubce.demo.iot.weixin.iot.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaMessageListener {
    @Autowired
    VoicePieceMessageParser voicePieceMessageParser;

    @KafkaListener(topics = "${kafka.device.voice.topic}")
    public void receive(ConsumerRecord<String, byte[]> consumerRecord) {
        log.info("receive message from kafka, message value length={}", consumerRecord.value().length);
        try {
            voicePieceMessageParser.process(consumerRecord.value());
        } catch (Exception e) {
            log.warn("process message {} fail", consumerRecord, e);
        }
    }
}
