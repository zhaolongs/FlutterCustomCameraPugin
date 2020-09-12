//
//  CameraViewController.h
//  CustomeCamera
//
//  Created by ios2chen on 2017/7/20.
//  Copyright © 2017年 ios2chen. All rights reserved.
//
#import <AVFoundation/AVFoundation.h>
#import "CustomeAlertView.h"
#import "CameraConfigOption.h"
#define UIScreen_Height [UIScreen mainScreen].bounds.size.height
#define UIScreen_Width [UIScreen mainScreen].bounds.size.Width
#import <UIKit/UIKit.h>

@interface CameraViewController : UIViewController

@property(strong,nonatomic) CameraConfigOption * cameraOptions;

//页面标识
//0 自定义 相机
//1 系统相机
//2 系统相册
// 3 一个弹框选择
@property(assign,nonatomic) NSInteger pageIndex ;
@end
