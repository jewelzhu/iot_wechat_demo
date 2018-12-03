package com.baidubce.demo.iot.weixin.audio;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;


@Slf4j
public class AudioConversionTest {
    @Test
    public void convertPcmToMP3() throws Exception {
        String inputPcmFile = "/tmp/1521013305045.pcm";
        String outputMp3File = "/Users/zhuzhu01/Documents/"+System.currentTimeMillis()+".mp3";
        String cmd = String.format("ffmpeg -f s16le -ar 16000 -ac 1 -i %s -ac 1 -ar 16000 %s",
                inputPcmFile, outputMp3File);
        log.info(cmd);
        Process process =
                Runtime.getRuntime().exec(cmd);
        log.info("exit value={}", process.waitFor());
    }

    @Test
    public void play() throws Exception {
        //File file = new File("/Users/zhuzhu01/Documents/test.mp3");
        File file = new File("/Users/zhuzhu01/Documents/test.pcm");
        AudioInputStream in= AudioSystem.getAudioInputStream(file);
        AudioInputStream din = null;
        AudioFormat baseFormat = in.getFormat();
        AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2,
                baseFormat.getSampleRate(),
                false);
        din = AudioSystem.getAudioInputStream(decodedFormat, in);
        // Play now.
        rawplay(decodedFormat, din);
        in.close();
    }

    private void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException
    {
        byte[] data = new byte[4096];
        SourceDataLine line = getLine(targetFormat);
        if (line != null)
        {
            // Start
            line.start();
            int nBytesRead = 0, nBytesWritten = 0;
            while (nBytesRead != -1)
            {
                nBytesRead = din.read(data, 0, data.length);
                if (nBytesRead != -1) nBytesWritten = line.write(data, 0, nBytesRead);
            }
            // Stop
            line.drain();
            line.stop();
            line.close();
            din.close();
        }
    }

    private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
    {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);
        return res;
    }
}
