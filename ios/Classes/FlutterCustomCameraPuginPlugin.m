#import "FlutterCustomCameraPuginPlugin.h"
#import "CameraViewController.h"
#import <AVFoundation/AVFoundation.h>
#import "CustomeAlertView.h"
#import "CameraConfigOption.h"
#import "LeePhotoOrAlbumImagePicker.h"

@interface FlutterCustomCameraPuginPlugin ()
@property (nonatomic, strong)  FlutterReply flutterReply ;
@end

@implementation FlutterCustomCameraPuginPlugin{
    FlutterBasicMessageChannel* messageChannel;
}


//注册插件的方法
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"flutter_custom_camera_pugin"
                                     binaryMessenger:[registrar messenger]];
    FlutterCustomCameraPuginPlugin* instance = [[FlutterCustomCameraPuginPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
    
    //自定义的消息通道
    //FlutterBasicMessageChannel 与Flutter 之间的双向通信
    [instance basicMessageChannelFunction:registrar];
    
    
}

// ios 原生通知消息 用于数据传递
- (void)execute:(NSNotification *)notification {
    //do something when received notification
    //notification.name is @"NOTIFICATION_NAME"
    if(notification.object){
        NSDictionary *nDict =notification.object;
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        
        NSInteger code =  [[nDict objectForKey:@"code"] intValue] ;
        
        if(code == 200){
            NSLog(@"发送成功消息");
            NSDictionary *dict = [NSDictionary dictionaryWithObject:nDict[@"filePath"] forKey:@"lImageUrl"];
            [dic setObject:dict forKey:@"data"];
            [dic setObject:@"原生  ios   返回给flutter的数据" forKey:@"message"];
            [dic setObject: [NSNumber numberWithInt:200] forKey:@"code"];
            //需要注意的是 这个方法只能回调一次
            self.flutterReply(dic);
        }else  if(code == 204){
            //自定义相机页面显示打开相册
            [self openCamer:1 andWithOptions:nil];
        }else{
            NSDictionary *dict = [NSDictionary dictionary];
            [dic setObject:dict forKey:@"data"];
            [dic setObject:@"相机拍照取消" forKey:@"message"];
            [dic setObject: [NSNumber numberWithInt:201] forKey:@"code"];
            //需要注意的是 这个方法只能回调一次
            self.flutterReply(dic);
        }
        
    }
}
- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"getPlatformVersion" isEqualToString:call.method]) {
        result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
    } else {
        result(FlutterMethodNotImplemented);
    }
}

-(void) basicMessageChannelFunction:(NSObject<FlutterPluginRegistrar>*)registrar {
    
    //    注册观察者：
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(execute:)
                                                 name:@"NOTIFICATION_NAME"
                                               object:nil];
    // 初始化定义
    // flutter_and_native_100 j
    // Flutter 调用 ios 在这里
    messageChannel = [FlutterBasicMessageChannel messageChannelWithName:@"flutter_and_native_custom_100" binaryMessenger:[registrar messenger]];
    
    __weak typeof(self) weakSelf = self;
    // 接收消息监听
    [messageChannel setMessageHandler:^(id message, FlutterReply callback) {
        weakSelf.flutterReply = callback ;
        NSString *method=message[@"method"];
        //相机配置参数
        NSDictionary *optionsDict=message[@"options"];
        if ([method isEqualToString:@"test"]) {
            NSLog(@"flutter 调用到了 ios test");
            NSMutableDictionary *dic = [NSMutableDictionary dictionary];
            [dic setObject:@"原生  ios   返回给flutter的数据" forKey:@"message"];
            [dic setObject: [NSNumber numberWithInt:200] forKey:@"code"];
            //需要注意的是 这个方法只能回调一次
            callback(dic);
            
        }else  if ([method isEqualToString:@"openCamera"]) {
            [weakSelf openCamer:0 andWithOptions:optionsDict];
        }else if ([method isEqualToString:@"openPhotoAlbum"]) {
            [weakSelf openCamer:1 andWithOptions:optionsDict];
        }else if ([method isEqualToString:@"openSystemCamera"]) {
            
            [weakSelf openCamer:2 andWithOptions:optionsDict];
        }else if ([method isEqualToString:@"openSystemAlert"]) {
            
            [weakSelf openCamer:3 andWithOptions:optionsDict];
        }
    }];
    
    
    
    
}

-(void)openCamer:(NSInteger) flag andWithOptions:(NSDictionary*) optionsDict{
    CameraConfigOption * cameraOptions = [[CameraConfigOption alloc]initWithDict:optionsDict];
    
    NSLog(@"flutter 调用到了 ios openCamera 打开相机 ");
    
    // 自定义相机页面
    CameraViewController *cameraVC = [[CameraViewController alloc]initWithNibName:@"CameraViewController" bundle:nil];
    cameraVC.view.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0];
    //配置参数
    cameraVC.cameraOptions =cameraOptions;
    //页面标识
    cameraVC.pageIndex = flag;
    UINavigationController *navController =   [[UINavigationController alloc] init];
    [navController pushViewController:cameraVC animated:YES];
    //全屏
    navController.modalPresentationStyle = UIModalPresentationCustom;
    navController.navigationBar.hidden = YES;
    //安全隐藏
    [UIApplication.sharedApplication.delegate.window.rootViewController dismissViewControllerAnimated:nil completion:^{
        
    }];
    [UIApplication.sharedApplication.delegate.window.rootViewController presentViewController:navController animated:YES completion:nil];
}
@end
