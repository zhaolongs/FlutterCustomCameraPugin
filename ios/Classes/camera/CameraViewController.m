//
//  CameraViewController.m
//  CustomeCamera
//
//  Created by ios2chen on 2017/7/20.
//  Copyright © 2017年 ios2chen. All rights reserved.
//

#import "CameraViewController.h"
#import <AssetsLibrary/AssetsLibrary.h>

@interface CameraViewController ()<AVCaptureFileOutputRecordingDelegate>

/*
 *  AVCaptureSession:它从物理设备得到数据流（比如摄像头和麦克风），输出到一个或
 *  多个目的地，它可以通过
 *  会话预设值(session preset)，来控制捕捉数据的格式和质量
 */
@property (nonatomic, strong) AVCaptureSession *iSession;
//设备
@property (nonatomic, strong) AVCaptureDevice *iDevice;
//输入
@property (nonatomic, strong) AVCaptureDeviceInput *iInput;

//照片输出
@property (nonatomic, strong) AVCaptureStillImageOutput *iOutput;
//视频输出
@property (nonatomic, strong) AVCaptureMovieFileOutput *iMovieOutput;
//预览层
@property (nonatomic, strong) AVCaptureVideoPreviewLayer *iPreviewLayer;
//小圈圈
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *saveIndicatorView;

//拍照
@property (weak, nonatomic) IBOutlet UIButton *takePhotoBtn;
@property (weak, nonatomic) IBOutlet UIButton *videoBtn;

@end

@implementation CameraViewController

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    if (self.iSession) {
        [self.iSession startRunning];
    }
}

-(void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    if (self.iSession) {
        [self.iSession stopRunning];
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.saveIndicatorView.hidden = YES;
    self.iSession = [[AVCaptureSession alloc]init];
    self.iSession.sessionPreset = AVCaptureSessionPresetHigh;
    
    NSArray *deviceArray = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
    for (AVCaptureDevice *device in deviceArray) {
        if (device.position == AVCaptureDevicePositionBack) {
            self.iDevice = device;
        }
    }
    
    //添加摄像头设备
    //对设备进行设置时需上锁，设置完再打开锁
    [self.iDevice lockForConfiguration:nil];
    if ([self.iDevice isFlashModeSupported:AVCaptureFlashModeAuto]) {
        [self.iDevice setFlashMode:AVCaptureFlashModeAuto];
    }
    if ([self.iDevice isFocusModeSupported:AVCaptureFocusModeAutoFocus]) {
        [self.iDevice setFocusMode:AVCaptureFocusModeAutoFocus];
    }
    if ([self.iDevice isWhiteBalanceModeSupported:AVCaptureWhiteBalanceModeAutoWhiteBalance]) {
        [self.iDevice setWhiteBalanceMode:AVCaptureWhiteBalanceModeAutoWhiteBalance];
    }
    [self.iDevice unlockForConfiguration];
    
    //添加音频设备
    AVCaptureDevice *audioDevice = [[AVCaptureDevice devicesWithMediaType:AVMediaTypeAudio] firstObject];
    
    AVCaptureDeviceInput *audioInput = [AVCaptureDeviceInput deviceInputWithDevice:audioDevice error:nil];
    
    self.iInput = [[AVCaptureDeviceInput alloc]initWithDevice:self.iDevice error:nil];
    
    
    
    self.iOutput = [[AVCaptureStillImageOutput alloc]init];
    NSDictionary *setDic = [NSDictionary dictionaryWithObjectsAndKeys:AVVideoCodecJPEG,AVVideoCodecKey, nil];
    self.iOutput.outputSettings = setDic;
    
    self.iMovieOutput = [[AVCaptureMovieFileOutput alloc]init];
    
    if ([self.iSession canAddInput:self.iInput]) {
        [self.iSession addInput:self.iInput];
    }
    if ([self.iSession canAddOutput:self.iOutput]) {
        [self.iSession addOutput:self.iOutput];
    }
    if ([self.iSession canAddInput:audioInput]) {
        [self.iSession addInput:audioInput];
    }
    
    self.iPreviewLayer = [[AVCaptureVideoPreviewLayer alloc]initWithSession:self.iSession];
    [self.iPreviewLayer setVideoGravity:AVLayerVideoGravityResizeAspectFill];
    self.iPreviewLayer.frame = [UIScreen mainScreen].bounds;
    [self.view.layer insertSublayer:self.iPreviewLayer atIndex:0];
    
    [self.iSession startRunning];
    
}

#pragma mark - ButtonAction

- (IBAction)backAction:(id)sender {
    [self closeCameraFunction];
}

-(void) closeCameraFunction{
    [self dismissViewControllerAnimated:YES completion:^{
        NSDictionary *dict = [NSDictionary dictionaryWithObject:[NSNumber numberWithInt:201] forKey:@"code"];
        //发送消息
        NSNotification *notification = [NSNotification notificationWithName:@"NOTIFICATION_NAME"
                                                                     object:dict];
        [[NSNotificationCenter defaultCenter] postNotification:notification];
    }];
}

//闪光灯
- (IBAction)flashAction:(id)sender {
    
    NSArray *deviceArray = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
    AVCaptureDevice *newDevice;
    AVCaptureDeviceInput *newInput;
    
    
    if (self.iDevice.position == AVCaptureDevicePositionBack) {
        for (AVCaptureDevice *device in deviceArray) {
            if (device.position == AVCaptureDevicePositionFront) {
                newDevice = device;
            }
        }
    } else {
        for (AVCaptureDevice *device in deviceArray) {
            if (device.position == AVCaptureDevicePositionBack) {
                newDevice = device;
            }
        }
    }
    
    newInput = [AVCaptureDeviceInput deviceInputWithDevice:newDevice error:nil];
    if (newInput!=nil) {
        
        [self.iSession beginConfiguration];
        
        [self.iSession removeInput:self.iInput];
        if ([self.iSession canAddInput:newInput]) {
            [self.iSession addInput:newInput];
            self.iDevice = newDevice;
            self.iInput = newInput;
        } else{
            [self.iSession addInput:self.iInput];
        }
        
        [self.iSession commitConfiguration];
    }
    
}

//前后摄像头置换
- (IBAction)changePositionAction:(id)sender {
    [self.iDevice lockForConfiguration:nil];
    
    UIButton *flashButton = (UIButton *)sender;
    flashButton.selected = !flashButton.selected;
    if (flashButton.selected) {
        if ([self.iDevice isFlashModeSupported:AVCaptureFlashModeOn]) {
            [self.iDevice setFlashMode:AVCaptureFlashModeOn];
            [[CustomeAlertView shareView] showCustomeAlertViewWithMessage:@"闪光灯已开启"];
        }
    } else{
        if ([self.iDevice isFlashModeSupported:AVCaptureFlashModeOff]) {
            [self.iDevice setFlashMode:AVCaptureFlashModeOff];
            [[CustomeAlertView shareView] showCustomeAlertViewWithMessage:@"闪光灯已关闭"];
        }
    }
    
    [self.iDevice unlockForConfiguration];
    
}
- (IBAction)takePhotoAction:(id)sender {
    
    if ([self.takePhotoBtn.titleLabel.text isEqualToString:@"拍照"]) {
        AVCaptureConnection *connection = [self.iOutput connectionWithMediaType:AVMediaTypeVideo];
        if (!connection) {
            [[CustomeAlertView shareView] showCustomeAlertViewWithMessage:@"Default"];
        } else{
            [self.iOutput captureStillImageAsynchronouslyFromConnection:connection completionHandler:^(CMSampleBufferRef imageDataSampleBuffer, NSError *error) {
                if (!imageDataSampleBuffer) {
                    [[CustomeAlertView shareView] showCustomeAlertViewWithMessage:@"Default"];
                } else{
                    
                    self.saveIndicatorView.hidden = NO;
                    [self.saveIndicatorView startAnimating];
                    NSData *imageData = [AVCaptureStillImageOutput jpegStillImageNSDataRepresentation:imageDataSampleBuffer];
                    UIImage *image = [UIImage imageWithData:imageData];
                    
                    //                    UIImageWriteToSavedPhotosAlbum(image, nil, nil, nil);
                    
                    [self saveImage:image];
                }
            }];
        }
    } else if([self.takePhotoBtn.titleLabel.text isEqualToString:@"开始"]){
        
        [self.takePhotoBtn setTitle:@"结束" forState:UIControlStateNormal];
        
        //        AVCaptureConnection *connect = [self.iMovieOutput connectionWithMediaType:AVMediaTypeVideo];
        NSURL *url = [NSURL fileURLWithPath:[NSTemporaryDirectory() stringByAppendingString:@"myMovie.mov"]];
        if (![self.iMovieOutput isRecording]) {
            [self.iMovieOutput startRecordingToOutputFileURL:url recordingDelegate:self];
        }
        
    } else if ([self.takePhotoBtn.titleLabel.text isEqualToString:@"结束"]){
        [self.takePhotoBtn setTitle:@"开始" forState:UIControlStateNormal];
        if ([self.iMovieOutput isRecording]) {
            [self.iMovieOutput stopRecording];
        }
    }
    
    
    
}

- (void)saveImage:(UIImage *)image {
    NSArray *paths =NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask,YES);
    
    
    NSString *filePath = [[paths objectAtIndex:0]stringByAppendingPathComponent:
                          [NSString stringWithFormat:@"demo.png"]];  // 保存文件的名称
    
    BOOL result =[UIImagePNGRepresentation(image)writeToFile:filePath   atomically:YES]; // 保存成功会返回YES
    if (result == YES) {
        NSLog(@"保存成功");
        [[CustomeAlertView shareView] showCustomeAlertViewWithMessage:[NSString stringWithFormat:@"照片已保存至 %@",filePath]];
        
        NSMutableDictionary *dict = [[NSMutableDictionary alloc]init];
        [dict setObject:filePath forKey:@"filePath"];
        [dict setObject:[NSNumber numberWithInt:200] forKey:@"code"];
        //发送消息
        NSNotification *notification = [NSNotification notificationWithName:@"NOTIFICATION_NAME"
                                                                     object:dict];
        [[NSNotificationCenter defaultCenter] postNotification:notification];
        
        [self.saveIndicatorView stopAnimating];
        
        [self dismissViewControllerAnimated:YES completion:^{
            
        }];
    }else{
        [[CustomeAlertView shareView] showCustomeAlertViewWithMessage:@"保存失败"];
        self.saveIndicatorView.hidden = YES;
        [self.saveIndicatorView stopAnimating];
    }
    
}
- (IBAction)videoButtonAction:(id)sender {
    
    self.videoBtn.selected = !self.videoBtn.selected;
    if (self.videoBtn.selected) {
        
        [self.iSession beginConfiguration];
        [self.iSession removeOutput:self.iOutput];
        if ([self.iSession canAddOutput:self.iMovieOutput]) {
            [self.iSession addOutput:self.iMovieOutput];
            
            [self.takePhotoBtn setTitle:@"开始" forState:UIControlStateNormal];
            
            //设置视频防抖
            AVCaptureConnection *connection = [self.iMovieOutput connectionWithMediaType:AVMediaTypeVideo];
            if ([connection isVideoStabilizationSupported]) {
                connection.preferredVideoStabilizationMode = AVCaptureVideoStabilizationModeCinematic;
            }
            
        } else{
            [self.iSession addOutput:self.iOutput];
        }
        
        [self.iSession commitConfiguration];
        
    } else{
        [self.iSession beginConfiguration];
        [self.iSession removeOutput:self.iMovieOutput];
        if ([self.iSession canAddOutput:self.iOutput]) {
            [self.iSession addOutput:self.iOutput];
            
            [self.takePhotoBtn setTitle:@"拍照" forState:UIControlStateNormal];
            
        } else{
            [self.iSession addOutput:self.iMovieOutput];
        }
        
        [self.iSession commitConfiguration];
    }
    
}

-(void)captureOutput:(AVCaptureFileOutput *)captureOutput didFinishRecordingToOutputFileAtURL:(NSURL *)outputFileURL fromConnections:(NSArray *)connections error:(NSError *)error{
    
    //保存视频到相册
    ALAssetsLibrary *assetsLibrary=[[ALAssetsLibrary alloc]init];
    [assetsLibrary writeVideoAtPathToSavedPhotosAlbum:outputFileURL completionBlock:nil];
    [[CustomeAlertView shareView] showCustomeAlertViewWithMessage:@"视频保存成功"];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
