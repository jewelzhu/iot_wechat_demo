package com.baidubce.demo.iot.weixin.task;

import com.baidubce.demo.iot.weixin.db.mapper.DeviceUserBindMapper;
import com.baidubce.demo.iot.weixin.model.db.DeviceUserBindDto;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;

@Component
@Slf4j
public class DeviceVoiceSender {

    @Autowired
    WxMpService wxMpService;

    @Autowired
    DeviceUserBindMapper deviceUserBindMapper;

    public void sendDeviceVoicesToSubUsers(String deviceId, String mp3FileName) {
        String mediaId = null;
        try {
            File file = new File(mp3FileName);
            WxMediaUploadResult res = wxMpService.getMaterialService().mediaUpload(WxConsts.MediaFileType.VOICE, file);
            log.info("upload voice file {} of {} to wechat success, result = {}", mp3FileName, deviceId, res);
            mediaId = res.getMediaId();
        } catch (Exception e) {
            log.error("upload {} voice fail. Your madia quota might exceed!", mp3FileName, e);
            return;
        }

        List<DeviceUserBindDto> binds = deviceUserBindMapper.getBindUsersOfDevice(deviceId);
        log.debug("users subscribed {} are {}", deviceId, binds);
        if (!CollectionUtils.isEmpty(binds)) {
            for (DeviceUserBindDto bind : binds) {
                try {
                    WxMpKefuMessage kefuMessage = WxMpKefuMessage.VOICE().mediaId(mediaId).toUser(bind.getWechatUserId()).build();
                    boolean messageResult = wxMpService.getKefuService().sendKefuMessage(kefuMessage);
                    log.info("send voice to user {} success, result={}", bind.getWechatUserId(), messageResult);
                } catch (Exception e) {
                    log.warn("send message to user {} fail, might due to no interact for more then 48 hours",
                            bind.getWechatUserId());
                }
            }
        }
    }
}
