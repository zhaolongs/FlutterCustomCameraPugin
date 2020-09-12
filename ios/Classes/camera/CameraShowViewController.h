//
//  CameraShowViewController.h
//  flutter_custom_camera_pugin
//
//  Created by  androidlongs on 2020/9/12.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface CameraShowViewController : UIViewController


@property(strong,nonatomic) NSString * imageUrl;

//页面标识 201 拍照进来的
// 202 相册进来的
@property(assign,nonatomic) NSInteger pageFlag;

@end

NS_ASSUME_NONNULL_END
