package com.baidubce.demo.iot.weixin.iot.kafka;

import com.baidubce.demo.iot.weixin.audio.AudioConverter;
import com.baidubce.demo.iot.weixin.iot.kafka.model.VoiceMessage;
import com.baidubce.demo.iot.weixin.iot.kafka.model.VoiceMetaData;
import com.baidubce.demo.iot.weixin.task.DeviceVoiceSender;
import com.baidubce.demo.iot.weixin.utils.IotWechatException;
import com.baidubce.demo.iot.weixin.utils.IotWechatExceptionCode;
import com.baidubce.demo.iot.weixin.utils.JsonUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/*
* You can reliably convert from byte[] to String and back, with a one-to-one mapping of chars to bytes, if you use the encoding "iso8859-1".
* */

@Component
@Slf4j
public class VoicePieceMessageParser {
    private static final String BOUNDARY = "\r\n--\r\n";

    @Autowired
    DeviceVoiceSender deviceVoiceSender;

    @Autowired
    AudioConverter audioConverter;

    @Value("${local.file.keep.mp3:false}")
    boolean keepTmpMp3Files;

    // 本地缓存模式仅适用于单个公众号服务器
    // TODO 如果部署多台公众号服务器的话，一段语音的多个片段会落在多台公众号服务器上，需要使用一个共享的cache（例如redis)

    // 另外 当前kafka只有一个partition，因此可以保证message的顺序，当收到isEnd=1的消息时即可触发语音重组
    // TODO 如果kafka有多个partition，当收到isEnd=1的消息时，可能前面的语音片段还未全部抵达，则需要延时机制，例如定时扫描器来触发语音的重组和发送
    private LoadingCache<String, List<VoiceMessage>> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, List<VoiceMessage>>() {
                        @Override
                        public List<VoiceMessage> load(String messageId) throws Exception {
                            return new ArrayList<>();
                        }
                    }
            );

    public List<VoiceMessage> getFromCache(String messageId) {
        try {
            return cache.get(messageId);
        } catch (ExecutionException e) {
            log.error("get {} from cache fail", messageId, e);
            return new ArrayList<>();
        }
    }

    /**
     * TODO 限制单组语音分片的最大个数，和最大长度
     * TODO 限制单个device每日最多可发送语音消息的数目
     * @param mqttPayload
     * @throws Exception
     */
    public void process(byte[] mqttPayload) throws Exception {
        String value = new String(mqttPayload, StandardCharsets.ISO_8859_1);
        String[] parts = value.split(BOUNDARY);
        if (parts.length != 2) {
            log.warn("unexpected parts_len={}, parts={}", parts.length, parts);
            throw new IotWechatException(IotWechatExceptionCode.INVALID_VOICE_MESSAGE_FROM_DEVICE);
        }
        String metaDataInJson = parts[0];
        VoiceMetaData voiceMetaData = JsonUtils.fromJson(metaDataInJson, VoiceMetaData.class);
        log.info("metaDataInJson={} {}", metaDataInJson, voiceMetaData);

        byte[] voiceData = parts[1].getBytes("ISO-8859-1");
        log.info("voiceDataLength={}", voiceData.length);

        List<VoiceMessage> voicePieces = getFromCache(voiceMetaData.getMsgId());
        voicePieces.add(new VoiceMessage(voiceData, voiceMetaData));
        if (voiceMetaData.isEnd()) {
            VoiceMessage wholeVoice = combinePiecesIntoVoice(voicePieces);

            // pcm转mp3（或amr)
            String mp3FilePath = audioConverter.convertFromPcmToMp3(wholeVoice.getVoiceData());

            // 发送到关注了deviceId的微信用户
            deviceVoiceSender.sendDeviceVoicesToSubUsers(wholeVoice.getVoiceMetaData().getDevId(), mp3FilePath);

            // 删除该mp3文件
            if (!keepTmpMp3Files) {
                try {
                    new File(mp3FilePath).deleteOnExit();
                } catch (Exception e) {
                    log.warn("delete file {} fail", mp3FilePath);
                }
            }

            // 注销该语音文件在本地内存中的缓存
            cache.invalidate(voiceMetaData.getMsgId());
        }
        else {
            log.info("voice message {} doesn't finish yet", voiceMetaData.getMsgId());
        }
    }

    /**
     * 将多个语音分片信息整合成一个完整的语音信息
     */
    public VoiceMessage combinePiecesIntoVoice(List<VoiceMessage> voicePieces) {
        log.debug("combinePiecesIntoVoice start");
        voicePieces.sort(Comparator.comparing(o -> o.getVoiceMetaData().getSeq()));
        int length = 0;
        for (VoiceMessage voicePiece : voicePieces) {
            length += voicePiece.getVoiceData().length;
        }
        byte[] all = new byte[length];
        int i = 0;
        for (VoiceMessage voicePiece : voicePieces) {
            int pieceLength = voicePiece.getVoiceData().length;
            System.arraycopy(voicePiece.getVoiceData(), 0, all, i, pieceLength);
            i += pieceLength;
        }
        log.debug("combinePiecesIntoVoice finish, voice length={}", length);
        String messageId = voicePieces.get(0).getVoiceMetaData().getMsgId();
        String deviceId = voicePieces.get(0).getVoiceMetaData().getDevId();
        return new VoiceMessage(all, new VoiceMetaData(messageId, 0, 1, deviceId));
    }
}
