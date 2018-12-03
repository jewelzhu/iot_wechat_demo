#!/bin/sh
APPLICATION=../lib/weixin-java-mp-demo-springboot-1.0.0-SNAPSHOT.jar
SPRING_CONFIG_FILE=../conf/application.yml
MAX_MEMORY=2048M
MAX_PERM_MEMORY=512M

#please mark sure ffmpeg has been installed

nohup java -Dspring.config.location=$SPRING_CONFIG_FILE -Dfile.encoding=UTF-8 \
-Xmx$MAX_MEMORY -XX:MaxPermSize=$MAX_PERM_MEMORY -XX:+UseConcMarkSweepGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps \
-Xloggc:gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=20M -jar $APPLICATION > ../log/log 2>&1 &