import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

/// 创建人： Created by zhaolong
/// 创建时间：Created by  on 2020/9/9.
///
/// 可关注公众号：我的大前端生涯   获取最新技术分享
/// 可关注网易云课堂：https://study.163.com/instructor/1021406098.htm
/// 可关注博客：https://blog.csdn.net/zl18603543572
///
///
/// 相册配置使用参数
class CameraConfigOptions {

  ///0.0.1 版本
  ///默认自定义相册是否显示 相册切换
  bool isShowPhotoAlbum = true;

  ///默认自定义相册是否显示 前后镜头切换
  bool isShowSelectCamera = true;

  ///默认自定义相册是否显示 闪光灯开关按钮
  bool isShowFlashButtonCamera = true;

  /// 是否预览照片 如拍照完成 或者相册选择完成
  bool isPreviewImage = true;

  /// 是否启动裁剪功能 如拍照完成 或者相册选择完成
  bool isCropImage = false;

  List<String> iconsList =[
    "icons/camera_record_icon.png",
    "icons/camera_flash_open_icon.png",
    "icons/camera_record_finish.png",
    "icons/camera_record_icon.png",
    "icons/camera_select_photo_icon.png",
    "icons/camera_white_back_icon.png"
  ];
}