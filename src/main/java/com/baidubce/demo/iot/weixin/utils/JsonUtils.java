package com.baidubce.demo.iot.weixin.utils;

import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Slf4j
public class JsonUtils {
  public static String toJson(Object obj) {
    return WxMpGsonBuilder.create().toJson(obj);
  }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
          return WxMpGsonBuilder.create().fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
          log.warn("invalid json {} of {}", json, classOfT);
          return null;
        }
    }
}
