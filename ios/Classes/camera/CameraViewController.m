//
//  CameraViewController.m
//  CustomeCamera
//
//  Created by ios2chen on 2017/7/20.
//  Copyright © 2017年 ios2chen. All rights reserved.
//

#import "CameraViewController.h"
#import <AssetsLibrary/AssetsLibrary.h>
#import "TestViewController.h"

#import "CameraShowViewController.h"
#import "CameraShowViewController.h"
#import "CameraUtils.h"
#import "LeePhotoOrAlbumImagePicker.h"

#import <Photos/PHAsset.h>
#import <Photos/PHImageManager.h>
@interface CameraViewController ()<AVCaptureFileOutputRecordingDelegate,UINavigationControllerDelegate,UIImagePickerControllerDelegate>

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
@property (strong, nonatomic)  UIButton *takePhotoBtn;
@property (weak, nonatomic) IBOutlet UIButton *videoBtn;


@property(assign,nonatomic) NSInteger openPageCount;


//相册
@property (weak, nonatomic) IBOutlet UIButton *photoAlbumButton;

@property (weak, nonatomic) IBOutlet UIButton *mCameraChangeButton;
@property (weak, nonatomic) IBOutlet UIButton *mCameraFlashButton;
//关闭按钮
@property (weak, nonatomic) IBOutlet UIButton *backButton;

@end

@implementation CameraViewController

-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    if(self.pageIndex ==0){
        if (self.iSession) {
            [self.iSession startRunning];
        }
    }
    if(self.openPageCount ==0){
        self.openPageCount ++;
        [self initViewFunction];
    }
    
}

-(void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    if(self.pageIndex ==0){
        if (self.iSession) {
            [self.iSession stopRunning];
        }
    }
}

-(void) initViewFunction{
    
    [self initCreateView];
    
    self.saveIndicatorView.hidden = YES;
    
    if(self.pageIndex ==0){
        [self initCameraFunction];
    }else if (self.pageIndex ==1 ){
        [self openPhotoAlbum];
    }else if (self.pageIndex ==2 ){
        [self openPhotoCamera];
    }else{
        [self showAlert];
    }
    
    if(self.pageIndex ==0){
        if(self.cameraOptions.isShowPhotoAlbum){
            self.photoAlbumButton.hidden = NO;
        }else{
            self.photoAlbumButton.hidden = YES;
        }
        if(self.cameraOptions.isShowSelectCamera){
            self.mCameraChangeButton.hidden = NO;
        }else{
            self.mCameraChangeButton.hidden = YES;
        }
        if(self.cameraOptions.isShowFlashButtonCamera){
            self.mCameraFlashButton.hidden = NO;
        }else{
            self.mCameraFlashButton.hidden = YES;
        }
    }else{
        self.photoAlbumButton.hidden = YES;
        self.mCameraChangeButton.hidden = YES;
        self.mCameraFlashButton.hidden = YES;
        self.takePhotoBtn.hidden = YES;
        self.backButton.hidden = YES;
    }
}

-(void) initCreateView{
    UIButton *button1 = [UIButton buttonWithType:UIButtonTypeCustom];
    button1.frame=CGRectMake(UIScreen_Width/2-40, UIScreen_Height - 100, 60, 60);
    
    NSString *takePhoneImageUrl =self.cameraOptions.imageAssetList[1];
    //设置button填充
    UIImage *icon = [UIImage imageWithContentsOfFile:takePhoneImageUrl];
    [button1 setImage:icon forState:UIControlStateNormal];
    
    
    UIImageView *imageView = [[UIImageView alloc]initWithFrame:button1.frame];
    imageView.image = icon;
    
    //设置button填充图片
    [button1 setImage:icon forState:UIControlStateNormal];
    button1.layer.cornerRadius = 30;
    button1.backgroundColor = [UIColor redColor];
    button1.clipsToBounds = YES;
    [button1 setTitle:@"拍照" forState:UIControlStateNormal];
    [button1 addTarget:self action:@selector(takePhotoAction:) forControlEvents:UIControlEventTouchUpInside];
    self.takePhotoBtn = button1;
    [self.view addSubview:button1];
    
    
    
    UIButton *closeButton = [UIButton buttonWithType:UIButtonTypeCustom];
    closeButton.frame=CGRectMake(20, 30, 60, 60);
    closeButton.backgroundColor = [UIColor clearColor];
    [closeButton setTitle:@"取消" forState:UIControlStateNormal];
    [closeButton addTarget:self action:@selector(backAction:) forControlEvents:UIControlEventTouchUpInside];
    self.backButton = closeButton;
    
    [self.view addSubview:closeButton];
    
}
-(void) showAlert{
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:nil message:nil preferredStyle:UIAlertControllerStyleActionSheet];
    UIAlertAction *photoAlbumAction = [UIAlertAction actionWithTitle:@"从相册选择" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self openPhotoAlbum];
    }];
    UIAlertAction *cemeraAction = [UIAlertAction actionWithTitle:@"拍照" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self openPhotoCamera];
    }];
    UIAlertAction *cancleAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        
    }];
    [alertController addAction:photoAlbumAction];
    [alertController addAction:cemeraAction];
    [alertController addAction:cancleAction];
    
    [self presentViewController:alertController animated:YES completion:nil];
}

/*
 1、AVCaptureDevice： 代表抽象的硬件设备(如前置摄像头，后置摄像头等)。
 2、AVCaptureInput： 代表输入设备（可以是它的子类），它配置抽象硬件设备的ports。
 3、AVCaptureOutput： 它代表输出数据，管理着输出到一个movie或者图像。
 4、AVCaptureSession： 它是input和output的桥梁。它协调着input到output的数据传输
 */
- (void)viewDidLoad {
    [super viewDidLoad];
    
}

-(void) initCameraFunction{
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

- (void)backAction:(UIButton*)sender {
    [self closeCameraFunction:201];
}
- (IBAction)photoAlbumActioon:(UIButton *)sender {
    
    [self openPhotoAlbum];
    //     [self closeCameraFunction:204];
    
    //    [self.navigationController pushViewController:[[TestViewController alloc]init] animated:YES];
    
}

/*
 UIImagePickerControllerSourceTypePhotoLibrary,//图库
 
 UIImagePickerControllerSourceTypeCamera,//调用相机
 
 UIImagePickerControllerSourceTypeSavedPhotosAlbum //调用相册
 */
-(void) openPhotoCamera{
    UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
    imagePicker.delegate = self;
    imagePicker.allowsEditing = NO;//编辑模式  但是编辑框是正方形的
    imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
    [self presentViewController:imagePicker animated:YES completion:nil];
}
-(void) openPhotoAlbum{
    // 跳转到相机或相册页面
    UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
    imagePicker.delegate = self;
    imagePicker.allowsEditing = NO;//编辑模式  但是编辑框是正方形的
    imagePicker.sourceType = UIImagePickerControllerSourceTypeSavedPhotosAlbum;
    [self presentViewController:imagePicker animated:YES completion:nil];
}
/*
 NSString *const  UIImagePickerControllerMediaType ;指定用户选择的媒体类型（文章最后进行扩展）
 NSString *const  UIImagePickerControllerOriginalImage ;原始图片
 NSString *const  UIImagePickerControllerEditedImage ;修改后的图片
 NSString *const  UIImagePickerControllerCropRect ;裁剪尺寸
 NSString *const  UIImagePickerControllerMediaURL ;媒体的URL
 NSString *const  UIImagePickerControllerReferenceURL ;原件的URL
 NSString *const  UIImagePickerControllerMediaMetadata;当来数据来源是照相机的时候这个值才有效
 */
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    
    [picker dismissViewControllerAnimated:YES completion:nil];
    UIImage *image = [info objectForKey:UIImagePickerControllerOriginalImage];
    //    NSString *type = [info objectForKey:UIImagePickerControllerMediaType];
    [self saveImage:image];
    
    //    NSURL *imageAssetUrl = [info objectForKey:UIImagePickerControllerReferenceURL];
    //
    //    PHFetchResult*result = [PHAsset fetchAssetsWithALAssetURLs:@[imageAssetUrl] options:nil];
    //
    //    PHAsset *asset = [result firstObject];
    //
    //    PHImageRequestOptions *phImageRequestOptions = [[PHImageRequestOptions alloc] init];
    //
    //    [[PHImageManager defaultManager] requestImageDataForAsset:asset options:phImageRequestOptions resultHandler:^(NSData * _Nullable imageData, NSString * _Nullable dataUTI, UIImageOrientation orientation, NSDictionary * _Nullable info) {
    //
    //
    //
    //    }];
    
    
    //     [CameraUtils nextWithImageUrl:filePath];
    
}

// 取消图片选择调用此方法
- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    [picker dismissViewControllerAnimated:YES completion:nil];
    
    if(self.pageIndex !=0){
        [self closeCameraFunction:201];
    }
}

-(void) closeCameraFunction:(NSInteger) code{
    [self dismissViewControllerAnimated:YES completion:^{
        NSDictionary *dict = [NSDictionary dictionaryWithObject:[NSNumber numberWithInteger:code] forKey:@"code"];
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
- (void)takePhotoAction:(UIButton *)sender {
    
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

//iOS拍照之后图片自动旋转90度解决办法
- (UIImage *)fixOrientation:(UIImage *) aImage{
    if (aImage.imageOrientation == UIImageOrientationUp) return aImage;
    
    CGAffineTransform transform = CGAffineTransformIdentity;
    
    switch (aImage.imageOrientation) { case UIImageOrientationDown: case UIImageOrientationDownMirrored: transform = CGAffineTransformTranslate(transform, aImage.size.width, aImage.size.height); transform = CGAffineTransformRotate(transform, M_PI); break;
            
        case UIImageOrientationLeft:
        case UIImageOrientationLeftMirrored:
            transform = CGAffineTransformTranslate(transform, aImage.size.width, 0);
            transform = CGAffineTransformRotate(transform, M_PI_2);
            break;
            
        case UIImageOrientationRight:
        case UIImageOrientationRightMirrored:
            transform = CGAffineTransformTranslate(transform, 0, aImage.size.height);
            transform = CGAffineTransformRotate(transform, -M_PI_2);
            break;
        default:
            break;
    }
    
    switch (aImage.imageOrientation) { case UIImageOrientationUpMirrored: case UIImageOrientationDownMirrored: transform = CGAffineTransformTranslate(transform, aImage.size.width, 0); transform = CGAffineTransformScale(transform, -1, 1); break;
            
        case UIImageOrientationLeftMirrored:
        case UIImageOrientationRightMirrored:
            transform = CGAffineTransformTranslate(transform, aImage.size.height, 0);
            transform = CGAffineTransformScale(transform, -1, 1);
            break;
        default:
            break;
    }
    
    CGContextRef ctx = CGBitmapContextCreate(NULL, aImage.size.width, aImage.size.height, CGImageGetBitsPerComponent(aImage.CGImage), 0, CGImageGetColorSpace(aImage.CGImage), CGImageGetBitmapInfo(aImage.CGImage)); CGContextConcatCTM(ctx, transform); switch (aImage.imageOrientation) { case UIImageOrientationLeft: case UIImageOrientationLeftMirrored: case UIImageOrientationRight: case UIImageOrientationRightMirrored:
            
            CGContextDrawImage(ctx, CGRectMake(0,0,aImage.size.height,aImage.size.width), aImage.CGImage);
            break;
            
        default:
            CGContextDrawImage(ctx, CGRectMake(0,0,aImage.size.width,aImage.size.height), aImage.CGImage);
            break;
    }
    
    CGImageRef cgimg = CGBitmapContextCreateImage(ctx); UIImage *img = [UIImage imageWithCGImage:cgimg]; CGContextRelease(ctx); CGImageRelease(cgimg); return img;
}

//拍照后的保存图片的方法
- (void)saveImage:(UIImage *)preImage {
    
    UIImage * image = preImage;
    
    
    image = [self fixOrientation:preImage];
    
    NSArray *paths =NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,NSUserDomainMask,YES);
    
    NSDate* dat = [NSDate dateWithTimeIntervalSinceNow:0];
    
    NSTimeInterval a=[dat timeIntervalSince1970];
    
    NSString*timeString = [NSString stringWithFormat:@"%0.f", a];//转为字符型
    
    
    NSString *filePath = [[paths objectAtIndex:0]stringByAppendingPathComponent:
                          [NSString stringWithFormat:@"%@them.png",timeString]];  // 保存文件的名称
    
    
    NSData *imageDate = UIImageJPEGRepresentation(image, 0);
    
    NSString *fullPath = [[NSHomeDirectory() stringByAppendingPathComponent:@"Document"] stringByAppendingPathComponent:@"image"];
    
    NSLog(@"%@",fullPath);
    
    [imageDate writeToFile:filePath atomically:YES];
    
    
    if (self.cameraOptions.isShowToast) {
        [[CustomeAlertView shareView] showCustomeAlertViewWithMessage:[NSString stringWithFormat:@"照片已保存至 %@",filePath]];
    }
    [self.saveIndicatorView stopAnimating];
    if(self.cameraOptions.isPreviewImage){
        //去预览
        CameraShowViewController *previewController =[ [CameraShowViewController alloc]initWithNibName:@"CameraShowViewController" bundle:nil];
        previewController.imageUrl = filePath;
        [self.navigationController pushViewController:previewController animated:YES];
        
    }else{
        [CameraUtils nextWithImageUrl:filePath];
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
