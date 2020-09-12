//
//  CameraUtils.h
//  flutter_custom_camera_pugin
//
//  Created by  androidlongs on 2020/9/12.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface CameraUtils : NSObject


+(void) nextWithImageUrl:(NSString*)imageUrl;
+(void) cancle:(NSInteger) flag;

@end

NS_ASSUME_NONNULL_END
