//
//  CameraConfigOption.m
//  flutter_custom_camera_pugin
//
//  Created by  androidlongs on 2020/9/12.
//

#import "CameraConfigOption.h"

@implementation CameraConfigOption

-(instancetype)initWithDict:(NSDictionary *)result{
    if (self=[super init]) {
        
        
        /*
         
         ///默认自定义相册是否显示 相册切换
         public boolean isShowPhotoAlbum = true;
         
         ///默认自定义相册是否显示 前后镜头切换
         public boolean isShowSelectCamera = true;
         
         ///默认自定义相册是否显示 闪光灯开关按钮
         public boolean isShowFlashButtonCamera = true;
         
         /// 是否预览照片 如拍照完成 或者相册选择完成
         public boolean isPreviewImage = true;
         
         /// 是否启动裁剪功能 如拍照完成 或者相册选择完成
         public boolean isCropImage = false;
         /// 是否显示提示
         public boolean isShowToast = true ;
         */
        
        _isShowPhotoAlbum = [[result objectForKey:@"isShowPhotoAlbum"] boolValue];
        _isShowSelectCamera =  [[result objectForKey:@"isShowSelectCamera"] boolValue];
        _isShowFlashButtonCamera = [[result objectForKey:@"isShowFlashButtonCamera"] boolValue];
        
        _isPreviewImage =  [[result objectForKey:@"isPreviewImage"] boolValue];
        _isCropImage =  [[result objectForKey:@"isCropImage"] boolValue];
        _isShowToast = [[result objectForKey:@"isShowToast"] boolValue]; 
        
        
    }
    return self;
}
+(instancetype)userWithDict:(NSDictionary *)dict{
    return [[self alloc] initWithDict:dict];
}
@end
