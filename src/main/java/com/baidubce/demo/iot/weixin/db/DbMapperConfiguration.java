package com.baidubce.demo.iot.weixin.db;

import com.baidubce.demo.iot.weixin.db.mapper.DeviceUserBindMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackageClasses = DeviceUserBindMapper.class)
public class DbMapperConfiguration {
}
