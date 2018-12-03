package com.baidubce.demo.iot.weixin.db.mapper;

import com.baidubce.demo.iot.weixin.WxMpDemoApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = WxMpDemoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
@Slf4j
public class DeviceUserBindMapperTest {
    @Autowired
    DeviceUserBindMapper deviceBindMapper;

    @Test
    public void testDeviceUserBindMapper() {
        String wechatUser = "123456";
        String deviceId = "myUnitTestDevice";
        deviceBindMapper.insertBind(wechatUser, deviceId, new Date());
        log.info("device={}", deviceBindMapper.getDeviceByBindUser(wechatUser));
        assertEquals(deviceId, deviceBindMapper.getDeviceByBindUser(wechatUser).getDeviceId());
        assertEquals(wechatUser, deviceBindMapper.getBindUsersOfDevice(deviceId).get(0).getWechatUserId());

        deviceBindMapper.deleteBind(wechatUser);
        assertEquals(null, deviceBindMapper.getDeviceByBindUser(wechatUser));
        assertEquals(0, deviceBindMapper.getBindUsersOfDevice(deviceId).size());
    }
}
