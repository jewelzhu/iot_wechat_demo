package com.baidubce.demo.iot.weixin.handler;

import com.baidubce.demo.iot.weixin.db.mapper.DeviceUserBindMapper;
import com.baidubce.demo.iot.weixin.iot.dm.DeviceMessageSender;
import com.baidubce.demo.iot.weixin.model.db.DeviceUserBindDto;
import com.baidubce.demo.iot.weixin.utils.JsonUtils;
import com.baidubce.demo.iot.weixin.builder.TextBuilder;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;
import static me.chanjar.weixin.mp.api.WxMpMaterialService.MEDIA_GET_URL;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
@Slf4j
public class MsgHandler extends AbstractHandler {

  @Autowired
  DeviceUserBindMapper deviceUserBindMapper;

  @Autowired
  WxMpService wxMpService;

  @Autowired
  DeviceMessageSender deviceMessageSender;

  @Override
  public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                  Map<String, Object> context, WxMpService weixinService,
                                  WxSessionManager sessionManager) {
    log.info("MsgHandler receives message {}", wxMessage);

    String reply = null;

    // 收到语音消息，需推送至绑定语音设备
    if (wxMessage.getMsgType().equals(XmlMsgType.VOICE)) {
      String wechatUser = wxMessage.getFromUser();
      DeviceUserBindDto deviceUserBindDto = deviceUserBindMapper.getDeviceByBindUser(wechatUser);
      if (deviceUserBindDto == null) {
        reply = "您当前未绑定任何设备哦，扫码绑定设备后语音才会被推送至设备端";
      }
      else {

        reply = String.format("您的语音消息将被发送至设备%s播放", deviceUserBindDto.getDeviceId());
        String url = null;
        try {
          url = String.format(MEDIA_GET_URL + "?access_token=%s&media_id=%s", wxMpService.getAccessToken(false), wxMessage.getMediaId());
          log.info("wechat voice download url= {} ", url);
        } catch (WxErrorException e) {
          log.error("failed to get accessToken", e);
          return null;
        }
        // 调用物管理接口发布audio url
        deviceMessageSender.sendAudioUrlToDevice(deviceUserBindDto.getDeviceId(), url);
      }
    }
    else {
      reply = "received non-voice messages:" + JsonUtils.toJson(wxMessage);
    }
    return new TextBuilder().build(reply, wxMessage, weixinService);

  }

}
