//
//  CameraUtils.m
//  flutter_custom_camera_pugin
//
//  Created by  androidlongs on 2020/9/12.
//

#import "CameraUtils.h"

@implementation CameraUtils

+(void)nextWithImageUrl:(NSString *)imageUrl{
    NSMutableDictionary *dict = [[NSMutableDictionary alloc]init];
    [dict setObject:imageUrl forKey:@"filePath"];
    [dict setObject:[NSNumber numberWithInt:200] forKey:@"code"];
    //发送消息
    NSNotification *notification = [NSNotification notificationWithName:@"NOTIFICATION_NAME"
                                                                 object:dict];
    [[NSNotificationCenter defaultCenter] postNotification:notification];
    [UIApplication.sharedApplication.delegate.window.rootViewController dismissViewControllerAnimated:NO completion:^{
    }];
}

+(void)cancle:(NSInteger)flag{
    
}
@end
