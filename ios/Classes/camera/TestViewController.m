//
//  TestViewController.m
//  flutter_custom_camera_pugin
//
//  Created by  androidlongs on 2020/9/12.
//

#import "TestViewController.h"
#import "LeePhotoOrAlbumImagePicker.h"

@interface TestViewController ()

@end

@implementation TestViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    LeePhotoOrAlbumImagePicker *myPicker = [[LeePhotoOrAlbumImagePicker alloc]init];
       [myPicker getPhotoAlbumOrTakeAPhotoWithController:self photoBlock:^(UIImage *image) {
           //回掉图片
           NSLog(@"回调");
       } withFlag : 3];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
