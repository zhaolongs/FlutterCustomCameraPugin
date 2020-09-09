import 'package:flutter/services.dart';

import '../flutter_custom_camera_pugin.dart';
import 'bean/camera_result_info.dart';

/// 创建人： Created by zhaolong
/// 创建时间：Created by  on 2020/9/9.
///
/// 可关注公众号：我的大前端生涯   获取最新技术分享
/// 可关注网易云课堂：https://study.163.com/instructor/1021406098.htm
/// 可关注博客：https://blog.csdn.net/zl18603543572
class CommonCameraFunction {
  //https://blog.csdn.net/zl18603543572/article/details/96043692
  String recive = "";

  //创建 BasicMessageChannel
  // flutter_and_native_100 为通信标识
  // StandardMessageCodec() 为参数传递的 编码方式
  static const messageChannel = const BasicMessageChannel(
      'flutter_and_native_custom_100', StandardMessageCodec());

  //发送消息
  static Future<Map<String, dynamic>> sendMessage(Map arguments) async {
    ///返回的是 Map<dynamic,dynamic> 类型
    Map reply = await messageChannel.send(arguments);
    //解析 原生发给 Flutter 的参数
    int code = reply["code"];
    String message = reply["message"];
    Map<String, dynamic> map = Map.from(reply);
    return Future.value(map);
  }

  static Future<CameraResultInfo> test() async {
    final Map<String, dynamic> result = await sendMessage(
        {"method": "test", "ontent": "测试一下插件连接", "code": 400});
    return CameraResultInfo.fromMap(result);
  }

  ///打开相机
  static Future<CameraResultInfo> openCamera(
      {CameraConfigOptions cameraConfigOptions}) async {
    if (cameraConfigOptions == null) {
      cameraConfigOptions = new CameraConfigOptions();
    }
    Map<String, dynamic> options = new Map();
    options['isShowFlashButtonCamera'] =
        cameraConfigOptions.isShowFlashButtonCamera;
    options['isShowPhotoAlbum'] = cameraConfigOptions.isShowPhotoAlbum;
    options['isShowSelectCamera'] = cameraConfigOptions.isShowSelectCamera;

    //Flutter 向 Android iOS 中基本的发送消息方式
    final Map<String, dynamic> result = await sendMessage(
        {"method": "openCamera", "ontent": "打开自定义相机", "code": 100,"options":options});
    return CameraResultInfo.fromMap(result);
  }

  ///打开相册
  static Future<CameraResultInfo> openPhotoAlbum() async {
    final Map<String, dynamic> result = await sendMessage(
        {"method": "openPhotoAlbum", "ontent": "打开系统相册", "code": 102});

    return CameraResultInfo.fromMap(result);
  }
}
