package com.baidubce.demo.iot.weixin.handler;

import com.baidubce.demo.iot.weixin.builder.TextBuilder;
import com.baidubce.demo.iot.weixin.db.mapper.DeviceUserBindMapper;
import com.baidubce.demo.iot.weixin.model.QRCodeContent;
import com.baidubce.demo.iot.weixin.model.db.DeviceUserBindDto;
import com.baidubce.demo.iot.weixin.utils.IotWechatException;
import com.baidubce.demo.iot.weixin.utils.IotWechatExceptionCode;
import com.baidubce.demo.iot.weixin.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Component
@Slf4j
public class ScanWaitmsgHandler extends AbstractHandler {
    @Autowired
    DeviceUserBindMapper deviceUserBindMapper;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {
        String codeContent = wxMessage.getScanCodeInfo().getScanResult();
        log.debug("qrcodeContent={}", codeContent);
        QRCodeContent qrCodeContent = JsonUtils.fromJson(codeContent, QRCodeContent.class);
        String reply;
        try {
            validQRCodeContent(qrCodeContent);
            DeviceUserBindDto deviceUserBindDto = deviceUserBindMapper.getDeviceByBindUser(wxMessage.getFromUser());
            if (deviceUserBindDto == null) {
                deviceUserBindMapper.insertBind(wxMessage.getFromUser(), qrCodeContent.getDeviceId(), new Date());
                reply = String.format("成功绑定设备%s，您可以和设备对话啦", qrCodeContent.getDeviceId());
            }
            else if (deviceUserBindDto.getDeviceId().equals(qrCodeContent.getDeviceId())) {
                reply = String.format("您已经绑定过设备%s，无需重复绑定", qrCodeContent.getDeviceId());
            }
            else {
                deviceUserBindMapper.deleteBind(wxMessage.getFromUser());
                deviceUserBindMapper.insertBind(wxMessage.getFromUser(), qrCodeContent.getDeviceId(), new Date());
                reply = String.format("您已从旧设备%s解绑，并成功绑定新设备%s", deviceUserBindDto.getDeviceId(),
                        qrCodeContent.getDeviceId());
            }
        } catch (IotWechatException e) {
            log.warn("QR code content check fail", e);
            reply = e.getMessage();
        }
        return new TextBuilder().build(reply, wxMessage, weixinService);
    }

    private void validQRCodeContent(QRCodeContent qrCodeContent) {
        // TODO 设备码合法性验证  限制一个设备最多绑定N个微信账户，一个微信账户只能绑一个设备
        if (qrCodeContent == null || StringUtils.isBlank(qrCodeContent.getDeviceId())) {
            throw new IotWechatException("绑定失败，这不是一个合法设备的二维码哦", IotWechatExceptionCode.QR_CODE_INVALID);
        }
    }
}
