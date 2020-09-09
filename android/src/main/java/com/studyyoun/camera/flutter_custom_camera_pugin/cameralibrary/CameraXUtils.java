package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraCallBack;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraDarkCallBack;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraPhotoGraphCallback;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.presenter.CameraPhotoPresenter;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.presenter.CameraXOpenPresenter;


public class CameraXUtils implements CameraContact.CommonCameraInterface {


    private static CameraXUtils mCameraUtils;
    private final CameraXOpenPresenter mCameraOpenPresenter;
    private final CameraPhotoPresenter mCameraPhotoPresenter;

    private CameraXUtils() {
        mCameraOpenPresenter = new CameraXOpenPresenter();
        mCameraPhotoPresenter = new CameraPhotoPresenter();
    }
    public static CameraXUtils getInstance() {
        if (mCameraUtils == null) {
            mCameraUtils = new CameraXUtils();
        }
        return mCameraUtils;
    }
    public CameraXUtils initCamerView(Context context, FrameLayout frameLayout, int picWidth, int picHeight, CameraCallBack callBack) {
        mCameraOpenPresenter.init(context,frameLayout,picWidth,picHeight,callBack);
        return mCameraUtils;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    }
    @Override
    public void destore() {
        mCameraOpenPresenter.destore();
    }

    @Override
    public void changeCameraClick() {
        mCameraOpenPresenter.changeCameraClick();
    }
    

    
    @Override
    public CameraXUtils setXContinuous(boolean continuous) {
        mCameraOpenPresenter.setContinuous(continuous);
        return mCameraUtils;
    }
    @Override
    public void reCameraClick() {
        mCameraOpenPresenter.reCameraClick();
    }
    @Override
    public void start(){
        mCameraOpenPresenter.start();
    }
    @Override
    public void stop(){
        mCameraOpenPresenter.stop();
    }

    @Override
    public void onCameraClick() {
        mCameraOpenPresenter.onCameraClick();
    }
    @Override
    public void openCapTureGroupFunction(Context context) {
        mCameraPhotoPresenter.openCapTureGroupFunction(context);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, Context context, CameraPhotoGraphCallback callback) {
        mCameraPhotoPresenter.onActivityResult(requestCode,resultCode,data,context,callback);
    }
    @Override
    public void openCameraFlashFunction() {
        mCameraOpenPresenter.openCameraFlashFunction();
    }


    public void setLineDarkCallBack(CameraDarkCallBack pLineDarkCallBack){
        mCameraOpenPresenter.setCameraDarkCallBack(pLineDarkCallBack);
    }
    public void setIsUseShopPreview(boolean flag){
        mCameraOpenPresenter.setShotPreview(flag);
    }
}
