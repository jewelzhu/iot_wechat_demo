package com.baidubce.demo.iot.weixin.model.db;

import lombok.Data;

import java.util.Date;

@Data
public class DeviceUserBindDto {
    private long id;
    private String wechatUserId;
    private String deviceId;
    private Date bindTime;
}
