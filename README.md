
本demo中的微信公众号对接功能基于https://github.com/binarywang/weixin-java-mp-demo-springboot.git开发.

本demo中的语音转换功能依赖ffmpeg，请确保部署本服务的server上已安装ffmpeg.

本demo实现了什么？
用户可以微信公众号内扫码绑定一个设备，然后通过微信和iot设备进行语音对话。

iot设备端sdk: https://github.com/jewelzhu/iot-edge-c-sdk/blob/master/iothub_client/src/iot_wechat_client.c

微信到设备:
微信客户端->微信服务器接受并生成url->本demo server->百度云物管理api->iot设备获取url下载播放

设备到微信：
iot设备采集语音然后分段mqtt上传->推送kafka->本demo server从kafka收集并聚合语音信息->上传语音到微信服务器->通知微信服务器发送语音给客户端->客户端收到语音
