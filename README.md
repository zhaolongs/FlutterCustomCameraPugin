

**题记**
  ——  执剑天涯，从你的点滴积累开始，所及之处，必精益求精。
  
  |[**github?**](https://github.com/zhaolongs) |[测试源码在这里](https://github.com/zhaolongs/FlutterCustomCameraPugin/blob/master/example/lib/main.dart)   |  [百度同步](https://baijiahao.baidu.com/builder/preview/s?id=1676587101499079482) |
|--|--|--|
| [CSDN](https://biglead.blog.csdn.net/)| [网易云课堂教程](https://study.163.com/instructor/1021406098.htm)  | [掘金](https://juejin.im/user/712139263459176)| [EDU学院教程](https://edu.csdn.net/lecturer/1555)  |
| [知乎](https://www.zhihu.com/people/zhao-long-90-89/posts)| [Flutter系列文章 ](https://blog.csdn.net/zl18603543572/article/details/93532582)  |[头条同步](https://www.toutiao.com/i6867301274614759948/)  
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200909184228606.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3psMTg2MDM1NDM1NzI=,size_16,color_FFFFFF,t_70#pic_center)

实际项目开发中，谷歌官方推荐的几个相机插件总是满足不了需要，更令人不舒服的是在华为某些高版本系列的手机中使用系统相机拍照或者是选择照片会让应用闪退。

小编也分析了原因，在这些手机拍照的照片过大，在手机相机拍完的那一瞬间，手机系统还没有处理完照片，然后应用就去读取这个照片，导致系统异常崩溃。

至本插件编写的 0.0.1 版本时，各相机应用插件都未修复其兼容性，以小编的性格，那就是再造个轮子，于是乎 本插件就诞生了。

> 0.0.1 版本 只支持 Android （已发布 2020-09-09）
> 0.0.3 版本 同时支持 iOS  (已发布 2020-09-12)
> 0.0.5 版本 自定义相机 同时支持 iOS  (已发布 2020-09-15)

> 0.0.5 版本的功能修复  
>    iOS平台的图片资源策略修改

***
本插件实现的最终目标​：

* 1、调用原生默认的自定义相机

* 2、调用原生的系统相机、相册选择图片

* 3、拍照或者选择照片后 调用 系统裁剪（自定义）功能

* 4、在 Flutter Widget 中嵌入 自定义相机，允许使用 Widget 开发的页面布局来操作相机
****
如下图是默认情况下自定义相机的页面效果：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200909175911101.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3psMTg2MDM1NDM1NzI=,size_16,color_FFFFFF,t_70#pic_center)

***

#### 1 添加依赖
小编以将这个自定义相机封装成一个插件组件，直接使用flutter_custom_camera_pugin插件就可使用，一般小编的作风就是提供两种集成方式如下：

通过**pub仓库**添加依赖，代码如下：[最新版本查看这里](https://pub.flutter-io.cn/packages/flutter_custom_camera_pugin)

```java
  dependencies:
	 flutter_custom_camera_pugin: ^0.0.1
```

或者是通过 **github** [点击查看github](https://github.com/zhaolongs/FlutterCustomCameraPugin.git)方式添加依赖，代码如下：

```java
dependencies:
	shake_animation_widget:
	      git:
	        url: https://github.com/zhaolongs/FlutterCustomCameraPugin.git
	        ref: master
```

然后加载依赖，代码如下：

```java
flutter pub get
```

然后在使用的地方导包，代码如下：

```java
import 'package:flutter_custom_camera_pugin/flutter_custom_camera_pugin.dart';
```


#### 2 打开自定义相机 拍照
你可以使用 FlutterCustomCameraPugin 的 openCamera 方法来打开自定义相机拍照页面，其中 可选参数 cameraConfigOptions 用来配置自定义相机页面的的按钮是否显示，如下代码清单 2-1：
```java
  ///代码清单 2-1 
  ///打开相机
  void openCamera() async {
    CameraConfigOptions options = new CameraConfigOptions();

    ///默认自定义相册是否显示 相册切换
    options.isShowSelectCamera = true;

    ///默认自定义相册是否显示 前后镜头切换
    options.isShowPhotoAlbum = true;

    ///默认自定义相册是否显示 闪光灯开关按钮
    options.isShowFlashButtonCamera = true;

    ///调起自定义相机
    ///拍照的返回结果
    CameraResultInfo resultInfo =
        await FlutterCustomCameraPugin.openCamera(cameraConfigOptions: options);

    if (resultInfo.code == 200) {
      imageFile = new File(resultInfo.data["lImageUrl"]);
    }else if (resultInfo.code == 201) {
     ///201 是拍照取消 如点击了关闭按钮 
     ///或者是 Android 手机的后退按钮
    }
    setState(() {});
  }

```

CameraConfigOptions 用来配置相机参数，如下所示：

```java
/// 相册配置使用参数
class CameraConfigOptions {

  ///0.0.1 版本 
  ///默认自定义相册是否显示 相册切换
  bool isShowPhotoAlbum = true;

  ///默认自定义相册是否显示 前后镜头切换
  bool isShowSelectCamera = true;

  ///默认自定义相册是否显示 闪光灯开关按钮
  bool isShowFlashButtonCamera = true;

}
```
CameraResultInfo 是拍照或者相册选择结果封闭，拍照成功、拍照取消、相册选择成功、相册选择失败均会回调：

```java
class CameraResultInfo {
  ///消息标识 
  int code;
  ///回调的消息
  String message ='';
  ///回调的数据
  dynamic data ;
  ///回调的方法名
  String method ='';
}
```

#### 3 打开 相册选择照片

```java
  ///打开相册
  void openPhotoAlbum() async {
    /// 相册的选择返回结果
    /// 选择成功与取消都会回调
    CameraResultInfo resultInfo =await FlutterCustomCameraPugin.openPhotoAlbum();
    if (resultInfo.code == 200) {
      imageFile = new File(resultInfo.data["lImageUrl"]);
    }
  }
```

#### 4 打开一个弹框选择

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200912221916959.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3psMTg2MDM1NDM1NzI=,size_16,color_FFFFFF,t_70#pic_center)

```java
 void openSystemAlert() async {
   /// 相册的选择返回结果
   /// 选择成功与取消都会回调
   CameraResultInfo resultInfo =await FlutterCustomCameraPugin.openSystemAlert();
   
   if (resultInfo.code == 200) {
     imageFile = new File(resultInfo.data["lImageUrl"]);
   }

   setState(() {});
 }
```

本公众号会首发系列专题文章，付费的视频课程会在公众号中免费刊登，在你上下班的路上或者是睡觉前的一刻，本公众号都是你浏览知识干货的一个小选择，收藏不如行动，在那一刻，公众号会提示你该学习了。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200914205134479.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3psMTg2MDM1NDM1NzI=,size_16,color_FFFFFF,t_70#pic_center)
