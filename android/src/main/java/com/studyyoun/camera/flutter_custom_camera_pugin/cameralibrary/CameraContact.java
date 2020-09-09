package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraPhotoGraphCallback;

public interface CameraContact {

    /**
     * 工具类功能
     * 1 自定义相机
     * 2 打开相册
     */
    interface CommonCameraInterface {
        //开始录制
        void start();

        //停止录制
        void stop();

        //权限请求回调
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

        //销毁
        void destore();

        //切换摄像头
        void changeCameraClick();

        CameraXUtils setXContinuous(boolean continuous);

        //重新拍照
        void reCameraClick();

        //拍照
        void onCameraClick();

        //打开相册
        void openCapTureGroupFunction(Context context);

        //相册选图回调
        void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, Context context, CameraPhotoGraphCallback callback);

        void openCameraFlashFunction();
    }

    interface CameraPresenter {
        void stop();
        void start();
        void reCameraClick();
        void onCameraClick();
        void openCameraFlashFunction();
        void destore();
    }

    //打开相册
    interface PhotoPresenter {
        //打开相册
        void openCapTureGroupFunction(Context context);

        //相册选图回调
        void onActivityResult(int requestCode, int resultCode, Intent data, Context context, CameraPhotoGraphCallback callback);
    }
}
