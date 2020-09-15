//
//  CameraConfigOption.h
//  flutter_custom_camera_pugin
//
//  Created by  androidlongs on 2020/9/12.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface CameraConfigOption : NSObject
///默认自定义相册是否显示 相册切换
@property(assign,nonatomic) BOOL isShowPhotoAlbum ;

///默认自定义相册是否显示 前后镜头切换
@property(assign,nonatomic) BOOL  isShowSelectCamera ;

///默认自定义相册是否显示 闪光灯开关按钮
@property(assign,nonatomic) BOOL isShowFlashButtonCamera;

/// 是否预览照片 如拍照完成 或者相册选择完成
@property(assign,nonatomic) BOOL  isPreviewImage ;

/// 是否启动裁剪功能 如拍照完成 或者相册选择完成
@property(assign,nonatomic) BOOL  isCropImage ;
/// 是否显示提示
@property(assign,nonatomic) BOOL isShowToast ;

@property(strong,nonatomic) NSArray *iconsList;
@property(strong,nonatomic) NSArray *imageAssetList;

-(instancetype)initWithDict:(NSDictionary *)result;
+(instancetype)userWithDict:(NSDictionary *)dict;
@end

NS_ASSUME_NONNULL_END
