#import "FlutterCustomCameraPuginPlugin.h"
//#import "CameraViewController.h"
#import "TestViewController.h"

@implementation FlutterCustomCameraPuginPlugin{
    FlutterBasicMessageChannel* messageChannel;
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"flutter_custom_camera_pugin"
                                     binaryMessenger:[registrar messenger]];
    FlutterCustomCameraPuginPlugin* instance = [[FlutterCustomCameraPuginPlugin alloc] init];
    [registrar addMethodCallDelegate:instance channel:channel];
    
    //FlutterBasicMessageChannel 与Flutter 之间的双向通信
    [instance basicMessageChannelFunction:registrar];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"getPlatformVersion" isEqualToString:call.method]) {
        result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
    } else {
        result(FlutterMethodNotImplemented);
    }
}

-(void) basicMessageChannelFunction:(NSObject<FlutterPluginRegistrar>*)registrar {
    
    // 初始化定义
    // flutter_and_native_100 j
    messageChannel = [FlutterBasicMessageChannel messageChannelWithName:@"flutter_and_native_custom_100" binaryMessenger:[registrar messenger]];
    
    
    // 接收消息监听
    [messageChannel setMessageHandler:^(id message, FlutterReply callback) {
        
        NSString *method=message[@"method"];
        if ([method isEqualToString:@"test"]) {
            
            NSLog(@"flutter 调用到了 ios test");
            NSMutableDictionary *dic = [NSMutableDictionary dictionary];
            
            [dic setObject:@"原生  ios   返回给flutter的数据" forKey:@"message"];
            [dic setObject: [NSNumber numberWithInt:200] forKey:@"code"];
            
            //需要注意的是 这个方法只能回调一次
            callback(dic);
            
        }else  if ([method isEqualToString:@"openCamera"]) {
            NSLog(@"flutter 调用到了 ios openCamera 打开相机 ");
//            CameraViewController *cameraVC = [[CameraViewController alloc]init];
//            cameraVC.view.backgroundColor = UIColor.redColor;
//            cameraVC.modalPresentationStyle = UIModalPresentationCustom;
//            [UIApplication.sharedApplication.delegate.window.rootViewController presentViewController:cameraVC animated:YES completion:nil];
            
            
            
            
            
            
            // 创建一个Button对象，根据类型来创建button
            // 圆角类型button:UIButtonTypeRoundedRect
            // 通过类方法来创建buttonWithType: 类名 + 方法名，不能通过alloc init方式创建
            UIButton* button = [UIButton buttonWithType:UIButtonTypeRoundedRect];
            button.frame = CGRectMake(100, 100, 100, 40);

            // 按钮的正常状态
            [button setTitle:@"点击" forState:UIControlStateNormal];

            // 按钮的按下状态
            [button setTitle:@"按下" forState:UIControlStateHighlighted];

            // 设置按钮的背景色
            button.backgroundColor = [UIColor redColor];

            // 设置正常状态下按钮文字的颜色，如果不写其他状态，默认都是用这个文字的颜色
            [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];

            // 设置按下状态文字的颜色
            [button setTitleColor:[UIColor grayColor] forState:UIControlStateHighlighted];

            // 设置按钮的风格颜色,只有titleColor没有设置的时候才有用
            [button setTintColor:[UIColor whiteColor]];

            // titleLabel：UILabel控件
            button.titleLabel.font = [UIFont systemFontOfSize:25];
            
            NSBundle *bundle = [NSBundle bundleForClass:[FlutterCustomCameraPuginPlugin class]];
            NSString *path =  bundle.resourcePath;
            NSLog(@"path  = %@",path);
            
            UIImage *image2 = [UIImage imageNamed:@"camera_record_icon.png"
                                            inBundle:[NSBundle bundleForClass:[self class]]
                       compatibleWithTraitCollection:nil];

    
//            NSString *bundlePath = [[NSBundle bundleForClass:[self class]].resourcePath
//                                        stringByAppendingPathComponent:@"/resource.bundle"];
            
            // 这里不使用mainBundle是为了适配pod 1.x和0.x
            NSURL *associateBundleURL = [[NSBundle mainBundle] URLForResource:@"resource" withExtension:@"bundle"];
            NSBundle *bundle33 = [NSBundle bundleWithURL:associateBundleURL];
            
            NSBundle * currentbundle = [NSBundle bundleForClass:[FlutterCustomCameraPuginPlugin class]];
            
            NSBundle * bundle2 = [NSBundle bundleWithPath:[currentbundle pathForResource:@"resource" ofType:@"bundle"]];

           UIImage * testImage = [[UIImage imageWithContentsOfFile:[bundle2 pathForResource:@"camera_record_finish" ofType:@"png"]] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];

        
            
//            NSLog(@"bundlePath  = %@",bundlePath);
//            UIImage *image = [UIImage imageNamed:@"camera_record_icon"
//                                            inBundle:resource_bundle
//                       compatibleWithTraitCollection:nil];

          
            
            
            //图片的显示是需要载体的；需要放在UIImageView；
            UIImageView *imgView = [[UIImageView alloc]init];
            //图片显示在屏幕上的大小是由载体控制的；
            //现在把载体的大小设置成图片的大小，使用图片的大小设置UIImageView的长宽；
            imgView.frame = CGRectMake(0, 0, 375, 667);
            imgView.backgroundColor = [UIColor yellowColor];
            [imgView setImage:testImage];
            
             [UIApplication.sharedApplication.delegate.window.rootViewController.view addSubview:imgView];
            
            
            [UIApplication.sharedApplication.delegate.window.rootViewController.view addSubview:button];
            
            TestViewController *co = [[TestViewController alloc]initWithNibName:@"TestViewController" bundle:[NSBundle mainBundle]];
//             [UIApplication.sharedApplication.delegate.window.rootViewController presentViewController:co animated:YES completion:nil];
            
//            UIView *view = [[[NSBundle mainBundle] loadNibNamed:@"TestViewController" owner:UIApplication.sharedApplication.delegate.window.rootViewController options:nil]lastObject];
//
//           [UIApplication.sharedApplication.delegate.window.rootViewController.view addSubview:view];
             
        }else if ([method isEqualToString:@"test3"]) {
        
    }
     }];
    
}

@end
