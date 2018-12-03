package com.baidubce.demo.iot.weixin.iot.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoiceMetaData {
    private String msgId; // voice message id
    private int seq;
    private int isEnd;
    private String devId; // device id

    public boolean isEnd() {
        return isEnd == 1;
    }
}
