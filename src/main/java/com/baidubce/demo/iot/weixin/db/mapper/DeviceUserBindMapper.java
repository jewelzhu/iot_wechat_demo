package com.baidubce.demo.iot.weixin.db.mapper;

import com.baidubce.demo.iot.weixin.model.db.DeviceUserBindDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

public interface DeviceUserBindMapper {
        @Insert("INSERT INTO device_user_bind(wechat_user_id, device_id, bind_time) VALUES (#{wechat_user_id}, #{device_id}, #{bind_time})")
        void insertBind(@Param("wechat_user_id") String wechatUserId,
                        @Param("device_id") String deviceId,
                        @Param("bind_time") Date bindTime);

        @Select("SELECT * FROM device_user_bind WHERE device_id=#{device_id}")
        @Results({
                @Result(property = "wechatUserId", column = "wechat_user_id"),
                @Result(property = "deviceId", column = "device_id"),
                @Result(property = "bindTime", column = "bind_time")
        })
        List<DeviceUserBindDto> getBindUsersOfDevice(@Param("device_id") String deviceId);

        @Select("SELECT * FROM device_user_bind WHERE wechat_user_id=#{wechat_user_id}")
        @Results({
                @Result(property = "wechatUserId", column = "wechat_user_id"),
                @Result(property = "deviceId", column = "device_id"),
                @Result(property = "bindTime", column = "bind_time")
        })
        DeviceUserBindDto getDeviceByBindUser(@Param("wechat_user_id") String wechatUserId);

        @Delete("DELETE FROM device_user_bind WHERE wechat_user_id=#{wechat_user_id}")
        void deleteBind(@Param("wechat_user_id") String wechatUserId);

}
