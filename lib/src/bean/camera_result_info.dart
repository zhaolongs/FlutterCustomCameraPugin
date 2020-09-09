import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

/// 创建人： Created by zhaolong
/// 创建时间：Created by  on 2020/9/9.
///
/// 可关注公众号：我的大前端生涯   获取最新技术分享
/// 可关注网易云课堂：https://study.163.com/instructor/1021406098.htm
/// 可关注博客：https://blog.csdn.net/zl18603543572
class CameraResultInfo {

  int code;
  ///回调的消息
  String message ='';
  ///回调的数据
  dynamic data ;
  ///回调的方法名
  String method ='';

  static CameraResultInfo fromMap(Map<String,dynamic> map){
    CameraResultInfo info = new CameraResultInfo();
    info.code= map["code"];
    info.message=map['message'];
    info.data=map['data'];
    return info;
  }

  static CameraResultInfo fromError({int errCode=500,String messag="插件错误"}){
    CameraResultInfo info = new CameraResultInfo();
    info.code= errCode;
    info.message=messag;
    return info;
  }

  @override
  String toString() {
    return 'CameraResultInfo{code: $code, message: $message}';
  }
}
