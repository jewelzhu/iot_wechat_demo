package com.baidubce.demo.iot.weixin.iot.dm;

import com.baidubce.BceClientConfiguration;
import com.baidubce.Protocol;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.iotdm.IotDmV3Client;
import com.baidubce.services.iotdm.model.v3.device.DeviceAttributes;
import com.baidubce.services.iotdm.model.v3.device.DeviceProfileResponse;
import com.baidubce.services.iotdm.model.v3.device.DeviceViewResponse;
import com.baidubce.services.iotdm.model.v3.device.UpdateDeviceProfileRequest;
import com.baidubce.services.iotdm.model.v3.device.UpdateDeviceViewRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class DeviceMessageSender {
    @Value("${bce.ak}")
    private String bceAk;

    @Value("${bce.sk}")
    private String bceSk;

    @Value("${bce.iot.dm.endpoint: iotdm.bj.baidubce.com}")
    private String bceDmEndpoint;

    private IotDmV3Client client;

    @PostConstruct
    public void setup() {
        BceClientConfiguration config = new BceClientConfiguration()
                .withProtocol(Protocol.HTTPS)
                .withCredentials(new DefaultBceCredentials(bceAk, bceSk))
                .withEndpoint(bceDmEndpoint);

        client = new IotDmV3Client(config);
    }

    public void sendAudioUrlToDevice(String deviceId, String url) {
        log.info("send audioUrl {} to {}", url, deviceId);
        ObjectNode reported = new ObjectMapper().createObjectNode();
        reported.put("audioUrl", url);

        ObjectNode desired = new ObjectMapper().createObjectNode();
        desired.put("audioUrl", url);

        UpdateDeviceViewRequest request = new UpdateDeviceViewRequest()
                .withReported(reported)
                .withDesired(desired);

        DeviceViewResponse deviceViewResponse = client.updateDeviceView(deviceId, request);
        log.info("update dm device view response = {}", deviceViewResponse);
    }
}
