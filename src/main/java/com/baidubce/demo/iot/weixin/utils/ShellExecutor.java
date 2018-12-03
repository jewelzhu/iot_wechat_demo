package com.baidubce.demo.iot.weixin.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
public class ShellExecutor {
    public static void execCmdWithTimout(String cmd, long timeOutMilliSeconds)
            throws IotWechatException {
        CommandLine commandLine = CommandLine.parse(cmd);
        DefaultExecutor executor = new DefaultExecutor();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(timeOutMilliSeconds);
        executor.setWatchdog(watchdog);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        ExecuteStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
        executor.setStreamHandler(streamHandler);
        try {
            log.info("execCmdWithTimout start.");
            int exitValue = executor.execute(commandLine);
            log.info("outputStrean:{}", outputStream);
            log.warn("errorStream:{}", errorStream);
            log.info(String.format("Shell with exitValue = [%d]", exitValue));
            if (watchdog.killedProcess()) {
                log.info("time out task");
                throw new IotWechatException(IotWechatExceptionCode.VOICE_CONVERSION_TIMEOUT);
            }
            if (exitValue == 0) {
                log.info("success task {}", cmd);
                return;
            } else {
                log.info("Task failed for cmd: {} with exitValue={}", commandLine, exitValue);
                throw new IotWechatException(byteArrayOutputStream2String(errorStream, "UTF-8", ""),
                        IotWechatExceptionCode.VOICE_CONVERSION_FAIL);
            }
        } catch (ExecuteException e) {
            log.error("exception when executing {}", commandLine, e);
            throw new IotWechatException(byteArrayOutputStream2String(errorStream, "UTF-8",
                    ""), IotWechatExceptionCode.VOICE_CONVERSION_FAIL);

        } catch (IOException e) {
            log.error("IO exception when executing {}.", commandLine, e);
            throw new IotWechatException("IO Exception", IotWechatExceptionCode.VOICE_CONVERSION_FAIL);
        }
    }

    private static String byteArrayOutputStream2String(
            ByteArrayOutputStream stream, String charset, String defaultMessage) {

        if (stream == null || stream.size() <= 0) {
            return defaultMessage;
        }

        String result = null;
        try {
            result = stream.toString(charset);
        } catch (UnsupportedEncodingException e) {
            log.error("convert error stream encoding error.", e);
            result = defaultMessage;
        }
        return result;
    }
}
