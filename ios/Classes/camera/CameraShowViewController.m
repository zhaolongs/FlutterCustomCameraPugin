//
//  CameraShowViewController.m
//  flutter_custom_camera_pugin
//
//  Created by  androidlongs on 2020/9/12.
//

#import "CameraShowViewController.h"
#import "CameraUtils.h"

@interface CameraShowViewController ()
@property (unsafe_unretained, nonatomic) IBOutlet UIImageView *showImageView;
@property (unsafe_unretained, nonatomic) IBOutlet UIButton *closeButton;
@property (unsafe_unretained, nonatomic) IBOutlet UIButton *nextButton;



@end

@implementation CameraShowViewController

- (void)viewDidLoad {
    [super viewDidLoad];
   
    
    UIImage *img = [UIImage imageWithContentsOfFile:self.imageUrl];
    
    self.showImageView .image = img;
}
- (IBAction)nextButtonAction:(UIButton *)sender {
     [self.navigationController popViewControllerAnimated:NO];
    [CameraUtils nextWithImageUrl:self.imageUrl];
}


- (IBAction)cancleButtonAction:(UIButton *)sender {
    [CameraUtils cancle:self.pageFlag];
    
    [self.navigationController popViewControllerAnimated:NO];
}

@end
