import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_custom_camera_pugin/flutter_custom_camera_pugin.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  CameraResultInfo _cameraResultInfo;

  @override
  void initState() {
    super.initState();
  }

  Future<void> testPlugin() async {
    CameraResultInfo platformVersion;

    try {
      platformVersion = await FlutterCustomCameraPugin.testPugin();
    } on PlatformException {
      platformVersion = CameraResultInfo.fromError();
    }
    if (!mounted) return;

    imageFile = File("/var/mobile/Containers/Data/Application/395710CE-46E3-47EB-9D36-4C2697F91C83/Documents/1599919466them.png");
    setState(() {
      _cameraResultInfo = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text(' Flutter 自定义相机插件 '),
        ),
        body:SingleChildScrollView(child:  Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Text(
                'Running on: ${_cameraResultInfo != null ? _cameraResultInfo.toString() : ''}\n'),

//            Image.asset("icons/camera_record_icon.png",package: "flutter_custom_camera_pugin",),
            OutlineButton(
              child: Text('测试下'),
              onPressed: () {
                testPlugin();
              },
            ),
            OutlineButton(
              child: Text('清除下数据'),
              onPressed: () {
                setState(() {
                  _cameraResultInfo = null;
                  imageFile = null;
                });
              },
            ),
            OutlineButton(
              child: Text('打开自定义相机'),
              onPressed: () {
                openCamera();
              },
            ),
            OutlineButton(
              child: Text('打开相册'),
              onPressed: () {
                openPhotoAlbum();
              },
            ),

            OutlineButton(
              child: Text('打开系统相机'),
              onPressed: () {
                openSystemCamera();
              },
            ),
            OutlineButton(
              child: Text('打开系统相册'),
              onPressed: () {
                openSystemPhotoAlbum();
              },
            ),
            OutlineButton(
              child: Text('打开系统相机相册选择弹框'),
              onPressed: () {
                openSystemAlert();
              },
            ),

            imageFile == null
                ? Text("图片预览区域")
                :  Image.file(
              imageFile,
              width: 375,
              fit: BoxFit.fitWidth,
            ),
          ],
        ),),
      ),
    );
  }

  File imageFile;

  ///打开相机
  ///如 你点击一个按钮
  void openCamera() async {

    ///相机使用配置参数
    CameraConfigOptions options = new CameraConfigOptions();

    ///默认自定义相册是否显示 相册切换
    options.isShowSelectCamera = true;

    ///默认自定义相册是否显示 前后镜头切换
    options.isShowPhotoAlbum = true;

    ///默认自定义相册是否显示 闪光灯开关按钮
    options.isShowFlashButtonCamera = true;
    /// 是否预览照片 如拍照完成 或者相册选择完成 默认为 true 预览
    options.isPreviewImage = false;
    ///调起自定义相机
    ///拍照的返回结果
    CameraResultInfo resultInfo =
        await FlutterCustomCameraPugin.openCamera(cameraConfigOptions: options);

    if (resultInfo.code == 200) {
      imageFile = new File(resultInfo.data["lImageUrl"]);
    } else if (resultInfo.code == 201) {
      ///201 是拍照取消 如点击了关闭按钮
      ///或者是 Android 手机的后退按钮
    }

    _cameraResultInfo = resultInfo;

    setState(() {});
  }

  ///考虑自定义一下 开发中
  void openCustomCamera() async {}

  ///打开相册
  void openPhotoAlbum() async {
    /// 相册的选择返回结果
    /// 选择成功与取消都会回调
    CameraResultInfo resultInfo =await FlutterCustomCameraPugin.openPhotoAlbum();
    if (resultInfo.code == 200) {
      imageFile = new File(resultInfo.data["lImageUrl"]);
    }

    _cameraResultInfo =resultInfo ;
    setState(() {});
  }

  void openSystemCamera() async{
    ///相机使用配置参数
    CameraConfigOptions options = new CameraConfigOptions();
    options.isPreviewImage = false;
    CameraResultInfo resultInfo =
        await FlutterCustomCameraPugin.openSystemCamera(cameraConfigOptions: options);

    if (resultInfo.code == 200) {
      String imageUrl =resultInfo.data["lImageUrl"];
      print("openSystemCamera "+imageUrl.toString());
      imageFile = new File(imageUrl);
    } else if (resultInfo.code == 201) {
      ///201 是拍照取消 如点击了关闭按钮
      ///或者是 Android 手机的后退按钮
    }

    setState(() {});
  }

  void openSystemPhotoAlbum() async {
    /// 相册的选择返回结果
    /// 选择成功与取消都会回调
    CameraResultInfo resultInfo =await FlutterCustomCameraPugin.openSystemPhotoAlbum();
    if (resultInfo.code == 200) {
      imageFile = new File(resultInfo.data["lImageUrl"]);
    }

    _cameraResultInfo =resultInfo ;
    setState(() {});
  }

  void openSystemAlert() async {
    /// 相册的选择返回结果
    /// 选择成功与取消都会回调
    CameraResultInfo resultInfo =await FlutterCustomCameraPugin.openSystemAlert();

    if (resultInfo.code == 200) {
      imageFile = new File(resultInfo.data["lImageUrl"]);
    }


    _cameraResultInfo =resultInfo ;
    setState(() {});
  }
}
