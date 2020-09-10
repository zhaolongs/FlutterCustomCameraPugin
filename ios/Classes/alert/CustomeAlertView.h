//
//  CustomeAlertView.h
//  GPUPhoto
//
//  Created by ios2chen on 2017/7/12.
//  Copyright © 2017年 Lfy. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CustomeAlertView : NSObject<NSCopying>

+(instancetype)shareView;
-(void)showCustomeAlertViewWithMessage:(NSString *)message;

@end
