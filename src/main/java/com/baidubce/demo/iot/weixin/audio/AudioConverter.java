package com.baidubce.demo.iot.weixin.audio;

import com.baidubce.demo.iot.weixin.utils.ShellExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;


@Component
@Slf4j
public class AudioConverter {
    @Value("${local.file.dir:/tmp/}")
    String localDir;

    @Value("${local.file.keep.pcm:false}")
    boolean keepPcmTmpFiles;

    private static int maxConversionExecutionTimeInMilliSeconds = 20000;
    /**
     * @return mp3 file path
     */
    public String convertFromPcmToMp3(byte[] pcmData, String pcmFormat, int pcmChannels,
                                      int pcmSampleRate, int mp3Channels, int mp3SampleRate) throws Exception {

        // save pcm file
        String pcmFile = localDir + UUID.randomUUID() + ".pcm";
        FileOutputStream fos = new FileOutputStream(pcmFile);
        fos.write(pcmData);
        fos.close();
        log.debug("write tmp pcm file {} success", pcmFile);

        // generate mp3 from pcm
        String outputMp3File = localDir + UUID.randomUUID() + ".mp3";
        String cmd = String.format("ffmpeg -f %s -ar %d -ac %d -i %s -ac %d -ar %d %s", pcmFormat, pcmSampleRate, pcmChannels,
                pcmFile, mp3Channels, mp3SampleRate, outputMp3File);
        ShellExecutor.execCmdWithTimout(cmd, maxConversionExecutionTimeInMilliSeconds);

        // remove tmp pcm file
        if (!keepPcmTmpFiles) {
            try {
                new File(pcmFile).deleteOnExit();
            } catch (Exception e) {
                log.warn("delete file {} fail", pcmFile);
            }
        }

        return outputMp3File;
    }

    public String convertFromPcmToMp3(byte[] pcmData) throws Exception {
        return convertFromPcmToMp3(pcmData, "s16le", 1, 16000, 1, 16000);
    }
}
