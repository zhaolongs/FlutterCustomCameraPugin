import 'dart:async';
import 'package:flutter_custom_camera_pugin/src/bean/camera_config_options.dart';
import 'package:flutter_custom_camera_pugin/src/bean/camera_result_info.dart';
import 'package:flutter_custom_camera_pugin/src/common_camera_function.dart';

export 'package:flutter_custom_camera_pugin/src/bean/camera_result_info.dart';
export 'package:flutter_custom_camera_pugin/src/bean/camera_config_options.dart';
class FlutterCustomCameraPugin {

  ///测试插件连接
  static Future<CameraResultInfo> testPugin() async{
    final CameraResultInfo result = await CommonCameraFunction.test();
    return result ;
  }

  ///打开相机
  static Future<CameraResultInfo> openCamera({CameraConfigOptions cameraConfigOptions}) async{
    //Flutter 向 Android iOS 中基本的发送消息方式
    final CameraResultInfo result = await CommonCameraFunction.openCamera(cameraConfigOptions: cameraConfigOptions);
    return result ;
  }

  ///打开相册
  static Future<CameraResultInfo> openPhotoAlbum() async {
    final CameraResultInfo result = await CommonCameraFunction.openPhotoAlbum();
    return result ;
  }
}
