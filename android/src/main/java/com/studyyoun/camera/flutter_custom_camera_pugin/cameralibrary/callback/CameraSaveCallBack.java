package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback;

public interface CameraSaveCallBack {
    void cameraFaile(int errCode, String message);
    void cameraSuccess(String mFilePath);
    void cameraPermisExit();
}
