//
//  CustomeAlertView.m
//  GPUPhoto
//
//  Created by ios2chen on 2017/7/12.
//  Copyright © 2017年 Lfy. All rights reserved.
//

#import "CustomeAlertView.h"
#import <UIKit/UIKit.h>

@interface CustomeAlertView ()

@property (nonatomic, strong) UIView *clearBackGroundView;

@end
@implementation CustomeAlertView


static CustomeAlertView *alertView = nil;

+(instancetype)shareView{
    
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        alertView = [[CustomeAlertView alloc]init];
        
    });
    
    return alertView;
}

+(instancetype)allocWithZone:(struct _NSZone *)zone{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        alertView = [super allocWithZone:zone];
    });
    return alertView;
}

-(id)copyWithZone:(NSZone *)zone{
    return alertView;
}

-(void)showCustomeAlertViewWithMessage:(NSString *)message{
    
    
    float maxWidth = [UIScreen mainScreen].bounds.size.width-100;
    
    self.clearBackGroundView = [[UIView alloc]initWithFrame:CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, [UIScreen mainScreen].bounds.size.height)];
    self.clearBackGroundView.backgroundColor = [UIColor clearColor];
    UIWindow *window = [UIApplication sharedApplication].keyWindow;
    [window addSubview:self.clearBackGroundView];
    
    UIView *middleAlphaView = [[UIView alloc]init];
    middleAlphaView.backgroundColor = [UIColor blackColor];
    middleAlphaView.alpha = 0.7;
    middleAlphaView.layer.masksToBounds = YES;
    middleAlphaView.layer.cornerRadius = 10;
    [self.clearBackGroundView addSubview:middleAlphaView];
    
    UILabel *messageLabel = [[UILabel alloc]init];
    messageLabel.textColor = [UIColor whiteColor];
    messageLabel.textAlignment = NSTextAlignmentCenter;
    messageLabel.font = [UIFont systemFontOfSize:16];
    messageLabel.numberOfLines = 0;
    
    
    
    CGSize labelSize = [message boundingRectWithSize:CGSizeMake(MAXFLOAT, 25) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:16]} context:nil].size;
    
    CGSize labelHeightSize = [message boundingRectWithSize:CGSizeMake(maxWidth, MAXFLOAT) options:NSStringDrawingUsesLineFragmentOrigin attributes:@{NSFontAttributeName:[UIFont systemFontOfSize:16]} context:nil].size;
    
    if (labelSize.width<=maxWidth) {
        middleAlphaView.frame = CGRectMake(([UIScreen mainScreen].bounds.size.width-labelSize.width-40)/2, ([UIScreen mainScreen].bounds.size.height-45)/2, labelSize.width+40, 45);
        messageLabel.frame = CGRectMake(middleAlphaView.frame.origin.x+20, middleAlphaView.frame.origin.y+10, labelSize.width, 25);
        
    } else{
        middleAlphaView.frame = CGRectMake(30, ([UIScreen mainScreen].bounds.size.height-labelHeightSize.height-20)/2, [UIScreen mainScreen].bounds.size.width-60, labelHeightSize.height+20);
        messageLabel.frame = CGRectMake(middleAlphaView.frame.origin.x+20, middleAlphaView.frame.origin.y+10, maxWidth, labelHeightSize.height);
        
    }
    
    
    messageLabel.text = message;
    [self.clearBackGroundView addSubview:messageLabel];
    
    [self performSelector:@selector(removeAlertView) withObject:nil afterDelay:1];
    
   
    
}

-(void)removeAlertView{
    [self.clearBackGroundView removeFromSuperview];
    self.clearBackGroundView = nil;
    
}

@end
