package com.baidubce.demo.iot.weixin.iot.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoiceMessage {
    private byte[] voiceData;
    private VoiceMetaData voiceMetaData;
}
